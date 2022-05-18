package edu.uwb.css533.service.resources;



import edu.uwb.css533.service.db.DatabaseConnection;
import edu.uwb.css533.service.db.UserServiceDB;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;


// http://server:port/path?para=something
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResources {

    UserServiceDB dbConnection;

    public UserResources() {
        this.dbConnection = new UserServiceDB();
    }

    @GET
    @Path("/healthCheck")
    public String healthCheck(@QueryParam("greeting") String greeting) {
        return greeting + "at " + new Date();
    }

    @GET
    @Path("/dbConnectionCheck")
    public void dbConnectionCheck() {
        dbConnection.connect();
    }

    @GET
    @Path("/getAllUsernames")
    public Response getAllUsernames() {
        String msg = dbConnection.getAllUsernames();
        if (msg.contains("Error")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
        return Response.ok(msg).build();
    }

    @POST
    @Path("/addUser")
    /**
     * path:/users/addUser?username=user1&password=password
     * add the given username and password in Users table
     * parameters: username String, password String
     * Validation:
     *      - both parameter should be non empty or null
     *      - username is not duplicated in Users table
     */
    public Response addUser(@QueryParam("username") String username,
                            @QueryParam("password") String password) {

        // valid username non empty or null
        if (!isValid(username)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid username")
                    .build();
        }
        if (!isValid(password)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid password")
                    .build();
        }

        String msg = dbConnection.addUser(username, password);
        if (msg.contains("Error")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
        return Response.ok().build();
    }

    /**
     * Validate input not empty or null
     * @param input String
     * @return true if password is valid, false otherwise
     */
    private boolean isValid (String input) {
        if (input == null || input.length() == 0) {

            return false;
        }
        return true;
    }

    @GET
    @Path("/logIn")
    /**
     * log in the given username and password
     */
    public Response logIn(@QueryParam("username") String username,
                          @QueryParam("password") String password) {
        // validate username and password
        if (!isValid(username)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid username")
                    .build();
        }
        if (!isValid(password)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid password")
                    .build();
        }
        String msg = dbConnection.logIn(username, password);
        if (msg.contains("Error")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        } else {
            return Response.ok(msg).build();
        }
    }

    @PUT
    @Path("/resetPassword")
    public Response resetPassword (@QueryParam("username") String username,
                                   @QueryParam("oldPassword") String oldPassword,
                                   @QueryParam("newPassword") String newPassword) {
        // validate username and passwordss
        if (!isValid(username)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid username")
                    .build();
        }
        if (!isValid(oldPassword)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid old password")
                    .build();
        }
        if (!isValid(newPassword)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid new password")
                    .build();
        }
        if (oldPassword.equals(newPassword)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("New password cannot be the same as the old password.")
                    .build();
        }

        String msg = dbConnection.resetPassword(username, oldPassword, newPassword);
        if (msg.contains("Error")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
        return Response.ok(msg).build();
    }

}
