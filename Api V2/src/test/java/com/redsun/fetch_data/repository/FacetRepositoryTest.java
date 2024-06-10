package com.redsun.fetch_data.repository;


import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.redsun.fetch_data.model.FacetGroup;
import com.azure.cosmos.*;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosContainer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class FacetRepositoryTest {

    private FacetRepository facetRepository;
    private CosmosContainer container;

    @BeforeEach
    public void setup() {
        container = Mockito.mock(CosmosContainer.class);
        CosmosClient cosmosClient = Mockito.mock(CosmosClient.class);
        CosmosDatabase database = Mockito.mock(CosmosDatabase.class);

        when(cosmosClient.getDatabase(any())).thenReturn(database);
        when(database.getContainer(any())).thenReturn(container);

//        facetRepository = new FacetRepository("testEndpoint", "testKey", "testDatabase", "testContainer");
//    }

    @Test
    public void testGetQueryData() {
        List<String> expectedResponse = Arrays.asList("base36Id1", "base36Id2");
        String query = "SELECT c.base36Id FROM c WHERE c.name = 'Category' AND c.classCode = 'H220'";

        when(container.queryItems(eq(query), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(getMockCosmosPagedIterable(expectedResponse));

        List<String> actualResponse = facetRepository.getQueryData("Category", "H220");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testListQueryData() {
        List<String> expectedResponse = Arrays.asList("base36Id1", "base36Id2");
        List<FacetGroup> facetGroups = Arrays.asList(
                new FacetGroup("Category", "H220"),
                new FacetGroup("Category", "1163"),
                new FacetGroup("Category", "H157")
        );

        String query = "SELECT c.base36Id FROM c WHERE " +
                "(c.name = 'Category' AND c.classCode = 'H220') OR " +
                "(c.name = 'Category' AND c.classCode = '1163') OR " +
                "(c.name = 'Category' AND c.classCode = 'H157')";

        when(container.queryItems(eq(query), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(getMockCosmosPagedIterable(expectedResponse));

        List<String> actualResponse = facetRepository.listQueryData(facetGroups);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testSearchQueryData() {
        List<String> expectedResponse = Arrays.asList("base36Id1", "base36Id2");
        String query = "SELECT c.base36Id FROM c WHERE c.name = 'Category'";

        when(container.queryItems(eq(query), any(CosmosQueryRequestOptions.class), eq(JsonNode.class)))
                .thenReturn(getMockCosmosPagedIterable(expectedResponse));

        List<String> actualResponse = facetRepository.searchQueryData("Category");
        assertEquals(expectedResponse, actualResponse);
    }

    private CosmosPagedIterable<JsonNode> getMockCosmosPagedIterable(List<String> response) {
        CosmosPagedIterable<JsonNode> mockIterable = Mockito.mock(CosmosPagedIterable.class);
        ObjectMapper objectMapper = new ObjectMapper();

        when(mockIterable.iterator()).thenReturn(response.stream()
                .map(item -> objectMapper.convertValue("{\"base36Id\": \"" + item + "\"}", JsonNode.class))
                .iterator());

        return mockIterable;
    }
}
