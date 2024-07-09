package com.redsun.api.hierarchy.service;

import com.redsun.api.hierarchy.repository.FacetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.redsun.api.hierarchy.model.FacetEntity;

import java.util.*;

@Service
public class FacetService {
    private final FacetRepository facetRepository;

    @Autowired
    public FacetService(FacetRepository facetRepository) {
        this.facetRepository = facetRepository;
    }


    public List<Map<String, Object>> searchFacets(List<String> facetTypes, String facetValue) {
        List<Map<String, Object>> response = new ArrayList<>();

        if (facetTypes == null || facetTypes.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "The query parameter facetType is missing");
            response.add(errorResponse);
            return response;
        }

        List<FacetEntity> facets = facetRepository.searchFacets(facetTypes, facetValue);

        for (FacetEntity facet : facets) {
            Map<String, Object> formattedFacet = new HashMap<>();
            formattedFacet.put("facetType", facet.getFacetType());
            formattedFacet.put("facetTypebase36Id", facet.getFacetTypebase36Id());

            // Assuming facetValues is a list of maps containing base36Id and facetValue
            List<Map<String, Object>> facetValues = new ArrayList<>();
            Map<String, Object> facetValueMap = new HashMap<>();
            facetValueMap.put("base36Id", facet.getBase36Id());
            facetValueMap.put("facetValue", facet.getFacetValue());
            facetValues.add(facetValueMap);

            formattedFacet.put("facetValues", facetValues);

            response.add(formattedFacet);
        }

        return response;
    }

    public Map<String, Object> listData(Integer pageNumber, Integer pageSize) {
        Map<String, Object> response = new LinkedHashMap<>();

        List<FacetEntity> data = facetRepository.listData(pageNumber, pageSize);

        int count = data.size();  // Assuming count is the size of the list
        int calculatedPageSize = pageSize;  // Use the passed pageSize parameter

        response.put("pageNumber", pageNumber);
        response.put("count", count);
        response.put("pageSize", calculatedPageSize);
        response.put("data", data);  // Add the entire list of FacetEntity objects

        return response;
    }
}