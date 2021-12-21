package org.aldomanco.plantsensor.home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.authentication.ConnectionProblemsActivity;
import org.aldomanco.plantsensor.health_state.HealthFragment;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.models.PlantStateModel;
import org.aldomanco.plantsensor.models.http_response_plantsensor.ThingSpeakJSON;
import org.aldomanco.plantsensor.plant_state.PlantStateFragment;
import org.aldomanco.plantsensor.receivers.ConnectionLostReceiver;
import org.aldomanco.plantsensor.services.ServiceGenerator;
import org.aldomanco.plantsensor.services.StateServices;
import org.aldomanco.plantsensor.utils.SubsystemEnumeration;
import org.aldomanco.plantsensor.watering_state.AutomaticWateringService;
import org.aldomanco.plantsensor.watering_state.WateringFragment;
import org.aldomanco.plantsensor.weather_state.WeatherFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoggedUserActivity extends AppCompatActivity {

    private PlantStateFragment plantStateFragment = null;
    private WeatherFragment weatherFragment = null;
    private HealthFragment healthFragment = null;
    private WateringFragment wateringFragment = null;

    private SharedPreferences sharedPreferences;

    private static LoggedUserActivity loggedUserActivity;

    private ConnectionLostReceiver connectionLostReceiver = new ConnectionLostReceiver();

    private static PlantModel plant;

    private PlantStateModel relativeMoistureSoil;
    private PlantStateModel relativeMoistureAir;
    private PlantStateModel temperatureSoil;
    private PlantStateModel temperatureAir;
    private PlantStateModel lightIntensity;

    private PlantStateModel forecastWeatherState;
    private PlantStateModel forecastPrecipitationAmount;
    private PlantStateModel forecastHumidityAir;
    private PlantStateModel forecastTemperatureAir;
    private PlantStateModel forecastWindSpeed;
    private PlantStateModel forecastSnowAmount;
    private PlantStateModel forecastPressureAir;

    private static StateServices stateServices;

    private Intent intentFirstActivity;

    private String plantName;
    private String plantType;
    private String plantLocationCity;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loggedUserActivity = this;

        sharedPreferences = getSharedPreferences("plant_data", Context.MODE_PRIVATE);
        plantName = sharedPreferences.getString("plant_name", "");
        plantType = sharedPreferences.getString("plant_type", "");
        plantLocationCity = sharedPreferences.getString("city", "");

        boolean automaticWatering = sharedPreferences.getBoolean("automatic_watering", false);

        if (automaticWatering){
            startService();
        }

        getThingSpeakData();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navbarListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int navigationSectionIdentifier = item.getItemId();

                    switch (navigationSectionIdentifier) {
                        case R.id.navigation_plant_state:
                            plantStateFragment = (PlantStateFragment) createNewInstanceIfNecessary(plantStateFragment, SubsystemEnumeration.plantState);
                            changeFragment(plantStateFragment);
                            break;
                        case R.id.navigation_weather_state:
                            weatherFragment = (WeatherFragment)
                                    createNewInstanceIfNecessary(weatherFragment, SubsystemEnumeration.weatherState);
                            changeFragment(weatherFragment);
                            break;
                        case R.id.navigation_watering_state:
                            wateringFragment = (WateringFragment)
                                    createNewInstanceIfNecessary(wateringFragment, SubsystemEnumeration.wateringState);
                            changeFragment(wateringFragment);
                            break;
                        case R.id.navigation_health_state:
                            healthFragment = (HealthFragment)
                                    createNewInstanceIfNecessary(healthFragment, SubsystemEnumeration.healthState);
                            changeFragment(healthFragment);
                            break;
                    }

                    return true;
                }
            };


    private Fragment createNewInstanceIfNecessary(Fragment fragment, SubsystemEnumeration
            identifier) {

        if (fragment == null) {
            try {
                if (identifier == SubsystemEnumeration.plantState) {
                    fragment = PlantStateFragment.newInstance();
                } else if (identifier == SubsystemEnumeration.weatherState) {
                    fragment = WeatherFragment.newInstance();
                } else if (identifier == SubsystemEnumeration.wateringState) {
                    fragment = WateringFragment.newInstance();
                } else if (identifier == SubsystemEnumeration.healthState) {
                    fragment = HealthFragment.newInstance();
                }
            } catch (Exception ignored) { }
        }

        return fragment;
    }

    public void changeFragment(Fragment selectedFragment) {

        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.logged_user_fragment, selectedFragment).commit();
        } catch (Exception ignored) { }
    }

    public static LoggedUserActivity getLoggedUserActivity() {
        return loggedUserActivity;
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(connectionLostReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(connectionLostReceiver);
    }

    public static PlantModel getPlant() {
        return plant;
    }

    public void setForecastPrecipitationAmount(double forecastPrecipitationAmount) {
        this.forecastPrecipitationAmount.setValueState(forecastPrecipitationAmount);
    }

    public void setForecastHumidityAir(double forecastHumidityAir) {
        this.forecastHumidityAir.setValueState(forecastHumidityAir);
    }

    public void setForecastTemperatureAir(double forecastTemperatureAir) {
        this.forecastTemperatureAir.setValueState(forecastTemperatureAir);
    }

    public void setForecastWindSpeed(double forecastWindSpeed) {
        this.forecastWindSpeed.setValueState(forecastWindSpeed);
    }

    public void setForecastPressureAir(double forecastPressureAir) {
        this.forecastPressureAir.setValueState(forecastPressureAir);
    }

    public void setForecastSnowAmount(double forecastSnowAmount) {
        this.forecastSnowAmount.setValueState(forecastSnowAmount);
    }

    public PlantStateModel getForecastSnowAmount() {
        return forecastSnowAmount;
    }

    public PlantStateModel getRelativeMoistureSoil() {
        return relativeMoistureSoil;
    }

    public PlantStateModel getRelativeMoistureAir() {
        return relativeMoistureAir;
    }

    public PlantStateModel getTemperatureSoil() {
        return temperatureSoil;
    }

    public PlantStateModel getTemperatureAir() {
        return temperatureAir;
    }

    public PlantStateModel getLightIntensity() {
        return lightIntensity;
    }

    public PlantStateModel getForecastWeatherState() {
        return forecastWeatherState;
    }

    public PlantStateModel getForecastPrecipitationAmount() {
        return forecastPrecipitationAmount;
    }

    public PlantStateModel getForecastHumidityAir() {
        return forecastHumidityAir;
    }

    public PlantStateModel getForecastTemperatureAir() {
        return forecastTemperatureAir;
    }

    public PlantStateModel getForecastWindSpeed() {
        return forecastWindSpeed;
    }

    public PlantStateModel getForecastPressureAir() {
        return forecastPressureAir;
    }

    private void initializeTemperatureAir(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = -30;
                endingYellowValueState = 30;
                startingGreenValueState = -10;
                endingGreenValueState = 15;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "Pianta Grassa":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            default:
                break;
        }

        temperatureAir = new PlantStateModel("Air Temperature", R.drawable.temperatura, plant.getTemperatureAir(), "desc", -40, 50, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeTemperatureSoil(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = -30;
                endingYellowValueState = 30;
                startingGreenValueState = -10;
                endingGreenValueState = 15;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "Pianta Grassa":
                startingYellowValueState = -25;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            default:
                break;
        }

        temperatureSoil = new PlantStateModel("Soil Temperature", R.drawable.temperatura_suolo, plant.getTemperatureSoil(), "desc", -40, 50, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeRelativeMoistureAir(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            default:
                break;
        }

        relativeMoistureAir = new PlantStateModel("Air Moisture", R.drawable.umidita, plant.getRelativeMoistureAir(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeRelativeMoistureSoil(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            default:
                break;
        }

        relativeMoistureSoil = new PlantStateModel("Soil Moisture", R.drawable.umidita_suolo, plant.getRelativeMoistureSoil(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeLightIntensity(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            default:
                break;
        }

        lightIntensity = new PlantStateModel("Light Intensity", R.drawable.luminosita, plant.getLightIntensity(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    // -----------------

    private void initializeForecastPrecipitationAmount(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            default:
                break;
        }

        forecastPrecipitationAmount = new PlantStateModel("Precipitation Amount", R.drawable.pioggia, plant.getForecastPrecipitationAmount(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeForecastRelativeMoistureAir(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            default:
                break;
        }

        forecastHumidityAir = new PlantStateModel("Humidity Air", R.drawable.umidita, plant.getForecastHumidityAir(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeForecastTemperatureAir(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            default:
                break;
        }

        forecastTemperatureAir = new PlantStateModel("Temperature Air", R.drawable.temperatura_2, plant.getForecastTemperatureAir(), "desc", -40, 50, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeForecastWindSpeed(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            default:
                break;
        }

        forecastWindSpeed = new PlantStateModel("Wind Speed", R.drawable.vento, plant.getForecastWindSpeed(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeForecastSnowAmount(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            default:
                break;
        }

        forecastSnowAmount = new PlantStateModel("Snow Amount", R.drawable.neve, plant.getForecastSnowAmount(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeForecastPressureAir(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            default:
                break;
        }

        forecastPressureAir = new PlantStateModel("Pressure Air", R.drawable.pressione, plant.getForecastPressureAir(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void getThingSpeakData() {

        Call<ThingSpeakJSON> httpRequest = getStateServices().getPlantStateData();

        httpRequest.enqueue(new Callback<ThingSpeakJSON>() {
            @Override
            public void onResponse(Call<ThingSpeakJSON> call, final Response<ThingSpeakJSON> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    ThingSpeakJSON thingSpeakJSON = response.body();

                    plant = new PlantModel(
                            1,
                            plantName,
                            plantType,
                            plantLocationCity,
                            thingSpeakJSON.getListPlantSensorValues().get(0).getRelativeMoistureSoil(),
                            thingSpeakJSON.getListPlantSensorValues().get(0).getRelativeMoistureAir(),
                            -21,
                            thingSpeakJSON.getListPlantSensorValues().get(0).getTemperatureAir(),
                            50,
                            "",
                            0,
                            0,
                            0,
                            0,
                            0,
                            0
                    );

                    initializeTemperatureAir(plant.getPlantType());
                    initializeTemperatureSoil(plant.getPlantType());
                    initializeRelativeMoistureAir(plant.getPlantType());
                    initializeRelativeMoistureSoil(plant.getPlantType());
                    initializeLightIntensity(plant.getPlantType());

                    initializeForecastPrecipitationAmount(plant.getPlantType());
                    initializeForecastRelativeMoistureAir(plant.getPlantType());
                    initializeForecastPressureAir(plant.getPlantType());
                    initializeForecastTemperatureAir(plant.getPlantType());
                    initializeForecastWindSpeed(plant.getPlantType());
                    initializeForecastSnowAmount(plant.getPlantType());

                    BottomNavigationView navbarLoggedUser = findViewById(R.id.logged_user_navbar);

                    navbarLoggedUser.setOnNavigationItemSelectedListener(navbarListener);

                    plantStateFragment = (PlantStateFragment) createNewInstanceIfNecessary(plantStateFragment, SubsystemEnumeration.plantState);
                    changeFragment(plantStateFragment);
                    navbarLoggedUser.setSelectedItemId(0);

                } else {
                    intentFirstActivity = new Intent(LoggedUserActivity.getLoggedUserActivity(), ConnectionProblemsActivity.class);
                    intentFirstActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentFirstActivity);

                    Intent intent = new Intent(LoggedUserActivity.getLoggedUserActivity(), LoggedUserActivity.class);
                    stopService(intent);

                    ActivityCompat.finishAffinity(LoggedUserActivity.getLoggedUserActivity());
                }
            }

            @Override
            public void onFailure(Call<ThingSpeakJSON> call, Throwable t) {
                // errore a livello di rete

                intentFirstActivity = new Intent(LoggedUserActivity.getLoggedUserActivity(), ConnectionProblemsActivity.class);
                intentFirstActivity.putExtra("is_logged", true);
                startActivity(intentFirstActivity);

                Intent intent = new Intent(LoggedUserActivity.getLoggedUserActivity(), LoggedUserActivity.class);
                stopService(intent);

                ActivityCompat.finishAffinity(LoggedUserActivity.getLoggedUserActivity());
            }
        });
    }

    public static StateServices getStateServices() {

        if (stateServices == null) {
            stateServices = ServiceGenerator.createService(StateServices.class);
        }

        return stateServices;
    }

    public void startService(){
        Intent intentService = new Intent(LoggedUserActivity.getLoggedUserActivity(), AutomaticWateringService.class);
        ContextCompat.startForegroundService(LoggedUserActivity.getLoggedUserActivity(), intentService);
    }
}