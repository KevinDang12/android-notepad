package com.example.basicnotepad;

/**
 * Handles password acceptance.
 */
public interface OnPasswordEntered {

    /** 
     * Handles password acceptances.
     * @param password password that the user entered.
     */
    void passwordEntered(String password);
}
