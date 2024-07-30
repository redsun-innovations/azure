package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.service.FacetService;
import com.redsun.api.hierarchy.service.HierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/group")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final HierarchyService hierarchyService;
    private final FacetService facetService;

    @Autowired
    public GroupController(HierarchyService hierarchyService, FacetService facetService) {
        this.hierarchyService = hierarchyService;
        this.facetService = facetService;
    }

    @GetMapping("/fetchGroupData")
    public Map<String, List<Map<String, Object>>> fetchCombinedData(@RequestParam(value = "base36Ids") String base36Ids) {
        logger.info("fetchCombinedData called with base36Ids: {}", base36Ids);
        Map<String, List<Map<String, Object>>> combinedResults = new HashMap<>();
        try {
            List<Map<String, Object>> hierarchyData = hierarchyService.fetchHierarchyData(base36Ids);
            List<Map<String, Object>> facetData = facetService.fetchFacetData(base36Ids);

            combinedResults.put("hierarchyData", hierarchyData);
            combinedResults.put("facetData", facetData);

            logger.info("fetchCombinedData completed successfully with results: {}", combinedResults);
            return combinedResults;
        } catch (Exception e) {
            logger.error("Error occurred while fetching combined data", e);
            throw e;
        }
    }
}