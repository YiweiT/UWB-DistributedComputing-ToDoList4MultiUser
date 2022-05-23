package edu.uwb.css533.service.resources.RequestObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddTask {
    String username;
    String listid;
    String taskname;
    String content;

    public AddTask(String username, String listid, String taskname, String content) {
        this.username = username;
        this.listid = listid;
        this.taskname = taskname;
        this.content = content;
    }

    public AddTask() {
    }

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
