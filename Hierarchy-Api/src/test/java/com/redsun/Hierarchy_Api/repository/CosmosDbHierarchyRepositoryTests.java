package com.redsun.Hierarchy_Api.repository;

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
public class CosmosDbHierarchyRepositoryTests {

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
    public void testFetchClassCodeData() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Create the first JsonNode object
        ObjectNode item1 = objectMapper.createObjectNode()
                .put("displayName", "Home Decor")
                .put("base36Id", "de018k")
                .put("path", "null")
                .put("classCode", "0010");

        // Create the second JsonNode object
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


        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(JsonNode.class));
    }

    @Test
    public void testFetchAllHierarchyData() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode item1 = objectMapper.createObjectNode()
                .put("displayName", "Home Decor")
                .put("base36Id", "de018k")
                .put("path", "null");

        // Create the second JsonNode object
        ObjectNode item2 = objectMapper.createObjectNode()
                .put("displayName", "Fresh Flowers & Houseplants")
                .put("base36Id", "mgr0")
                .put("path", "Home Decor");

        // Create the third JsonNode object
        ObjectNode item3 = objectMapper.createObjectNode()
                .put("displayName", "Cut Roses")
                .put("base36Id", "mgr0")
                .put("path", "Home Decor/Fresh Flowers & Houseplants");

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
        assertEquals("Home Decor", fetchedItem1.get("displayName"));
        assertEquals("de018k", fetchedItem1.get("base36Id"));
        Object parentBase36Id = fetchedItem1.get("parentBase36Id");
        assertTrue(parentBase36Id == null || "null".equals(parentBase36Id),
                "Expected parentBase36Id to be null or \"null\"");

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(JsonNode.class));
    }

    @Test
    public void testListAllHierarchyData() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode item1 = objectMapper.createObjectNode()
                .put("classCode", "0010")
                .put("base36Id", "de018k");

        // Create the second JsonNode object
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
}
