package edu.uwb.css533.service.db;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListServiceDB extends DatabaseConnection{


    private int isExisting(String listid) {
        if (isConnected()) {
            String sql = "SELECT COUNT(*) FROM Lists where listid=?";

            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(listid));
                ResultSet rs = statement.executeQuery();
                int cnt = 0;
                System.out.println(rs);
                System.out.println(rs.toString());
                if (rs.next()) {
                    cnt = rs.getInt(1);
                    System.out.println(cnt);
                }
                System.out.println("is exisitng " + cnt);
                if (cnt > 0) {
                    return 1;
                } else {
                    return 0;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }
    /**
     * Check whether the given username can access list id
     * Search in users_info table, whether the given listid is in the listids Column of the username row
     * @param username String username
     * @param listid String list id
     * @return accessible if count > 0, error method otherwise
     */
    public String checkAccess(String username, String listid) {
        if (isConnected()) {
            int existing = isExisting(listid);
            System.out.println("checkAccess " + existing);
            if (existing == 1) {
                String sql = "SELECT COUNT(*) FROM users_info where ? = ANY(listids) AND username=?;";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, listid);
                    statement.setString(2, username);

                    ResultSet rs = statement.executeQuery();
                    int cnt = 0;

                    if (rs.next()) {
                        cnt = rs.getInt(1);
                    }
                    if (cnt < 1) {
                        return String.format("Error: %s cannot access %s.", username, listid);
                    }

                    return String.format("%s can access %s.", username, listid);
                } catch (SQLException e) {
                    return "Error: " + e.getMessage();
                }
            } else if (existing == 0) {
                return "Error: no such list exists.";
            } else {
                return "Error: unable to access.";
            }

        } else {

            return "Error: Unable to connect " + url;
        }
    }

    /**
     * Grant the access for a username to a list with id as listid
     * 1. check whether the username already has access to listid.
     * 2. if not have access yet, add listid to username's listids column
     * @param username
     * @param listid
     * @return "Grand access successfully" if the username has no access initially and successful update listids column
     *          error message otherwise.
     */
    public String grantAccess(String username, String listid) {
        if (isConnected()) {
            String checkAccessMsg = checkAccess(username, listid);
            if (checkAccessMsg.contains("cannot access")) {
                String sql = "UPDATE users_info SET listids = array_append(listids,?) WHERE username=?;";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, listid);
                    statement.setString(2, username);

                    int rows = statement.executeUpdate();
                    return String.format("Grant access successfully! Now %s can access %s", username, listid);
                } catch (SQLException e) {
                    return "Error: " + e.getMessage();
                }
            } else if (checkAccessMsg.contains("can access")) {
                return String.format("%s already has access to %s", username, listid);
            } else {
                return checkAccessMsg;
            }
        } else {
            return "Error: Unable to connect " + url;
        }
    }

    /**
     * remove username's access to listid
     * 1. check whether the username already has access to listid.
     * 2. if yes, remove listid from username's listids column
     * @param username
     * @param listid
     * @return
     * "Access removed successfully" if the username has the access to listid initially
     * and successful update listids column
     * error message otherwise
     */
    public String removeAccess(String username, String listid) {
        if (isConnected()) {
            String checkAccessMsg = checkAccess(username, listid);
            System.out.println("remove access " + checkAccessMsg);
            if (checkAccessMsg.contains("can access")) {
                String sql = "UPDATE users_info SET listids = array_remove(listids,?) WHERE username=?;";
                try {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, listid);
                    statement.setString(2, username);

                    int rows = statement.executeUpdate();
                    return String.format("Access removed successfully! Now %s cannot access %s", username, listid);
                } catch (SQLException e) {
                    return "Error: " + e.getMessage();
                }
            } else if (checkAccessMsg.contains("cannot access")) {
                return String.format("%s does not have access to %s", username, listid);
            } else {
                return checkAccessMsg;
            }
        } else {
            return "Error: Unable to connect " + url;
        }
    }

    /**
     * Return all the users who can access listid
     * @param listid
     * @return Return all the users who can access listid; error message otherwise
     */
    public String getAllUsers(String listid){
        if (isConnected()) {

            String sql = "SELECT USERNAME FROM USERS_INFO WHERE ?=ANY(LISTIDS);";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, listid);
                ResultSet rs = stmt.executeQuery();

                List<String> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(rs.getString("username"));
                }
                return users.toString();
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        } else {

            return "Error: Unable to connect " + url;
        }
    }

    public String deleteAllLists(String userId){
        if (isConnected()) {
            String sql = "SELECT listids FROM Users_info WHERE username=?;";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1,userId);
                ResultSet rs = stmt.executeQuery();
                List<Integer> listids = new ArrayList<>();
                while (rs.next()) {
                    Array result = rs.getArray(1);
                    String[] result_2 = (String[]) result.getArray();
                    if(result_2.length == 0){

                        return String.format("No list under the user (%s).", userId);
                    }
                    for (String listid : result_2){

                        deleteList(userId,listid);
                    }
                }
                return String.format("Successfully delete all lists under by %s", userId);
            }catch(SQLException e) {
                return "Error: " + e.getMessage();
            }

        } else {
            return "Error: Unable to connect " + url;
        }

    }

    /**
     *
     * @param username
     * @param listname
     * @return
     */
    public String addList(String username, String listname) throws SQLException {
        if (isConnected()) {
            // check whether the listname is unique for this username
            if (!isExistingListname(username, listname)) {
                // create a list with the given listname and list_type.
                // Return listid to add access for username to listid in users_info table
                String sql = "INSERT INTO lists (listname) VALUES (?) RETURNING listid;";
                try  {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, listname);


                    ResultSet rs = statement.executeQuery();
                    String listid = "";
                    while(rs.next()) {
                        listid = String.valueOf(rs.getInt("listid"));
                    }
                    // grant user access to listid
                    String grantAccessMsg = grantAccess(username, listid);
                    if (grantAccessMsg.contains("Error")) {
                        return "Error: Unable to create list";
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Message", "Successfully create list: " + listname);
                    jsonObject.put("listid", listid);
                    return jsonObject.toString();
                } catch (SQLException e) {
                    return "Error: " + e.getMessage();
                }
            }
            return String.format(
                    "Error: listname (%s) has been taken. Please choose another listname", listname);
        } else {
            return "Error: Unable to connect " + url;
        }

//

    }

    /**
     * Check whether the given listname is existing under the given username
     * @param username
     * @param listname
     * @return true if the listname exists under the given username. False otherwise
     * @throws SQLException
     */
    private boolean isExistingListname(String username, String listname) throws SQLException {
        if (isConnected()) {
            System.out.println("isExistingListname " + username + ", " + listname);
            String sql1 = "SELECT COUNT(*) FROM(SELECT l.listname FROM "
                    + "(SELECT UNNEST(listids)::INTEGER AS listid FROM "
                    + "Users_info Where username = ?) AS i "
                    + "INNER JOIN Lists AS l ON i.listid = l.listid) listnames "
                    + "WHERE listname = ?;";
            try {
                PreparedStatement statement = connection.prepareStatement(sql1);
                statement.setString(1, username);
                statement.setString(2, listname);
                System.out.println(statement.toString());
                ResultSet rs = statement.executeQuery();
                int cnt = 0;
                while (rs.next()) {
                    cnt = rs.getInt(1);
                    System.out.println(cnt);
                }
                if (cnt > 0) {
                    System.out.println(listname + " exists in " + username);
                    return true;
                }
                System.out.println(listname + " does not exist in " + username);
                return false;

            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        } else {
            System.out.println("Error: Unable to connect " + url);
            return false;
        }
    }

    public String deleteList(String userName, String listName) {
        if (isConnected()) {
            String list_id = userName+"_"+listName;
            String sql = "DELETE FROM Lists WHERE listid=?;";
            try {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(list_id));
                int rows = stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return "Error: " + e.getMessage();
            }
            String update = removeAccess(userName,list_id);
            if(update.contains("successfully")){
                return String.format("Successfully delete list (%s).", listName);
            }
            else {
                System.out.println("update info table failed");
                return String.format("Error: Failed to delete list (%s)." , listName);
            }
        } else  {
            return "Error: Unable to connect to " + url;
        }
    }

    public String deleteList2(String username, String listid) {
        if (isConnected()) {
            // check whether the listid accessible by user
            String accessMsg = removeAccess(username, listid);
            System.out.println(accessMsg);
            if(!accessMsg.contains("Error")) {
                String sql = "DELETE FROM Lists WHERE listid=?;";

                try {
                    PreparedStatement statement = connection.prepareStatement(sql);

                    statement.setInt(1, Integer.parseInt(listid));
                    int row = statement.executeUpdate();

                    System.out.println("delete successfully " + listid);
                    return "delete successfully " + listid;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return "Error: " + e.getMessage();
                }

            } else {
                return accessMsg;
            }

        } else {
            return "Error: Unable to connect to " + url;
        }
    }

