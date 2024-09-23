package com.redsun.api.hierarchy.entity;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonInclude;

@Container(containerName = "Users")
public class FacetTypeEntity {
    @Id
    private String id;

    @PartitionKey
    private String pk;

    private String facetType;

    @JsonInclude(JsonInclude. Include.NON_NULL)
    private String facetValue;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String facetTypeDescription;

    private String base10;
    private String base36Id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String facetTypebase36Id;

    private String priority;
    private String seoTitle;
    private String seoCanonicalUrl;
    private String seoContent;
    private String metaDescription;
    private String metaKeywords;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

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

    public String getFacetTypeDescription() {
        return facetTypeDescription;
    }

    public void setFacetTypeDescription(String facetTypeDescription) {
        this.facetTypeDescription = facetTypeDescription;
    }

    public String getBase10() {
        return base10;
    }

    public void setBase10(String base10) {
        this.base10 = base10;
    }

    public String getBase36Id() {
        return base36Id;
    }

    public void setBase36Id(String base36Id) {
        this.base36Id = base36Id;
    }

    public String getFacetTypebase36Id() {
        return facetTypebase36Id;
    }

    public void setFacetTypebase36Id(String facetTypebase36Id) {
        this.facetTypebase36Id = facetTypebase36Id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSeoTitle() {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle) {
        this.seoTitle = seoTitle;
    }

    public String getSeoCanonicalUrl() {
        return seoCanonicalUrl;
    }

    public void setSeoCanonicalUrl(String seoCanonicalUrl) {
        this.seoCanonicalUrl = seoCanonicalUrl;
    }

    public String getSeoContent() {
        return seoContent;
    }

    public void setSeoContent(String seoContent) {
        this.seoContent = seoContent;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }
}
