package edu.uwb.css533.service.resources;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateTaskStatus {
    private String username;
    private String listid;
    private String taskid;
    private String status;

    @JsonGetter("username")
    public String getUsername() {
        return username;
    }
    @JsonGetter("listid")
    public String getListid() {
        return listid;
    }
    @JsonGetter("taskid")
    public String getTaskid() {
        return taskid;
    }
    @JsonGetter("status")
    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "UpdateContent{" +
                "username='" + username + '\'' +
                ", listid='" + listid + '\'' +
                ", taskid='" + taskid + '\'' +
                ", content='" + status + '\'' +
                '}';
    }
}
