package com.redsun.api.hierarchy.repository;

import com.azure.cosmos.CosmosContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redsun.api.hierarchy.entity.FacetEntity;
import com.redsun.api.hierarchy.entity.FacetTypeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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


    public List<FacetTypeEntity> findByFacetType(String facetType) {
        return facetTypeRepository.findByFacetType(facetType);
    }

    public void saveFacet(FacetTypeEntity facetTypeEntity) {
        facetTypeRepository.save(facetTypeEntity);
    }

}
