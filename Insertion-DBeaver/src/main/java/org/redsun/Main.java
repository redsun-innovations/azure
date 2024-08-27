package org.redsun;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
public class Main {
    // Database connection details
    private static final String DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=master;integratedSecurity=true;encrypt=false;";
    public static void main(String[] args) {
        // Tables to monitor
        String[] tables = {"MyTable1", "MyTable2", "MyTable3", "MyTable4"};
        System.out.println("Starting data migration process...");
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {

            for (String table : tables) {
                fetchAndStoreData(conn, table);
            }
            System.out.println("Data migration process completed.");
        } catch (SQLException e) {
            System.err.println("SQL Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static void fetchAndStoreData(Connection conn, String table) throws SQLException {
        // Get the current timestamp and the timestamp from 2 minutes ago
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoMinutesAgo = now.minusMinutes(2);
        System.out.println("Fetching data from table: " + table);
        System.out.println("Fetching records updated or inserted after: " + twoMinutesAgo);
        // Query to fetch rows updated or inserted within the last 2 minutes
        String query = "SELECT ID, Data, CreatedAt, UpdatedAt FROM Q." + table +
                " WHERE UpdatedAt >= ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(twoMinutesAgo));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String data = rs.getString("Data");
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                    // Determine the action based on the timestamp
                    String action = determineAction(rs.getTimestamp("CreatedAt"), updatedAt);
                    System.out.println("Found record - ID: " + id + ", Data: " + data +
                            ", CreatedAt: " + createdAt + ", UpdatedAt: " + updatedAt + ", Action: " + action);
                    storeData(conn, table, id, data, createdAt, updatedAt, action);
                }
            }
        }
    }
    private static String determineAction(Timestamp createdAt, Timestamp updatedAt) {
        // Convert timestamps to LocalDateTime for comparison
        LocalDateTime createdAtTime = createdAt.toLocalDateTime();
        LocalDateTime updatedAtTime = updatedAt.toLocalDateTime();

        // If the creation and update timestamps are the same, it's an insert
        if (createdAtTime.equals(updatedAtTime)) {
            return "Inserted";
        } else {
            // If the update timestamp is different from the creation timestamp, it's an update
            return "Updated";
        }
    }
    private static void storeData(Connection conn, String table, int id, String data, Timestamp createdAt, Timestamp updatedAt, String action) throws SQLException {
        // Insert data into the destination table with an action type
        String insertQuery = "INSERT INTO Q.DestinationTable (ID, TableName, Data, CreatedAt, UpdatedAt, Action) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, table);
            pstmt.setString(3, data);
            pstmt.setTimestamp(4, createdAt);
            pstmt.setTimestamp(5, updatedAt);
            pstmt.setString(6, action); // Set the action type
            pstmt.executeUpdate();
            System.out.println("Inserted record - ID: " + id + ", TableName: " + table +
                    ", Data: " + data + ", CreatedAt: " + createdAt + ", UpdatedAt: " + updatedAt + ", Action: " + action);
        }
    }
}