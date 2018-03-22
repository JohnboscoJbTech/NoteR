package com.boscojbtechventures.noter.NoteAction;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import com.boscojbtechventures.noter.R;

import java.util.Locale;

/**
 * Created by Johnbosco on 13/05/2017.
 */

public class SpeechToText {

    Context context;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    public SpeechToText(Context _context) {
        context = _context;
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,  (R.string.speech_prompt));
        try {
            //startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context, (R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }
}
