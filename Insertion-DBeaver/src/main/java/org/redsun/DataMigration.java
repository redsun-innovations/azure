package org.redsun;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;

public class DataMigration {
    private static final Logger logger = LoggerFactory.getLogger(DataMigration.class);

    private static final String DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=master;integratedSecurity=true;encrypt=false;";
    private static final LocalDateTime SPECIFIC_TIME = LocalDateTime.of(2024, 8, 22, 12, 0); // Example: August 22, 2024, 12:00 PM
    private static final String CONNECTION_STRING = "<Your-Service-Bus-Connection-String>";
    private static final String TOPIC_NAME = "<Your-Topic-Name>";

    public static void main(String[] args) {
        String[] tables = {"MyTable1", "MyTable2", "MyTable3", "MyTable4"};
        logger.info("Starting data migration process...");

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                     .connectionString(CONNECTION_STRING)
                     .sender()
                     .topicName(TOPIC_NAME)
                     .buildClient()) {

            for (String table : tables) {
                fetchAndSendData(conn, senderClient, table);
            }
            logger.info("Data migration process completed.");
        } catch (SQLException e) {
            logger.error("SQL Exception occurred: " + e.getMessage(), e);
        }
    }

    private static void fetchAndSendData(Connection conn, ServiceBusSenderClient senderClient, String table) throws SQLException {
        logger.debug("Fetching data from table: {}, records updated after: {}", table, SPECIFIC_TIME);

        String query = "SELECT ID, Data, CreatedAt, UpdatedAt FROM Q." + table + " WHERE UpdatedAt > ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(SPECIFIC_TIME));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String data = rs.getString("Data");
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");

                    String action = determineAction(createdAt, updatedAt);
                    logger.debug("Found record - ID: {}, Data: {}, CreatedAt: {}, UpdatedAt: {}, Action: {}",
                            id, data, createdAt, updatedAt, action);

                    sendMessage(senderClient, table, id, data, createdAt, updatedAt, action);
                }
            }
        }
    }

    private static String determineAction(Timestamp createdAt, Timestamp updatedAt) {
        LocalDateTime createdAtTime = createdAt.toLocalDateTime();
        LocalDateTime updatedAtTime = updatedAt.toLocalDateTime();
        return createdAtTime.equals(updatedAtTime) ? "Inserted" : "Updated";
    }

    private static void sendMessage(ServiceBusSenderClient senderClient, String table, int id, String data, Timestamp createdAt, Timestamp updatedAt, String action) {
        String messageContent = String.format("ID: %d, TableName: %s, Data: %s, CreatedAt: %s, UpdatedAt: %s, Action: %s",
                id, table, data, createdAt, updatedAt, action);

        senderClient.sendMessage(new ServiceBusMessage(messageContent));
        logger.debug("Sent message - ID: {}, TableName: {}, Data: {}, CreatedAt: {}, UpdatedAt: {}, Action: {}",
                id, table, data, createdAt, updatedAt, action);
    }
}
