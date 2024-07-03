package com.redsun.api.hierarchy.repository;

import java.util.List;
import java.util.Map;

public interface HierarchyRepository {

    /**
     * Fetches hierarchical data for a specific class code.
     *
     * @param classCode The class code for which hierarchical data is fetched.
     * @return List of maps representing hierarchical data entries.
     */
    List<Map<String, Object>> fetchClassCodeData(String classCode);

    /**
     * Fetches all hierarchical data entries.
     *
     * @return List of maps representing all hierarchical data entries.
     */
    List<Map<String, Object>> fetchAllHierarchyData();

    /**
     * Lists hierarchical data entries for specified class codes.
     *
     * @param classCodes      List of class codes to fetch hierarchical data for.
     * @param avoidDuplicates Flag indicating whether to avoid duplicate entries.
     * @return List of maps representing hierarchical data entries for the specified class codes.
     */
    List<Map<String, Object>> listAllHierarchyData(List<String> classCodes, boolean avoidDuplicates);
}