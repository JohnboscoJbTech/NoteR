package com.boscojbtechventures.noter.Utility;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Johnbosco on 12/05/2017.
 */

public abstract class Utils {

    public static void startAnotherActivity(Class className, Context context) {
        Intent newActivity = new Intent(context, className);
        context.startActivity(newActivity);
    }

    public static void startActivityWithBundle(Class className, Context context, String[] bundles) {
        Intent newActivity = new Intent(context, className);
        for (int i = 0; i < bundles.length; i++) {
            //newActivity.putExtra(bundles[, fileName);
        }
        context.startActivity(newActivity);
    }

    public static String getNotePathDirectory(String input) {
        String output = "";
        String[] splited = input.split("/");
        for (int s = 0; s < splited.length - 1; s++) {
            output += splited[s] + "/";
        }
        return output;
    }

    public static String getNoteName(String input) {
        String[] splited = input.split("/");

        return splited[splited.length - 1];
    }

    public static String getPaths(){
        return "";
    }
}
