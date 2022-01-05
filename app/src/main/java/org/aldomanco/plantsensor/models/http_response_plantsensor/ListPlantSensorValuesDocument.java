package org.aldomanco.plantsensor.models.http_response_plantsensor;

import android.content.Intent;

import androidx.core.app.ActivityCompat;

import com.google.gson.annotations.SerializedName;

import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.authentication.ConnectionProblemsActivity;

public class ListPlantSensorValuesDocument {

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("field1")
    private String temperatureAir;

    @SerializedName("field2")
    private String relativeMoistureAir;

    @SerializedName("field3")
    private String relativeMoistureSoil;

    @SerializedName("field5")
    private String lightIntensity;

    private Intent intentFirstActivity;

    public ListPlantSensorValuesDocument(String createdAt, String temperatureAir, String relativeMoistureAir, String relativeMoistureSoil, String lightIntensity) {
        this.createdAt = createdAt;
        this.temperatureAir = temperatureAir;
        this.relativeMoistureAir = relativeMoistureAir;
        this.relativeMoistureSoil = relativeMoistureSoil;
        this.lightIntensity = lightIntensity;
    }

    public double getTemperatureAir() {

        if (temperatureAir==null){
            intentFirstActivity = new Intent(LoggedUserActivity.getLoggedUserActivity(), ConnectionProblemsActivity.class);
            intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LoggedUserActivity.getLoggedUserActivity().startActivity(intentFirstActivity);
            ActivityCompat.finishAffinity(LoggedUserActivity.getLoggedUserActivity());
            return 0.0;
        }

        return Double.parseDouble(temperatureAir);
    }

    public void setTemperatureAir(double temperatureAir) {
        this.temperatureAir = String.valueOf(temperatureAir);
    }

    public double getRelativeMoistureAir() {

        if (relativeMoistureAir==null){
            intentFirstActivity = new Intent(LoggedUserActivity.getLoggedUserActivity(), ConnectionProblemsActivity.class);
            intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LoggedUserActivity.getLoggedUserActivity().startActivity(intentFirstActivity);
            ActivityCompat.finishAffinity(LoggedUserActivity.getLoggedUserActivity());
            return 0.0;
        }

        return Double.parseDouble(relativeMoistureAir);
    }

    public void setRelativeMoistureAir(double relativeMoistureAir) {
        this.relativeMoistureAir = String.valueOf(relativeMoistureAir);
    }

    public double getRelativeMoistureSoil() {

        if (relativeMoistureSoil==null){
            intentFirstActivity = new Intent(LoggedUserActivity.getLoggedUserActivity(), ConnectionProblemsActivity.class);
            intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LoggedUserActivity.getLoggedUserActivity().startActivity(intentFirstActivity);
            ActivityCompat.finishAffinity(LoggedUserActivity.getLoggedUserActivity());
            return 0.0;
        }

        return Double.parseDouble(relativeMoistureSoil);
    }

    public void setRelativeMoistureSoil(String relativeMoistureSoil) {
        this.relativeMoistureSoil = String.valueOf(relativeMoistureSoil);
    }

    public double getLightIntensity() {

        if (lightIntensity==null){
            intentFirstActivity = new Intent(LoggedUserActivity.getLoggedUserActivity(), ConnectionProblemsActivity.class);
            intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LoggedUserActivity.getLoggedUserActivity().startActivity(intentFirstActivity);
            ActivityCompat.finishAffinity(LoggedUserActivity.getLoggedUserActivity());
            return 0.0;
        }

        return Double.parseDouble(lightIntensity);
    }

    public void setLightIntensity(String lightIntensity) {
        this.lightIntensity = String.valueOf(lightIntensity);
    }
}