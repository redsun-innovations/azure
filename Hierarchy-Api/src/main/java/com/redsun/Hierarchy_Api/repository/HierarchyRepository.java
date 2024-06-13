package com.redsun.Hierarchy_Api.repository;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;


@Repository
public class HierarchyRepository {
    private final CosmosClient cosmosClient;
    private final CosmosDatabase database;
    private final CosmosContainer container;
    private final ObjectMapper objectMapper;

    public HierarchyRepository(@Value("${azure.cosmos.endpoint}") String endpoint,
                               @Value("${azure.cosmos.key}") String key,
                               @Value("${azure.cosmos.database}") String databaseName,
                               @Value("${azure.cosmos.container}") String containerName,
                               @Value("${azure.cosmos.connection-mode}") String connectionMode) {

        cosmosClient = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .gatewayMode()
                .buildClient();

        database = cosmosClient.getDatabase(databaseName);
        container = database.getContainer(containerName);
        objectMapper = new ObjectMapper();
    }

    public List<Map<String, Object>> fetchHierarchyData(String classCode, boolean avoidDuplicates) {
        StringBuilder queryBuilder = new StringBuilder("SELECT c.displayName, c.classCode, c.base36Id, c.path FROM c WHERE c.pk = 'hierarchy'");
        queryBuilder.append(" AND c.classCode = '").append(classCode).append("'");

        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        CosmosPagedIterable<JsonNode> items = container.queryItems(queryBuilder.toString(), options, JsonNode.class);

        List<Map<String, Object>> results = new ArrayList<>();
        Set<String> uniqueParentIds = new HashSet<>();

        if (!items.iterator().hasNext()) {
            Map<String, Object> result = new HashMap<>();
            result.put("classCode", classCode);

            result.put("hierarchyValues", new ArrayList<Map<String, Object>>());

            Map<String, Object> hierarchyValue = new HashMap<>();
            hierarchyValue.put("base36Id", null);
            hierarchyValue.put("parentBase36Id", null);

            ((List<Map<String, Object>>) result.get("hierarchyValues")).add(hierarchyValue);
            results.add(result);

            return results;
        }

        items.forEach(item -> {
            String itemDisplayName = item.get("displayName").asText();
            String itemPath = item.get("path").asText();
            String itemClassCode = item.get("classCode").asText();

            String base36Id = item.get("base36Id").asText();
            String path = item.has("path") ? item.get("path").asText() : null;

            String parentBase36Id = getParentBase36Id(path);

            if (avoidDuplicates && !uniqueParentIds.add(parentBase36Id)) {
                return;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("displayName", itemDisplayName);
            result.put("path", itemPath);
            result.put("classCode", itemClassCode);
            result.put("hierarchyValues", new ArrayList<Map<String, Object>>());

            Map<String, Object> hierarchyValue = new HashMap<>();
            hierarchyValue.put("base36Id", base36Id);
            hierarchyValue.put("parentBase36Id", parentBase36Id);

            ((List<Map<String, Object>>) result.get("hierarchyValues")).add(hierarchyValue);
            results.add(result);
        });

        return results;
    }

    public List<Map<String, Object>> fetchHierarchyData(String classCode) {
        return fetchHierarchyData(classCode, true);
    }

    private String getParentBase36Id(String fullPath) {
        if (fullPath == null) {
            return null;
        }

        String[] pathElements = fullPath.split("/");
        String parentDisplayName;

        if (pathElements.length == 1) {
            parentDisplayName = pathElements[0];
        } else {
            parentDisplayName = pathElements[pathElements.length - 1];
        }

        SqlQuerySpec parentQuerySpec = new SqlQuerySpec(
                "SELECT c.base36Id FROM c WHERE c.pk = 'hierarchy' AND c.displayName = @displayName",
                new SqlParameter("@displayName", parentDisplayName)
        );

        CosmosPagedIterable<JsonNode> parentItems = container.queryItems(parentQuerySpec, new CosmosQueryRequestOptions(), JsonNode.class);
        if (parentItems.iterator().hasNext()) {
            return parentItems.iterator().next().get("base36Id").asText();
        }
        return null;
    }


    public List<Map<String, Object>> getAllHierarchyData(List<String> classCodes, boolean avoidDuplicates) {
        StringBuilder queryBuilder = new StringBuilder("SELECT c.classCode, c.base36Id FROM c WHERE c.pk = 'hierarchy'");

        if (classCodes != null && !classCodes.isEmpty()) {
            String classCodeFilter = classCodes.stream()
                    .map(code -> "'" + code + "'")
                    .collect(Collectors.joining(", "));
            queryBuilder.append(" AND c.classCode IN (").append(classCodeFilter).append(")");
        }

        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        CosmosPagedIterable<JsonNode> items = container.queryItems(queryBuilder.toString(), options, JsonNode.class);

        Map<String, String> base36IdMap = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();

        items.forEach(item -> {
            String itemClassCode = item.get("classCode").asText();
            String base36Id = item.get("base36Id").asText();

            if (avoidDuplicates && base36IdMap.containsKey(itemClassCode)) {
                return;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("classCode", itemClassCode);
            result.put("base36Id", base36Id);

            results.add(result);

            base36IdMap.put(itemClassCode, base36Id);
        });

        for (String classCode : classCodes) {
            if (!base36IdMap.containsKey(classCode)) {
                Map<String, Object> result = new HashMap<>();
                result.put("classCode", classCode);
                result.put("base36Id", null);
                results.add(result);
            }
        }

        return results;
    }

}


