package com.example.ocpspring.Repositories;

import com.example.ocpspring.models.servicepack.ServiceTable;
import com.example.ocpspring.models.userspack.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByServiceTable(ServiceTable serviceTable);
}