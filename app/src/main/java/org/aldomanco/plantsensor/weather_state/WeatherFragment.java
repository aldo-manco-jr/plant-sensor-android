package org.aldomanco.plantsensor.weather_state;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment implements View.OnClickListener {

    private View view;

    private static WeatherFragment weatherFragment;

    private PlantStateAdapter adapter;

    private PlantModel plant;

    private List<PlantStateModel> listWeatherState;

    AutoCompleteTextView spinnerPlantLocation;

    FloatingActionButton buttonManualSelectionLocation;
    FloatingActionButton buttonAutomaticSelectionLocation;
    Button buttonSetLocation;
    Intent intentOpenMap;

    String city;
    String country;

    String[] arrayPlantLocations;
    ArrayAdapter<String> adapterPlantLocations;

    public WeatherFragment() {
    }

    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        weatherFragment = this;

        buttonAutomaticSelectionLocation = view.findViewById(R.id.button_automatic_selection_location);
        buttonAutomaticSelectionLocation.setOnClickListener(this);

        buttonManualSelectionLocation = view.findViewById(R.id.button_manual_selection_location);
        buttonManualSelectionLocation.setOnClickListener(this);

        buttonSetLocation = view.findViewById(R.id.button_set_location_weather_section);
        buttonSetLocation.setOnClickListener(this);

        spinnerPlantLocation = view.findViewById(R.id.spinner_plant_location_id_weather_section);

        arrayPlantLocations = getResources().getStringArray(R.array.plantLocation);
        adapterPlantLocations = new ArrayAdapter<>(LoggedUserActivity.getLoggedUserActivity(), R.layout.menu_plant_location, arrayPlantLocations);
        spinnerPlantLocation.setAdapter(adapterPlantLocations);

        getWeatherStateList();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_automatic_selection_location:
                intentOpenMap = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(intentOpenMap, 1);
                break;
            case R.id.button_manual_selection_location:
                intentOpenMap = new Intent(getActivity(), ManualMapsActivity.class);
                startActivityForResult(intentOpenMap, 1);
                break;
            case R.id.button_set_location_weather_section:

                if (!spinnerPlantLocation.getText().toString().isEmpty()) {
                    city = spinnerPlantLocation.getText().toString().trim().split(",")[0];
                    country = spinnerPlantLocation.getText().toString().trim().split(" ")[1];

                    LoggedUserActivity.getPlant().setPlantLocationCity(city);
                    LoggedUserActivity.getPlant().setPlantLocationCountry(country);

                    Toast.makeText(getActivity(), LoggedUserActivity.getPlant().getPlantLocationCity() + "", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), LoggedUserActivity.getPlant().getPlantLocationCountry() + "", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 1:
                if (resultCode == Activity.RESULT_OK) {

                    city = data.getStringExtra("city");
                    country = data.getStringExtra("country");

                    spinnerPlantLocation.setText(city + ", " + country);
                }
                break;
            default:
                break;
        }
    }

    public void getWeatherStateList() {

        plant = LoggedUserActivity.getPlant();

        listWeatherState = new ArrayList<>();
        listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
        listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationProbability());
        listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
        listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
        listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
        listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());
        listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastIndexPollution());

        initializeRecyclerView(listWeatherState);

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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_weather_state);

        if (adapter == null) {
            adapter = new PlantStateAdapter(listWeatherState);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();

        arrayPlantLocations = getResources().getStringArray(R.array.plantLocation);
        adapterPlantLocations = new ArrayAdapter<>(LoggedUserActivity.getLoggedUserActivity(), R.layout.menu_plant_types, arrayPlantLocations);
        spinnerPlantLocation.setAdapter(adapterPlantLocations);
    }
}