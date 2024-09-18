
package com.redsun.api.hierarchy.entity;
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
    public HierarchyEntity(String displayName, String base36Id, String path, String classCode) {
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

    /**
     * Retrieves the display name of the hierarchy group.
     * @return The display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of the hierarchy group.
     * @param displayName The display name to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Retrieves the class code associated with the hierarchy group.
     * @return The class code.
     */
    public String getClassCode() {
        return classCode;
    }

    /**
     * Sets the class code associated with the hierarchy group.
     * @param classCode The class code to set.
     */
    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    /**
     * Retrieves the Base36 identifier of the hierarchy group.
     * @return The Base36 identifier.
     */
    public String getBase36Id() {
        return base36Id;
    }

    /**
     * Sets the Base36 identifier of the hierarchy group.
     * @param base36Id The Base36 identifier to set.
     */
    public void setBase36Id(String base36Id) {
        this.base36Id = base36Id;
    }


}
