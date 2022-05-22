package edu.uwb.css533.service.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddTask {
    String username;
    String listid;
    String taskname;
    String content;

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("listid")
    public String getListid() {
        return listid;
    }

    @JsonProperty("taskname")
    public String getTaskname() {
        return taskname;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }
}
