package com.example.quiz2.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiConfig {
    public static final String BASE_URL = "https://gateway.marvel.com/v1/public/";
    public static final String PUBLIC_KEY = "e7bcf8f2f77aa7f9544003e67bc6a38f";
    public static final String PRIVATE_KEY = "82b9b70c068884e746fcbdc36f46c5ca4176c99c";
    
    private static Retrofit retrofit = null;
    private static AuthService authService = null;

    public static AuthService getAuthService() {
        if (authService == null) {
            authService = getRetrofitInstance().create(AuthService.class);
        }
        return authService;
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }

    public static String getPublicKey() {
        return PUBLIC_KEY;
    }

    public static String getPrivateKey() {
        return PRIVATE_KEY;
    }
} 