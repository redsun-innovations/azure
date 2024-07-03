package com.redsun.api.hierarchy.service;

import com.redsun.api.hierarchy.repository.CosmosHierarchyRepositoryExtends;
import com.redsun.api.hierarchy.model.HierarchyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HierarchyService {

    private final CosmosHierarchyRepositoryExtends hierarchyRepository;

    @Autowired
    public HierarchyService(CosmosHierarchyRepositoryExtends hierarchyRepository) {
        this.hierarchyRepository = hierarchyRepository;
    }

    public List<HierarchyEntity> fetchClassCodeData(String classCode) {
        if (classCode == null || classCode.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "ClassCodes list is not provided");
            return Collections.singletonList(errorResponse);
        }

        return hierarchyRepository.fetchByClassCodeData(classCode);
    }

    public List<Map<String, Object>> getHierarchyData(String classCode, boolean avoidDuplicates) {
        if (classCode == null || classCode.isEmpty()) {
            return hierarchyRepository.fetchByAllHierarchyData();
        } else {
            List<String> classCodes = Arrays.asList(classCode.split(","));
            return hierarchyRepository.listByAllHierarchyData(classCodes, avoidDuplicates);
        }
    }
}
