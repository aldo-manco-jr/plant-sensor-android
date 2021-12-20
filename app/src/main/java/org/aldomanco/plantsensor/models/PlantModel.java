package org.aldomanco.plantsensor.models;

public class PlantModel {

    private int plantId;
    private String plantName;
    private String plantType;
    private String plantLocationCity;
    private String plantLocationCountry;
    private String plantOwnerUsername;

    private double relativeMoistureSoil;
    private double relativeMoistureAir;
    private double temperatureSoil;
    private double temperatureAir;
    private double lightIntensity;

    private String forecastWeatherState;
    private double forecastPrecipitationAmount;
    private double forecastHumidityAir;
    private double forecastTemperatureAir;
    private double forecastWindSpeed;
    private double forecastSnowAmount;
    private double forecastPressureAir;

    public PlantModel(int plantId, String plantName, String plantType, String plantLocationCity, String plantLocationCountry, String plantOwnerUsername) {
        this.plantId = plantId;
        this.plantName = plantName;
        this.plantType = plantType;
        this.plantLocationCity = plantLocationCity;
        this.plantLocationCountry = plantLocationCountry;
        this.plantOwnerUsername = plantOwnerUsername;
    }

    public PlantModel(int plantId, String plantName, String plantType, String plantLocationCity, String plantLocationCountry, String plantOwnerUsername, double relativeMoistureSoil, double relativeMoistureAir, double temperatureSoil, double temperatureAir, double lightIntensity) {
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

    public PlantModel(int plantId, String plantName, String plantType, String plantLocationCity, String plantLocationCountry, String plantOwnerUsername, double relativeMoistureSoil, double relativeMoistureAir, double temperatureSoil, double temperatureAir, double lightIntensity, String forecastWeatherState, double forecastPrecipitationAmount, double forecastHumidityAir, double forecastTemperatureAir, double forecastWindSpeed, double forecastSnowAmount, double forecastPressureAir) {
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
        this.forecastWeatherState = forecastWeatherState;
        this.forecastPrecipitationAmount = forecastPrecipitationAmount;
        this.forecastHumidityAir = forecastHumidityAir;
        this.forecastTemperatureAir = forecastTemperatureAir;
        this.forecastWindSpeed = forecastWindSpeed;
        this.forecastSnowAmount = forecastSnowAmount;
        this.forecastPressureAir = forecastPressureAir;
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

    public double getRelativeMoistureSoil() {
        return relativeMoistureSoil;
    }

    public void setRelativeMoistureSoil(double relativeMoistureSoil) {
        this.relativeMoistureSoil = relativeMoistureSoil;
    }

    public double getRelativeMoistureAir() {
        return relativeMoistureAir;
    }

    public void setRelativeMoistureAir(double relativeMoistureAir) {
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

    public double getLightIntensity() {
        return lightIntensity;
    }

    public void setLightIntensity(double lightIntensity) {
        this.lightIntensity = lightIntensity;
    }

    public String getForecastWeatherState() {
        return forecastWeatherState;
    }

    public void setForecastWeatherState(String forecastWeatherState) {
        this.forecastWeatherState = forecastWeatherState;
    }

    public double getForecastPrecipitationAmount() {
        return forecastPrecipitationAmount;
    }

    public void setForecastPrecipitationAmount(double forecastPrecipitationAmount) {
        this.forecastPrecipitationAmount = forecastPrecipitationAmount;
    }

    public double getForecastHumidityAir() {
        return forecastHumidityAir;
    }

    public void setForecastHumidityAir(double forecastHumidityAir) {
        this.forecastHumidityAir = forecastHumidityAir;
    }

    public double getForecastTemperatureAir() {
        return forecastTemperatureAir;
    }

    public void setForecastTemperatureAir(double forecastTemperatureAir) {
        this.forecastTemperatureAir = forecastTemperatureAir;
    }

    public double getForecastWindSpeed() {
        return forecastWindSpeed;
    }

    public void setForecastWindSpeed(double forecastWindSpeed) {
        this.forecastWindSpeed = forecastWindSpeed;
    }

    public double getForecastPressureAir() {
        return forecastPressureAir;
    }

    public void setForecastPressureAir(double forecastPressureAir) {
        this.forecastPressureAir = forecastPressureAir;
    }

    public double getForecastSnowAmount() {
        return forecastSnowAmount;
    }

    public void setForecastSnowAmount(double forecastSnowAmount) {
        this.forecastSnowAmount = forecastSnowAmount;
    }
}
