package edu.uwb.css533.service.db;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.eclipse.jetty.util.ajax.JSON;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class TaskServiceDB extends ListServiceDB {
    public boolean checkExist(String taskId, String listid){
        String sql = "SELECT TASKID FROM TASKS WHERE TASKID= ? AND LISTID=?;";
        try {

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1,Integer.parseInt(taskId));
            stmt.setInt(2, Integer.parseInt(listid));
            ResultSet rs = stmt.executeQuery();
//            if (rs == null){
//                System.out.println("No data found.");
//                return false;
//            }
            while (rs.next()) {
                String compare = Integer.toString(rs.getInt("taskid"));
                if(taskId.equals(compare)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }
    public boolean checkDuplicate(String taskName, String listId){
        String sql = "SELECT TASKNAME FROM TASKS WHERE LISTID= ?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(listId));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if(taskName.equals(rs.getString("TASKNAME"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }

    public String getTask(String username, String listid, String taskid) {
        if (isConnected()) {
            // check taskid existing
            if (checkExist(taskid, listid)) {
                // check username listid accessibility
                String accessMsg = checkAccess(username, listid);
                if (accessMsg.contains("can access")) {
                    // fetch task information: taskid, taskname, content, status
                    return getTask(taskid);

                } else {
                    return accessMsg;
                }

            } else {
                System.out.println(String.format("Error: no such task (%s) exists in list (%s).", taskid, listid));
                return String.format(String.format("Error: no such task (%s) exists in list (%s).", taskid, listid));
            }


        } else {
            return "Error: Unable to connect to db - " + url;
        }

    }

    private String getTask(String taskid) {
        String sql = "SELECT TASKID, TASKNAME, CONTENT, STATUS " +
                "FROM TASKS WHERE TASKID=?;";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(taskid));
            ResultSet rs = statement.executeQuery();
            JSONObject row = new JSONObject();
            while (rs.next()) {
                row.put("taskid", rs.getInt("taskid"));
                row.put("taskname", rs.getString("taskname"));
                row.put("content", rs.getString("content"));
                row.put("status", rs.getString("status"));
            }
            return row.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }


    public String addTask(String task_name, String task_description, String list_id, String user_name){
//        String[] returnValue = {};
        JSONObject jsonObject = new JSONObject();
        int taskId = 0;
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
//            returnValue[0] = "Invalid user";
            jsonObject.put("Error", "Invalid user");
            return jsonObject.toString();
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Unable to connect to database.");
//            returnValue[0] = "Unable to connect to database.";
            jsonObject.put("Error", "Unable to connect to database.");
            return jsonObject.toString();
        }

        boolean taskNameExist = checkDuplicate(task_name,list_id);
        if(taskNameExist){
            System.out.println("taskName has already exists for current list");
//            returnValue[0] = "taskName has already exists for current list";
            jsonObject.put("Error", String.format("%s has already exists for current list.", task_name));
            return jsonObject.toString();
        }

        String sql = "INSERT INTO TASKS (TASKNAME, CONTENT, LISTID) VALUES ( ?, ?, ?) RETURNING TASKID;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, task_name);
            stmt.setString(2, task_description);

            stmt.setInt(3, Integer.parseInt(list_id));
//            java.util.Date date = new Date();
//            Timestamp ts = new Timestamp(date.getTime());
//            stmt.setTimestamp(5, ts);
            stmt.execute();
            ResultSet id = stmt.getResultSet();
            id.next();
            taskId = id.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
//            returnValue[0] = e.getMessage();
            jsonObject.put("Error", e.getMessage());
            return jsonObject.toString();
        }

//        returnValue[0] = "Successfully insert the task: " + task_name+ " of current list: "+list_id;
//        returnValue[1] = Integer.toString(taskId);
        jsonObject.put("Message", "Successfully create the task: " + task_name+ " in current list: "+list_id);
        jsonObject.put("taskid", taskId);
        String listUpdate = updateListDate(list_id);
        if(!listUpdate.contains("Successfully")){
//            returnValue[2] = listUpdate;
            jsonObject.put("Error", listUpdate);
        }
        return jsonObject.toString();
    }
    public String deleteTask(String task_id, String list_id,String userName){
        boolean notAllowed = checkAccess(userName,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Unable to connect to database.");
            return "Unable to connect to database.";
        }
        boolean taskNameExist = checkExist(task_id, list_id);

        if(!taskNameExist){
            System.out.println(String.format("Task (%s) does not exists for current list (%s)", task_id, list_id));
            return String.format("Task (%s) does not exists for current list (%s)", task_id, list_id);

        }

        String sql = "DELETE FROM TASKS WHERE TASKID= ? and LISTID=?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1,Integer.parseInt(task_id));
            stmt.setInt(2, Integer.parseInt(list_id));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                String listUpdate = updateListDate(list_id);
                if(!listUpdate.contains("Successfully")){
                    return listUpdate;
                }
                return " Successfully deleted task " + task_id +" of list " + list_id + " for user: " + userName;
            } else {
                return String.format("Error: task (%s) of list (%s) does not exist.", task_id, list_id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }


    }

    public String deleteAllTasks(String user_name, String list_id){
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Error: Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println(("Error: Unable to connect to database."));
            return "Error: Unable to connect database.";
        }
        String sql = "DELETE FROM TASKS WHERE LISTID=?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(list_id));
            stmt.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        String listUpdate = updateListDate(list_id);
        if(!listUpdate.contains("Successfully")){
            return listUpdate;
        }
        return "Successfully deleted all tasks of  list " +list_id;
    }

    public String displayAllTaskNames(String user_name, String list_id){
        String msg="";
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println(("Unable to connect to database."));
            return "Unable to connect to database.";
        }
        String sql_get_task_name = "SELECT TASKID, TASKNAME, CONTENT, STATUS FROM TASKS WHERE LISTID=?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql_get_task_name);
            stmt.setInt(1, Integer.parseInt(list_id));
            ResultSet rs = stmt.executeQuery();
