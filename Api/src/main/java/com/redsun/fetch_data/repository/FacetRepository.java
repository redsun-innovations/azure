package com.redsun.fetch_data.repository;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import com.redsun.fetch_data.model.FacetGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Repository
public class FacetRepository {

    private final CosmosClient cosmosClient;
    private final CosmosDatabase database;
    private final CosmosContainer container;
    private final ObjectMapper objectMapper;

    public FacetRepository(@Value("${azure.cosmos.endpoint}") String endpoint,
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

    public List<String> getQueryData(String facetType, String facetValue) {
        String query = "SELECT c.facetTypebase36Id FROM c WHERE c.facetType = '" + facetType + "' AND c.facetValue = '" + facetValue + "'";
        return executeBase36IdQuery(query);
    }

    public List<String> listQueryData(List<FacetGroup> facetGroups) {
        String query = "SELECT c.facetTypebase36Id FROM c WHERE " +
                facetGroups.stream()
                        .map(group -> "(c.facetType = '" + group.getFacetType() + "' AND c.facetValue = '" + group.getFacetValue() + "')")
                        .collect(Collectors.joining(" OR "));
        return executeBase36IdQuery(query);
    }

    public List<String> searchQueryData(String facetType) {
        String query = "SELECT c.facetTypebase36Id FROM c WHERE c.facetType = '" + facetType + "'";
        return executeBase36IdQuery(query);
    }

    private List<String> executeBase36IdQuery(String query) {
        List<String> base36Ids = new ArrayList<>();
        try {
            CosmosPagedIterable<JsonNode> items = container.queryItems(query, new CosmosQueryRequestOptions(), JsonNode.class);
            for (JsonNode item : items) {
                if (item.has("facetTypebase36Id")) {
                    base36Ids.add(item.get("facetTypebase36Id").asText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base36Ids;
    }
    public void close() {
        cosmosClient.close();
    }
}