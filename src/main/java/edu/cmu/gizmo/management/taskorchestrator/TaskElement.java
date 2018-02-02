package edu.cmu.gizmo.management.taskorchestrator;

/**
 * This is the base interface for all types of task elements. When querying a
 *
 * @author jsg
 */
public interface TaskElement {

    /**
     * Load the TaskElement. Implementers of this interface need to know how to
     * load each particular task element type.
     *
     * @return true if the task loaded successfully, false otherwise.
     */
    Boolean load();

    /**
     * Unload a loaded TaskElement. Implementers of this interface need to know
     * how to unload a particular task element type
     *
     * @return
     */
    Boolean unload();

    /**
     * Return the type of this task Element
     *
     * @return
     */
    TaskElementType getType();

    enum TaskElementType {
        SCRIPT,
        PRIMITIVE
    }

}
