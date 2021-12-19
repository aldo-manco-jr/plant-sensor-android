package org.aldomanco.plantsensor.models;

import com.google.gson.annotations.SerializedName;

public class MixedWeatherDataDocument {

    @SerializedName("temp")
    private String forecastTemperatureAir;

    @SerializedName("pressure")
    private String forecastPressureAir;

    @SerializedName("humidity")
    private String forecastRelativeMoistureAir;

    public MixedWeatherDataDocument(String forecastTemperatureAir, String forecastPressureAir, String forecastRelativeMoistureAir) {
        this.forecastTemperatureAir = forecastTemperatureAir;
        this.forecastPressureAir = forecastPressureAir;
        this.forecastRelativeMoistureAir = forecastRelativeMoistureAir;
    }

    public double getForecastTemperatureAir() {
        return Double.parseDouble(forecastTemperatureAir);
    }

    public void setForecastTemperatureAir(double forecastTemperatureAir) {
        this.forecastTemperatureAir = String.valueOf(forecastTemperatureAir);
    }

    public int getForecastPressureAir() {
        return Integer.parseInt(forecastPressureAir);
    }

    public void setForecastPressureAir(int forecastPressureAir) {
        this.forecastPressureAir = String.valueOf(forecastPressureAir);
    }

    public int getForecastRelativeMoistureAir() {
        return Integer.parseInt(forecastRelativeMoistureAir);
    }

    public void setForecastRelativeMoistureAir(int forecastRelativeMoistureAir) {
        this.forecastRelativeMoistureAir = String.valueOf(forecastRelativeMoistureAir);
    }
}
