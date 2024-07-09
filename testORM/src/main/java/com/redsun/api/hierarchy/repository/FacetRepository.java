package com.redsun.api.hierarchy.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.redsun.api.hierarchy.model.FacetEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FacetRepository extends CosmosRepository<FacetEntity, String>{

    @Query(value = "SELECT * FROM c WHERE c.pk = 'facets' AND (ARRAY_CONTAINS(@facetTypes, c.facetType))" +
            "AND (@facetValue IS NULL OR c.facetValue = @facetValue)")
    List<FacetEntity> searchFacets(@Param("facetTypes") List<String> facetTypes,
                                   @Param("facetValue") String facetValue);

    @Query("SELECT * FROM c WHERE c.pk = 'facets' OFFSET @offset LIMIT @pageSize")
    List<FacetEntity> listData(@Param("offset") int offset, @Param("pageSize") int pageSize);
}
