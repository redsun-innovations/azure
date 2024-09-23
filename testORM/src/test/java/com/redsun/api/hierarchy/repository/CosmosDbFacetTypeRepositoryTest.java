package com.redsun.api.hierarchy.repository;

import com.azure.cosmos.*;
import com.redsun.api.hierarchy.entity.FacetTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CosmosDbFacetTypeRepositoryTest {
    @Mock
    private CosmosContainer container;

    @Mock
    private FacetTypeRepository mockFacetTypeRepository;
    private CosmosDbFacetTypeRepository facetTypeRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        facetTypeRepository = new CosmosDbFacetTypeRepository(container, mockFacetTypeRepository);
    }

    @Test
    void testFindByFacetType() {
        // Given
        String pk = "facets";
        String facetType = "ats_code";
        String facetValue = "pink";
        FacetTypeEntity mockFacetTypeEntity = new FacetTypeEntity();
        mockFacetTypeEntity.setFacetType(facetType);

        when(mockFacetTypeRepository.findByFacetType(pk, facetType, facetValue))
                .thenReturn(Collections.singletonList(mockFacetTypeEntity));

        // When
        List<FacetTypeEntity> result = facetTypeRepository.findByFacetType(pk, facetType, facetValue);

        // Then
        assertEquals(1, result.size());
        assertEquals("ats_code", result.get(0).getFacetType());
        verify(mockFacetTypeRepository).findByFacetType(pk, facetType, facetValue);
    }

    @Test
    void testHighestBase10() {
        // Given
        String facetType = "ats_code";
        FacetTypeEntity mockFacetTypeEntity = new FacetTypeEntity();
        mockFacetTypeEntity.setBase10("99998750");
        when(mockFacetTypeRepository.findHighestBase10(facetType))
                .thenReturn(Collections.singletonList(mockFacetTypeEntity));

        // When
        List<FacetTypeEntity> result = facetTypeRepository.HighestBase10(facetType);

        // Then
        assertEquals(1, result.size());
        assertEquals("99998750", result.get(0).getBase10());
        verify(mockFacetTypeRepository).findHighestBase10(facetType);
    }

    @Test
    void testParentBase36Id() {
        // Given
        String facetType = "ats_code";
        FacetTypeEntity mockFacetTypeEntity = new FacetTypeEntity();
        mockFacetTypeEntity.setFacetTypebase36Id("2");
        when(mockFacetTypeRepository.findParentBase36Id(facetType))
                .thenReturn(Collections.singletonList(mockFacetTypeEntity));

        // When
        List<FacetTypeEntity> result = facetTypeRepository.parentBase36Id(facetType);

        // Then
        assertEquals(1, result.size());
        assertEquals("2", result.get(0).getFacetTypebase36Id());
        verify(mockFacetTypeRepository).findParentBase36Id(facetType);
    }

    @Test
    void testSaveFacetType() {
        // Given
        FacetTypeEntity facetTypeEntity = new FacetTypeEntity();
        facetTypeEntity.setId(null); // Simulating a new entity

        // When
        facetTypeRepository.saveFacet(facetTypeEntity);

        // Then
        assertNotNull(facetTypeEntity.getId()); // Verify that a new ID has been generated
        verify(mockFacetTypeRepository).save(facetTypeEntity); // Verify save was called
    }

    @Test
    void testFindByFacetTypeThrowsException() {
        // Given
        String pk = "facets";
        String facetType = "ats_code";
        String facetValue = "pink";

        // Simulating an exception being thrown
        when(mockFacetTypeRepository.findByFacetType(pk, facetType, facetValue))
                .thenThrow(new RuntimeException("No records found for the given facetType"));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            facetTypeRepository.findByFacetType(pk, facetType, facetValue);
        });

        assertEquals("No records found for the given facetType", exception.getMessage());
        verify(mockFacetTypeRepository).findByFacetType(pk, facetType, facetValue);
    }

    @Test
    void testHighestBase10ThrowsException() {
        // Given
        String facetType = "ats_code";

        // Simulating an exception being thrown
        when(mockFacetTypeRepository.findHighestBase10(facetType))
                .thenThrow(new RuntimeException("Base10 not found for the given facetType"));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            facetTypeRepository.HighestBase10(facetType);
        });

        assertEquals("Base10 not found for the given facetType", exception.getMessage());
        verify(mockFacetTypeRepository).findHighestBase10(facetType);
    }

    @Test
    void testParentBase36IdThrowsException() {
        // Given
        String facetType = "ats_code";

        // Simulating an exception being thrown
        when(mockFacetTypeRepository.findParentBase36Id(facetType))
                .thenThrow(new RuntimeException("Base36Id not found for the given facetType"));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            facetTypeRepository.parentBase36Id(facetType);
        });

        assertEquals("Base36Id not found for the given facetType", exception.getMessage());
        verify(mockFacetTypeRepository).findParentBase36Id(facetType);
    }

    @Test
    void testSaveFacetTypeThrowsException() {
        // Given
        FacetTypeEntity facetTypeEntity = new FacetTypeEntity();
        facetTypeEntity.setId(null); // Simulating a new entity

        // Simulating an exception being thrown
        doThrow(new RuntimeException("Error occured while adding facetType")).when(mockFacetTypeRepository).save(facetTypeEntity);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            facetTypeRepository.saveFacet(facetTypeEntity);
        });

        assertEquals("Error occured while adding facetType", exception.getMessage());
        verify(mockFacetTypeRepository).save(facetTypeEntity);
    }

}
