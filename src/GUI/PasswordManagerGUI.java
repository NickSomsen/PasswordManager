package GUI;

import App.PasswordManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The main GUI showing all notes in a WrapLayout. New notes can be added and existing notes can be added by button/label
 * clicks with the logic of PasswordManager.
 */
public class PasswordManagerGUI extends JFrame implements ActionListener {

    private final PasswordManager passwordManager;
    private JTextField searchInput;
    private JButton addNote;
    private JPanel resultPanel;
    private final JLabel appLabel = new JLabel("Password and notes manager");

    public static void main(String[] args) {
        new PasswordManagerGUI();
    }

    /**
     * Constructor
     */
    PasswordManagerGUI() {
        passwordManager = new PasswordManager(this);
        setPreferredSize(new Dimension(470, 500));
        setTitle("PasswordManager");
        createGUI();
        // retrieve the notes from the database and add them to the resultsPanel
        addNotesToPanel(passwordManager.getNotes(""));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * creates the GUI and adds an actionListener to the search field
     */
    public void createGUI() {
        // the main container is a BoxLayout along the y-axis. Note that everything added to the BoxLayout needs their
        // AlignmentX set to LEFT_ALIGNMENT for the alignment to the left to work
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainContainer);

        appLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        appLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContainer.add(appLabel);

        // input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setPreferredSize(new Dimension(getPreferredSize().width, 50));
        inputPanel.setMaximumSize(inputPanel.getPreferredSize());
        JLabel inputLabel = new JLabel("Search:");
        searchInput = new JTextField();
        searchInput.setPreferredSize(new Dimension(160, 20));
        // add a DocumentListener so that search input gets constantly updated while typing
        searchInput.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                addNotesToPanel(passwordManager.getNotes(searchInput.getText()));
            }
            public void removeUpdate(DocumentEvent e) {
                addNotesToPanel(passwordManager.getNotes(searchInput.getText()));
            }
            public void insertUpdate(DocumentEvent e) {
                addNotesToPanel(passwordManager.getNotes(searchInput.getText()));
            }
        });
        addNote = new JButton("Add note");
        addNote.addActionListener(this);
        inputPanel.add(inputLabel);
        inputPanel.add(searchInput);
        JLabel filler = new JLabel();
        filler.setPreferredSize(new Dimension(127, 0));
        inputPanel.add(filler);
        inputPanel.add(addNote);
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContainer.add(inputPanel);

        // results panel with custom WrapLayout extended from FlowLayout
        resultPanel = new JPanel(new WrapLayout(FlowLayout.CENTER, 10, 10));
        JScrollPane resultScrollPane = new JScrollPane(resultPanel);
        resultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContainer.add(resultScrollPane);
    }

    /**
     * adds note panels to the resultPanel
     * @param notePanes ArrayList of all note panels
     */
    public void addNotesToPanel(ArrayList<JPanel> notePanes) {
        resultPanel.removeAll();

        if (notePanes != null) {
            for (JPanel notePane : notePanes) {
                resultPanel.add(notePane);
            }
            appLabel.setText("Password and notes manager (" + notePanes.size() + " notes)");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not retrieve notes from the database.", "Database retrieval error",
                    JOptionPane.ERROR_MESSAGE);
            appLabel.setText("Password and notes manager");
        }

        // since components were added and removed, validate() and repaint() the frame to update the GUI
        validate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addNote) {
            NewNote newNote = new NewNote();
            newNote.setPasswordManagerGUI(this);
            newNote.setPasswordManager(passwordManager);
        }
    }
}
