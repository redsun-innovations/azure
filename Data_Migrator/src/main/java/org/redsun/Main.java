package org.redsun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.sql.*;
import org.redsun.DataProcess.DataFetcher;

public class Main {
    private static final Logger Logger = LoggerFactory.getLogger(Main.class);
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")){
            properties.load(input);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private static final String DATABASE_URL = properties.getProperty("db.connection.string");
    public static final String SERVICE_BUS_CONNECTION_STRING = properties.getProperty("service.bus.connection.string");
    public static final String Topic_Name = properties.getProperty("service.bus.topic.name");
    private static final String USERNAME = properties.getProperty("db.username");
    private static final String PASSWORD = properties.getProperty("db.password");

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD)) {
            if (conn != null) {
                System.out.println("Connected to database");
                DataFetcher.fetchAndStoreData(conn, SERVICE_BUS_CONNECTION_STRING, Topic_Name);
            } else {
                System.out.println("Failed to connect to the database");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception:" + e.getMessage());
            e.printStackTrace();
        }

    }
}