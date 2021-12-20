package org.aldomanco.plantsensor.models.http_response_weather;

import com.google.gson.annotations.SerializedName;

public class SnowDocument {

    @SerializedName("1h")
    private String forecastSnowAmount;

    public SnowDocument(double forecastSnowAmount) {
        this.forecastSnowAmount = String.valueOf(forecastSnowAmount);
    }

    public Double getForecastSnowAmount() {

        if (forecastSnowAmount==null){
            return 0.0;
        }

        return Double.parseDouble(forecastSnowAmount);
    }

    public void setForecastSnowAmount(double forecastSnowAmount) {
        this.forecastSnowAmount = String.valueOf(forecastSnowAmount);
    }
}
