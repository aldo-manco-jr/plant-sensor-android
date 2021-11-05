package org.aldomanco.plantsensor.watering_state;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.plant_state.PlantModel;
import org.aldomanco.plantsensor.plant_state.PlantStateAdapter;
import org.aldomanco.plantsensor.plant_state.PlantStateFragment;
import org.aldomanco.plantsensor.plant_state.PlantStateModel;

import java.util.ArrayList;
import java.util.List;

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

    private PlantStateModel relativeMoistureSoil;
    private PlantStateModel relativeMoistureAir;
    private PlantStateModel temperatureSoil;
    private PlantStateModel temperatureAir;
    private PlantStateModel lightIntensity;

    private PlantStateModel forecastWeatherState;
    private PlantStateModel forecastPrecipitationAmount;
    private PlantStateModel forecastPrecipitationProbability;
    private PlantStateModel forecastHumidityAir;
    private PlantStateModel forecastTemperatureAir;
    private PlantStateModel forecastWindSpeed;
    private PlantStateModel forecastPressureAir;
    private PlantStateModel forecastIndexPollution;

    private List<PlantStateModel> listSmallPlantState;

    PlantModel plant;

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

        initializeTemperatureAir(plant.getPlantType());
        initializeTemperatureSoil(plant.getPlantType());
        initializeRelativeMoistureAir(plant.getPlantType());
        initializeRelativeMoistureSoil(plant.getPlantType());
        initializeLightIntensity(plant.getPlantType());

        initializeForecastPrecipitationAmount(plant.getPlantType());
        initializeForecastPrecipitationProbability(plant.getPlantType());
        initializeForecastRelativeMoistureAir(plant.getPlantType());
        initializeForecastPressureAir(plant.getPlantType());
        initializeForecastIndexPollution(plant.getPlantType());
        initializeForecastTemperatureAir(plant.getPlantType());
        initializeForecastWindSpeed(plant.getPlantType());

        listSmallPlantState = new ArrayList<>();

        listSmallPlantState.add(temperatureAir);
        listSmallPlantState.add(temperatureSoil);
        listSmallPlantState.add(relativeMoistureAir);
        listSmallPlantState.add(relativeMoistureSoil);
        listSmallPlantState.add(lightIntensity);

        listSmallPlantState.add(forecastPrecipitationAmount);
        listSmallPlantState.add(forecastPrecipitationProbability);
        listSmallPlantState.add(forecastHumidityAir);
        listSmallPlantState.add(forecastTemperatureAir);
        listSmallPlantState.add(forecastWindSpeed);
        listSmallPlantState.add(forecastPressureAir);
        listSmallPlantState.add(forecastIndexPollution);

        initializeRecyclerView(listSmallPlantState);

            /*Call<GetAllUsersResponse> httpRequest = LoggedUserActivity.getUsersService().getAllUsers();

            httpRequest.enqueue(new Callback<GetAllUsersResponse>() {
                @Override
                public void onResponse(Call<GetAllUsersResponse> call, Response<GetAllUsersResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null : "body() non doveva essere null";

                        initializeRecyclerView(response.body().getAllUsers());
                    }
                }

                @Override
                public void onFailure(Call<GetAllUsersResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }*/
    }

    private void initializeRecyclerView(List<PlantStateModel> listPlantState) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_watering);

        if (adapterSmallPlantState == null) {
            adapterSmallPlantState = new SmallPlantStateAdapter(listPlantState);
        }

        recyclerView.setAdapter(adapterSmallPlantState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // ---------------------------

    private void initializeTemperatureAir(String plantType) {

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

        temperatureAir = new PlantStateModel("Air Temperature", R.drawable.temperatura, plant.getTemperatureAir(), "desc", -40, 50, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeTemperatureSoil(String plantType) {

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

        temperatureSoil = new PlantStateModel("Soil Temperature", R.drawable.temperatura_suolo, plant.getTemperatureSoil(), "desc", -40, 50, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeRelativeMoistureAir(String plantType) {

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

        relativeMoistureAir = new PlantStateModel("Air Moisture", R.drawable.umidita, plant.getRelativeMoistureAir(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeRelativeMoistureSoil(String plantType) {

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

        relativeMoistureSoil = new PlantStateModel("Soil Moisture", R.drawable.umidita_suolo, plant.getRelativeMoistureSoil(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeLightIntensity(String plantType) {

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

        forecastPrecipitationAmount = new PlantStateModel("Precipitation Amount", R.drawable.luminosita, plant.getForecastPrecipitationAmount(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeForecastPrecipitationProbability(String plantType) {

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

        forecastPrecipitationProbability = new PlantStateModel("Precipitation Probability", R.drawable.luminosita, plant.getForecastPrecipitationProbability(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
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

        forecastHumidityAir = new PlantStateModel("Humidity Air", R.drawable.luminosita, plant.getForecastHumidityAir(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
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

        forecastTemperatureAir = new PlantStateModel("Temperature Air", R.drawable.luminosita, plant.getForecastTemperatureAir(), "desc", -40, 50, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
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

        forecastWindSpeed = new PlantStateModel("Wind Speed", R.drawable.luminosita, plant.getForecastWindSpeed(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
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

        forecastPressureAir = new PlantStateModel("Pressure Air", R.drawable.luminosita, plant.getForecastPressureAir(), "desc", 0, 100, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }

    private void initializeForecastIndexPollution(String plantType) {

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

        forecastIndexPollution = new PlantStateModel("Index Pollution", R.drawable.luminosita, plant.getForecastIndexPollution(), "desc", 1, 10, startingYellowValueState, endingYellowValueState, startingGreenValueState, endingGreenValueState);
    }
}