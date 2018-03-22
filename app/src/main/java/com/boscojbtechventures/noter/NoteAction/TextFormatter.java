package com.boscojbtechventures.noter.NoteAction;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Johnbosco on 27/02/2017.
 */

public class TextFormatter {

    Context context;
    private int BOLD_TEXT = 1;
    private int ITALICS_TEXT = 2;
    private int UNDERLINE_TEXT = 3;
    private int start, end, stop;
    private String textbefore;
    private String selectedText;
    private String textafter;

    public TextFormatter(Context _context) {
        context = _context;
    }

    //makes the edittext's text bold
    public void makeBoldText(EditText editText) {
        formatText(editText, BOLD_TEXT);
    }

    //makes the edittext's text italic
    public void makeItalicsText(EditText editText) {
        formatText(editText, ITALICS_TEXT);
    }

    //underlines the edittext's text
    public void makeUnderlineText(EditText editText) {
        formatText(editText, UNDERLINE_TEXT);
    }

    public void changeCaps(EditText editText) {
        end = editText.length();

        //gets the selected text
        start = editText.getSelectionStart();
        stop = editText.getSelectionEnd();
        if (start > stop) {
            stop = editText.getSelectionStart();
            start = editText.getSelectionEnd();
        }

        //gets the texts as html
        textbefore = getTextAsHtml(editText, 0, start);
        selectedText = getTextAsHtml(editText, start, stop);
        textafter = getTextAsHtml(editText, stop, end);
        if (!selectedText.equals("") || start != stop) {
            int end0 = textbefore.length() - 12;
            int end1 = selectedText.length() - 12;
            int end2 = textafter.length() - 12;

            if (end0 < 6) {
                end0 = 6;
            }
            if (end1 < 6) {
                end1 = 6;
            }
            if (end2 < 6) {
                end2 = 6;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(CleanText(textbefore, end0));
            builder.append(setCapitalization(selectedText, end1));
            builder.append(CleanText(textafter, end2));

            editText.setText(Html.fromHtml(builder.toString()));
            editText.setSelection(stop);
            textbefore = "";
            textafter = "";
            selectedText = "";
        }
        else {
            Toast.makeText(context, "select a text", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelFormat(EditText editText) {
        end = editText.length();

        //gets the selected text
        start = editText.getSelectionStart();
        stop = editText.getSelectionEnd();
        if (start > stop) {
            stop = editText.getSelectionStart();
            start = editText.getSelectionEnd();
        }

        //gets the texts as html
        textbefore = getTextAsHtml(editText, 0, start);
        selectedText = editText.getText().subSequence(start, stop).toString();
        textafter = getTextAsHtml(editText, stop, end);
        if (!selectedText.equals("") || start != stop) {
            int end0 = textbefore.length() - 12;
            int end1 = selectedText.length() - 12;
            int end2 = textafter.length() - 12;

            if (end0 < 6) {
                end0 = 6;
            }
            if (end1 < 6) {
                end1 = 6;
            }
            if (end2 < 6) {
                end2 = 6;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(CleanText(textbefore, end0));
            builder.append(selectedText);
            builder.append(CleanText(textafter, end2));

            editText.setText(Html.fromHtml(builder.toString()));
            editText.setSelection(stop);
            textbefore = "";
            textafter = "";
            selectedText = "";
        }
        else {
            Toast.makeText(context, "select a text", Toast.LENGTH_SHORT).show();
        }
    }

    public void centerText(EditText editText) {
        int cursorPosition = editText.getSelectionStart();
        int width = editText.getWidth();
    }

    private void formatText(EditText editText, int action) {
        if (Build.VERSION.SDK_INT < 16) {
            //context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    //WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        end = editText.length();
        //gets the selected text
        start = editText.getSelectionStart();
        stop = editText.getSelectionEnd();
        if (start > stop) {
            stop = editText.getSelectionStart();
            start = editText.getSelectionEnd();
        }
        //gets the texts as html
        textbefore = getTextAsHtml(editText, 0, start);
        selectedText = getTextAsHtml(editText, start, stop);
        textafter = getTextAsHtml(editText, stop, end);
        if (!selectedText.equals("") || start != stop) {
            int end0 = textbefore.length() - 12;
            int end1 = selectedText.length() - 12;
            int end2 = textafter.length() - 12;

            if (end0 < 6) {
                end0 = 6;
            }
            if (end1 < 6) {
                end1 = 6;
            }
            if (end2 < 6) {
                end2 = 6;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(CleanText(textbefore, end0));
            builder.append(CleanSelectedText(selectedText, end1, action));
            builder.append(CleanText(textafter, end2));

            editText.setText(Html.fromHtml(builder.toString()));
            editText.setSelection(stop);
            textbefore = "";
            textafter = "";
            selectedText = "";
        }
        else {
            Toast.makeText(context, "select a text", Toast.LENGTH_SHORT).show();
        }
    }

    //cleans the text removing all html format inserted by the spannablestring method
    public StringBuilder CleanText(String text, int end) {
        StringBuilder builder = new StringBuilder();
        builder.append(text);
        builder.replace(0, 13, "");
        builder.replace(end-6, end, "");
        return builder;
    }

    private String setCapitalization(String text, int end) {
        StringBuilder builder = new StringBuilder();
        builder.append(text);
        builder.replace(0, 13, "");
        builder.replace(end-6, end, "");
        String caseText = builder.toString();
        if (isUpperCase(caseText)) {
            return changeUpperCase(caseText);
        }
        else if (isLowerCase(caseText)){
            return changeLowerCase(caseText);
        }
        else {
            return caseText;
        }
    }

    //cleans the text removing all html format inserted by the spannablestring method
    //and formmating the tex with the right format (bold, italic, etc)
    private StringBuilder CleanSelectedText(String text, int end, int action) {
        StringBuilder builder = new StringBuilder();
        builder.append(text);
        builder.replace(0, 13, "");
        builder.replace(end-6, end, "");
        StringBuilder chosen = new StringBuilder();
        if(action == 1) {
            chosen.append("<b>").append(builder.toString()).append("</b>");
        }
        if(action == 2) {
            chosen.append("<i>").append(builder.toString()).append("</i>");
        }
        if(action == 3) {
            chosen.append("<u>").append(builder.toString()).append("</u>");
        }
        return chosen;
    }

    //returns the string as html --use jsoup to clear html formatting
    private String getTextAsHtml(EditText text, int start, int end) {
        return Html.toHtml(new SpannableString(text.getText().subSequence(start, end)));
    }

    private boolean isUpperCase(String text) {
        char c = text.charAt(0);
        if (Character.isUpperCase(c)) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isLowerCase(String text) {
        char c = text.charAt(0);
        if (Character.isLowerCase(c)) {
            return true;
        }
        else {
            return false;
        }
    }

    private String changeUpperCase(String text) {
        String output = "";
        for (int i = 0; i < text.length(); i++) {
            output += Character.toLowerCase(text.charAt(i));
        }
        return output;
    }

    private String changeLowerCase(String text) {
        String output = "";
        for (int i = 0; i < text.length(); i++) {
            output += Character.toUpperCase(text.charAt(i));
        }
        return output;
    }

    private boolean isFullStop(){
        return false;
    }

    private String changeCamoCase(String text) {
        return "";
    }

    private void editText(EditText editText) {
        Editable editable = editText.getEditableText();
        //add text to editable
        String addedText = editable.append("Text to add").toString();//3 overloads
        //clears the editable
        editable.clear();
        //clears also the span
        editable.clearSpans();
        //Returns the array of input filters that are currently applied to changes to this Editable.
        editable.getFilters();
        //and lots more...
    }

    private void formatTextEditable(EditText editText, int action) {
        Editable editable = editText.getEditableText();
        int end = editable.length();
    }
}
