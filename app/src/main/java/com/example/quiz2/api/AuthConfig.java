package com.example.quiz2.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import android.util.Log;

public class AuthConfig {
    private static final String TAG = "AuthConfig";
    // URL para dispositivo físico
    public static final String BASE_URL = "http://192.168.1.59:3000/api/";
    
    private static Retrofit retrofit = null;
    private static AuthService authService = null;

    public static AuthService getAuthService() {
        if (authService == null) {
            Log.d(TAG, "Creando nuevo servicio de autenticación");
            authService = getRetrofitInstance().create(AuthService.class);
        }
        return authService;
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Log.d(TAG, "Inicializando Retrofit con URL: " + BASE_URL);
            
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d(TAG, "OkHttp: " + message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
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
} 