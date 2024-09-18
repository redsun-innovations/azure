package com.redsun.api.hierarchy.service;

import com.redsun.api.hierarchy.entity.FacetTypeEntity;

import com.redsun.api.hierarchy.repository.CosmosDbFacetTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FacetTypeService {

    private static final Logger logger = LoggerFactory.getLogger(FacetService.class);
    private final CosmosDbFacetTypeRepository cosmosDbFacetTypeRepository;

    /**
     * Constructor for FacetService.
     * <p>
     * Initializes the service with the given FacetRepository.
     *
     * @param cosmosDbFacetTypeRepository the repository used for querying facets
     */
    @Autowired
    public FacetTypeService(CosmosDbFacetTypeRepository cosmosDbFacetTypeRepository) {
        this.cosmosDbFacetTypeRepository = cosmosDbFacetTypeRepository;
    }

    public String addFacetType(String facetType, String facetTypeDescription) {
        // Check if the facetType exists
        List<FacetTypeEntity> existingFacet = cosmosDbFacetTypeRepository.findByFacetType(facetType);

        if (existingFacet != null && !existingFacet.isEmpty()) {
            return "facetType '" + facetType + "' is already present.";
        }

        // Insert new facet if it doesn't exist
        FacetTypeEntity newFacet = new FacetTypeEntity();
        newFacet.setPk("facetType");
        newFacet.setFacetType(facetType);
        newFacet.setFacetTypeDescription(facetTypeDescription);
        newFacet.setBase10("1");
        newFacet.setBase36Id("1");
        newFacet.setPriority("0");
        newFacet.setSeoTitle("");
        newFacet.setSeoCanonicalUrl("");
        newFacet.setSeoContent("");
        newFacet.setMetaDescription("");
        newFacet.setMetaKeywords("");

        // Save to DB via the repository
        cosmosDbFacetTypeRepository.saveFacet(newFacet);

        return "facetType '" + facetType + "' has been successfully added.";
    }
}
