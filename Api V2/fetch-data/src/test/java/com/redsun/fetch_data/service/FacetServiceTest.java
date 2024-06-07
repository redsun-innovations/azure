package com.redsun.fetch_data.service;

import com.redsun.fetch_data.model.FacetGroup;
import com.redsun.fetch_data.model.FacetGroupTest;
import com.redsun.fetch_data.repository.FacetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
public class FacetServiceTest {

    private FacetRepository facetRepository;
    private FacetService facetService;

    @BeforeEach
    public void setup() {
        facetRepository = Mockito.mock(FacetRepository.class);
        facetService = new FacetService(facetRepository);
    }

    @Test
    public void testGetQueryData() {
        List<String> expectedResponse = Arrays.asList("base36Id1", "base36Id2");
        when(facetRepository.getQueryData("Category", "1163")).thenReturn(expectedResponse);

        List<String> actualResponse = facetService.getQueryData("Category", "1163");
        assertEquals(expectedResponse, actualResponse);

        verify(facetRepository, times(1)).getQueryData("Category", "1163");
    }

    @Test
    public void testListQueryData() {
        List<String> expectedResponse = Arrays.asList("base36Id1", "base36Id2");
        List<FacetGroup> facetGroups = Arrays.asList(
                new FacetGroup  ("Category", "H220"),
                new FacetGroup("Category", "1163"),
                new FacetGroup("Category", "H157")
        );

        when(facetRepository.listQueryData(facetGroups)).thenReturn(expectedResponse);

        List<String> actualResponse = facetService.listQueryData(facetGroups);
        assertEquals(expectedResponse, actualResponse);

        verify(facetRepository, times(1)).listQueryData(facetGroups);
    }

    @Test
    public void testSearchQueryData() {
        List<String> expectedResponse = Arrays.asList("base36Id1", "base36Id2");
        when(facetRepository.searchQueryData("Category")).thenReturn(expectedResponse);

        List<String> actualResponse = facetService.searchQueryData("Category");
        assertEquals(expectedResponse, actualResponse);

        verify(facetRepository, times(1)).searchQueryData("Category");
    }
}
