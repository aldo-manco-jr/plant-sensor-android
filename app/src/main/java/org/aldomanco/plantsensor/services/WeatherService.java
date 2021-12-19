package org.aldomanco.plantsensor.services;

import com.google.gson.annotations.SerializedName;

import org.aldomanco.plantsensor.models.OpenWeatherMapJSON;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("https://api.openweathermap.org/data/2.5/weather?appid=0db0d31cbbea7111a556d66cace7a1bc&units=metric&lang=it")
    Call<OpenWeatherMapJSON> getWeatherData(
            @Query("q") String locationCity
    );

    @GET("https://api.thingspeak.com/channels.json?api_key=VRQYAUV1M05F4W0U")
    Call<OpenWeatherMapJSON> getPlantStateData(
            @Query("q") String locationCity
    );
}
