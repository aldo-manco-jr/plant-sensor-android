package org.aldomanco.plantsensor.models.http_response_weather;

import com.google.gson.annotations.SerializedName;

public class OpenWeatherMapJSON {

    //@SerializedName("weather")
    //private WeatherStateDocument sectionWeatherState;

    @SerializedName("main")
    private MixedWeatherDataDocument sectionMixedWeatherData;

    @SerializedName("wind")
    private WindDocument sectionWind;

    @SerializedName("rain")
    private PrecipitationDocument sectionPrecipitation;

    @SerializedName("snow")
    private SnowDocument sectionSnow;

    public OpenWeatherMapJSON(MixedWeatherDataDocument sectionMixedWeatherData, WindDocument sectionWind, PrecipitationDocument sectionPrecipitation, SnowDocument sectionSnow) {
        this.sectionMixedWeatherData = sectionMixedWeatherData;
        this.sectionWind = sectionWind;
        this.sectionPrecipitation = sectionPrecipitation;
        this.sectionSnow = sectionSnow;
    }

    /*public WeatherStateDocument getSectionWeatherState() {
        return sectionWeatherState;
    }

    public void setSectionWeatherState(WeatherStateDocument sectionWeatherState) {
        this.sectionWeatherState = sectionWeatherState;
    }*/

    public MixedWeatherDataDocument getSectionMixedWeatherData() {
        return sectionMixedWeatherData;
    }

    public void setSectionMixedWeatherData(MixedWeatherDataDocument sectionMixedWeatherData) {
        this.sectionMixedWeatherData = sectionMixedWeatherData;
    }

    public WindDocument getSectionWind() {
        return sectionWind;
    }

    public void setSectionWind(WindDocument sectionWind) {
        this.sectionWind = sectionWind;
    }

    public double getSectionPrecipitation() {

        if (sectionPrecipitation==null){
            return 0.0;
        }

        return sectionPrecipitation.getForecastPrecipitationAmount();
    }

    public void setSectionPrecipitation(PrecipitationDocument sectionPrecipitation) {
        this.sectionPrecipitation = sectionPrecipitation;
    }

    public double getSectionSnow() {

        if (sectionSnow==null){
            return 0.0;
        }

        return sectionSnow.getForecastSnowAmount();
    }

    public void setSectionSnow(SnowDocument sectionSnow) {
        this.sectionSnow = sectionSnow;
    }
}