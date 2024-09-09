package org.redsun.model;

public class Hierarchy {

    private String displayName;
    private String base36Id;
    private String pk;

    // Constructors
    public Hierarchy() {}

    public Hierarchy(String displayName, String base36Id, String pk) {
        this.displayName = displayName;
        this.base36Id = base36Id;
        this.pk = pk;
    }

    // Getters and Setters
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBase36Id() {
        return base36Id;
    }

    public void setBase36Id(String base36Id) {
        this.base36Id = base36Id;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @Override
    public String toString() {
        return "Hierarchy{" +
                "displayName='" + displayName + '\'' +
                ", base36Id='" + base36Id + '\'' +
                ", pk='" + pk + '\'' +
                '}';
    }
}
