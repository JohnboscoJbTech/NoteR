package com.boscojbtechventures.noter.Fragment.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.boscojbtechventures.noter.Interface.IMultiDialog;
import com.boscojbtechventures.noter.Interface.ISaveDialog;
import com.boscojbtechventures.noter.R;

/**
 * Created by UMB on 03/07/2017.
 */

public class SaveDialog implements ISaveDialog{

    private Context context;
    private ISaveDialog saveDialog;
    private String name, noteName;
    private Dialog dialog;
    private EditText fileName;

    public SaveDialog(Context _context, final String dialogName, String fileName){
        context = _context;
        this.name = dialogName;
        saveDialog = (ISaveDialog) context;
        this.noteName = fileName;
    }

    public String getName() {
        return this.name;
    }

    public void showDialog(){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            dialog.setContentView(R.layout.save_dialog);
        else
            dialog.setContentView(R.layout.save_dialog_pop);
        fileName = (EditText)dialog.findViewById(R.id.fileName);
        //fileName.setText(context.getResources().getText(R.string.note_name));
        if (!noteName.equals(null)) {
            if(noteName.endsWith("*")){
                noteName.replace("*", "");
                noteName = noteName.substring(0, noteName.length()-1);
            }
            fileName.setText(noteName);
        }
        fileName.selectAll();//onclick should select all
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button cancelButton = (Button) dialog.findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.cancel();
                saveDialog.onCancelDialog(SaveDialog.this);
            }
        });

        Button closeButton = (Button) dialog.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                saveDialog.onCloseDialog(SaveDialog.this);
            }
        });

        Button yesButton = (Button) dialog.findViewById(R.id.btn_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.cancel();
                saveDialog.onOkDialog(SaveDialog.this, fileName.getText().toString().trim());
            }
        });

        dialog.show();
    }

    public void closeDialog(){
        dialog.dismiss();
    }

    @Override
    public void onCloseDialog(SaveDialog dialog) {

    }

    @Override
    public void onCancelDialog(SaveDialog dialog) {

    }

    @Override
    public void onOkDialog(SaveDialog dialog, String name) {

    }
}
