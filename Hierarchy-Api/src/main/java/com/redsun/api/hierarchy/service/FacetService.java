package com.redsun.api.hierarchy.service;

import com.redsun.api.hierarchy.constant.Constant;
import com.redsun.api.hierarchy.repository.FacetRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class for managing and querying facets within the hierarchy.
 * <p>
 * This service provides methods for searching facets based on their types and values,
 * as well as listing data with pagination support.
 */
@Service
public class FacetService {

    private static final Logger logger = LoggerFactory.getLogger(FacetService.class);
    private final FacetRepository facetRepository;

    /**
     * Constructor for FacetService.
     * <p>
     * Initializes the service with the given FacetRepository.
     *
     * @param facetRepository the repository used for querying facets
     */
    @Autowired
    public FacetService(FacetRepository facetRepository) {
        this.facetRepository = facetRepository;
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
            errorResponse.put(Constant.ERROR, "The query parameter facetType is missing");
            response.add(errorResponse);
            return response;
        }
        try {
            List<Map<String, Object>> facets = facetRepository.searchFacets(facetTypes, facetValue);

            for (Map<String, Object> facet : facets) {
                Map<String, Object> formattedFacet = new HashMap<>();
                formattedFacet.put(Constant.FACETTYPE, facet.get(Constant.FACETTYPE));
                formattedFacet.put(Constant.FACETTYPEBASE36ID, facet.get(Constant.FACETTYPEBASE36ID));
                formattedFacet.put(Constant.FACETVALUES, facet.get(Constant.FACETVALUES));

                response.add(formattedFacet);
            }
        } catch (Exception e) {
            logger.error("Error occurred while searching facets: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(Constant.ERROR, "An error occurred while fetching facets. Please try again later.");
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
            Map<String, Object> data = facetRepository.listData(pageNumber, pageSize);
            int count = (int) data.getOrDefault("count", 0);
            int calculatedPageSize = (int) data.getOrDefault("pageSize", Constant.DEFAULTPAGESIZE);

            response.put("pageNumber", pageNumber);
            response.put("count", count);
            response.put("pageSize", calculatedPageSize);
            response.put("data", data.get("data"));
        } catch (Exception e) {
            logger.error("Error occurred while listing data: {}", e.getMessage(), e);
            response.clear();
            response.put(Constant.ERROR, "An error occurred while fetching data. Please try again later.");
        }
        return response;
    }
}
