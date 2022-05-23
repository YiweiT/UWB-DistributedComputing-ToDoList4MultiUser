package edu.uwb.css533.service.resources.RequestObjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangePwd {
    String username;
    String oldPassword;
    String newPassword;

    public ChangePwd(String username, String oldPassword, String newPassword) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public ChangePwd() {
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("oldPassword")
    public String getOldPassword() {
        return oldPassword;
    }

    @JsonProperty("newPassword")
    public String getNewPassword() {
        return newPassword;
    }
}
