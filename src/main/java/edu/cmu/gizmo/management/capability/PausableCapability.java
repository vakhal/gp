/*
 * Pausable.java 1.0 2012-07-09
 */
package edu.cmu.gizmo.management.capability;


/**
 * Represents a capability that can be paused.
 *
 * @author Jeff Gennari
 * @version 1.0
 */
public interface PausableCapability {

    /**
     * Pause the capability.
     *
     * @return an Object containing the state needed to resume the paused
     * capability
     */
    Object pause();

    /**
     * Resume a paused capability.
     *
     * @param state the state to use to resume the capability.
     */
    void resume(Object state);

}
