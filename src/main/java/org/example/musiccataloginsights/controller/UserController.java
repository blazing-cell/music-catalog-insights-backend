package org.example.musiccataloginsights.controller;

import org.example.musiccataloginsights.dto.ForgotPasswordRequest;
import org.example.musiccataloginsights.dto.LoginRequest;
import org.example.musiccataloginsights.dto.LoginResponse;
import org.example.musiccataloginsights.dto.ResetPasswordRequest;
import org.example.musiccataloginsights.entity.User;
import org.example.musiccataloginsights.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {


    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }



    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {

        try {

            User createdUser =
                    userService.createUser(user);

            return ResponseEntity.ok(createdUser);


        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        }

    }




    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUsers();

    }





    @GetMapping("/{id}")
    public User getUserById(
            @PathVariable Long id
    ) {

        return userService.getUserById(id);

    }





    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {


        try {

            LoginResponse response =
                    userService.login(request);


            return ResponseEntity.ok(response);


        } catch (RuntimeException e) {


            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        }

    }





    @PutMapping("/update/{email}")
    public User updateUser(
            @PathVariable String email,
            @RequestBody User user
    ) {

        return userService.updateUser(email, user);

    }






    @GetMapping("/profile")
    public User getProfile(
            Authentication authentication
    ) {


        String email =
                authentication.getName();


        return userService.getUserByEmail(email);

    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {

        try {

            String token =
                    userService.forgotPassword(
                            request.getEmail()
                    );


            return ResponseEntity.ok(
                    "Reset token: " + token
            );


        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        }

    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {

        try {

            userService.resetPassword(
                    request.getToken(),
                    request.getPassword()
            );


            return ResponseEntity.ok(
                    "Password reset successfully"
            );


        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        }

    }


}