package com.example.ocpspring.services;

import com.example.ocpspring.Repositories.CollaborateurRepository;
import com.example.ocpspring.Repositories.GardeRepository;
import com.example.ocpspring.Repositories.ServiceRepository;
import com.example.ocpspring.Repositories.UserRepository;
import com.example.ocpspring.config.JwtUtil;
import com.example.ocpspring.models.collaborateur.Collaborateur;
import com.example.ocpspring.models.garde.Garde;
import com.example.ocpspring.models.servicepack.ServiceTable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GardeService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CollaborateurRepository collaborateurRepository;

    @Autowired
    private GardeRepository gardeRepository;

    @Autowired
    private ServiceRepository serviceRepository;
    // Map to keep track of the last assigned index for each service
    private final Map<Long, Integer> lastAssignedIndex = new ConcurrentHashMap<>();

    public void assignGardeForNext8Weeks() {
        List<ServiceTable> services = serviceRepository.findAll();
        for (ServiceTable service : services) {
            List<Collaborateur> collaborateurList = collaborateurRepository.findByServiceTable(service);
            if (collaborateurList.isEmpty()) {
                continue; // Skip services with no users
            }
            // Sort users by their ID in ascending order
            List<Collaborateur> sortedUsers = collaborateurList.stream()
                    .sorted(Comparator.comparing(Collaborateur::getId))
                    .toList();
            // Get the last assigned index or start from 0
            int currentIndex = lastAssignedIndex.getOrDefault(service.getId(), 0);

            LocalDate currentDate;
            if (getLatestWeekendDateByServiceTable(service) == null){
                currentDate = getNextWeekend(LocalDate.now());
            } else {
                currentDate = getNextWeekend(getLatestWeekendDateByServiceTable(service).plusWeeks(1));
            }
            // Loop through the next 8 weekends
            for (int i = 0; i < sortedUsers.size() * 2; i++) {

                LocalDate weekendDate = currentDate.plusWeeks(i);
                // Assign the user based on the current index
                Collaborateur collaborateur = sortedUsers.get(currentIndex);
                // Save the assignment
                Garde garde = new Garde();
                garde.setWeekendDate(weekendDate);
                garde.setCollaborateur(collaborateur);
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
            garde.getCollaborateur().getUsername().equals(user)
        ).findFirst().orElse(null);
        return result;
    }

    public List<Collaborateur> getCollabsByService(Long serviceId){
        Optional<ServiceTable> service = serviceRepository.findById(serviceId);

        List<Collaborateur> collaborateurs = collaborateurRepository.findByServiceTable(service.orElseThrow(
                () -> new RuntimeException("User not found")
        ));

        return collaborateurs.stream()
                .sorted(Comparator.comparing(Collaborateur::getId))
                .toList();
    }


    @Transactional
    public void updateDisponibilite(Long gardeId, boolean disponibilite) {
        Garde garde = gardeRepository.findById(gardeId)
                .orElseThrow(() -> new EntityNotFoundException("Garde not found"));

        // Update the availability status
        garde.setDisponibilite(disponibilite);
        gardeRepository.save(garde);

        // If the user is no longer available, reassign future guard duties
        if (!disponibilite) {
            reassignGuardDutiesAndShiftDates(garde);
        }
    }

    private void reassignGuardDutiesAndShiftDates(Garde unavailableGarde) {

        List<Garde> futureGardes = gardeRepository.findAllByIdGreaterThanEqualAndServiceTableOrderByWeekendDateAsc(
                unavailableGarde.getId(), unavailableGarde.getServiceTable()
        );

        if (futureGardes.isEmpty()) {
            return; // No future assignments to reassign
        }else {
            //gardeRepository.deleteById(futureGardes.get(0).getId());
        }

        for (int i = futureGardes.size() -1 ; i > 0  ; i--) {
            Garde currentGarde = futureGardes.get(i);


            // Otherwise, shift the next user's weekendDate to the current one's
            Garde previousGarde = futureGardes.get(i - 1);
            currentGarde.setWeekendDate(previousGarde.getWeekendDate());
            gardeRepository.save(currentGarde);
        }

        // Finally, delete the original guard duties of the unavailable user
        //gardeRepository.deleteById(futureGardes.get(0).getId());
    }

    public Garde getGardeById(Long id) {
        return gardeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Garde not found with ID: " + id));
    }
    // Method to check if a collaborator is available
    public boolean isCollaborateurDisponible(Collaborateur collaborateur) {
        // Retrieve the latest garde record for the collaborator
        Optional<Garde> latestGarde = gardeRepository.findFirstByCollaborateurOrderByWeekendDateDesc(collaborateur);

        // If there's no garde record, or if the latest one has disponibilite set to true, return true (available)
        return latestGarde.map(Garde::isDisponibilite).orElse(true);
    }

    public LocalDate getLatestWeekendDateByServiceTable(ServiceTable serviceTable) {
        Optional<Garde> latestGarde = gardeRepository.findFirstByServiceTableOrderByWeekendDateDesc(serviceTable);
        return latestGarde.map(Garde::getWeekendDate).orElse(null);
    }

    public List<Garde> getGardeByService(Long serviceId){
        ServiceTable serviceTable = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        return gardeRepository.findByServiceTable(serviceTable);
    }
    public List<Garde> getAllGardes(){
        return gardeRepository.findAll();
    }
}

