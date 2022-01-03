package org.aldomanco.plantsensor.models.http_response_plantsensor;

import com.google.gson.annotations.SerializedName;

public class ListPlantSensorValuesDocument {

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("field1")
    private String temperatureAir;

    @SerializedName("field2")
    private String relativeMoistureAir;

    @SerializedName("field3")
    private String relativeMoistureSoil;

    public ListPlantSensorValuesDocument(String createdAt, String temperatureAir, String relativeMoistureAir, String relativeMoistureSoil) {
        this.createdAt = createdAt;
        this.temperatureAir = temperatureAir;
        this.relativeMoistureAir = relativeMoistureAir;
        this.relativeMoistureSoil = relativeMoistureSoil;
    }

    public double getTemperatureAir() {
        return Double.parseDouble(temperatureAir);
    }

    public void setTemperatureAir(double temperatureAir) {
        this.temperatureAir = String.valueOf(temperatureAir);
    }

    public double getRelativeMoistureAir() {
        return Double.parseDouble(relativeMoistureAir);
    }

    public void setRelativeMoistureAir(double relativeMoistureAir) {
        this.relativeMoistureAir = String.valueOf(relativeMoistureAir);
    }

    public double getRelativeMoistureSoil() {
        return Double.parseDouble(relativeMoistureSoil);
    }

    public void setRelativeMoistureSoil(String relativeMoistureSoil) {
        this.relativeMoistureSoil = String.valueOf(relativeMoistureSoil);
    }
}
