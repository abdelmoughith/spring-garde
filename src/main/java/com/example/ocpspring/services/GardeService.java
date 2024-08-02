package com.example.ocpspring.services;

import com.example.ocpspring.Repositories.GardeRepository;
import com.example.ocpspring.Repositories.ServiceRepository;
import com.example.ocpspring.Repositories.UserRepository;
import com.example.ocpspring.config.JwtUtil;
import com.example.ocpspring.models.garde.Garde;
import com.example.ocpspring.models.servicepack.ServiceTable;
import com.example.ocpspring.models.userspack.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class GardeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GardeRepository gardeRepository;

    @Autowired
    private ServiceRepository serviceRepository;
    // Map to keep track of the last assigned index for each service
    private final Map<Long, Integer> lastAssignedIndex = new ConcurrentHashMap<>();

    public void assignGardeForNext8Weeks() {
        List<ServiceTable> services = serviceRepository.findAll();
        for (ServiceTable service : services) {
            List<User> users = userRepository.findByServiceTable(service);
            if (users.isEmpty()) {
                continue; // Skip services with no users
            }
            // Sort users by their ID in ascending order
            List<User> sortedUsers = users.stream()
                    .sorted(Comparator.comparing(User::getId))
                    .toList();
            // Get the last assigned index or start from 0
            int currentIndex = lastAssignedIndex.getOrDefault(service.getId(), 0);
            // Loop through the next 8 weekends
            for (int i = 0; i < 8; i++) {
                LocalDate weekendDate = getNextWeekend(LocalDate.now().plusWeeks(i));
                // Assign the user based on the current index
                User user = sortedUsers.get(currentIndex);
                // Save the assignment
                Garde garde = new Garde();
                garde.setWeekendDate(weekendDate);
                garde.setUser(user);
                garde.setServiceTable(service);
                gardeRepository.save(garde);
                // Update the last assigned index
                currentIndex = (currentIndex + 1) % sortedUsers.size();
            }
            // Update the map with the new index
            lastAssignedIndex.put(service.getId(), currentIndex);
        }
    }

    private LocalDate getNextWeekend(LocalDate date) {
        while (date.getDayOfWeek() != DayOfWeek.SATURDAY) {
            date = date.plusDays(1);
        }
        return date;
    }
    public List<Garde> findByWeekendDate(LocalDate localDate){
         return gardeRepository.findByWeekendDate(localDate)
                .orElseThrow(() -> new RuntimeException("garde not found"));
    }
    public Garde updateGarde(Garde garde){
        return gardeRepository.save(garde);
    }
    @Autowired
    JwtUtil jwtUtil;

    public Garde findByWeekendDateAndUserId(String jwt, LocalDate localDate){
        List<Garde> gardes = gardeRepository.findByWeekendDate(localDate)
                .orElseThrow(() -> new RuntimeException("garde not found"));
        String user = jwtUtil.extractUsername(jwt);
        Garde result = gardes.stream().filter(garde ->
            garde.getUser().getUsername().equals(user)
        ).findFirst().orElse(null);
        return result;
    }


}

