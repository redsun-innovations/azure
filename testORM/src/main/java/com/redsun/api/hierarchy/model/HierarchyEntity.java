
package com.redsun.api.hierarchy.model;
import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import org.springframework.data.annotation.Id;

@Container(containerName = "your-container-name") // Replace with your actual container name
public class HierarchyEntity {

    @Id
    private String id;

    @PartitionKey
    private String pk;

    private String displayName;
    private String base36Id;
    private String path;
    private String classCode;



    // Parameterized constructor
    public HierarchyEntity(String id, String pk, String displayName, String base36Id, String path, String classCode) {
        this.id = id;
        this.pk = pk;
        this.displayName = displayName;
        this.base36Id = base36Id;
        this.path = path;
        this.classCode = classCode;
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }


}
