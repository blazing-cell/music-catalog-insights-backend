package org.example.musiccataloginsights.service;

import org.example.musiccataloginsights.entity.User;
import org.example.musiccataloginsights.repository.PasswordResetTokenRepository;
import org.example.musiccataloginsights.repository.UserRepository;
import org.example.musiccataloginsights.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private UserService userService;



    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void shouldCreateUserSuccessfully(){

        User user = new User();

        user.setEmail("test@gmail.com");
        user.setUsername("test");
        user.setPassword("123456");


        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());


        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.empty());


        when(passwordEncoder.encode("123456"))
                .thenReturn("encodedPassword");


        when(userRepository.save(user))
                .thenReturn(user);



        User savedUser =
                userService.createUser(user);



        assertNotNull(savedUser);

        assertEquals(
                "encodedPassword",
                savedUser.getPassword()
        );


        verify(userRepository)
                .findByEmail("test@gmail.com");


        verify(passwordEncoder)
                .encode("123456");


        verify(userRepository)
                .save(user);

    }



    @Test
    void shouldThrowExceptionWhenEmailExists(){

        User user = new User();

        user.setEmail("test@gmail.com");


        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));


        assertThrows(
                RuntimeException.class,
                () -> userService.createUser(user)
        );

    }

}