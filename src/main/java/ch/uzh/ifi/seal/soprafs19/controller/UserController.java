package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    /*
    UserController(UserService service) {
        this.service = service;
    }
    */

    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }

    @PostMapping("/users")
    ResponseEntity createUser(@RequestBody User newUser) {

        // Check if user already exists
        if (service.checkUserExists(newUser.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists!");
        }

        // Create user
        service.createUser(newUser);

        // Send response 201
        return ResponseEntity.created(URI.create("/users/"+newUser.getId())).body(URI.create("/users/"+newUser.getId()));
    }

    @PostMapping("/users/login")
    ResponseEntity loginUser(@RequestBody User loginUser) {

        // Login user
        User user = service.loginUser(loginUser.getUsername(),loginUser.getPassword());

        if (user != null) {
            // Send response 200
            return ResponseEntity.ok(user);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username or Password is wrong!");
    }

    @PostMapping("/users/logout")
    ResponseEntity logoutUser(@RequestHeader("Token") String token, @RequestBody User logoutUser) {

        // Check if user who sent request is authenticated
        if (!service.checkUserAuthentication(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthenticated request!");
        }

        // Logout user
        if (service.logoutUser(token)) {
            // Send response 200
            return ResponseEntity.ok("Logout successful!");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Something went wrong during logout!");
    }

    @GetMapping("/users/{id}")
    ResponseEntity getUser(@RequestHeader("Token") String token, @PathVariable Long id) {

        // Check if user who sent request is authenticated
        if (!service.checkUserAuthentication(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthenticated request!");
        }

        // Get user by id
        Optional<User> user = service.getUserById(id);

        // Check if user exists
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User was not found!");
        }

        // Send response 200
        return ResponseEntity.ok(user.get());
    }

    @PutMapping("/users/{id}")
    ResponseEntity updateUser(@RequestHeader("Token") String token, @RequestBody User updatedUser, @PathVariable Long id) {

        // Check if user who sent request is authenticated
        if (!service.checkUserAuthentication(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthenticated request!");
        }

        // Get user by id
        Optional<User> user = service.getUserById(id);

        // Check if user exists
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User was not found!");
        }

        // Check if user who sent request is same user who is going to be updated
        if (!user.get().getToken().equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthenticated request!");
        }

        // Update user data
        service.updateUser(user.get(),updatedUser);

        // Send response 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
