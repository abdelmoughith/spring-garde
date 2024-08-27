package com.example.ocpspring.Repositories;

import com.example.ocpspring.models.collaborateur.Collaborateur;
import com.example.ocpspring.models.garde.Garde;
import com.example.ocpspring.models.servicepack.ServiceTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GardeRepository extends JpaRepository<Garde, Long> {

    Optional<List<Garde>> findByWeekendDate(LocalDate localDate);
    List<Garde> findByServiceTable(ServiceTable serviceTable);

    List<Garde> findAllByWeekendDateAfterOrderByWeekendDateAsc(LocalDate now);
    List<Garde> findAllByIdGreaterThanEqualAndServiceTableOrderByWeekendDateAsc(Long id, ServiceTable serviceTable);




    boolean existsByServiceTableAndWeekendDate(ServiceTable service, LocalDate weekendDate);

    // Method to retrieve the most recent Garde record for a given Collaborateur
    Optional<Garde> findFirstByCollaborateurOrderByWeekendDateDesc(Collaborateur collaborateur);
    Optional<Garde> findFirstByServiceTableOrderByWeekendDateDesc(ServiceTable serviceTable);
    @Query("SELECT MAX(g.weekendDate) FROM Garde g WHERE g.serviceTable = :serviceTable")
    Optional<LocalDate> findLatestWeekendDateByServiceTable(ServiceTable serviceTable);
    boolean existsByWeekendDate(LocalDate weekendDate);

}
