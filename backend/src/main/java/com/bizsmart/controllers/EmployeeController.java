package com.bizsmart.controllers;

import com.bizsmart.models.ERole;
import com.bizsmart.models.Role;
import com.bizsmart.models.User;
import com.bizsmart.payload.request.SignupRequest;
import com.bizsmart.payload.response.MessageResponse;
import com.bizsmart.repositories.RoleRepository;
import com.bizsmart.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping
    @PreAuthorize("hasRole('BUSINESS_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllEmployees() {
        List<User> employees = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName() == ERole.ROLE_EMPLOYEE || r.getName() == ERole.ROLE_STAFF))
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @PreAuthorize("hasRole('BUSINESS_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<?> addEmployee(@Valid @RequestBody SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User employee = new User(
                request.getUsername(),
                request.getEmail(),
                encoder.encode(request.getPassword() != null ? request.getPassword() : "staff123"),
                request.getFullName()
        );

        Role employeeRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_EMPLOYEE)));

        employee.setRoles(new HashSet<>(Set.of(employeeRole)));
        User saved = userRepository.save(employee);

        return ResponseEntity.ok(saved);
    }
}
