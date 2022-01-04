package org.aldomanco.plantsensor.services;

import org.aldomanco.plantsensor.models.http_response_plantsensor.ThingSpeakJSON;
import org.aldomanco.plantsensor.models.http_response_weather.OpenWeatherMapJSON;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StateServices {

    @GET("https://api.openweathermap.org/data/2.5/weather?appid=0db0d31cbbea7111a556d66cace7a1bc&units=metric&lang=it")
    Call<OpenWeatherMapJSON> getWeatherData(
            @Query("q") String locationCity
    );

    @GET("https://api.thingspeak.com/channels/1611414/feeds.json?api_key=VRQYAUV1M05F4W0U&results=5")
    Call<ThingSpeakJSON> getPlantStateData();

    @GET("https://api.thingspeak.com/update?api_key=CD4DA55EX6PGAKQU")
    Call<Object> setShouldWaterValue(
            @Query("field8") int shouldWater,
            @Query("field1") double temperatureAir,
            @Query("field2") double relativeMoistureAir,
            @Query("field3") double relativeMoistureSoil,
            @Query("field5") double lightIntensity
    );
}
