import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class StudentManagementSystemGUI {
    private JFrame frame;
    private DefaultTableModel studentTableModel;
    private JComboBox<String> studentComboBox;
    private JComboBox<String> courseComboBox;
    private JTextField gradeField;

    private Map<String, Vector<String>> enrolledCourses = new HashMap<>();
    private Map<String, Map<String, String>> grades = new HashMap<>();

    public StudentManagementSystemGUI() {
        frame = new JFrame("Student Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu actionsMenu = new JMenu("Actions");
        JMenuItem addStudentMenuItem = new JMenuItem("Add Student");
        JMenuItem updateStudentMenuItem = new JMenuItem("Update Student");
        JMenuItem viewStudentDetailsMenuItem = new JMenuItem("View Student Details");
        JMenuItem addCourseMenuItem = new JMenuItem("Add Course");
        JMenuItem enrollStudentInCourseMenuItem = new JMenuItem("Enroll Student in Course");
        JMenuItem assignGradeMenuItem = new JMenuItem("Assign Grade to Student");

        addStudentMenuItem.addActionListener(this::showAddStudentForm);
        updateStudentMenuItem.addActionListener(this::showUpdateStudentForm);
        viewStudentDetailsMenuItem.addActionListener(this::showStudentDetails);
        addCourseMenuItem.addActionListener(this::showAddCourseForm);
        enrollStudentInCourseMenuItem.addActionListener(this::showCourseEnrollmentForm);
        assignGradeMenuItem.addActionListener(this::showGradeManagementForm);

        actionsMenu.add(addStudentMenuItem);
        actionsMenu.add(updateStudentMenuItem);
        actionsMenu.add(viewStudentDetailsMenuItem);
        actionsMenu.add(addCourseMenuItem);
        actionsMenu.add(enrollStudentInCourseMenuItem);
        actionsMenu.add(assignGradeMenuItem);

        menuBar.add(actionsMenu);

        frame.setJMenuBar(menuBar);

        JTable studentTable = new JTable();
        studentTableModel = new DefaultTableModel(new Object[]{"First Name", "Last Name"}, 0);
        studentTable.setModel(studentTableModel);

        frame.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    private void showAddStudentForm(ActionEvent e) {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        Object[] fields = {"First Name:", firstNameField, "Last Name:", lastNameField};
        int option = JOptionPane.showConfirmDialog(frame, fields, "Add Student", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            if (!firstName.isEmpty() && !lastName.isEmpty()) {
                Vector<String> studentData = new Vector<>();
                studentData.add(firstName);
                studentData.add(lastName);
                studentTableModel.addRow(studentData);
            } else {
                JOptionPane.showMessageDialog(frame, "Both first name and last name are required!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateStudentForm(ActionEvent e) {
        // Selecting the student to update
        JComboBox<String> studentToUpdateComboBox = new JComboBox<>();
        for (int i = 0; i < studentTableModel.getRowCount(); i++) {
            studentToUpdateComboBox.addItem(studentTableModel.getValueAt(i, 0) + " " + studentTableModel.getValueAt(i, 1));
        }
        JPanel panel = new JPanel();
        panel.add(new JLabel("Select Student:"));
        panel.add(studentToUpdateComboBox);
        int result = JOptionPane.showConfirmDialog(frame, panel, "Select Student to Update", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // Get the selected student
            String selectedStudent = (String) studentToUpdateComboBox.getSelectedItem();
            // Splitting the selected student into first name and last name
            String[] nameParts = selectedStudent.split(" ");
            String firstName = nameParts[0];
            String lastName = nameParts[1];

            // Creating text fields for first name and last name with current values
            JTextField updatedFirstNameField = new JTextField(firstName);
            JTextField updatedLastNameField = new JTextField(lastName);

            // Displaying the form for updating student details
            JPanel updatePanel = new JPanel(new GridLayout(0, 2));
            updatePanel.add(new JLabel("First Name:"));
            updatePanel.add(updatedFirstNameField);
            updatePanel.add(new JLabel("Last Name:"));
            updatePanel.add(updatedLastNameField);

            // Displaying fields for editing courses and grades
            JComboBox<String> courseComboBox = new JComboBox<>();
            JTextField gradeField = new JTextField();
            for (String course : enrolledCourses.keySet()) {
                courseComboBox.addItem(course);
            }
            updatePanel.add(new JLabel("Course:"));
            updatePanel.add(courseComboBox);
            updatePanel.add(new JLabel("Grade:"));
            updatePanel.add(gradeField);

            // Showing the update form in a dialog box
            int updateResult = JOptionPane.showConfirmDialog(frame, updatePanel, "Update Student Details",
                    JOptionPane.OK_CANCEL_OPTION);
            if (updateResult == JOptionPane.OK_OPTION) {
                // Updating the student details in the table
                int selectedRow = studentToUpdateComboBox.getSelectedIndex();
                studentTableModel.setValueAt(updatedFirstNameField.getText(), selectedRow, 0);
                studentTableModel.setValueAt(updatedLastNameField.getText(), selectedRow, 1);

                // Updating the course and grade
                String selectedCourse = (String) courseComboBox.getSelectedItem();
                String newGrade = gradeField.getText().trim();
                Map<String, String> studentGrades = grades.get(selectedStudent);
                studentGrades.put(selectedCourse, newGrade);
                grades.put(selectedStudent, studentGrades);

                JOptionPane.showMessageDialog(frame, "Student details updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }


    private void showStudentDetails(ActionEvent e) {
        JTable studentTable = new JTable();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Student", "Course", "Grade"}, 0);
        studentTable.setModel(model);

        for (Map.Entry<String, Map<String, String>> entry : grades.entrySet()) {
            String studentName = entry.getKey();
            Map<String, String> studentGrades = entry.getValue();
            for (Map.Entry<String, String> gradeEntry : studentGrades.entrySet()) {
                String course = gradeEntry.getKey();
                String grade = gradeEntry.getValue();
                model.addRow(new Object[]{studentName, course, grade});
            }
        }

        JScrollPane scrollPane = new JScrollPane(studentTable);
        JOptionPane.showMessageDialog(frame, scrollPane, "Student Details", JOptionPane.INFORMATION_MESSAGE);
    }



    private void showAddCourseForm(ActionEvent e) {
        JTextField courseField = new JTextField();
        int option = JOptionPane.showConfirmDialog(frame, courseField, "Enter Course Name", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String courseName = courseField.getText().trim();
            if (!courseName.isEmpty()) {
                if (!enrolledCourses.containsKey(courseName)) {
                    enrolledCourses.put(courseName, new Vector<>());
                    courseComboBox.addItem(courseName);
                } else {
                    JOptionPane.showMessageDialog(frame, "Course already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Course name is required!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showCourseEnrollmentForm(ActionEvent e) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));
        JLabel studentLabel = new JLabel("Select Student:");
        studentComboBox = new JComboBox<>();
        for (int i = 0; i < studentTableModel.getRowCount(); i++) {
            studentComboBox.addItem(studentTableModel.getValueAt(i, 0) + " " + studentTableModel.getValueAt(i, 1));
        }
        JLabel courseLabel = new JLabel("Select Course:");
        courseComboBox = new JComboBox<>();
        for (String course : enrolledCourses.keySet()) {
            courseComboBox.addItem(course);
        }
        JLabel gradeLabel = new JLabel("Enter Grade:");
        gradeField = new JTextField();
        panel.add(studentLabel);
        panel.add(studentComboBox);
        panel.add(courseLabel);
        panel.add(courseComboBox);
        panel.add(gradeLabel);
        panel.add(gradeField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Enroll Student in Course", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String student = (String) studentComboBox.getSelectedItem();
            String course = (String) courseComboBox.getSelectedItem();
            String grade = gradeField.getText().trim();
            if (!grade.isEmpty()) {
                Map<String, String> studentGrades = grades.getOrDefault(student, new HashMap<>());
                studentGrades.put(course, grade);
                grades.put(student, studentGrades);
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter a grade!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showGradeManagementForm(ActionEvent e) {
        // Selecting the student
        JComboBox<String> studentComboBox = new JComboBox<>();
        for (int i = 0; i < studentTableModel.getRowCount(); i++) {
            studentComboBox.addItem(studentTableModel.getValueAt(i, 0) + " " + studentTableModel.getValueAt(i, 1));
        }
        JPanel panel = new JPanel();
        panel.add(new JLabel("Select Student:"));
        panel.add(studentComboBox);
        int result = JOptionPane.showConfirmDialog(frame, panel, "Select Student", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // Get the selected student
            String selectedStudent = (String) studentComboBox.getSelectedItem();

            // Displaying the student's courses and grades
            Map<String, String> studentGrades = grades.get(selectedStudent);
            JComboBox<String> courseComboBox = new JComboBox<>();
            JTextField gradeField = new JTextField();
            if (studentGrades != null) {
                for (Map.Entry<String, String> entry : studentGrades.entrySet()) {
                    courseComboBox.addItem(entry.getKey());
                }
                courseComboBox.addActionListener(evt -> {
                    String selectedCourse = (String) courseComboBox.getSelectedItem();
                    String selectedGrade = studentGrades.get(selectedCourse);
                    gradeField.setText(selectedGrade != null ? selectedGrade : "");
                });
            }

            JPanel gradePanel = new JPanel(new GridLayout(0, 2));
            gradePanel.add(new JLabel("Course:"));
            gradePanel.add(courseComboBox);
            gradePanel.add(new JLabel("Grade:"));
            gradePanel.add(gradeField);

            // Showing the Grade Management form in a dialog box
            int gradeResult = JOptionPane.showConfirmDialog(frame, gradePanel, "Grade Management",
                    JOptionPane.OK_CANCEL_OPTION);
            if (gradeResult == JOptionPane.OK_OPTION) {
                // Get the selected course and assigned grade
                String selectedCourse = (String) courseComboBox.getSelectedItem();
                String assignedGrade = gradeField.getText().trim();

                // Update the grade for the selected course
                if (!selectedCourse.isEmpty() && !assignedGrade.isEmpty()) {
                    studentGrades.put(selectedCourse, assignedGrade);
                    grades.put(selectedStudent, studentGrades);
                    JOptionPane.showMessageDialog(frame, "Grade assigned successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a course and assign a grade!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentManagementSystemGUI::new);
    }
}
