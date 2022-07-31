package GUI;

import App.PasswordManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Allows notes to be updated by getting user input from the UpsertNote GUI class and handling the actionPerformed
 * events.
 */
public class EditNote extends UpsertNote {

    private final int noteID;

    /**
     * Constructor
     * @param noteID the database id of the note
     */
    public EditNote(int noteID) {
        this.noteID = noteID;
        // sets the JFrame title
        setTitle("Edit Note");
        discardNote.setText("Close");
    }

    /**
     * deletes a note
     */
    public void deleteNote() {
        PasswordManager.UpsertStatus status = passwordManager.deleteNote(noteID);
        if (status == PasswordManager.UpsertStatus.FAILED) {
            JOptionPane.showMessageDialog(this,
                    "Could not delete note", "Database deletion error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            updatePasswordManagerNotes();
            dispose();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveNote) {
            PasswordManager.UpsertStatus status = passwordManager.editNote(noteID, titleInput.getText(), contentInput.getText());

            switch (status) {
                case SUCCESS -> {
                    JOptionPane.showMessageDialog(this,
                            "Successfully edited note '" + titleInput.getText() + "'.", "Note updated",
                            JOptionPane.INFORMATION_MESSAGE);
                    updatePasswordManagerNotes();
                }
                case NO_CONTENT -> {
                    int reply = JOptionPane.showConfirmDialog(this,
                            "Nothing to save. Delete note?", "Nothing to save", JOptionPane.YES_NO_OPTION);
                    if (reply == 0) { deleteNote(); }
                }
            }

        } else if (e.getSource() == discardNote) {
            dispose();
        }
    }
}
