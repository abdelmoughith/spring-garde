package com.example.ocpspring.services;

import com.example.ocpspring.Repositories.CollaborateurRepository;
import com.example.ocpspring.models.collaborateur.Collaborateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollaborateurServiceForSecretaire {
    @Autowired
    CollaborateurRepository repository;

    public Collaborateur createColab(Collaborateur collaborateur){
        if (repository.existsByUsername(collaborateur.getUsername())){
            throw new DataIntegrityViolationException("User already exists with this username");
        }
        return repository.save(collaborateur);
    }
    public List<Collaborateur> getAllColabs() {
        return repository.findAll();
    }

    // Get a Collaborateur by ID
    public Collaborateur getColabById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collaborateur not found with ID: " + id));
    }

    // Update an existing Collaborateur
    public Collaborateur updateColab(Long id, Collaborateur updatedCollaborateur) {
        Collaborateur existingCollaborateur = getColabById(id);

        existingCollaborateur.setFirstname(updatedCollaborateur.getFirstname());
        existingCollaborateur.setLastname(updatedCollaborateur.getLastname());
        existingCollaborateur.setUsername(updatedCollaborateur.getUsername());
        existingCollaborateur.setDate(updatedCollaborateur.getDate());

        return repository.save(existingCollaborateur);
    }

    // Delete a Collaborateur by ID
    public void deleteColab(Long id) {
        Collaborateur existingCollaborateur = getColabById(id);
        repository.delete(existingCollaborateur);
    }

}
