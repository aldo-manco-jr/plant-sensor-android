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

    public AutomaticWateringService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();

        AutomaticWateringService automaticWateringService = this;

        runnableCode = new Runnable() {
            @Override
            public void run() {

                sharedPreferences = LoggedUserActivity.getLoggedUserActivity().getSharedPreferences("plant_data", Context.MODE_PRIVATE);

                boolean automaticWatering = sharedPreferences.getBoolean("automatic_watering", true);

                Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), automaticWatering+"", Toast.LENGTH_LONG).show();

                if (!automaticWatering) {
                    setShouldWaterValue(0, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                    Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), "Stop Watering...", Toast.LENGTH_LONG).show();
                    stopSelf();
                }

                getThingSpeakData(automaticWateringService, false);
                handler.postDelayed(this, 30000);
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

                    if (temperatureAirState.getMinValueState() > temperatureAirState.getValueState()) {
                        temperatureAirState.setValueState(temperatureAirState.getMinValueState());
                    } else if (temperatureAirState.getMaxValueState() < temperatureAirState.getValueState()) {
                        temperatureAirState.setValueState(temperatureAirState.getMaxValueState());
                    }

                    relativeMoistureAirState.setValueState(relativeMoistureAir);

                    if (relativeMoistureAirState.getMinValueState() > relativeMoistureAirState.getValueState()) {
                        relativeMoistureAirState.setValueState(relativeMoistureAirState.getMinValueState());
                    } else if (relativeMoistureAirState.getMaxValueState() < relativeMoistureAirState.getValueState()) {
                        relativeMoistureAirState.setValueState(relativeMoistureAirState.getMaxValueState());
                    }

                    relativeMoistureSoilState.setValueState(relativeMoistureSoil);

                    if (relativeMoistureSoilState.getMinValueState() > relativeMoistureSoilState.getValueState()) {
                        relativeMoistureSoilState.setValueState(relativeMoistureSoilState.getMinValueState());
                    } else if (relativeMoistureSoilState.getMaxValueState() < relativeMoistureSoilState.getValueState()) {
                        relativeMoistureSoilState.setValueState(relativeMoistureSoilState.getMaxValueState());
                    }

                    temperatureSoilState.setValueState(temperatureSoil);

                    if (temperatureSoilState.getMinValueState() > temperatureSoilState.getValueState()) {
                        temperatureSoilState.setValueState(temperatureSoilState.getMinValueState());
                    } else if (temperatureSoilState.getMaxValueState() < temperatureSoilState.getValueState()) {
                        temperatureSoilState.setValueState(temperatureSoilState.getMaxValueState());
                    }

                    lightIntensityState.setValueState(lightIntensity);

                    if (lightIntensityState.getMinValueState() > lightIntensityState.getValueState()) {
                        lightIntensityState.setValueState(lightIntensityState.getMinValueState());
                    } else if (lightIntensityState.getMaxValueState() < lightIntensityState.getValueState()) {
                        lightIntensityState.setValueState(lightIntensityState.getMaxValueState());
                    }

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

    private void setShouldWaterValue(double shouldWater, double temperatureAir, double relativeMoistureAir, double temperatureSoil, double relativeMoistureSoil, double lightIntensity) {

        Call<Object> httpRequest = getStateServices().setShouldWaterValue(shouldWater, temperatureAir, relativeMoistureAir, relativeMoistureSoil, temperatureSoil, lightIntensity);

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

                    if (forecastHumidityAirState.getMinValueState() > forecastHumidityAirState.getValueState()) {
                        forecastHumidityAirState.setValueState(forecastHumidityAirState.getMinValueState());
                    } else if (forecastHumidityAirState.getMaxValueState() < forecastHumidityAirState.getValueState()) {
                        forecastHumidityAirState.setValueState(forecastHumidityAirState.getMaxValueState());
                    }

                    forecastPrecipitationAmountState.setValueState(forecastPrecipitationAmount);

                    if (forecastPrecipitationAmountState.getMinValueState() > forecastPrecipitationAmountState.getValueState()) {
                        forecastPrecipitationAmountState.setValueState(forecastPrecipitationAmountState.getMinValueState());
                    } else if (forecastPrecipitationAmountState.getMaxValueState() < forecastPrecipitationAmountState.getValueState()) {
                        forecastPrecipitationAmountState.setValueState(forecastPrecipitationAmountState.getMaxValueState());
                    }

                    forecastSnowAmountState.setValueState(forecastSnowAmount);

                    if (forecastSnowAmountState.getMinValueState() > forecastSnowAmountState.getValueState()) {
                        forecastSnowAmountState.setValueState(forecastSnowAmountState.getMinValueState());
                    } else if (forecastSnowAmountState.getMaxValueState() < forecastSnowAmountState.getValueState()) {
                        forecastSnowAmountState.setValueState(forecastSnowAmountState.getMaxValueState());
                    }

                    forecastPressureAirState.setValueState(forecastPressureAir);

                    if (forecastPressureAirState.getMinValueState() > forecastPressureAirState.getValueState()) {
                        forecastPressureAirState.setValueState(forecastPressureAirState.getMinValueState());
                    } else if (forecastPressureAirState.getMaxValueState() < forecastPressureAirState.getValueState()) {
                        forecastPressureAirState.setValueState(forecastPressureAirState.getMaxValueState());
                    }

                    forecastWindSpeedState.setValueState(forecastWindSpeed);

                    if (forecastWindSpeedState.getMinValueState() > forecastWindSpeedState.getValueState()) {
                        forecastWindSpeedState.setValueState(forecastWindSpeedState.getMinValueState());
                    } else if (forecastWindSpeedState.getMaxValueState() < forecastWindSpeedState.getValueState()) {
                        forecastWindSpeedState.setValueState(forecastWindSpeedState.getMaxValueState());
                    }

                    forecastTemperatureAirState.setValueState(forecastTemperatureAir);

                    if (forecastTemperatureAirState.getMinValueState() > forecastTemperatureAirState.getValueState()) {
                        forecastTemperatureAirState.setValueState(forecastTemperatureAirState.getMinValueState());
                    } else if (forecastTemperatureAirState.getMaxValueState() < forecastTemperatureAirState.getValueState()) {
                        forecastTemperatureAirState.setValueState(forecastTemperatureAirState.getMaxValueState());
                    }

                    if (shouldWater(true)) {
                        setShouldWaterValue(1, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                        Toast.makeText(getApplicationContext(), "Start Watering...", Toast.LENGTH_LONG).show();
                    } else {
                        setShouldWaterValue(0, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                        Toast.makeText(getApplicationContext(), "Stop Watering...", Toast.LENGTH_LONG).show();
                    }

                } else {

                    if (shouldWater(false)) {
                        setShouldWaterValue(1, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                        Toast.makeText(getApplicationContext(), "Start Watering...", Toast.LENGTH_LONG).show();
                    } else {
                        setShouldWaterValue(0, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                        Toast.makeText(getApplicationContext(), "Stop Watering...", Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<OpenWeatherMapJSON> call, Throwable t) {
                // errore a livello di rete

                if (shouldWater(false)) {
                    setShouldWaterValue(1, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                    Toast.makeText(getApplicationContext(), "Start Watering...", Toast.LENGTH_LONG).show();
                } else {
                    setShouldWaterValue(0, temperatureAir, relativeMoistureAir, temperatureSoil, relativeMoistureSoil, lightIntensity);
                    Toast.makeText(getApplicationContext(), "Stop Watering...", Toast.LENGTH_LONG).show();
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