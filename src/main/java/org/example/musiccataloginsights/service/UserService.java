package org.example.musiccataloginsights.service;

import org.example.musiccataloginsights.dto.LoginRequest;
import org.example.musiccataloginsights.dto.LoginResponse;
import org.example.musiccataloginsights.entity.PasswordResetToken;
import org.example.musiccataloginsights.entity.User;
import org.example.musiccataloginsights.repository.PasswordResetTokenRepository;
import org.example.musiccataloginsights.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.musiccataloginsights.security.JwtService;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService,PasswordResetTokenRepository passwordResetTokenRepository)
    {
        this.userRepository = userRepository;
        this.jwtService=jwtService;
        this.passwordResetTokenRepository=passwordResetTokenRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public User updateUser(String email, User updatedUser) {

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());

        return userRepository.save(existingUser);
    }
    public LoginResponse login(LoginRequest request) {

        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // 2. Check the password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                "Login successful",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token
        );
    }
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

    }
    public String forgotPassword(String email) {


        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );



        String token =
                UUID.randomUUID()
                        .toString();



        PasswordResetToken resetToken =
                new PasswordResetToken();


        resetToken.setToken(token);


        resetToken.setExpiryDate(
                LocalDateTime.now()
                        .plusMinutes(15)
        );


        resetToken.setUser(user);



        passwordResetTokenRepository.save(
                resetToken
        );


        return token;

    }
    public void resetPassword(
            String token,
            String newPassword
    ) {


        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid token"
                                )
                        );



        if(
                resetToken.getExpiryDate()
                        .isBefore(LocalDateTime.now())
        ){

            throw new RuntimeException(
                    "Token expired"
            );

        }



        User user =
                resetToken.getUser();



        user.setPassword(
                passwordEncoder.encode(newPassword)
        );



        userRepository.save(user);



        passwordResetTokenRepository
                .delete(resetToken);

    }
}