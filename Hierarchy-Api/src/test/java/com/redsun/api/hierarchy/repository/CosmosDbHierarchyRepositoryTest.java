package com.redsun.api.hierarchy.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@AutoConfigureMockMvc
class CosmosDbHierarchyRepositoryTest {

    @Mock
    private CosmosContainer container;

    @Mock
    private CosmosPagedIterable<JsonNode> cosmosPagedIterable;

    @Mock
    private CosmosDbFacetRepository facetRepository;

    @InjectMocks
    private CosmosDbHierarchyRepository hierarchyRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        hierarchyRepository  = new CosmosDbHierarchyRepository(container);
    }

    @Test
    void testFetchClassCodeData()  {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode item1 = objectMapper.createObjectNode()
                .put("displayName", "Home Decor")
                .put("base36Id", "de018k")
                .put("path", "null")
                .put("classCode", "0010");

        ObjectNode item2 = objectMapper.createObjectNode()
                .put("displayName", "Fresh Flowers & Houseplants")
                .put("base36Id", "mgr0")
                .put("path", "Home Decor")
                .put("classCode", "1157");

        ArrayNode mockItems = objectMapper.createArrayNode();
        mockItems.add(item1);
        mockItems.add(item2);

        // Mock CosmosPagedIterable with the List of JsonNode objects
        CosmosPagedIterable<JsonNode> mockQueryResults = mock(CosmosPagedIterable.class);
        when(mockQueryResults.iterator()).thenReturn(mockItems.elements());
        when(container.queryItems(any(String.class), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(mockQueryResults);

        List<Map<String, Object>> result = hierarchyRepository.fetchClassCodeData("1157");

        assertEquals(1, result.size());
        Map<String, Object> fetchedItem = result.get(0);
        assertEquals("1157", fetchedItem.get("classCode"));
        assertEquals("Fresh Flowers & Houseplants", fetchedItem.get("displayName"));

        List<Map<String, Object>> hierarchyValues = (List<Map<String, Object>>) fetchedItem.get("hierarchyValues");

            Map<String, Object> firstHierarchyValue = hierarchyValues.get(0);
            String base36IdInHierarchy = (String) firstHierarchyValue.get("base36Id");
            assertEquals("mgr0", base36IdInHierarchy);
            assertEquals("Home Decor", firstHierarchyValue.get("path"));
            assertEquals("de018k", firstHierarchyValue.get("parentBase36Id"));

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(JsonNode.class));
    }


    @Test
    void testFetchAllHierarchyData() {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode item1 = objectMapper.createObjectNode()
                .put("displayName", "Home Decor")
                .put("base36Id", "de018k")
                .put("path", "null")
                .put("classCode", "0010");

        ObjectNode item2 = objectMapper.createObjectNode()
                .put("displayName", "Fresh Flowers & Houseplants")
                .put("base36Id", "mgr0")
                .put("path", "Home Decor")
                .put("classCode", "1157");

        ObjectNode item3 = objectMapper.createObjectNode()
                .put("displayName", "Cut Roses")
                .put("base36Id", "mgr0")
                .put("path", "Home Decor/Fresh Flowers & Houseplants")
                .put("classCode", "1163");

        ArrayNode mockItems = objectMapper.createArrayNode();
        mockItems.add(item1);
        mockItems.add(item2);
        mockItems.add(item3);

        CosmosPagedIterable<JsonNode> mockQueryResults = mock(CosmosPagedIterable.class);
        when(mockQueryResults.iterator()).thenReturn(mockItems.elements());
        when(container.queryItems(any(String.class), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(mockQueryResults);

        List<Map<String, Object>> result = hierarchyRepository.fetchAllHierarchyData();

        assertEquals(3, result.size());
        Map<String, Object> fetchedItem1 = result.get(0);
        assertEquals("0010", fetchedItem1.get("classCode"));
        assertEquals("Home Decor", fetchedItem1.get("displayName"));
        assertEquals("de018k", fetchedItem1.get("base36Id"));
        Object parentBase36Id = fetchedItem1.get("parentBase36Id");
        assertTrue(parentBase36Id == null || "".equals(parentBase36Id),
                "Expected parentBase36Id to be null or empty");

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(JsonNode.class));
    }
    @Test
    void testListAllHierarchyData() {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode item1 = objectMapper.createObjectNode()
                .put("classCode", "0010")
                .put("base36Id", "de018k");

        ObjectNode item2 = objectMapper.createObjectNode()
                .put("classCode", "1157")
                .put("base36Id", "mgr0");

        ArrayNode mockItems = objectMapper.createArrayNode();
        mockItems.add(item1);
        mockItems.add(item2);

        CosmosPagedIterable<JsonNode> mockQueryResults = mock(CosmosPagedIterable.class);
        when(mockQueryResults.iterator()).thenReturn(mockItems.elements());
        when(container.queryItems(any(String.class), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(mockQueryResults);

        List<Map<String, Object>> result = hierarchyRepository.listAllHierarchyData(List.of("0010", "1157"), true);

        assertEquals(2, result.size());
        assertEquals("0010", result.get(0).get("classCode"));
        assertEquals("de018k", result.get(0).get("base36Id"));
        assertEquals("1157", result.get(1).get("classCode"));
        assertEquals("mgr0", result.get(1).get("base36Id"));

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(JsonNode.class));
    }

    @Test
    void testFetchClassCodeData_ClassCodeNotFound() {
        // Mock CosmosPagedIterable with an empty list
        CosmosPagedIterable<JsonNode> mockQueryResults = mock(CosmosPagedIterable.class);
        when(mockQueryResults.iterator()).thenReturn(new ArrayList<JsonNode>().iterator());
        when(container.queryItems(any(String.class), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(mockQueryResults);

        String classCode = "1111";

        List<Map<String, Object>> result = hierarchyRepository.fetchClassCodeData(classCode);

        assertEquals(1, result.size());
        Map<String, Object> classCodeEntry = result.get(0);
        assertEquals(classCode, classCodeEntry.get("classCode"));
        assertNull(classCodeEntry.get("displayName"));

        List<Map<String, Object>> hierarchyValues = (List<Map<String, Object>>) classCodeEntry.get("hierarchyValues");
        assertEquals(1, hierarchyValues.size());

        Map<String, Object> hierarchyItem = hierarchyValues.get(0);
        assertNull(hierarchyItem.get("path"));
        assertEquals("null", hierarchyItem.get("parentBase36Id"));
        assertEquals("null", hierarchyItem.get("base36Id"));

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(JsonNode.class));
    }

}