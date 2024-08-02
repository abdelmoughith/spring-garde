package com.example.ocpspring.models.servicepack;

import com.example.ocpspring.models.userspack.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "service")
public class ServiceTable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;



}