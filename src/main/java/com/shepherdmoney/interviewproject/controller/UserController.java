package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserRepository userRepository; // Wire in the user repository
    
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        // Create an user entity with information given in the payload
        User user = new User();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());
        
        // Store it in the database
        userRepository.save(user);

        // Return the id of the user in 200 OK response
        return ResponseEntity.ok(user.getId());
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        // Check if a user with the given ID exists
        if (userRepository.existsById(userId)) {
            // Delete the user
            userRepository.deleteById(userId);
            // Return 200 OK
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            // Return 400 Bad Request if a user with the ID does not exist
            return ResponseEntity.badRequest().body("User with ID " + userId + " does not exist.");
        }
    }
}
