package org.aldomanco.plantsensor.health_state;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.models.Color;
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

    private PlantStateModel phMeters;
    private PlantStateModel indexOfHealth;

    private TextView phMetersValue;
    private TextView indexOfHealthValue;

    private ProgressBar progressBarPhMeters;
    private ProgressBar progressBarIndexOfHealth;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        initializePhMeters(plant.getPlantType());
        initializeIndexOfHealth(plant.getPlantType());

        phMetersValue = view.findViewById(R.id.percentual_phmeters);
        indexOfHealthValue = view.findViewById(R.id.percentual_healt);

        if (phMeters.getValueState() == Double.MIN_VALUE
                || phMeters.getValueState() == Double.MAX_VALUE) {

            phMetersValue.setText("Not Available");

        } else {

            phMetersValue.setText(String.valueOf(phMeters.getValueState()));

        }

        if (indexOfHealth.getValueState() == Double.NEGATIVE_INFINITY
                || indexOfHealth.getValueState() == Double.POSITIVE_INFINITY) {

            indexOfHealthValue.setText("Not Available");

        } else {

            indexOfHealthValue.setText(String.format("%.1f", indexOfHealth.getValueState()) + "%");

        }

        progressBarIndexOfHealth = view.findViewById(R.id.progress_bar_healt);

        progressBarIndexOfHealth.setMin(((Double) indexOfHealth.getMinValueState()).intValue());
        progressBarIndexOfHealth.setMax(((Double) indexOfHealth.getMaxValueState()).intValue());
        progressBarIndexOfHealth.setProgress(((Double) indexOfHealth.getValueState()).intValue());

        indexOfHealth.setColorPlantState();

        if (indexOfHealth.getColorPlantState() == Color.GREEN) {
            progressBarIndexOfHealth.setBackgroundDrawable(LoggedUserActivity.getLoggedUserActivity().getDrawable(R.drawable.green_progress_bar));
        } else if (indexOfHealth.getColorPlantState() == Color.YELLOW_NEGATIVE || indexOfHealth.getColorPlantState() == Color.YELLOW_POSITIVE) {
            progressBarIndexOfHealth.setBackgroundDrawable(LoggedUserActivity.getLoggedUserActivity().getDrawable(R.drawable.yellow_progress_bar));
        } else if (indexOfHealth.getColorPlantState() == Color.RED_NEGATIVE || indexOfHealth.getColorPlantState() == Color.RED_POSITIVE) {
            progressBarIndexOfHealth.setBackgroundDrawable(LoggedUserActivity.getLoggedUserActivity().getDrawable(R.drawable.progress_bar));
        }

        progressBarPhMeters = view.findViewById(R.id.progress_bar_phmeters);

        progressBarPhMeters.setMin(((Double) phMeters.getMinValueState()).intValue());
        progressBarPhMeters.setMax(((Double) phMeters.getMaxValueState()).intValue());
        progressBarPhMeters.setProgress(((Double) phMeters.getValueState()).intValue());

        phMeters.setColorPlantState();

        if (phMeters.getColorPlantState() == Color.GREEN) {
            progressBarPhMeters.setBackgroundDrawable(LoggedUserActivity.getLoggedUserActivity().getDrawable(R.drawable.green_progress_bar));
        } else if (phMeters.getColorPlantState() == Color.YELLOW_NEGATIVE || phMeters.getColorPlantState() == Color.YELLOW_POSITIVE) {
            progressBarPhMeters.setBackgroundDrawable(LoggedUserActivity.getLoggedUserActivity().getDrawable(R.drawable.yellow_progress_bar));
        } else if (phMeters.getColorPlantState() == Color.RED_NEGATIVE || phMeters.getColorPlantState() == Color.RED_POSITIVE) {
            progressBarPhMeters.setBackgroundDrawable(LoggedUserActivity.getLoggedUserActivity().getDrawable(R.drawable.progress_bar));
        }

        getSmallPlantStateList();
    }

    View.OnClickListener listenerInfoIndexOfHealth = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                    .setIcon(R.drawable.ic_baseline_privacy_tip_24)
                    .setTitle("Index Of Health")
                    .setMessage("L'indice di salute rappresenta il monitoraggio della salute generale della pianta calcolato in funzione dei dati rilevati dai sensori")
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
                    .setMessage("Il sensore del PH rileva il ph presente nel terreno, ossia la concentrazione degli ioni idrogeno nel terreno")
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

        sharedPreferences = LoggedUserActivity.getLoggedUserActivity().getSharedPreferences("plant_data", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city", null);

        if (city != null) {
            getOpenWeatherMapData(city);
        } else {
            getNormalSmallPlantStateList();
            getDangerSmallPlantStateList();
            getWarningSmallPlantStateList();
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

                } else {

                    getNormalSmallPlantStateList();
                    getDangerSmallPlantStateList();
                    getWarningSmallPlantStateList();

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

                getNormalSmallPlantStateList();
                getDangerSmallPlantStateList();
                getWarningSmallPlantStateList();
            }
        });
    }

    public static StateServices getWeatherService() {

        if (stateServices == null) {
            stateServices = ServiceGenerator.createService(StateServices.class);
        }

        return stateServices;
    }

    private void initializePhMeters(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 3;
                endingYellowValueState = 9;
                startingGreenValueState = 5;
                endingGreenValueState = 7;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 3;
                endingYellowValueState = 9;
                startingGreenValueState = 5;
                endingGreenValueState = 7;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 3;
                endingYellowValueState = 9;
                startingGreenValueState = 5;
                endingGreenValueState = 7;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 3;
                endingYellowValueState = 9;
                startingGreenValueState = 5;
                endingGreenValueState = 7;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 3;
                endingYellowValueState = 9;
                startingGreenValueState = 5;
                endingGreenValueState = 7;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 3;
                endingYellowValueState = 9;
                startingGreenValueState = 5;
                endingGreenValueState = 7;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 3;
                endingYellowValueState = 9;
                startingGreenValueState = 5;
                endingGreenValueState = 7;
                break;
            default:
                break;
        }

        phMeters = new PlantStateModel(
                "PH Meters",
                R.drawable.ph_sensor,
                plant.getPhMeters(),
                "Il sensore del PH rileva il ph presente nel terreno, ossia la concentrazione degli ioni idrogeno nel terreno",
                0,
                14,
                startingYellowValueState,
                endingYellowValueState,
                startingGreenValueState,
                endingGreenValueState);
    }

    private void initializeIndexOfHealth(String plantType) {

        double startingYellowValueState = 0;
        double endingYellowValueState = 0;

        double startingGreenValueState = 0;
        double endingGreenValueState = 0;

        switch (plantType) {
            case "Fiori Primaverili":
                startingYellowValueState = 60;
                endingYellowValueState = 102;
                startingGreenValueState = 80;
                endingGreenValueState = 101;
                break;
            case "Fiori Autunnali":
                startingYellowValueState = 60;
                endingYellowValueState = 102;
                startingGreenValueState = 80;
                endingGreenValueState = 101;
                break;
            case "Pianta Alimurgica":
                startingYellowValueState = 60;
                endingYellowValueState = 102;
                startingGreenValueState = 80;
                endingGreenValueState = 101;
                break;
            case "Pianta Grassa":
                startingYellowValueState = 60;
                endingYellowValueState = 102;
                startingGreenValueState = 80;
                endingGreenValueState = 101;
                break;
            case "Pianta Rampicante":
                startingYellowValueState = 60;
                endingYellowValueState = 102;
                startingGreenValueState = 80;
                endingGreenValueState = 101;
                break;
            case "Pianta Sempreverde":
                startingYellowValueState = 60;
                endingYellowValueState = 102;
                startingGreenValueState = 80;
                endingGreenValueState = 101;
                break;
            case "Pianta Tropicale":
                startingYellowValueState = 60;
                endingYellowValueState = 102;
                startingGreenValueState = 80;
                endingGreenValueState = 101;
                break;
            default:
                break;
        }

        indexOfHealth = new PlantStateModel(
                "Index Of Health",
                R.drawable.plant_healt,
                indexOfHealthCalculator(),
                "L'indice di salute rappresenta il monitoraggio della salute generale della pianta calcolato in funzione dei dati rilevati dai sensori",
                0,
                100,
                startingYellowValueState,
                endingYellowValueState,
                startingGreenValueState,
                endingGreenValueState);
    }

    private double indexOfHealthCalculator() {

        PlantStateModel temperatureAirState = LoggedUserActivity.getLoggedUserActivity().getTemperatureAir();
        PlantStateModel relativeMoistureAirState = LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureAir();
        PlantStateModel temperatureSoilState = LoggedUserActivity.getLoggedUserActivity().getTemperatureSoil();
        PlantStateModel relativeMoistureSoilState = LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureSoil();
        PlantStateModel lightIntensity = LoggedUserActivity.getLoggedUserActivity().getLightIntensity();

        double indexTemperatureAir = 0;
        double indexRelativeMoistureAir = 0;
        double indexTemperatureSoil = 0;
        double indexRelativeMoistureSoil = 0;
        double indexLightIntensity = 0;
        double indexPhMeters = 0;

        temperatureAirState.setColorPlantState();

        if (temperatureAirState.getColorPlantState() == Color.RED_NEGATIVE
                || temperatureAirState.getColorPlantState() == Color.YELLOW_NEGATIVE) {

            indexTemperatureAir = 100 - (power(
                    absoluteValue(temperatureAirState.getStartingGreenValueState() - temperatureAirState.getValueState()) /
                            absoluteValue(temperatureAirState.getStartingGreenValueState() - temperatureAirState.getMinValueState())) * 100);

        } else if (temperatureAirState.getColorPlantState() == Color.RED_POSITIVE
                || temperatureAirState.getColorPlantState() == Color.YELLOW_POSITIVE) {

            indexTemperatureAir = 100 - (power(
                    absoluteValue(temperatureAirState.getEndingGreenValueState() - temperatureAirState.getValueState()) /
                            absoluteValue(temperatureAirState.getEndingGreenValueState() - temperatureAirState.getMaxValueState())) * 100);

        } else if (temperatureAirState.getColorPlantState() == Color.GREEN) {

            indexTemperatureAir = 100;

        }

        relativeMoistureAirState.setColorPlantState();

        if (relativeMoistureAirState.getColorPlantState() == Color.RED_NEGATIVE
                || relativeMoistureAirState.getColorPlantState() == Color.YELLOW_NEGATIVE) {

            indexRelativeMoistureAir = 100 - (power(
                    absoluteValue(relativeMoistureAirState.getStartingGreenValueState() - relativeMoistureAirState.getValueState()) /
                            absoluteValue(relativeMoistureAirState.getStartingGreenValueState() - relativeMoistureAirState.getMinValueState())) * 100);

        } else if (relativeMoistureAirState.getColorPlantState() == Color.RED_POSITIVE
                || relativeMoistureAirState.getColorPlantState() == Color.YELLOW_POSITIVE) {

            indexRelativeMoistureAir = 100 - (power(
                    absoluteValue(relativeMoistureAirState.getEndingGreenValueState() - relativeMoistureAirState.getValueState()) /
                            absoluteValue(relativeMoistureAirState.getEndingGreenValueState() - relativeMoistureAirState.getMaxValueState())) * 100);

        } else if (relativeMoistureAirState.getColorPlantState() == Color.GREEN) {

            indexRelativeMoistureAir = 100;

        }

        temperatureSoilState.setColorPlantState();

        if (temperatureSoilState.getColorPlantState() == Color.RED_NEGATIVE
                || temperatureSoilState.getColorPlantState() == Color.YELLOW_NEGATIVE) {

            indexTemperatureSoil = 100 - (power(
                    absoluteValue(temperatureSoilState.getStartingGreenValueState() - temperatureSoilState.getValueState()) /
                            absoluteValue(temperatureSoilState.getStartingGreenValueState() - temperatureSoilState.getMinValueState())) * 100);

        } else if (temperatureSoilState.getColorPlantState() == Color.RED_POSITIVE
                || temperatureSoilState.getColorPlantState() == Color.YELLOW_POSITIVE) {

            indexTemperatureSoil = 100 - (power(
                    absoluteValue(temperatureSoilState.getEndingGreenValueState() - temperatureSoilState.getValueState()) /
                            absoluteValue(temperatureSoilState.getEndingGreenValueState() - temperatureSoilState.getMaxValueState())) * 100);

        } else if (temperatureSoilState.getColorPlantState() == Color.GREEN) {

            indexTemperatureSoil = 100;

        }

        relativeMoistureSoilState.setColorPlantState();

        if (relativeMoistureSoilState.getColorPlantState() == Color.RED_NEGATIVE
                || relativeMoistureSoilState.getColorPlantState() == Color.YELLOW_NEGATIVE) {

            indexRelativeMoistureSoil = 100 - (power(
                    absoluteValue(relativeMoistureSoilState.getStartingGreenValueState() - relativeMoistureSoilState.getValueState()) /
                            absoluteValue(relativeMoistureSoilState.getStartingGreenValueState() - relativeMoistureSoilState.getMinValueState())) * 100);

        } else if (relativeMoistureSoilState.getColorPlantState() == Color.RED_POSITIVE
                || relativeMoistureSoilState.getColorPlantState() == Color.YELLOW_POSITIVE) {

            indexRelativeMoistureSoil = 100 - (power(
                    absoluteValue(relativeMoistureSoilState.getEndingGreenValueState() - relativeMoistureSoilState.getValueState()) /
                            absoluteValue(relativeMoistureSoilState.getEndingGreenValueState() - relativeMoistureSoilState.getMaxValueState())) * 100);

        } else if (relativeMoistureSoilState.getColorPlantState() == Color.GREEN) {

            indexRelativeMoistureSoil = 100;

        }

        lightIntensity.setColorPlantState();

        if (lightIntensity.getColorPlantState() == Color.RED_NEGATIVE
                || lightIntensity.getColorPlantState() == Color.YELLOW_NEGATIVE) {

            indexLightIntensity = 100 - (power(
                    absoluteValue(lightIntensity.getStartingGreenValueState() - lightIntensity.getValueState()) /
                            absoluteValue(lightIntensity.getStartingGreenValueState() - lightIntensity.getMinValueState())) * 100);

        } else if (lightIntensity.getColorPlantState() == Color.RED_POSITIVE
                || lightIntensity.getColorPlantState() == Color.YELLOW_POSITIVE) {

            indexLightIntensity = 100 - (power(
                    absoluteValue(lightIntensity.getEndingGreenValueState() - lightIntensity.getValueState()) /
                            absoluteValue(lightIntensity.getEndingGreenValueState() - lightIntensity.getMaxValueState())) * 100);

        } else if (lightIntensity.getColorPlantState() == Color.GREEN) {

            indexLightIntensity = 100;

        }

        phMeters.setColorPlantState();

        if (phMeters.getColorPlantState() == Color.RED_NEGATIVE
                || phMeters.getColorPlantState() == Color.YELLOW_NEGATIVE) {

            indexPhMeters = 100 - (power(
                    absoluteValue(phMeters.getStartingGreenValueState() - lightIntensity.getValueState()) /
                            absoluteValue(phMeters.getStartingGreenValueState() - lightIntensity.getMinValueState())) * 100);

        } else if (phMeters.getColorPlantState() == Color.RED_POSITIVE
                || phMeters.getColorPlantState() == Color.YELLOW_POSITIVE) {

            indexPhMeters = 100 - (power(
                    absoluteValue(phMeters.getEndingGreenValueState() - phMeters.getValueState()) /
                            absoluteValue(phMeters.getEndingGreenValueState() - phMeters.getMaxValueState())) * 100);

        } else if (phMeters.getColorPlantState() == Color.GREEN) {

            indexPhMeters = 100;

        }

        double priorityTemperatureAir = 6;
        double priorityRelativeMoistureAir = 6;
        double priorityTemperatureSoil = 12;
        double priorityRelativeMoistureSoil = 12;
        double priorityLightSensor = 3;
        double priorityPhMeters = 12;

        double prioritySummation = priorityTemperatureAir + priorityRelativeMoistureAir + priorityTemperatureSoil + priorityRelativeMoistureSoil + priorityLightSensor + priorityPhMeters;

        double indexOfHealth =
                (indexTemperatureAir * priorityTemperatureAir +
                        indexRelativeMoistureAir * priorityRelativeMoistureAir +
                        indexTemperatureSoil * priorityTemperatureSoil +
                        indexRelativeMoistureSoil * priorityRelativeMoistureSoil +
                        indexLightIntensity * priorityLightSensor +
                        indexPhMeters * priorityPhMeters)
                        / (prioritySummation);

        return indexOfHealth;
    }

    private double absoluteValue(double number) {
        return number >= 0 ? number : -number;
    }

    double power(double alpha) {
        return alpha * alpha;
    }
}