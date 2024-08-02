package com.example.ocpspring.control.usersControl;

import com.example.ocpspring.models.garde.Garde;
import com.example.ocpspring.models.userspack.User;
import com.example.ocpspring.services.GardeService;
import com.example.ocpspring.services.ServiceService;
import com.example.ocpspring.services.UserService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/moderator")
public class ModeratorController {

    @Autowired
    private UserService userService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private GardeService gardeService;

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        // Return user information based on the principal
        return ResponseEntity.ok("User info for: " + principal.getName());
    }

    private static final Logger logger = LoggerFactory.getLogger(ModeratorController.class);

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
}
