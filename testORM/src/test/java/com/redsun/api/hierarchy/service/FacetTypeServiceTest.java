package com.redsun.api.hierarchy.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.redsun.api.hierarchy.entity.FacetTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;

import com.redsun.api.hierarchy.repository.CosmosDbFacetTypeRepository;

public class FacetTypeServiceTest {
    @Mock
    private CosmosDbFacetTypeRepository cosmosDbFacetTypeRepository;

    @InjectMocks
    private FacetTypeService facetTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);  // Initialize mocks
    }

    @Test
    void testAddFacetTypeAlreadyExists() {
        // Arrange
        String facetType = "ats_code";
        String facetValue = "pink";
        List<FacetTypeEntity> existingFacet = new ArrayList<>();
        existingFacet.add(new FacetTypeEntity());  // Mock existing facet
        when(cosmosDbFacetTypeRepository.findByFacetType("facets", facetType, facetValue)).thenReturn(existingFacet);

        // Act
        String result = facetTypeService.addFacetType(facetType, facetValue);

        // Assert
        assertEquals("facetType 'ats_code' is already present.", result);
        verify(cosmosDbFacetTypeRepository, times(1)).findByFacetType("facets", facetType, facetValue);
    }

//    @Test
//    void testAddFacetTypeWithFacetValue() {
//        // Arrange
//        String facetType = "ats_code";
//        String facetValue = "pink";
//
//        // Mocking a list with a single entity for HighestBase10
//        List<FacetTypeEntity> highestBase10List = new ArrayList<>();
//        FacetTypeEntity base10Entity = new FacetTypeEntity();
//        base10Entity.setBase10("99998750");  // Simulating the highest base10 value as "123"
//        highestBase10List.add(base10Entity);
//        when(cosmosDbFacetTypeRepository.HighestBase10(facetType)).thenReturn(highestBase10List);
//
//        // Mocking a list with a parent base36Id for parentBase36Id
//        List<FacetTypeEntity> parentBase36IdList = new ArrayList<>();
//        FacetTypeEntity parentEntity = new FacetTypeEntity();
//        parentEntity.setBase36Id("2");  // Simulating parent base36Id as "xyz"
//        parentBase36IdList.add(parentEntity);
//        when(cosmosDbFacetTypeRepository.parentBase36Id(facetType)).thenReturn(parentBase36IdList);
//
//        // Act
//        String result = facetTypeService.addFacetType(facetType, facetValue);
//
//        // Assert
//        assertEquals("facetType + facetType + has been successfully added.", result);
//        assertEquals("99998750", highestBase10List.get(0).getBase10());
//        assertEquals("2", parentBase36IdList.get(0).getBase36Id());
//
//        // Additional verification to check that the repository methods were called correctly
//        verify(cosmosDbFacetTypeRepository, times(1)).HighestBase10(facetType);
//        verify(cosmosDbFacetTypeRepository, times(1)).parentBase36Id(facetType);
//    }

