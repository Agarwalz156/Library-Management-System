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
        System.out.println("Fetched books: " + books.size()); // Debug: print number of books fetched
        booksTableModel.setRowCount(0);
        for (Book book : books) {
            System.out.println("Book: " + book.getBookId() + ", " + book.getTitle() + ", " + book.getAuthor() + ", " + book.getDepartment() + ", " + book.getAvailableCopies()); // Debug: print book details
            booksTableModel.addRow(new Object[]{book.getBookId(), book.getTitle(), book.getAuthor(), book.getDepartment(), book.getAvailableCopies()});
        }
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No books found in the database.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addBook() {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField totalCopiesField = new JTextField();
        JComboBox<String> departmentBox = new JComboBox<>();

        // Fetch departments from database
        List<String> departments = getDepartmentsFromDatabase();
        for (String dept : departments) {
            departmentBox.addItem(dept);
        }

        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(new JLabel("Total Copies:"));
        panel.add(totalCopiesField);
        panel.add(new JLabel("Department:"));
        panel.add(departmentBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            int totalCopies;
            try {
                totalCopies = Integer.parseInt(totalCopiesField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Total Copies must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String department = (String) departmentBox.getSelectedItem();
            int departmentId = getDepartmentIdByName(department);
            boolean success = BookManager.addBook(title, author, isbn, totalCopies, departmentId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Book added successfully.");
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add book.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private List<String> getDepartmentsFromDatabase() {
        List<String> departments = new java.util.ArrayList<>();
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT name FROM departments");
             java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departments.add(rs.getString("name"));
            }
        } catch (Exception e) {
            // fallback: add a default
            departments.add("Unknown");
        }
        return departments;
    }

    private int getDepartmentIdByName(String name) {
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT department_id FROM departments WHERE name = ?")) {
            stmt.setString(1, name);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("department_id");
                }
            }
        } catch (Exception e) {
            // fallback
        }
        return 1; // default to 1 if not found
    }

    private void viewIssuedBooks() {
        // Show all issued books in a dialog
        JDialog dialog = new JDialog(this, "Issued Books", true);
        String[] columns = {"Issue ID", "Student Name", "Book Title", "Issue Date", "Return Date"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        List<IssuedBookInfo> issuedBooks = getIssuedBooksFromDatabase();
        for (IssuedBookInfo info : issuedBooks) {
            model.addRow(new Object[]{info.issueId, info.studentName, info.bookTitle, info.issueDate, info.returnDate});
        }
        dialog.add(new JScrollPane(table));
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private List<IssuedBookInfo> getIssuedBooksFromDatabase() {
        List<IssuedBookInfo> list = new java.util.ArrayList<>();
        String sql = "SELECT ib.issue_id, s.full_name, b.title, ib.issue_date, ib.return_date " +
                "FROM issued_books ib " +
                "JOIN students s ON ib.student_id = s.student_id " +
                "JOIN books b ON ib.book_id = b.book_id";
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new IssuedBookInfo(
                        rs.getInt("issue_id"),
                        rs.getString("full_name"),
                        rs.getString("title"),
                        rs.getDate("issue_date"),
                        rs.getDate("return_date")
                ));
            }
        } catch (Exception e) {
            // handle error
        }
        return list;
    }

    private static class IssuedBookInfo {
        int issueId;
        String studentName;
        String bookTitle;
        java.sql.Date issueDate;
        java.sql.Date returnDate;
        IssuedBookInfo(int issueId, String studentName, String bookTitle, java.sql.Date issueDate, java.sql.Date returnDate) {
            this.issueId = issueId;
            this.studentName = studentName;
            this.bookTitle = bookTitle;
            this.issueDate = issueDate;
            this.returnDate = returnDate;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminGUI adminGUI = new AdminGUI();
            adminGUI.setVisible(true);
        });
    }
}