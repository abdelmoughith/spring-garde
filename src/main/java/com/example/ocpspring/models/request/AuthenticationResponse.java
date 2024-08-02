package com.example.ocpspring.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
public class AuthenticationResponse {
    private final String jwt;
}
