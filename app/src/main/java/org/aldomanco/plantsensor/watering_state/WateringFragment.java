package org.aldomanco.plantsensor.watering_state;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.models.http_response_weather.OpenWeatherMapJSON;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.models.PlantStateModel;
import org.aldomanco.plantsensor.services.ServiceGenerator;
import org.aldomanco.plantsensor.services.StateServices;
import org.aldomanco.plantsensor.weather_state.WeatherFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WateringFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WateringFragment extends Fragment {

    Switch switchManualWatering;
    Switch switchAutomaticWatering;

    private View view;
    private static WateringFragment wateringFragment;

    SmallPlantStateAdapter adapterSmallPlantState;
    RecyclerView recyclerViewSmallPlantState;

    private List<PlantStateModel> listSmallPlantState;

    PlantModel plant;

    private SharedPreferences sharedPreferences;
    private String city;

    private static StateServices stateServices;

    private Intent intentServiceAutomaticWatering;

    public WateringFragment() { }

    public static WateringFragment newInstance() {
        WateringFragment fragment = new WateringFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_watering, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        wateringFragment = this;

        sharedPreferences = LoggedUserActivity.getLoggedUserActivity().getSharedPreferences("plant_data", Context.MODE_PRIVATE);

        switchAutomaticWatering = view.findViewById(R.id.switch_automatic_watering);
        switchManualWatering = view.findViewById(R.id.switch_manual_watering);

        boolean automaticWatering = sharedPreferences.getBoolean("automatic_watering", false);

        switchAutomaticWatering.setChecked(automaticWatering);
        switchManualWatering.setChecked(false);

        switchAutomaticWatering.setOnCheckedChangeListener(onActivationAutomaticWatering);
        switchManualWatering.setOnCheckedChangeListener(onActivationManualWatering);

        intentServiceAutomaticWatering = new Intent(LoggedUserActivity.getLoggedUserActivity(), AutomaticWateringService.class);

        getSmallPlantStateList();
    }

    CompoundButton.OnCheckedChangeListener onActivationManualWatering = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            if (isChecked){
                switchAutomaticWatering.setChecked(false);
                stopService(null);
            }
        }
    };

    CompoundButton.OnCheckedChangeListener onActivationAutomaticWatering = new CompoundButton.OnCheckedChangeListener() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            if (isChecked){
                switchManualWatering.setChecked(false);
                startService(null);
            }else {
                stopService(null);
            }
        }
    };

    public void getSmallPlantStateList() {

        plant = LoggedUserActivity.getPlant();

        listSmallPlantState = new ArrayList<>();

        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureAir());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureSoil());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureAir());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureSoil());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getLightIntensity());

        city = sharedPreferences.getString("city", null);

        if (city != null) {
            getOpenWeatherMapData(city);
        }else {
            initializeRecyclerView(listSmallPlantState);
        }
    }

    private void initializeRecyclerView(List<PlantStateModel> listPlantState) {

        recyclerViewSmallPlantState = view.findViewById(R.id.recyclerview_watering);

        if (adapterSmallPlantState == null) {
            adapterSmallPlantState = new SmallPlantStateAdapter(listPlantState);
        }

        recyclerViewSmallPlantState.setAdapter(adapterSmallPlantState);
        recyclerViewSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getOpenWeatherMapData(String locationCity) {

        Call<OpenWeatherMapJSON> httpRequest = getWeatherService().getWeatherData(locationCity);

        httpRequest.enqueue(new Callback<OpenWeatherMapJSON>() {
            @Override
            public void onResponse(Call<OpenWeatherMapJSON> call, final Response<OpenWeatherMapJSON> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null : "body() non doveva essere null";

                    OpenWeatherMapJSON openWeatherMapJSON = response.body();

                    LoggedUserActivity.getPlant().setForecastHumidityAir(openWeatherMapJSON.getSectionMixedWeatherData().getForecastRelativeMoistureAir());
                    LoggedUserActivity.getPlant().setForecastPressureAir(openWeatherMapJSON.getSectionMixedWeatherData().getForecastPressureAir());
                    LoggedUserActivity.getPlant().setForecastWindSpeed(openWeatherMapJSON.getSectionWind().getForecastWindSpeed());
                    LoggedUserActivity.getPlant().setForecastPrecipitationAmount(openWeatherMapJSON.getSectionPrecipitation());
                    LoggedUserActivity.getPlant().setForecastSnowAmount(openWeatherMapJSON.getSectionSnow());
                    LoggedUserActivity.getPlant().setForecastTemperatureAir(openWeatherMapJSON.getSectionMixedWeatherData().getForecastTemperatureAir());

                    LoggedUserActivity.getLoggedUserActivity().setForecastHumidityAir(openWeatherMapJSON.getSectionMixedWeatherData().getForecastRelativeMoistureAir());
                    LoggedUserActivity.getLoggedUserActivity().setForecastPressureAir(openWeatherMapJSON.getSectionMixedWeatherData().getForecastPressureAir());
                    LoggedUserActivity.getLoggedUserActivity().setForecastWindSpeed(openWeatherMapJSON.getSectionWind().getForecastWindSpeed());
                    LoggedUserActivity.getLoggedUserActivity().setForecastPrecipitationAmount(openWeatherMapJSON.getSectionPrecipitation());
                    LoggedUserActivity.getLoggedUserActivity().setForecastSnowAmount(openWeatherMapJSON.getSectionSnow());
                    LoggedUserActivity.getLoggedUserActivity().setForecastTemperatureAir(openWeatherMapJSON.getSectionMixedWeatherData().getForecastTemperatureAir());

                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastSnowAmount());
                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());

                    initializeRecyclerView(listSmallPlantState);

                }else {

                    initializeRecyclerView(listSmallPlantState);

                    new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                            .setIcon(android.R.drawable.stat_notify_error)
                            .setTitle("Server Error")
                            .setMessage("Internal server error.")
                            .setPositiveButton("OK", null).show();
                }
            }

            @Override
            public void onFailure(Call<OpenWeatherMapJSON> call, Throwable t) {
                // errore a livello di rete

                initializeRecyclerView(listSmallPlantState);
            }
        });
    }

    public static StateServices getWeatherService() {

        if (stateServices == null) {
            stateServices = ServiceGenerator.createService(StateServices.class);
        }

        return stateServices;
    }

    public void startService(View view){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("automatic_watering", true);
        editor.apply();

        Intent intentService = new Intent(LoggedUserActivity.getLoggedUserActivity(), AutomaticWateringService.class);
        ContextCompat.startForegroundService(LoggedUserActivity.getLoggedUserActivity(), intentService);
    }

    public void stopService(View view){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("automatic_water", false);
        editor.apply();

        Intent intentService = new Intent(LoggedUserActivity.getLoggedUserActivity(), AutomaticWateringService.class);
        getActivity().stopService(intentService);
    }
}