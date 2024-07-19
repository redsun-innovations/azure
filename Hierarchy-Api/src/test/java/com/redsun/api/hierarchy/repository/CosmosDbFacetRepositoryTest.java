package com.redsun.api.hierarchy.repository;

import com.azure.cosmos.*;
import com.redsun.api.hierarchy.constant.ConstantTest;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



@AutoConfigureMockMvc
class CosmosDbFacetRepositoryTest {

    @Mock
    private CosmosClient cosmosClient;

    @Mock
    private CosmosDatabase database;

    @Mock
    private CosmosContainer container;

    @Mock
    private CosmosPagedIterable<JsonNode> cosmosPagedIterable;

    @Mock
    private CosmosPagedIterable<JsonNode> cosmosPagedIterable1;

    @Mock
    private CosmosPagedIterable<JsonNode> cosmosPagedIterable2;

    private CosmosDbFacetRepository facetRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        facetRepository = new CosmosDbFacetRepository(container);
    }



    @Test
    void testSearchFacetsWithFacetValue() {
        // Given
        List<String> facetTypes = Arrays.asList(ConstantTest.EST_EFF_PRICE_GT_0);
        String facetValue = "Y";

        // Create JsonNode using ObjectMapper
        JsonNode item1 = new ObjectMapper().createObjectNode()
                .put("pk", ConstantTest.FACETS)
                .put(ConstantTest.FACETTYPE, ConstantTest.EST_EFF_PRICE_GT_0)
                .put(ConstantTest.FACETTYPEBASE36ID, "5")
                .put(ConstantTest.FACETVALUE, "Y")
                .put(ConstantTest.FACETBASE36ID, "6");

        CosmosPagedIterable<JsonNode> pagedIterable = mock(CosmosPagedIterable.class);
        when(pagedIterable.iterator()).thenReturn(Collections.singletonList(item1).iterator());

        // Mock container.queryItems
        when(container.queryItems(any(String.class), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(pagedIterable);

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
        List<String> facetTypes = Arrays.asList(ConstantTest.EST_EFF_PRICE_GT_0, ConstantTest.BRAND);
        String facetValue = null;

        JsonNode jsonNode1 = new ObjectMapper().createObjectNode()
                .put("pk", ConstantTest.FACETS)
                .put(ConstantTest.FACETTYPE, ConstantTest.EST_EFF_PRICE_GT_0)
                .put(ConstantTest.FACETTYPEBASE36ID, "5")
                .put(ConstantTest.FACETVALUE, "Y")
                .put(ConstantTest.FACETBASE36ID, "6");

        JsonNode jsonNode2 = new ObjectMapper().createObjectNode()
                .put("pk", ConstantTest.FACETS)
                .put(ConstantTest.FACETTYPE, ConstantTest.EST_EFF_PRICE_GT_0)
                .put(ConstantTest.FACETTYPEBASE36ID, "5")
                .put(ConstantTest.FACETVALUE, "N")
                .put(ConstantTest.FACETBASE36ID, "7");

        JsonNode jsonNode3 = new ObjectMapper().createObjectNode()
                .put("pk", ConstantTest.FACETS)
                .put(ConstantTest.FACETTYPE, ConstantTest.BRAND)
                .put(ConstantTest.FACETTYPEBASE36ID, "8c")
                .put(ConstantTest.FACETVALUE, "Dell")
                .put(ConstantTest.FACETBASE36ID, "8d");

        CosmosPagedIterable<JsonNode> pagedIterable = mock(CosmosPagedIterable.class);
        when(pagedIterable.iterator()).thenReturn(Arrays.asList(jsonNode1, jsonNode2, jsonNode3).iterator());

        // Mock container.queryItems
        when(container.queryItems(any(String.class), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(pagedIterable);

        // When
        List<Map<String, Object>> result = facetRepository.searchFacets(facetTypes, facetValue);

        // Then
        assertEquals(2, result.size());

        Map<String, Object> facet1 = result.get(0);
        assertEquals(ConstantTest.EST_EFF_PRICE_GT_0, facet1.get(ConstantTest.FACETTYPE));
        assertEquals("5", facet1.get(ConstantTest.FACETTYPEBASE36ID));
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

        JsonNode jsonNode1 = new ObjectMapper().createObjectNode()
                .put("pk", ConstantTest.FACETS)
                .put(ConstantTest.FACETTYPE, ConstantTest.EST_EFF_PRICE_GT_0)
                .put(ConstantTest.FACETTYPEBASE36ID, "5")
                .put(ConstantTest.FACETVALUE, "Y")
                .put(ConstantTest.FACETBASE36ID, "6");

        JsonNode jsonNode2 = new ObjectMapper().createObjectNode()
                .put("pk", ConstantTest.FACETS)
                .put(ConstantTest.FACETTYPE, ConstantTest.BRAND)
                .put(ConstantTest.FACETTYPEBASE36ID, "8c")
                .put(ConstantTest.FACETVALUE, "Dell")
                .put(ConstantTest.FACETBASE36ID, "8d");

        CosmosPagedIterable<JsonNode> pagedIterable = mock(CosmosPagedIterable.class);
        when(pagedIterable.iterator()).thenReturn(Arrays.asList(jsonNode1, jsonNode2).iterator());

        when(container.queryItems(any(String.class), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(pagedIterable);

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

        JsonNode jsonNode1 = new ObjectMapper().createObjectNode()
                .put("pk", ConstantTest.FACETS)
                .put(ConstantTest.FACETTYPE, ConstantTest.BRAND)
                .put(ConstantTest.FACETTYPEBASE36ID, "8c")
                .put(ConstantTest.FACETVALUE, "Dell")
                .put(ConstantTest.FACETBASE36ID, "8d");

        CosmosPagedIterable<JsonNode> pagedIterable = mock(CosmosPagedIterable.class);
        when(pagedIterable.iterator()).thenReturn(Collections.singletonList(jsonNode1).iterator());

        // Mock container.queryItems
        when(container.queryItems(any(String.class), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(pagedIterable);

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


}

