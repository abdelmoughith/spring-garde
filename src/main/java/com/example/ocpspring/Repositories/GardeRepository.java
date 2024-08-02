package com.example.ocpspring.Repositories;

import com.example.ocpspring.models.garde.Garde;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GardeRepository extends JpaRepository<Garde, Long> {

    Optional<List<Garde>> findByWeekendDate(LocalDate localDate);
}
