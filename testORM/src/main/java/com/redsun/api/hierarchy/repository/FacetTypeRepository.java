package com.redsun.api.hierarchy.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.redsun.api.hierarchy.entity.FacetTypeEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacetTypeRepository extends CosmosRepository<FacetTypeEntity, String> {
    @Query("SELECT * FROM C WHERE c.pk @pk AND c. facetType = @facetType AND (@facetValue null or c.facetValue = @facetValue)")
            List<FacetTypeEntity> findByFacetType(
            @Param("pk") String pk,
            @Param("facetType") String facetType,
            @Param("facetValue") String facetValue
    );
    @Query (value = "SELECT TOP 1 c.base10 FROM C ORDER BY c.base10 DESC")
    List<FacetTypeEntity> findHighestBase10(@Param("facetType") String facetType);

    @Query (value = "SELECT c.base36Id FROM c WHERE c.pk 'facetType' AND c. facetType = @facetType")
    List<FacetTypeEntity> findParentBase36Id(
            @Param("facetType") String facetType
    );
}