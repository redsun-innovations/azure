package com.redsun.api.hierarchy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.*;

import com.redsun.api.hierarchy.service.FacetService;
import com.redsun.api.hierarchy.service.HierarchyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

 class GroupControllerTest {


    private MockMvc mockMvc;

    @Mock
    private HierarchyService hierarchyService;

    @Mock
    private FacetService facetService;

    @InjectMocks
    private GroupController groupController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Test
    void testFetchCombinedData() throws Exception {
        String base36Ids = "6Z7Z8";

        // Mock hierarchy data
        List<Map<String, Object>> hierarchyData = Arrays.asList(
                createSampleHierarchy("mgr0", "Fresh Flowers & Houseplants", "1157"),
                createSampleHierarchy("mgrw", "Cut Roses", "1163")
        );
        when(hierarchyService.fetchHierarchyData(base36Ids)).thenReturn(hierarchyData);

        // Mock facet data
        List<Map<String, Object>> facetData = Arrays.asList(
                createSampleFacet("6", "Type1"),
                createSampleFacet("7", "Type2")
        );
        when(facetService.fetchFacetData(base36Ids)).thenReturn(facetData);

        mockMvc.perform(get("/group/fetchGroupData")
                        .param("base36Ids", base36Ids))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hierarchyData[0].base36Id").value("mgr0"))
                .andExpect(jsonPath("$.facetData[0].base36Id").value("6"));
    }

    private Map<String, Object> createSampleHierarchy(String base36Id, String displayName, String classCode) {
        Map<String, Object> hierarchy = new HashMap<>();
        hierarchy.put("base36Id", base36Id);
        hierarchy.put("name", displayName);
        Map<String, Object> typeData = new HashMap<>();
        typeData.put("Category", "hierarchy");
        typeData.put("classCode", classCode);
        hierarchy.put("type", typeData);
        return hierarchy;
    }

    private Map<String, Object> createSampleFacet(String base36Id, String facetType) {
        Map<String, Object> facet = new HashMap<>();
        facet.put("base36Id", base36Id);
        facet.put("facetType", facetType);
        Map<String, Object> typeData = new HashMap<>();
        typeData.put("Category", "facet");
        facet.put("type", typeData);
        return facet;
    }

     @Test
     void testFetchCombinedDataException() throws Exception {
         String base36Ids = "6Z7ZH157";

         when(hierarchyService.fetchHierarchyData(base36Ids)).thenThrow(new RuntimeException("Error"));
         when(facetService.fetchFacetData(base36Ids)).thenReturn(Collections.emptyList());

         mockMvc.perform(MockMvcRequestBuilders.get("/group/fetchGroupData")
                         .param("base36Ids", base36Ids)
                         .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$").isEmpty());

         verify(hierarchyService).fetchHierarchyData(base36Ids);
         verify(facetService, never()).fetchFacetData(base36Ids);
     }
}