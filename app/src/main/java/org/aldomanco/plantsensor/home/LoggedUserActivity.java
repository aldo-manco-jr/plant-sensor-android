package org.aldomanco.plantsensor.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.health_state.HealthFragment;
import org.aldomanco.plantsensor.plant_state.PlantStateFragment;
import org.aldomanco.plantsensor.receivers.ConnectionLostReceiver;
import org.aldomanco.plantsensor.utils.SubsystemEnumeration;
import org.aldomanco.plantsensor.watering_state.WateringFragment;
import org.aldomanco.plantsensor.weather_state.WeatherFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    /*private static StreamsService streamsService;
    private static UsersService usersService;
    private static ImagesService imagesService;
    private static NotificationsService notificationsService;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loggedUserActivity = this;

        BottomNavigationView navbarLoggedUser = findViewById(R.id.logged_user_navbar);

        navbarLoggedUser.setOnNavigationItemSelectedListener(navbarListener);

        try {
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
//            streamsService = streamsService = ServiceGenerator.createService(StreamsService.class, getToken());
//            usersService = ServiceGenerator.createService(UsersService.class, getToken());
            plantStateFragment = (PlantStateFragment) createNewInstanceIfNecessary(plantStateFragment, SubsystemEnumeration.plantState);

            changeFragment(plantStateFragment);

        } catch (Exception e) { }
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
                } /*else if (identifier == SubsystemEnumeration.weatherState) {
                    fragment = WeatherFragment.newInstance();
                } else if (identifier == SubsystemEnumeration.wateringState) {
                    fragment = WateringFragment.newInstance();
                } else if (identifier == SubsystemEnumeration.healthState) {
                    fragment = HealthFragment.newInstance();
                }*/
            } catch (Exception ignored) {
            }
        }

        return fragment;
    }

    /*public static StreamsService getStreamsService() {
        return streamsService;
    }

    public static NotificationsService getNotificationsService() {

        if (notificationsService == null) {
            notificationsService = ServiceGenerator.createService(NotificationsService.class, getToken());
        }
        return notificationsService;
    }*/

    public static String getToken() {
        return token;
    }

    public static String getUsernameLoggedUser() {
        return usernameLoggedUser;
    }

    /*@Override
    public void onBackPressed() {
        tellFragments();
    }

    private void tellFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof PlantStateFragment)
                ((PlantStateFragment) fragment).onBackPressed();
            else if (fragment instanceof WeatherFragment) {
                ((WeatherFragment) fragment).onBackPressed();
            } else if (fragment instanceof WateringFragment) {
                ((WateringFragment) fragment).onBackPressed();
            } else if (fragment instanceof HealthFragment) {
                ((HealthFragment) fragment).onBackPressed();
            }
        }
    }*/

    //TODO NON VA BENE QUESTO METODO
    // le socket vengono ripetute tante volte quante sono le volte in cui il frmmento è STATO creato
    //TODO è sbagliata, i frammenti continuano a esistere da qualche parte

    public void changeFragment(Fragment selectedFragment) {

        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.logged_user_fragment, selectedFragment).commit();
        } catch (Exception ignored) { }

        /*Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.logged_user_fragment);

        if (oldFragment != newFragment) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transactionsManager = fragmentManager.beginTransaction();

            if (oldFragment != null) {
                transactionsManager
                        .replace(R.id.logged_user_fragment, newFragment)
                        //.remove(oldFragment)
                        .commit();
            } else {
                transactionsManager
                        .replace(R.id.logged_user_fragment, newFragment)
                        .commit();
            }
        }*/
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
}