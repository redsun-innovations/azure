package com.redsun.Hierarchy_Api.controller;

import com.redsun.Hierarchy_Api.service.FacetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FacetControllerTests {

    @Mock
    private FacetService facetService;

    @InjectMocks
    private FacetController facetController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(facetController).build();
    }

    @Test
    public void testSearchFacets() throws Exception {

        List<Map<String, Object>> mockFacets = createSampleFacets();
        when(facetService.searchFacets(anyList(), anyString())).thenReturn(mockFacets);

        // Perform GET request
        mockMvc.perform(get("/v1/facets")
                        .param("facetType", "ats_code")
                        .param("facetValue", "Y"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].facetType").value("ats_code"))
                .andExpect(jsonPath("$[0].facetTypebase36Id").value("1"))
                .andExpect(jsonPath("$[0].facetValues[0].base36Id").value("2"))
                .andExpect(jsonPath("$[0].facetValues[0].facetValue").value("Y"));
    }

    @Test
    public void testSearchFacets_MissingFacetType() throws Exception {

        when(facetService.searchFacets(null, null)).thenReturn(Collections.singletonList(Collections.singletonMap("error", "The query parameter facetType is missing")));

        // Perform GET request without facetType
        mockMvc.perform(get("/v1/facets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].error").value("The query parameter facetType is missing"));
    }
    @Test
    public void testListData() throws Exception {

        Map<String, Object> mockData = createSampleListData();
        when(facetService.listData(anyInt(), anyInt())).thenReturn(mockData);

        // Perform GET request
        mockMvc.perform(get("/v1/facets/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.count").value(8))
                .andExpect(jsonPath("$.pageSize").value(100))
                .andExpect(jsonPath("$.data[0].facetType").value("ats_code"))
                .andExpect(jsonPath("$.data[0].facetTypebase36Id").value("1"))
                .andExpect(jsonPath("$.data[0].facetValues[0].base36Id").value("2"))
                .andExpect(jsonPath("$.data[0].facetValues[0].facetValue").value("Y"));
    }

    private List<Map<String, Object>> createSampleFacets() {
        List<Map<String, Object>> facets = new ArrayList<>();

        Map<String, Object> facet1 = new HashMap<>();
        facet1.put("facetType", "ats_code");
        facet1.put("facetTypebase36Id", "1");
        List<Map<String, Object>> facetValues1 = new ArrayList<>();
        Map<String, Object> facetValue1 = new HashMap<>();
        facetValue1.put("base36Id", "2");
        facetValue1.put("facetValue", "Y");
        facetValues1.add(facetValue1);
        facet1.put("facetValues", facetValues1);
        facets.add(facet1);

        return facets;
    }

    private Map<String, Object> createSampleListData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("pageNumber", 1);
        data.put("count", 8);
        data.put("pageSize", 100);

        List<Map<String, Object>> facets = new ArrayList<>();
        Map<String, Object> facet1 = new HashMap<>();
        facet1.put("facetType", "ats_code");
        facet1.put("facetTypebase36Id", "1");
        List<Map<String, Object>> facetValues1 = new ArrayList<>();
        Map<String, Object> facetValue1 = new HashMap<>();
        facetValue1.put("base36Id", "2");
        facetValue1.put("facetValue", "Y");
        facetValues1.add(facetValue1);
        facet1.put("facetValues", facetValues1);
        facets.add(facet1);

        data.put("data", facets);
        return data;
    }
}
