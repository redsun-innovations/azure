package com.redsun.Hierarchy_Api.service;

import com.redsun.Hierarchy_Api.repository.HierarchyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class HierarchyService {
    private final HierarchyRepository hierarchyRepository;

    @Autowired
    public HierarchyService(HierarchyRepository hierarchyRepository) {
        this.hierarchyRepository = hierarchyRepository;
    }

    public List<Map<String, Object>> fetchHierarchyData(String displayName, String classCode, boolean avoidDuplicates) {
        List<Map<String, Object>> hierarchyItems = hierarchyRepository.fetchHierarchyData(displayName, classCode, avoidDuplicates);
        return hierarchyItems;
    }

    public List<Map<String, Object>> getAllHierarchyData(List<String> classCodes,  boolean avoidDuplicates) {
        return hierarchyRepository.getAllHierarchyData(classCodes, avoidDuplicates);
    }


}