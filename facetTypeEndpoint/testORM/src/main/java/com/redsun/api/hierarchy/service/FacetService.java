package com.redsun.api.hierarchy.service;

import com.redsun.api.hierarchy.constant.Const;
import com.redsun.api.hierarchy.entity.FacetEntity;
import com.redsun.api.hierarchy.entity.FacetTypeEntity;
import com.redsun.api.hierarchy.repository.CosmosDbFacetRepository;
import com.redsun.api.hierarchy.repository.FacetRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing and querying facets within the hierarchy.
 * <p>
 * This service provides methods for searching facets based on their types and values,
 * as well as listing data with pagination support.
 */
@Service
public class FacetService {

    private static final Logger logger = LoggerFactory.getLogger(FacetService.class);
    private final CosmosDbFacetRepository cosmosDbFacetRepository;

    /**
     * Constructor for FacetService.
     * <p>
     * Initializes the service with the given FacetRepository.
     *
     * @param cosmosDbFacetRepository the repository used for querying facets
     */
    @Autowired
    public FacetService(CosmosDbFacetRepository cosmosDbFacetRepository) {
        this.cosmosDbFacetRepository = cosmosDbFacetRepository;
    }

    /**
     * Searches for facets based on the provided facet types and facet value.
     * <p>
     * This method returns a list of facets matching the specified types and value.
     * If the facet types are missing or empty, an error response is returned.
     *
     * @param facetTypes a list of facet types to search for
     * @param facetValue the value of the facets to search for
     * @return a list of maps representing the found facets, or an error response if the facet types are missing
     */
    public List<Map<String, Object>> searchFacets(List<String> facetTypes, String facetValue) {
        List<Map<String, Object>> response = new ArrayList<>();

        if (facetTypes == null || facetTypes.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "The query parameter facetType is missing");
            response.add(errorResponse);
            return response;
        }
        try {
            List<Map<String, Object>> facets = cosmosDbFacetRepository.searchFacets(facetTypes, facetValue);

            for (Map<String, Object> facet : facets) {
                Map<String, Object> formattedFacet = new HashMap<>();
                formattedFacet.put(Const.FACETTYPE, facet.get(Const.FACETTYPE));
                formattedFacet.put(Const.FACETTYPEBASE36ID, facet.get(Const.FACETTYPEBASE36ID));
                formattedFacet.put(Const.FACETVALUES, facet.get(Const.FACETVALUES));

                response.add(formattedFacet);
            }
        } catch (Exception e) {
            logger.error("Error occurred while searching facets: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while fetching facets. Please try again later.");
            response.add(errorResponse);
        }

        return response;
    }

    /**
     * Lists data with pagination support.
     * <p>
     * This method fetches data from the repository based on the specified page number and page size,
     * and returns the paginated data along with the total count and actual page size used.
     *
     * @param pageNumber the page number to fetch
     * @param pageSize the size of the page to fetch
     * @return a map containing the paginated data, or an error response if an error occurs
     */
    public Map<String, Object> listData(Integer pageNumber, Integer pageSize) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String,Object>data = cosmosDbFacetRepository.listData(pageNumber, pageSize);
            int count = (int) data.getOrDefault("count", 0);
            int calculatedPageSize = (int) data.getOrDefault("pageSize", 200);

            response.put("pageNumber", pageNumber);
            response.put("count", count);
            response.put("pageSize", calculatedPageSize);
            response.put("data", data.get("data"));
        } catch (Exception e) {
            logger.error("Error occurred while listing data: {}", e.getMessage(), e);
            response.clear();
            response.put("error", "An error occurred while fetching data. Please try again later.");
        }
        return response;
    }


    public List<Map<String, Object>> fetchFacetData(String base36Ids) {
        List<String> base36IdList = Arrays.stream(base36Ids.split("Z"))
                .collect(Collectors.toList());

        try {
            List<Map<String, Object>> results = cosmosDbFacetRepository.findFacetByBase36Ids(base36IdList);
            return results;
        } catch (Exception e) {
            logger.error("Error occurred while fetching facet data", e.getMessage(), e);
            return Collections.emptyList();
        }
    }



}
