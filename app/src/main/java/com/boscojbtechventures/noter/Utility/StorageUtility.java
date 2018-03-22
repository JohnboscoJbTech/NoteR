package com.boscojbtechventures.noter.Utility;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Johnbosco on 10/03/2017.
 * class does all the work on the storage
 */

public class StorageUtility {
    private String state;

    public boolean isExternalStorageWritable() {
        state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getFileStorageDir() {
        // Get the directory for the user's public documents directory.
        File path;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS);
        }
        else {
            String extStore = System.getenv("EXTERNAL_STORAGE");
            path = new File(extStore);

            if (path.equals(null)) {
                String secStore = System.getenv("SECONDARY_STORAGE");
                path = new File(secStore);
            }
        }
        if (path.exists()) {
            Log.e("EXISTS", "Directory exists");
        }
        else {
            path.mkdir();
            if (!path.exists()) {
                Log.e("NOT CREATED", "Directory not created");
            }
        }
        return path;
    }

    public String getFilePath() {
        return getFileStorageDir().getPath();
    }

    public String getAbsFilePath() {
        return getFileStorageDir().getAbsolutePath();
    }

    public boolean checkSpaceOnExternalStorage() {
        //get the free space of the sdcard
        long freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        if (freeSpace >= 20000000) {
            return true;
        }
        else {
            return false;
        }
    }

    /*public File getInternalFileDirectory() {
        Context context;
        //return context.getFilesDir();
    }*/
}
