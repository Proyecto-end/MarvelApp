package com.example.quiz2.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MarvelComicResponse {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("results")
        private List<MarvelResponse.Comic> results;

        public List<MarvelResponse.Comic> getResults() {
            return results;
        }
    }
} 