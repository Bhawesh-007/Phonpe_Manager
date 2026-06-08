package com.Bhawesh.expense_tracker.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token; //this will return a token to the web browser
}
