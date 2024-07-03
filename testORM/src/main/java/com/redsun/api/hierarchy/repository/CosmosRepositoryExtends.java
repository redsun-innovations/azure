package com.redsun.api.hierarchy.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.redsun.api.hierarchy.model.FacetEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CosmosRepositoryExtends extends CosmosRepository<FacetEntity, String> {
    @Query("SELECT c.facetType, c.facetTypebase36Id, c.facetValue, c.base36Id " +
            "FROM c " +
            "WHERE c.pk = 'facets' AND c.facetType IN :facetTypes")
    List<FacetEntity> findByFacetTypeInAndFacetValue(@Param("facetTypes") List<String> facetTypes, @Param("facetValue") String facetValue);

    @Query("SELECT c.facetType, c.facetTypebase36Id, c.facetValue, c.base36Id " +
            "FROM c " +
            "WHERE c.pk = 'facets' OFFSET @offset LIMIT @pageSize")
    List<FacetEntity> findPaginatedFacets(@Param("offset") int offset, @Param("pageSize") int pageSize);
}