//            if (rs == null){
//                System.out.println("No task found for current list.");
//                return "No task found for current list.";
//            }
            msg = msg+ "Successfully display all tasks. Here is all your tasks:"+"\n";
            System.out.println("Here is all your tasks:");
            JSONArray result = new JSONArray();
            while (rs.next()) {
                System.out.println("Task Name: " + rs.getString("taskname"));
                JSONObject row = new JSONObject();
                row.put("taskid", rs.getInt("taskid"));
                row.put("taskname", rs.getString("taskname"));
                row.put("content", rs.getString("content"));
                row.put("status", rs.getString("status"));
                result.put(row);

                System.out.println("Task Name: " + rs.getString("taskname"));
            }
            if (result.isEmpty()) {
                System.out.println("No task found for current list.");
                return "No task found for current list.";
            }
            return result.toString();
        }catch(SQLException e){
            e.printStackTrace();
            return e.getMessage();
        }

    }



    public String updateTaskContent(String user_name, String list_id, String content, String task_id){
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Unable to connect database.");
            return "Unable to connect to database.";
        }
        String listUpdate = updateListDate(list_id);
        if(!listUpdate.contains("Successfully")){
            return listUpdate;
        }
        return "Successfully updated task content:\n "+content+"\nfor task "+task_id;
    }


    public String updateTaskStatus(String user_name, String list_id,String status, String task_id){
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Unable to connect to database.");
            return "Unable to connect to database.";
        }
        if(! (status.equals("Not Started") || status.equals("Completed" )||status.equals("In-Progress" ))){
            System.out.println("Task type can only be Completed, Not Started or In-Progress");
            return "Task type can only be Completed, Not Started or In-Progress";
        }
        String update_task_status_sql = "UPDATE Tasks SET STATUS=? WHERE TASKID=?;";
        try{
            PreparedStatement stmt = connection.prepareStatement(update_task_status_sql);
            stmt.setString(1,status);

            stmt.setInt(2, Integer.parseInt(task_id));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        String listUpdate = updateListDate(list_id);
        if(!listUpdate.contains("Successfully")){
            return listUpdate;
        }

        return "Successfully updated task status: "+status+" for task "+task_id;
    }
    public String updateListDate(String listid){
        String sql = "UPDATE Lists SET LAST_MODIFIED_DATE=NOW() WHERE listid=?;";
        try{
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1,Integer.parseInt(listid));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Successfully update the last modified date of current list";
    }
}
