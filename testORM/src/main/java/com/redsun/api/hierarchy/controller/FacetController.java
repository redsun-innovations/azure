package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.service.FacetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * This controller provides endpoints for managing and retrieving facet-related data.
 * It allows searching for facets and listing facet data with pagination support.
 */
@RestController
@RequestMapping("/v1/facets")
public class FacetController {

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
        return facetService.searchFacets(facetTypes, facetValue);
    }

    /**
     * Lists facet data with pagination support.
     *
     * @param pageNumber the page number to retrieve (default is 1)
     * @param pageSize the number of items per page (default is 200)
     * @return a map containing the paginated facet data
     */

    @GetMapping("/list")
    public Map<String, Object> listData(
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "200") int pageSize) {
        return facetService.listData(pageNumber, pageSize);
    }


}