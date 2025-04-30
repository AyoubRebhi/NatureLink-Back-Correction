package com.example.naturelink.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
    private String role;
    private String proofDocument;
}
