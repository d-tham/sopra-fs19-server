package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import ch.uzh.ifi.seal.soprafs19.exceptions.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    @ExceptionHandler({UserAlreadyExistsException.class})
    User createUser(@RequestBody User newUser) {
        if (this.service.getUserByUsername(newUser.getUsername()) != null) {
            throw new UserAlreadyExistsException("User already exists!"); }

        return this.service.createUser(newUser);
    }

    @GetMapping("/users/{userId}")
    @ExceptionHandler({UserAlreadyExistsException.class})
    User single(@PathVariable("userId") long userId) {
        if (this.service.getUserByUserId(userId) == null) {
            throw new UserNotFoundException("User was not found!"); }

        return service.getUserByUserId(userId);}

    @PostMapping("/login")
    @ExceptionHandler({UserNotFoundException.class})
    User loginUser(@RequestBody User tempUser) {
        if (this.service.getUserByUsername(tempUser.getUsername()) == null) {
            throw new UserNotFoundException("User was not found!"); }

        return service.loginUser(tempUser.getUsername(), tempUser.getPassword());
    }

    @CrossOrigin
    @PutMapping("/users/{userId}")
    User updateUser(@PathVariable("userId") long userId, @RequestBody User tempUser) {
        if (!this.service.getUserByUserId(userId).getToken().equals(tempUser.getToken())) {
            throw new RuntimeException("NOT AUTHORIZED");
        }
        return this.service.updateUser(userId, tempUser.getUsername(), tempUser.getBirthDate());
    }

}
