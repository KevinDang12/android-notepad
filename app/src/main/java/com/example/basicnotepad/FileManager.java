package com.example.basicnotepad;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A convenient class to open, read, and write files.
 */
public class FileManager {

    /** Context */
    private final Context context;

    /**
     * Creates a new FileManager instance.
     * @param context The context storing information about the app environment.
     */
    public FileManager(Context context) {
        this.context = context;
    }

    /**
     * Writes data to specified file.
     * @param directory Location of the file.
     * @param fileName Name of the file to save data to.
     * @param data Information to be saved.
     * @return If successful, returns true. Else returns false
     */
    public boolean writeToFile(String directory, String fileName, byte[] data) {

        // for example
        String fullPathToFile = directory + "/" + fileName;

        File dataFolder = new File(context.getExternalFilesDir(null), directory);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        try {
            // FileOutputStream allows you to write to file
            // context.openFileOutput(fullPathToFile, Context.MODE_PRIVATE); creates

            // FileOutputStream whose root directory is in the "special folder" for the app (so basically internal storage)
            // "Internal storage" is like documents folder - where your save data is saved
            // "external storage" is like e drive - it is the sd card that saves photos, videos, etc.

            // saveFile is a file inside of the internal storage
            // context.getFilesDir() is the internal storage
            File saveFile = new File(context.getExternalFilesDir(null), fullPathToFile);
            FileOutputStream fos = new FileOutputStream(saveFile, false);
            fos.write(data);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    /**
     * Reads data from specified file.
     * @param directory Location of the file.
     * @param fileName Name of the file to save data to.
     * @return contents of file. If there is an error return null.
     */
    public byte[] readFromFile(String directory, String fileName) {

        String fullPathToFile = directory + "/" + fileName;

        File dataFolder = new File(context.getExternalFilesDir(null), directory);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        try {

            // saveFile is a file inside of the internal storage
            // context.getFilesDir() is the internal storage
            File saveFile = new File(context.getExternalFilesDir(null), fullPathToFile);

            FileInputStream inputStream = new FileInputStream(saveFile);

            int bytesRead;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            while ((bytesRead = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, bytesRead);
            }
            outputStream.close();
            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the list of files from the user's device
     * @param directory Location of the files
     * @return The list of files, null if no files exist
     */
    public ArrayList<File> getFileList(String directory) {

        File dataFolder = new File(context.getExternalFilesDir(null), directory);
        if (!dataFolder.exists()) {
            return null;
        }

        File[] files = dataFolder.listFiles();
        return new ArrayList<>(Arrays.asList(files));
    }

    /**
     * Check if file exist within the user's device
     * @param directory Location of the files
     * @param fileName Name of the file
     * @return True if the file exists in the user's device, else false
     */
    public boolean fileExists(String directory, String fileName) {

        File nameOfFile = new File(context.getExternalFilesDir(null), directory + "/" + fileName);
        return nameOfFile.exists();

    }

}