//    @Test
//    void testAddFacetTypeWithNullFacetValue() {
//        // Arrange
//        String facetType = "ats_code";
//        String facetValue = null;  // Simulating a null facet value
//
//        // Mocking a list with a single entity for HighestBase10
//        List<FacetTypeEntity> highestBase10List = new ArrayList<>();
//        FacetTypeEntity base10Entity = new FacetTypeEntity();
//        base10Entity.setBase10("99998750");  // Simulating the highest base10 value as "123"
//        highestBase10List.add(base10Entity);
//        when(cosmosDbFacetTypeRepository.HighestBase10(facetType)).thenReturn(highestBase10List);
//
//        // Mocking a list with a parent base36Id for parentBase36Id
//        List<FacetTypeEntity> parentBase36IdList = new ArrayList<>();
//        FacetTypeEntity parentEntity = new FacetTypeEntity();
//        parentEntity.setBase36Id("2");  // Simulating parent base36Id as "xyz"
//        parentBase36IdList.add(parentEntity);
//        when(cosmosDbFacetTypeRepository.parentBase36Id(facetType)).thenReturn(parentBase36IdList);
//
//        String result = facetTypeService.addFacetType(facetType, facetValue);
//
//
//        // Check that the appropriate exception message is thrown
//        assertEquals("facetType + facetType + has been successfully added.", result);
//        assertEquals("99998750", highestBase10List.get(0).getBase10());
//        assertEquals("2", parentBase36IdList.get(0).getBase36Id());
//
//        // Additional verification to check that no repository methods were called due to null facetValue
//        verify(cosmosDbFacetTypeRepository, times(1)).HighestBase10(facetType);
//        verify(cosmosDbFacetTypeRepository, times(1)).parentBase36Id(facetType);
//    }

    @Test
    void testAddFacetTypeWithFacetValue() {
        // Arrange
        String facetType = "ats_code";
        String facetValue = "pink";

        // Mocking a list with a single entity for HighestBase10
        List<FacetTypeEntity> highestBase10List = new ArrayList<>();
        FacetTypeEntity base10Entity = new FacetTypeEntity();
        base10Entity.setBase10("99998750");
        highestBase10List.add(base10Entity);
        when(cosmosDbFacetTypeRepository.HighestBase10(facetType)).thenReturn(highestBase10List);

        // Mocking a list with a parent base36Id for parentBase36Id
        List<FacetTypeEntity> parentBase36IdList = new ArrayList<>();
        FacetTypeEntity parentEntity = new FacetTypeEntity();
        parentEntity.setBase36Id("2");
        parentBase36IdList.add(parentEntity);
        when(cosmosDbFacetTypeRepository.parentBase36Id(facetType)).thenReturn(parentBase36IdList);

        // Act
        String result = facetTypeService.addFacetType(facetType, facetValue);

        // Assert
        assertEquals("facetType + facetType + has been successfully added.", result);
        assertEquals("99998750", highestBase10List.get(0).getBase10());
        assertEquals("2", parentBase36IdList.get(0).getBase36Id());

        // Capturing the argument passed to the saveFacet method
        ArgumentCaptor<FacetTypeEntity> captor = ArgumentCaptor.forClass(FacetTypeEntity.class);
        verify(cosmosDbFacetTypeRepository).saveFacet(captor.capture());

        // Validate the captured entity
        FacetTypeEntity savedFacet = captor.getValue();
        assertEquals("facets", savedFacet.getPk());
        assertEquals(facetType, savedFacet.getFacetType());
        assertEquals(facetValue, savedFacet.getFacetValue());
        assertEquals("99998751", savedFacet.getBase10()); // base10 incremented by 1
        assertEquals(Integer.toString(99998751, 36), savedFacet.getBase36Id());
        assertEquals("2", savedFacet.getFacetTypebase36Id());

        // Verify repository method calls
        verify(cosmosDbFacetTypeRepository, times(1)).HighestBase10(facetType);
        verify(cosmosDbFacetTypeRepository, times(1)).parentBase36Id(facetType);
    }

    @Test
    void testAddFacetTypeWithNullFacetValue() {
        // Arrange
        String facetType = "ats_code";
        String facetValue = null;

        // Mocking a list with a single entity for HighestBase10
        List<FacetTypeEntity> highestBase10List = new ArrayList<>();
        FacetTypeEntity base10Entity = new FacetTypeEntity();
        base10Entity.setBase10("99998750");
        highestBase10List.add(base10Entity);
        when(cosmosDbFacetTypeRepository.HighestBase10(facetType)).thenReturn(highestBase10List);

        // Mocking a list with a parent base36Id for parentBase36Id
        List<FacetTypeEntity> parentBase36IdList = new ArrayList<>();
        FacetTypeEntity parentEntity = new FacetTypeEntity();
        parentEntity.setBase36Id("2");
        parentBase36IdList.add(parentEntity);
        when(cosmosDbFacetTypeRepository.parentBase36Id(facetType)).thenReturn(parentBase36IdList);

        // Act
        String result = facetTypeService.addFacetType(facetType, facetValue);

        // Assert
        assertEquals("facetType + facetType + has been successfully added.", result);
        assertEquals("99998750", highestBase10List.get(0).getBase10());
        assertEquals("2", parentBase36IdList.get(0).getBase36Id());

        // Capturing the argument passed to the saveFacet method
        ArgumentCaptor<FacetTypeEntity> captor = ArgumentCaptor.forClass(FacetTypeEntity.class);
        verify(cosmosDbFacetTypeRepository).saveFacet(captor.capture());

        // Validate the captured entity
        FacetTypeEntity savedFacet = captor.getValue();
        assertEquals("facetType", savedFacet.getPk()); // facetType is the partition key when facetValue is null
        assertEquals(facetType, savedFacet.getFacetType());
        assertNull(savedFacet.getFacetValue()); // facetValue is null
        assertEquals("99998751", savedFacet.getBase10()); // base10 incremented by 1
        assertEquals(Integer.toString(99998751, 36), savedFacet.getBase36Id());
        assertNull(savedFacet.getFacetTypebase36Id());

        // Verify repository method calls
        verify(cosmosDbFacetTypeRepository, times(1)).HighestBase10(facetType);
        verify(cosmosDbFacetTypeRepository, times(1)).parentBase36Id(facetType);
    }

    @Test
    void testAddFacetTypeException() {
        // Arrange
        String facetType = "ats_code";
        String facetValue = "pink";

        // Mocking a list with a single entity for HighestBase10
        List<FacetTypeEntity> highestBase10List = new ArrayList<>();
        FacetTypeEntity base10Entity = new FacetTypeEntity();
        base10Entity.setBase10("99998750");
        highestBase10List.add(base10Entity);
        when(cosmosDbFacetTypeRepository.HighestBase10(facetType)).thenReturn(highestBase10List);

        // Mocking a list with a parent base36Id for parentBase36Id
        List<FacetTypeEntity> parentBase36IdList = new ArrayList<>();
        FacetTypeEntity parentEntity = new FacetTypeEntity();
        parentEntity.setBase36Id("2");
        parentBase36IdList.add(parentEntity);
        when(cosmosDbFacetTypeRepository.parentBase36Id(facetType)).thenReturn(parentBase36IdList);

        // Simulate exception when saving the facet
        doThrow(new RuntimeException("Database save error")).when(cosmosDbFacetTypeRepository).saveFacet(any(FacetTypeEntity.class));

        // Act
        String result = facetTypeService.addFacetType(facetType, facetValue);

        // Assert
        assertEquals("Error: Unable to add facet type - Database save error", result);

        // Verify that saveFacet method was called
        verify(cosmosDbFacetTypeRepository).saveFacet(any(FacetTypeEntity.class));
    }

    @Test
    void testAddFacetTypeHighestBase10Exception() {
        // Arrange
        String facetType = "ats_code";
        String facetValue = "pink";

        // Simulate exception when calling HighestBase10
        when(cosmosDbFacetTypeRepository.HighestBase10(facetType)).thenThrow(new RuntimeException("Database base10 error"));

        // Act
        String result = facetTypeService.addFacetType(facetType, facetValue);

        // Assert
        assertEquals("Error: Unable to add facet type - Database base10 error", result);

        // Verify that HighestBase10 method was called
        verify(cosmosDbFacetTypeRepository).HighestBase10(facetType);
    }

    @Test
    void testAddFacetTypeParentBase36IdException() {
        // Arrange
        String facetType = "ats_code";
        String facetValue = "pink";

        // Mocking a list with a single entity for HighestBase10
        List<FacetTypeEntity> highestBase10List = new ArrayList<>();
        FacetTypeEntity base10Entity = new FacetTypeEntity();
        base10Entity.setBase10("99998750");
        highestBase10List.add(base10Entity);
        when(cosmosDbFacetTypeRepository.HighestBase10(facetType)).thenReturn(highestBase10List);

        // Simulate exception when calling parentBase36Id
        when(cosmosDbFacetTypeRepository.parentBase36Id(facetType)).thenThrow(new RuntimeException("Database base36Id error"));

        // Act
        String result = facetTypeService.addFacetType(facetType, facetValue);

        // Assert
        assertEquals("Error: Unable to add facet type - Database base36Id error", result);

        // Verify that HighestBase10 was called, but parentBase36Id throws an exception
        verify(cosmosDbFacetTypeRepository).HighestBase10(facetType);
        verify(cosmosDbFacetTypeRepository).parentBase36Id(facetType);
    }

    @Test
    void testAddFacetTypeFindByFacetTypeException() {
        // Arrange
        String facetType = "ats_code";
        String facetValue = "pink";

        // Simulate exception when calling findByFacetType
        when(cosmosDbFacetTypeRepository.findByFacetType("facets", facetType, facetValue)).thenThrow(new RuntimeException("Database find error"));

        // Act
        String result = facetTypeService.addFacetType(facetType, facetValue);

        // Assert
        assertEquals("Error: Unable to add facet type - Database find error", result);

        // Verify that findByFacetType method was called
        verify(cosmosDbFacetTypeRepository).findByFacetType("facets", facetType, facetValue);
    }


}
