package edu.uwb.css533.service.resources;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateTaskContent {
    private String username;
    private String listid;
    private String taskid;
    private String content;

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
    @JsonGetter("content")
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "UpdateContent{" +
                "username='" + username + '\'' +
                ", listid='" + listid + '\'' +
                ", taskid='" + taskid + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
