package Database;

import App.Note;
import App.PasswordManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

/**
 * handles all database events
 */
public class Database {

    private final Path DB_LOCATION = Paths.get(System.getProperty("user.home"), ".passwordmanager");
    private final String DB_NAME = "data.db";
    private final String URL = "jdbc:sqlite:////" + Paths.get(DB_LOCATION.toString(), DB_NAME);
    private JSONObject jsonObject;

    public Database() {
        getQueries();
    }

    /**
     * makes a connection to the SQLite database using the relative url to the database file
     * @return a connection to the database
     * @throws SQLException database error
     */
    public Connection makeConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * closes the SQLite database connection
     * @param connection the connection to the database
     * @throws SQLException database error
     */
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    /**
     * gets all database queries from a json file
     */
    private void getQueries() {
        String queryFile = "/Database/create_db.json";
        try {
            JSONParser jsonParser = new JSONParser();
            // an InputStreamReader is used so that the file can also be read when run from a jar file
            InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(queryFile), StandardCharsets.UTF_8);
            Object obj = jsonParser.parse(reader);
            jsonObject = (JSONObject) obj;
        } catch (ParseException e) {
            System.out.println("Error when parsing '" + queryFile + "'.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not find query file '" + queryFile + "'.");
            e.printStackTrace();
        }
    }

    /**
     * creates the SQLite database if it doesn't exist yet
     */
    public void createDatabase() {
        // check if the database directory exists
        File dir = new File(String.valueOf(DB_LOCATION));
        if (!dir.exists()) dir.mkdir();

        // get the database file path from the url to check if the database exists
        String dbPath = Paths.get(DB_LOCATION.toString(), DB_NAME).toString();
        File f = new File(dbPath);
        try {
            if (f.createNewFile()) {
                // the database file did not exist yet; it has been created. Now, create the "note" table.
                try {
                    String noteTableQuery = (String) jsonObject.get("note_table");
                    Connection connection = makeConnection();
                    PreparedStatement statement = connection.prepareStatement(noteTableQuery);
                    statement.execute();
                    closeConnection(connection);
                } catch (SQLException e) {
                    System.out.println("Error when creating database.");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Couldn't create database file '" + dbPath + "'.");
            e.printStackTrace();
        }
    }

    /**
     * fetches all notes currently in the database
     * @return ArrayList containing all note information in Note objects
     */
    public ArrayList<Note> retrieveNotes() {
        try {
            ArrayList<Note> notes = new ArrayList<>();
            Connection connection = makeConnection();

            String query = "SELECT * FROM note";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                notes.add(new Note(
                        results.getInt("ID"),
                        results.getString("title"),
                        results.getString("content"),
                        results.getLong("create_date"),
                        results.getLong("last_mod_date")));
            }

            closeConnection(connection);
            return notes;
        } catch (SQLException e) {
            System.out.println("Error when fetching notes");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * inserts a new note into the database
     * @param note instance of Note
     * @return the insert status
     */
    public PasswordManager.UpsertStatus insertNewNote(Note note) {
        PasswordManager.UpsertStatus status = PasswordManager.UpsertStatus.SUCCESS;

        try {
            String newNoteQuery = (String) jsonObject.get("new_note");
            Connection connection = makeConnection();
            PreparedStatement statement = connection.prepareStatement(newNoteQuery);
            statement.setString(1, note.getTitle());
            statement.setString(2, note.getContent());
            statement.setLong(3, note.getCreateStamp());
            statement.setLong(4, note.getLastModStamp());
            statement.execute();
            closeConnection(connection);
        } catch (SQLException e) {
            System.out.println("Could not save note");
            e.printStackTrace();
            status = PasswordManager.UpsertStatus.FAILED;
        }

        return status;
    }

    /**
     * updates the title, content and last modification date of a note in the database based on the note ID
     * @param note instance of Note
     * @return the status of the insertion
     */
    public PasswordManager.UpsertStatus updateNote(Note note) {
        PasswordManager.UpsertStatus status = PasswordManager.UpsertStatus.SUCCESS;

        try {
            String updateNoteQuery = (String) jsonObject.get("update_note");
            Connection connection = makeConnection();
            PreparedStatement statement = connection.prepareStatement(updateNoteQuery);
            statement.setString(1, note.getTitle());
            statement.setString(2, note.getContent());
            statement.setLong(3, note.getLastModStamp());
            statement.setInt(4, note.getId());
            statement.execute();
            closeConnection(connection);
        } catch (SQLException e) {
            System.out.println("Could not edit note");
            e.printStackTrace();
            status = PasswordManager.UpsertStatus.FAILED;
        }

        return status;
    }

    /**
     * deletes a note from the database based on the note ID
     * @param noteID the ID of the note in the database
     * @return the status of the deletion
     */
    public PasswordManager.UpsertStatus deleteNote(int noteID) {
        PasswordManager.UpsertStatus status = PasswordManager.UpsertStatus.SUCCESS;

        try {
            String updateNoteQuery = (String) jsonObject.get("delete_note");
            Connection connection = makeConnection();
            PreparedStatement statement = connection.prepareStatement(updateNoteQuery);
            statement.setInt(1, noteID);
            statement.execute();
            closeConnection(connection);
        } catch (SQLException e) {
            System.out.println("Could not delete note");
            e.printStackTrace();
            status = PasswordManager.UpsertStatus.FAILED;
        }

        return status;
    }
}