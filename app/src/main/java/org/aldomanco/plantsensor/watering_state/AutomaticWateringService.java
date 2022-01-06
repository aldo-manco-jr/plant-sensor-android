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
import org.aldomanco.plantsensor.models.Color;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.models.PlantStateModel;
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
    private String plantType;

    private PlantStateModel relativeMoistureSoilState;
    private PlantStateModel relativeMoistureAirState;
    private PlantStateModel temperatureSoilState;
    private PlantStateModel temperatureAirState;
    private PlantStateModel lightIntensityState;

    private PlantStateModel forecastPrecipitationAmountState;
    private PlantStateModel forecastHumidityAirState;
    private PlantStateModel forecastTemperatureAirState;
    private PlantStateModel forecastWindSpeedState;
    private PlantStateModel forecastSnowAmountState;
    private PlantStateModel forecastPressureAirState;

    private boolean isWatering;

    public AutomaticWateringService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        isWatering=false;

        handler = new Handler();

        AutomaticWateringService automaticWateringService = this;

        runnableCode = new Runnable() {
            @Override
            public void run() {
                getThingSpeakData(automaticWateringService, false);
                handler.postDelayed(this, 60000);
            }
        };

        relativeMoistureSoilState = LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureSoil();
        relativeMoistureAirState = LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureAir();
        temperatureSoilState = LoggedUserActivity.getLoggedUserActivity().getTemperatureSoil();
        temperatureAirState = LoggedUserActivity.getLoggedUserActivity().getTemperatureAir();
        lightIntensityState = LoggedUserActivity.getLoggedUserActivity().getLightIntensity();

        forecastPrecipitationAmountState = LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount();
        forecastHumidityAirState = LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir();
        forecastTemperatureAirState = LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir();
        forecastWindSpeedState = LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed();
        forecastSnowAmountState = LoggedUserActivity.getLoggedUserActivity().getForecastSnowAmount();
        forecastPressureAirState = LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir();

        sharedPreferences = LoggedUserActivity.getLoggedUserActivity().getSharedPreferences("plant_data", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city", null);
        plantType = sharedPreferences.getString("plant_type", null);
    }

    @Override
    public void onDestroy() {
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

                    temperatureAir = thingSpeakJSON.getListPlantSensorValues().get(thingSpeakJSON.getListPlantSensorValues().size() - 1).getTemperatureAir();
                    relativeMoistureAir = thingSpeakJSON.getListPlantSensorValues().get(thingSpeakJSON.getListPlantSensorValues().size() - 1).getRelativeMoistureAir();
                    relativeMoistureSoil = thingSpeakJSON.getListPlantSensorValues().get(thingSpeakJSON.getListPlantSensorValues().size() - 1).getRelativeMoistureSoil();
                    temperatureSoil = thingSpeakJSON.getListPlantSensorValues().get(thingSpeakJSON.getListPlantSensorValues().size() - 1).getTemperatureSoil();
                    lightIntensity = thingSpeakJSON.getListPlantSensorValues().get(thingSpeakJSON.getListPlantSensorValues().size() - 1).getLightIntensity();

                    temperatureAirState.setValueState(temperatureAir);
                    relativeMoistureAirState.setValueState(relativeMoistureAir);
                    relativeMoistureSoilState.setValueState(relativeMoistureSoil);
                    temperatureSoilState.setValueState(temperatureSoil);
                    lightIntensityState.setValueState(lightIntensity);

                    if (createNotification) {
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

    private Notification setContentForegroundNotification(String notificationContent) {

        Intent notificationIntent = new Intent(this, LoggedUserActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, PlantSensorApp.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(notificationContent)
                .setContentText(notificationContent)
                .setSmallIcon(R.drawable.watering)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void setShouldWaterValue(int shouldWater, double temperatureAir, double relativeMoistureAir, double temperatureSoil, double relativeMoistureSoil, double lightIntensity) {

        Call<Object> httpRequest = getStateServices().setShouldWaterValue(shouldWater, temperatureAir, relativeMoistureAir, relativeMoistureSoil, lightIntensity);

        httpRequest.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, final Response<Object> response) {

                if (!response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";



                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                // errore a livello di rete


            }
        });
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

                    forecastHumidityAirState.setValueState(forecastRelativeMoistureAir);
                    forecastPrecipitationAmountState.setValueState(forecastPrecipitationAmount);
                    forecastSnowAmountState.setValueState(forecastSnowAmount);
                    forecastPressureAirState.setValueState(forecastPressureAir);
                    forecastWindSpeedState.setValueState(forecastWindSpeed);
                    forecastTemperatureAirState.setValueState(forecastTemperatureAir);

                    if (shouldWater(true)) {
                        setShouldWaterValue(1, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                        isWatering=true;
                        Toast.makeText(getApplicationContext(), forecastTemperatureAir + " " + temperatureAir + " " + relativeMoistureAir + " " + relativeMoistureSoil, Toast.LENGTH_LONG).show();
                    }else if (!shouldWater(true) && isWatering){
                        setShouldWaterValue(0, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                        isWatering=false;
                    }

                } else {

                    if (shouldWater(false)) {
                        setShouldWaterValue(1, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                        isWatering=true;
                        Toast.makeText(getApplicationContext(), temperatureAir + " " + relativeMoistureAir + " " + relativeMoistureSoil, Toast.LENGTH_LONG).show();
                    }else if (!shouldWater(false) && isWatering){
                        setShouldWaterValue(0, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                        isWatering=false;
                    }

                }
            }

            @Override
            public void onFailure(Call<OpenWeatherMapJSON> call, Throwable t) {
                // errore a livello di rete

                if (shouldWater(false)) {
                    setShouldWaterValue(1, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                    isWatering=true;
                    Toast.makeText(getApplicationContext(), temperatureAir + " " + relativeMoistureAir + " " + relativeMoistureSoil, Toast.LENGTH_LONG).show();
                }else if (!shouldWater(false) && isWatering){
                    setShouldWaterValue(0, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                    isWatering=false;
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

    public boolean shouldWater(boolean weatherIncluded) {

        if (weatherIncluded) {

            if (relativeMoistureSoilState.getColorPlantState() == Color.RED_POSITIVE
                    || relativeMoistureSoilState.getColorPlantState() == Color.YELLOW_POSITIVE
                    || relativeMoistureSoilState.getColorPlantState() == Color.GREEN) {

                return false;

            } else if (relativeMoistureSoilState.getColorPlantState() == Color.RED_NEGATIVE) {

                return true;

            } else if (relativeMoistureSoilState.getColorPlantState() == Color.YELLOW_NEGATIVE) {

                if (forecastPrecipitationAmountState.getColorPlantState() == Color.RED_POSITIVE
                        || forecastPrecipitationAmountState.getColorPlantState() == Color.YELLOW_POSITIVE) {

                    return false;
                }

                if (forecastSnowAmountState.getColorPlantState() == Color.RED_POSITIVE) {

                    return false;
                }

                if (temperatureSoilState.getColorPlantState() == Color.RED_POSITIVE
                        || temperatureSoilState.getColorPlantState() == Color.YELLOW_POSITIVE) {

                    return false;
                }

                if (temperatureAirState.getColorPlantState() == Color.RED_POSITIVE
                        || temperatureAirState.getColorPlantState() == Color.YELLOW_POSITIVE) {

                    return false;
                }

                if (relativeMoistureAirState.getColorPlantState() == Color.RED_POSITIVE) {

                    return false;
                }

                return true;
            }

        } else {

            if (relativeMoistureSoilState.getColorPlantState() == Color.RED_POSITIVE
                    || relativeMoistureSoilState.getColorPlantState() == Color.YELLOW_POSITIVE
                    || relativeMoistureSoilState.getColorPlantState() == Color.GREEN) {

                return false;

            } else if (relativeMoistureSoilState.getColorPlantState() == Color.RED_NEGATIVE) {

                return true;

            } else if (relativeMoistureSoilState.getColorPlantState() == Color.YELLOW_NEGATIVE) {

                if (temperatureSoilState.getColorPlantState() == Color.RED_POSITIVE
                        || temperatureSoilState.getColorPlantState() == Color.YELLOW_POSITIVE) {

                    return false;
                }

                if (temperatureAirState.getColorPlantState() == Color.RED_POSITIVE
                        || temperatureSoilState.getColorPlantState() == Color.YELLOW_POSITIVE) {

                    return false;
                }

                if (relativeMoistureAirState.getColorPlantState() == Color.RED_POSITIVE) {

                    return false;
                }

                return true;
            }

        }
        return true;
    }
}