package edu.cmu.gizmo.management.taskclient.actions;

import edu.cmu.gizmo.management.taskclient.TaskClient;
import edu.cmu.gizmo.management.taskclient.observers.ICapabilityObserver;
import edu.cmu.gizmo.management.taskclient.observers.TaskClientObserver;
import edu.cmu.gizmo.management.taskmanager.TaskExecutor.TaskType;
import edu.cmu.gizmo.management.taskmanager.TaskManager.TaskRequester;
import edu.cmu.gizmo.management.taskmanager.TaskReservation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author majed alzayer
 * <p>
 * This class is a Struts {@link Action} that is responsible of
 * responding the incoming Http requests to start the Dashboard,
 * start a user-selected task, send the user input to a capability,
 * move the CoBot's camera, update the data model to be consumed by
 * the view, and continue the execution of the currently running
 * task.
 */
public class TaskClientAction extends GizmoAction {

    /**
     * The object that is responsible of keeping track of the task's
     * capabilities
     */
    private TaskClientObserver taskClientObserver;

    /**
     * The object that holds the output to be streamed to the UI. It is
     * updated with every request to get the output (see action=getOutput)
     */
    private Object output;
    /**
     * The object that holds the streamed images from CoBot's camera.
     * It is updated with every request to get the video output.
     * (see action=getVideoOutput)
     */
    private byte[] videoOuput;

