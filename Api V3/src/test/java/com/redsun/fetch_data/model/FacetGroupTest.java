package com.redsun.fetch_data.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FacetGroupTest {

    @Test
    public void testConstructor() {
        String name = "Category";
        String classCode = "H220";

        FacetGroup facetGroup = new FacetGroup(name, classCode);

        assertEquals(name, facetGroup.getName());
        assertEquals(classCode, facetGroup.getClassCode());
    }

    @Test
    public void testSetName() {
        String name = "Category";
        FacetGroup facetGroup = new FacetGroup();
        facetGroup.setName(name);
        assertEquals(name, facetGroup.getName());
    }

    @Test
    public void testSetClassCode() {
        String classCode = "H220";
        FacetGroup facetGroup = new FacetGroup();
        facetGroup.setClassCode(classCode);
        assertEquals(classCode, facetGroup.getClassCode());
    }
}
