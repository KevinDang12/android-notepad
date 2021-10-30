package com.example.basicnotepad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class for obtaining the list of files to list
 */
public class NotepadAdapter extends RecyclerView.Adapter<NotepadAdapter.FileViewHolder> {

    /** ArrayList storing the saved notepad files */
    private final ArrayList<File> files;

    /** Listener for selecting a notepad file */
    private final NotepadFileSelectedListener listener;

    /**
     * Constructor for obtaining the list of files to list
     * @param files The list of files to display
     * @param listener Notifies that file has been selected
     */
    public NotepadAdapter(ArrayList<File> files, NotepadFileSelectedListener listener) {
        this.files = files;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notepad_file_holder, parent, false);

        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        // Get file from file arraylist(position)
        File file = files.get(position);

        // Get last file modification in milliseconds
        long date = file.lastModified();

        // Send milliseconds into date format and display as a string
        Date lastModified = new Date(date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        String formattedDateString = formatter.format(lastModified);

        // set the filename and last modified date
        holder.setFileName(file.getName());
        holder.setFileDate(formattedDateString);
        holder.itemView.setOnClickListener(v -> listener.onFileSelected(NotepadAdapter.this.files.get(position)));

    }

    @Override
    public int getItemCount() {
        return this.files.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class FileViewHolder extends RecyclerView.ViewHolder {

        private final TextView fileName;
        private final TextView fileDate;

        public FileViewHolder(View view){
            super(view); 
            // Define click listener for the ViewHolder's View

            this.fileName = (TextView) view.findViewById(R.id.fileName);
            this.fileDate = (TextView) view.findViewById(R.id.fileModified);
        }

        public void setFileName(String s) {
            this.fileName.setText(s);
        }

        public void setFileDate(String s) {
            this.fileDate.setText(s);
        }
    }
}
