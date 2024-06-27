package com.redsun.api.hierarchy.repository;


import com.azure.cosmos.CosmosContainer;
import com.redsun.api.hierarchy.constant.Const;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;


import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class CosmosDbFacetRepository implements FacetRepository {

    private final CosmosContainer container;


    public CosmosDbFacetRepository(CosmosContainer container) {
        this.container = container;

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
        Iterator<JsonNode>iterator = items.iterator();

        while(iterator.hasNext()){
            JsonNode item = iterator.next();


            String facetType = item.has(Const.FACETTYPE) ? item.get(Const.FACETTYPE).asText() : null;
            String facetTypeBase36Id = item.has(Const.FACETTYPEBASE36ID) ? item.get(Const.FACETTYPEBASE36ID).asText() : null;
            String facetValueText = item.has(Const.FACETVALUE) ? item.get(Const.FACETVALUE).asText() : null;
            String base36Id = item.has(Const.FACETBASE36ID) ? item.get(Const.FACETBASE36ID).asText() : null;

            Map<String, Object> facetMap;
            if (groupedFacets.containsKey(facetType)) {
                facetMap = groupedFacets.get(facetType);
            } else {
                facetMap = new HashMap<>();
                facetMap.put(Const.FACETTYPE, facetType);
                facetMap.put(Const.FACETTYPEBASE36ID, facetTypeBase36Id);
                facetMap.put(Const.FACETVALUES, new ArrayList<Map<String, Object>>());
                groupedFacets.put(facetType, facetMap);
            }

            Map<String, Object> facetValueMap = new HashMap<>();
            facetValueMap.put(Const.FACETVALUE, facetValueText);
            facetValueMap.put(Const.FACETBASE36ID, base36Id);

            List<Map<String, Object>> facetValues = (List<Map<String, Object>>) facetMap.get(Const.FACETVALUES);
            facetValues.add(facetValueMap);
        }

        facetTypes.forEach(facetType -> {
            groupedFacets.computeIfAbsent(facetType, key -> {
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
                return nullFacetMap;
            });
        });

        return new ArrayList<>(groupedFacets.values());
    }
    public Map<String, Object> listData(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) {
            pageNumber = 1;
        }
        if (pageSize == null) {
            pageSize = 200;
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
    }


}
