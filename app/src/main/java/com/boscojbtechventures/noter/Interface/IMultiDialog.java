package com.boscojbtechventures.noter.Interface;


import com.boscojbtechventures.noter.Fragment.Dialog.MultiDialog;

/**
 * Created by UMB on 04/07/2017.
 */

public interface IMultiDialog {
    public void onCloseDialog(MultiDialog dialog);

    public void onCancelDialog(MultiDialog dialog);

    public void onOkDialog(MultiDialog dialog);
}
