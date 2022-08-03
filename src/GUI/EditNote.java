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
        discardNote.setText("Delete");
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
                case FAILED -> JOptionPane.showMessageDialog(this,
                        "Could not edit note", "Database update error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == discardNote) {
            int reply = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this note?", "Delete note", JOptionPane.YES_NO_OPTION);
            if (reply == 0) { deleteNote(); }
        }
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
}
