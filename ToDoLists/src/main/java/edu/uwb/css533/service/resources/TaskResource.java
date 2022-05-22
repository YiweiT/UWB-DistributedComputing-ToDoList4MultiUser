package edu.uwb.css533.service.resources;

import com.google.common.collect.ImmutableMap;
import edu.uwb.css533.service.db.TaskServiceDB;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {
    TaskServiceDB databaseConnection;

    @POST
    @Path("/addTask")
    public Response addTask(AddTask task) {
        String msg = "";
        String taskName = task.getTaskname();
        String taskDescription = task.getContent();
        String listId = task.getListid();
        String userName = task.getUsername();

        String[] msgList = databaseConnection.addTask(taskName,taskDescription,listId,userName);
        msg = msgList[0];
        if(msg.contains("Successfully")){
            String message = " task " + taskName +"of list" + listId + " has been added for user: " + userName;
            System.out.println(message);
            return Response.ok(ImmutableMap.of(msgList[1], msgList[0])).build();
        }
        else{
            String message = " Adding " +taskName +" of  list " +listId + "for user: "+ userName + "has failed";
            System.out.println(message);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }

    @GET
    @Path("/deleteTask")
    public Response deleteTask(
            @QueryParam("username") String userName,
            @QueryParam("listid") String listId,
            @QueryParam("taskid") String taskId) {
        String msg = "";
        msg = databaseConnection.deleteTask(taskId,listId,userName);
        if(msg.contains("Successfully")){
            String message=" task " + taskId +"of list" + listId + " has been deleted for user: " + userName;
            System.out.println(message);
            return Response.ok(msg).build();
        }
        else{
            String message = " Deleting " +taskId +" of  list " +listId + "for user: "+ userName + "has failed";
            System.out.println(message);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }

    @GET
    @Path("/deleteAllTasks")
    public Response deleteAllTasks(
            @QueryParam("username") String userName,
            @QueryParam("listid") String listId) {
        String msg = "";
        msg= databaseConnection.deleteAllTasks(userName,listId);
        if(msg.contains("Successfully")){
            String message=" All task of list" + listId + " has been deleted";
            System.out.println(message);
            return Response.ok(msg).build();
        }
        else{
            String message=" Deleting all tasks of  list " +listId + "for user: "+ userName + "has failed";
            System.out.println(message);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }
    @GET
    @Path("/displayAllTasksNames")
    public Response displayAllTasksNames(
            @QueryParam("username") String userName,
            @QueryParam("listid") String listId) {
        String msg = "";
        msg= databaseConnection.displayAllTaskNames(userName,listId);
        if(msg.contains("Successfully")){
            System.out.println( " All task of list" + listId + " has been displayed for user: " + userName);
            return Response.ok(msg).build();
        }
        else{
            System.out.println(" Displaying all tasks of  list " +listId + "for user: "+ userName + "has failed");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }
    @PUT
    @Path("/updateTaskContent")
    public Response updateTaskContent(
            @QueryParam("username") String userName,
            @QueryParam("listid") String listId,
            @QueryParam("taskid") String taskId,
            @QueryParam("content") String taskContent) {
        String msg = "";
        msg= databaseConnection.updateTaskContent(userName,listId,taskContent,taskId);
        if(msg.contains("Successfully")){
            System.out.println("Update task content to "+taskContent+" of task " + taskId +"of list" + listId + " has been added for user: " + userName);
            return Response.ok(msg).build();
        }
        else{
            System.out.println("Updating task content to "+taskContent+" of task " +taskId +" of  list " +listId + "for user: "+ userName + "has failed");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }
    @PUT
    @Path("/updateTaskStatus")
    public Response updateTaskStatus(
            @QueryParam("username") String userName,
            @QueryParam("listid") String listId,
            @QueryParam("taskid") String taskId,
            @QueryParam("status") String taskStatus) {
        String msg = "";
        msg= databaseConnection.updateTaskStatus(userName,listId,taskStatus,taskId);
        if(msg.contains("Successfully")){
            System.out.println("Update task status to "+taskStatus+" of task " + taskId +"of list" + listId + " has been added for user: " + userName);
            return Response.ok(msg).build();
        }
        else{
            System.out.println("Updating task status to "+taskStatus+" of task " +taskId +" of  list " +listId + "for user: "+ userName + "has failed");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }
}