    private Object defaultInput;

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     *
     * This is the Strut's Action main method. The body of this mthod is
     * organized in the form of if statements, where each if statement
     * checks for the value of the "action" request parameter. Different
     * values of the "action" parameter represents different types
     * of requests.
     */
    @Override
    public String execute() throws Exception {

        HttpServletRequest request = getServletRequest();

		/*
         * When the "start" action is requested, the dasboard is started
		 * by default in addition to the task whose name is passed with
		 * the request.
		 */
        if (request.getParameter("action").equals("start")) {

            String taskName = request.getParameter("taskName");
            int taskDuration = Integer.parseInt(request.getParameter("taskDuration"));
            String taskPlan = request.getParameter("taskPlan");

            System.out.println("[TaskClientAction] got a request "
                    + "to start: "
                    + taskName);
			
			/* This object will be used throughout the session to
			 * to be called when needed.
			 */
            TaskClient taskClient = new TaskClient();
			
			/*
			 * Setting the task client object in session to be retrieved
			 * whenever it is needed
			 */
            request.getSession().setAttribute(
                    "taskClient", taskClient);
            System.out.println("[TaskClientAction] Starting "
                    + taskName);

            // Instantiating the task client observer
            taskClientObserver = new TaskClientObserver(taskClient);

            // let it observe the task client througohut the session
            taskClient.addObserver(taskClientObserver);
            System.out.println("[TaskClientAction] "
                    + "Creating an observer ");

            taskClientObserver.setTaskName(taskName);

            request.getSession().setAttribute(
                    "clientObserver", taskClientObserver);


            // Create a new reservation for the task
            TaskReservation aReservation = new TaskReservation(
                    taskName,
                    taskDuration,
                    TaskRequester.TASK_CLIENT,
                    null,
                    TaskType.SCRIPT_TASK,
                    getScriptsHomeDirectory() + "/" + taskPlan);

            System.out.println("[TaskClientAction] "
                    + "Loading the task . . .");

            //load the task to start it
            taskClient.loadDashboard();

            //load the new task
            taskClient.loadNewTask(aReservation);
        }
		
		/*
		 * When the "sendInput" is requested, the input that is passed
		 * with the request is set to the capability that is identified by
		 * the task id and the capability id
		 */
        if (request.getParameter("action").equals("sendInput")) {

            int taskId = Integer.parseInt(
                    request.getParameter("taskId"));
            int capabilityId = Integer.parseInt(
                    request.getParameter("capabilityId"));

            System.out.println("[TaskClientAction] got "
                    + "a request to send the input to task"
                    + taskId + " and capability " +
                    capabilityId);

            ConcurrentHashMap<Object, Object> inputHashMap =
                    new ConcurrentHashMap<Object, Object>();

            TaskClient taskClient = (TaskClient) request.
                    getSession().getAttribute("taskClient");
			
			/*
			 * Get all request parameter names, get those parameters
			 * that start with "input" and put them in the input hashmap.
			 * The assumption here is that all incoming inputs are named
			 * as "input1", "input2", "input3", ...
			 */
            Enumeration<String> parameterNames =
                    request.getParameterNames();

            while (parameterNames.hasMoreElements()) {
                String requestParam =
                        parameterNames.nextElement();
                if (!requestParam.equals("taskId")
                        && !requestParam.equals("capabilityId")) {

                    inputHashMap.put(requestParam, request.
                            getParameter(requestParam));
                }
            }

            getTaskClientObserver()
                    .setCapabilityObserverDefaultInput(taskId, capabilityId, inputHashMap);

            taskClient.sendInput(taskId,
                    capabilityId,
                    inputHashMap);

        }
		
		/*
		 * When the "moveCamera" is requested, an input (representing the
		 * camera direction) is passed.
		 */
        if (request.getParameter("action").equals("moveCamera")) {

            int taskId = Integer.parseInt(
                    request.getParameter("taskId"));
            int capabilityId = Integer.parseInt(
                    request.getParameter("capabilityId"));

            String targetDirection = (String) request
                    .getParameter("direction");

            TaskClient taskClient = (TaskClient) request
                    .getSession().getAttribute("taskClient");


            Float[] point = getDirectionCoordinates(targetDirection);

            ConcurrentHashMap<Object, Object> in =
                    new ConcurrentHashMap<Object, Object>();
            in.put("camera", point);

            taskClient.sendInput(taskId, capabilityId, in);

        }
		
		/*
		 * When the "moveCobot" is requested, an input (representing
		 * cobot direction) is passed.
		 */
        if (request.getParameter("action").equals("moveCobot")) {

            int taskId = Integer.parseInt(
                    request.getParameter("taskId"));
            int capabilityId = Integer.parseInt(
                    request.getParameter("capabilityId"));

            String targetDirection = (String) request
                    .getParameter("direction");

            TaskClient taskClient = (TaskClient) request
                    .getSession().getAttribute("taskClient");


            Float[] point = {0.0f, 0.0f};

            if (targetDirection.equals("right")) {
                System.out.println(
                        "[TaskClientAction] Key right");

                point[1] = (float) -0.25;

            } else if (targetDirection.equals("left")) {
                System.out.println(
                        "[TaskClientAction] Key left");

                point[1] = (float) 0.25;
            } else if (targetDirection.equals("up")) {
                System.out.println(
                        "[TaskClientAction] Key up");

                point[0] = (float) 0.1;
            }

            ConcurrentHashMap<Object, Object> in =
                    new ConcurrentHashMap<Object, Object>();
            in.put("moveCobot", point);

            taskClient.sendInput(taskId, capabilityId, in);

        }
		
		/*
		 * When the "getOutput" is requested, the output instance
		 * member is updated by retrieving the output of the
		 * capability that matches the passed task id and capability
		 * id. This request is part of a ping-echo mechanism to update
		 * the UI, i.e. it is periodically ping-ed. Streaming
		 * the updated output is in the form
		 * of a JSON object. (See struts.xml) for the streaming
		 * configuration.
		 */
        if (request.getParameter("action").equals("getOutput")) {
            int taskId;
            int capabilityId;

            try {
                System.out.println(
                        "[TaskClientAction] Got a request for output!");

                String taskIdStr = request.
                        getParameter("taskId");

                String capIdStr = request.
                        getParameter("capabilityId");

                if (taskIdStr != null && capIdStr != null) {
                    taskId = Integer.parseInt(taskIdStr);

                    capabilityId = Integer.
                            parseInt(capIdStr);

                    System.out.println("[TaskClientAction] getting "
                            + "the output from the object");

                    output = getTaskClientObserver()
                            .getOutputFromCapabilityObserver(taskId,
                                    capabilityId);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
			
			/* See struts.xml to know about the configuration of
			 * this result name*/
            return "streamOutput";
        }
		
		/*
		 * When the "updateUIStatus" is requested, the task client
		 * observer instance member is updated by retrieving the
		 * updated task client observer from the servlet context. 
		 * The goal here is to get an updated UI model so that 
		 * capabilities UI status can be reflected in a timely
		 * manner. Streaming the updated UI model is in the form
		 * of a JSON object. (See struts.xml) for the streaming
		 * configuration.
		 */
        if (request.getParameter("action").equals("updateUIStatus")) {
            try {
                taskClientObserver = getTaskClientObserver();

                // Update the session with the elements Ids
                updateSessoinUIElementIds(taskClientObserver);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
			
			/* See struts.xml to know about the configuration of
			 * this result name*/
            return "streamUpdate";
        }
		
		
		/*
		 * When the "completeCapability" is requested, the continueTask()
		 * method is called to continue the currently running task
		 */
        if (request.getParameter("action").
                equals("completeCapability")) {

            int taskId = Integer.parseInt(
                    request.getParameter("taskId"));

            int capabilityId = Integer.parseInt(
                    request.getParameter("capabilityId"));

            taskClientObserver = getTaskClientObserver();

            taskClientObserver.setCapabilityAsEnded(
                    taskId, capabilityId);

            TaskClient taskClient = (TaskClient) request.
                    getSession().getAttribute("taskClient");

            taskClient.continueTask();
        }
		
		/*
		 * When the "getVideoOutput" is requested, the videoOuput
		 * instance member is updated through getting output from
		 * the dashboard capability that is stored in the task 
		 * client observe. The output image is written to the 
		 * output stream.
		 */
        if (request.getParameter("action").equals("getVideoOutput")) {

            try {
                int taskId = Integer.parseInt(
                        request.getParameter("taskId"));

                int capabilityId = Integer.parseInt(
                        request.getParameter("capabilityId"));

                System.out.println("[TaskClientAction] "
                        + "getting video output images");

                taskClientObserver = getTaskClientObserver();

                Object output = taskClientObserver
                        .getOutputFromCapabilityObserver(
                                taskId, capabilityId);

                if (output instanceof ConcurrentHashMap) {
                    return null;
                }

                videoOuput = (byte[]) output;

                HttpServletResponse response = getServletResopnse();
                response.setContentType("image/jpeg");
                response.getOutputStream().write(videoOuput);
                response.getOutputStream().flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
				/*
				 * null is returned as the action response so that no view
				 * page is requested. There are better ways to do this,
				 * namely through Strut's stream result. It was not
				 * done because of time constraints.
				 */
            return null;
        }

        if (request.getParameter("action").equals("getDefaultInput")) {
            int taskId;
            int capabilityId;

            try {
                System.out.println(
                        "[TaskClientAction] Got a request for defaultInput!");

                String taskIdStr = request.
                        getParameter("taskId");

                String capIdStr = request.
                        getParameter("capabilityId");

                if (taskIdStr != null && capIdStr != null) {
                    taskId = Integer.parseInt(taskIdStr);

                    capabilityId = Integer.
                            parseInt(capIdStr);

                    System.out.println("[TaskClientAction] getting "
                            + "the defaultInput from the object");

                    defaultInput = getTaskClientObserver()
                            .getDefaultInputFromCapabilityObserver(taskId,
                                    capabilityId);
                    return "streamDefaultInput";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		
		
		/* SUCCESS is the default Struts action return.*/
        return SUCCESS;
    }

    /**
     * This getter method is required by the JSON streamer (part
     * of Struts JSON plugin)
     *
     * @return the task client observer stored at the servlet context
     */
    public TaskClientObserver getTaskClientObserver() {
        return (TaskClientObserver) getServletRequest().
                getSession().getAttribute("clientObserver");
    }

    /**
     * This setter method is required by the JSON streamer (part
     * of Struts JSON plugin)
     *
     * @param taskClientObserver
     */
    public void setTaskClientObserver(TaskClientObserver taskClientObserver) {
        this.taskClientObserver = taskClientObserver;
    }

    /**
     * This getter method is required by the JSON streamer (part
     * of Struts JSON plugin)
     *
     * @return the output instance member
     */
    public Object getOutput() {
        return output;
    }

    /**
     * This setter method is required by the JSON streamer (part
     * of Struts JSON plugin)
     *
     * @param output
     */
    public void setOutput(Object output) {
        this.output = output;
    }

    /**
     * @return video output instance member
     */
    public byte[] getVideoOuput() {
        return videoOuput;
    }

    /**
     * @param videoOuput
     */
    public void setVideoOuput(byte[] videoOuput) {
        this.videoOuput = videoOuput;
    }


    public Object getDefaultInput() {
        return defaultInput;
    }

    public void setDefaultInput(Object defaultInput) {
        this.defaultInput = defaultInput;
    }

    /**
     * This method sets session attributes for each capability. The value
     * of the attributes corresponds to a combination of the task id and
     * the capability id of each of the capabilities. Those attributes
     * will be accessed from the UI page of each capability so that the
     * task id and capability id are passed with sendtInput and getOutput
     * requests.
     *
     * @param clientObserver
     */
    private void updateSessoinUIElementIds(TaskClientObserver clientObserver) {

        HttpServletRequest request = getServletRequest();

        ArrayList<ICapabilityObserver> capabilityObservers =
                clientObserver.getCapabilityObservers();

        for (ICapabilityObserver anObserver : capabilityObservers) {

            /* Here is the attribute value: a combination of task id
             * and capability id
			 */
            String uiElementId = anObserver.getTaskId()
                    + "_x_" + anObserver.getCapabilityId();

            String uiName = anObserver.getCapabilityUiDirectory();

            if (request.getSession().getAttribute(uiName) == null) {
                request.getSession().
                        setAttribute(uiName, uiElementId);
            }
        }

    }

    /**
     * @param targetDirection. The direction having one of the values: right,
     *                         left, up or down
     * @return An array that represents the [x:right/left,y:up/down]
     * direction
     */
    private Float[] getDirectionCoordinates(String targetDirection) {
		
		/* An array that represents the [x:right/left,y:up/down]
		 * direction
		 */
        Float[] point = {0.0f, 0.0f};

        if (targetDirection.equals("right")) {
            System.out.println(
                    "[TaskClientAction] Key right");

            point[0] = (float) 0.25;

        } else if (targetDirection.equals("left")) {
            System.out.println(
                    "[TaskClientAction] Key left");

            point[0] = (float) -0.25;
        } else if (targetDirection.equals("up")) {
            System.out.println(
                    "[TaskClientAction] Key up");

            point[1] = (float) 0.25;
        } else if (targetDirection.equals("down")) {
            System.out.println(
                    "[TaskClientAction] Key down");

            point[1] = (float) -0.25;
        }

        return point;
    }


    private String getScriptsHomeDirectory() {
        HttpServletRequest request = getServletRequest();

        String path = request.getServletContext().getRealPath("/config.properties");
        File file = new File(path);
        return file.getParent() + "/WEB-INF/classes";

    }

}
