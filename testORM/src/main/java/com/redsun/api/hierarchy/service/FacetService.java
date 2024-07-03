package com.redsun.api.hierarchy.service;

import com.redsun.api.hierarchy.model.FacetEntity;
import com.redsun.api.hierarchy.repository.CosmosRepositoryExtends;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FacetService {
    private final CosmosRepositoryExtends facetRepository;

    @Autowired
    public FacetService(CosmosRepositoryExtends facetRepository) {
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

        List<FacetEntity> facets = facetRepository.findByFacetTypeInAndFacetValue(facetTypes, facetValue);
        System.out.println("facetservice" + facets);

        // Add logic to format facets if needed
        for (FacetEntity facet : facets) {
            Map<String, Object> formattedFacet = new HashMap<>();
            formattedFacet.put("facetType", facet.getFacetType());
            formattedFacet.put("facetTypebase36Id", facet.getFacetTypebase36Id());
            formattedFacet.put("facetValue", facet.getFacetValue());
            formattedFacet.put("base36Id", facet.getBase36Id());
            response.add(formattedFacet);
        }

        return response;
    }

    public Map<String, Object> listData(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 200;
        }

        int offset = (pageNumber - 1) * pageSize;
        List<FacetEntity> facets = facetRepository.findPaginatedFacets(offset, pageSize);
        System.out.println("Paginated facets: " + facets);

        List<Map<String, Object>> results = new ArrayList<>();
        for (FacetEntity facet : facets) {
            Map<String, Object> formattedFacet = new HashMap<>();
            formattedFacet.put("facetType", facet.getFacetType());
            formattedFacet.put("facetTypebase36Id", facet.getFacetTypebase36Id());
            formattedFacet.put("facetValue", facet.getFacetValue());
            formattedFacet.put("base36Id", facet.getBase36Id());
            results.add(formattedFacet);
        }

        int count = facets.size();  // Ideally, count should come from a separate query or method
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("pageNumber", pageNumber);
        response.put("count", count);
        response.put("pageSize", pageSize);
        response.put("data", results);

        return response;
    }
}
