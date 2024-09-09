package org.redsun.config;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CosmosDbConfig {

    private static CosmosClient cosmosClient;
    private static CosmosDatabase cosmosDatabase;
    private static CosmosContainer cosmosContainer;

    static {
        try (InputStream input = CosmosDbConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
            }
            prop.load(input);

            String cosmosDbUri = prop.getProperty("cosmosdb.uri");
            String primaryKey = prop.getProperty("cosmosdb.primaryKey");
            String databaseName = prop.getProperty("cosmosdb.database");
            String containerName = prop.getProperty("cosmosdb.container");

            cosmosClient = new CosmosClientBuilder()
                    .endpoint(cosmosDbUri)
                    .key(primaryKey)
                    .buildClient();

            cosmosDatabase = cosmosClient.getDatabase(databaseName);
            cosmosContainer = cosmosDatabase.getContainer(containerName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static CosmosClient getCosmosClient() {
        return cosmosClient;
    }

    public static CosmosDatabase getCosmosDatabase() {
        return cosmosDatabase;
    }

    public static CosmosContainer getCosmosContainer() {
        return cosmosContainer;
    }
}
