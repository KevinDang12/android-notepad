package com.example.basicnotepad;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Ask user to type in password
 */
public class PasswordFragment extends Fragment {

    /** Listener for handling the password entered */
    private final OnPasswordEntered main;

    /** EditText area for the user to enter their password */
    private EditText passwordEditText;

    /**
     * Ask user to type in password
     * @param onPasswordEntered listener to notify when user enters password
     */
    public PasswordFragment(OnPasswordEntered onPasswordEntered) {
        super(R.layout.password_login);
        this.main = onPasswordEntered;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.password_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.passwordEditText = view.findViewById(R.id.password);
        final Button loginButton = view.findViewById(R.id.login);

        loginButton.setOnClickListener(v -> main.passwordEntered(passwordEditText.getText().toString()));

        this.showSoftKeyboard(passwordEditText);


        // this.passwordEditText.setOnEditorActionListener(listener); (assigns a listener to EditText, and invokes the method in listener)
        // Method in listener (onEditorAction); makes a call to main activity to passwordEntered when the button is pressed.

        TextView.OnEditorActionListener listener = (v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                main.passwordEntered(passwordEditText.getText().toString());
                Log.d("Debug", "Signed in");
            }
            return false;
        };

        this.passwordEditText.setOnEditorActionListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.passwordEditText.setText("");
    }

    /**
     * Clears the password text if the user password is incorrect.
     */
    public void clearPassword() {
        this.passwordEditText.setText("");
    }

    /**
     * Opens keyboard focus on the view.
     * @param view View that the keyboard opens for.
     */
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
}