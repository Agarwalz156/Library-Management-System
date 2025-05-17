package src;

import java.util.ArrayList;
import java.util.List;

public class StudentManager {

    public static Student authenticate(String username, String password) {
        // Mock authentication logic
        if (username.equals("student1") && password.equals("password1")) {
            return new Student(1, "student1", "Student One", "student");
        } else if (username.equals("student2") && password.equals("password2")) {
            return new Student(2, "student2", "Student Two", "student");
        }
        return null;
    }

    public static List<Student> getAllStudents() {
        // Mock data for students
        List<Student> students = new ArrayList<>();
        students.add(new Student(1, "student1", "Student One", "student"));
        students.add(new Student(2, "student2", "Student Two", "student"));
        return students;
    }
}