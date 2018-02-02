package edu.cmu.gizmo.management.taskclient.observers;

import java.util.Observable;

/**
 * @author majedalzayer
 * <p>
 * This interface containts properties that are needed to generally act
 * on the capabilities observers at runtime by utilizing polymorphism. Also,
 * it is used to as a contract that the capability developer must conform to
 * in order to be able to implement a capability observer.
 */
public interface ICapabilityObserver {

    /**
     * @return the output of the capability
     */
    Object getOutput();

    /**
     * @param output
     */
    void setOutput(Object output);

    /**
     * @param anObservable CURRENTLY NOT USED
     */
    void observe(Observable anObservable);

    /**
     * @return the UI status of the capability
     */
    CapabilityUIStatus getStatus();

    /**
     * @param status
     */
    void setStatus(CapabilityUIStatus status);

    /**
     * @return the capability id
     */
    int getCapabilityId();

    /**
     * @param capabilityId
     */
    void setCapabilityId(int capabilityId);

    /**
     * @return the task id
     */
    int getTaskId();

    /**
     * @param taskId
     */
    void setTaskId(int taskId);

    /**
     * @return the capability UI file directory. Currently,
     * it's the JSP file name
     */
    String getCapabilityUiDirectory();

    /**
     * @param directory
     */
    void setCapabilityUiDirectory(String directory);

    /**
     * @param prevOutput Sets the output of the previous capability
     */
    void setDeafultInput(Object prevOutput);

    /**
     * @param capabilityName
     */
    void setCapabilityName(String capabilityName);

    Object getDefaultInput();

    /**
     * @author majedalzayer
     * <p>
     * The UI status of a capability:
     * - STARTED: Once a capability observer is created.
     * - COMPLETED: Once a capability finishes its functionality
     * and no more output is sent.
     * - ENDED: To mark the end of the capability
     */
    enum CapabilityUIStatus {
        STARTED, COMPLETED, ENDED
    }
}
