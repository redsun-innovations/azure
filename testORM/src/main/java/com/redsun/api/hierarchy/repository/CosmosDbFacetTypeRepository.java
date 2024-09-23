package com.redsun.api.hierarchy.repository;

import com.azure.cosmos.CosmosContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redsun.api.hierarchy.entity.FacetEntity;
import com.redsun.api.hierarchy.entity.FacetTypeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class CosmosDbFacetTypeRepository {

    private final CosmosContainer container;
    private final ObjectMapper objectMapper;

    private final FacetTypeRepository facetTypeRepository;

    private static final Logger logger = LoggerFactory.getLogger(CosmosDbFacetTypeRepository.class);

    /**
     * Constructor for CosmosDbFacetRepository.
     *
     * @param container the CosmosContainer instance for interacting with Azure Cosmos DB
     */

    public CosmosDbFacetTypeRepository(CosmosContainer container, FacetTypeRepository facetTypeRepository) {
        this.container = container;
        this.objectMapper = new ObjectMapper();
        this.facetTypeRepository = facetTypeRepository;
    }


    public List<FacetTypeEntity> findByFacetType(String pk, String facetType, String facetValue) {
        try {
            return facetTypeRepository.findByFacetType(pk, facetType, facetValue);
        } catch (Exception e) {
            logger.error("Error finding facet type with pk '{}', facetType '{}', and facetValue '{}':{}", pk, facetType, facetValue, e.getMessage(), e);
            throw e;
        }
    }

    public void saveFacet(FacetTypeEntity facetTypeEntity) {
        try {
            if (facetTypeEntity.getId() == null || facetTypeEntity.getId().isEmpty()) {
                facetTypeEntity.setId(UUID.randomUUID().toString());
            }
            facetTypeRepository.save(facetTypeEntity);
        } catch (Exception e) {
            logger.error("Error saving facet type entity: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<FacetTypeEntity> HighestBase10(String facetType) {
        try {
            return facetTypeRepository.findHighestBase10(facetType); } catch (Exception e) {
            logger.error("Error finding highest base10 for facetType '{}': {}", facetType, e.getMessage(), e);
            throw e;
        }
    }
    public List<FacetTypeEntity> parentBase36Id(String facetType) {
        try {
            return facetTypeRepository.findParentBase36Id(facetType);
        } catch (Exception e) {
            logger.error("Error finding parent base36Id for facetType '{}': {}", facetType, e.getMessage(), e);
            throw e;
        }
    }
}


