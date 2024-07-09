package com.redsun.api.hierarchy.repository;


import com.azure.cosmos.CosmosContainer;
import com.redsun.api.hierarchy.constant.Const;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Repository implementation for accessing and managing facets data stored in Azure Cosmos DB.
 * This class provides methods for searching and listing facet data.
 */

@Repository
public class CosmosDbFacetRepository implements FacetRepository {

    private static final Logger logger = LoggerFactory.getLogger(CosmosDbFacetRepository.class);

    private final CosmosContainer container;

    /**
     * Constructor for CosmosDbFacetRepository.
     *
     * @param container the CosmosContainer instance for interacting with Azure Cosmos DB
     */

    public CosmosDbFacetRepository(CosmosContainer container) {
        this.container = container;

    }

    /**
     * Searches for facets based on the provided facet types and facet value.
     *
     * @param facetTypes the list of facet types to search
     * @param facetValue the facet value to search
     * @return a list of maps representing the facets found
     */

    public List<Map<String, Object>> searchFacets(List<String> facetTypes, String facetValue) {
        try {
        String facetTypesCondition = "'" + String.join("','", facetTypes) + "'";
        String query = "SELECT c.facetType, c.facetTypebase36Id, c.facetValue, c.base36Id " +
                "FROM c " +
                "WHERE c.pk = 'facets' AND c.facetType IN (" + facetTypesCondition + ")";
        if (facetValue != null) {
            query += " AND c.facetValue = '" + facetValue + "'";
        }
        Map<String, Map<String, Object>> groupedFacets = retrieveGroupedFacets(query);

        ensureAllFacetTypesPresent(facetTypes, groupedFacets);

        return new ArrayList<>(groupedFacets.values());
    } catch (Exception e) {
            logger.error("Error in searchFacets method: {}", e.getMessage(), e);
            return Collections.emptyList(); // or handle error scenario as per your application logic
        }
    }

    /**
     * Retrieves grouped facets based on the provided query.
     *
     * @param query the query to execute on the Cosmos DB
     * @return a map of grouped facets
     */

    private Map<String, Map<String, Object>> retrieveGroupedFacets(String query) {
        try {
        Map<String, Map<String, Object>> groupedFacets = new LinkedHashMap<>();
        CosmosPagedIterable<JsonNode> items = container.queryItems(query, new CosmosQueryRequestOptions(), JsonNode.class);
        Iterator<JsonNode> iterator = items.iterator();

        while (iterator.hasNext()) {
            JsonNode item = iterator.next();

            String facetType = item.has(Const.FACETTYPE) ? item.get(Const.FACETTYPE).asText() : null;
            String facetTypeBase36Id = item.has(Const.FACETTYPEBASE36ID) ? item.get(Const.FACETTYPEBASE36ID).asText() : null;
            String facetValueText = item.has(Const.FACETVALUE) ? item.get(Const.FACETVALUE).asText() : null;
            String base36Id = item.has(Const.FACETBASE36ID) ? item.get(Const.FACETBASE36ID).asText() : null;

            Map<String, Object> facetMap = groupedFacets.computeIfAbsent(facetType, key -> {
                Map<String, Object> newFacetMap = new HashMap<>();
                newFacetMap.put(Const.FACETTYPE, facetType);
                newFacetMap.put(Const.FACETTYPEBASE36ID, facetTypeBase36Id);
                newFacetMap.put(Const.FACETVALUES, new ArrayList<Map<String, Object>>());
                return newFacetMap;
            });

            if (facetType != null) {
                Map<String, Object> facetValueMap = new HashMap<>();
                facetValueMap.put(Const.FACETVALUE, facetValueText);
                facetValueMap.put(Const.FACETBASE36ID, base36Id);
                List<Map<String, Object>> facetValues = (List<Map<String, Object>>) facetMap.get(Const.FACETVALUES);
                facetValues.add(facetValueMap);
            }
        }

        return groupedFacets;
    } catch (Exception e) {
            logger.error("Error in retrieveGroupedFacets method: {}", e.getMessage(), e);
            return Collections.emptyMap(); // or handle error scenario as per your application logic
        }
    }

    /**
     * Ensures that all provided facet types are present in the grouped facets.
     * If a facet type is not found, it adds a null entry for that facet type.
     *
     * @param facetTypes the list of facet types to check
     * @param groupedFacets the map of grouped facets
     */

