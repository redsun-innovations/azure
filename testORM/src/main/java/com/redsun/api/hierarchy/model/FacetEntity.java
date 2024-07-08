package com.redsun.api.hierarchy.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import org.springframework.data.annotation.Id;

import java.util.List;

@Container(containerName = "Users")  // Replace with your container name
public class FacetEntity {
    @Id
    private int id;
    @PartitionKey
    private String pk;

    private String facetType;
    private String facetValue;
    private String base36Id;
    private String facetTypebase36Id;
    private int pageNumber;
    private int count;
    private int pageSize;
    private List<String> data;


    public FacetEntity (int id, String pk, String facetType, String facetValue, String base36Id, String facetTypebase36Id){
        this.id = id;
        this.pk = pk;
        this.facetType = facetType;
        this.facetValue = facetValue;
        this.base36Id = base36Id;
        this.facetTypebase36Id = facetTypebase36Id;
    }


    public int getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }
    /**
     * Retrieves the facet type.
     * @return The facet type.
     */
    public String getFacetType() {
        return facetType;
    }

    /**
     * Sets the facet type.
     * @param facetType The facet type to set.
     */
    public void setFacetType(String facetType) {
        this.facetType = facetType;
    }

    /**
     * Retrieves the facet value.
     * @return The facet value.
     */
    public String getFacetValue() {
        return facetValue;
    }

    /**+
     * Sets the facet value.
     * @param facetValue The facet value to set.
     */
    public void setFacetValue(String facetValue) {
        this.facetValue = facetValue;
    }

    /**
     * Retrieves the Base36 identifier.
     * @return The Base36 identifier.
     */
    public String getBase36Id() {
        return base36Id;
    }

    /**
     * Sets the Base36 identifier.
     * @param base36Id The Base36 identifier to set.
     */
    public void setBase36Id(String base36Id) {
        this.base36Id = base36Id;
    }

    /**
     * Retrieves the Base36 identifier for facet type.
     * @return The Base36 identifier for facet type.
     */
    public String getFacetTypebase36Id() {
        return facetTypebase36Id;
    }

    /**
     * Sets the Base36 identifier for facet type.
     * @param facetTypebase36Id The Base36 identifier for facet type to set.
     */
    public void setFacetTypebase36Id(String facetTypebase36Id) {
        this.facetTypebase36Id = facetTypebase36Id;
    }

    /**
     * Retrieves the current page number.
     * @return The current page number.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets the current page number.
     * @param pageNumber The page number to set.
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Retrieves the count of items.
     * @return The count of items.
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the count of items.
     * @param count The count of items to set.
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Retrieves the page size.
     * @return The page size.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Sets the page size.
     * @param pageSize The page size to set.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Retrieves the list of data associated with the facet group.
     * @return The list of data.
     */
    public List<String> getData() {
        return data;
    }

    /**
     * Sets the list of data associated with the facet group.
     * @param data The list of data to set.
     */
    public void setData(List<String> data) {
        this.data = data;
    }


}
