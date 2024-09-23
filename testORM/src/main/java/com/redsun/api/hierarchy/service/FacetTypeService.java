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

    public String addFacetType(String facetType, String facetValue) {
        try {
            String pk = facetValue == null || facetValue.isEmpty() ? "facetType" : "facets";
            List<FacetTypeEntity> existingFacet = cosmosDbFacetTypeRepository.findByFacetType(pk, facetType, facetValue);
            if (existingFacet != null && !existingFacet.isEmpty()) {
                return "facetType '" + facetType + "' is already present.";
            }
            List<FacetTypeEntity> highestBase10Entity = cosmosDbFacetTypeRepository.HighestBase10(facetType);
            String maxBase10 = highestBase10Entity.isEmpty() ? "0" : highestBase10Entity.get(0).getBase10();
            int base10Value = Integer.parseInt(maxBase10) + 1;

            List<FacetTypeEntity> ParentBase36Id = cosmosDbFacetTypeRepository.parentBase36Id(facetType);
            String parentBase36Id = !ParentBase36Id.isEmpty() ? ParentBase36Id.get(0).getBase36Id() : null;

            FacetTypeEntity newFacet = new FacetTypeEntity();
            newFacet.setPk(pk);
            newFacet.setFacetType(facetType);
            if (facetValue != null && !facetValue.isEmpty()) {
                newFacet.setFacetValue(facetValue);
                newFacet.setBase10(String.valueOf(Integer.parseInt(maxBase10) + 1));
                newFacet.setBase36Id(Integer.toString(base10Value, 36));
                newFacet.setFacetTypebase36Id(parentBase36Id);
            } else {
                newFacet.setFacetTypeDescription(facetType.replace("_", "").toUpperCase());
                newFacet.setBase10(String.valueOf(Integer.parseInt(maxBase10) + 1));
                newFacet.setBase36Id(Integer.toString(base10Value, 36));
            }

            newFacet.setPriority("0");
            newFacet.setSeoTitle("");
            newFacet.setSeoCanonicalUrl("");
            newFacet.setSeoContent("");
            newFacet.setMetaDescription("");
            newFacet.setMetaKeywords("");

            cosmosDbFacetTypeRepository.saveFacet(newFacet);
            return "facetType + facetType + has been successfully added.";
        } catch (Exception e) {
            logger.error("Error adding facetType '{}': '{}'", facetType, e.getMessage(), e);
            return "Error: Unable to add facet type - " + e.getMessage();
        }
    }
}
