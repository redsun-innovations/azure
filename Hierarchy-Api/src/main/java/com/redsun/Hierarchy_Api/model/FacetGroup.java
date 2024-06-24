package com.redsun.Hierarchy_Api.model;

import java.util.List;

public class FacetGroup {
    private String facetType;
    private String facetValue;

    private String base36Id;
    private String facetTypebase36Id;

    private int pageNumber;

    private int count;

    private int pageSize;

    private List<String> data;

    public String getFacetType() {
        return facetType;
    }

    public void setFacetType(String facetType) {
        this.facetType = facetType;
    }

    public String getFacetValue() {
        return facetValue;
    }

    public void setFacetValue(String facetValue) {
        this.facetValue = facetValue;
    }

    public String getFacetTypebase36Id() {
        return facetTypebase36Id;
    }

    public void setFacetTypebase36Id(String facetTypebase36Id) {
        this.facetTypebase36Id = facetTypebase36Id;
    }

    public String getBase36Id() {
        return base36Id;
    }

    public void setBase36Id(String base36Id) {
        this.base36Id = base36Id;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
