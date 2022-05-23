package edu.uwb.css533.service.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateTaskContent {
    String username;
    String listid;
    String taskid;
    String content;

    public UpdateTaskContent() {
    }

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
    @JsonProperty("content")
    public String getContent() {
        return content;
    }
}
