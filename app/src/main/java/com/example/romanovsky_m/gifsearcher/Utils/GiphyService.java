package com.example.romanovsky_m.gifsearcher.Utils;

import com.example.romanovsky_m.gifsearcher.GsonModels.Giphy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GiphyService {

    @GET("trending")
    Call<Giphy> getTrending(@Query("api_key") String apiKey);

    @GET("search")
    Call<Giphy> getSearch(@Query("q") String q,
                          @Query("api_key") String apiKey,
                          @Query("lang") String lang);

}
