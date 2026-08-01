package org.example.musiccataloginsights.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginResponse {

    private String message;
    private Long userId;
    private String username;
    private String email;
    private String token;


    public LoginResponse(String message, Long userId, String username, String email,String token) {
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.token= token;
    }


}