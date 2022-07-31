package GUI;

import App.PasswordManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/***
 * abstract class that creates the GUI for updating and inserting (UpSert) notes. Extended by NewNote and EditNote.
 */
public abstract class UpsertNote extends JFrame implements ActionListener {

    public PasswordManager passwordManager;
    public PasswordManagerGUI passwordManagerGUI;
    public JTextField titleInput;
    public JTextArea contentInput;
    public JButton saveNote;
    public JButton discardNote;

    /**
     * Constructor
     */
    public UpsertNote() {
        setPreferredSize(new Dimension(470, 500));
        createGUI();
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * @param passwordManager instance of PasswordManager as bridge between the GUI and the database
     */
    public void setPasswordManager(PasswordManager passwordManager) {
        this.passwordManager = passwordManager;
    }

    /**
     * @param passwordManagerGUI instance of PasswordManagerGUI to update the GUI after changes have been made
     */
    public void setPasswordManagerGUI(PasswordManagerGUI passwordManagerGUI) {
        this.passwordManagerGUI = passwordManagerGUI;
    }

    /**
     * creates the Upsert GUI
     */
    public void createGUI() {
        // a BorderLayout is used as wrapper container. This is later placed in the frame using BorderLayout.NORTH. This
        // means that the BorderLayout will only take up the vertical space it needs and no more (otherwise the title
        // input would be expanded).
        JPanel wrapperContainer = new JPanel(new BorderLayout());

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        wrapperContainer.add(mainContainer);

        // title input
        JLabel titleLabel = new JLabel("Title");
        mainContainer.add(titleLabel);
        titleInput = new JTextField();
        titleInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContainer.add(titleInput);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        // content input with scrollbar
        JLabel contentLabel = new JLabel("Content");
        mainContainer.add(contentLabel);
        // the width is configured by the width of the window because of the components in a boxlayout expanding
        contentInput = new JTextArea(21, 0);
        contentInput.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(contentInput);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContainer.add(scrollPane);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        // discard and save buttons
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveNote = new JButton("Save");
        saveNote.addActionListener(this);
        discardNote = new JButton("Discard");
        discardNote.addActionListener(this);
        buttonPane.add(discardNote);
        buttonPane.add(saveNote);
        buttonPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContainer.add(buttonPane);

        // place with BorderLayout.NORTH so that mainContainer only takes up the vertical space it needs
        add(wrapperContainer, BorderLayout.NORTH);
    }

    /**
     * sets text in the title input field
     */
    public void setNoteTitle(String title) { titleInput.setText(title); }

    /**
     * sets text in the content input field
     */
    public void setNoteContent (String content) { contentInput.setText(content); }

    /**
     * re-retrieves all notes from the database and adds them to the GUI
     */
    public void updatePasswordManagerNotes() {
        // the new note that has been added is first retrieved from the database, and then added to the main
        // GUI
        passwordManager.retrieveNotes();
        passwordManagerGUI.addNotesToPanel(passwordManager.getNotes(""));
    }

    @Override
    public abstract void actionPerformed(ActionEvent e);
}
