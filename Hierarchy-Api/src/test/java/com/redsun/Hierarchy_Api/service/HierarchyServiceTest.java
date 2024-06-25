package com.redsun.Hierarchy_Api.service;

import com.redsun.Hierarchy_Api.repository.HierarchyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class HierarchyServiceTest {

    @Mock
    private HierarchyRepository hierarchyRepository;

    @InjectMocks
    private HierarchyService hierarchyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchClassCodeData() {
        String classCode = "H157";

        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> hierarchyItem = new HashMap<>();
        hierarchyItem.put("path", "For the Home/Fresh Flowers & Houseplants");
        hierarchyItem.put("parentBase36Id", "mgr0");
        hierarchyItem.put("base36Id", "12gr0");

        Map<String, Object> hierarchyItem2 = new HashMap<>();
        hierarchyItem2.put("path", "Home Decor/Fresh Flowers & Houseplants");
        hierarchyItem2.put("parentBase36Id", "mgr0");
        hierarchyItem2.put("base36Id", "12gr0");

        List<Map<String, Object>> hierarchyValues = new ArrayList<>();
        hierarchyValues.add(hierarchyItem);
        hierarchyValues.add(hierarchyItem2);

        Map<String, Object> classCodeEntry = new HashMap<>();
        classCodeEntry.put("classCode", "H157");
        classCodeEntry.put("displayName", "Floral Arrangements");
        classCodeEntry.put("hierarchyValues", hierarchyValues);

        mockResponse.add(classCodeEntry);

        when(hierarchyRepository.fetchClassCodeData(classCode)).thenReturn(mockResponse);
        List<Map<String, Object>> response = hierarchyService.fetchClassCodeData(classCode);

        assertEquals(mockResponse, response);
    }

    @Test
    public void testGetHierarchyDataWithClassCode() {

        List<String> classCodes = Arrays.asList("0010", "1157", "F506");
        boolean avoidDuplicates = true;

        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> entry1 = new HashMap<>();
        entry1.put("classCode", "0010");
        entry1.put("base36Id", "de018k");

        Map<String, Object> entry2 = new HashMap<>();
        entry2.put("classCode", "1157");
        entry2.put("base36Id", "mgr0");

        Map<String, Object> entry3 = new HashMap<>();
        entry3.put("classCode", "F506");
        entry3.put("base36Id", "1o4wvb3");

        mockResponse.add(entry1);
        mockResponse.add(entry2);
        mockResponse.add(entry3);

        when(hierarchyRepository.listAllHierarchyData(classCodes, avoidDuplicates)).thenReturn(mockResponse);

        List<Map<String, Object>> response = hierarchyService.getHierarchyData(String.join(",", classCodes), avoidDuplicates);

        assertEquals(mockResponse, response);
    }

    @Test
    public void testGetHierarchyDataWithoutClassCode() {
        String classCode = null;
        boolean avoidDuplicates = true;

        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> hierarchyItem = new HashMap<>();
        hierarchyItem.put("displayName", "Floral Arrangements");
        hierarchyItem.put("parentBase36Id", "mgr0");
        hierarchyItem.put("base36Id", "12gr0");

        mockResponse.add(hierarchyItem);

        when(hierarchyRepository.fetchAllHierarchyData()).thenReturn(mockResponse);

        List<Map<String, Object>> response = hierarchyService.getHierarchyData(classCode, avoidDuplicates);

        assertEquals(mockResponse, response);
    }
}
