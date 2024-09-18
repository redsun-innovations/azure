package com.redsun.api.hierarchy.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.redsun.api.hierarchy.entity.FacetEntity;
import com.redsun.api.hierarchy.entity.FacetTypeEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FacetRepository extends CosmosRepository<FacetEntity, String>{

    @Query(value = "SELECT * FROM c WHERE c.pk = 'facets' AND Array_Contains(@facetType,c.facetType) AND (@facetValue = null or c.facetValue = @facetValue)")
    List<FacetEntity> searchFacets(@Param("facetType") List<String> facetTypes,
                                   @Param("facetValue") String facetValue);

    @Query("SELECT * FROM c WHERE c.pk = 'facets' OFFSET @offset LIMIT @pageSize")
    List<FacetEntity> listData(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Query("SELECT * FROM c WHERE c.pk = 'facet' AND ARRAY_CONTAINS(@base36Ids, c.base36Id)")
    List<FacetEntity> findFacetByBase36Ids(@Param("base36Ids") List<String> base36Ids);


}
