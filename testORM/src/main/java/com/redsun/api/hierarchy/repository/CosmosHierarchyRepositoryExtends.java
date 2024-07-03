package com.redsun.api.hierarchy.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.redsun.api.hierarchy.model.HierarchyEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CosmosHierarchyRepositoryExtends extends CosmosRepository<HierarchyEntity, String> {

    @Query("SELECT c.displayName, c.base36Id, c.path, c.classCode " +
            "FROM c " +
            "WHERE c.pk = 'hierarchy' " +
            "AND c.classCode = @classCode")
    List<HierarchyEntity> fetchByClassCodeData(@Param("classCode") String classCode);

    @Query("SELECT c.displayName, c.base36Id, c.path " +
            "FROM c " +
            "WHERE c.pk = 'hierarchy' " +
            "ORDER BY c.path ASC")
    List<HierarchyEntity> fetchByAllHierarchyData();

    @Query("SELECT c.classCode, c.base36Id " +
            "FROM c " +
            "WHERE c.pk = 'hierarchy'")
    List<HierarchyEntity> listByAllHierarchyData(@Param("classCodes") List<String> classCodes, @Param("avoidDuplicates") boolean avoidDuplicates);
}
