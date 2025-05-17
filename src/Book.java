package src;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private int totalCopies;
    private int availableCopies;
    private String department;

    public Book(int bookId, String title, String author, String isbn, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public String getDepartment() {
        return department;
    }
}
