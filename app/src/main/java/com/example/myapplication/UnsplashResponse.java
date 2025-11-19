package com.example.myapplication;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Represents the search result from Unsplash API
public class UnsplashResponse {
    @SerializedName("results")
    private List<UnsplashPhoto> results;

    public List<UnsplashPhoto> getResults() {
        return results;
    }
}

// Represents a single photo object from Unsplash
class UnsplashPhoto {
    @SerializedName("urls")
    private UnsplashUrls urls;

    public UnsplashUrls getUrls() {
        return urls;
    }
}

// Represents the different URL sizes for a photo
class UnsplashUrls {
    @SerializedName("regular")
    private String regular;

    public String getRegular() {
        return regular;
    }
}
