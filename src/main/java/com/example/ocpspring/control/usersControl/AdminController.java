package com.example.ocpspring.control.usersControl;

import com.example.ocpspring.models.collaborateur.Collaborateur;
import com.example.ocpspring.models.garde.Garde;
import com.example.ocpspring.models.request.ServiceID;
import com.example.ocpspring.models.userspack.Role;
import com.example.ocpspring.models.request.RoleChangeRequest;
import com.example.ocpspring.models.userspack.User;
import com.example.ocpspring.services.GardeService;
import com.example.ocpspring.services.ServiceService;
import com.example.ocpspring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private GardeService gardeService;


    @PutMapping("change-service/{userId}")
    public ResponseEntity<Map<String, String>> changeUserService(
            @PathVariable Long userId,
            @RequestBody ServiceID serviceId
    ) {
        User updatedUser = userService.changeUserService(userId, serviceId.getServiceId());
        String userResponse;

        if (serviceId.getServiceId() != -1L) {
            userResponse = "User : " +
                    updatedUser.getFirstname() +
                    " " +
                    updatedUser.getLastname() +
                    " " +
                    "service updated to " +
                    serviceService.getServiceById(serviceId.getServiceId()).get().getName();
        } else {
            userResponse = "User : " +
                    updatedUser.getFirstname() +
                    " " +
                    updatedUser.getLastname() +
                    " " +
                    "service set to null";
        }

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("response", userResponse);
        return ResponseEntity.ok(responseMap);
    }





    @PutMapping("/change-role/{userId}")
    public ResponseEntity<?> changeUserRole(
            @PathVariable Long userId,
            @RequestBody RoleChangeRequest roleChangeRequest
    ) {
        // Validate and convert the role string to the Role enum
        Role newRole;
        try {
            newRole = Role.valueOf(roleChangeRequest.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role: " + roleChangeRequest.getRole());
        }

        User updatedUser = userService.changeUserRole(userId, newRole);
        String userResponse = "User : " +
                updatedUser.getFirstname() +
                " " +
                updatedUser.getLastname() +
                " " +
                "updated role to : " + roleChangeRequest.getRole().toUpperCase();
        return ResponseEntity.ok(userResponse);
    }

    // json field must be named "weekendDate"
    @GetMapping("/getgarde")
    public ResponseEntity<List<Garde>> findGardeWeekend(@RequestBody Map<String, String> request){
        LocalDate weekendDate = LocalDate.parse(request.get("weekendDate"));
        List<Garde> garde = gardeService.findByWeekendDate(weekendDate);
        return ResponseEntity.ok(garde);
    }

    @GetMapping("/service_user/{service_id}")
    public ResponseEntity<List<User>> findUsersByService(@PathVariable Long service_id){
        return ResponseEntity.ok(userService.getUsersByService(service_id));
    }
    @GetMapping("/getgarde/{id}")
    public ResponseEntity<List<Garde>> getGardeOfService(@PathVariable Long id){
        if (id == -1L){
            return ResponseEntity.ok(gardeService.getAllGardes());
        }
        return ResponseEntity.ok(gardeService.getGardeByService(id));
    }

}


