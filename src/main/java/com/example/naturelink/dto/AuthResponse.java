package com.example.naturelink.dto;

import lombok.*;
import lombok.Data;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AuthResponse {
    private String token ;
    private String message;

    // Constructor for token-based response (USER registration)
    public AuthResponse(String token) {
        this.token = token;
    }

    // Constructor for message-based response (pending registration)
    public AuthResponse(String message, boolean isPending) {
        this.message = message;
    }
}
