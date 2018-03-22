package com.boscojbtechventures.noter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boscojbtechventures.noter.Adapter.DashboardViewAdapter;
import com.boscojbtechventures.noter.Fragment.Dialog.MultiDialog;
import com.boscojbtechventures.noter.Interface.IMultiDialog;
import com.boscojbtechventures.noter.Model.Note;
import com.boscojbtechventures.noter.NoteAction.ReadNote;
import com.boscojbtechventures.noter.Repo.NoteRepository;
import com.boscojbtechventures.noter.Utility.FileChooser;
import com.boscojbtechventures.noter.Utility.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Dashboard extends AppCompatActivity implements IMultiDialog {

    //ImageView newNote, openNote, instruction, about;
    ImageButton newNote, openNote, options;
    GridView recentNote;
    ListView recentNotes;
    TextView textView;
    ProgressDialog dialog;
    Context context;
    ReadNote readNote;
    private static final int CHOOSE_FILE_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        initialize();
        assignValues();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        MultiDialog dialog = new MultiDialog(context, "AppExitDialog");
        dialog.showDialog(context.getResources().getString(R.string.confirm_close));
        //super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        assignValues();
    }

    private void initialize() {
        newNote = (ImageButton)findViewById(R.id.btn_new);
        openNote = (ImageButton)findViewById(R.id.btn_open);
        options = (ImageButton)findViewById(R.id.btn_options);
        //recentNotes = (GridView)findViewById(R.id.recent_notes);
        recentNotes = (ListView) findViewById(R.id.recent_notes);
        textView = (TextView)findViewById(R.id.empty_list);
    }

    private void assignValues() {
        final NoteRepository noteRepository = new NoteRepository(context);
        ArrayList<Note> notes = noteRepository.getAllNotes();
        if(notes.isEmpty()) {
            textView.setText("You have no recent notes Create a new note");
            textView.setVisibility(View.VISIBLE);
        }
        else {
            textView.setVisibility(View.GONE);
            DashboardViewAdapter adapter = new DashboardViewAdapter(context, notes);
            recentNotes.setAdapter(adapter);
            /*recentNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Note note = noteRepository.viewNote(position);
                    Toast.makeText(context, "You clicked " + note.getName(), Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }

    private void setListeners() {
        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startAnotherActivity(NoteBook.class, context);
            }
        });
        openNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readNote = new ReadNote();
                new FileChooser(Dashboard.this).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(final File file) throws FileNotFoundException {
                        //progressBar.setVisibility(View.VISIBLE);
                        dialog = new ProgressDialog(context);
                        //dialog.setProgress(0);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setCancelable(false);
                        dialog.show();
                        new ReadNoteDetails().execute(file);
                    }}).showDialog();
            }
        });
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog optionsDialog = new Dialog(context);
                ListView listView = new ListView(context);
                String[] options = {"Instructions", "About"};
                optionsDialog.setTitle("Choose an option");
                listView.setAdapter(new ArrayAdapter(context,android.R.layout.simple_list_item_1, options ) {
                    @Override
                    public View getView(int position, View view, ViewGroup parent) {
                        view = super.getView(position, view, parent);
                        ((TextView) view).setSingleLine(true);
                        return view;
                    }
                });
                listView.setOnItemClickListener(new ShowOption());
                optionsDialog.setContentView(listView);
                optionsDialog.show();
            }
        });
    }

    @Override
    public void onCloseDialog(MultiDialog dialog) {
        dialog.closeDialog();
    }

    @Override
    public void onCancelDialog(MultiDialog dialog) {
        dialog.closeDialog();
    }

    @Override
    public void onOkDialog(MultiDialog dialog) {
        finishAffinity();
    }

    private class ReadNoteDetails extends AsyncTask<File, Void, Intent> {
        final String PREFS_NAME = "com.boscojbtechventures.noter";
        final String PREF_FILE_PATH = "file_path";
        final int DOESNT_EXIST = -1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Intent doInBackground(File... files) {
            String fileName = files[0].getName();
            String filePath = Utils.getNotePathDirectory(files[0].getPath());
            String fileAbsolutePath = files[0].getAbsolutePath();
            int start = fileName.lastIndexOf(".");
            String extn = fileName.substring(start, fileName.length());
            boolean isExtension = fileName.endsWith(".txt");
            //initialize db-repo and note model and store details as extras
            //initialize the note intent
            Intent noteBook = new Intent(context, NoteBook.class);
            noteBook.putExtra("NOTE_TEXT", readNote.readTextFile(files[0]));
            noteBook.putExtra("NOTE_NAME", fileName);
            noteBook.putExtra("NOTE_PATH", filePath);
            noteBook.putExtra("NOTE_ABSOLUTE_PATH", fileAbsolutePath);
            return noteBook;
        }
        @Override
        protected void onPostExecute(Intent intent) {
            super.onPostExecute(intent);
            //dialog.setProgress(100);
            dialog.dismiss();
            context.startActivity(intent);
        }
    }

    private class ShowOption implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent option = null;
            switch (position) {
                case 0:
                    option = new Intent(context, Help.class);
                    break;
                case 1:
                    option = new Intent(context, About.class);
                    break;
            }
            startActivity(option);
        }
    }
}
