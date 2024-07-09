package com.redsun.api.hierarchy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redsun.api.hierarchy.model.HierarchyEntity;
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

        List<HierarchyEntity> entities = hierarchyRepository.fetchClassCodeData(classCode);
        return convertHierarchyEntitiesToMaps(entities);
    }

    public List<Map<String, Object>> getHierarchyData(String classCode, boolean avoidDuplicates) {
        if (classCode == null || classCode.isEmpty()) {
            return convertHierarchyEntitiesToMaps(hierarchyRepository.fetchAllHierarchyData());
        } else {
            List<String> classCodes = Arrays.asList(classCode.split(","));
            return convertHierarchyEntitiesToMaps(hierarchyRepository.listAllHierarchyData(classCodes, avoidDuplicates));
        }
    }

    private List<Map<String, Object>> convertHierarchyEntitiesToMaps(List<HierarchyEntity> entities) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (HierarchyEntity entity : entities) {
            Map<String, Object> itemMap = new ObjectMapper().convertValue(entity, new TypeReference<Map<String, Object>>() {});
            items.add(itemMap);
        }
        return items;
    }

}