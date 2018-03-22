package com.boscojbtechventures.noter.Interface;

import com.boscojbtechventures.noter.Fragment.Dialog.SaveDialog;

/**
 * Created by sirbuks on 15/02/2017.
 */

public interface ISaveDialog {
    public void onCloseDialog(SaveDialog dialog);

    public void onCancelDialog(SaveDialog dialog);

    public void onOkDialog(SaveDialog dialog, String name);
}
