package edu.uwb.css533.service.resources.RequestObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddList {
    String username;
    String listname;

    public AddList(String username, String listname) {
        this.username = username;
        this.listname = listname;
    }

    public AddList() {
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }
    @JsonProperty("listname")
    public String getListname() {
        return listname;
    }
}
