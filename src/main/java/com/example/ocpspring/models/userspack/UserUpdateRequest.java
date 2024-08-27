package com.example.ocpspring.models.userspack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String username;
    private String firstname;
    private String lastname;
    private Long serviceId;

    // Constructor, getters, and setters
    public UserUpdateRequest(String username, String firstname, String lastname, Long serviceId) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.serviceId = serviceId;
    }

    // Getters and setters...
}

