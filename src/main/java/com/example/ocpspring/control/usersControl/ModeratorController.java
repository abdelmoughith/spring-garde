package com.example.ocpspring.control.usersControl;

import com.example.ocpspring.models.collaborateur.Collaborateur;
import com.example.ocpspring.models.garde.Garde;
import com.example.ocpspring.models.request.BooleanRequest;
import com.example.ocpspring.models.userspack.User;
import com.example.ocpspring.services.GardeService;
import com.example.ocpspring.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/moderator")
public class ModeratorController {

    @Autowired
    private GardeService gardeService;

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getUserInfo(Principal principal) {
        // Return user information based on the principal

        return ResponseEntity.ok(Map.of("username", principal.getName()));
    }

    private static final Logger logger = LoggerFactory.getLogger(ModeratorController.class);

    /*
    @PutMapping("/updateGarde")
    public ResponseEntity<?> findGardeWeekend(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody Map<String, ?> request
    ){
        // extract token from header
        String jwtToken;
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // Extract the token
            jwtToken = bearerToken.substring(7);
        } else {
            // Handle the case where the token is missing or malformed
            throw new RuntimeException("JWT Token is missing or not properly formatted");
        }
        // token done
        LocalDate weekendDate = LocalDate.parse((CharSequence) request.get("weekendDate"));

        //Long realUserId = userService.getCurrentUserId(jwtToken);

        Garde garde = gardeService.findByWeekendDateAndUserId(jwtToken, weekendDate);

        if (garde != null ){
            garde.setDisponibilite(false);
            gardeService.updateGarde(garde);
            return ResponseEntity.ok(garde);
        }else {
            return ResponseEntity.badRequest().body("You are not permitted to set values for other users");
        }

    }

     */
    @GetMapping("/getbyservice/{service_id}")
    public ResponseEntity<List<Collaborateur>> findUsersByService(@PathVariable Long service_id){
        return ResponseEntity.ok(gardeService.getCollabsByService(service_id));
    }
    @PutMapping("/disponibilite/{id}")
    public ResponseEntity<Map<String, String>> updateDisponibilite(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody BooleanRequest disponibilite) {
        Map<String, String> response = new HashMap<>();
        try {

            // Get the current user ID from the JWT token
            Long currentUserId = userService.getCurrentUserId(token.replace("Bearer ", ""));

            // Load the current user and get their ServiceTable
            User currentUser = userService.loadUserById(currentUserId);
            Garde garde = gardeService.getGardeById(id);

            // Check if the Collaborateur's ServiceTable matches the current user's ServiceTable
            if (garde.getServiceTable().getId().equals(currentUser.getServiceTable().getId())) {
                gardeService.updateDisponibilite(id, disponibilite.isDisponibilite());
                response.put("response", "Disponibilite updated successfully");
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                response.put("response", "Unauthorized");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Conflict status code
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // Not found status code if user or service not found
        } catch (Exception ex) {
            response.put("response", "Error updating disponibilite.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
