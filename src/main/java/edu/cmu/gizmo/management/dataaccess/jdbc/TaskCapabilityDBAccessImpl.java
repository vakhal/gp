/*
 * TaskCapabilityDBAccessImpl.java Jul 12, 2012 1.0
 */
package edu.cmu.gizmo.management.dataaccess.jdbc;

import java.awt.Desktop.Action;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import edu.cmu.gizmo.management.beans.PrimitiveCapability;
import edu.cmu.gizmo.management.beans.TaskDef;
import edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess;
import edu.cmu.gizmo.management.taskmanager.scripttask.DependsOn;
import edu.cmu.gizmo.management.taskmanager.scripttask.Flag;
import edu.cmu.gizmo.management.taskmanager.scripttask.GroupedTask;
import edu.cmu.gizmo.management.taskmanager.scripttask.Task;
import edu.cmu.gizmo.management.util.DBConnection;


/**
 * The Class TaskCapabilityDBAccessImpl.
 */
public class TaskCapabilityDBAccessImpl implements TaskCapabilityDBAccess {
	
	/** The conn. */
	private Connection conn = null;
	
	/** The ptmt. */
	private PreparedStatement ptmt = null;
	
	/** The result set. */
	private ResultSet resultSet = null;
	
	/** The tb name. */
	private String tbName = null;
	
	/** The db name. */
	private String dbName = "tasksdb";

