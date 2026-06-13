package com.chella.userservice.service;

import com.chella.userservice.dto.request.LoginRequest;
import com.chella.userservice.dto.request.RegisterRequest;
import com.chella.userservice.dto.response.LoginResponse;
import com.chella.userservice.dto.response.UserResponse;

public interface UserService {
    
    UserResponse register(RegisterRequest request);
    
    LoginResponse login(LoginRequest request);
    
    UserResponse getUserById(Long id);
    
    UserResponse getProfile();
}
