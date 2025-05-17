package src;

public class Student extends User {
    public Student(int userId, String username, String fullName, String role) {
        super(userId, username, fullName, role);
    }

    public int getId() {
        return getUserId();
    }
}