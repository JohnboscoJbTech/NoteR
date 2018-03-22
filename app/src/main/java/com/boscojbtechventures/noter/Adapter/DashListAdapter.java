package com.boscojbtechventures.noter.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boscojbtechventures.noter.R;

import java.util.List;

/**
 * Created by UMB on 15/07/2017.
 */

public class DashListAdapter extends ArrayAdapter {

    private ImageView fileIcon;
    private TextView fileName;
    private int resource;

    public DashListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull Object[] objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = new LinearLayout(getContext());
            layout.setPadding(5,5,5,5);
            inflater.inflate(resource, layout, true);
        }
        else {
            layout = (LinearLayout)convertView;
        }
        init(layout);
        assign(getItem(position).toString());
        return layout;
    }

    private void init(View view) {
        fileIcon = (ImageView) view.findViewById(R.id.file_icon);
        fileName =(TextView)view.findViewById(R.id.file_name);
    }

    private void assign(String file) {
        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (file.equals("...")) {
                fileIcon.setBackground(getContext().getResources().getDrawable(R.drawable.ic_backspace_black_24dp));
            }else if (!file.contains(".")) {
                fileIcon.setBackground(getContext().getResources().getDrawable(R.drawable.dashboard_folder));
            }
        }*/
        fileName.setText(file);
    }

    private String stripConcats(String name) {
        return name.substring(4, name.length());
    }
}
