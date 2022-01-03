package org.aldomanco.plantsensor.watering_state;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.authentication.ConnectionProblemsActivity;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.home.PlantSensorApp;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.models.http_response_plantsensor.ThingSpeakJSON;
import org.aldomanco.plantsensor.models.http_response_weather.OpenWeatherMapJSON;
import org.aldomanco.plantsensor.plant_state.PlantStateFragment;
import org.aldomanco.plantsensor.services.ServiceGenerator;
import org.aldomanco.plantsensor.services.StateServices;
import org.aldomanco.plantsensor.utils.SubsystemEnumeration;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutomaticWateringService extends Service {

    private static StateServices stateServices;

    private double temperatureAir;
    private double temperatureSoil;
    private double relativeMoistureAir;
    private double relativeMoistureSoil;
    private double lightIntensity;

    private double forecastTemperatureAir;
    private double forecastRelativeMoistureAir;
    private double forecastPrecipitationAmount;
    private double forecastSnowAmount;
    private double forecastWindSpeed;
    private double forecastPressureAir;

    private Handler handler;
    private Runnable runnableCode;

    private SharedPreferences sharedPreferences;
    private String city;

    public AutomaticWateringService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();

        handler = new Handler();

        AutomaticWateringService automaticWateringService = this;

        runnableCode = new Runnable() {
            @Override
            public void run() {
                getThingSpeakData(automaticWateringService, false);
                handler.postDelayed(this, 60000);
            }
        };

        sharedPreferences = LoggedUserActivity.getLoggedUserActivity().getSharedPreferences("plant_data", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city", null);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(runnableCode);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);

        getThingSpeakData(this, true);

        handler.post(runnableCode);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        handler.removeCallbacks(runnableCode);
    }

    private void getThingSpeakData(AutomaticWateringService automaticWateringService, boolean createNotification) {

        Call<ThingSpeakJSON> httpRequest = getStateServices().getPlantStateData();

        httpRequest.enqueue(new Callback<ThingSpeakJSON>() {
            @Override
            public void onResponse(Call<ThingSpeakJSON> call, final Response<ThingSpeakJSON> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    ThingSpeakJSON thingSpeakJSON = response.body();

                    temperatureAir = thingSpeakJSON.getListPlantSensorValues().get(thingSpeakJSON.getListPlantSensorValues().size()-1).getTemperatureAir();
                    relativeMoistureAir = thingSpeakJSON.getListPlantSensorValues().get(thingSpeakJSON.getListPlantSensorValues().size()-1).getRelativeMoistureAir();
                    relativeMoistureSoil = thingSpeakJSON.getListPlantSensorValues().get(thingSpeakJSON.getListPlantSensorValues().size()-1).getRelativeMoistureSoil();

                    if (createNotification){
                        Intent notificationIntent = new Intent(automaticWateringService, LoggedUserActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(automaticWateringService, 0, notificationIntent, 0);

                        Notification notificationAutomaticWateringActive = new NotificationCompat.Builder(automaticWateringService, PlantSensorApp.NOTIFICATION_CHANNEL_ID)
                                .setContentTitle("Automatic Watering is ON")
                                .setContentText("Automatic Watering is ON")
                                .setSmallIcon(R.drawable.watering)
                                .setContentIntent(pendingIntent)
                                .build();

                        startForeground(1, notificationAutomaticWateringActive);
                    }

                    getOpenWeatherMapData(city);

                } else {

                }
            }

            @Override
            public void onFailure(Call<ThingSpeakJSON> call, Throwable t) {
                // errore a livello di rete


            }
        });
    }

    private Notification setContentForegroundNotification(String notificationContent){

        Intent notificationIntent = new Intent(this, LoggedUserActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, PlantSensorApp.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(notificationContent)
                .setContentText(notificationContent)
                .setSmallIcon(R.drawable.watering)
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * This is the method that can be called to update the Notification
     */
    private void updateNotification(String notificationContent) {

        Notification notification = setContentForegroundNotification(notificationContent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private void getOpenWeatherMapData(String locationCity) {

        Call<OpenWeatherMapJSON> httpRequest = getStateServices().getWeatherData(locationCity);

        httpRequest.enqueue(new Callback<OpenWeatherMapJSON>() {
            @Override
            public void onResponse(Call<OpenWeatherMapJSON> call, final Response<OpenWeatherMapJSON> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    OpenWeatherMapJSON openWeatherMapJSON = response.body();

                    forecastPrecipitationAmount = openWeatherMapJSON.getSectionPrecipitation();
                    forecastPressureAir = openWeatherMapJSON.getSectionMixedWeatherData().getForecastPressureAir();
                    forecastRelativeMoistureAir = openWeatherMapJSON.getSectionMixedWeatherData().getForecastRelativeMoistureAir();
                    forecastSnowAmount = openWeatherMapJSON.getSectionSnow();
                    forecastWindSpeed = openWeatherMapJSON.getSectionWind().getForecastWindSpeed();
                    forecastTemperatureAir = openWeatherMapJSON.getSectionMixedWeatherData().getForecastTemperatureAir();

                    if (shouldWater(true)){
                        Toast.makeText(getApplicationContext(), forecastTemperatureAir+" "+temperatureAir+" "+relativeMoistureAir+" "+relativeMoistureSoil, Toast.LENGTH_LONG).show();
                    }

                } else {

                    if (shouldWater(false)){
                        Toast.makeText(getApplicationContext(), temperatureAir+" "+relativeMoistureAir+" "+relativeMoistureSoil, Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<OpenWeatherMapJSON> call, Throwable t) {
                // errore a livello di rete

                if (shouldWater(false)){
                    Toast.makeText(getApplicationContext(), temperatureAir+" "+relativeMoistureAir+" "+relativeMoistureSoil, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static StateServices getStateServices() {

        if (stateServices == null) {
            stateServices = ServiceGenerator.createService(StateServices.class);
        }

        return stateServices;
    }

    public boolean shouldWater(boolean weatherIncluded){

        if (weatherIncluded){

            if (temperatureAir>0 && forecastTemperatureAir>=0){
                return true;
            }else {
                return false;
            }

        }else {

            if (temperatureAir>0){
                return true;
            }else {
                return false;
            }

        }
    }
}