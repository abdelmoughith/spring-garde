package com.example.ocpspring.control.serviceControl;


import com.example.ocpspring.models.servicepack.ServiceTable;
import com.example.ocpspring.services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping("/getall")
    public List<ServiceTable> getAllServices() {
        return serviceService.getAllServices();
    }

    @GetMapping("getall/{id}")
    public ResponseEntity<ServiceTable> getServiceById(@PathVariable Long id) {
        Optional<ServiceTable> service = serviceService.getServiceById(id);
        if (service.isPresent()) {
            return ResponseEntity.ok(service.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/post")
    public ServiceTable createService(@RequestBody ServiceTable serviceTable) {
        return serviceService.saveService(serviceTable);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ServiceTable> updateService(@PathVariable Long id, @RequestBody ServiceTable serviceDetails) {
        Optional<ServiceTable> service = serviceService.getServiceById(id);
        if (service.isPresent()) {
            ServiceTable serviceToUpdate = service.get();
            serviceToUpdate.setName(serviceDetails.getName());
            serviceToUpdate.setDescription(serviceDetails.getDescription());
            return ResponseEntity.ok(serviceService.saveService(serviceToUpdate));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
