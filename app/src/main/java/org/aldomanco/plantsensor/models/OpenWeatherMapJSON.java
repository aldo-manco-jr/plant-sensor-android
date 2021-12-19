package org.aldomanco.plantsensor.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpenWeatherMapJSON {

    //@SerializedName("weather")
    //private WeatherStateDocument sectionWeatherState;

    @SerializedName("main")
    private MixedWeatherDataDocument sectionMixedWeatherData;

    @SerializedName("wind")
    private WindDocument sectionWind;

    //@SerializedName("rain")
    //private PrecipitationDocument sectionPrecipitation;

    public OpenWeatherMapJSON(WeatherStateDocument sectionWeatherState, MixedWeatherDataDocument sectionMixedWeatherData, WindDocument sectionWind, PrecipitationDocument sectionPrecipitation) {
      //  this.sectionWeatherState = sectionWeatherState;
        this.sectionMixedWeatherData = sectionMixedWeatherData;
        this.sectionWind = sectionWind;
        //this.sectionPrecipitation = sectionPrecipitation;
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

    /*public PrecipitationDocument getSectionPrecipitation() {
        return sectionPrecipitation;
    }

    public void setSectionPrecipitation(PrecipitationDocument sectionPrecipitation) {
        this.sectionPrecipitation = sectionPrecipitation;
    }*/
}
