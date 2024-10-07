package com.api.data_migrator.scheduler;

import com.api.data_migrator.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class ScheduledTask {

    @Autowired
    private NodeService nodeService;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${azure.servicebus.connection-string}")
    private String serviceBusConnectionString;

    @Value("${azure.servicebus.topic-name}")
    private String topicName;

    @Scheduled(cron = "0 0 * * * *")
    public void runDataFetcher() {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            nodeService.processAndSendData(conn, serviceBusConnectionString, topicName);
        } catch (SQLException e) {
            e.printStackTrace(); // Use logger in production
        }
    }
}