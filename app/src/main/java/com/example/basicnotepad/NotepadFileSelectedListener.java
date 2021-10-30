package com.example.basicnotepad;

import java.io.File;

/**
 * Notifies that files has been selected by user.
 */
public interface NotepadFileSelectedListener {

    /**
     * Notifies that files has been selected by user.
     * @param file File that has been selected.
     */
    void onFileSelected(File file);

}
