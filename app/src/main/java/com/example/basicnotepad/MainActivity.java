package com.example.basicnotepad;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Create the layout of the Notepad application and menu bar
 */
public class MainActivity extends AppCompatActivity implements OnPasswordEntered, NotepadFileSelectedListener {

    /** Manages the files for the notepad */
    private FileManager fileManager;

    /** File to containing the notepad */
    private File file;

    /** The password the user entered */
    private String password;

    /** Fragment containing the notepad text and UI */
    private ContentMainFragment contentMainFragment;

    /** Name of the selected file */
    private String filename;

    /** Interface for menu items */
    private Menu menu;

    /** Fragment asking the user for the password */
    private PasswordFragment passwordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.fileManager = new FileManager(this);


         Log.d("debug",  "starting");
        ArrayList<File> files = this.fileManager.getFileList("data");
        if (files == null || files.size() == 0) {
            Log.d("debug",  "Fragment opens");
            this.passwordFragment = new PasswordFragment(this);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, this.passwordFragment, null)
                    .commit();

        } else {

            Log.d("debug",  "Content is opening");
            OpenFileFragment openFileFragment = new OpenFileFragment(this, files);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, openFileFragment, null)
                    .commit();
        }

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                MenuItem openFile = MainActivity.this.menu.findItem(R.id.open_file_button);
                openFile.setVisible(false);

                MenuItem saveFile = MainActivity.this.menu.findItem(R.id.save_button);
                saveFile.setVisible(false);

                MenuItem createFile = MainActivity.this.menu.findItem(R.id.create_new_note);
                createFile.setVisible(true);

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
                if (fragment instanceof PasswordFragment) {
                    ArrayList<File> files = MainActivity.this.fileManager.getFileList("data");
                    OpenFileFragment openFileFragment = new OpenFileFragment(MainActivity.this, files);

                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragment_container_view, openFileFragment, null)
                            .commit();
                }

                getSupportFragmentManager().popBackStack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onPause() {
        super.onPause();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = this.getCurrentFocus();

        if (view == null) {
            view = new View(this);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_button) {
            this.saveData();
        }

        if (id == R.id.open_file_button) {
            this.openFile();
        }

        if (id == R.id.create_new_note) {
            Log.d("debug", "creation");
            this.createNewFile();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves and open the list of files
     */
    public void openFile() {
        saveData();

        ArrayList<File> files = this.fileManager.getFileList("data");
        OpenFileFragment openFileFragment = new OpenFileFragment(this, files);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, openFileFragment, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();

        MenuItem createNewNote = this.menu.findItem(R.id.create_new_note);
        createNewNote.setVisible(true);

        MenuItem openFile = this.menu.findItem(R.id.open_file_button);
        openFile.setVisible(false);

        MenuItem saveFile = this.menu.findItem(R.id.save_button);
        saveFile.setVisible(false);
    }

    /**
     * Saves and creates a new file.
     */
    public void createNewFile() {
        saveData();
        this.file = null;

        this.passwordFragment  = new PasswordFragment(this);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, this.passwordFragment, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();

        MenuItem createNewNote = this.menu.findItem(R.id.create_new_note);
        createNewNote.setVisible(false);

        MenuItem openFile = this.menu.findItem(R.id.open_file_button);
        openFile.setVisible(false);

        MenuItem saveFile = this.menu.findItem(R.id.save_button);
        saveFile.setVisible(false);
    }

    /**
     * Saves data to internal storage.
     * @return True if data is saved on device, else returns false
     */
    private boolean saveData() {

        if (this.contentMainFragment == null) {
            Log.d("debug", "null");
            return false;
        } else {
            byte[] encrypt;
            String text = this.contentMainFragment.getNotes();
            try {
                encrypt = CipherHelper.encrypt(text.getBytes(), this.password);
                Log.d("debug", "SHould be encrypted");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            boolean written = this.fileManager.writeToFile("data", this.filename, encrypt);
            Log.d("debug", "file is written");
            return written;
        }
    }

    /**
     * Loads data from internal storage.
     * @param password The password to use to decrypt.
     * @return string input of data stored. If there is an error return null.
     */
    private String obtainData(String password) {
        Log.d("debug", this.file.getName());
        // storage/emulated/0/Android/data/com.example.basicnotepad/files/data/input_save
        byte[] encrypt = this.fileManager.readFromFile("data", this.file.getName());

        String decrypt;
        if (encrypt == null || Arrays.equals(encrypt, "".getBytes())) {
            return "Enter your notes here.";
        }
        try {
            decrypt = new String(CipherHelper.decrypt(encrypt, password));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return decrypt;
    }

    /**
     * Override method to open and display file to content main fragment, creates new file if it does not exist on the device
     * Gets called in PasswordFragment to return password when button is clicked
     * @param password password that the user entered.
     */
    @Override
    public void passwordEntered(String password) {

        this.password = password;
        if (this.file == null) {
            askForFileName("Creating new file needs new name:");

            return;
        }

        String data = this.obtainData(this.password);

        if (this.password.equals("") || data == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Password is incorrect. Enter a new password.");

            builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

            // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code>
            // from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            AlertDialog dialog = builder.create();
            dialog.show();

            this.passwordFragment.clearPassword();

        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();

            this.contentMainFragment = new ContentMainFragment(data);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, this.contentMainFragment, null)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();

            MenuItem createNewNote = this.menu.findItem(R.id.create_new_note);
            createNewNote.setVisible(true);

            MenuItem openFile = this.menu.findItem(R.id.open_file_button);
            openFile.setVisible(true);

            MenuItem saveFile = this.menu.findItem(R.id.save_button);
            saveFile.setVisible(true);
        }
    }

    /**
     * Opens dialog box to ask for file name
     * @param message Message to display to the user
     */
    private void askForFileName(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle("Choose file name");


        EditText nameOfFile = new EditText(this);
                builder.setView(nameOfFile);

        // Add the buttons
        builder.setPositiveButton("Confirm", (dialog, id) -> {

            dialog.dismiss();
            // Obtain the string from the editText
            // and send it to the onFileNameChosen()
            String name = nameOfFile.getText().toString();
            onFileNameChosen(name);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Creates new filename using the selected filename
     * @param fileName Name of the file
     */
    private void onFileNameChosen(String fileName) {
        // Checks if filename is empty and show dialog if it is with message
        if (fileName == null || fileName.equals("")) {
            askForFileName("File name is empty. Enter a name for the file");
        }
        // checks if filename already exists
        else if (this.fileManager.fileExists("data", fileName)) {
            askForFileName("File name already exist. Enter another name for the file");
        }
        //
        else {
            this.filename = fileName;
            byte[] data = "Enter your notes here!".getBytes();
            this.fileManager.writeToFile("data", fileName, data);

            FragmentManager fragmentManager = getSupportFragmentManager();

            String content = new String(data, StandardCharsets.UTF_8);

            this.contentMainFragment = new ContentMainFragment(content);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, this.contentMainFragment, null)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();

            MenuItem createNewNote = this.menu.findItem(R.id.create_new_note);
            createNewNote.setVisible(true);

            MenuItem openFile = this.menu.findItem(R.id.open_file_button);
            openFile.setVisible(true);

            MenuItem saveFile = this.menu.findItem(R.id.save_button);
            saveFile.setVisible(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.saveData();
    }

    @Override
    public void onFileSelected(File file) {

        this.file = file;
        this.filename = this.file.getName();

        this.passwordFragment = new PasswordFragment(this);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, this.passwordFragment, null)
                .addToBackStack(null)
                .commit();
    }
}