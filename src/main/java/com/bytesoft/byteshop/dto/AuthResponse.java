package com.bytesoft.byteshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tipo = "Bearer";

    public AuthResponse(String token) {
        this.token = token;
    }
}