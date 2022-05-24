package edu.uwb.css533.service.db;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceDBTest {
    TaskServiceDB databaseConnection;

    public TaskServiceDBTest() throws SQLException {
        this.databaseConnection = new TaskServiceDB();
        UserServiceDB udb = new UserServiceDB();
        ListServiceDB ldb = new ListServiceDB();
        udb.addUser("me","123");
        ldb.addList("me", "apple");
    }

    @Test
    void addTask() {
        /*normal execution*/
        String get = databaseConnection.addTask("a","abc","1","me");
        Assert.assertFalse(get.contains("Error"));
        /*error condition 1: duplicate tasks*/
        String get2 = databaseConnection.addTask("a","abc","1","me");
        Assert.assertTrue(get2.contains("has already exists for current list"));
        /*error condition 2: non-exist list*/
        String get3 = databaseConnection.addTask("b","efg","2","me");
        Assert.assertTrue(get3.contains("Error"));
    }

    @Test
    void deleteTask() {
        /*normal execution*/
        String get = databaseConnection.deleteTask("1","1","me");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1: delete non-exist task*/
        String get2 = databaseConnection.deleteTask("1","1","me");
        Assert.assertTrue(get2.contains("does not exists for current list"));
    }

    @Test
    void deleteAllTasks() {
        /*normal execution*/
        databaseConnection.addTask("a","abc","1","me");
        databaseConnection.addTask("b","abc","1","me");
        String get = databaseConnection.deleteAllTasks("me","1");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1: unauthorized user*/
        String get2 = databaseConnection.deleteAllTasks("he","1");
        Assert.assertTrue(get2.contains("Error: Invalid user"));
    }

    @Test
    void displayAllTaskNames() {
        /*normal execution*/
        databaseConnection.addTask("a","abc","1","me");
        databaseConnection.addTask("b","abc","1","me");
        String get = databaseConnection.displayAllTaskNames("me","1");
        Assert.assertTrue(get.contains("taskname"));
        /*error condition 1: unauthorized user*/
        String get2 = databaseConnection.displayAllTaskNames("he","1");
        Assert.assertTrue(get2.contains("Invalid user"));
    }

    @Test
    void updateTaskContent() {
        /*normal execution*/
        String get = databaseConnection.updateTaskContent("me","1","efg","1");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1: unauthorized user*/
        String get2 = databaseConnection.updateTaskContent("he","1","efg","1");
        Assert.assertTrue(get2.contains("Invalid user"));
    }

    @Test
    void updateTaskStatus() {
        /*normal execution*/
        String get = databaseConnection.updateTaskStatus("me","1","In-Progress","1");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1: invalid status*/
        String get2 = databaseConnection.updateTaskStatus("me","1","a","1");
        Assert.assertTrue(get2.contains("Task type can only be Completed, Not Started or In-Progress"));
    }
}
