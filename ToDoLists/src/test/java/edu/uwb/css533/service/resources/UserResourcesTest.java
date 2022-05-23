package edu.uwb.css533.service.resources;

import edu.uwb.css533.service.resources.RequestObjects.ChangePwd;
import edu.uwb.css533.service.resources.RequestObjects.User;
import org.junit.Assert;

import static org.junit.jupiter.api.Assertions.*;

class UserResourcesTest {

    UserResources userResources = new UserResources("testdb");
    @org.junit.jupiter.api.Test
    void dbCheck() {
        userResources.dbConnectionCheck();
    }

    @org.junit.jupiter.api.Test
    void addUser() {
        // normal execution
        Assert.assertEquals(200,
                userResources.addUser(new User("user1", "password1")).getStatus() /*actual value*/);
        // error condition 1: duplicated username
        Assert.assertEquals(400 /*expected value*/,
                userResources.addUser(new User("user1", "password1")).getStatus() /*actual value*/);
        // error condition 2: invalid username
        Assert.assertEquals(400,
                userResources.addUser(new User("", "password1")).getStatus() /*actual value*/);
        // error condition 3: invalid password
        Assert.assertEquals(400,
                userResources.addUser(new User("user1", "")).getStatus() /*actual value*/);
    }

    @org.junit.jupiter.api.Test
    void logIn() {
        userResources.addUser(new User("user2", "password2"));
        // normal execution
        Assert.assertEquals(200,
                userResources.logIn("user2", "password2").getStatus());
        // error condition 1: invalid username
        Assert.assertEquals(400,
                userResources.logIn("", "password2").getStatus());
        // error condition 2: invalid password
        Assert.assertEquals(400,
                userResources.logIn("user2", "").getStatus());
        // error condition 3: username not match
        Assert.assertEquals(400,
                userResources.logIn("user5", "password2").getStatus());
        // error condition 4: password not match
        Assert.assertEquals(400,
                userResources.logIn("user2", "password").getStatus());
    }

    @org.junit.jupiter.api.Test
    void resetPassword() {
        userResources.addUser(new User("user3", "password3"));
        userResources.logIn("user3", "password3");

        // normal execution
        Assert.assertEquals(200,
                userResources.changePassword(
                        new ChangePwd("user3", "password3", "Changed"))
                        .getStatus());
        // error condition 1: invalid username
        Assert.assertEquals(400,
                userResources.changePassword(
                        new ChangePwd("", "password3", "Changed"))
                        .getStatus());
        // error condition 2: invalid old password
        Assert.assertEquals(400,
                userResources.changePassword(
                        new ChangePwd("user3", "", "Changed"))
                        .getStatus());
        // error condition 3: invalid new password
        Assert.assertEquals(400,
                userResources.changePassword(
                        new ChangePwd("user3", "password3", ""))
                        .getStatus());
        // error condition 4: username not found
        Assert.assertEquals(400,
                userResources.changePassword(
                        new ChangePwd("uuuuu", "password3", "Changed"))
                        .getStatus());
        // error condition 5: password not match
        Assert.assertEquals(400,
                userResources.changePassword(
                        new ChangePwd("user3", "jijijiji", "Changed"))
                        .getStatus());
        // error condition 6: the new password is the same as the old password
        Assert.assertEquals(400,
                userResources.changePassword(
                        new ChangePwd("user3", "password3", "password3"))
                        .getStatus());
    }
}