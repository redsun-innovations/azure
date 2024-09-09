package org.redsun.service;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;
import org.redsun.config.CosmosDbConfig;
import org.redsun.model.Hierarchy;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class CosmosDbService {

    private final CosmosContainer cosmosContainer;
    private static final String PARTITION_KEY = "hierarchy"; // Directly set the partition key here

    public CosmosDbService() {
        this.cosmosContainer = CosmosDbConfig.getCosmosContainer();
    }

    public List<Hierarchy> getHierarchies() {
        // Construct the query with the partition key directly inserted
        String query = String.format("SELECT c.displayName, c.base36Id FROM c WHERE c.pk = '%s'", PARTITION_KEY);

        // Create the query specification
        SqlQuerySpec querySpec = new SqlQuerySpec(query);

        // Set query request options if needed (e.g., partition key)
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();

        // Execute the query
        CosmosPagedIterable<JsonNode> queryResults = cosmosContainer.queryItems(querySpec, queryOptions, JsonNode.class);

        // Process the results and map them to the Hierarchy model
        List<Hierarchy> hierarchyList = new ArrayList<>();
        for (JsonNode item : queryResults) {
            String displayName = item.get("displayName").asText();
            String base36Id = item.get("base36Id").asText();
            Hierarchy hierarchy = new Hierarchy(displayName, base36Id, PARTITION_KEY);
            hierarchyList.add(hierarchy);
        }

        return hierarchyList;
    }
}
