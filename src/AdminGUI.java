package src;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminGUI extends JFrame {

    private JTable studentsTable;
    private JTable booksTable;
    private DefaultTableModel studentsTableModel;
    private DefaultTableModel booksTableModel;

    public AdminGUI() {
        setTitle("Admin Portal");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initAdminPanel();
    }

    private void initAdminPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top Panel for Admin Actions
        JPanel topPanel = new JPanel();
        JButton addBookButton = new JButton("Add Book");
        JButton viewIssuedBooksButton = new JButton("View Issued Books");
        topPanel.add(addBookButton);
        topPanel.add(viewIssuedBooksButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel for Tables
        JTabbedPane tabbedPane = new JTabbedPane();

        // Students Tab
        studentsTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Username"}, 0);
        studentsTable = new JTable(studentsTableModel);
        JScrollPane studentsScrollPane = new JScrollPane(studentsTable);
        tabbedPane.addTab("Students", studentsScrollPane);

        // Books Tab
        booksTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Department", "Available Copies"}, 0);
        booksTable = new JTable(booksTableModel);
        JScrollPane booksScrollPane = new JScrollPane(booksTable);
        tabbedPane.addTab("Books", booksScrollPane);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);

        // Action Listeners
        addBookButton.addActionListener(e -> addBook());
        viewIssuedBooksButton.addActionListener(e -> viewIssuedBooks());

        loadStudents();
        loadBooks();
    }

    private void loadStudents() {
        // Fetch students from the database and populate the table
        List<Student> students = StudentManager.getAllStudents();
        System.out.println("Fetched students: " + students); // Debug statement
        studentsTableModel.setRowCount(0);
        for (Student student : students) {
            studentsTableModel.addRow(new Object[]{student.getId(), student.getFullName(), student.getUsername()});
        }
    }

    private void loadBooks() {
        // Fetch books from the database and populate the table
        List<Book> books = BookManager.getAllBooks();
        System.out.println("Fetched books: " + books); // Debug statement
        booksTableModel.setRowCount(0);
        for (Book book : books) {
            booksTableModel.addRow(new Object[]{book.getBookId(), book.getTitle(), book.getAuthor(), book.getDepartment(), book.getAvailableCopies()});
        }
    }

    private void addBook() {
        // Logic to add a new book (e.g., open a dialog to input book details)
        JOptionPane.showMessageDialog(this, "Add Book functionality not implemented yet.");
    }

    private void viewIssuedBooks() {
        // Logic to view issued books (e.g., open a new window or dialog)
        JOptionPane.showMessageDialog(this, "View Issued Books functionality not implemented yet.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminGUI adminGUI = new AdminGUI();
            adminGUI.setVisible(true);
        });
    }
}