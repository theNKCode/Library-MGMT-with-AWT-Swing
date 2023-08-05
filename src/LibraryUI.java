import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXDatePicker;

//import javax.swing.UIManager;
//import javax.swing.border.Border;

public class LibraryUI extends JFrame {

    private JComboBox<Integer> bookIdField, studentIdField;
    private JTextField borrowIdField;
    private JTextArea borrowInfoArea;
    private JXDatePicker borrowDatePicker;
    private JButton deleteButton;

    // MySQL database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_mgmt";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Nikhil123";

    public LibraryUI() {
        // try {
        // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // } catch (ClassNotFoundException | InstantiationException |
        // IllegalAccessException | UnsupportedLookAndFeelException e) {
        // e.printStackTrace();
        // }

        setTitle("Library Book Record Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 580);
        setLayout(null); // Using absolute positioning

        // Create labels and drop-down fields for book ID, student ID, and borrow ID
        JLabel bookIdLabel = new JLabel("Book ID:");
        bookIdLabel.setBounds(40, 40, 80, 20);
        bookIdField = new JComboBox<>();
        bookIdField.setBounds(130, 40, 200, 20);

        JLabel studentIdLabel = new JLabel("Student ID:");
        studentIdLabel.setBounds(40, 80, 80, 20);
        studentIdField = new JComboBox<>();
        studentIdField.setBounds(130, 80, 200, 20);

        JLabel borrowIdLabel = new JLabel("Borrow ID:");
        borrowIdLabel.setBounds(40, 120, 80, 20);
        borrowIdField = new JTextField();
        borrowIdField.setBounds(130, 120, 200, 20);

        // Create label and date picker for borrowing date
        JLabel borrowDateLabel = new JLabel("Borrow Date:");
        borrowDateLabel.setBounds(40, 160, 80, 20);
        borrowDatePicker = new JXDatePicker();
        borrowDatePicker.setDate(new Date());
        borrowDatePicker.setFormats("dd-MM-yyyy");
        borrowDatePicker.setBounds(130, 160, 200, 20);

        // Create button for borrowing a book
        JButton borrowButton = new JButton("Borrow Book");
        borrowButton.setBounds(40, 200, 120, 30);
        borrowButton.setBackground(new Color(71, 137, 66));
        borrowButton.setForeground(Color.WHITE);

        // Create button for returning a book
        JButton returnButton = new JButton("Return Book");
        returnButton.setBounds(180, 200, 120, 30);
        returnButton.setBackground(new Color(144, 84, 217));
        returnButton.setForeground(Color.WHITE);

        // Create button for deleting a borrow record
        JButton updateButton = new JButton("Update Record");
        updateButton.setBounds(40, 240, 120, 30);
        updateButton.setBackground(new Color(220, 53, 69));
        updateButton.setForeground(Color.WHITE);

        // Create delete button
        deleteButton = new JButton("Delete Record");
        deleteButton.setBounds(180, 240, 120, 30);
        deleteButton.setBackground(new Color(217, 17, 117));
        deleteButton.setForeground(Color.WHITE);

        // Create button for deleting a borrow record
        JButton displayButton = new JButton("Display Record");
        displayButton.setBounds(100, 280, 120, 30);
        displayButton.setBackground(new Color(0, 0, 0));
        displayButton.setForeground(Color.WHITE);

        // Create scrollable text area to display borrowing information
        borrowInfoArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(borrowInfoArea);
        scrollPane.setBounds(30, 340, 300, 180);

        // Add components to the frame
        add(bookIdLabel);
        add(bookIdField);
        add(studentIdLabel);
        add(studentIdField);
        add(borrowIdLabel);
        add(borrowIdField);
        add(borrowDateLabel);
        add(borrowDatePicker);
        add(borrowButton);
        add(returnButton);
        add(updateButton);
        add(deleteButton);
        add(displayButton);
        add(scrollPane);

        // Fetch book IDs and populate the bookIdField drop-down
        fetchBookIds();

        // Fetch student IDs and populate the studentIdField drop-down
        fetchStudentIds();

        // Add action listener to the borrow button
        // ACTION LISTENER OF BORROW BUTTON
        borrowButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int bookId = (int) bookIdField.getSelectedItem();
                        int studentId = (int) studentIdField.getSelectedItem();
                        String borrowId = borrowIdField.getText();
                        Date borrowDate = borrowDatePicker.getDate();

                        // Borrow a book
                        try (
                                Connection connection = DriverManager.getConnection(
                                        DB_URL,
                                        DB_USER,
                                        DB_PASSWORD
                                )
                        ) {
                            String query =
                                    "INSERT INTO borrow (borrow_id, book_id, student_id, borrowing_date) VALUES (?, ?, ?, ?)";
                            PreparedStatement statement = connection.prepareStatement(query);
                            statement.setString(1, borrowId);
                            statement.setInt(2, bookId);
                            statement.setInt(3, studentId);
                            statement.setDate(4, new java.sql.Date(borrowDate.getTime()));

                            // Execute the query
                            int rowsInserted = statement.executeUpdate();

                            if (rowsInserted > 0) {
                                JOptionPane.showMessageDialog(null, "Record Added Successfully");
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to add rental record");
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error connecting to the database");
                            ex.printStackTrace();
                        }

                        // Update borrowing information in the text area
                        borrowInfoArea.append(
                                "Book ID: " +
                                        bookId +
                                        "\nStudent ID: " +
                                        studentId +
                                        "\nBorrow ID: " +
                                        borrowId +
                                        "\nBorrow Date: " +
                                        borrowDate +
                                        "\n\n"
                        );

                        // Clear the input fields
                        borrowIdField.setText("");
                        borrowDatePicker.setDate(new Date());
                    }
                }
        );

