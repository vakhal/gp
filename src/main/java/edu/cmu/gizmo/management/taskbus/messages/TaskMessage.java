/*
 * TaskMessage.java 1.0 2012-06-22
 */

package edu.cmu.gizmo.management.taskbus.messages;

import java.io.Serializable;


/**
 * This is the super interface for all messages that traverse the task bus.
 * It is an abstract class as it cannot be instantiated directly
 *
 * @author Jeff Gennari
 * @version 1.0 2012-06-22
 */
public interface TaskMessage extends Serializable {

    /**
     * The Constant CAPABILITY_OUTPUT.
     */
    String CAPABILITY_OUTPUT = "CAPABILITY_OUTPUT";

    /**
     * The Constant CAPABILITY_INPUT.
     */
    String CAPABILITY_INPUT = "CAPABILITY_INPUT";

    /**
     * The Constant CAPABILITY_STATUS.
     */
    String CAPABILITY_STATUS = "CAPABILITY_STATUS";

    /**
     * The Constant CAPABILITY_COMPLETE.
     */
    String CAPABILITY_COMPLETE = "CAPABILITY_COMPLETE";

    /**
     * The Constant TERMINATE_CAPABILITY.
     */
    String TERMINATE_CAPABILITY = "TERMINATE_CAPABILITY";

    /**
     * The Constant START_CAPABILITY.
     */
    String START_CAPABILITY = "START_CAPABILITY";

    /**
     * The Constant HELO_CLIENT.
     */
    String HELO_CLIENT = "HELO_CLIENT";

    /**
     * The Constant LOAD_TASK.
     */
    String LOAD_TASK = "LOAD_TASK";

    /**
     * The Constant TASK_READY.
     */
    String TASK_READY = "TASK_READY";

    /**
     * The Constant PAUSE_TASK.
     */
    String PAUSE_TASK = "PAUSE_TASK";

    /**
     * The Constant PAUSE_TASK_COMPLETE.
     */
    String PAUSE_TASK_COMPLETE = "TASK_PAUSED";

    /**
     * The Constant PAUSE_LIST.
     */
    String PAUSE_LIST = "PAUSE_LIST";

    /**
     * The Constant RESUME_TASK.
     */
    String RESUME_TASK = "RESUME_TASK";

    /**
     * The Constant RESUME_TASK_COMPLETE.
     */
    String RESUME_TASK_COMPLETE = "TASK_RESUMED";

    /**
     * The Constant CANCEL_TASK.
     */
    String CANCEL_TASK = "CANCEL_TASK";

    /**
     * The Constant CLIENT_REPLAN.
     */
    String CLIENT_REPLAN = "CLIENT_REPLAN";

    /**
     * The Constant SYSTEM_REPLAN.
     */
    String SYSTEM_REPLAN = "SYSTEM_REPLAN";

    /**
     * The Constant TASK_COMPLETE.
     */
    String TASK_COMPLETE = "TASK_COMPLETE";

    /**
     * The Constant START_CAPABILITY.
     */
    String REJECT_TASK = "REJECT_TASK";

    /**
     * Gets the message type.
     *
     * @return the message type
     */
    String getMessageType();

}
