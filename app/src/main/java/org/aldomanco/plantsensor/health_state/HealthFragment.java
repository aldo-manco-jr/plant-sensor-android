package org.aldomanco.plantsensor.health_state;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.Toast;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.models.PlantStateModel;
import org.aldomanco.plantsensor.models.http_response_weather.OpenWeatherMapJSON;
import org.aldomanco.plantsensor.services.ServiceGenerator;
import org.aldomanco.plantsensor.services.StateServices;
import org.aldomanco.plantsensor.watering_state.SmallPlantStateAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthFragment extends Fragment {

    private View view;
    private static HealthFragment healthFragment;

    SmallPlantStateAdapter adapterDangerSmallPlantState;
    SmallPlantStateAdapter adapterWarningSmallPlantState;
    SmallPlantStateAdapter adapterNormalSmallPlantState;

    RecyclerView recyclerViewDangerSmallPlantState;
    RecyclerView recyclerViewWarningSmallPlantState;
    RecyclerView recyclerViewNormalSmallPlantState;

    private List<PlantStateModel> listSmallPlantState;

    private List<PlantStateModel> listDangerSmallPlantState;
    private List<PlantStateModel> listWarningSmallPlantState;
    private List<PlantStateModel> listNormalSmallPlantState;

    PlantModel plant;

    ImageButton infoIndexOfHealth;
    ImageButton infoPhMeters;

    private static StateServices stateServices;
    private SharedPreferences sharedPreferences;
    private String city;

    public HealthFragment() {
    }

    public static HealthFragment newInstance() {
        HealthFragment fragment = new HealthFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        healthFragment = this;

        infoIndexOfHealth = view.findViewById(R.id.info_index_of_health);
        infoPhMeters = view.findViewById(R.id.info_ph_meters);

        infoIndexOfHealth.setOnClickListener(listenerInfoIndexOfHealth);
        infoPhMeters.setOnClickListener(listenerInfoPhMeters);

        plant = LoggedUserActivity.getPlant();

        getSmallPlantStateList();
    }

    View.OnClickListener listenerInfoIndexOfHealth = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                    .setIcon(R.drawable.ic_baseline_privacy_tip_24)
                    .setTitle("Index Of Health")
                    .setMessage("index of health")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    };

    View.OnClickListener listenerInfoPhMeters = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                    .setIcon(R.drawable.ic_baseline_privacy_tip_24)
                    .setTitle("PH Meters")
                    .setMessage("ph meters")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    };

    public void getSmallPlantStateList() {

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

    public void getDangerSmallPlantStateList() {

        listDangerSmallPlantState = new ArrayList<>();

        for (PlantStateModel plantState : listSmallPlantState) {

            if (plantState.getValueState() > plantState.getEndingYellowValueState()
                    || plantState.getValueState() < plantState.getStartingYellowValueState()) {

                listDangerSmallPlantState.add(plantState);
            }
        }

        initializeDangerRecyclerView(listDangerSmallPlantState);
    }

    public void getWarningSmallPlantStateList() {

        listWarningSmallPlantState = new ArrayList<>();

        for (PlantStateModel plantState : listSmallPlantState) {

            if ((plantState.getValueState() >= plantState.getStartingYellowValueState()
                    && plantState.getValueState() < plantState.getStartingGreenValueState())
                    || (plantState.getValueState() > plantState.getEndingGreenValueState()
                    && plantState.getValueState() <= plantState.getEndingYellowValueState())) {

                listWarningSmallPlantState.add(plantState);
            }
        }

        initializeWarningRecyclerView(listWarningSmallPlantState);
    }

    public void getNormalSmallPlantStateList() {

        listNormalSmallPlantState = new ArrayList<>();

        for (PlantStateModel plantState : listSmallPlantState) {

            if (plantState.getValueState() >= plantState.getStartingGreenValueState()
                    && plantState.getValueState() <= plantState.getEndingGreenValueState()) {

                listNormalSmallPlantState.add(plantState);
            }
        }

        initializeNormalRecyclerView(listNormalSmallPlantState);
    }

    private void initializeDangerRecyclerView(List<PlantStateModel> listDangerSmallPlantState) {

        recyclerViewDangerSmallPlantState = view.findViewById(R.id.recyclerview_health_danger);

        if (adapterDangerSmallPlantState == null) {
            adapterDangerSmallPlantState = new SmallPlantStateAdapter(listDangerSmallPlantState);
        }

        recyclerViewDangerSmallPlantState.setAdapter(adapterDangerSmallPlantState);
        recyclerViewDangerSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initializeWarningRecyclerView(List<PlantStateModel> listWarningSmallPlantState) {

        recyclerViewWarningSmallPlantState = view.findViewById(R.id.recyclerview_health_warning);

        if (adapterWarningSmallPlantState == null) {
            adapterWarningSmallPlantState = new SmallPlantStateAdapter(listWarningSmallPlantState);
        }

        recyclerViewWarningSmallPlantState.setAdapter(adapterWarningSmallPlantState);
        recyclerViewWarningSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initializeNormalRecyclerView(List<PlantStateModel> listNormalSmallPlantState) {

        recyclerViewNormalSmallPlantState = view.findViewById(R.id.recyclerview_health_normal);

        if (adapterNormalSmallPlantState == null) {
            adapterNormalSmallPlantState = new SmallPlantStateAdapter(listNormalSmallPlantState);
        }

        recyclerViewNormalSmallPlantState.setAdapter(adapterNormalSmallPlantState);
        recyclerViewNormalSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
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

                    getNormalSmallPlantStateList();
                    getDangerSmallPlantStateList();
                    getWarningSmallPlantStateList();

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
                LoggedUserActivity.getPlant().setForecastSnowAmount(0.0);

                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastSnowAmount());
                listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());

                getNormalSmallPlantStateList();
                getDangerSmallPlantStateList();
                getWarningSmallPlantStateList();

                new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                        .setIcon(android.R.drawable.stat_notify_error)
                        .setTitle("Server Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    public static StateServices getWeatherService() {

        if (stateServices == null) {
            stateServices = ServiceGenerator.createService(StateServices.class);
        }

        return stateServices;
    }
}