package com.boscojbtechventures.noter;

import android.accessibilityservice.AccessibilityService;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boscojbtechventures.noter.Fragment.Dialog.MultiDialog;
import com.boscojbtechventures.noter.Fragment.Dialog.SaveDialog;
import com.boscojbtechventures.noter.Interface.IMultiDialog;
import com.boscojbtechventures.noter.Interface.ISaveDialog;
import com.boscojbtechventures.noter.Model.CustomExBookView;
import com.boscojbtechventures.noter.Model.Note;
import com.boscojbtechventures.noter.Model.NotePaper;
import com.boscojbtechventures.noter.NoteAction.ReadNote;
import com.boscojbtechventures.noter.NoteAction.SaveNote;
import com.boscojbtechventures.noter.NoteAction.TextFormatter;
import com.boscojbtechventures.noter.NoteAction.UndoRedo;
import com.boscojbtechventures.noter.Utility.FileChooser;
import com.boscojbtechventures.noter.Utility.StorageUtility;
import com.boscojbtechventures.noter.Utility.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class NoteBook extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ISaveDialog, IMultiDialog {

    Context context;
    EditText noteContent;
    ImageView bold, underline, italics, capitalize, cancelFormat, speak;
    TextView noteName;
    TextFormatter formatter;
    UndoRedo undoRedo;
    String filename;
    private String PREFERENCE_NAME = "com.boscojbtechventures.noter";
    private String PREFERENCE_SAVED = "NOTE_SAVED";
    private String PREFERENCE_OVERWRITEABLE = "NOTE_OVERWRITEABLE";
    private boolean SAVED_DEFAULT = false;
    private int saved;
    SaveNote saveNote;
    private ReadNote readNote;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int CHOOSE_FILE_REQUESTCODE = 200;
    private String notePath, noteAbsPath;
    private Note note;
    private ProgressDialog progressDialog;
    LinearLayout drawingActions, editingActions;
    public static boolean inDrawingMode;
    private ShareActionProvider mShareActionProvider;
    private String imageFilePathWithName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        noteName = (TextView)toolbar.findViewById(R.id.title_note_name);
        setSupportActionBar(toolbar);
        context = this;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menuNav = navigationView.getMenu();
        MenuItem item = menuNav.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        initialize();

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formatter = new TextFormatter(context);
                formatter.makeBoldText(noteContent);
            }
        });
        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formatter = new TextFormatter(context);
                formatter.makeUnderlineText(noteContent);
            }
        });
        italics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formatter = new TextFormatter(context);
                formatter.makeItalicsText(noteContent);
            }
        });
        capitalize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formatter = new TextFormatter(context);
                formatter.changeCaps(noteContent);
            }
        });
        cancelFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formatter = new TextFormatter(context);
                formatter.cancelFormat(noteContent);
            }
        });
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        final ImageView undo = (ImageView)findViewById(R.id.undo_note);

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoRedo.undo();
            }
        });
        final ImageView redo = (ImageView)findViewById(R.id.redo_note);

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoRedo.redo();
            }
        });
        //sharedPreferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        note = new Note();
        note.setDate(new Date().toString());
        Intent intent = getIntent();
        if ((intent.hasExtra("NOTE_NAME") && intent.hasExtra("NOTE_TEXT"))) {
            filename = intent.getStringExtra("NOTE_NAME");
            noteName.setText(filename);
            String content = intent.getStringExtra("NOTE_TEXT");
            noteContent.setText(Html.fromHtml(content));
            notePath = intent.getStringExtra("NOTE_PATH");
            noteAbsPath = intent.getStringExtra("NOTE_ABSOLUTE_PATH");
            note.setName(filename);
            note.setPath(notePath);
            note.setAbsPath(noteAbsPath);
            note.setHasNoteBeenSaved(false);
            note.setNoteOverwriteable(true);
            note.setNoteEdited(false);
            /*hasNoteBeenSaved = false;//sharedPreferences.getBoolean(PREFERENCE_SAVED, SAVED_DEFAULT);
            isNoteEdited = false;
            //sharedPreferences.edit().putBoolean(PREFERENCE_OVERWRITEABLE, true).apply();
            isNoteOverwriteable  = true; //sharedPreferences.getBoolean(PREFERENCE_OVERWRITEABLE, SAVED_DEFAULT);*/
        }
        else if (intent.getType() != null && intent.getType().equals("text/plain")) {
            // Handle intents with text ...
            Uri data = intent.getData();
            File file = new File(data.getPath());
            if ((file.exists() && file.canWrite())) {
                readNote = new ReadNote();
                String content = readNote.readTextFile(file);
                noteContent.setText(Html.fromHtml(content));
                filename = Utils.getNoteName(data.getPath());
                noteName.setText(filename);
                notePath = Utils.getNotePathDirectory(data.getPath());
                noteAbsPath = Utils.getNotePathDirectory(data.getPath());
                note.setName(filename);
                note.setPath(notePath);
                note.setAbsPath(noteAbsPath);
                note.setHasNoteBeenSaved(false);
                note.setNoteOverwriteable(true);
                note.setNoteEdited(false);
            }
        }
        else {
            filename = context.getString(R.string.note_name);
            noteName.setText(filename);
            notePath = "";
            noteAbsPath = "";
            noteContent.setText("");
            /*sharedPreferences.edit().putBoolean(PREFERENCE_OVERWRITEABLE, false).apply();
            isNoteOverwriteable = false; //sharedPreferences.getBoolean(PREFERENCE_OVERWRITEABLE, SAVED_DEFAULT);
            hasNoteBeenSaved = false; //sharedPreferences.getBoolean(PREFERENCE_SAVED, SAVED_DEFAULT);;
            isNoteEdited = false;*/
            note.setName(filename);
            note.setPath(notePath);
            note.setAbsPath(noteAbsPath);
            note.setHasNoteBeenSaved(false);
            note.setNoteOverwriteable(false);
            note.setNoteEdited(false);
        }
        noteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (filename.endsWith("*")) {
                    noteName.setText(filename);
                }
                /*else if (!noteEdited) {
                    noteBookName.setText(filename);
                }*/
                else {
                    noteName.setText(filename + "*");
                }
                note.setNoteEdited(true);
                note.setHasNoteBeenSaved(false);
                /*isNoteEdited = true;
                hasNoteBeenSaved = false;*/
            }
            @Override
            public void afterTextChanged(Editable s) {
                /*if (s.toString().endsWith(".")){
                    noteContent.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                }*/
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.note_save_button) {
            if(note.isHasNoteBeenSaved()){
                saveNote();
            }
            else if (note.isNoteEdited() && note.isNoteOverwriteable()) {
                saveNote();
            }
            else if(note.isNoteEdited()) {
                /*DialogFragment saveFileDialogFragment = new SaveFileDialogFragment();
                saveFileDialogFragment.show(getFragmentManager(), "SaveFileDialogFragment");*/
                SaveDialog dialog = new SaveDialog(context, "SaveDialog", context.getResources().getText(R.string.note_name).toString());
                dialog.showDialog();
            }
            else {
                Toast.makeText(context, R.string.no_changes_made, Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.action_editing) {
            if (inDrawingMode) {
                MultiDialog dialog = new MultiDialog(context, "DrawingKeepDialog");
                dialog.showDialog(context.getResources().getString(R.string.confirm_keep_drawing));
            }
            drawingActions.animate().translationY(-80);
            editingActions.animate().translationY(0);
            inDrawingMode = false;
        }
        if (id == R.id.action_drawing) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(noteContent.getWindowToken(), 0);
            editingActions.animate().translationY(-80);
            drawingActions.animate().translationY(0);
            inDrawingMode = true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new_note) {
            if (noteContent.getText().toString().equals("")) {
                noteName.setText(R.string.note_name);
            }
            else if (note.isNoteEdited() || !note.isHasNoteBeenSaved()){
                //show dialog for saving note name
                SaveDialog dialog = new SaveDialog(context, "SaveDialog", noteName.getText().toString());
                dialog.showDialog();
            }
            else {
                noteName.setText(R.string.note_name);
                noteContent.setText("");
                note.setNoteEdited(false);
                note.setHasNoteBeenSaved(false);
                note.setNoteOverwriteable(false);
                /*isNoteEdited = false;
                hasNoteBeenSaved = false;
                isNoteOverwriteable = false;*/
            }
        } else if (id == R.id.nav_open_note) {
            //check if the note is saved
            if(note.isNoteEdited() && !note.isHasNoteBeenSaved()) {
                MultiDialog dialog = new MultiDialog(context, "OpenNoteDialog");
                dialog.showDialog(context.getResources().getString(R.string.confirm_note_close));
            }
            else {
                readNote = new ReadNote();
                new FileChooser(NoteBook.this).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(final File file) throws FileNotFoundException {
                        //new Dashboard.ReadNoteDetails().execute(file);
                    }}).showDialog();
            }
        } else if (id == R.id.nav_save_note_as) {
            //this is just like newly saving a note
            SaveDialog dialog = new SaveDialog(context, "SaveDialog", context.getResources().getText(R.string.note_name).toString());
            dialog.showDialog();
        } else if (id == R.id.nav_open_folder) {
            if(notePath.equals("")) {
                Toast.makeText(context, "There is no open note", Toast.LENGTH_SHORT).show();
            }
            else {
                File path = new File(notePath.substring(0, notePath.lastIndexOf("/")));
                FileChooser fileChooser = new FileChooser(NoteBook.this, path).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(File file) throws FileNotFoundException {
                        Toast.makeText(context, "Close note before opening another note", Toast.LENGTH_SHORT).show();
                    }
                });
                fileChooser.showDialog();
            }
        } else if (id == R.id.nav_rename) {
            //open a dialog and rename the file
            SaveDialog dialog = new SaveDialog(context, "RenameDialog", noteName.getText().toString());
            dialog.showDialog();

        } else if (id == R.id.nav_close_note) {
            //check if note has been saved
            if (note.isNoteEdited()) {
                MultiDialog dialog = new MultiDialog(context, "ConfirmDialog");
                dialog.showDialog(context.getResources().getString(R.string.confirm_note_close));
            }
            else {
                Utils.startAnotherActivity(Dashboard.class, context);
            }
        }
        else if (id == R.id.menu_item_share) {
            if (!note.isNoteOverwriteable() && !note.isHasNoteBeenSaved()) {
                Toast.makeText(context, "This book has not been saved", Toast.LENGTH_SHORT).show();
                return false;
            }
            else {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(note.getPath() + "/" + note.getName())));
                sendIntent.setType("text/*");
                startActivity(sendIntent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initialize() {
        //note book initialization
        noteContent = (EditText) findViewById(R.id.note_content);
        //edit action initialization
        bold = (ImageView)findViewById(R.id.boldText);
        underline = (ImageView)findViewById(R.id.underlineText);
        italics = (ImageView)findViewById(R.id.italicText);
        capitalize = (ImageView)findViewById(R.id.all_caps);
        cancelFormat = (ImageView)findViewById(R.id.cancel_format);
        speak = (ImageView)findViewById(R.id.microphone);
        //undo redo initialization
        undoRedo = new UndoRedo(noteContent);
        //mode initialization
        editingActions = (LinearLayout)findViewById(R.id.formatting_actions);
        drawingActions = (LinearLayout)findViewById(R.id.drawing_actions);

        /**
         * Initial states
         * */
        drawingActions.animate().translationY(-80);
    }

    public void saveNote() {
        StorageUtility utility = new StorageUtility();
        //NoteRepository repository = new NoteRepository(context);
        //Note note = new Note();
        if(notePath.equals("")) {
            notePath = utility.getFilePath();
            //Todo show dialog for user to select location to store note
        }
        if(noteAbsPath.equals("")) {
            noteAbsPath = utility.getAbsFilePath() + "/" + filename;
        }
        note.setPath(notePath);
        note.setName(filename);
        note.setAbsPath(noteAbsPath);
        note.setDrawingPath(CustomExBookView.drawingPaths.toString());
        if (CustomExBookView.drawingPaths.size() > 0) note.setHasDrawing(1);
        else note.setHasDrawing(0);

        String contents = noteContent.getText().toString();
        if (contents.equals("")) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.content_note),
                    R.string.empty_note, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else {
            saveNote = new SaveNote(context);
            if (utility.isExternalStorageWritable()) {
                if (utility.checkSpaceOnExternalStorage()) {

                    saved = saveNote.saveNoteBook(noteContent, note);
                    Snackbar snackbar;
                    switch (saved) {
                        case 111:
                            snackbar = Snackbar.make(findViewById(R.id.content_note),
                                    R.string.incorrect_name, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            break;
                        case 222:
                            snackbar = Snackbar.make(findViewById(R.id.content_note),
                                    R.string.no_permission, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            break;
                        case 333:
                            MultiDialog dialog = new MultiDialog(context, "ConfirmOverwriteDialog");
                            dialog.showDialog(context.getResources().getString(R.string.confirm_overwrite));
                            break;
                        case 444:
                            if (filename.endsWith(".txt")) {
                                noteName.setText(filename);
                            }else {
                                noteName.setText(filename + ".txt");
                            }
                            snackbar = Snackbar.make(findViewById(R.id.content_note),
                                    getString(R.string.saved_note, notePath), Snackbar.LENGTH_LONG);
                            snackbar.show();
                            note.setHasNoteBeenSaved(true);
                            note.setNoteEdited(false);
                            note.setNoteOverwriteable(true);
                            /*hasNoteBeenSaved = true;
                            isNoteEdited = false;
                            isNoteOverwriteable = true;*/
                            //repository.addNote(note);
                            break;
                        case 555:
                            snackbar = Snackbar.make(findViewById(R.id.content_note),
                                    R.string.error_saving, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            break;
                    }
                } else {
                    Toast.makeText(context, R.string.space_not_enough, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, R.string.insert_sdcard, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,  (R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context, (R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    noteContent.setText(result.get(0));
                }
                break;
            }
            case CHOOSE_FILE_REQUESTCODE: {
                if (resultCode == RESULT_OK && data != null) {
                    String filePath = data.getData().getPath();
                    String[] names = filePath.split("/");
                    String fname = names[names.length - 1];
                    StorageUtility utility = new StorageUtility();
                    File path = utility.getFileStorageDir();
                    String fileName = path + "/" + fname;
                    readNote = new ReadNote();
                    String notes = readNote.read(fileName);
                    filename = fname;
                    noteName.setText(filename);
                    noteContent.setText(Html.fromHtml(notes));
                    note.setNoteOverwriteable(true);
                    note.setNoteEdited(false);
                    note.setHasNoteBeenSaved(true);
                    /*isNoteOverwriteable = true;
                    hasNoteBeenSaved = true;
                    isNoteEdited = false;*/
                }
            }
        }
    }

    @Override
    public void onCloseDialog(SaveDialog dialog) {
        switch (dialog.getName()) {
            case "RenameDialog":
                break;
            case "SaveDialog":
                //Utils.startAnotherActivity(Dashboard.class, context);
                break;
            case "ConfirmDialog":
                break;
        }
        dialog.closeDialog();
    }

    @Override
    public void onCancelDialog(SaveDialog dialog) {
        switch (dialog.getName()) {
            case "RenameDialog":
                break;
            case "SaveDialog":
                break;
            case "ConfirmDialog":
                break;
        }
        dialog.closeDialog();
    }

    @Override
    public void onOkDialog(SaveDialog dialog, String name) {
        switch (dialog.getName()) {
            case "RenameDialog":
                this.filename = name;
                if (filename == null || filename.equals("")) {
                    Toast.makeText(this.getApplicationContext(), R.string.filename_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    noteName.setText(name);
                    Toast.makeText(this.getApplicationContext(), "File renamed", Toast.LENGTH_LONG).show();
                }
                break;
            case "SaveDialog":
                this.filename = name;
                if (filename == null || filename.equals("")) {
                    Toast.makeText(this.getApplicationContext(), R.string.filename_empty, Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    saveNote();
                }
                break;
        }
        dialog.closeDialog();
    }

    @Override
    public void onCloseDialog(MultiDialog dialog) {
        dialog.closeDialog();
    }

    @Override
    public void onCancelDialog(MultiDialog dialog) {
        switch (dialog.getName()) {
            case "ConfirmDialog":
                Utils.startAnotherActivity(Dashboard.class, context);
                break;
            case "OpenNoteDialog":
                readNote = new ReadNote();
                new FileChooser(NoteBook.this).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(final File file) throws FileNotFoundException {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        new ReadNoteDetails().execute(file);
                    }}).showDialog();
                break;
            case "ConfirmOverwriteDialog":
                dialog.closeDialog();
                break;
            default:
                dialog.closeDialog();
                break;
        }
        dialog.closeDialog();
    }

    @Override
    public void onOkDialog(MultiDialog dialog) {
        switch (dialog.getName()) {
            case "ConfirmDialog":
                if(note.isHasNoteBeenSaved()) {
                    saveNote();
                }
                else {
                    SaveDialog innerDialog = new SaveDialog(context, "RenameDialog", noteName.getText().toString());
                    innerDialog.showDialog();
                }
                break;
            case "OpenNoteDialog":
                SaveDialog innerDialog = new SaveDialog(context, "RenameDialog", noteName.getText().toString());
                innerDialog.showDialog();
                break;
            case "ConfirmOverwriteDialog":
                note.setNoteOverwriteable(true);
                saveNote();
                break;
            case "DrawingKeepDialog":
                File imagesDirectory = new File(getFilesDir(), "Images");
                String imageFileName = UUID.randomUUID().toString()+".png";
                final String initialText = noteContent.getText().toString().trim();
                noteContent.setText("");
                ArrayList<Float> xAxis = ((NotePaper)noteContent).xAxisValues;
                ArrayList<Float> yAxis = ((NotePaper)noteContent).yAxisValues;
                float minXValue = Collections.min(xAxis);
                float minYValue = Collections.min(yAxis);
                float maxXValue = Collections.max(xAxis);
                float maxYValue = Collections.max(yAxis);
                noteContent.setDrawingCacheEnabled(true);
                if (!imagesDirectory.exists()) {
                    boolean directoryCreated = imagesDirectory.mkdir();
                    if (directoryCreated) {
                        File imageFile = new File(imagesDirectory, imageFileName);
                        imageFilePathWithName = imageFile.toString();
                        FileOutputStream output = null;
                        try {
                            output = new FileOutputStream(imageFile);
                            Bitmap bitmapImage = Bitmap.createBitmap(noteContent.getDrawingCache(), (int)minXValue, (int)maxYValue, (int)(maxXValue-minXValue), (int)(maxYValue-minYValue));
                            Bitmap b = noteContent.getDrawingCache();//((NotePaper)noteContent).canvasBitmap;
                            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, output);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else {
                    File imageFile = new File(imagesDirectory, imageFileName);
                    imageFilePathWithName = imageFile.toString();
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(imageFile);
                        Bitmap bitmapImage = Bitmap.createBitmap(noteContent.getDrawingCache(), (int)minXValue, (int)minYValue, (int)(maxXValue-minXValue) + 1,
                                (int)(maxYValue-minYValue) + 1);//noteContent.getDrawingCache();
                        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, output);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //imageFilePathWithName = "/storage/sdcard0/Pictures/recent.png";
                String content = initialText + " <img src=\"" + imageFilePathWithName + "\"/>";
                noteContent.setText(Html.fromHtml(content, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        Drawable drawFromPath;
                        //int path = context.getResources().getIdentifier(imageFilePathWithName, "drawable", "com.package...");
                        //drawFromPath = context.getResources().getDrawable(path);
                        drawFromPath = Drawable.createFromPath(source);
                        drawFromPath.setBounds(initialText.length(), 0, drawFromPath.getIntrinsicWidth(), drawFromPath.getIntrinsicHeight());
                        return drawFromPath;
                    }
                }, null));
               /* String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), drawView.getDrawingCache(),
                        UUID.randomUUID().toString()+".png", "drawing");*/
                break;
        }
        dialog.closeDialog();
    }

    private class ReadNoteDetails extends AsyncTask<File, Void, Intent> {
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
            progressDialog.dismiss();
            finish();
            context.startActivity(intent);
        }
    }
}