    private void ensureAllFacetTypesPresent(List<String> facetTypes, Map<String, Map<String, Object>> groupedFacets) {
       try {
        facetTypes.forEach(facetType -> {
            if (!groupedFacets.containsKey(facetType)) {
                Map<String, Object> nullFacetMap = new HashMap<>();
                nullFacetMap.put(Const.FACETTYPE, facetType);
                nullFacetMap.put(Const.FACETTYPEBASE36ID, null);
                List<Map<String, Object>> nullFacetValues = new ArrayList<>();
                Map<String, Object> nullFacetValueMap = new HashMap<>();
                nullFacetValueMap.put(Const.FACETVALUE, null);
                nullFacetValueMap.put(Const.FACETBASE36ID, null);
                nullFacetValues.add(nullFacetValueMap);
                nullFacetMap.put(Const.FACETVALUES, nullFacetValues);
                groupedFacets.put(facetType, nullFacetMap);
            }
        });
    } catch (Exception e) {
        logger.error("Error in ensureAllFacetTypesPresent method: {}", e.getMessage(), e);
        // Optionally rethrow or handle the exception based on your application's error handling strategy
    }
}

    /**
     * Lists facet data based on the provided page number and page size.
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize the number of items per page
     * @return a map containing the paginated facet data
     */

    public Map<String, Object> listData(Integer pageNumber, Integer pageSize) {
        try {
        if (pageNumber == null) {
            pageNumber = Const.DEFAULTPAGENUMBER;
        }
        if (pageSize == null) {
            pageSize = Const.DEFAULTPAGESIZE;
        }

        int offset = pageSize * (pageNumber - 1);
        String query = "SELECT c.facetType, c.facetTypebase36Id, c.facetValue, c.base36Id FROM c WHERE c.pk = 'facets' OFFSET " + offset + " LIMIT " + pageSize;
        CosmosPagedIterable<JsonNode> items = container.queryItems(query, new CosmosQueryRequestOptions(), JsonNode.class);

        Map<String, Map<String, Object>> groupedFacets = new LinkedHashMap<>();
        Iterator<JsonNode>iterator = items.iterator();

        while(iterator.hasNext()){
            JsonNode item = iterator.next();

            String facetType = item.has(Const.FACETTYPE) ? item.get(Const.FACETTYPE).asText() : null;
            String facetTypebase36Id = item.has(Const.FACETTYPEBASE36ID) ? item.get(Const.FACETTYPEBASE36ID).asText() : null;
            String facetValueText = item.has(Const.FACETVALUE) ? item.get(Const.FACETVALUE).asText() : null;
            String base36Id = item.has(Const.FACETBASE36ID) ? item.get(Const.FACETBASE36ID).asText() : null;

            String uniqueKey = facetType + "-" + facetTypebase36Id;

            Map<String, Object> facetMap;
            if (groupedFacets.containsKey(uniqueKey)) {
                facetMap = groupedFacets.get(uniqueKey);
            } else {
                facetMap = new HashMap<>();
                facetMap.put(Const.FACETTYPE, facetType);
                facetMap.put(Const.FACETTYPEBASE36ID, facetTypebase36Id);
                facetMap.put(Const.FACETVALUES, new ArrayList<Map<String, Object>>());
                groupedFacets.put(uniqueKey, facetMap);
            }

            Map<String, Object> facetValueMap = new HashMap<>();
            facetValueMap.put(Const.FACETVALUE, facetValueText);
            facetValueMap.put(Const.FACETBASE36ID, base36Id);

            List<Map<String, Object>> facetValues = (List<Map<String, Object>>) facetMap.get(Const.FACETVALUES);
            facetValues.add(facetValueMap);
        }

        List<Map<String, Object>> results = new ArrayList<>(groupedFacets.values());

        Map<String, Object> response = new HashMap<>();
        response.put("pageNumber", pageNumber);
        response.put("count", results.size());
        response.put("pageSize", pageSize);
        response.put("data", results);

        return response;
    } catch (Exception e) {
        logger.error("Error in listData method: {}", e.getMessage(), e);
        // Optionally rethrow or handle the exception based on your application's error handling strategy
        return Collections.emptyMap(); // Or handle the error with appropriate response
        }
    }
}
