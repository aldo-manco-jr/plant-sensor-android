package org.aldomanco.plantsensor.models;

import com.google.gson.annotations.SerializedName;

public class PrecipitationDocument {

    @SerializedName("1h")
    private String forecastPrecipitationAmount;

    public PrecipitationDocument(double forecastPrecipitationAmount) {
        this.forecastPrecipitationAmount = String.valueOf(forecastPrecipitationAmount);
    }

    public double getForecastPrecipitationAmount() {
        return Double.parseDouble(forecastPrecipitationAmount);
    }

    public void setForecastPrecipitationAmount(double forecastPrecipitationAmount) {
        this.forecastPrecipitationAmount = String.valueOf(forecastPrecipitationAmount);
    }
}
