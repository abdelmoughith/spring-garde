package com.example.ocpspring.control.collaborateurCotroller;

import com.example.ocpspring.models.collaborateur.Collaborateur;
import com.example.ocpspring.models.servicepack.ServiceTable;
import com.example.ocpspring.models.userspack.User;
import com.example.ocpspring.services.CollaborateurServiceForSecretaire;
import com.example.ocpspring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collaborateurs")
public class ColabController {

    @Autowired
    private UserService userService;

    @Autowired
    private CollaborateurServiceForSecretaire collaborateurService;

    // Create a new Collaborateur
    @PostMapping("/post")
    public ResponseEntity<Collaborateur> createColab(@RequestHeader("Authorization") String token,
                                                     @RequestBody Collaborateur collaborateur) {
        try {
            // Get the current user ID from the JWT token
            Long currentUserId = userService.getCurrentUserId(token.replace("Bearer ", ""));

            // Load the current user and get their ServiceTable
            User currentUser = userService.loadUserById(currentUserId);
            ServiceTable userServiceTable = currentUser.getServiceTable();

            // Check if the Collaborateur's ServiceTable matches the current user's ServiceTable
            if (collaborateur.getServiceTable().getId().equals(userServiceTable.getId())) {
                Collaborateur createdCollaborateur = collaborateurService.createColab(collaborateur);
                return new ResponseEntity<>(createdCollaborateur, HttpStatus.CREATED);
            } else {
                // Return forbidden status if ServiceTable doesn't match
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Conflict status code
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Not found status code if user or service not found
        }
    }

    // Get all Collaborateurs
    @GetMapping("/getall")
    public ResponseEntity<List<Collaborateur>> getAllColabs() {
        List<Collaborateur> collaborateurs = collaborateurService.getAllColabs();
        return new ResponseEntity<>(collaborateurs, HttpStatus.OK);
    }

    // Get a Collaborateur by ID
    @GetMapping("get/{id}")
    public ResponseEntity<Collaborateur> getColabById(@PathVariable Long id) {
        try {
            Collaborateur collaborateur = collaborateurService.getColabById(id);
            return new ResponseEntity<>(collaborateur, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Not Found status code
        }
    }

    // Update an existing Collaborateur
    @PutMapping("put/{id}")
    public ResponseEntity<Collaborateur> updateColab(@RequestHeader("Authorization") String token,
                                                     @PathVariable Long id,
                                                     @RequestBody Collaborateur updatedCollaborateur) {
        try {
            // Get the current user ID from the JWT token
            Long currentUserId = userService.getCurrentUserId(token.replace("Bearer ", ""));

            // Load the current user and get their ServiceTable
            User currentUser = userService.loadUserById(currentUserId);
            ServiceTable userServiceTable = currentUser.getServiceTable();

            // Check if the Collaborateur's ServiceTable matches the current user's ServiceTable
            if (updatedCollaborateur.getServiceTable().getId().equals(userServiceTable.getId())) {
                Collaborateur collaborateur = collaborateurService.updateColab(id, updatedCollaborateur);
                return new ResponseEntity<>(collaborateur, HttpStatus.OK);
            } else {
                // Return forbidden status if ServiceTable doesn't match
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Conflict status code
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Not found status code if user or service not found
        }
    }

    // Delete a Collaborateur by ID
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteColab(@RequestHeader("Authorization") String token,
                                            @PathVariable Long id) {

        try {
            // Get the current user ID from the JWT token
            Long currentUserId = userService.getCurrentUserId(token.replace("Bearer ", ""));

            // Load the current user and get their ServiceTable
            User currentUser = userService.loadUserById(currentUserId);
            ServiceTable userServiceTable = currentUser.getServiceTable();

            Collaborateur collaborateurToDelete = collaborateurService.getColabById(id);

            // Check if the Collaborateur's ServiceTable matches the current user's ServiceTable
            if (collaborateurToDelete.getServiceTable().getId().equals(userServiceTable.getId())) {
                collaborateurService.deleteColab(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                // Return forbidden status if ServiceTable doesn't match
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Conflict status code
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Not found status code if user or service not found
        }

    }
}

