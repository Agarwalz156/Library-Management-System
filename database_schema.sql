DROP TABLE IF EXISTS issued_books;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS departments;

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;
CREATE TABLE departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    total_copies INT NOT NULL,
    available_copies INT NOT NULL,
    department_id INT NOT NULL,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL
);
CREATE TABLE issued_books (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    book_id INT NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);
INSERT INTO departments (name) VALUES
('Computer Science'),
('Mathematics'),
('Physics'),
('Chemistry'),
('Biology'),
('Electrical Engineering'),
('Mechanical Engineering'),
('Civil Engineering'),
('Business Administration'),
('Literature');
INSERT INTO students (username, password, full_name) VALUES
('student1', 'password1', 'Student One'),
('student2', 'password2', 'Student Two');

INSERT INTO books (title, author, isbn, total_copies, available_copies, department_id) VALUES
('Introduction to Algorithms', 'Thomas H. Cormen', '9780262033848', 5, 5, 1),
('Clean Code', 'Robert C. Martin', '9780132350884', 3, 3, 1),
('Design Patterns', 'Erich Gamma', '9780201633610', 2, 2, 1),
('Artificial Intelligence: A Modern Approach', 'Stuart Russell', '9780136042594', 4, 4, 1),
('Database System Concepts', 'Abraham Silberschatz', '9780073523323', 6, 6, 1),
('Linear Algebra Done Right', 'Sheldon Axler', '9783319110790', 5, 5, 2),
('Calculus', 'James Stewart', '9781285740621', 4, 4, 2),
('Physics for Scientists and Engineers', 'Raymond A. Serway', '9781133947271', 5, 5, 3),
('Organic Chemistry', 'Paula Yurkanis Bruice', '9780134042282', 3, 3, 4),
('Biology', 'Neil A. Campbell', '9780321775658', 4, 4, 5);


