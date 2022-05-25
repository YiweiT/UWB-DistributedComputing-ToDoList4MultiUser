package edu.uwb.css533.service.db;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceDBTest {
    TaskServiceDB db = new TaskServiceDB("testdb");;
    UserServiceDB udb = new UserServiceDB("testdb");
    ListServiceDB ldb = new ListServiceDB("testdb");

//    public TaskServiceDBTest() throws SQLException {
//        this.db = new TaskServiceDB("testdb");
//        UserServiceDB udb = new UserServiceDB("testdb");
//        ListServiceDB ldb = new ListServiceDB("testdb");
//        udb.addUser("me","123");
//        ldb.addList("me", "apple");
//    }

    @Test
    void addTask() {
        db.dropTestTables();
        db.createTables();
        udb.addUser("me","123");
        ldb.addList("me", "apple");
        /*normal execution*/
        String get = db.addTask("a","abc","1","me");
        Assert.assertFalse(get.contains("Error"));
        /*error condition 1: duplicate tasks*/
        String get2 = db.addTask("a","abc","1","me");
        Assert.assertTrue(get2.contains("has already exists for current list"));
        /*error condition 2: non-exist list*/
        String get3 = db.addTask("b","efg","2","me");
        Assert.assertTrue(get3.contains("Error"));
    }

    @Test
    void deleteTask() {
        db.dropTestTables();
        db.createTables();
        udb.addUser("me","123");
        ldb.addList("me", "apple");
        db.addTask("a","abc","1","me");
        /*normal execution*/
        String get = db.deleteTask("1","1","me");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1: delete non-exist task*/
        String get2 = db.deleteTask("1","1","me");
        Assert.assertTrue(get2.contains("does not exists for current list"));
    }

    @Test
    void deleteAllTasks() {
        db.dropTestTables();
        db.createTables();
        udb.addUser("me","123");
        ldb.addList("me", "apple");
        /*normal execution*/
        db.addTask("a","abc","1","me");
        db.addTask("b","abc","1","me");
        String get = db.deleteAllTasks("me","1");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1: unauthorized user*/
        String get2 = db.deleteAllTasks("he","1");
        Assert.assertTrue(get2.contains("Error"));
    }

    @Test
    void displayAllTaskNames() {
        db.dropTestTables();
        db.createTables();
        udb.addUser("me","123");
        ldb.addList("me", "apple");
        /*normal execution*/
        db.addTask("a","abc","1","me");
        db.addTask("b","abc","1","me");
        String get = db.displayAllTaskNames("me","1");
        Assert.assertTrue(get.contains("taskname"));
        /*error condition 1: unauthorized user*/
        String get2 = db.displayAllTaskNames("he","1");
        Assert.assertTrue(get2.contains("Error"));
    }

    @Test
    void updateTaskContent() {
        db.dropTestTables();
        db.createTables();
        udb.addUser("me","123");
        ldb.addList("me", "apple");
        db.addTask("b","abc","1","me");
        /*normal execution*/
        String get = db.updateTaskContent("me","1","efg","1");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1: unauthorized user*/
        String get2 = db.updateTaskContent("he","1","efg","1");
        Assert.assertTrue(get2.contains("Error"));
    }

    @Test
    void updateTaskStatus() {
        db.dropTestTables();
        db.createTables();
        udb.addUser("me","123");
        ldb.addList("me", "apple");
        db.addTask("b","abc","1","me");
        /*normal execution*/
        String get = db.updateTaskStatus("me","1","In-Progress","1");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1: invalid status*/
        String get2 = db.updateTaskStatus("me","1","a","1");
        Assert.assertTrue(get2.contains("Task type can only be Completed, Not Started or In-Progress"));
    }

    @Test
    void getTask() {
        db.dropTestTables();
        db.createTables();
        udb.addUser("me","123");
        ldb.addList("me", "apple");
        db.addTask("b","abc","1","me");
        // normal condition
        String msg1 = db.getTask("me", "1", "1");
        Assert.assertEquals("{\"taskname\":\"b\",\"taskid\":1,\"content\":\"abc\",\"status\":\"Not Started\"}", msg1);
        // error condition
        String msg2 = db.getTask("me", "1", "2");
        Assert.assertTrue(msg2.contains("Error"));
    }
}
