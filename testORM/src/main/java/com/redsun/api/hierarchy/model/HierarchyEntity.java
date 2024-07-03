
package com.redsun.api.hierarchy.model;
import com.azure.spring.data.cosmos.core.mapping.Container;


/**

 * This class encapsulates data related to display name, class code,
 * Base36 identifiers, and parent Base36 identifier.
 */
@Container(containerName = "Users")
public class HierarchyEntity {

    private String displayName;

    private String classCode;

    private String base36Id;
    private String parentBase36Id;


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

    /**
     * Retrieves the parent Base36 identifier of the hierarchy group.
     * @return The parent Base36 identifier.
     */
    public String getParentBase36Id() {
        return parentBase36Id;
    }

    /**
     * Sets the parent Base36 identifier of the hierarchy group.
     * @param parentBase36Id The parent Base36 identifier to set.
     */
    public void setParentBase36Id(String parentBase36Id) {
        this.parentBase36Id = parentBase36Id;
    }
}