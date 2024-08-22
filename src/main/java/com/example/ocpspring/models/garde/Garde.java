package com.example.ocpspring.models.garde;

import com.example.ocpspring.models.collaborateur.Collaborateur;
import com.example.ocpspring.models.servicepack.ServiceTable;
import com.example.ocpspring.models.userspack.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "garde", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"weekendDate", "user_id", "service_id"})
})
public class Garde {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate weekendDate;

    @ManyToOne
    @JoinColumn(name = "collaborateur_id", nullable = false)
    private Collaborateur collaborateur;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceTable serviceTable;


    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean disponibilite = true;

    @Column
    private String checked = null;
}

