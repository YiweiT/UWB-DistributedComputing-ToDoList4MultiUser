package edu.uwb.css533.service.db;

import edu.uwb.css533.service.db.ListServiceDB;
import edu.uwb.css533.service.db.UserServiceDB;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ListServiceDBTest {
    ListServiceDB db;
    public ListServiceDBTest() {
        this.db = new ListServiceDB();
        UserServiceDB udb = new UserServiceDB();
        udb.addUser("he","123");
        udb.addUser("me","123");
    }


    @Test
    void addList() throws SQLException{
        /*normal execution*/
        String get =db.addList("me","orange");
        Assert.assertFalse(get.contains("Error"));
        /*error condition 1: add duplicate lists*/
        String get2 =db.addList("me","orange");
        Assert.assertTrue(get2.contains("Error"));
    }

    @Test
    void deleteList2() {
        /*normal execution*/
        String get =db.deleteList2("me","1");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1: delete non-exist list*/
        String get2 =db.deleteList2("me","1");
        Assert.assertFalse(get2.contains("Successfully"));
    }

    @Test
    void getAllLists() {
        /*normal execution*/
        String get =db.getAllLists("me");
        Assert.assertFalse(get.contains("Error"));
        /*error condition 1:get list from non-exist user*/
        String get2 =db.getAllLists("he");
        Assert.assertTrue(get2.equals("[]"));
    }

    @Test
    void deleteAllLists()  {
        /*normal execution*/
        String get =db.deleteAllLists("me");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1:delete list from non-exist user*/
        String get2 =db.deleteAllLists("he");
        Assert.assertFalse(get2.contains("Successfully"));
        /*error condition 2:delete list from user never has any list*/
        UserServiceDB udb = new UserServiceDB();
        udb.addUser("he","123");
        String get3 =db.deleteAllLists("he");
        Assert.assertFalse(get3.contains("Successfully"));
    }

}