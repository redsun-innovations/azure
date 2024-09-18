package com.redsun.api.hierarchy.repository;

import com.azure.cosmos.*;
import com.redsun.api.hierarchy.constant.ConstantTest;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redsun.api.hierarchy.entity.FacetEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@AutoConfigureMockMvc
class CosmosDbFacetRepositoryTest {


    private static final Logger logger = LoggerFactory.getLogger(CosmosDbFacetRepositoryTest.class);

    @Mock
    private CosmosClient cosmosClient;

    @Mock
    private CosmosDatabase database;

    @Mock
    private CosmosContainer container;

    @Mock
    private FacetRepository mockFacetRepository;
    private CosmosDbFacetRepository facetRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        facetRepository = new CosmosDbFacetRepository(container,mockFacetRepository);
    }



    @Test
    void testSearchFacetsWithFacetValue() {
        // Given
        List<String> facetTypes = Arrays.asList(ConstantTest.EST_EFF_PRICE_GT_0);
        String facetValue = "Y";

        // Mock FacetRepository
        List<FacetEntity> mockFacets = Collections.singletonList(
                new FacetEntity("est_eff_price_gt_0", "Y", "6", "5")
        );
        when(mockFacetRepository.searchFacets(anyList(), anyString())).thenReturn(mockFacets);

        // When
        List<Map<String, Object>> result = facetRepository.searchFacets(facetTypes, facetValue);

        // Then
        assertEquals(1, result.size());

        Map<String, Object> facet = result.get(0);

        assertEquals(ConstantTest.EST_EFF_PRICE_GT_0, facet.get(ConstantTest.FACETTYPE));
        assertEquals("5", facet.get("facetTypebase36Id"));
        List<Map<String, Object>> facetValues = (List<Map<String, Object>>) facet.get(ConstantTest.FACETVALUES);
        assertEquals(1, facetValues.size());
        assertEquals("Y", facetValues.get(0).get(ConstantTest.FACETVALUE));
        assertEquals("6", facetValues.get(0).get(ConstantTest.FACETBASE36ID));
    }


    @Test
    void testSearchFacetsWithMultipleFacetValues() {
        // Given
        List<String> facetTypes = Arrays.asList("est_eff_price_gt_0", "Brand");
        String facetValue = null;

        // Mock FacetRepository
        List<FacetEntity> mockFacets = Arrays.asList(
                new FacetEntity("est_eff_price_gt_0", "Y", "6", "5"),
                new FacetEntity("est_eff_price_gt_0", "N", "7", "5"),
                new FacetEntity("Brand", "Dell", "8d", "8c")
        );
        when(mockFacetRepository.searchFacets(facetTypes, facetValue)).thenReturn(mockFacets);

        // When
        List<Map<String, Object>> result = facetRepository.searchFacets(facetTypes, facetValue);

        // Then
        assertEquals(2, result.size());

        Map<String, Object> facet1 = result.get(0);
        assertEquals(ConstantTest.EST_EFF_PRICE_GT_0, facet1.get(ConstantTest.FACETTYPE));
        assertEquals("5", facet1.get(ConstantTest.FACETTYPEBASE36ID));;
        List<Map<String, Object>> facetValues1 = (List<Map<String, Object>>) facet1.get(ConstantTest.FACETVALUES);
        assertEquals(2, facetValues1.size());
        assertEquals("Y", facetValues1.get(0).get(ConstantTest.FACETVALUE));
        assertEquals("6", facetValues1.get(0).get(ConstantTest.FACETBASE36ID));
        assertEquals("N", facetValues1.get(1).get(ConstantTest.FACETVALUE));
        assertEquals("7", facetValues1.get(1).get(ConstantTest.FACETBASE36ID));

        Map<String, Object> facet2 = result.get(1);
        assertEquals(ConstantTest.BRAND, facet2.get(ConstantTest.FACETTYPE));
        assertEquals("8c", facet2.get(ConstantTest.FACETTYPEBASE36ID));
        List<Map<String, Object>> facetValues2 = (List<Map<String, Object>>) facet2.get(ConstantTest.FACETVALUES);
        assertEquals(1, facetValues2.size());
        assertEquals("Dell", facetValues2.get(0).get(ConstantTest.FACETVALUE));
        assertEquals("8d", facetValues2.get(0).get(ConstantTest.FACETBASE36ID));
    }

    @Test
    void testListDataWithDefaultParameters() {
        // Given
        Integer pageNumber = null; // Should default to 1
        Integer pageSize = null;   // Should default to 200

        // Mock FacetRepository
        List<FacetEntity> mockFacets = Arrays.asList(
                new FacetEntity("est_eff_price_gt_0", "Y", "6", "5"),
                new FacetEntity("Brand", "Dell", "8d", "8c")
        );
        when(mockFacetRepository.listData(any(Integer.class), any(Integer.class))).thenReturn(mockFacets);

        // When
        Map<String, Object> result = facetRepository.listData(pageNumber, pageSize);

        // Then
        assertEquals(1, result.get("pageNumber"));
        assertEquals(200, result.get("pageSize"));
        assertEquals(2, result.get("count"));

        List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");
        assertEquals(2, data.size());

        Map<String, Object> facet1 = data.get(0);
        assertEquals(ConstantTest.EST_EFF_PRICE_GT_0, facet1.get(ConstantTest.FACETTYPE));
        assertEquals("5", facet1.get(ConstantTest.FACETTYPEBASE36ID));
        List<Map<String, Object>> facetValues1 = (List<Map<String, Object>>) facet1.get(ConstantTest.FACETVALUES);
        assertEquals(1, facetValues1.size());
        assertEquals("Y", facetValues1.get(0).get(ConstantTest.FACETVALUE));
        assertEquals("6", facetValues1.get(0).get(ConstantTest.FACETBASE36ID));

        Map<String, Object> facet2 = data.get(1);
        assertEquals(ConstantTest.BRAND, facet2.get(ConstantTest.FACETTYPE));
        assertEquals("8c", facet2.get(ConstantTest.FACETTYPEBASE36ID));
        List<Map<String, Object>> facetValues2 = (List<Map<String, Object>>) facet2.get(ConstantTest.FACETVALUES);
        assertEquals(1, facetValues2.size());
        assertEquals("Dell", facetValues2.get(0).get(ConstantTest.FACETVALUE));
        assertEquals("8d", facetValues2.get(0).get(ConstantTest.FACETBASE36ID));
    }


    @Test
    void testListDataWithSpecificParameters() {
        // Given
        Integer pageNumber = 2;
        Integer pageSize = 1;

        // Mock FacetRepository
        List<FacetEntity> mockFacets = Collections.singletonList(
                new FacetEntity("Brand", "Dell", "8d", "8c")
        );
        when(mockFacetRepository.listData(any(Integer.class), any(Integer.class))).thenReturn(mockFacets);

        // When
        Map<String, Object> result = facetRepository.listData(pageNumber, pageSize);

        // Then
        assertEquals(2, result.get("pageNumber"));
        assertEquals(1, result.get("pageSize"));
        assertEquals(1, result.get("count"));

        List<Map<String, Object>> data = (List<Map<String, Object>>) result.get("data");
        assertEquals(1, data.size());

        Map<String, Object> facet = data.get(0);
        assertEquals(ConstantTest.BRAND, facet.get(ConstantTest.FACETTYPE));
        assertEquals("8c", facet.get(ConstantTest.FACETTYPEBASE36ID));
        List<Map<String, Object>> facetValues = (List<Map<String, Object>>) facet.get(ConstantTest.FACETVALUES);
        assertEquals(1, facetValues.size());
        assertEquals("Dell", facetValues.get(0).get(ConstantTest.FACETVALUE));
        assertEquals("8d", facetValues.get(0).get(ConstantTest.FACETBASE36ID));
    }


    @Test
    void testFindFacetByBase36Ids() {
        List<String> base36Ids = Arrays.asList("6", "7");
        List<FacetEntity> mockResults = Arrays.asList(
                new FacetEntity( "est_eff_price_gt_0", "Y", "6", "5"),
                new FacetEntity( "est_eff_price_gt_0", "N", "7", "5")

        );

        when(mockFacetRepository.findFacetByBase36Ids(base36Ids)).thenReturn(mockResults);

        List<Map<String, Object>> expectedTransformedData = Arrays.asList(
                createTransformedData("6", "est_eff_price_gt_0", "Y"),
                createTransformedData("7", "est_eff_price_gt_0", "N")
        );

        List<Map<String, Object>> results = facetRepository.findFacetByBase36Ids(base36Ids);

        assertEquals(expectedTransformedData, results);

    }



    private Map<String, Object> createTransformedData(String base36Id, String facetType, String facetValue) {
        Map<String, Object> transformedData = new HashMap<>();
        transformedData.put("base36Id", base36Id);
        transformedData.put("facetType", facetType);

        Map<String, Object> typeData = new HashMap<>();
        typeData.put("Category", "facet");
        typeData.put("facetValue", facetValue);
        transformedData.put("type", typeData);

        return transformedData;
    }

}

//    @Test
//    void testFindFacetByBase36Ids_Exception() {
//        List<String> base36Ids = Arrays.asList("6", "7", "8");
//        when(mockFacetRepository.findFacetByBase36Ids(base36Ids)).thenThrow(new RuntimeException("Error"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            facetRepository.findFacetByBase36Ids(base36Ids);
//        });
//
//        assertEquals("Error", exception.getMessage());
//        verify(logger).error(eq("Error occurred while finding facet data."), eq(base36Ids), eq("Error"), any(RuntimeException.class));
//    }