package edu.uwb.css533.service.resources;

import edu.uwb.css533.service.db.ListServiceDB;
import edu.uwb.css533.service.resources.RequestObjects.AddList;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/lists")
@Produces(MediaType.APPLICATION_JSON)
public class ListResource {
    ListServiceDB dbConnection;

    public ListResource() {
        this.dbConnection = new ListServiceDB();
    }
    public ListResource(String db) {
        this.dbConnection = new ListServiceDB(db);
    }

    @GET
    @Path("/{username}/checkAccess/{listid}")
    public Response checkAccess (@PathParam("username") String username,
                                 @PathParam("listid") String listid) {

        String msg = dbConnection.checkAccess(username, listid);
        if (msg.contains("Error")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
        return Response.ok(msg).build();
    }

    @GET
    @Path("/getAllUsers/{listid}")
    public Response getAllUsers(@PathParam("listid") String listId) {
        String msg = dbConnection.getAllUsers(listId);
        if (msg.contains("Error")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
        return Response.ok(msg).build();
    }

    @GET
    @Path("/{username}/grantAccess/{listid}")
    public Response grantAccess (@PathParam("username") String username,
                                 @PathParam("listid") String listid) {

        String msg = dbConnection.grantAccess(username, listid);
        if (msg.contains("Error")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
        return Response.ok(msg).build();
    }

    @GET
    @Path("/{username}/removeAccess/{listid}")
    public Response removeAccess (@PathParam("username") String username,
                                 @PathParam("listid") String listid) {

        String msg = dbConnection.removeAccess(username, listid);
        if (msg.contains("Error")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
        return Response.ok(msg).build();
    }

    @POST
    @Path("/addList")
    public Response addList(
            AddList list) {
        String userName = list.getUsername();
        String listname = list.getListname();

        String success = dbConnection.addList(userName,listname);
        if(success.contains("Successfully")){
            return Response.ok(success).build();
        }
        else{
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(success)
                    .build();
        }
    }
    @GET
    @Path("/deleteList")
    public Response deleteList(
            @QueryParam("username") String userName,
            @QueryParam("listid") String listid) {
        String msg = "";
        String success = dbConnection.deleteList2(userName,listid);
        if(success.contains("Successfully")){
//            msg = " list " + listName + " has been deleted for user: " + userName;
            return Response.ok(success).build();
        }
        else{
//            msg=" Deleting list " +listName + "for user: "+ userName + "has failed";
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(success)
                    .build();
        }
    }
    @GET
    @Path("deleteAllLists")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAllLists(
            @QueryParam("username") String userName) {

        String success = dbConnection.deleteAllLists(userName);
        if(success.contains("Successfully")){
            return Response.ok(success).build();
        }
        else{

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(success)
                    .build();
        }
    }
    @GET
    @Path("/getAllLists")
    public Response displayAllListsNames(
            @QueryParam("username") String userName) {

        String success = dbConnection.getAllLists(userName);
        if(success.contains("Error")){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(success)
                    .build();

        }
        return Response.ok(success).build();
    }

}
