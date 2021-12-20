package org.aldomanco.plantsensor.models.http_response_weather;

import com.google.gson.annotations.SerializedName;

public class WeatherStateDocument {

    @SerializedName("main")
    private String weatherStateTitle;

    @SerializedName("description")
    private String weatherStateDescription;

    public WeatherStateDocument(String weatherStateTitle, String weatherStateDescription) {
        this.weatherStateTitle = weatherStateTitle;
        this.weatherStateDescription = weatherStateDescription;
    }

    public String getWeatherStateTitle() {
        return weatherStateTitle;
    }

    public void setWeatherStateTitle(String weatherStateTitle) {
        this.weatherStateTitle = weatherStateTitle;
    }

    public String getWeatherStateDescription() {
        return weatherStateDescription;
    }

    public void setWeatherStateDescription(String weatherStateDescription) {
        this.weatherStateDescription = weatherStateDescription;
    }
}
