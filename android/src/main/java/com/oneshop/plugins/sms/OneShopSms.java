package com.oneshop.plugins.sms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import androidx.core.content.FileProvider;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;

@CapacitorPlugin(
    name = "OneShopSms",
    requestCodes = { OneShopSms.SMS_INTENT_REQUEST_CODE },
    permissions = {
        @Permission(strings = { Manifest.permission.WRITE_EXTERNAL_STORAGE }),
        @Permission(strings = { Manifest.permission.READ_EXTERNAL_STORAGE })
    }
)
public class OneShopSms extends Plugin {

    private static final String TAG = "MyActivity";

    private static final int REQUEST_IMAGE_CAPTURE = 12345;
    static final int SMS_INTENT_REQUEST_CODE = 2311;
    private static final String ERR_SERVICE_NOT_FOUND = "ERR_SERVICE_NOT_FOUND";

    @SuppressLint("NewApi")
    private Uri getFileNameSms(String attachment, int i) {
        // Todo handle variable file path types like iOS
        // has prefix:
        // http, www/, file://, data:, default

        String imageDataBytes = attachment.substring(attachment.indexOf(",") + 1);
        byte[] decodedString = java.util.Base64.getDecoder().decode(imageDataBytes);
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

    private byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private static String getFileNameShare(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        final String pattern = ".*/([^?#]+)?";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        if (m.find()) {
            return m.group(1);
        } else {
            return "file";
        }
    }

    private void saveFile(byte[] bytes, String dirName, String fileName) throws IOException {
        final File dir = new File(dirName);
        final FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
        fos.write(bytes);
        fos.flush();
        fos.close();
    }

    private String getMIMEType(String fileName) {
        String type = "*/*";
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            return type;
        }
        final String end = fileName.substring(dotIndex + 1, fileName.length()).toLowerCase();
        String fromMap = MIME_Map.get(end);
        return fromMap == null ? type : fromMap;
    }

    private static final Map<String, String> MIME_Map = new HashMap<String, String>();

    static {
        MIME_Map.put("3gp", "video/3gpp");
        MIME_Map.put("apk", "application/vnd.android.package-archive");
        MIME_Map.put("asf", "video/x-ms-asf");
        MIME_Map.put("avi", "video/x-msvideo");
        MIME_Map.put("bin", "application/octet-stream");
        MIME_Map.put("bmp", "image/bmp");
        MIME_Map.put("c", "text/plain");
        MIME_Map.put("class", "application/octet-stream");
        MIME_Map.put("conf", "text/plain");
        MIME_Map.put("cpp", "text/plain");
        MIME_Map.put("doc", "application/msword");
        MIME_Map.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        MIME_Map.put("xls", "application/vnd.ms-excel");
        MIME_Map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        MIME_Map.put("exe", "application/octet-stream");
        MIME_Map.put("gif", "image/gif");
        MIME_Map.put("gtar", "application/x-gtar");
        MIME_Map.put("gz", "application/x-gzip");
        MIME_Map.put("h", "text/plain");
        MIME_Map.put("htm", "text/html");
        MIME_Map.put("html", "text/html");
        MIME_Map.put("jar", "application/java-archive");
        MIME_Map.put("java", "text/plain");
        MIME_Map.put("jpeg", "image/jpeg");
        MIME_Map.put("jpg", "image/*");
        MIME_Map.put("js", "application/x-javascript");
        MIME_Map.put("log", "text/plain");
        MIME_Map.put("m3u", "audio/x-mpegurl");
        MIME_Map.put("m4a", "audio/mp4a-latm");
        MIME_Map.put("m4b", "audio/mp4a-latm");
        MIME_Map.put("m4p", "audio/mp4a-latm");
        MIME_Map.put("m4u", "video/vnd.mpegurl");
        MIME_Map.put("m4v", "video/x-m4v");
        MIME_Map.put("mov", "video/quicktime");
        MIME_Map.put("mp2", "audio/x-mpeg");
        MIME_Map.put("mp3", "audio/x-mpeg");
        MIME_Map.put("mp4", "video/mp4");
        MIME_Map.put("mpc", "application/vnd.mpohun.certificate");
        MIME_Map.put("mpe", "video/mpeg");
        MIME_Map.put("mpeg", "video/mpeg");
        MIME_Map.put("mpg", "video/mpeg");
        MIME_Map.put("mpg4", "video/mp4");
        MIME_Map.put("mpga", "audio/mpeg");
        MIME_Map.put("msg", "application/vnd.ms-outlook");
        MIME_Map.put("ogg", "audio/ogg");
        MIME_Map.put("pdf", "application/pdf");
        MIME_Map.put("png", "image/png");
        MIME_Map.put("pps", "application/vnd.ms-powerpoint");
        MIME_Map.put("ppt", "application/vnd.ms-powerpoint");
        MIME_Map.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        MIME_Map.put("prop", "text/plain");
        MIME_Map.put("rc", "text/plain");
        MIME_Map.put("rmvb", "audio/x-pn-realaudio");
        MIME_Map.put("rtf", "application/rtf");
        MIME_Map.put("sh", "text/plain");
        MIME_Map.put("tar", "application/x-tar");
        MIME_Map.put("tgz", "application/x-compressed");
        MIME_Map.put("txt", "text/plain");
        MIME_Map.put("wav", "audio/x-wav");
        MIME_Map.put("wma", "audio/x-ms-wma");
        MIME_Map.put("wmv", "audio/x-ms-wmv");
        MIME_Map.put("wps", "application/vnd.ms-works");
        MIME_Map.put("xml", "text/plain");
        MIME_Map.put("z", "application/x-compress");
        MIME_Map.put("zip", "application/x-zip-compressed");
        MIME_Map.put("", "*/*");
    }

