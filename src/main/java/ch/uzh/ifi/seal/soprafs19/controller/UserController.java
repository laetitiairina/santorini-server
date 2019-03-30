package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }

    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        return this.service.createUser(newUser);
    }

    @PostMapping("/users/login")
    ResponseEntity loginUser(@RequestBody User loginUser) {

        // TODO: login user

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/users/logout")
    ResponseEntity logoutUser(@RequestBody User logoutUser) {

        // TODO: logout user

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users/{id}")
    ResponseEntity getUser(@PathVariable Long id) {

        // TODO: get user by id

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/users/{id}")
    ResponseEntity updateUser(@PathVariable Long id) {

        // TODO: update user by id

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
