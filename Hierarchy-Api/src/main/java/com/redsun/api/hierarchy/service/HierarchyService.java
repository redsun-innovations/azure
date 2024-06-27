package com.redsun.api.hierarchy.service;

import com.redsun.api.hierarchy.repository.HierarchyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class HierarchyService {
    private final HierarchyRepository hierarchyRepository;

    @Autowired
    public HierarchyService(HierarchyRepository hierarchyRepository) {
        this.hierarchyRepository = hierarchyRepository;
    }

    public List<Map<String, Object>> fetchClassCodeData(String classCode) {
        if (classCode == null || classCode.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "ClassCodes list is not provided");
            return Collections.singletonList(errorResponse);
        }

        return hierarchyRepository.fetchClassCodeData(classCode);
    }

    public List<Map<String, Object>> getHierarchyData(String classCode,boolean avoidDuplicates) {
        if (classCode == null || classCode.isEmpty()) {
            return hierarchyRepository.fetchAllHierarchyData();
        } else {
            List<String> classCodes = Arrays.asList(classCode.split(","));
            return hierarchyRepository.listAllHierarchyData(classCodes, avoidDuplicates);
        }
    }

}