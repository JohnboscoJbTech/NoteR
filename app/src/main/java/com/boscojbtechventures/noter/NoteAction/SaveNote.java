package com.boscojbtechventures.noter.NoteAction;

import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.widget.EditText;

import com.boscojbtechventures.noter.Model.Note;
import com.boscojbtechventures.noter.NoteBook;
import com.boscojbtechventures.noter.Repo.NoteRepository;
import com.boscojbtechventures.noter.Utility.StorageUtility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Johnbosco on 10/03/2017.
 */

public class SaveNote extends NoteBook {

    private Context context;
    private String noteName;
    private String noteTexts;
    TextFormatter format;
    int NOTE_SAVED = 444;
    int HAS_SPACES = 111;
    int NO_PERMISSION = 222;
    int NAME_EXISTS = 333;
    int UNIDENTIFIED = 555;

    public SaveNote(Context _context) {
        context = _context;
    }

    public int saveNoteBook(EditText noteContent, Note noteBook) {
        noteName = noteBook.getName();
        if (noteName.contains(" ")) {
            return HAS_SPACES;
        }
        else {
            try {
                StorageUtility utility = new StorageUtility();
                NoteRepository noteRepository = new NoteRepository(context);

                format = new TextFormatter(context);
                File path;
                if (noteBook.getPath().equals("")) {
                    path = utility.getFileStorageDir();//should be directory documents
                }
                else {
                    path = new File(noteBook.getPath());
                }
                File file;
                if (noteName.endsWith(".txt")){
                    file = new File(path, noteName);
                }
                else {
                    file = new File(path, noteName.concat(".txt"));
                }
                if ((file.exists() && !file.canWrite())) {
                    return NO_PERMISSION;
                }
                else if ((file.exists() && file.canWrite())) {
                    if(noteBook.isNoteOverwriteable()) {
                        boolean fileCreated = false, deleted;
                        deleted = file.delete();
                        if (deleted) {
                            fileCreated = file.createNewFile();
                        }
                        if (fileCreated) {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                            noteTexts = Html.toHtml(new SpannableString(noteContent.getText()));
                            int end = noteTexts.length() - 12;
                            StringBuilder builder = format.CleanText(noteTexts, end);
                            streamWriter.append(builder.toString());
                            streamWriter.close();
                            outputStream.close();
                            noteRepository.addNote(noteBook);
                            return NOTE_SAVED;
                        }
                    }
                    else {
                        return NAME_EXISTS;
                    }
                }
                else {
                    boolean fileCreated = file.createNewFile();
                    if(fileCreated) {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                            /*if (fileformat == FILEFORMAT_CR)
                            {
                                out.write(noteContent.getText().toString().replace("\n", "\r"));
                            } else if (fileformat == FILEFORMAT_CRNL) {
                                out.write(noteContent.getText().toString().replace("\n", "\r\n"));
                            } else {
                                out.write(noteContent.getText().toString());
                            }*/
                        noteTexts = Html.toHtml(new SpannableString(noteContent.getText()));
                        int end = noteTexts.length() - 12;
                        StringBuilder builder = format.CleanText(noteTexts, end);
                        streamWriter.append(builder.toString());
                        streamWriter.close();
                        outputStream.close();
                        noteRepository.addNote(noteBook);
                        return NOTE_SAVED;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return UNIDENTIFIED;
    }

    public static boolean writeTextFile(String path, String text) {
        File file = new File(path);
        OutputStreamWriter writer;
        FileOutputStream stream;
        BufferedWriter out;
        String eol_text = text;
        try {
            /*if (Settings.END_OF_LINE != EOL_LINUX) {
                eol_text = eol_text.replaceAll("\n", Settings.getEndOfLine());
            }*/

            stream = new FileOutputStream(file);
            writer = new OutputStreamWriter(stream);//, Settings.ENCODING);
            out = new BufferedWriter(writer);
            out.write(eol_text);
            out.flush();
            stream.getFD().sync();
            out.close();
        } catch (OutOfMemoryError e) {
            Log.w("Out of memory error", e);
            return false;
        } catch (IOException e) {
            Log.w("Can't write to file " + path, e);
            return false;
        }
        return true;
    }
}
