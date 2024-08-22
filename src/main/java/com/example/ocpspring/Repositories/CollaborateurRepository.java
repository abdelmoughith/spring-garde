package com.example.ocpspring.Repositories;

import com.example.ocpspring.models.collaborateur.Collaborateur;
import com.example.ocpspring.models.servicepack.ServiceTable;
import com.example.ocpspring.models.userspack.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaborateurRepository extends JpaRepository<Collaborateur, Long> {
    boolean existsByUsername(String username);
    List<Collaborateur> findByServiceTable(ServiceTable serviceTable);
}
