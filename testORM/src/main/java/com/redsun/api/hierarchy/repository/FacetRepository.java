package com.redsun.api.hierarchy.repository;

import java.util.List;
import java.util.Map;

public interface FacetRepository {

    List<Map<String, Object>> searchFacets(List<String> facetTypes, String facetValue);

//    Map<String, Object> listData(Integer pageNumber, Integer pageSize);
}
