package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.service.FacetService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * This controller provides endpoints for managing and retrieving facet-related data.
 * It allows searching for facets and listing facet data with pagination support.
 */
@RestController
@RequestMapping("/v1/facets")
public class FacetController {

    private static final Logger logger = LoggerFactory.getLogger(FacetController.class);
    private final FacetService facetService;

    /**
     * Constructor for FacetController.
     *
     * @param facetService the service to handle facet operations
     */

    @Autowired
    public FacetController(FacetService facetService) {
        this.facetService = facetService;
    }

    /**
     * Searches for facets based on the provided facet types and facet value.
     *
     * @param facetTypes the types of facets to search for (optional)
     * @param facetValue the value of the facet to search for (optional)
     * @return a list of facets matching the search criteria
     */

    @GetMapping
    public List<Map<String, Object>> searchFacets(
            @RequestParam(value = "facetType", required = false) List<String> facetTypes,
            @RequestParam(value = "facetValue", required = false) String facetValue) {
        logger.info("searchFacets called with facetTypes: {} and facetValue: {}", facetTypes, facetValue);
        try {
            List<Map<String, Object>> results = facetService.searchFacets(facetTypes, facetValue);
            logger.info("Facetsdata retrived successfully with results");
            return results;
        } catch (Exception e) {
            logger.error("Error occurred while searching facets", e);
            return Collections.emptyList();
        }
    }

    /**
     * Lists facet data with pagination support.
     *
     * @param pageNumber the page number to retrieve (default is 1)
     * @param pageSize   the number of items per page (default is 200)
     * @return a map containing the paginated facet data
     */

    @GetMapping("/list")
    public Map<String, Object> listData(
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "200") int pageSize) {
        logger.info("listData called with pageNumber: {} and pageSize: {}", pageNumber, pageSize);
        try {
            Map<String, Object> results = facetService.listData(pageNumber, pageSize);
            logger.info("ListData retrived successfully with results");
            return results;
        } catch (Exception e) {
            logger.error("Error occurred while listing facet data: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }
}