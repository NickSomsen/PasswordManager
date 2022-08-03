package GUI;

import App.PasswordManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Allows new notes to be created by getting user input from the UpsertNote GUI class and handling the actionPerformed
 * events.
 */
public class NewNote extends UpsertNote {

    /**
     * Constructor
     */
    public NewNote() {
        // sets the JFrame title
        setTitle("New Note");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveNote) {
            PasswordManager.UpsertStatus status = passwordManager.createNewNote(titleInput.getText(), contentInput.getText());
            switch (status) {
                case SUCCESS -> {
                    JOptionPane.showMessageDialog(this,
                    "Successfully created note '" + titleInput.getText() + "'.", "Note created",
                    JOptionPane.INFORMATION_MESSAGE);
                    saveNote.setVisible(false);
                    discardNote.setText("Close");
                    updatePasswordManagerNotes();
                }
                case NO_CONTENT -> JOptionPane.showMessageDialog(this,
                        "Nothing to save!", "No note content",
                        JOptionPane.INFORMATION_MESSAGE);
                case FAILED -> JOptionPane.showMessageDialog(this,
                        "Could not create note", "Database insert error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == discardNote) {
            dispose();
        }
    }
}