        // Add action listener to the return button
        returnButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int borrowId = Integer.parseInt(borrowIdField.getText());

                        // Return a book
                        try (
                                Connection connection = DriverManager.getConnection(
                                        DB_URL,
                                        DB_USER,
                                        DB_PASSWORD
                                )
                        ) {
                            String query =
                                    "UPDATE borrow SET return_date = CURDATE() WHERE borrow_id = ?";
                            PreparedStatement statement = connection.prepareStatement(query);
                            statement.setInt(1, borrowId);
                            // Execute the query
                            int rowsInserted = statement.executeUpdate();

                            if (rowsInserted > 0) {
                                JOptionPane.showMessageDialog(null, "Book Returned Successfully");
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to return record");
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error connecting to the database");
                            ex.printStackTrace();
                        }

                        // Update borrowing information in the text area
                        borrowInfoArea.append(
                                "Borrow ID: " +
                                        borrowId +
                                        "\nReturn Date: " +
                                        new java.sql.Date(System.currentTimeMillis()) +
                                        "\n\n"
                        );

                        // Clear the input field
                        borrowIdField.setText("");
                    }
                }
        );

        // Add action listener to the delete button
        updateButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int bookId1 = (int) bookIdField.getSelectedItem();
                        int studentId1 = (int) studentIdField.getSelectedItem();
                        String borrowId1 = borrowIdField.getText();
                        Date borrowDate1 = borrowDatePicker.getDate();

                        // Delete a borrow record
                        try (
                                Connection connection = DriverManager.getConnection(
                                        DB_URL,
                                        DB_USER,
                                        DB_PASSWORD
                                )
                        ) {
                            String query =
                                    "Update borrow SET book_id = ?, student_id = ?, borrowing_date = ? WHERE borrow_id = ?";
                            PreparedStatement statement = connection.prepareStatement(query);
                            statement.setInt(1, bookId1);
                            statement.setInt(2, studentId1);
                            statement.setDate(3, new java.sql.Date(borrowDate1.getTime()));
                            statement.setString(4, borrowId1);
                            int rowsInserted = statement.executeUpdate();

                            if (rowsInserted > 0) {
                                JOptionPane.showMessageDialog(null, "Book Updated Successfully");
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to update record");
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error connecting to the database");
                            ex.printStackTrace();
                        }

                        // Update borrowing information in the text area
                        borrowInfoArea.append(
                                "Book ID: " +
                                        bookId1 +
                                        "\nStudent ID: " +
                                        studentId1 +
                                        "\nBorrow ID: " +
                                        borrowId1 +
                                        "\nBorrow Date: " +
                                        borrowDate1 +
                                        "\n\n"
                        );

                        // Clear the input field
                        borrowIdField.setText("");
                    }
                }
        );

        deleteButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Get the borrow ID to delete
                        String borrowId = borrowIdField.getText();

                        // Delete the record with the specified borrow ID
                        try (
                                Connection connection = DriverManager.getConnection(
                                        DB_URL,
                                        DB_USER,
                                        DB_PASSWORD
                                )
                        ) {
                            String query = "DELETE FROM borrow WHERE borrow_id = ?";
                            PreparedStatement statement = connection.prepareStatement(query);
                            statement.setString(1, borrowId);
                            int rowsInserted = statement.executeUpdate();

                            if (rowsInserted > 0) {
                                JOptionPane.showMessageDialog(null, "Record Deleted Successfully");
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to delete record");
                            }

                            // Clear the input field
                            borrowIdField.setText("");

                            // Update borrowing information in the text area
                            borrowInfoArea.append(
                                    "Record with Borrow ID " + borrowId + " has been deleted.\n\n"
                            );
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error connecting to the database");
                            ex.printStackTrace();
                        }
                    }
                }
        );

        // Add action listener to the display button
        displayButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Retrieve contacts from the database
                        try (
                                Connection connection = DriverManager.getConnection(
                                        DB_URL,
                                        DB_USER,
                                        DB_PASSWORD
                                )
                        ) {
                            String query =
                                    "SELECT borrow_id, book_id, student_id, borrowing_date, return_date from borrow";
                            PreparedStatement statement = connection.prepareStatement(query);
                            ResultSet resultSet = statement.executeQuery();

                            // Clear existing contacts in the contacts area
                            borrowInfoArea.setText("");

                            // Display retrieved contacts in the contacts area
                            while (resultSet.next()) {
                                String borrow_id = resultSet.getString("borrow_id");
                                String book_id = resultSet.getString("book_id");
                                String student_id = resultSet.getString("student_id");
                                Date borrowing_date = resultSet.getDate("borrowing_date");
                                Date return_date = resultSet.getDate("return_date");

                                borrowInfoArea.append(
                                        "\n    Borrow ID: " +
                                                borrow_id +
                                                "\n    Book ID: " +
                                                book_id +
                                                "\n    Student ID: " +
                                                student_id +
                                                "\n    Borrow Date: " +
                                                borrowing_date +
                                                "\n    Return Date: " +
                                                return_date +
                                                "\n"
                                );
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error connecting to the database");
                            ex.printStackTrace();
                        }
                    }
                }
        );

        // Set default close operation and make the frame visible
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fetchBookIds() {
        try (
                Connection connection = DriverManager.getConnection(
                        DB_URL,
                        DB_USER,
                        DB_PASSWORD
                )
        ) {
            String query = "SELECT book_id FROM books";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int bookId = resultSet.getInt("book_id");
                bookIdField.addItem(bookId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void fetchStudentIds() {
        try (
                Connection connection = DriverManager.getConnection(
                        DB_URL,
                        DB_USER,
                        DB_PASSWORD
                )
        ) {
            String query = "SELECT student_id FROM student";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int studentId = resultSet.getInt("student_id");
                studentIdField.addItem(studentId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
//         try {
//         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//         } catch (ClassNotFoundException | InstantiationException |
//         IllegalAccessException | UnsupportedLookAndFeelException e) {
//         e.printStackTrace();
//         }

        SwingUtilities.invokeLater(() -> {
            LibraryUI ui = new LibraryUI();
            ui.setVisible(true);
        });
    }
}