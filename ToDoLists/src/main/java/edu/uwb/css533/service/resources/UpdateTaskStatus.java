package edu.uwb.css533.service.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateTaskStatus {
    String username;
    String listid;
    String taskid;
    String taskstatus;


    @JsonProperty("username")
    public String getUsername() {
        return username;
    }
    @JsonProperty("listid")
    public String getListId() {
        return listid;
    }
    @JsonProperty("taskid")
    public String getTaskId() {
        return taskid;
    }
    @JsonProperty("status")
    public String getTaskstatus() {
        return taskstatus;
    }

    @Override
    public String toString() {
        return "UpdateTaskStatus{" +
                "username='" + username + '\'' +
                ", listid='" + listid + '\'' +
                ", taskid='" + taskid + '\'' +
                ", taskstatus='" + taskstatus + '\'' +
                '}';
    }
}
