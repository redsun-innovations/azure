package com.redsun.fetch_data.repository;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import com.redsun.fetch_data.model.FacetGroup;


import com.azure.cosmos.CosmosClientBuilder;

import java.util.*;


@Repository
public class FacetRepository {

    private final CosmosClient cosmosClient;
    private final CosmosDatabase database;
    private final CosmosContainer container;
    private final ObjectMapper objectMapper;

    public FacetRepository(@Value("${azure.cosmos.endpoint}") String endpoint,
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

    public List<Map<String, Object>> searchFacets(List<String> facetTypes, String facetValue) {
        String facetTypesCondition = "'" + String.join("','", facetTypes) + "'";
        String query = "SELECT c.facetType, c.facetTypebase36Id, c.facetValue, c.base36Id " +
                "FROM c " +
                "WHERE c.pk = 'facets' AND c.facetType IN (" + facetTypesCondition + ")";
        if (facetValue != null) {
            query += " AND c.facetValue = '" + facetValue + "'";
        }
    Map<String, Map<String, Object>> groupedFacets = new LinkedHashMap<>();
    CosmosPagedIterable<JsonNode> items = container.queryItems(query, new CosmosQueryRequestOptions(), JsonNode.class);

        items.forEach(item -> {
        String facetType = item.has("facetType") ? item.get("facetType").asText() : null;
        String facetTypeBase36Id = item.has("facetTypebase36Id") ? item.get("facetTypebase36Id").asText() : null;
        String facetValueText = item.has("facetValue") ? item.get("facetValue").asText() : null;
        String base36Id = item.has("base36Id") ? item.get("base36Id").asText() : null;

        Map<String, Object> facetMap;
        if (groupedFacets.containsKey(facetType)) {
            facetMap = groupedFacets.get(facetType);
        } else {
            facetMap = new HashMap<>();
            facetMap.put("facetType", facetType);
            facetMap.put("facetTypebase36Id", facetTypeBase36Id);
            facetMap.put("facetValues", new ArrayList<Map<String, Object>>());
            groupedFacets.put(facetType, facetMap);
        }

        Map<String, Object> facetValueMap = new HashMap<>();
        facetValueMap.put("facetValue", facetValueText);
        facetValueMap.put("base36Id", base36Id);

        List<Map<String, Object>> facetValues = (List<Map<String, Object>>) facetMap.get("facetValues");
        facetValues.add(facetValueMap);
    });

        for (String facetType : facetTypes) {
            if (!groupedFacets.containsKey(facetType)) {
                Map<String, Object> nullFacetMap = new HashMap<>();
                nullFacetMap.put("facetType", facetType);
                nullFacetMap.put("facetTypebase36Id", null);
                List<Map<String, Object>> nullFacetValues = new ArrayList<>();
                Map<String, Object> nullFacetValueMap = new HashMap<>();
                nullFacetValueMap.put("facetValue", null);
                nullFacetValueMap.put("base36Id", null);
                nullFacetValues.add(nullFacetValueMap);
                nullFacetMap.put("facetValues", nullFacetValues);
                groupedFacets.put(facetType, nullFacetMap);
            }
        }

    return new ArrayList<>(groupedFacets.values());
}

    public Map<String, Object> listData(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) {
            pageNumber = 1;
        }
        if (pageSize == null) {
            pageSize = 200;
        }

        List<Map<String, Object>> results = new ArrayList<>();
        int offset = pageSize * (pageNumber - 1);
        String query = "SELECT c.facetType, c.facetTypebase36Id, c.facetValue, c.base36Id FROM c WHERE c.pk = 'facets' OFFSET " + offset + " LIMIT " + pageSize;
        CosmosPagedIterable<JsonNode> items = container.queryItems(query, new CosmosQueryRequestOptions(), JsonNode.class);

        for (JsonNode item : items) {
            Map<String, Object> resultItem = new HashMap<>();
            JsonNode facetTypeNode = item.get("facetType");
            JsonNode facetTypebase36Id = item.get("facetTypebase36Id");
            JsonNode base36IdNode = item.get("base36Id");
            JsonNode facetValueNode = item.get("facetValue");

            if (facetTypeNode != null) {
                resultItem.put("facetType", facetTypeNode.asText());
            }
            if (facetTypebase36Id != null) {
                resultItem.put("facetTypebase36Id", facetTypebase36Id.asText());
            }
            if (base36IdNode != null && facetValueNode != null) {
                List<Map<String, String>> facetValues = new ArrayList<>();
                facetValues.add(Map.of(
                        "base36Id", base36IdNode.asText(),
                        "facetValue", facetValueNode.asText()
                ));
                resultItem.put("facetValues", facetValues);
            }

            results.add(resultItem);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("pageNumber", pageNumber);
        response.put("count", results.size());
        response.put("pageSize", pageSize);
        response.put("data", results);

        return response;
    }
    public void close() {
        cosmosClient.close();
    }
}