	public Vector<String> listPrimitives() {
		Vector<String> capabilityList = new Vector<String>();		
		
		tbName = "primitive_capability";
		String queryString = "SELECT capability FROM " + dbName + "." + tbName;
		System.out.println("[TaskCapabilityDBAccessImpl] listPrimitives(): " + queryString);
		String capability = null;
		try {
			ptmt = conn.prepareStatement(queryString);
			resultSet = ptmt.executeQuery();
			
			while(resultSet.next()) {
				capabilityList.add(resultSet.getString("capability"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("[TaskCapabilityDBAccessImpl] listPrimitives(): PrimitiveCapability is Retrieved Successfully");
		
		return capabilityList;
	}

	
	
	public Vector<String> listPrimitivesFromPrimitveCapabilityTB() {
		Vector<String> primitiveList = new Vector<String>();		
		
		tbName = "primitive_capability";
		String queryString = "SELECT primitive FROM " + dbName + "." + tbName;
		System.out.println("[TaskCapabilityDBAccessImpl] listPrimitives(): " + queryString);
		String primitive = null;
		try {
			ptmt = conn.prepareStatement(queryString);
			resultSet = ptmt.executeQuery();
			
			while(resultSet.next()) {
				primitiveList.add(resultSet.getString("primitive"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("[TaskCapabilityDBAccessImpl] listPrimitives(): PrimitiveCapability is Retrieved Successfully");
		
		return primitiveList;
	}

	
	
	
	
	
	
	
	
	
	
	public String retrievePrimitive(String capability) {
		tbName = "primitive_capability";
		String queryString = "SELECT * FROM " + dbName + "." + tbName + " WHERE capability=?";
		String primitive = null;
		try {
			ptmt = conn.prepareStatement(queryString);
			ptmt.setString(1, capability);
			resultSet = ptmt.executeQuery();
			
			if(resultSet.next()) {
				primitive = resultSet.getString("primitive");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("[TaskCapabilityDBAccessImpl] retrievePrimitive(): PrimitiveCapability is Retrieved Successfully");

		return primitive;
	}

	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#insertPrimitiveCapability(edu.cmu.gizmo.management.beans.PrimitiveCapability)
	 */
	public void insertPrimitiveCapability(PrimitiveCapability pc) {
		// mySQL insert stmt example: INSERT INTO tbl_name 
		// (col1,col2) VALUES(15,col1*2);
		tbName = "primitive_capability";
		
        String queryString = 
        		"INSERT INTO " 
        		+ dbName + "." + tbName + "(primitive, capability) VALUES(?,?)";
        System.out.println("[TaskCapabilityDBAccessImpl] insertPrimitiveCapability(): " 
        		+ queryString);

        try {
            ptmt = conn.prepareStatement(queryString);
            ptmt.setString(1, pc.getPrimitive());
            ptmt.setString(2, pc.getCapability());
            ptmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
        System.out.println(
        		"[TaskCapabilityDBAccessImpl] insertPrimitiveCapability(): insertPrimitiveCapability Executed Successfully"
        		);
	}
	
	
	
	// Queries DB for which capability to load for 
	//	CheckAvailability		The capability used to query a calendar
	//	FindEvent		The capability used to query for an events location
	//	FindPerson		The capability used to query for an persons location
	//	GoToRoom		The capability used to move the robot to a room
	//	SeekHelp		The capability used to search for help
	//	SendEmail		The capability used to send an email
	//	CreateItinerary		The capability used to create an itinerary
	//	Communicate		The capability used to enable interaction with the robot
	//	MoveRobot		The capability used to incrementally move the robot

	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#retrieveCapability(java.lang.String)
	 */
	public String retrieveCapability(String primitive) {
		tbName = "primitive_capability";
		String queryString = "SELECT * FROM " + dbName + "." + tbName + " WHERE primitive=?";
		System.out.println("[TaskCapabilityDBAccessImpl] retrieveCapability(): " + queryString);
		String capability = null;
		try {
			ptmt = conn.prepareStatement(queryString);
			ptmt.setString(1, primitive);
			resultSet = ptmt.executeQuery();
			
			if(resultSet.next()) {
				capability = resultSet.getString("capability");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("[TaskCapabilityDBAccessImpl] retrieveCapability(): PrimitiveCapability is Retrieved Successfully");

		return capability;
	}	

	/**
	 * Instantiates a new task capability db access impl.
	 */
	public TaskCapabilityDBAccessImpl() {
		conn();
	}

	/**
	 * Conn.
	 */
	public void conn() {		
		conn = DBConnection.getConnection(dbName);
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#close()
	 */
	public void close() {
		try {
			if(ptmt != null)
				ptmt.close();
			DBConnection.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#composeTask(java.lang.String, java.util.ArrayList)
	 */
	public boolean composeTask(String taskName, ArrayList<Task> taskList)
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#findTask(int)
	 */
	public boolean findTask(int taskID)
	{
		return false;
		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#insertTaskDef(TaskDef)
	 */
	public void insertTaskDef(TaskDef td) {
		tbName = "task_def";
        String queryString = 
        		"INSERT INTO " 
        		+ dbName + "." + tbName + "(task_id, task_desc, task_name, task_script_name, task_parent_id) VALUES(?,?,?,?,?)";
        System.out.println("[TaskCapabilityDBAccessImpl] insertTaskDef(): " 
        		+ queryString);

        try {
            ptmt = conn.prepareStatement(queryString);
            ptmt.setInt(1, td.getTaskId());
            ptmt.setString(2, td.getTaskDesc());
            ptmt.setString(3, td.getTaskName());
            ptmt.setString(4, td.getTaskScriptName());
            ptmt.setInt(5, td.getTaskParentId());
            ptmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
        System.out.println(
        		"[TaskCapabilityDBAccessImpl] insertTaskDef(): insertTaskDef Executed Successfully"
        		);		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#findTask(TaskDef)
	 */
	public boolean findTaskByTaskName(String taskName)
	{
		tbName = "task_def";
		String queryString = "SELECT * FROM " + dbName + "." + tbName + " WHERE task_name=?";
		System.out.println("[TaskCapabilityDBAccessImpl] findTaskByTaskName(): " + queryString);
		boolean found = false;
		try {
			ptmt = conn.prepareStatement(queryString);
			ptmt.setString(1, taskName);
			resultSet = ptmt.executeQuery();
			
			if(resultSet.next()) {
				//taskName = resultSet.getString("task_name");
				found = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("[TaskCapabilityDBAccessImpl] findTaskByTaskName(): findTaskByTaskName Executed Successfully");

		return found;
	}

	public boolean findCapabilityByCapaiblityName(String capabilityName) {
		tbName = "capability";
		String queryString = "SELECT * FROM " + dbName + "." + tbName + " WHERE cp_name=?";
		System.out.println("[TaskCapabilityDBAccessImpl] findCapabilityByCapaiblityName(): " + queryString);
		boolean found = false;
		try {
			ptmt = conn.prepareStatement(queryString);
			ptmt.setString(1, capabilityName);
			resultSet = ptmt.executeQuery();
			
			if(resultSet.next()) {
				found = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("[TaskCapabilityDBAccessImpl] findCapabilityByCapaiblityName(): findCapabilityByCapaiblityName executed Successfully");

		return found;
		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#findDependendsOn(int)
	 */
	public int findDependendsOn(int taskID) {
		return -1;
		
	}

	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#setParameter(int, java.lang.String, java.lang.String)
	 */
	public void setParameter(int taskID, String taskParameterName, String taskParameterValue) {
		
	}

	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#createItinerary(java.lang.String, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, java.util.ArrayList)
	 */
	public void createItinerary(String groupedTaskName, ArrayList<Flag> flags, ArrayList<Action> acitons, 
			ArrayList<DependsOn> dependsOns, ArrayList<Task> tasks) {
		
	}

	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#scheduleTask(java.util.Date, int, int)
	 */
	public boolean scheduleTask(Date startTime, int minutes, int groupedTaskID) {
		return false;
		
	}

	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#isGroupedTaskAvailable(java.lang.String)
	 */
	public boolean isGroupedTaskAvailable(String groupedTaskName) {
		return false;
		
	}

	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#addTaskToGroupedTask(int, int)
	 */
	public boolean addTaskToGroupedTask(int groupedTaskId, int taskId) {
		return false;
		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#addTaskToGroupedTask(java.lang.String, java.lang.String)
	 */
	public boolean addTaskToGroupedTask(String groupedTaskName, String taskName) {
		return false;
		
	}
		
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#findTaskName(int)
	 */
	public String findTaskName(int taskID) {
		return null;
		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#findTaskMsg(java.lang.String, int)
	 */
	public String findTaskMsg(String taskName, int status) {
		return null;
		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#findStatusValue(java.lang.String, java.lang.String)
	 */
	public int findStatusValue(String taskName, String msg) {
		return -1;
		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#findTaskActionMsg(java.lang.String, int)
	 */
	public String findTaskActionMsg(String taskName, int actionStatus) {
		return null;
		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#findTaskActionStatus(java.lang.String, java.lang.String)
	 */
	public int findTaskActionStatus(String taskName, String actionMsg) {
		return -1;
		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#findGroupedDependsOn(int)
	 */
	public int findGroupedDependsOn(int groupedTaskId) {
		return -1;
		
	}

	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#findGroupedIdForTaskId(int)
	 */
	public int findGroupedIdForTaskId(int taskId) {
		return -1;
		
	}

	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#isParameterSet(java.lang.String, java.lang.String)
	 */
	public boolean isParameterSet(String taskName, String parameterName) {
		return false;
		
	}

	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#setParameterForTask(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setParameterForTask(String taskName, String parameterName, String parameterValue) {
		
	}
	
	/* (non-Javadoc)
	 * @see edu.cmu.gizmo.management.dataaccess.TaskCapabilityDBAccess#retrieveGroupedTask(int)
	 */
	public GroupedTask retrieveGroupedTask(int groupedTaskId) {
		return null;
		
	}



}
