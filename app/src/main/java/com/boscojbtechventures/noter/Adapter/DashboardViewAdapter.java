package com.boscojbtechventures.noter.Adapter;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.boscojbtechventures.noter.Dashboard;
import com.boscojbtechventures.noter.Fragment.Dialog.MultiDialog;
import com.boscojbtechventures.noter.Model.Note;
import com.boscojbtechventures.noter.NoteAction.ReadNote;
import com.boscojbtechventures.noter.NoteBook;
import com.boscojbtechventures.noter.R;
import com.boscojbtechventures.noter.Repo.NoteRepository;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by UMB on 28/06/2017.
 */

public class DashboardViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Note> notes;
    TextView name, path;
    ImageButton removeNote;
    RelativeLayout recentLayout;

    public DashboardViewAdapter(Context _context, ArrayList<Note> _notes) {
        this.notes = _notes;
        context = _context;
    }
    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Note getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_view_pattern, null);
        init(convertView);
        assign(notes.get(position), convertView);
        return convertView;
    }

    private void init(View view) {
        name =  (TextView)view.findViewById(R.id.name);
        path = (TextView)view.findViewById(R.id.path);
        removeNote = (ImageButton)view.findViewById(R.id.remove_note);
        recentLayout = (RelativeLayout) view.findViewById(R.id.note_recent_list);
    }

    private void assign(final Note note, final View view) {
        name.setText(note.getName());
        path.setText(note.getPath());
        removeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NoteRepository noteRepository = new NoteRepository(context);
                noteRepository.deleteNote(note);
                Snackbar snackbar = Snackbar.make(view,
                        context.getString(R.string.recent_note_delete), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("YES", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noteRepository.undoDeleteNote(note);
                    }
                });
                snackbar.show();
                notifyDataSetChanged();
            }
        });
        recentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NoteRepository noteRepository = new NoteRepository(context);
                Note openNote = noteRepository.viewNote(note.getId());
                ReadNote readNote = new ReadNote();
                File path = new File(openNote.getPath());
                File file = new File(path, openNote.getName());
                if ((file.exists() && file.canWrite())) {
                    Intent noteBook = new Intent(context, NoteBook.class);
                    noteBook.putExtra("NOTE_TEXT", readNote.readTextFile(file));
                    noteBook.putExtra("NOTE_NAME", openNote.getName());
                    noteBook.putExtra("NOTE_PATH", openNote.getPath());
                    noteBook.putExtra("NOTE_ABSOLUTE_PATH", openNote.getAbsPath());
                    context.startActivity(noteBook);
                }
            }
        });
    }
}
