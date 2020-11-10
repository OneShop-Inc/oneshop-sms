package com.oneshop.plugins.sms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import org.json.JSONException;

@NativePlugin(
    requestCodes = { OneshopSms.SMS_INTENT_REQUEST_CODE },
    permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE }
)
public class OneshopSms extends Plugin {

    private static final String TAG = "MyActivity";

    private static final int REQUEST_IMAGE_CAPTURE = 12345;
    static final int SMS_INTENT_REQUEST_CODE = 2311;
    private static final String ERR_SERVICE_NOT_FOUND = "ERR_SERVICE_NOT_FOUND";

    @SuppressLint("NewApi")
    private Uri getFileName(String attachment, int i) {
        // Todo handle variable file path types like iOS
        // has prefix:
        // http, www/, file://, data:, default

        String imageDataBytes = attachment.substring(attachment.indexOf(",") + 1);
        byte[] decodedString = Base64.getDecoder().decode(imageDataBytes);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        String saveFilePath = getContext().getExternalCacheDir() + "";
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
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            decodedByte.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File outFile = new File(saveFilePath + "/" + imageFileName);
        return Uri.fromFile(outFile);
    }

    @PluginMethod
    public void openMessenger(PluginCall call) {
        if (!hasRequiredPermissions()) {
            pluginRequestPermissions(
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE },
                REQUEST_IMAGE_CAPTURE
            );
        }

        String number = call.getString("number", "");
        String body = call.getString("body", "");

        com.getcapacitor.JSArray array = new com.getcapacitor.JSArray();
        com.getcapacitor.JSArray attachments = call.getArray("attachments", array);

        Intent smsIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        smsIntent.putExtra("sms_body", body);
        // See http://stackoverflow.com/questions/7242190/sending-sms-using-intent-does-not-add-recipients-on-some-devices
        smsIntent.putExtra("address", number);
        // smsIntent.setData(Uri.parse("smsto:" + Uri.encode(number)));
        smsIntent.setType("image/*");
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
            // Causes TransactionTooLargeException
            // startActivityForResult(call, smsIntent, SMS_INTENT_REQUEST_CODE);

            // Does not trigger handleOnActivityResult
            getContext().startActivity(smsIntent);
        } else {
            call.reject(ERR_SERVICE_NOT_FOUND);
        }

        call.success();
    }

    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "handling request perms result");
        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            Log.d(TAG, "No stored plugin call for permissions request result");
            return;
        }

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                savedCall.error("User denied permission");
                return;
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d(TAG, "we got them");
            // We got the permission
        }
    }

    // Todo do we need this? No, unless we are able to fix the TransactionTooLargeException issue
    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "handleOnActivityResult");
        if (requestCode == SMS_INTENT_REQUEST_CODE) {
            Log.d(TAG, "handleOnActivityResult!!!");
            PluginCall savedCall = getSavedCall();
            savedCall.resolve();
        }
        super.handleOnActivityResult(requestCode, resultCode, data);
    }
}
