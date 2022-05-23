package edu.uwb.css533.service.resources.RequestObjects;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateTaskContent {
    private String username;
    private String listid;
    private String taskid;
    private String content;

    public UpdateTaskContent(String username, String listid, String taskid, String content) {
        this.username = username;
        this.listid = listid;
        this.taskid = taskid;
        this.content = content;
    }

    public UpdateTaskContent() {
    }

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
