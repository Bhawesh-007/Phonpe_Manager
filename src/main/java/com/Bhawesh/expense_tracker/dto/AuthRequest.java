package com.Bhawesh.expense_tracker.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
