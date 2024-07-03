package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.service.HierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * This controller provides endpoints for managing and retrieving hierarchy-related data.
 * It allows fetching class code data and retrieving hierarchy data with options to avoid duplicates.
 */

@RestController
@RequestMapping("/v1/hierarchy")
public class HierarchyController {
    private final HierarchyService hierarchyService;

    /**
     * Constructor for HierarchyController.
     *
     * @param hierarchyService the service to handle hierarchy operations
     */
    @Autowired
    public HierarchyController(HierarchyService hierarchyService) {
        this.hierarchyService = hierarchyService;
    }

    /**
     * Fetches data associated with a given class code.
     *
     * @param classCode the class code to fetch data for
     * @return a list of maps containing the class code data
     */
    @GetMapping("/class-code/{classCode}")
    public List<Map<String, Object>> fetchClassCodeData(@PathVariable String classCode) {
        return hierarchyService.fetchClassCodeData(classCode);
    }
    /**
     * Retrieves hierarchy data based on the provided class code and duplication preference.
     *
     * @param classCode the class code to filter hierarchy data (optional)
     * @param avoidDuplicates whether to avoid duplicate entries (default is true)
     * @return a list of maps containing the hierarchy data
     */

    @GetMapping
    public List<Map<String, Object>> getHierarchyData(
            @RequestParam(required = false) String classCode,
            @RequestParam(required = false, defaultValue = "true") boolean avoidDuplicates) {
        return hierarchyService.getHierarchyData(classCode, avoidDuplicates);
    }
}
