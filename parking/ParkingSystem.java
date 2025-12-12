package parking;

import java.sql.*;
import java.util.Scanner;

public class ParkingSystem {

    // Connect to MySQL database
    public static Connection connectDB() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/parkingdb";
        String user = "root"; // default XAMPP username
        String password = ""; // default XAMPP password
        return DriverManager.getConnection(url, user, password);
    }

    // Add a parking slot
    public static void addSlot(Scanner sc) {
        try (Connection con = connectDB()) {

            System.out.print("Enter Level No: ");
            int level = sc.nextInt();
            sc.nextLine(); // consume leftover newline

            String sql = "INSERT INTO parking_slot (level_no) VALUES (?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, level);
            ps.executeUpdate();

            System.out.println("Slot Added Successfully!");

        } catch (SQLException e) {
            System.out.println("Error adding slot: " + e.getMessage());
        }
    }

    // Park a vehicle
    public static void parkVehicle(Scanner sc) {
        try (Connection con = connectDB()) {

            System.out.print("Enter Slot ID: ");
            int slotId = sc.nextInt();
            sc.nextLine(); // consume leftover newline

            System.out.print("Enter Vehicle No: ");
            String vehicleNo = sc.nextLine();

            String sql = "UPDATE parking_slot SET is_free = 0, vehicle_no = ? WHERE slot_id = ? AND is_free = 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, vehicleNo);
            ps.setInt(2, slotId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Vehicle Parked Successfully!");
            } else {
                System.out.println("Slot Not Free or Slot Does Not Exist.");
            }

        } catch (SQLException e) {
            System.out.println("Error parking vehicle: " + e.getMessage());
        }
    }

    // Free a parking slot
    public static void freeSlot(Scanner sc) {
        try (Connection con = connectDB()) {

            System.out.print("Enter Slot ID: ");
            int slotId = sc.nextInt();
            sc.nextLine(); // consume leftover newline

            String sql = "UPDATE parking_slot SET is_free = 1, vehicle_no = NULL WHERE slot_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, slotId);
            ps.executeUpdate();

            System.out.println("Slot Freed Successfully!");

        } catch (SQLException e) {
            System.out.println("Error freeing slot: " + e.getMessage());
        }
    }

    // View all parking slots
    public static void viewSlots() {
        try (Connection con = connectDB()) {

            String sql = "SELECT * FROM parking_slot";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            System.out.println("\n--- PARKING SLOTS ---");
            while (rs.next()) {
                int id = rs.getInt("slot_id");
                int level = rs.getInt("level_no");
                boolean free = rs.getInt("is_free") == 1;
                String vehicle = rs.getString("vehicle_no");

                System.out.println("Slot ID: " + id +
                                   " | Level: " + level +
                                   " | Free: " + free +
                                   " | Vehicle: " + vehicle);
            }

        } catch (SQLException e) {
            System.out.println("Error viewing slots: " + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        while (choice != 5) {
            System.out.println("\n===== SIMPLE PARKING SYSTEM =====");
            System.out.println("1. Add Slot");
            System.out.println("2. Park Vehicle");
            System.out.println("3. Free Slot");
            System.out.println("4. View Slots");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            if (sc.hasNextInt()) {
                choice = sc.nextInt();
                sc.nextLine(); // consume leftover newline

                switch (choice) {
                    case 1 -> addSlot(sc);
                    case 2 -> parkVehicle(sc);
                    case 3 -> freeSlot(sc);
                    case 4 -> viewSlots();
                    case 5 -> System.out.println("Exiting Program...");
                    default -> System.out.println("Invalid Choice!");
                }
            } else {
                System.out.println("Please enter a valid number!");
                sc.nextLine(); // clear invalid input
            }
        }

        sc.close();
    }
}
