package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.AuthRequest;
import com.Bhawesh.expense_tracker.dto.AuthResponse;
import com.Bhawesh.expense_tracker.dto.RegisterRequest;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.repository.UserRepository;
import com.Bhawesh.expense_tracker.security.JwtService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//this service is responsible for registering the user and verfying passwords
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthResponse register(RegisterRequest request){
        if(userRepository.findByEmail((request.getEmail())).isPresent()){
            throw new IllegalArgumentException("Email already exists");
        }

            var user = User.builder()
                    .name(request.getUsername())
                    .email(request.getEmail())
                    .role(request.getRole())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .build();
            userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return AuthResponse.builder()
                    .token(jwtToken)
                    .build();
    }
    //Login an existing user
    public AuthResponse authenticate(AuthRequest request){
         authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(
                         request.getEmail(),
                         request.getPassword()
                 )
                 );
         //now if password is correct then
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();

    }
}
