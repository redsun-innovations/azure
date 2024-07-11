package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.service.HierarchyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This controller provides endpoints for managing and retrieving hierarchy-related data.
 * It allows fetching class code data and retrieving hierarchy data with options to avoid duplicates.
 */

@RestController
@RequestMapping("/v1/hierarchy")
public class HierarchyController {

    private static final Logger logger = LoggerFactory.getLogger(HierarchyController.class);
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
        logger.info("fetchClassCodeData called with classCode: {}", classCode);
        try {
            return hierarchyService.fetchClassCodeData(classCode);
        } catch (Exception e) {
            logger.error("Error occurred while fetching class code data for {}: {}", classCode, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves hierarchy data based on the provided class code and duplication preference.
     *
     * @param classCode       the class code to filter hierarchy data (optional)
     * @param avoidDuplicates whether to avoid duplicate entries (default is true)
     * @return a list of maps containing the hierarchy data
     */

    @GetMapping
    public List<Map<String, Object>> getHierarchyData(
            @RequestParam(required = false) String classCode,
            @RequestParam(required = false, defaultValue = "true") boolean avoidDuplicates) {
        logger.info("getHierarchyData called with classCode: {} and avoidDuplicates: {}", classCode, avoidDuplicates);
        try {
            return hierarchyService.getHierarchyData(classCode, avoidDuplicates);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving hierarchy data: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

}
