package com.redsun.Hierarchy_Api.repository;

import java.util.*;

public interface FacetRepository {

    List<Map<String, Object>> searchFacets(List<String> facetTypes, String facetValue);

    Map<String, Object> listData(Integer pageNumber, Integer pageSize);

}