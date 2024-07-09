package com.redsun.api.hierarchy.repository;


import com.azure.cosmos.CosmosContainer;
import com.redsun.api.hierarchy.constant.Const;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.redsun.api.hierarchy.model.FacetEntity;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository implementation for accessing and managing facets data stored in Azure Cosmos DB.
 * This class provides methods for searching and listing facet data.
 */

public class CosmosDbFacetRepository {

    private final CosmosContainer container;


    /**
     * Constructor for CosmosDbFacetRepository.
     *
     * @param container the CosmosContainer instance for interacting with Azure Cosmos DB
     */

    public CosmosDbFacetRepository(CosmosContainer container, FacetRepository repository) {
        this.container = container;

        this.repository = repository;
    }


    @Autowired
    private final FacetRepository repository;
    /**
     * Searches for facets based on the provided facet types and facet value.
     *
     * @param facetTypes the list of facet types to search
     * @param facetValue the facet value to search
     * @return a list of maps representing the facets found
     */

    public List<Map<String, Object>> searchFacets(List<String> facetTypes, String facetValue) {
//        String facetTypesCondition = facetTypes.stream()
//                .map(facet -> "'" + facet + "'")
//                .collect(Collectors.joining(","));
//        String facetTypesCondition = "'" + String.join("','", facetTypes) + "'";
        List<FacetEntity> facets = repository.searchFacets(facetTypes, facetValue);

//
        Map<String, Map<String, Object>> groupedFacets = retrieveGroupedFacets(facets.toString());

        ensureAllFacetTypesPresent(facetTypes, groupedFacets);

        return new ArrayList<>(groupedFacets.values());
    }

    /**
     * Retrieves grouped facets based on the provided query.
     *
     * @param query the query to execute on the Cosmos DB
     * @return a map of grouped facets
     */


    private Map<String, Map<String, Object>> retrieveGroupedFacets(String query) {
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
    }

    /**
     * Ensures that all provided facet types are present in the grouped facets.
     * If a facet type is not found, it adds a null entry for that facet type.
     *
     * @param facetTypes the list of facet types to check
     * @param groupedFacets the map of grouped facets
     */

    private void ensureAllFacetTypesPresent(List<String> facetTypes, Map<String, Map<String, Object>> groupedFacets) {
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
    }

    /**
     * Lists facet data based on the provided page number and page size.
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize the number of items per page
     * @return a map containing the paginated facet data
     */

    public Map<String, Object> listData(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) {
            pageNumber = 1;
        }
        if (pageSize == null) {
            pageSize = 200;
        }

        int offset = pageSize * (pageNumber - 1);
        List<FacetEntity> facets = repository.listData(offset, pageSize);
        CosmosPagedIterable<JsonNode> items = container.queryItems(facets.toString(), new CosmosQueryRequestOptions(), JsonNode.class);

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
    }


}
