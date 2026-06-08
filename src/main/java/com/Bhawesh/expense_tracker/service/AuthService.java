package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.AuthRequest;
import com.Bhawesh.expense_tracker.repository.UserRepository;
import com.Bhawesh.expense_tracker.security.JwtService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//this service is responsible for registering the user and verfying passwords
public class AuthService {
    private final UserRepository userRepository;;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public String register(AuthRequest request){

    }
}
