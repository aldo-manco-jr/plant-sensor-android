package org.aldomanco.plantsensor.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.health_state.HealthFragment;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.models.PlantStateModel;
import org.aldomanco.plantsensor.models.http_response_plantsensor.ThingSpeakJSON;
import org.aldomanco.plantsensor.plant_state.PlantStateFragment;
import org.aldomanco.plantsensor.receivers.ConnectionLostReceiver;
import org.aldomanco.plantsensor.services.ServiceGenerator;
import org.aldomanco.plantsensor.services.StateServices;
import org.aldomanco.plantsensor.utils.SubsystemEnumeration;
import org.aldomanco.plantsensor.watering_state.WateringFragment;
import org.aldomanco.plantsensor.weather_state.WeatherFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoggedUserActivity extends AppCompatActivity {

    private static String token;
    private static String usernameLoggedUser;

    private PlantStateFragment plantStateFragment = null;
    private WeatherFragment weatherFragment = null;
    private HealthFragment healthFragment = null;
    private WateringFragment wateringFragment = null;

    private SharedPreferences sharedPreferences;

    private static LoggedUserActivity loggedUserActivity;

    private static final String NOTIFICATION_CHANNEL_ID = "PlantSensor Notification Manager ID";
    private static final String NOTIFICATION_CHANNEL_NAME = "PlantSensor Notification Manager Title";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "PlantSensor Notification Manager Description";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loggedUserActivity = this;

        getThingSpeakData();

        /*try {
            token = getIntent().getExtras().getString("authToken");
            usernameLoggedUser = getIntent().getExtras().getString("username");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);

                NotificationCompat.Builder notification = new NotificationCompat.Builder(LoggedUserActivity.getLoggedUserActivity(), NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_insert_photo_24)
                        .setContentTitle("Login Successful")
                        .setContentText("Welcome " + LoggedUserActivity.getUsernameLoggedUser() + "!")
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(LoggedUserActivity.getLoggedUserActivity());
                notificationManagerCompat.notify(1, notification.build());
            }

            sharedPreferences = getSharedPreferences(getString(R.string.sharedpreferences_authentication), Context.MODE_PRIVATE);

        } catch (Exception e) { }*/
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

    public static String getUsernameLoggedUser() {
        return usernameLoggedUser;
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

    public static String getNotificationChannelId() {
        return NOTIFICATION_CHANNEL_ID;
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
            case "A":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "B":
                startingYellowValueState = -30;
                endingYellowValueState = 30;
                startingGreenValueState = -10;
                endingGreenValueState = 15;
                break;
            case "C":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
                break;
            case "D":
                startingYellowValueState = -20;
                endingYellowValueState = 40;
                startingGreenValueState = 0;
                endingGreenValueState = 25;
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
            case "A":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "B":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "C":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "D":
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
            case "A":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "B":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "C":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "D":
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
            case "A":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "B":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "C":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "D":
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
            case "A":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "B":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "C":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "D":
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
            case "A":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "B":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "C":
                startingYellowValueState = 10;
                endingYellowValueState = 90;
                startingGreenValueState = 30;
                endingGreenValueState = 70;
                break;
            case "D":
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
                            "Plant Name",
                            "Fiori Primaverili",
                            "Isernia",
                            "Italy",
                            "Federica",
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

                } else if(response.code()==404){
                    new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                            .setIcon(android.R.drawable.stat_notify_error)
                            .setTitle("Invalid Location")
                            .setMessage("Please enter an existing city name, the one entered not exists.")
                            .setPositiveButton("OK", null).show();

                }else if(response.code()==500){
                    new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                            .setIcon(android.R.drawable.stat_notify_error)
                            .setTitle("Server Error")
                            .setMessage("Internal server error.")
                            .setPositiveButton("OK", null).show();
                }
            }

            @Override
            public void onFailure(Call<ThingSpeakJSON> call, Throwable t) {
                // errore a livello di rete

                LoggedUserActivity.getPlant().setForecastHumidityAir(0);
                LoggedUserActivity.getPlant().setForecastPressureAir(0);
                LoggedUserActivity.getPlant().setForecastWindSpeed(0);
                LoggedUserActivity.getPlant().setForecastPrecipitationAmount(0.0);
                LoggedUserActivity.getPlant().setForecastTemperatureAir(0.0);
                LoggedUserActivity.getPlant().setForecastSnowAmount(0.0);

                plant = new PlantModel(
                        1,
                        "Plant Name",
                        "Fiori Primaverili",
                        "Isernia",
                        "Italy",
                        "Federica",
                        20,
                        0,
                        -21,
                        0,
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

                new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                        .setIcon(android.R.drawable.stat_notify_error)
                        .setTitle("Server Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    public static StateServices getStateServices() {

        if (stateServices == null) {
            stateServices = ServiceGenerator.createService(StateServices.class);
        }

        return stateServices;
    }
}