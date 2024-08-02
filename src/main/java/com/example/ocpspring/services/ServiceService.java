package com.example.ocpspring.services;


import com.example.ocpspring.Repositories.ServiceRepository;
import com.example.ocpspring.models.servicepack.ServiceTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<ServiceTable> getAllServices() {
        return serviceRepository.findAll();
    }

    public Optional<ServiceTable> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    public ServiceTable saveService(ServiceTable serviceTable) {
        return serviceRepository.save(serviceTable);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }


}
