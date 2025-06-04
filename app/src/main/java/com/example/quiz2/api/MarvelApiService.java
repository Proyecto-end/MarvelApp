package com.example.quiz2.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MarvelApiService {
    @GET("characters")
    Call<MarvelResponse> getCharacters(
        @Query("apikey") String apiKey,
        @Query("ts") String timestamp,
        @Query("hash") String hash,
        @Query("limit") int limit,
        @Query("offset") int offset
    );

    @GET("comics")
    Call<MarvelComicResponse> getComics(
        @Query("apikey") String apiKey,
        @Query("ts") String timestamp,
        @Query("hash") String hash,
        @Query("limit") int limit,
        @Query("offset") int offset
    );
} 