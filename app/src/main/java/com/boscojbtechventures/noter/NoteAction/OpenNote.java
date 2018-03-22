package com.boscojbtechventures.noter.NoteAction;

import android.app.Activity;

import com.boscojbtechventures.noter.Utility.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Johnbosco on 13/05/2017.
 */

public abstract class OpenNote {

    private static File selectedFile;

    public static File useFileChooser(Activity activity) {
        new FileChooser(activity).setFileListener(new FileChooser.FileSelectedListener() {
            @Override public void fileSelected(final File file) throws FileNotFoundException {
                selectedFile = file;
            }}).showDialog();
        return selectedFile;
    }
}
