package src;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookManager {

    public static List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            stmt.setString(3, likeKeyword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static boolean issueBook(int userId, int bookId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Check available copies
            String checkSql = "SELECT available_copies FROM books WHERE book_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int available = rs.getInt("available_copies");
                if (available <= 0) {
                    conn.rollback();
                    return false; // No copies available
                }
            } else {
                conn.rollback();
                return false; // Book not found
            }

            // Update available copies
            String updateSql = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, bookId);
            updateStmt.executeUpdate();

            // Insert transaction
            String insertSql = "INSERT INTO transactions (user_id, book_id, issue_date, due_date) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY))";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, bookId);
            insertStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean returnBook(int userId, int bookId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Find the transaction with no return date
            String findSql = "SELECT transaction_id, due_date FROM transactions WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
            PreparedStatement findStmt = conn.prepareStatement(findSql);
            findStmt.setInt(1, userId);
            findStmt.setInt(2, bookId);
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                Date dueDate = rs.getDate("due_date");
                Date returnDate = new Date(System.currentTimeMillis());

                // Calculate fine
                double fine = FineCalculator.calculateFine(dueDate, returnDate);

                // Update transaction with return date and fine
                String updateSql = "UPDATE transactions SET return_date = ?, fine_paid = ? WHERE transaction_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setDate(1, returnDate);
                updateStmt.setDouble(2, fine);
                updateStmt.setInt(3, transactionId);
                updateStmt.executeUpdate();

                // Update available copies
                String updateBookSql = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";
                PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql);
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();

                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false; // No active transaction found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
