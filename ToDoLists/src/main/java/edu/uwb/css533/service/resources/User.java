package edu.uwb.css533.service.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    public String username;

    public String password;

    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("username") String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
