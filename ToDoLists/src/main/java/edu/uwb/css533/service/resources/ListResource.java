package edu.uwb.css533.service.resources;

import edu.uwb.css533.service.db.ListServiceDB;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/lists")
@Produces(MediaType.APPLICATION_JSON)
public class ListResource {
    ListServiceDB dbConnection;

    public ListResource() {
        this.dbConnection = new ListServiceDB();
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

}
