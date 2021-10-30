package com.example.basicnotepad;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment containing main content.
 */
public class ContentMainFragment extends Fragment {

    /** Saved notes written by the user */
    private String data;

    /** Area for the user to write their notes */
    private NotePadArea contentEditText;

    /**
     * Create a notepad area fragment for the user.
     */
    public ContentMainFragment() {}

    /**
     * Create a notepad area fragment for the user.
     * @param data note that is returned to the user.
     */
    public ContentMainFragment(String data) {
       super(R.layout.content_main);
       this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.contentEditText = view.findViewById(R.id.notepad_input);
        this.contentEditText.setText(data);
        this.contentEditText.fillScreen();

        NotePadTornPage contentImageView = view.findViewById(R.id.notepad_torn);
        contentImageView.setLineHeight(this.contentEditText.getLineHeight());

        ViewTreeObserver observer  = this.contentEditText.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(() -> ContentMainFragment.this.contentEditText.fillScreen());

        // Switch statement checking the value of the action event, if action is equal to ACTION_UP, show the soft
        // keyboard on screen else don't show the keyboard break
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
                showSoftKeyboard(contentEditText);
            }
            return true;
        });

        this.showSoftKeyboard(contentEditText);
    }

    /**
     * Opens keyboard focus on the view.
     * @param view View that the keyboard opens for.
     */
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Return the text that the user typed in.
     * @return text from contentEditText.
     */
    public String getNotes() {
        return this.contentEditText.getText().toString();
    }
}