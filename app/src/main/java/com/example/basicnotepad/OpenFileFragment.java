package com.example.basicnotepad;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

/**
 * Choose note files saved on the user's device
 */
public class OpenFileFragment extends Fragment implements NotepadFileSelectedListener {

    /** Listener for selecting a notepad file */
    private final NotepadFileSelectedListener notepadSelectedListener;

    /** ArrayList storing the saved notepad files */
    private final ArrayList<File> files;

    /**
     * Choose note files saved on the user's device
     * @param notepadSelectedListener The listener that needs to be notified when fragment has
     * a selected file.
     * @param files The list of Note files to open from
     */
    public OpenFileFragment(NotepadFileSelectedListener notepadSelectedListener, ArrayList<File> files) {
        super(R.layout.open_note_file);
        this.files = files;
        this.notepadSelectedListener = notepadSelectedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.open_note_file, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView fileRecyclerView = view.findViewById(R.id.fileRecyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        fileRecyclerView.setLayoutManager(layoutManager);

        NotepadAdapter notepadAdapter = new NotepadAdapter(this.files, this);
        fileRecyclerView.setAdapter(notepadAdapter);
    }

    @Override
    public void onFileSelected(File file) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Are you sure you want to select this file?")
                .setTitle("Confirm File Selection");
                
        // Add the buttons
        builder.setPositiveButton("Confirm", (dialog, id) -> {
            // User clicked OK button
            OpenFileFragment.this.notepadSelectedListener.onFileSelected(file);
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> {
            // User cancelled the dialog
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
