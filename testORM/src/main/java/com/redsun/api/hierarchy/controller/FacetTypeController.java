package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.entity.FacetTypeEntity;
import com.redsun.api.hierarchy.service.FacetTypeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http. ResponseEntity;
import org.springframework.web.bind.annotation. PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/facets")
public class FacetTypeController {

    private static final Logger logger = LoggerFactory.getLogger(FacetTypeController.class);
    private final FacetTypeService facetTypeService;

    /**
     * Constructor for FacetController.
     *
     * @param facetTypeService the service to handle facet operations
     */

    @Autowired
    public FacetTypeController(FacetTypeService facetTypeService) {
        this.facetTypeService = facetTypeService;
    }

    @PostMapping("/addFacets")
    public ResponseEntity<String> addFacetType(@RequestBody FacetTypeEntity addFacet) {
        try {
            String facetType = addFacet.getFacetType();
            String facetValue = addFacet.getFacetValue();
            String response = facetTypeService.addFacetType(facetType, facetValue);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating facet type entity: {}", e.getMessage(), e); throw e;
        }
    }

}
