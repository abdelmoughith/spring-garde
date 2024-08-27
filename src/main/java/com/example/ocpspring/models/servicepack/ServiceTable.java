package com.example.ocpspring.models.servicepack;

import com.example.ocpspring.models.userspack.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "service")
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;


    public ServiceTable(Long id) {
        this.id = id;
    }
}