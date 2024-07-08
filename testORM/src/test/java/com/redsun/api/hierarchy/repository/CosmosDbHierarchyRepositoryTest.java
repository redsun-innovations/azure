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
import com.redsun.api.hierarchy.constant.ConstantTest;
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

    @InjectMocks
    private CosmosDbHierarchyRepository hierarchyRepository;
zz

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        hierarchyRepository  = new CosmosDbHierarchyRepository(container,hierarchyRepository);
    }

    @Test
    void testFetchClassCodeData()  {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode item1 = objectMapper.createObjectNode()
                .put(ConstantTest.DISPLAYNAME, ConstantTest.HOME_DECOR)
                .put(ConstantTest.BASE36ID, ConstantTest.DE018K)
                .put("path", "null")
                .put(ConstantTest.CLASSCODE, "0010");

        JsonNode item2 = objectMapper.createObjectNode()
                .put(ConstantTest.DISPLAYNAME, ConstantTest.FRESH_FLOWERS)
                .put(ConstantTest.BASE36ID, "mgr0")
                .put("path", ConstantTest.HOME_DECOR)
                .put(ConstantTest.CLASSCODE, "1157");

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
        assertEquals("1157", fetchedItem.get(ConstantTest.CLASSCODE));
        assertEquals(ConstantTest.FRESH_FLOWERS, fetchedItem.get(ConstantTest.DISPLAYNAME));

        List<Map<String, Object>> hierarchyValues = (List<Map<String, Object>>) fetchedItem.get("hierarchyValues");

            Map<String, Object> firstHierarchyValue = hierarchyValues.get(0);
            String base36IdInHierarchy = (String) firstHierarchyValue.get(ConstantTest.BASE36ID);
        assertEquals("mgr0", base36IdInHierarchy);
        assertEquals(ConstantTest.HOME_DECOR, firstHierarchyValue.get("path"));
        assertEquals(ConstantTest.DE018K, firstHierarchyValue.get(ConstantTest.PARENTBASE36ID));

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(JsonNode.class));
    }


    @Test
    void testFetchAllHierarchyData() {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode item1 = objectMapper.createObjectNode()
                .put(ConstantTest.DISPLAYNAME, ConstantTest.HOME_DECOR)
                .put(ConstantTest.BASE36ID, ConstantTest.DE018K)
                .put("path", "null")
                .put(ConstantTest.CLASSCODE, "0010");

        JsonNode item2 = objectMapper.createObjectNode()
                .put(ConstantTest.DISPLAYNAME, ConstantTest.FRESH_FLOWERS)
                .put(ConstantTest.BASE36ID, "mgr0")
                .put("path", ConstantTest.HOME_DECOR)
                .put(ConstantTest.CLASSCODE, "1157");

        JsonNode item3 = objectMapper.createObjectNode()
                .put(ConstantTest.DISPLAYNAME, "Cut Roses")
                .put(ConstantTest.BASE36ID, "mgr0")
                .put("path", "Home Decor/Fresh Flowers & Houseplants")
                .put(ConstantTest.CLASSCODE, "1163");

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
        assertEquals(ConstantTest.HOME_DECOR, fetchedItem1.get(ConstantTest.DISPLAYNAME));
        assertEquals(ConstantTest.DE018K, fetchedItem1.get(ConstantTest.BASE36ID));
        Object parentBase36Id = fetchedItem1.get(ConstantTest.PARENTBASE36ID);
        assertTrue(parentBase36Id == null || "".equals(parentBase36Id),
                "Expected parentBase36Id to be null or empty");

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(JsonNode.class));
    }
    @Test
    void testListAllHierarchyData() {

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode item1 = objectMapper.createObjectNode()
                .put(ConstantTest.CLASSCODE, "0010")
                .put(ConstantTest.BASE36ID, ConstantTest.DE018K);

        ObjectNode item2 = objectMapper.createObjectNode()
                .put(ConstantTest.CLASSCODE, "1157")
                .put(ConstantTest.BASE36ID, "mgr0");

        ArrayNode mockItems = objectMapper.createArrayNode();
        mockItems.add(item1);
        mockItems.add(item2);

        CosmosPagedIterable<JsonNode> mockQueryResults = mock(CosmosPagedIterable.class);
        when(mockQueryResults.iterator()).thenReturn(mockItems.elements());
        when(container.queryItems(any(String.class), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(mockQueryResults);

        List<Map<String, Object>> result = hierarchyRepository.listAllHierarchyData(List.of("0010", "1157"), true);

        assertEquals(2, result.size());
        assertEquals("0010", result.get(0).get(ConstantTest.CLASSCODE));
        assertEquals(ConstantTest.DE018K, result.get(0).get(ConstantTest.BASE36ID));
        assertEquals("1157", result.get(1).get(ConstantTest.CLASSCODE));
        assertEquals("mgr0", result.get(1).get(ConstantTest.BASE36ID));

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(JsonNode.class));
    }

    @Test
    void testFetchClassCodeDataNotFound() {
        // Mock CosmosPagedIterable with an empty list
        CosmosPagedIterable<JsonNode> mockQueryResults = mock(CosmosPagedIterable.class);
        when(mockQueryResults.iterator()).thenReturn(new ArrayList<JsonNode>().iterator());
        when(container.queryItems(any(String.class), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(mockQueryResults);

        String classCode = "1111";

        List<Map<String, Object>> result = hierarchyRepository.fetchClassCodeData(classCode);

        assertEquals(1, result.size());
        Map<String, Object> classCodeEntry = result.get(0);
        assertEquals(classCode, classCodeEntry.get(ConstantTest.CLASSCODE));
        assertNull(classCodeEntry.get(ConstantTest.DISPLAYNAME));

        List<Map<String, Object>> hierarchyValues = (List<Map<String, Object>>) classCodeEntry.get("hierarchyValues");
        assertEquals(1, hierarchyValues.size());

        Map<String, Object> hierarchyItem = hierarchyValues.get(0);
        assertNull(hierarchyItem.get("path"));
        assertEquals("null", hierarchyItem.get(ConstantTest.PARENTBASE36ID));
        assertEquals("null", hierarchyItem.get(ConstantTest.BASE36ID));

        verify(container, times(1)).queryItems(anyString(), any(CosmosQueryRequestOptions.class), eq(JsonNode.class));
    }

}