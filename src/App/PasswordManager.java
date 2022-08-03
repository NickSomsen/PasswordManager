package App;

import Database.Database;
import GUI.EditNote;
import GUI.PasswordManagerGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * The main class of the PasswordManager app. Gets input from the PasswordManagerGUI class and makes database changes using
 * the Database class.
 */
public class PasswordManager {

    private final PasswordManagerGUI passwordManagerGUI;
    private final Database db = new Database();
    private ArrayList<Note> notes;

    /**
     * determines the searchTerm matches in the note title and content
     */
    enum MatchType {
        NONE,
        CONTENT,
        TITLE,
        BOTH
    }

    /**
     * determines the insert/update statuses
     */
    public enum UpsertStatus {
        SUCCESS,
        FAILED,
        NO_CONTENT
    }

    /**
     * Constructor
     * @param passwordManagerGUI instance of PasswordManagerGUI
     */
    public PasswordManager(PasswordManagerGUI passwordManagerGUI) {
        this.passwordManagerGUI = passwordManagerGUI;
        createDatabase();
        retrieveNotes();
    }

    /**
     * creates the database if it doesn't exist already
     */
    public void createDatabase() { db.createDatabase(); }

    /**
     * retrieves all notes from the database
     */
    public void retrieveNotes() {
        notes = db.retrieveNotes();
        if (notes != null) {
            // sort the notes so that the last edited notes are listed first (with the highest LastModStamp)
            notes.sort(Comparator.comparing(Note::getLastModStamp).reversed());
        }
    }

    /**
     * gets notes that contain the searchTerm in the note title or content
     * @param searchTerm the search term
     * @return ArrayList containing all note panes
     */
    public ArrayList<JPanel> getNotes(String searchTerm) {
        searchTerm = searchTerm.strip();
        ArrayList<JPanel> notePanes = new ArrayList<>();

        if (notes != null) {
            // the notes were successfully retrieved from the database
            if (searchTerm.equals("")) {
                // no search term input; return all notes in JPanels to the GUI
                for (Note note : notes) {
                    notePanes.add(createNotePane(note, searchTerm, MatchType.NONE));
                }
            } else {
                // search term input was given; only return the notes that contain the search term in the title or content
                for (Note note : notes) {
                    if (Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE).matcher(note.getContent()).find() && Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE).matcher(note.getTitle()).find()) {
                        // title and content contain searchTerm
                        notePanes.add(createNotePane(note, searchTerm, MatchType.BOTH));
                    } else if (Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE).matcher(note.getContent()).find()) {
                        // only content contains the searchTerm
                        notePanes.add(createNotePane(note, searchTerm, MatchType.CONTENT));
                    } else if (Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE).matcher(note.getTitle()).find()) {
                        // only title contains the searchTerm
                        notePanes.add(createNotePane(note, searchTerm, MatchType.TITLE));
                    }
                }
            }
            return notePanes;
        } else {
            // the notes could not be retrieved because of a database error
            return null;
        }
    }

    /**
     * creates a JPanel containing a JTextArea containing the note content, a JLabel containing the note title, and
     * another JLabel containing the note last edit date. The title label will be given an onclick event that allows
     * the note to be edited.
     * @param note instance of Note containing all note data
     * @param searchTerm the search term
     * @param matchType decides if highlighting should be done in note title or content, or both or none.
     * @return the note pane
     */
    public JPanel createNotePane(Note note, String searchTerm, MatchType matchType) {
        JPanel notePanel = new JPanel();
        notePanel.setLayout(new BoxLayout(notePanel, BoxLayout.Y_AXIS));

        JTextArea contentArea = new JTextArea(13, 18);
        contentArea.setText(note.getContent());
        contentArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel noteTitle = new JLabel();
        String title = note.getTitle();
        if (title.equals("")) {
            // if the note was saved without a title, give it a default title containing the creation date
            noteTitle.setText("Note " + note.getCreateDate().substring(0, note.getCreateDate().lastIndexOf(" ")));
        } else if (title.length() <= 30) {
            // the entire title fits
            noteTitle.setText(title);
        } else {
            // title is too long, cut-off the title with "..."
            noteTitle.setText(note.reduceTitleLength());
        }
        noteTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        // to ensure the title is centered horizontally after highlighting in the title is done
        noteTitle.setHorizontalAlignment(SwingConstants.CENTER);
        noteTitle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        noteTitle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // assign an on-click method to the note title that opens a window that allows the note to be edited
                EditNote editNote = new EditNote(note.getId());
                editNote.setNoteTitle(note.getTitle());
                editNote.setNoteContent(note.getContent());
                editNote.setPasswordManager(PasswordManager.this);
                editNote.setPasswordManagerGUI(passwordManagerGUI);
            }
        });

        JLabel noteLastModDate = new JLabel(note.getLastModDate());
        noteLastModDate.setAlignmentX(Component.CENTER_ALIGNMENT);

        // do the searchTerm highlighting in the note content, title, both or neither
        switch (matchType) {
            case CONTENT -> note.highlightContent(contentArea, searchTerm);
            case TITLE -> noteTitle.setText(note.highlightTitle(searchTerm));
            case BOTH -> {
                note.highlightContent(contentArea, searchTerm);
                noteTitle.setText(note.highlightTitle(searchTerm));
            }
            case NONE -> {}
        }

        notePanel.add(scrollPane);
        notePanel.add(noteTitle);
        notePanel.add(noteLastModDate);
        return notePanel;
    }

    /**
     * creates a new Note that will then be inserted into the database
     * @param title the note title
     * @param content the note content
     * @return the status of the note creation
     */
    public UpsertStatus createNewNote(String title, String content) {
        content = content.strip();
        if (content.equals("")) {
            return UpsertStatus.NO_CONTENT;
        }

        long date = getCurrentDate();
        return db.insertNewNote(new Note(-1, title.strip(), content, date, date));
    }

    /**
     * gets the current datetime in number of milliseconds since the standard base time known as "the epoch",
     * namely January 1, 1970, 00:00:00 GMT.
     * @return the datetime stamp
     */
    public long getCurrentDate() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * allows a note to get edited. If the content is empty, prompt the user if they want to delete the note in
     * EditNote
     * @param noteID the note ID in the database
     * @param title the note title
     * @param content the note content
     * @return the edit status
     */
    public UpsertStatus editNote(int noteID, String title, String content) {
        return db.updateNote(new Note(noteID, title.strip(), content.strip(), -1, getCurrentDate()));
    }

    /**
     * allows a note to be deleted from the database based on the ID of the note in the database
     * @param noteID the note ID
     * @return the status of the deletion (either SUCCESS or FAILED)
     */
    public UpsertStatus deleteNote(int noteID) {
        return db.deleteNote(noteID);
    }
}
