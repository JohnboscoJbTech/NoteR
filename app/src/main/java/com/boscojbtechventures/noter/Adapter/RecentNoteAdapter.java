package com.boscojbtechventures.noter.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boscojbtechventures.noter.Model.Note;
import com.boscojbtechventures.noter.R;

import java.util.ArrayList;

/**
 * Created by UMB on 16/07/2017.
 */

public class RecentNoteAdapter extends ArrayAdapter {

    private ArrayList<Note> notes;
    TextView name, path;
    Button removeNote;
    RelativeLayout noteRecent;
    private int resource;

    public RecentNoteAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Note> _notes) {
        super(context, resource, _notes);
        this.notes = _notes;
        this.resource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = new LinearLayout(getContext());
            inflater.inflate(resource, layout, true);
        }
        else {
            layout = (LinearLayout)convertView;
        }
        init(layout);
        assign((Note)getItem(position));
        return layout;
    }

    private void init(View view) {
        name =  (TextView)view.findViewById(R.id.name);
        path = (TextView)view.findViewById(R.id.path);
        removeNote = (Button)view.findViewById(R.id.remove_note);
        noteRecent = (RelativeLayout)view.findViewById(R.id.note_recent_list);
    }

    private void assign(final Note note) {
        name.setText(note.getName());
        path.setText(note.getPath());
        removeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(), "Delete note?", Toast.LENGTH_LONG).show();
            }
        });
        /*noteRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(), "The name of the note is " + note.getName(), Toast.LENGTH_LONG).show();
            }
        });*/
    }
}
