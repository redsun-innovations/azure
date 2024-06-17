package com.redsun.Hierarchy_Api.controller;

import com.redsun.Hierarchy_Api.service.HierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    public List<Map<String, Object>> fetchHierarchyData(@RequestParam(required = true) String classCode,
                                                        @RequestParam(required = false, defaultValue = "false") boolean avoidDuplicates) {
        return hierarchyService.fetchHierarchyData(classCode, avoidDuplicates);
    }

    @GetMapping("/allData")
    public List<Map<String, Object>> fetchAllHierarchyData() {
        return hierarchyService.fetchAllHierarchyData();
    }



    @GetMapping("/getData")
    public List<Map<String, Object>> getAllHierarchyData(@RequestParam String classCode,
                                                         @RequestParam(required = false, defaultValue = "false") boolean avoidDuplicates) {
        List<String> classCodes = Arrays.asList(classCode.split(","));
        return hierarchyService.getAllHierarchyData(classCodes, avoidDuplicates);
    }
}
