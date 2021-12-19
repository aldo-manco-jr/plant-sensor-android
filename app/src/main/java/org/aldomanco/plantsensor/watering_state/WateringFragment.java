package org.aldomanco.plantsensor.watering_state;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.models.OpenWeatherMapJSON;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.models.PlantStateModel;
import org.aldomanco.plantsensor.services.ServiceGenerator;
import org.aldomanco.plantsensor.services.WeatherService;

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

    private static WeatherService weatherService;

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

        switchAutomaticWatering = view.findViewById(R.id.switch_automatic_watering);
        switchManualWatering = view.findViewById(R.id.switch_manual_watering);

        switchAutomaticWatering.setOnCheckedChangeListener(onActivationAutomaticWatering);

        switchManualWatering.setOnCheckedChangeListener(onActivationManualWatering);

        getSmallPlantStateList();
    }

    CompoundButton.OnCheckedChangeListener onActivationManualWatering = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            if (isChecked){
                Toast.makeText(getActivity(), "ON manual", Toast.LENGTH_LONG).show();
                switchAutomaticWatering.setChecked(false);
            }else {
                Toast.makeText(getActivity(), "OFF manual", Toast.LENGTH_LONG).show();
            }
        }
    };

    CompoundButton.OnCheckedChangeListener onActivationAutomaticWatering = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            if (isChecked){
                Toast.makeText(getActivity(), "ON auto", Toast.LENGTH_LONG).show();
                switchManualWatering.setChecked(false);
            }else {
                Toast.makeText(getActivity(), "OFF auto", Toast.LENGTH_LONG).show();
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

        sharedPreferences = LoggedUserActivity.getLoggedUserActivity().getSharedPreferences("city", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city", null);

        if (city != null) {
            getOpenWeatherMapData(city);
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
                    //LoggedUserActivity.getPlant().setForecastPrecipitationAmount(openWeatherMapJSON.getSectionPrecipitation().getForecastPrecipitationAmount());
                    LoggedUserActivity.getPlant().setForecastTemperatureAir(openWeatherMapJSON.getSectionMixedWeatherData().getForecastTemperatureAir());

                    LoggedUserActivity.getLoggedUserActivity().setForecastHumidityAir(openWeatherMapJSON.getSectionMixedWeatherData().getForecastRelativeMoistureAir());
                    LoggedUserActivity.getLoggedUserActivity().setForecastPressureAir(openWeatherMapJSON.getSectionMixedWeatherData().getForecastPressureAir());
                    LoggedUserActivity.getLoggedUserActivity().setForecastWindSpeed(openWeatherMapJSON.getSectionWind().getForecastWindSpeed());
                    //LoggedUserActivity.getPlant().setForecastPrecipitationAmount(openWeatherMapJSON.getSectionPrecipitation().getForecastPrecipitationAmount());
                    LoggedUserActivity.getLoggedUserActivity().setForecastTemperatureAir(openWeatherMapJSON.getSectionMixedWeatherData().getForecastTemperatureAir());

                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
                    listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());

                    initializeRecyclerView(listSmallPlantState);

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
            public void onFailure(Call<OpenWeatherMapJSON> call, Throwable t) {
                // errore a livello di rete

                LoggedUserActivity.getPlant().setForecastHumidityAir(0);
                LoggedUserActivity.getPlant().setForecastPressureAir(0);
                LoggedUserActivity.getPlant().setForecastWindSpeed(0);
                LoggedUserActivity.getPlant().setForecastPrecipitationAmount(0.0);
                LoggedUserActivity.getPlant().setForecastTemperatureAir(0.0);

                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());

                initializeRecyclerView(listSmallPlantState);

                new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                        .setIcon(android.R.drawable.stat_notify_error)
                        .setTitle("Server Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    public static WeatherService getWeatherService() {

        if (weatherService == null) {
            weatherService = ServiceGenerator.createService(WeatherService.class);
        }

        return weatherService;
    }
}