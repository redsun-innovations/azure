package com.redsun.Hierarchy_Api.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;



@AutoConfigureMockMvc
public class CosmosDbHierarchyRepositoryTests {

    @Mock
    private CosmosContainer container;

    @Mock
    private CosmosPagedIterable<JsonNode> cosmosPagedIterable;

    @InjectMocks
    private CosmosDbHierarchyRepository hierarchyRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(container.queryItems(anyString(), any(CosmosQueryRequestOptions.class), any(Class.class)))
                .thenReturn(cosmosPagedIterable);
    }

    @Test
    public void testFetchClassCodeData() {

        List<Map<String, Object>> mockItems = new ArrayList<>();
        mockItems.add(Map.of(
                "displayName", "Home Decor",
                "base36Id", "de018k",
                "path", "null",
                "classCode", "0010"
        ));
        mockItems.add(Map.of(
                "displayName", "Fresh Flowers & Houseplants",
                "base36Id", "mgr0",
                "path", "Home Decor",
                "classCode", "1157"
        ));

        CosmosPagedIterable<Map> mockQueryResults = mock(CosmosPagedIterable.class);
        when(container.queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(Map.class))).thenReturn(mockQueryResults);
        when(mockQueryResults.iterableByPage()).thenReturn(() -> mockItems.iterator());

        List<Map<String, Object>> result = hierarchyRepository.fetchClassCodeData("1157");

        assertEquals(1, result.size());
        Map<String, Object> fetchedItem = result.get(0);
        assertEquals("1157", fetchedItem.get("classCode"));
        assertEquals("Fresh Flowers & Houseplants", fetchedItem.get("displayName"));
        assertEquals("mgr0", fetchedItem.get("base36Id"));
        assertEquals("Home Decor", fetchedItem.get("path"));

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(Map.class));
    }
    @Test
    public void testFetchAllHierarchyData() {

        List<Map<String, Object>> mockItems = new ArrayList<>();
        mockItems.add(Map.of(
                "displayName", "Home Decor",
                "base36Id", "de018k",
                "path", "null"
        ));
        mockItems.add(Map.of(
                "displayName", "Fresh Flowers & Houseplants",
                "base36Id", "mgr0",
                "path", "Home Decor"
        ));
        mockItems.add(Map.of(
                "displayName", "Cut Roses",
                "base36Id", "mgrw",
                "path", "Home Decor/Fresh Flowers & Houseplants"
        ));

        CosmosPagedIterable<Map> mockQueryResults = mock(CosmosPagedIterable.class);
        when(container.queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(Map.class))).thenReturn(mockQueryResults);
        when(mockQueryResults.iterableByPage()).thenReturn(() -> mockItems.iterator());

        List<Map<String, Object>> result = hierarchyRepository.fetchAllHierarchyData();

        assertEquals(3, result.size());
        Map<String, Object> fetchedItem1 = result.get(0);
        assertEquals("Home Decor", fetchedItem1.get("displayName"));
        assertEquals("de018k", fetchedItem1.get("base36Id"));
        assertNull(fetchedItem1.get("parentBase36Id"));

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(Map.class));
    }
    @Test
    public void testListAllHierarchyData() {

        List<Map<String, Object>> mockItems = new ArrayList<>();
        mockItems.add(Map.of(
                "classCode", "0010",
                "base36Id", "de018k"
        ));
        mockItems.add(Map.of(
                "classCode", "1157",
                "base36Id", "mgr0"
        ));

        CosmosPagedIterable<Map> mockQueryResults = mock(CosmosPagedIterable.class);
        when(container.queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(Map.class))).thenReturn(mockQueryResults);
        when(mockQueryResults.iterableByPage()).thenReturn(() -> mockItems.iterator());

        List<Map<String, Object>> result = hierarchyRepository.listAllHierarchyData(List.of("0010", "1157"), true);

        assertEquals(2, result.size());
        assertEquals("0010", result.get(0).get("classCode"));
        assertEquals("de018k", result.get(0).get("base36Id"));
        assertEquals("1157", result.get(1).get("classCode"));
        assertEquals("mgr0", result.get(1).get("base36Id"));

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(Map.class));
    }
}
