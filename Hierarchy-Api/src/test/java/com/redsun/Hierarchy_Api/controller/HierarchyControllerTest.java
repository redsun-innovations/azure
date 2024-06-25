package com.redsun.Hierarchy_Api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redsun.Hierarchy_Api.service.HierarchyService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class HierarchyControllerTest {

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
    public void testFetchClassCodeData() throws Exception {

        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> hierarchyItem = new HashMap<>();
        hierarchyItem.put("classCode", "H157");
        hierarchyItem.put("displayName", "Floral Arrangements");
        hierarchyItem.put("hierarchyValues", new ArrayList<>());
        mockResponse.add(hierarchyItem);

        when(hierarchyService.fetchClassCodeData(eq("H157"))).thenReturn(mockResponse);

        // Perform GET request
        mockMvc.perform(get("/v1/hierarchy/class-code/{classCode}", "H157"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].classCode").value("H157"))
                .andExpect(jsonPath("$[0].displayName").value("Floral Arrangements"))
                .andExpect(jsonPath("$[0].hierarchyValues").isArray());

    }

    @Test
    public void testGetHierarchyDataWithoutClassCode() throws Exception {

        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> entry = new HashMap<>();
        entry.put("classCode", "0010");
        entry.put("base36Id", "de018k");
        mockResponse.add(entry);

        when(hierarchyService.getHierarchyData(eq(null), eq(true))).thenReturn(mockResponse);
        // Perform GET request
        mockMvc.perform(get("/v1/hierarchy").param("avoidDuplicates", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].classCode").value("0010"))
                .andExpect(jsonPath("$[0].base36Id").value("de018k"));

        verify(hierarchyService).getHierarchyData(eq(null), eq(true));
    }

}
