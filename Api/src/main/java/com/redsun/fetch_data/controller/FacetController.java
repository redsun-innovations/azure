package com.redsun.fetch_data.controller;

import com.redsun.fetch_data.model.FacetGroup;
import com.redsun.fetch_data.service.FacetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/facet")
public class FacetController {

    @Autowired
    private FacetService facetService;

    @GetMapping("/get")
    public List<String> getQueryData(@RequestParam String facetType, @RequestParam String facetValue) {
        return facetService.getQueryData(facetType, facetValue);
    }

    @PostMapping("/list")
    public List<String> listQueryData(@RequestBody List<FacetGroup> facetGroups) {
        return facetService.listQueryData(facetGroups);
    }


    @GetMapping("/search")
    public List<String> searchQueryData(@RequestParam("facetType") String facetType) {
        return facetService.searchQueryData(facetType);
    }
}