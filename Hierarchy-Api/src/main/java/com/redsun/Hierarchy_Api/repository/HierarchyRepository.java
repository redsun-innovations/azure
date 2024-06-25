package com.redsun.Hierarchy_Api.repository;

import com.azure.cosmos.*;
import java.util.List;
import java.util.Map;

public interface HierarchyRepository {

    List<Map<String, Object>> fetchClassCodeData(String classCode);

    List<Map<String, Object>> fetchAllHierarchyData();

    List<Map<String, Object>> listAllHierarchyData(List<String> classCodes, boolean avoidDuplicates);
}