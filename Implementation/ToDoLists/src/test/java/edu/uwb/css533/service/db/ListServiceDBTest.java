package edu.uwb.css533.service.db;

import edu.uwb.css533.service.db.ListServiceDB;
import edu.uwb.css533.service.db.UserServiceDB;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ListServiceDBTest {
    ListServiceDB db = new ListServiceDB("testdb");;
    UserServiceDB udb = new UserServiceDB("testdb");



    @Test
    void addList() throws SQLException{
        udb.dropTestTables();
        udb.createTables();
        udb.addUser("he","123");
        udb.addUser("me","123");
        /*normal execution*/
        String get =db.addList("me","orange");
        System.out.println(get);
        Assert.assertFalse(get.contains("Error"));
        /*error condition 1: add duplicate lists*/
        String get2 =db.addList("me","orange");
        Assert.assertTrue(get2.contains("Error"));
    }

    @Test
    void deleteList2() {
        udb.dropTestTables();
        udb.createTables();
        /*normal execution*/
        String user1 = udb.addUser("he","123");
        String user2 = udb.addUser("me","123");
        db.addList("me","orange");
        String get =db.deleteList2("me","1");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1: delete non-exist list*/
        String get2 =db.deleteList2("me","1");
        Assert.assertFalse(get2.contains("Successfully"));
    }

    @Test
    void getAllLists() {
        udb.dropTestTables();
        udb.createTables();
        udb.addUser("he","123");

        /*normal execution*/
        db.addList("me","orange");
        String get =db.getAllLists("me");
        Assert.assertFalse(get.contains("Error"));
        /*error condition 1:get list from non-exist user*/
        String get2 =db.getAllLists("he");
        Assert.assertTrue(get2.contains("no list"));
    }

    @Test
    void deleteAllLists()  {
        udb.dropTestTables();
        udb.createTables();
        udb.addUser("me","123");
//        udb.addUser("")
        /*normal execution*/
        db.addList("me", "2");
        db.addList("me", "3");
        String get =db.deleteAllLists("me");
        Assert.assertTrue(get.contains("Successfully"));
        /*error condition 1:delete list from non-exist user*/
        String get2 =db.deleteAllLists("he");
        Assert.assertFalse(get2.contains("Successfully"));
        /*error condition 2:delete list from user never has any list*/

        udb.addUser("he","123");
        String get3 =db.deleteAllLists("he");
        Assert.assertFalse(get3.contains("Successfully"));
    }

    @Test
    void getAllUsers() {
        udb.dropTestTables();
        udb.createTables();
        udb.addUser("me","123");
        udb.addUser("he", "1233");
        db.addList("me", "2");
        db.grantAccess("he", "1");
        // normal condition
        String msg = db.getAllUsers("1");
        Assert.assertEquals("[me, he]", msg);
        // error condition : no such list
        String msg1 = db.getAllUsers("2");
        Assert.assertTrue(msg1.contains("Error"));
    }

    @Test
    void checkAccess () {
        udb.dropTestTables();
        udb.createTables();
        udb.addUser("me","123");
        udb.addUser("he", "1233");
        db.addList("me", "2");

        // normal condition
        String msg1 = db.checkAccess("me", "1");
        Assert.assertTrue(msg1.contains("can acces"));
        // error condition: no access
        String msg2 = db.checkAccess("he", "1");
        Assert.assertTrue(msg2.contains("cannot acces"));
    }

    @Test
    void grantAccess () {
        udb.dropTestTables();
        udb.createTables();
        udb.addUser("me","123");
        udb.addUser("he", "1233");
        db.addList("me", "2");

        // normal condition
        String msg1 = db.grantAccess("he", "1");
        Assert.assertTrue(msg1.contains("Successfully"));
        // error condition - no such list
        String msg2 = db.grantAccess("he", "2");
        Assert.assertFalse(msg2.contains("Successfully"));
    }

    @Test
    void removeAccess () {
        udb.dropTestTables();
        udb.createTables();
        udb.addUser("me","123");
        udb.addUser("he", "1233");
        db.addList("me", "2");
        db.grantAccess("he", "1");
        // normal condition
        String msg1 = db.removeAccess("he", "1");
        Assert.assertTrue(msg1.contains("Successfully"));

        // error condition - no access
        String msg2 = db.removeAccess("hh", "1");
        Assert.assertFalse(msg2.contains("Successfully"));
    }

}