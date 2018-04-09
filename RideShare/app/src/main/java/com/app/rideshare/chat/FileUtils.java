package com.app.rideshare.chat;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;

public class FileUtils {

    public static String getFileName(Uri uri) {

        String filename = null;

        if (uri.getPath().length() > 0) {
            String filepath = uri.getPath();
            String fname = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());
            String filetype = ".jpg";
            filename = fname + filetype;
        }

        return filename;
    }

    public static void createRecDirectoryAndSaveFile(byte[] imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Connecter/Received/");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Connecter/Received/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/Connecter/Received/"), fileName);

        if (file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(imageToSave);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSentDirectoryAndSaveFile(Bitmap bitmap, Uri uri) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Connecter/sent/");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Connecter/sent/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/Connecter/sent/"), FileUtils.getFileName(uri));

        if (file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {

        if ("content".equalsIgnoreCase(uri.getScheme())) {

            String[] projection = {"_data"};
            Cursor cursor = null;

            try {

                cursor = context.getContentResolver().query(uri, projection, null, null, null);

                int column_index = cursor.getColumnIndexOrThrow("_data");

                if (cursor.moveToFirst())
                    return cursor.getString(column_index);

            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}