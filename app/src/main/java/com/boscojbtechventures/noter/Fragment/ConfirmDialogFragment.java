package com.boscojbtechventures.noter.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.boscojbtechventures.noter.Interface.IConfirmDialog;
import com.boscojbtechventures.noter.R;

/**
 * Created by sirbuks on 11/03/2017.
 */

public class ConfirmDialogFragment extends DialogFragment implements IConfirmDialog {
    IConfirmDialog mlistener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View confirmView;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            confirmView = inflater.inflate(R.layout.confirm_dialog, null);
        else
            confirmView = inflater.inflate(R.layout.confirm_dialog_pop, null);
        builder.setView(confirmView)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mlistener.onDialogPositiveClick(ConfirmDialogFragment.this);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mlistener.onDialogNegativeClick(ConfirmDialogFragment.this);
                    }
                })
                .setCancelable(true).setIcon(R.mipmap.ic_launcher);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mlistener = (IConfirmDialog) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement ISaveDialog interface");
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
