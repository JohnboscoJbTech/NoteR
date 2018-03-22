package com.boscojbtechventures.noter.Fragment.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.boscojbtechventures.noter.Interface.IMultiDialog;
import com.boscojbtechventures.noter.R;

/**
 * Created by UMB on 03/07/2017.
 */

public class MultiDialog implements IMultiDialog{

    Context context;
    IMultiDialog multiDialog;
    private String name;
    Dialog dialog;

    public  MultiDialog(Context _context, final String dialogName){
        context = _context;
        this.name = dialogName;
        multiDialog = (IMultiDialog)context;
    }

    public String getName() {
        return this.name;
    }

    public void showDialog(String msg){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            dialog.setContentView(R.layout.multi_dialogbox);
        else
            dialog.setContentView(R.layout.multi_dialogbox_pop);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView text = (TextView) dialog.findViewById(R.id.dialog_query_text);
        text.setText(msg);

        Button cancelButton = (Button) dialog.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.cancel();
                multiDialog.onCancelDialog(MultiDialog.this);
            }
        });

        Button closeButton = (Button) dialog.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                multiDialog.onCloseDialog(MultiDialog.this);
            }
        });

        Button yesButton = (Button) dialog.findViewById(R.id.btn_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.cancel();
                multiDialog.onOkDialog(MultiDialog.this);
            }
        });

        dialog.show();
    }

    public void closeDialog(){
        dialog.dismiss();
    }

    public void onCloseDialog(MultiDialog dialog) {

    }

    public void onCancelDialog(MultiDialog dialog) {

    }

    public void onOkDialog(MultiDialog dialog) {

    }
}
