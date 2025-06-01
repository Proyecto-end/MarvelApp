package com.example.quiz2.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.example.quiz2.api.LoginRequest;
import com.example.quiz2.api.RegisterRequest;
import com.example.quiz2.api.AuthResponse;

public interface AuthService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
} 