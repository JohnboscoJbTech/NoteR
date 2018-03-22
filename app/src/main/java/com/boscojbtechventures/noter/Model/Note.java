package com.boscojbtechventures.noter.Model;

/**
 * Created by Johnbosco on 14/02/2017.
 */

public class Note {

    private String name;
    private int id;
    private String date;
    private String path;
    private String absPath;
    private int deleted;
    private boolean isNoteEdited;
    private boolean isNoteOverwriteable;
    private boolean hasNoteBeenSaved;
    private int hasDrawing;//1=> has drawing, 0=> has no drawing
    private String drawingPath;

    public Note() {
//TODO add update note for notes that are overwriteable
    }

    public void setId (int _id) {
        this.id = _id;
    }

    public void setName (String _name) {
        this.name = _name;
    }

    public void setDate (String _date) {
        this.date = _date;
    }

    public void setPath(String _owner) {
        this.path = _owner;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDate() {
        return this.date;
    }

    public String getPath() {
        return this.path;
    }

    public String getAbsPath() {
        return absPath;
    }

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public boolean isNoteEdited() {
        return isNoteEdited;
    }

    public void setNoteEdited(boolean noteEdited) {
        isNoteEdited = noteEdited;
    }

    public boolean isNoteOverwriteable() {
        return isNoteOverwriteable;
    }

    public void setNoteOverwriteable(boolean noteOverwriteable) {
        isNoteOverwriteable = noteOverwriteable;
    }

    public boolean isHasNoteBeenSaved() {
        return hasNoteBeenSaved;
    }

    public void setHasNoteBeenSaved(boolean hasNoteBeenSaved) {
        this.hasNoteBeenSaved = hasNoteBeenSaved;
    }

    public int getHasDrawing() {
        return hasDrawing;
    }

    public void setHasDrawing(int hasDrawing) {
        this.hasDrawing = hasDrawing;
    }

    public String getDrawingPath() {
        return drawingPath;
    }

    public void setDrawingPath(String drawingPath) {
        this.drawingPath = drawingPath;
    }
}
