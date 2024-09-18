package com.redsun.api.hierarchy.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import com.redsun.api.hierarchy.entity.HierarchyEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HierarchyRepository extends CosmosRepository<HierarchyEntity, String> {

        @Query("SELECT * FROM c WHERE c.pk = 'hierarchy' ORDER BY c.path ASC")
        List<HierarchyEntity> fetchClassCodeData(String classCode);

        @Query("SELECT * FROM c WHERE c.pk = 'hierarchy' ORDER BY c.path ASC")
        List<HierarchyEntity> fetchAllHierarchyData();

        @Query("SELECT * FROM c WHERE c.pk = 'hierarchy' AND (IS_NULL(@classCodes) OR ARRAY_CONTAINS(@classCodes, c.classCode))")
        List<HierarchyEntity> listAllHierarchyData(@Param("classCodes") List<String> classCodes, boolean avoidDuplicates);


        @Query("SELECT * FROM c WHERE c.pk = 'hierarchy' AND ARRAY_CONTAINS(@base36Ids, c.base36Id)")
        List<HierarchyEntity> findHierarchyByBase36Ids(@Param("base36Ids") List<String> base36Ids);
}