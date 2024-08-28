package com.example.ocpspring.services;

import com.example.ocpspring.Repositories.ServiceRepository;
import com.example.ocpspring.Repositories.UserRepository;
import com.example.ocpspring.config.JwtUtil;
import com.example.ocpspring.models.servicepack.ServiceTable;
import com.example.ocpspring.models.userspack.Role;
import com.example.ocpspring.models.userspack.User;
import com.example.ocpspring.models.userspack.UserUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ServiceRepository serviceRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())),
                user.getId()
        );
    }
    public User loadUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public User registerUser(String firstname, String lastname, String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setRole(Role.SECRETAIRE);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public User changeUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        return userRepository.save(user);
    }

    public User changeUserService(Long userId, Long serviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (serviceId != -1L) {
            ServiceTable serviceTable = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Service not found"));
            user.setServiceTable(serviceTable);
        } else {
            user.setServiceTable(null); // Allow setting serviceTable to null
        }

        return userRepository.save(user);
    }
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }


    @Autowired
    private JwtUtil jwtUtil;


    public Long getCurrentUserId(String jwt) {
        //String jwt = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return jwtUtil.extractUserId(jwt);
    }
    public List<User> getUsersByService(Long serviceId){
        Optional<ServiceTable> service = serviceRepository.findById(serviceId);

        List<User> users = userRepository.findByServiceTable(service.orElseThrow(
                () -> new RuntimeException("User not found")
        ));

        return users.stream()
                    .sorted(Comparator.comparing(User::getId))
                    .toList();
    }
    public List<User> getUsersByRole(Role role){
        return userRepository.findByRole(role);
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userUpdateRequest.getServiceId() != null) {
            ServiceTable serviceTable = serviceRepository.findById(userUpdateRequest.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Service not found"));
            user.setServiceTable(serviceTable);
        } else {
            user.setServiceTable(null); // Set service to null if no service ID is provided
        }

        user.setUsername(userUpdateRequest.getUsername());
        user.setFirstname(userUpdateRequest.getFirstname());
        user.setLastname(userUpdateRequest.getLastname());

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("user not found"));
    }
}