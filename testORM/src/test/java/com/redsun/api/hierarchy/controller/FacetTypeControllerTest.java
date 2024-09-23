package com.redsun.api.hierarchy.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.redsun.api.hierarchy.service.FacetTypeService;
import com.redsun.api.hierarchy.entity.FacetTypeEntity;

public class FacetTypeControllerTest {
    @Mock
    private FacetTypeService facetTypeService;

    @InjectMocks
    private FacetTypeController facetTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);  // Initialize mocks
    }

    @Test
    void testAddFacetTypeAndFacetValue() {
        // Arrange
        FacetTypeEntity addFacet = new FacetTypeEntity();
        addFacet.setFacetType("ats_code");
        addFacet.setFacetValue("pink");

        String expectedResponse = "Facet type 'ats_code' has been added successfully";
        when(facetTypeService.addFacetType("ats_code", "pink")).thenReturn(expectedResponse);

        // Act
        ResponseEntity<String> responseEntity = facetTypeController.addFacetType(addFacet);

        // Assert
        assertNotNull(responseEntity);  // Ensure response is not null
        assertEquals(200, responseEntity.getStatusCodeValue());  // Check HTTP status code
        assertEquals(expectedResponse, responseEntity.getBody());  // Check response body

        // Verify interaction with the service
        verify(facetTypeService, times(1)).addFacetType("ats_code", "pink");
    }


    @Test
    void testAddFacetTypeException() {
        // Arrange
        FacetTypeEntity addFacet = new FacetTypeEntity();
        addFacet.setFacetType("ats_code");
        addFacet.setFacetValue("pink");

        when(facetTypeService.addFacetType("ats_code", "pink")).thenThrow(new RuntimeException("Error creating facet type entity"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            facetTypeController.addFacetType(addFacet);
        });

        // Assert exception details
        assertEquals("Error creating facet type entity", exception.getMessage());

        // Verify interaction with the service
        verify(facetTypeService, times(1)).addFacetType("ats_code", "pink");
    }

    @Test
    void testAddFacetTypeWithOnlyFacetType() {
        // Arrange
        FacetTypeEntity addFacet = new FacetTypeEntity();
        addFacet.setFacetType("ats_code");
        addFacet.setFacetValue(null);  // facetValue is null

        String expectedResponse = "Facet type 'ats_code' has been added successfully ";
        when(facetTypeService.addFacetType("ats_code", null)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<String> responseEntity = facetTypeController.addFacetType(addFacet);

        // Assert
        assertNotNull(responseEntity);  // Ensure response is not null
        assertEquals(200, responseEntity.getStatusCodeValue());  // Check HTTP status code
        assertEquals(expectedResponse, responseEntity.getBody());  // Check response body

        // Verify interaction with the service
        verify(facetTypeService, times(1)).addFacetType("ats_code", null);
    }

    @Test
    void testAddFacetTypeWithOnlyFacetTypeException() {
        // Arrange
        FacetTypeEntity addFacet = new FacetTypeEntity();
        addFacet.setFacetType("ats_code");
        addFacet.setFacetValue(null);  // facetValue is null

        // Simulate an exception being thrown by the service
        when(facetTypeService.addFacetType("ats_code", null)).thenThrow(new RuntimeException("Error creating facet type entity"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            facetTypeController.addFacetType(addFacet);
        });

        // Assert exception details
        assertEquals("Error creating facet type entity", exception.getMessage());

        // Verify interaction with the service
        verify(facetTypeService, times(1)).addFacetType("ats_code", null);
    }


}
