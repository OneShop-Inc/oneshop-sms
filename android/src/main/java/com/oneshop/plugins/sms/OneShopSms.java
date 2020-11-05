package com.oneshop.plugins.sms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

@NativePlugin
public class OneShopSms extends Plugin {
    private static final String TAG = "MyActivity";

    static final int SMS_INTENT_REQUEST_CODE = 2311;
    private static final String ERR_SERVICE_NOTFOUND = "ERR_SERVICE_NOTFOUND";

    @SuppressLint("NewApi")
    private Uri getFileName(String attachment, int i) {
        String imageDataBytes = attachment.substring(attachment.indexOf(",")+1);
        byte[] decodedString = Base64.getDecoder().decode(imageDataBytes);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        String saveFilePath = getContext().getExternalCacheDir()+"";
        File dir = new File(saveFilePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String imageFileName = i + "_cashToSendMms.jpeg";
        File file = new File(dir, imageFileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            decodedByte.compress(Bitmap.CompressFormat.JPEG, 40, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(new File(saveFilePath + "/" + imageFileName));
    }

    @PluginMethod
    public void openMessenger(PluginCall call) {
        String number = call.getString("number", "");
        String body = call.getString("body", "");

        com.getcapacitor.JSArray array = new com.getcapacitor.JSArray();
        com.getcapacitor.JSArray attachments = call.getArray("attachments", array);

        Intent smsIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        smsIntent.putExtra("sms_body", body);
        // See http://stackoverflow.com/questions/7242190/sending-sms-using-intent-does-not-add-recipients-on-some-devices
        smsIntent.putExtra("address", number);
        smsIntent.setDataAndType(Uri.parse("smsto:" + Uri.encode(number)), "image/*");
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                ArrayList uris = new ArrayList();
                for (int i = 0; i < attachments.length(); i++) {
                    String attachment = attachments.getString(i);
                    uris.add(getFileName(attachment, i));
                }
                smsIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (smsIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(call, smsIntent, SMS_INTENT_REQUEST_CODE);
        } else {
            call.reject(ERR_SERVICE_NOTFOUND);
        }
    }

    // Todo do we need this?
    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"handleOnActivityResult");
        if (requestCode == SMS_INTENT_REQUEST_CODE) {
            Log.d(TAG,"handleOnActivityResult!!!");
            PluginCall savedCall = getSavedCall();
            savedCall.resolve();
        }
        super.handleOnActivityResult(requestCode, resultCode, data);
    }
}
