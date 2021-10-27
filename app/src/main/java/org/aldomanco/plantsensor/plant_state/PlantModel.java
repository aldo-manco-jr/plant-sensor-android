package org.aldomanco.plantsensor.plant_state;

public class PlantModel {

    private int plantId;
    private String plantName;
    private String plantType;
    private String plantLocationCity;
    private String plantLocationCountry;
    private String plantOwnerUsername;

    private int relativeMoistureSoil;
    private int relativeMoistureAir;
    private double temperatureSoil;
    private double temperatureAir;
    private int lightIntensity;

    public PlantModel(int plantId, String plantName, String plantType, String plantLocationCity, String plantLocationCountry, String plantOwnerUsername, int relativeMoistureSoil, int relativeMoistureAir, double temperatureSoil, double temperatureAir, int lightIntensity) {
        this.plantId = plantId;
        this.plantName = plantName;
        this.plantType = plantType;
        this.plantLocationCity = plantLocationCity;
        this.plantLocationCountry = plantLocationCountry;
        this.plantOwnerUsername = plantOwnerUsername;
        this.relativeMoistureSoil = relativeMoistureSoil;
        this.relativeMoistureAir = relativeMoistureAir;
        this.temperatureSoil = temperatureSoil;
        this.temperatureAir = temperatureAir;
        this.lightIntensity = lightIntensity;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getPlantLocationCity() {
        return plantLocationCity;
    }

    public void setPlantLocationCity(String plantLocationCity) {
        this.plantLocationCity = plantLocationCity;
    }

    public String getPlantLocationCountry() {
        return plantLocationCountry;
    }

    public void setPlantLocationCountry(String plantLocationCountry) {
        this.plantLocationCountry = plantLocationCountry;
    }

    public String getPlantOwnerUsername() {
        return plantOwnerUsername;
    }

    public void setPlantOwnerUsername(String plantOwnerUsername) {
        this.plantOwnerUsername = plantOwnerUsername;
    }

    public int getRelativeMoistureSoil() {
        return relativeMoistureSoil;
    }

    public void setRelativeMoistureSoil(int relativeMoistureSoil) {
        this.relativeMoistureSoil = relativeMoistureSoil;
    }

    public int getRelativeMoistureAir() {
        return relativeMoistureAir;
    }

    public void setRelativeMoistureAir(int relativeMoistureAir) {
        this.relativeMoistureAir = relativeMoistureAir;
    }

    public double getTemperatureSoil() {
        return temperatureSoil;
    }

    public void setTemperatureSoil(double temperatureSoil) {
        this.temperatureSoil = temperatureSoil;
    }

    public double getTemperatureAir() {
        return temperatureAir;
    }

    public void setTemperatureAir(double temperatureAir) {
        this.temperatureAir = temperatureAir;
    }

    public int getLightIntensity() {
        return lightIntensity;
    }

    public void setLightIntensity(int lightIntensity) {
        this.lightIntensity = lightIntensity;
    }
}
