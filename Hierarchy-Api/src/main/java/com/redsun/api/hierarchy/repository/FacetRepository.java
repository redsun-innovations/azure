package com.redsun.api.hierarchy.repository;

import java.util.*;

public interface FacetRepository {

    /**
     * Searches for facets based on the provided facet types and facet value.
     *
     * @param facetTypes List of facet types to search for.
     * @param facetValue Value associated with the facet types to filter the search.
     * @return List of maps representing facets matching the search criteria.
     */
    List<Map<String, Object>> searchFacets(List<String> facetTypes, String facetValue);

/**
 * Retrieves data in paginated form based on the provided page number and page size.
 *
 * @param pageNumber Page number indicating the page of data to retrieve (1-based index).
 * @param pageSize   Number of items per page.
 * @return Map containing data for the specified page, including pagination details.
 */
    Map<String, Object> listData(Integer pageNumber, Integer pageSize);

}