    // pulled from https://github.com/EddyVerbruggen/SocialSharing-PhoneGap-Plugin
    public static String sanitizeFilename(String name) {
        return name.replaceAll("[:\\\\/*?|<> ]", "_");
    }

    // pulled from https://github.com/EddyVerbruggen/SocialSharing-PhoneGap-Plugin
    private void cleanupOldFiles(File dir) {
        for (File f : dir.listFiles()) {
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }
    }

    // pulled from https://github.com/EddyVerbruggen/SocialSharing-PhoneGap-Plugin
    private void createOrCleanDir(final String downloadDir) throws IOException {
        final File dir = new File(downloadDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("CREATE_DIRS_FAILED");
            }
        } else {
            cleanupOldFiles(dir);
        }
    }

    // pulled from https://github.com/EddyVerbruggen/SocialSharing-PhoneGap-Plugin
    private String getDownloadDir() throws IOException {
        // better check, otherwise it may crash the app
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // we need to use external storage since we need to share to another app
            final String dir = getContext().getExternalFilesDir(null) + "/socialsharing-downloads";
            createOrCleanDir(dir);
            return dir;
        } else {
            return null;
        }
    }

    // pulled from https://github.com/EddyVerbruggen/SocialSharing-PhoneGap-Plugin
    private Uri getFileUriAndSetType(Intent sendIntent, String dir, String image) throws IOException {
        // we're assuming an image, but this can be any filetype you like
        String localImage = image;
        if (image.endsWith("mp4") || image.endsWith("mov") || image.endsWith("3gp")) {
            sendIntent.setType("video/*");
        } else if (image.endsWith("mp3")) {
            sendIntent.setType("audio/x-mpeg");
        } else {
            sendIntent.setType("image/*");
        }

        if (image.startsWith("http") || image.startsWith("www/")) {
            String filename = getFileNameShare(image);
            localImage = "file://" + dir + "/" + filename.replaceAll("[^a-zA-Z0-9._-]", "");
            if (image.startsWith("http")) {
                // filename optimisation taken from https://github.com/EddyVerbruggen/SocialSharing-PhoneGap-Plugin/pull/56
                URLConnection connection = new URL(image).openConnection();
                String disposition = connection.getHeaderField("Content-Disposition");
                if (disposition != null) {
                    final Pattern dispositionPattern = Pattern.compile("filename=([^;]+)");
                    Matcher matcher = dispositionPattern.matcher(disposition);
                    if (matcher.find()) {
                        filename = matcher.group(1).replaceAll("[^a-zA-Z0-9._-]", "");
                        if (filename.length() == 0) {
                            // in this case we can't determine a filetype so some targets (gmail) may not render it correctly
                            filename = "file";
                        }
                        localImage = "file://" + dir + "/" + filename;
                    }
                }
                saveFile(getBytes(connection.getInputStream()), dir, filename);
                // update file type
                String fileType = getMIMEType(image);
                sendIntent.setType(fileType);
            } else {
                saveFile(getBytes(getContext().getAssets().open(image)), dir, filename);
            }
        } else if (image.startsWith("data:")) {
            // safeguard for https://code.google.com/p/android/issues/detail?id=7901#c43
            if (!image.contains(";base64,")) {
                sendIntent.setType("text/plain");
                return null;
            }
            // image looks like this: data:image/png;base64,R0lGODlhDAA...
            final String encodedImg = image.substring(image.indexOf(";base64,") + 8);
            // correct the intent type if anything else was passed, like a pdf: data:application/pdf;base64,..
            if (!image.contains("data:image/")) {
                sendIntent.setType(image.substring(image.indexOf("data:") + 5, image.indexOf(";base64")));
            }
            // the filename needs a valid extension, so it renders correctly in target apps
            final String imgExtension = image.substring(image.indexOf("/") + 1, image.indexOf(";base64"));
            String fileName;
            // if a subject was passed, use it as the filename
            // filenames must be unique when passing in multiple files [#158]

            fileName = "file" + "." + imgExtension;

            saveFile(Base64.decode(encodedImg, Base64.DEFAULT), dir, fileName);
            localImage = "file://" + dir + "/" + fileName;
        } else if (image.startsWith("df:")) {
            // safeguard for https://code.google.com/p/android/issues/detail?id=7901#c43
            if (!image.contains(";base64,")) {
                sendIntent.setType("text/plain");
                return null;
            }
            // format looks like this :  df:filename.txt;data:image/png;base64,R0lGODlhDAA...
            final String fileName = image.substring(image.indexOf("df:") + 3, image.indexOf(";data:"));
            final String fileType = image.substring(image.indexOf(";data:") + 6, image.indexOf(";base64,"));
            final String encodedImg = image.substring(image.indexOf(";base64,") + 8);
            sendIntent.setType(fileType);
            saveFile(Base64.decode(encodedImg, Base64.DEFAULT), dir, sanitizeFilename(fileName));
            localImage = "file://" + dir + "/" + sanitizeFilename(fileName);
        } else if (!image.startsWith("file://")) {
            throw new IllegalArgumentException("URL_NOT_SUPPORTED");
        } else {
            //get file MIME type
            String type = getMIMEType(image);
            //set intent data and Type
            sendIntent.setType(type);
        }
        return Uri.parse(localImage);
    }

    @PluginMethod
    public void share(PluginCall call) {
        String imageString = call.getString("image");
        String appId = call.getString("appId", "");
        String topColor = call.getString("topColor", "");
        String bottomColor = call.getString("bottomColor", "");

        Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
        try {
            final String dir = getDownloadDir();
            Uri fileUri = getFileUriAndSetType(intent, dir, imageString);
            fileUri =
                FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".sharing.provider", new File(fileUri.getPath()));

            if (appId != "") {
                intent.putExtra("source_application", appId);
            }

            // Attach your sticker to the intent from a URI, and set background colors
            intent.setType("image/jpeg");
            intent.putExtra("interactive_asset_uri", fileUri);

            if (topColor != "") {
                intent.putExtra("top_background_color", "#33FF33");
            }
            if (bottomColor != "") {
                intent.putExtra("bottom_background_color", "#FF00FF");
            }

            // Instantiate an activity
            Activity activity = getActivity();

            // Grant URI permissions for the sticker
            activity.grantUriPermission("com.instagram.android", fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Verify that the activity resolves the intent and start it
            if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                getActivity()
                    .runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                startActivityForResult(call, intent, "onShareResult");
                            }
                        }
                    );
            }
        } catch (Exception e) {
            call.reject("Something whent wrong sharing");
        }
    }

    @PluginMethod
    public void canShare(PluginCall call) {
        JSObject ret = new JSObject();
        try {
            getActivity().getPackageManager().getApplicationInfo("com.instagram.android", 0);
            ret.put("value", true);
        } catch (PackageManager.NameNotFoundException e) {
            ret.put("value", false);
        }
        call.resolve(ret);
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
                    uris.add(getFileNameSms(attachment, i));
                }
                smsIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (smsIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Causes TransactionTooLargeException
            // TODO possibly fixed? https://capacitorjs.com/docs/updating/plugins/3-0
            // startActivityForResult(call, smsIntent, SMS_INTENT_REQUEST_CODE);

            // Does not trigger handleOnActivityResult
            // getContext().startActivity(smsIntent);
            startActivityForResult(call, smsIntent, "onSmsRequestResult");
        } else {
            call.reject(ERR_SERVICE_NOT_FOUND);
        }
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

    @ActivityCallback
    private void onSmsRequestResult(PluginCall call, ActivityResult result) {
        Log.d(TAG, "onSmsRequestResult");
        call.resolve();
    }

    @ActivityCallback
    private void onShareResult(PluginCall call, ActivityResult result) {
        if (result.getResultCode() == 0) {
            call.resolve();
        } else {
            call.reject("something went wrong sharing to instagram");
        }
    }
}
