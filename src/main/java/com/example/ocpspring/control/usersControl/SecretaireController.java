package com.example.ocpspring.control.usersControl;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class SecretaireController {

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        // Return user information based on the principal
        return ResponseEntity.ok("User info for: " + principal.getName());
    }
}
