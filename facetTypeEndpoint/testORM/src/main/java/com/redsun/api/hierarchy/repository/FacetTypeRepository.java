package com.redsun.api.hierarchy.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.redsun.api.hierarchy.entity.FacetTypeEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacetTypeRepository extends CosmosRepository<FacetTypeEntity, String> {
    @Query("SELECT * FROM c WHERE c.pk = 'facetType' AND c.facetType = @facetType")
    List<FacetTypeEntity> findByFacetType(@Param("facetType") String facetType);
}