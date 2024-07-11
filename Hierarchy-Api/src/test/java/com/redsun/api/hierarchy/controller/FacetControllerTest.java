package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.constant.ConstantTest;
import com.redsun.api.hierarchy.service.FacetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FacetControllerTest {

    @Mock
    private FacetService facetService;

    @InjectMocks
    private FacetController facetController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(facetController).build();
    }

    /**
     * Test case for searching facets based on facet type and value.
     *
     * @throws Exception If there is an error performing the HTTP request or validating the response.
     */
    @Test
    void testSearchFacets() throws Exception {
        try {
        List<Map<String, Object>> mockFacets = createSampleFacets();
        when(facetService.searchFacets(anyList(), anyString())).thenReturn(mockFacets);

        // Perform GET request
        mockMvc.perform(get("/v1/facets")
                        .param(ConstantTest.FACETTYPE, ConstantTest.ATS_CODE)
                        .param(ConstantTest.FACETVALUE, "Y"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].facetType").value(ConstantTest.ATS_CODE))
                .andExpect(jsonPath("$[0].facetTypebase36Id").value("1"))
                .andExpect(jsonPath("$[0].facetValues[0].base36Id").value("2"))
                .andExpect(jsonPath("$[0].facetValues[0].facetValue").value("Y"));
    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    /**
     * Test case for handling missing facetType parameter in the request.
     *
     * @throws Exception If there is an error performing the HTTP request or validating the response.
     */
    @Test
    void testMissingFacetType() throws Exception {
        try {
        when(facetService.searchFacets(null, null)).thenReturn(Collections.singletonList(Collections.singletonMap("error", "The query parameter facetType is missing")));

        // Perform GET request without facetType
        mockMvc.perform(get("/v1/facets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].error").value("The query parameter facetType is missing"));
    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }


    @Test
    void testSearchFacetsException() throws Exception {
        try {
            when(facetService.searchFacets(anyList(), anyString())).thenThrow(new RuntimeException("Error"));

            // Perform GET request
            mockMvc.perform(get("/v1/facets")
                            .param(ConstantTest.FACETTYPE, ConstantTest.ATS_CODE)
                            .param(ConstantTest.FACETVALUE, "Y"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    /**
     * Test case for listing data with pagination information.
     *
     * @throws Exception If there is an error performing the HTTP request or validating the response.
     */
    @Test
    void testListData() throws Exception {
    try{
        Map<String, Object> mockData = createSampleListData();
        when(facetService.listData(anyInt(), anyInt())).thenReturn(mockData);

        // Perform GET request
        mockMvc.perform(get("/v1/facets/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.count").value(8))
                .andExpect(jsonPath("$.pageSize").value(100))
                .andExpect(jsonPath("$.data[0].facetType").value(ConstantTest.ATS_CODE))
                .andExpect(jsonPath("$.data[0].facetTypebase36Id").value("1"))
                .andExpect(jsonPath("$.data[0].facetValues[0].base36Id").value("2"))
                .andExpect(jsonPath("$.data[0].facetValues[0].facetValue").value("Y"));
    }catch (Exception e) {
        e.printStackTrace();
        fail("Exception thrown during test: " + e.getMessage());
    }
    }

    @Test
    void testListDataException() throws Exception {
        try {
            when(facetService.listData(anyInt(), anyInt())).thenThrow(new RuntimeException("Error"));

            // Perform GET request
            mockMvc.perform(get("/v1/facets/list"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{}"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> createSampleFacets() {
        List<Map<String, Object>> facets = new ArrayList<>();

        Map<String, Object> facet1 = new HashMap<>();
        facet1.put(ConstantTest.FACETTYPE, ConstantTest.ATS_CODE);
        facet1.put(ConstantTest.FACETTYPEBASE36ID, "1");
        List<Map<String, Object>> facetValues1 = new ArrayList<>();
        Map<String, Object> facetValue1 = new HashMap<>();
        facetValue1.put(ConstantTest.BASE36ID, "2");
        facetValue1.put(ConstantTest.FACETVALUE, "Y");
        facetValues1.add(facetValue1);
        facet1.put(ConstantTest.FACETVALUES, facetValues1);
        facets.add(facet1);

        Map<String, Object> facet2 = new HashMap<>();
        facet2.put(ConstantTest.FACETTYPE, "est_eff_price_gt_0");
        facet2.put(ConstantTest.FACETTYPEBASE36ID, "5");
        List<Map<String, Object>> facetValues2 = new ArrayList<>();
        Map<String, Object> facetValue2 = new HashMap<>();
        facetValue2.put(ConstantTest.BASE36ID, "6");
        facetValue2.put(ConstantTest.FACETVALUE, "Y");
        facetValues2.add(facetValue2);
        facet2.put(ConstantTest.FACETVALUES, facetValues2);
        facets.add(facet2);

        return facets;
    }

    private Map<String, Object> createSampleListData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("pageNumber", 1);
        data.put("count", 8);
        data.put("pageSize", 100);

        List<Map<String, Object>> facets = new ArrayList<>();
        Map<String, Object> facet1 = new HashMap<>();
        facet1.put(ConstantTest.FACETTYPE, ConstantTest.ATS_CODE);
        facet1.put(ConstantTest.FACETTYPEBASE36ID, "1");
        List<Map<String, Object>> facetValues1 = new ArrayList<>();
        Map<String, Object> facetValue1 = new HashMap<>();
        facetValue1.put(ConstantTest.BASE36ID, "2");
        facetValue1.put(ConstantTest.FACETVALUE, "Y");
        facetValues1.add(facetValue1);
        facet1.put(ConstantTest.FACETVALUES, facetValues1);
        facets.add(facet1);

        Map<String, Object> facet2 = new HashMap<>();
        facet2.put(ConstantTest.FACETTYPE, "est_eff_price_gt_0");
        facet2.put(ConstantTest.FACETTYPEBASE36ID, "5");
        List<Map<String, Object>> facetValues2 = new ArrayList<>();
        Map<String, Object> facetValue2 = new HashMap<>();
        facetValue2.put(ConstantTest.BASE36ID, "6");
        facetValue2.put(ConstantTest.FACETVALUE, "Y");
        facetValues2.add(facetValue2);
        facet2.put(ConstantTest.FACETVALUES, facetValues2);
        facets.add(facet2);

        data.put("data", facets);
        return data;
    }
}