//    public String getAllLists(String user_id){
//        String[] result = {};
//        if (isConnected()) {
//            String sql_get_list_name = "SELECT listids FROM Users_info WHERE username=?;";
//            try {
//                PreparedStatement stmt = connection.prepareStatement(sql_get_list_name);
//                stmt.setString(1,user_id);
//                ResultSet rs = stmt.executeQuery();
//                while (rs.next()) {
//                    Array resultList = rs.getArray(1);
//                    result = (String[]) resultList.getArray();
//                }
//            }catch(SQLException e){
//                return "Error: " + e.getMessage();
//            }
//            if(result.length == 0){
//                System.out.println("There exits no lists");
//                return String.format("Error: user (%s) has no list.", user_id);
//            }
//            for (int i =0; i<result.length;i++){
//                String sql_get_name_type = "SELECT listid, list_type FROM Todo_lists WHERE listid=?;";
//                try {
//                    PreparedStatement stmt_name_type = connection.prepareStatement(sql_get_name_type);
//                    stmt_name_type.setString(1,result[i]);
//                    ResultSet rs_name_type = stmt_name_type.executeQuery();
//                    System.out.println("Here is all your to do lists");
//                    String msg = ""
//                    while (rs_name_type.next()) {
//                        msg += ("Name: " + rs_name_type.getString("lsitid")
//                                + " Type: " + rs_name_type.getString("list_type") + "\n");
//
//                    }
//                    return msg;
//                }catch(SQLException e){
//                    return "Error: " + e.getMessage();
//                }
//            }
//            return true;
//        } else {
//            return false;
//        }
//
//    }

    public String getAllLists(String username) {
        if (isConnected()) {
            //
            String sql = "select l.listid, l.listname, " +
                    "l.list_type from lists as l inner join(SELECT " +
                    "UNNEST(listids)::INTEGER as listid from " +
                    "users_info where username=?) i on l.listid = i.listid " +
                    "order by l.last_modified_date desc;";

            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                ResultSet rs = statement.executeQuery();
                ResultSetMetaData md = rs.getMetaData();
                int numCols = md.getColumnCount();
                List<String> colNames = IntStream.range(0, numCols)
                        .mapToObj(i -> {
                            try {
                                return md.getColumnName(i + 1);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                return "?";
                            }
                        })
                        .collect(Collectors.toList());
                JSONArray result = new JSONArray();
                while(rs.next()) {
                    JSONObject row = new JSONObject();
                    row.put("listid", rs.getInt("listid"));
                    row.put("listname", rs.getString("listname"));
                    result.put(row);
                }
                return result.toString();
            } catch (SQLException e) {
                return "Error: " + e.getMessage();
            }
        } else {
            return "Error: Unable to connect to " + url;
        }
    }



}