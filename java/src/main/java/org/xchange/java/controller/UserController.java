package org.xchange.java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xchange.java.service.UserService;
import org.xchange.java.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour gérer les opérations liées aux utilisateurs.
 * Définit les endpoints pour les opérations CRUD et l'authentification des
 * utilisateurs.
 * 
 * Chemin de base: /api/users
 * 
 * @author Celil
 */
@RestController
@RequestMapping("/api/users") // Définit le chemin de base pour tous les endpoints dans ce contrôleur
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/update")
    public User updateUser(User user) {
        return userService.updateUser(user.getId(), user);
    }

    @PostMapping("/delete")
    public void deleteUser(User user) {
        userService.deleteUser(user.getId());
    }

}