package com.redsun.Hierarchy_Api.service;

import com.redsun.Hierarchy_Api.repository.HierarchyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class HierarchyService {
    private final HierarchyRepository hierarchyRepository;

    @Autowired
    public HierarchyService(HierarchyRepository hierarchyRepository) {
        this.hierarchyRepository = hierarchyRepository;
    }

    public List<Map<String, Object>> fetchHierarchyData(String classCode, boolean avoidDuplicates) {
        if (classCode == null || classCode.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error","ClassCodes list is not provided");
        }

        List<Map<String, Object>> hierarchyItems = hierarchyRepository.fetchHierarchyData(classCode, avoidDuplicates);
        return hierarchyItems;
    }

    public List<Map<String, Object>> getAllHierarchyData(List<String> classCodes,  boolean avoidDuplicates) {
        if (classCodes == null || classCodes.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error","ClassCodes list is not provided");
        }
        return hierarchyRepository.getAllHierarchyData(classCodes, avoidDuplicates);
    }


}