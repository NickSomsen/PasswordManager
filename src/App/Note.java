package App;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * stores all information of a note
 */
public class Note {

    private final int id;
    private final String title;
    private final String content;
    private final long createDate;
    private final long lastModDate;
    private final Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
    private final SimpleDateFormat simpleFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");

    /**
     * Constructor
     * @param id the ID of the note in the database
     * @param title the note title
     * @param content the note content
     * @param createDate the note creation date in milliseconds since January 1, 1970, 00:00:00 GMT
     * @param lastModDate the note last modification date in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public Note(int id, String title, String content, long createDate, long lastModDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createDate = createDate;
        this.lastModDate = lastModDate;
    }

    /**
     * @return the note ID
     */
    public int getId() { return id; }

    /**
     * @return the note title
     */
    public String getTitle() {
        return title;
    }

    /**
     * reduces the note title to 30 characters
     * @return the shorter title
     */
    public String reduceTitleLength() {
        // maximum title length is 30 (endIndex is exclusive)
        return title.substring(0, 28) + "...";
    }

    /**
     * @return the note content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return the note creation date in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public long getCreateStamp() {
        return createDate;
    }

    /**
     * @return the note last modification date in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public long getLastModStamp() {
        return lastModDate;
    }

    /**
     * @return the note create date in "dd MMM yyyy HH:mm" format
     */
    public String getCreateDate() {
        return simpleFormat.format(createDate);
    }

    /**
     * @return the note last modification date in "dd MMM yyyy HH:mm" format
     */
    public String getLastModDate() {
        return simpleFormat.format(lastModDate);
    }

    /**
     * highlights the searchTerm in the note content using a Highlighter
     * @param contentArea the note content JTextArea
     * @param searchTerm the searchTerm to highlight
     */
    public void highlightContent(JTextArea contentArea, String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        Highlighter highlighter = contentArea.getHighlighter();
        String noteContent = content.toLowerCase();

        // loop over all searchTerm matches in the note content in order to highlight using a Highlighter
        int index = noteContent.indexOf(searchTerm);
        while (index >= 0) {
            int endIndex = index + searchTerm.length();
            try {
                highlighter.addHighlight(index, endIndex, painter);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            // search again but then 1 character further in order to get all matches in the content string
            index = noteContent.indexOf(searchTerm, index + 1);
        }
    }

    /**
     * highlights the searchTerm in the note title using HTML since you can't use a Highlighter in JLabels
     * @param searchTerm the searchTerm to highlight
     * @return the note title but with HTML for the highlighting
     */
    public String highlightTitle(String searchTerm) {
        String title = this.title;
        if (title.length() > 30) {
            // cut-off the title with "..." and only highlight what is visible on screen
            title = this.reduceTitleLength();
        }
        // text in JLabels cannot be highlighted using a highlighter. Therefore, I use HTML to highlight the searchTerm
        // matches in the title. I do this by replacing all occurrences of the searchTerm with <span bgcolor=''>searchTerm</span>.
        // First, escape all regex chars in the searchTerm to prevent unwanted behaviour (there was an error when typing '(' in
        // the search field because it was seen as a matching group).
        Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
        searchTerm = SPECIAL_REGEX_CHARS.matcher(searchTerm).replaceAll("\\\\$0");
        return "<html>" + title.replaceAll("(?i)("+searchTerm+")", "<span bgcolor='#F7A9A9'>$1</span>") + "</html>";
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createDate='" + createDate + '\'' +
                ", lastModDate='" + lastModDate + '\'' +
                ", painter=" + painter +
                '}';
    }
}
