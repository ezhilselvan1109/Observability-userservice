package com.chella.userservice.service.impl;

import com.chella.userservice.dto.request.LoginRequest;
import com.chella.userservice.dto.request.RegisterRequest;
import com.chella.userservice.dto.response.LoginResponse;
import com.chella.userservice.dto.response.UserResponse;
import com.chella.userservice.entity.User;
import com.chella.userservice.exception.DuplicateEmailException;
import com.chella.userservice.exception.DuplicateUsernameException;
import com.chella.userservice.exception.InvalidCredentialsException;
import com.chella.userservice.exception.UserNotFoundException;
import com.chella.userservice.mapper.UserMapper;
import com.chella.userservice.repository.UserRepository;
import com.chella.userservice.security.CustomUserDetails;
import com.chella.userservice.security.JwtUtil;
import com.chella.userservice.service.UserService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final MeterRegistry meterRegistry;
    private final ObservationRegistry observationRegistry;

    @Override
    public UserResponse register(RegisterRequest request) {
        return Observation.createNotStarted("user.registration", observationRegistry)
            .observe(() -> {
                if (userRepository.existsByEmail(request.getEmail())) {
                    throw new DuplicateEmailException("Email is already registered");
                }
                if (userRepository.existsByUsername(request.getUsername())) {
                    throw new DuplicateUsernameException("Username is already taken");
                }

                User user = userMapper.toEntity(request);
                user.setPassword(passwordEncoder.encode(request.getPassword()));

                User savedUser = userRepository.save(user);
                meterRegistry.counter("users_registered_total").increment();
                return userMapper.toResponse(savedUser);
            });
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
                
        String jwtToken = jwtUtil.generateToken(new CustomUserDetails(user));
        return new LoginResponse(jwtToken);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User profile not found"));
        return userMapper.toResponse(user);
    }
}
