package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.service.FacetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/facets")
public class FacetController {

    private final FacetService facetService;

    @Autowired
    public FacetController(FacetService facetService) {
        this.facetService = facetService;
    }

    @GetMapping
    public List<Map<String, Object>> searchFacets(
            @RequestParam(value = "facetType", required = false) List<String> facetTypes,
            @RequestParam(value = "facetValue", required = false) String facetValue) {
        return facetService.searchFacets(facetTypes, facetValue);
    }

    @GetMapping("/list")
    public Map<String, Object> listData(
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "200") int pageSize) {
        return facetService.listData(pageNumber, pageSize);
    }


}