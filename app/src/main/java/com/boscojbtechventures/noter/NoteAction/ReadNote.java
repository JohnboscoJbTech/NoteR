package com.boscojbtechventures.noter.NoteAction;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Johnbosco on 21/03/2017.
 */

public class ReadNote {

    public String read(String filename) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    public String readTextFile(File file) {
        InputStreamReader inputStreamReader;
        BufferedReader bufferReader;
        StringBuffer stringBuffer = new StringBuffer();
        int character;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(file));//detectCharSet(file.getAbsolutePath())
            bufferReader = new BufferedReader(inputStreamReader);
            do {
                character = bufferReader.read();
                if (character != -1) {
                    stringBuffer.append((char) character);
                }
            } while (character != -1);
            bufferReader.close();
        } catch (IOException e) {
            Log.w("GET", "Can't read file " + file.getName(), e);
            return null;
        } catch (OutOfMemoryError e) {
            Log.w("GET", "File is to big to read", e);
            return null;
        }
        // detect end of lines
        //int windows = content.indexOf("\r\n");
        //int macos = content.indexOf("\r");
        return stringBuffer.toString();
    }
}
