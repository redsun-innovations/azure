package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.constant.ConstantTest;
import com.redsun.api.hierarchy.service.HierarchyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HierarchyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HierarchyService hierarchyService;

    @InjectMocks
    private HierarchyController hierarchyController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(hierarchyController).build();
    }

    @Test
    void testFetchClassCodeData() throws Exception {
        try {
        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> hierarchyItem = new HashMap<>();
        hierarchyItem.put(ConstantTest.CLASSCODE, "H157");
        hierarchyItem.put(ConstantTest.DISPLAYNAME, "Floral Arrangements");
        hierarchyItem.put(ConstantTest.HIERARCHYVALUES, new ArrayList<>());
        mockResponse.add(hierarchyItem);

        when(hierarchyService.fetchClassCodeData("H157")).thenReturn(mockResponse);

        mockMvc.perform(get("/v1/hierarchy/class-code/{classCode}", "H157"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].classCode").value("H157"))
                .andExpect(jsonPath("$[0].displayName").value("Floral Arrangements"))
                .andExpect(jsonPath("$[0].hierarchyValues").isArray());

    } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testFetchClassCodeDataException() throws Exception {
        try {
            when(hierarchyService.fetchClassCodeData(anyString())).thenThrow(new RuntimeException("Error"));

            // Perform GET request
            mockMvc.perform(get("/v1/hierarchy/class-code/{classCode}", "H157"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testGetHierarchyDataWithoutClassCode() throws Exception {
        try {
        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> entry = new HashMap<>();
        entry.put(ConstantTest.CLASSCODE, "0010");
        entry.put(ConstantTest.BASE36ID, "de018k");
        mockResponse.add(entry);

        when(hierarchyService.getHierarchyData(isNull(), eq(true))).thenReturn(mockResponse);
        // Perform GET request
        mockMvc.perform(get("/v1/hierarchy").param("avoidDuplicates", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].classCode").value("0010"))
                .andExpect(jsonPath("$[0].base36Id").value("de018k"));

        verify(hierarchyService).getHierarchyData(isNull(), eq(true));
    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testGetHierarchyDataException() throws Exception {
        try {
            when(hierarchyService.getHierarchyData(anyString(), anyBoolean())).thenThrow(new RuntimeException("Error"));

            // Perform GET request
            mockMvc.perform(get("/v1/hierarchy")
                            .param("classCode", "H157")
                            .param("avoidDuplicates", "true"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

}
