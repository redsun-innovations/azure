package com.redsun.Hierarchy_Api.model;

public class HierarchyGroup {

    private String displayName;

    private String classCode;

    private String base36Id;
    private String parentBase36Id;


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getBase36Id() {
        return base36Id;
    }

    public void setBase36Id(String base36Id) {
        this.base36Id = base36Id;
    }

    public String getParentBase36Id() {
        return parentBase36Id;
    }

    public void setParentBase36Id(String parentBase36Id) {
        this.parentBase36Id = parentBase36Id;
    }

}
