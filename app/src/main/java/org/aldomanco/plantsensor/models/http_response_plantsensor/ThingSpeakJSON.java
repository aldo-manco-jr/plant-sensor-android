package org.aldomanco.plantsensor.models.http_response_plantsensor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ThingSpeakJSON {

    @SerializedName("channel")
    private PlantSensorNamesDocument listPlantSensorName;

    @SerializedName("feeds")
    private List<ListPlantSensorValuesDocument> listPlantSensorValues;

    public ThingSpeakJSON(PlantSensorNamesDocument listPlantSensorName, List<ListPlantSensorValuesDocument> listPlantSensorValues) {
        this.listPlantSensorName = listPlantSensorName;
        this.listPlantSensorValues = listPlantSensorValues;
    }

    public PlantSensorNamesDocument getListPlantSensorName() {
        return listPlantSensorName;
    }

    public void setListPlantSensorName(PlantSensorNamesDocument listPlantSensorName) {
        this.listPlantSensorName = listPlantSensorName;
    }

    public List<ListPlantSensorValuesDocument> getListPlantSensorValues() {
        return listPlantSensorValues;
    }

    public void setListPlantSensorValues(List<ListPlantSensorValuesDocument> listPlantSensorValues) {
        this.listPlantSensorValues = listPlantSensorValues;
    }
}
