package com.redsun.api.hierarchy.service;

import com.redsun.api.hierarchy.constant.Const;
import com.redsun.api.hierarchy.entity.HierarchyEntity;
import com.redsun.api.hierarchy.repository.CosmosDbHierarchyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Service class for managing and querying hierarchy data.
 * <p>
 * This service provides methods for fetching class code data and hierarchical data,
 * with support for avoiding duplicates and handling error responses.
 */
@Service
public class HierarchyService {

    private static final Logger logger = LoggerFactory.getLogger(HierarchyService.class);
    private final CosmosDbHierarchyRepository cosmosDbHierarchyRepository;

    /**
     * Constantructor for HierarchyService.
     * <p>
     * Initializes the service with the given HierarchyRepository.
     *
     * @param cosmosDbHierarchyRepository the repository used for querying hierarchy data
     */
    @Autowired
    public HierarchyService(CosmosDbHierarchyRepository cosmosDbHierarchyRepository) {
        this.cosmosDbHierarchyRepository = cosmosDbHierarchyRepository;
    }

    /**
     * Fetches data for a given class code.
     * <p>
     * This method returns a list of data maps matching the specified class code.
     * If the class code is missing or empty, an error response is returned.
     *
     * @param classCode the class code to fetch data for
     * @return a list of maps representing the fetched data, or an error response if the class code is missing
     */
    public List<Map<String, Object>> fetchClassCodeData(String classCode) {
        List<Map<String, Object>> response = new ArrayList<>();

        try {
            if (classCode == null || classCode.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "ClassCodes list is not provided");
                response.add(errorResponse);
                return response;
            }

            return cosmosDbHierarchyRepository.fetchClassCodeData(classCode);
        } catch (Exception e) {
            logger.error("Error occurred while fetching class code data: {}", e.getMessage(), e);
            response.clear();
            response.add(Collections.singletonMap("error", "An error occurred while fetching class code data. Please try again later."));

            return response;
        }
    }

    /**
     * Fetches hierarchical data based on class code and duplicate avoidance flag.
     * <p>
     * This method returns a list of hierarchy data maps. If a class code is provided,
     * it fetches data for the specified class code(s). If the class code is missing or empty,
     * it fetches all hierarchy data.
     *
     * @param classCode the class code(s) to fetch hierarchy data for, as a comma-separated string
     * @param avoidDuplicates flag indicating whether to avoid duplicates in the data
     * @return a list of maps representing the fetched hierarchy data, or an error response if an error occurs
     */

    public List<Map<String, Object>> getHierarchyData(String classCode, boolean avoidDuplicates) {
        List<Map<String, Object>> response = new ArrayList<>();

        try {
            if (classCode == null || classCode.isEmpty()) {
                return cosmosDbHierarchyRepository.fetchAllHierarchyData();
            } else {
                List<String> classCodes = Arrays.asList(classCode.split(","));
                return cosmosDbHierarchyRepository.listAllHierarchyData(classCodes, avoidDuplicates);
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching hierarchy data: {}", e.getMessage(), e);
            response.clear();
            response.add(Collections.singletonMap("error", "An error occurred while fetching hierarchy data. Please try again later."));

            return response;
        }
    }

    public List<Map<String, Object>> fetchHierarchyData(String base36Ids) {
        List<String> base36IdList = Arrays.stream(base36Ids.split("Z"))
                .collect(Collectors.toList());

        try {
            List<Map<String, Object>> results = cosmosDbHierarchyRepository.findHierarchyByBase36Ids(base36IdList);
            logger.info("fetchHierarchyData completed successfully with results: {}", results);
            return results;
        } catch (Exception e) {
            logger.error("Error occurred while fetching hierarchy data", e);
            throw e;
        }
    }

}