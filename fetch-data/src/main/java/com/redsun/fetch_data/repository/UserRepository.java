package com.redsun.fetch_data.repository;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import com.redsun.fetch_data.model.User;


@Repository
public class UserRepository {

    private final CosmosClient cosmosClient;
    private final CosmosDatabase database;
    private final CosmosContainer container;
    private final ObjectMapper objectMapper;

    public UserRepository(@Value("${azure.cosmos.endpoint}") String endpoint,
                          @Value("${azure.cosmos.key}") String key,
                          @Value("${azure.cosmos.database}") String databaseName,
                          @Value("${azure.cosmos.container}") String containerName) {

        cosmosClient = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .buildClient();

        database = cosmosClient.getDatabase(databaseName);
        container = database.getContainer(containerName);
        objectMapper = new ObjectMapper();
    }

    public String fetchAllData(String path, String displayName) {
        String query = "SELECT * FROM c WHERE c.path = '" + path + "' AND c.displayName = '" + displayName + "'";
        return executeQuery(query);
    }

    private String executeQuery(String query) {
        StringBuilder result = new StringBuilder();
        try {
            CosmosPagedIterable<JsonNode> items = container.queryItems(query, new CosmosQueryRequestOptions(), JsonNode.class);
            for (JsonNode item : items) {
                result.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(item)).append("\n");
            }
        } catch (Exception e) {
            result.append("Error executing query: ").append(e.getMessage());
        }
        return result.toString();
    }

    public void close() {
        cosmosClient.close();
    }
}