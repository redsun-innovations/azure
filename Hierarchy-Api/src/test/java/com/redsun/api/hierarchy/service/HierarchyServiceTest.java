package com.redsun.api.hierarchy.service;


import com.redsun.api.hierarchy.constant.ConstantTest;
import com.redsun.api.hierarchy.repository.HierarchyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class HierarchyServiceTest {

    @Mock
    private HierarchyRepository hierarchyRepository;

    @InjectMocks
    private HierarchyService hierarchyService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchClassCodeDataWithNullClassCode() {
        try {
        String classCode = null;

        List<Map<String, Object>> response = hierarchyService.fetchClassCodeData(classCode);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("error", "ClassCodes list is not provided");

        assertEquals(Collections.singletonList(expectedResponse), response);
    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testFetchClassCodeDataWithEmptyClassCode() {
        try {
        String classCode = "";

        List<Map<String, Object>> response = hierarchyService.fetchClassCodeData(classCode);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("error", "ClassCodes list is not provided");

        assertEquals(Collections.singletonList(expectedResponse), response);
    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testFetchClassCodeData() {
        try {
        String classCode = "H157";

        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> hierarchyItem = new HashMap<>();
        hierarchyItem.put("path", "For the Home/Fresh Flowers & Houseplants");
        hierarchyItem.put(ConstantTest.PARENTBASE36ID, "mgr0");
        hierarchyItem.put(ConstantTest.BASE36ID, ConstantTest.FACET_CODE_12GR0);

        Map<String, Object> hierarchyItem2 = new HashMap<>();
        hierarchyItem2.put("path", "Home Decor/Fresh Flowers & Houseplants");
        hierarchyItem2.put(ConstantTest.PARENTBASE36ID, "mgr0");
        hierarchyItem2.put(ConstantTest.BASE36ID, ConstantTest.FACET_CODE_12GR0);

        List<Map<String, Object>> hierarchyValues = new ArrayList<>();
        hierarchyValues.add(hierarchyItem);
        hierarchyValues.add(hierarchyItem2);

        Map<String, Object> classCodeEntry = new HashMap<>();
        classCodeEntry.put(ConstantTest.CLASSCODE, "H157");
        classCodeEntry.put(ConstantTest.DISPLAYNAME, "Floral Arrangements");
        classCodeEntry.put(ConstantTest.HIERARCHYVALUES, hierarchyValues);

        mockResponse.add(classCodeEntry);

        when(hierarchyRepository.fetchClassCodeData(classCode)).thenReturn(mockResponse);
        List<Map<String, Object>> response = hierarchyService.fetchClassCodeData(classCode);

        assertEquals(mockResponse, response);
    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testGetHierarchyDataWithClassCode() {
        try {
        List<String> classCodes = Arrays.asList("0010", "1157", "F506");
        boolean avoidDuplicates = true;

        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> entry1 = new HashMap<>();
        entry1.put(ConstantTest.CLASSCODE, "0010");
        entry1.put(ConstantTest.BASE36ID, "de018k");

        Map<String, Object> entry2 = new HashMap<>();
        entry2.put(ConstantTest.CLASSCODE, "1157");
        entry2.put(ConstantTest.BASE36ID, "mgr0");

        Map<String, Object> entry3 = new HashMap<>();
        entry3.put(ConstantTest.CLASSCODE, "F506");
        entry3.put(ConstantTest.BASE36ID, "1o4wvb3");

        mockResponse.add(entry1);
        mockResponse.add(entry2);
        mockResponse.add(entry3);

        when(hierarchyRepository.listAllHierarchyData(classCodes, avoidDuplicates)).thenReturn(mockResponse);

        List<Map<String, Object>> response = hierarchyService.getHierarchyData(String.join(",", classCodes), avoidDuplicates);

        assertEquals(mockResponse, response);
    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testGetHierarchyDataWithoutClassCode() {
        try {
        String classCode = null;
        boolean avoidDuplicates = true;

        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> hierarchyItem = new HashMap<>();
        hierarchyItem.put(ConstantTest.DISPLAYNAME, "Floral Arrangements");
        hierarchyItem.put(ConstantTest.PARENTBASE36ID, "mgr0");
        hierarchyItem.put(ConstantTest.BASE36ID, ConstantTest.FACET_CODE_12GR0);

        mockResponse.add(hierarchyItem);

        when(hierarchyRepository.fetchAllHierarchyData()).thenReturn(mockResponse);

        List<Map<String, Object>> response = hierarchyService.getHierarchyData(classCode, avoidDuplicates);

        assertEquals(mockResponse, response);
    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testFetchClassCodeDataException() {
        try {
            String classCode = "0010";
            when(hierarchyRepository.fetchClassCodeData(classCode)).thenThrow(new RuntimeException("Error"));

            List<Map<String, Object>> result = hierarchyService.fetchClassCodeData(classCode);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("An error occurred while fetching class code data. Please try again later.", result.get(0).get("error"));
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testGetHierarchyDataException() {
        try {
            String classCode = "sampleCode";
            boolean avoidDuplicates = true;
            when(hierarchyRepository.listAllHierarchyData(anyList(), eq(avoidDuplicates))).thenThrow(new RuntimeException("Error"));

            List<Map<String, Object>> result = hierarchyService.getHierarchyData(classCode, avoidDuplicates);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("An error occurred while fetching hierarchy data. Please try again later.", result.get(0).get("error"));
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }
}
