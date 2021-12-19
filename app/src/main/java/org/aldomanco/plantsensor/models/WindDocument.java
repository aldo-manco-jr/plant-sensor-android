package org.aldomanco.plantsensor.models;

import com.google.gson.annotations.SerializedName;

public class WindDocument {

    @SerializedName("speed")
    private String forecastWindSpeed;

    public WindDocument(double forecastWindSpeed) {
        this.forecastWindSpeed = String.valueOf(forecastWindSpeed);
    }

    public double getForecastWindSpeed() {
        return Double.parseDouble(forecastWindSpeed);
    }

    public void setForecastWindSpeed(double forecastWindSpeed) {
        this.forecastWindSpeed = String.valueOf(forecastWindSpeed);
    }
}
