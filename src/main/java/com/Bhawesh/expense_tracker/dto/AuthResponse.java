package com.Bhawesh.expense_tracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token; //this will return a token to the web browser
}
