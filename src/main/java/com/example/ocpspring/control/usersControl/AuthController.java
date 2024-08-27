package com.example.ocpspring.control.usersControl;

import com.example.ocpspring.config.JwtUtil;
import com.example.ocpspring.models.request.AuthenticationResponse;
import com.example.ocpspring.models.request.LoginRequest;
import com.example.ocpspring.models.userspack.Role;
import com.example.ocpspring.models.userspack.User;
import com.example.ocpspring.models.userspack.UserUpdateRequest;
import com.example.ocpspring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getPassword()
        );
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/secretaires")
    public List<User> getSecretaires() {
        return userService.getUsersByRole(Role.SECRETAIRE);
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long userId
    ) {
        try {
            userService.deleteUser(userId);
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("response", "User deleted successfully.");
            return ResponseEntity.ok(responseMap);
        } catch (RuntimeException e) {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("response", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMap);
        }
    }
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequest userUpdateRequest) {

        User updatedUser = userService.updateUser(userId, userUpdateRequest);
        String userResponse = "User updated: " +
                updatedUser.getFirstname() +
                " " +
                updatedUser.getLastname() +
                ", Service: " +
                (updatedUser.getServiceTable() != null ?
                        updatedUser.getServiceTable().getName() : "None");

        // Create a response map
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("response", userResponse);
        return ResponseEntity.ok(responseMap);
    }



}
