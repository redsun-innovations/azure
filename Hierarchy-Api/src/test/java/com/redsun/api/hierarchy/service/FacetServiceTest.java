package com.redsun.api.hierarchy.service;

import com.redsun.api.hierarchy.constant.ConstantTest;
import com.redsun.api.hierarchy.constant.Constant;
import com.redsun.api.hierarchy.repository.FacetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacetServiceTest {

    @Mock
    private FacetRepository facetRepository;

    @InjectMocks
    private FacetService facetService;



    @Test
    void testSearchFacetsWithValidInput() {
        try {
        List<String> facetTypes = Arrays.asList(ConstantTest.ATS_CODE, ConstantTest.EST_EFF_PRICE_GT_0);
        String facetValue = "Y";

        List<Map<String, Object>> mockFacets = createSampleFacets();
        when(facetRepository.searchFacets(anyList(), anyString())).thenReturn(mockFacets);

        List<Map<String, Object>> response = facetService.searchFacets(facetTypes, facetValue);

        assertEquals(2, response.size());
        assertEquals(ConstantTest.ATS_CODE, response.get(0).get("facetType"));

    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testSearchFacetsWithMissingFacetTypes() {
        try {
        List<String> facetTypes = null;

        List<Map<String, Object>> response = facetService.searchFacets(facetTypes, null);

        assertEquals(1, response.size());
        assertEquals("The query parameter facetType is missing", response.get(0).get("error"));
    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testListData() {
        try {
        Integer pageNumber = 1;
        Integer pageSize = 100;
        Map<String, Object> mockData = createSampleListData();
        when(facetRepository.listData(pageNumber, pageSize)).thenReturn(mockData);

        Map<String, Object> response = facetService.listData(pageNumber, pageSize);

        assertEquals(pageNumber, response.get("pageNumber"));
        assertEquals(8, response.get("count"));
    }catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testSearchFacetsException() {
        try {
            List<String> facetTypes = Arrays.asList("ats_code", "Brand");
            String facetValue = "Y";
            when(facetRepository.searchFacets(facetTypes, facetValue)).thenThrow(new RuntimeException("Error"));

            List<Map<String, Object>> result = facetService.searchFacets(facetTypes, facetValue);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("An error occurred while fetching facets. Please try again later.", result.get(0).get(Constant.ERROR));
        } catch (Exception e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    @Test
    void testListDataException() {
        try {
            Integer pageNumber = 1;
            Integer pageSize = 10;
            when(facetRepository.listData(pageNumber, pageSize)).thenThrow(new RuntimeException("Error"));

            Map<String, Object> result = facetService.listData(pageNumber, pageSize);

            assertNotNull(result);
            assertEquals("An error occurred while fetching data. Please try again later.", result.get(Constant.ERROR));
        } catch (Exception e) {
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
        facet2.put(ConstantTest.FACETTYPE, ConstantTest.EST_EFF_PRICE_GT_0);
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
        facet2.put(ConstantTest.FACETTYPE, ConstantTest.EST_EFF_PRICE_GT_0);
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
