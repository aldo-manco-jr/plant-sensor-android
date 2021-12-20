package org.aldomanco.plantsensor.models.http_response_weather;

import com.google.gson.annotations.SerializedName;

public class PrecipitationDocument {

    @SerializedName("1h")
    private String forecastPrecipitationAmount;

    public PrecipitationDocument(double forecastPrecipitationAmount) {
        this.forecastPrecipitationAmount = String.valueOf(forecastPrecipitationAmount);
    }

    public Double getForecastPrecipitationAmount() {

        if (forecastPrecipitationAmount==null){
            return 0.0;
        }

        return Double.parseDouble(forecastPrecipitationAmount);
    }

    public void setForecastPrecipitationAmount(double forecastPrecipitationAmount) {
        this.forecastPrecipitationAmount = String.valueOf(forecastPrecipitationAmount);
    }
}
