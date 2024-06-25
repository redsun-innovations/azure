package com.redsun.Hierarchy_Api.controller;

import com.redsun.Hierarchy_Api.service.HierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/v1/hierarchy")
public class HierarchyController {
    private final HierarchyService hierarchyService;

    @Autowired
    public HierarchyController(HierarchyService hierarchyService) {
        this.hierarchyService = hierarchyService;
    }

    @GetMapping("/class-code/{classCode}")
    public List<Map<String, Object>> fetchClassCodeData(@PathVariable String classCode) {
        return hierarchyService.fetchClassCodeData(classCode);
    }

    @GetMapping
    public List<Map<String, Object>> getHierarchyData(
            @RequestParam(required = false) String classCode,
            @RequestParam(required = false, defaultValue = "true") boolean avoidDuplicates) {
        return hierarchyService.getHierarchyData(classCode, avoidDuplicates);
    }
}
