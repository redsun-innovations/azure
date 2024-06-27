package com.redsun.api.hierarchy.service;

import com.redsun.api.hierarchy.repository.FacetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        List<Map<String, Object>> facets = facetRepository.searchFacets(facetTypes, facetValue);

        for (Map<String, Object> facet : facets) {
            Map<String, Object> formattedFacet = new HashMap<>();
            formattedFacet.put("facetType", facet.get("facetType"));
            formattedFacet.put("facetTypebase36Id", facet.get("facetTypebase36Id"));
            formattedFacet.put("facetValues", facet.get("facetValues"));

            response.add(formattedFacet);
        }

        return response;
    }

    public Map<String, Object> listData(Integer pageNumber, Integer pageSize) {
        Map<String, Object> data = facetRepository.listData(pageNumber, pageSize);
        int count = (int) data.getOrDefault("count", 0);
        int calculatedPageSize = (int) data.getOrDefault("pageSize", 200);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("pageNumber", pageNumber);
        response.put("count", count);
        response.put("pageSize", calculatedPageSize);
        response.put("data", data.get("data"));

        return response;
    }
}