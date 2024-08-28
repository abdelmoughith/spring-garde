package com.example.ocpspring.control.usersControl;

import com.example.ocpspring.models.userspack.User;
import com.example.ocpspring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class SecretaireController {
    @Autowired
    UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getUserInfo(Principal principal) {
        // Return user information based on the principal
        // Get the username of the authenticated user
        String username = principal.getName();

        // Find the user in the database
        User user = userService.findByUsername(username);

        // Return the user data
        return ResponseEntity.ok(user);
    }
}
