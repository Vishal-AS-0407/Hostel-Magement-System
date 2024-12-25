import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class Student {
    private String name;
    private int rollNumber;
    private String department;
    private boolean present;

    public Student(String name, int rollNumber, String department) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.department = department;
        this.present = true; 
    }

    public String getName() {
        return name;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    @Override
    public String toString() {
        return String.format("| %-20s | %-10d | %-20s | %-8s |", name, rollNumber, department, present ? "Present" : "Absent");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

class Room {
    private int roomNumber;
    private List<Student> students;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
        this.students = new ArrayList<>();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public List<Student> getStudents() {
        return students;
    }

    public boolean isFull() {
        return students.size() >= 4;
    }

    public boolean addStudent(Student student) {
        if (!isFull()) {
            students.add(student);
            return true;
        }
        return false;
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public boolean containsStudent(int rollNumber) {
        for (Student student : students) {
            if (student.getRollNumber() == rollNumber) {
                return true;
            }
        }
        return false;
    }

    public Student getStudentByRollNumber(int rollNumber) {
        for (Student student : students) {
            if (student.getRollNumber() == rollNumber) {
                return student;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Room Number: ").append(roomNumber).append("\n");

        if (students.isEmpty()) {
            builder.append("No students in the room.");
        } else {
            builder.append("Students in the room:\n");
            builder.append("+----------------------+------------+----------------------+----------+\n");
            builder.append("| Name                 | Roll Number | Department           | Present  |\n");
            builder.append("+----------------------+------------+----------------------+----------+\n");
            for (Student student : students) {
                builder.append(student.toString()).append("\n");
            }
            builder.append("+----------------------+------------+----------------------+----------+\n");
        }

        return builder.toString();
    }
}

enum Department {
    AIE,
    CSE,
    CYS
}

class HostelManagementSystem {
    private List<Room> rooms;
    private List<Student> students;
    private static final String STUDENT_DATA_FILE = "student_data.txt";
    private static final String ATTENDANCE_FILE = "attendance.txt";

    public HostelManagementSystem() {
        rooms = new ArrayList<>();
        students = new ArrayList<>();
        loadStudentData();
        loadAttendanceData();
    }

    public void displayStudentRoomAndAttendance() {
        System.out.println("Student Room and Attendance Details:");
        System.out.println("+------------+-------------------+----------------------+----------+");

        for (Room room : rooms) {
            System.out.println("Room Number: " + room.getRoomNumber());

            if (room.getStudents().isEmpty()) {
                System.out.println("No students in the room.");
            } else {
                System.out.println("+----------------------+------------+----------------------+----------+");
                System.out.println("| Name                 | Roll Number | Department           | Present  |");
                System.out.println("+----------------------+------------+----------------------+----------+");

                for (Student student : room.getStudents()) {
                    System.out.println(student.toString());
                }

                System.out.println("+----------------------+------------+----------------------+----------+");
            }
        }
    }

    public void addStudent() {
        Scanner scanner = new Scanner(System.in);

        boolean validRollNumber = false;
        int rollNumber = 0;

        while (!validRollNumber) {
            System.out.println("Enter roll number (5 digits): ");
            try {
                String rollNumberString = scanner.nextLine();
                if (rollNumberString.matches("\\d{5}")) {
                    rollNumber = Integer.parseInt(rollNumberString);
                    validRollNumber = true;
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid roll number. Roll number must be 5 digits.");
            }
        }

        // Check if roll number already exists
        if (isRollNumberExists(rollNumber)) {
            System.out.println("Roll number already exists. Please enter a unique roll number.");
            return;
        }

        System.out.println("Enter student name: ");
        String name = scanner.nextLine();

        // Validate name to allow only alphabetic characters
        boolean validName = name.matches("[a-zA-Z]+");

        while (!validName) {
            System.out.println("Invalid name. Please enter a valid name (alphabetic characters only): ");
            name = scanner.nextLine();
            validName = name.matches("[a-zA-Z]+");
        }

        boolean validDepartment = false;
        String department = "";

        while (!validDepartment) {
            System.out.println("Enter department (AIE, CSE, or CYS): ");
            department = scanner.nextLine().toUpperCase();

            try {
                Department.valueOf(department);
                validDepartment = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid department. Please enter a valid department.");
            }
        }

        Student student = new Student(name, rollNumber, department);
        students.add(student);
        saveStudentData(student);
        System.out.println("Student added successfully.");
    }

    private boolean isRollNumberExists(int rollNumber) {
        for (Student student : students) {
            if (student.getRollNumber() == rollNumber) {
                return true;
            }
        }
        return false;
    }



    public void saveStudentData(Student student) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENT_DATA_FILE, true))) {
            String data = student.getName() + "," + student.getRollNumber() + "," + student.getDepartment();
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error occurred while saving student data.");
        }
    }

    public void loadStudentData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENT_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String name = data[0];
                    int rollNumber = Integer.parseInt(data[1]);
                    String department = data[2];
                    Student student = new Student(name, rollNumber, department);
                    students.add(student);
                }
            }
        } catch (IOException e) {
            System.out.println("Error occurred while loading student data.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid roll number format in the student data file.");
        }
    }

    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            System.out.println("Student Details:");
            System.out.println("+----------------------+------------+----------------------+----------+");
            System.out.println("| Name                 | Roll Number | Department           | Present  |");
            System.out.println("+----------------------+------------+----------------------+----------+");
            for (Student student : students) {
                System.out.println(student.toString());
            }
            System.out.println("+----------------------+------------+----------------------+----------+");
        }
    }

    public void searchStudent() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter student name or roll number to search: ");
        String searchTerm = scanner.nextLine();

        boolean found = false;

        for (Student student : students) {
            if (student.getName().equalsIgnoreCase(searchTerm) || student.getRollNumber() == Integer.parseInt(searchTerm)) {
                System.out.println("Student Found:");
                System.out.println(student.toString());
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("No student found with the given search term.");
        }
    }

    public void modifyStudent() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter roll number of the student to modify: ");
        int rollNumber = 0;
        try {
            rollNumber = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid roll number format. Please enter a numeric value.");
            return;
        }

        boolean found = false;

        for (Student student : students) {
            if (student.getRollNumber() == rollNumber) {
                System.out.println("Enter new name: ");
                String newName = scanner.nextLine();

                System.out.println("Enter new department (AIE, CSE, or CYS): ");
                String newDepartment = scanner.nextLine().toUpperCase();

                student.setName(newName);
                student.setDepartment(newDepartment);

                System.out.println("Student details modified successfully.");
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("No student found with the given roll number.");
        }
    }

    public void removeStudent() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter roll number of the student to remove: ");
        int rollNumber = 0;
        try {
            rollNumber = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid roll number format. Please enter a numeric value.");
            return;
        }

        boolean found = false;

        for (Student student : students) {
            if (student.getRollNumber() == rollNumber) {
                students.remove(student);
                System.out.println("Student removed successfully.");
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("No student found with the given roll number.");
        }
    }

    public void allocateRoom() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter room number (1-10): ");
        int roomNumber = 0;
        try {
            roomNumber = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid room number format. Please enter a numeric value.");
            return;
        }

        Room room = getRoomByNumber(roomNumber);

        if (room == null) {
            room = new Room(roomNumber);
            rooms.add(room);
        }

        if (room.isFull()) {
            System.out.println("Room is already full. Cannot add more students.");
            return;
        }

        System.out.println("Enter roll number of the student to allocate: ");
        int rollNumber = 0;
        try {
            rollNumber = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid roll number format. Please enter a numeric value.");
            return;
        }

        Student student = getStudentByRollNumber(rollNumber);

        if (student == null) {
            System.out.println("No student found with the given roll number.");
            return;
        }

        if (room.containsStudent(rollNumber)) {
            System.out.println("Student is already allocated to the room.");
            return;
        }

        boolean added = room.addStudent(student);

        if (added) {
            System.out.println("Student allocated to the room successfully.");
        } else {
            System.out.println("Failed to allocate student to the room. Room is already full.");
        }
    }

    public void removeStudentFromRoom() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter room number (1-10): ");
        int roomNumber = 0;
        try {
            roomNumber = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid room number format. Please enter a numeric value.");
            return;
        }

        Room room = getRoomByNumber(roomNumber);

        if (room == null) {
            System.out.println("No room found with the given number.");
            return;
        }

        System.out.println("Enter roll number of the student to remove: ");
        int rollNumber = 0;
        try {
            rollNumber = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid roll number format. Please enter a numeric value.");
            return;
        }

        Student student = room.getStudentByRollNumber(rollNumber);

        if (student == null) {
            System.out.println("No student found in the room with the given roll number.");
            return;
        }

        room.removeStudent(student);
        System.out.println("Student removed from the room successfully.");
    }

    public void markAttendance() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter roll number of the student to mark attendance: ");
        int rollNumber = 0;
        try {
            rollNumber = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid roll number format. Please enter a numeric value.");
            return;
        }

        Student student = getStudentByRollNumber(rollNumber);

        if (student == null) {
            System.out.println("No student found with the given roll number.");
            return;
        }

        System.out.println("Is the student present? (Y/N): ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            student.setPresent(true);
            System.out.println("Attendance marked as present.");
        } else if (choice.equalsIgnoreCase("N")) {
            student.setPresent(false);
            System.out.println("Attendance marked as absent.");
        } else {
            System.out.println("Invalid choice. Attendance not marked.");
        }

        saveAttendanceData();
    }

    public void saveAttendanceData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ATTENDANCE_FILE))) {
            for (Student student : students) {
                String data = student.getRollNumber() + "," + (student.isPresent() ? "P" : "A");
                writer.write(data);
                writer.newLine();
            }
            System.out.println("Attendance data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while saving attendance data.");
        }
    }

    public void loadAttendanceData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    int rollNumber = Integer.parseInt(data[0]);
                    boolean present = data[1].equalsIgnoreCase("P");
                    Student student = getStudentByRollNumber(rollNumber);
                    if (student != null) {
                        student.setPresent(present);
                    }
                }
            }
            System.out.println("Attendance data loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while loading attendance data.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid roll number format in the attendance data file.");
        }
    }

    public Room getRoomByNumber(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    public Student getStudentByRollNumber(int rollNumber) {
        for (Student student : students) {
            if (student.getRollNumber() == rollNumber) {
                return student;
            }
        }
        return null;
    }

    public void displayStudentsByDepartment() {
        System.out.println("Students by Department:");
        System.out.println("-----------------------");

        // Iterate over the departments
        for (Department department : Department.values()) {
            System.out.println("Department: " + department);
            System.out.println("+------------+----------------------+");
            System.out.println("| Roll Number |        Name          |");
            System.out.println("+------------+----------------------+");

            // Iterate over the students and display those belonging to the current department
            for (Student student : students) {
                if (student.getDepartment().equals(department.name())) {
                    System.out.printf("| %-11d | %-20s |%n", student.getRollNumber(), student.getName());
                }
            }

            System.out.println("+------------+----------------------+");
            System.out.println();
        }
    }

    public void deleteAllData() {
        try {
            // Delete student data
            BufferedWriter studentWriter = new BufferedWriter(new FileWriter(STUDENT_DATA_FILE));
            studentWriter.write("");
            studentWriter.close();
            students.clear();

            // Delete attendance data
            BufferedWriter attendanceWriter = new BufferedWriter(new FileWriter(ATTENDANCE_FILE));
            attendanceWriter.write("");
            attendanceWriter.close();

            System.out.println("All data deleted successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while deleting data.");
        }
    }
}

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static HostelManagementSystem managementSystem = new HostelManagementSystem();

    public static void main(String[] args) {
        showMenu();
    }

    public static void showMenu() {
        while (true) {
            System.out.println("-------Welcome to Hostel Management system of the YBAnnex-------");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Modify Student Details");
            System.out.println("5. Remove Student");
            System.out.println("6. Allocate Room");
            System.out.println("7. Remove Student from Room");
            System.out.println("8. Mark Attendance");
            System.out.println("9. Display Students by Department");
            System.out.println("10. Display Student Room and Attendance");
            System.out.println("11. Delete All Data");
            System.out.println("12. Exit");
            System.out.print("Enter your choice: ");

            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid choice format. Please enter a numeric value.");
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    managementSystem.addStudent();
                    break;
                case 2:
                    managementSystem.displayAllStudents();
                    break;
                case 3:
                    managementSystem.searchStudent();
                    break;
                case 4:
                    managementSystem.modifyStudent();
                    break;
                case 5:
                    managementSystem.removeStudent();
                    break;
                case 6:
                    managementSystem.allocateRoom();
                    break;
                case 7:
                    managementSystem.removeStudentFromRoom();
                    break;
                case 8:
                    managementSystem.markAttendance();
                    break;
                case 9:
                    managementSystem.displayStudentsByDepartment();
                    break;
                case 10:
                    managementSystem.displayStudentRoomAndAttendance();
                    break;
                case 11:
                    managementSystem.deleteAllData();
                    break;
                case 12:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
