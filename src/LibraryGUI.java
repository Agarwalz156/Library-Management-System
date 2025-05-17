package src;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LibraryGUI extends JFrame {
    private JTextField searchField;
    private JButton searchButton;
    private JTable booksTable;
    private DefaultTableModel booksTableModel;

    private JButton issueButton;
    private JButton returnButton;
    private JLabel statusLabel;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private Student currentUser;

    public LibraryGUI() {
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initLoginPanel();
    }

    private void initLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");

        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(loginButton, gbc);

        add(loginPanel);

        loginButton.addActionListener(e -> authenticateUser());
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals("admin") && password.equals("adminpass")) {
            // Open Admin Portal
            AdminGUI adminGUI = new AdminGUI();
            adminGUI.setVisible(true);
            dispose();
        } else {
            Student student = StudentManager.authenticate(username, password);
            if (student != null) {
                // Open Student Portal
                currentUser = student;
                getContentPane().removeAll();
                initMainPanel();
                revalidate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void initMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        searchField = new JTextField(30);
        searchButton = new JButton("Search Books");
        topPanel.add(searchField);
        topPanel.add(searchButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        booksTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "ISBN", "Available Copies"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(booksTableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        issueButton = new JButton("Issue Book");
        returnButton = new JButton("Return Book");
        statusLabel = new JLabel("Welcome, Guest");

        bottomPanel.add(issueButton);
        bottomPanel.add(returnButton);
        bottomPanel.add(statusLabel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        searchButton.addActionListener(e -> searchBooks());
        issueButton.addActionListener(e -> issueSelectedBook());
        returnButton.addActionListener(e -> returnSelectedBook());
    }

    private void searchBooks() {
        String keyword = searchField.getText();
        List<Book> books = BookManager.searchBooks(keyword);
        System.out.println("Search results for keyword '" + keyword + "': " + books); // Debug statement
        booksTableModel.setRowCount(0);
        for (Book book : books) {
            booksTableModel.addRow(new Object[]{
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getAvailableCopies()
            });
        }
        statusLabel.setText(books.size() + " book(s) found.");
    }

    private void issueSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to issue.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookId = (int) booksTableModel.getValueAt(selectedRow, 0);
        boolean success = BookManager.issueBook(currentUser.getId(), bookId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Book issued successfully.");
            searchBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to issue book. It may not be available.", "Issue Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to return.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookId = (int) booksTableModel.getValueAt(selectedRow, 0);
        boolean success = BookManager.returnBook(currentUser.getId(), bookId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Book returned successfully.");
            searchBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to return book. Please check if you have issued this book.", "Return Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });
    }
}
