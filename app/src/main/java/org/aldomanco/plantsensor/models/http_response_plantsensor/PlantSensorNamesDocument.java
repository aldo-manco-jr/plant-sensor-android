package org.aldomanco.plantsensor.models.http_response_plantsensor;

import com.google.gson.annotations.SerializedName;

public class PlantSensorNamesDocument {

    @SerializedName("field1")
    private String nameSensorTemperatureAir;

    @SerializedName("field2")
    private String nameSensorRelativeMoistureAir;

    public PlantSensorNamesDocument(String nameSensorTemperatureAir, String nameSensorRelativeMoistureAir) {
        this.nameSensorTemperatureAir = nameSensorTemperatureAir;
        this.nameSensorRelativeMoistureAir = nameSensorRelativeMoistureAir;
    }

    public String getNameSensorTemperatureAir() {
        return nameSensorTemperatureAir;
    }

    public void setNameSensorTemperatureAir(String nameSensorTemperatureAir) {
        this.nameSensorTemperatureAir = nameSensorTemperatureAir;
    }

    public String getNameSensorRelativeMoistureAir() {
        return nameSensorRelativeMoistureAir;
    }

    public void setNameSensorRelativeMoistureAir(String nameSensorRelativeMoistureAir) {
        this.nameSensorRelativeMoistureAir = nameSensorRelativeMoistureAir;
    }
}
