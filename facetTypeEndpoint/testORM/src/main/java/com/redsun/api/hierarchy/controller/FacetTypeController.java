package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.service.FacetService;
import com.redsun.api.hierarchy.service.FacetTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/facets")
public class FacetTypeController {

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

    @PostMapping("/addFacetType")
    public ResponseEntity<String> addFacetType(@RequestParam String facetType, @RequestParam String facetTypeDescription) {
        String response = facetTypeService.addFacetType(facetType, facetTypeDescription);
        return ResponseEntity.ok(response);
    }

}
