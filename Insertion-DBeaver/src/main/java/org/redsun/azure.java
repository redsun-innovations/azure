package org.redsun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;


public class azure {
    private static final Logger logger = LoggerFactory.getLogger(AzureMigration.class);

    private static final String DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=master;integratedSecurity=true;encrypt=false;";
    private static final LocalDateTime SPECIFIC_TIME = LocalDateTime.of(2024, 8, 22, 12, 0); // Example: August 22, 2024, 12:00 PM

    public static void main(String[] args) {
        logger.info("Starting data migration process...");

        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            fetchAndStoreData(conn);
            logger.info("Data migration process completed.");
        } catch (SQLException e) {
            logger.error("SQL Exception occurred: " + e.getMessage(), e);
        }
    }

    private static void fetchAndStoreData(Connection conn) throws SQLException {
        logger.debug("Fetching data from the table, records updated after: {}", SPECIFIC_TIME);

        // Query to fetch rows where last_updated_time is greater than the specific time
        String query = "SELECT dimval_spec, dimval_display_order, dimval_dimension_name, Endeca_Id, dimval_display_name, Endeca_Action, dimval_parent_spec, dimval_created_time, dimval_last_updated_time " +
                "FROM YourTableName WHERE dimval_last_updated_time > ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(SPECIFIC_TIME));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String dimvalSpec = rs.getString("dimval_spec");
                    int displayOrder = rs.getInt("dimval_display_order");
                    String dimensionName = rs.getString("dimval_dimension_name");
                    String endecaId = rs.getString("Endeca_Id");
                    String displayName = rs.getString("dimval_display_name");
                    String action = rs.getString("Endeca_Action");
                    String parentSpec = rs.getString("dimval_parent_spec");
                    Timestamp createdTime = rs.getTimestamp("dimval_created_time");
                    Timestamp lastUpdatedTime = rs.getTimestamp("dimval_last_updated_time");

                    String actionDetermined = determineAction(createdTime, lastUpdatedTime);
                    logger.debug("Found record - Spec: {}, DisplayOrder: {}, DimensionName: {}, EndecaId: {}, DisplayName: {}, Action: {}, ParentSpec: {}, CreatedTime: {}, LastUpdatedTime: {}, DeterminedAction: {}",
                            dimvalSpec, displayOrder, dimensionName, endecaId, displayName, action, parentSpec, createdTime, lastUpdatedTime, actionDetermined);

                    storeData(conn, dimvalSpec, displayOrder, dimensionName, endecaId, displayName, action, parentSpec, createdTime, lastUpdatedTime, actionDetermined);
                }
            }
        }
    }

    private static String determineAction(Timestamp createdAt, Timestamp updatedAt) {
        LocalDateTime createdAtTime = createdAt.toLocalDateTime();
        LocalDateTime updatedAtTime = updatedAt.toLocalDateTime();
        return createdAtTime.equals(updatedAtTime) ? "Inserted" : "Updated";
    }

    private static void storeData(Connection conn, String dimvalSpec, int displayOrder, String dimensionName, String endecaId, String displayName, String action, String parentSpec, Timestamp createdTime, Timestamp lastUpdatedTime, String actionDetermined) throws SQLException {
        String insertQuery = "INSERT INTO DestinationTable (dimval_spec, dimval_display_order, dimval_dimension_name, Endeca_Id, dimval_display_name, Endeca_Action, dimval_parent_spec, dimval_created_time, dimval_last_updated_time, ActionDetermined) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, dimvalSpec);
            pstmt.setInt(2, displayOrder);
            pstmt.setString(3, dimensionName);
            pstmt.setString(4, endecaId);
            pstmt.setString(5, displayName);
            pstmt.setString(6, action);
            pstmt.setString(7, parentSpec);
            pstmt.setTimestamp(8, createdTime);
            pstmt.setTimestamp(9, lastUpdatedTime);
            pstmt.setString(10, actionDetermined);
            pstmt.executeUpdate();
            logger.debug("Inserted record - Spec: {}, DisplayOrder: {}, DimensionName: {}, EndecaId: {}, DisplayName: {}, Action: {}, ParentSpec: {}, CreatedTime: {}, LastUpdatedTime: {}, DeterminedAction: {}",
                    dimvalSpec, displayOrder, dimensionName, endecaId, displayName, action, parentSpec, createdTime, lastUpdatedTime, actionDetermined);
        }
    }
}
