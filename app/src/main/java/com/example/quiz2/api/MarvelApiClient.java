package com.example.quiz2.api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MarvelApiClient {
    private static MarvelApiClient instance;
    private final MarvelApiService apiService;

    private MarvelApiClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        apiService = retrofit.create(MarvelApiService.class);
    }

    public static synchronized MarvelApiClient getInstance() {
        if (instance == null) {
            instance = new MarvelApiClient();
        }
        return instance;
    }

    public MarvelApiService getApiService() {
        return apiService;
    }

    public static String generateHash(String timestamp) {
        try {
            String input = timestamp + ApiConfig.PRIVATE_KEY + ApiConfig.PUBLIC_KEY;
            android.util.Log.d("API", "Input para hash: " + input);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String hash = hexString.toString();
            android.util.Log.d("API", "Hash generado: " + hash);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            android.util.Log.e("API", "Error generando hash: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
} 