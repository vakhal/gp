/*
 * Robot.java 1.1 2012-06-23
 */
package edu.cmu.gizmo.management.robot;

import java.net.Socket;


/**
 * This interface contains the methods that all robots should implement.
 *
 * @author Jeff Gennari
 * @version 1.1 2012-06-23
 */
public interface Robot {

    /**
     * Install a socket connection to the robot.
     *
     * @param connection the connection to the robot
     */
    public void connect(Socket connection);

    /**
     * Terminate the connection to the robot.
     */
    public void disconnect();

}
