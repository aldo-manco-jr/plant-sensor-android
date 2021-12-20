package org.aldomanco.plantsensor.weather_state;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.models.http_response_weather.OpenWeatherMapJSON;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.plant_state.PlantStateAdapter;
import org.aldomanco.plantsensor.models.PlantStateModel;
import org.aldomanco.plantsensor.services.ServiceGenerator;
import org.aldomanco.plantsensor.services.StateServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private static StateServices stateServices;

    private SharedPreferences sharedPreferences;

    public static double INFINITE = 2_147_483_647;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private String currentLocationLabel;

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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LoggedUserActivity.getLoggedUserActivity());

        getWeatherStateList();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_automatic_selection_location:
                getCurrentLocation();
                break;
            case R.id.button_manual_selection_location:
                intentOpenMap = new Intent(getActivity(), ManualMapsActivity.class);
                startActivityForResult(intentOpenMap, 1);
                break;
            case R.id.button_set_location_weather_section:

                if (!spinnerPlantLocation.getText().toString().isEmpty()) {

                    String editTextCity = spinnerPlantLocation.getText().toString().trim();

                    if (editTextCity.contains(",")) {
                        city = editTextCity.split(",")[0];
                        country = editTextCity.split(",")[1].replace(" ", "");
                    } else {
                        city = editTextCity;
                    }

                    LoggedUserActivity.getPlant().setPlantLocationCity(city);
                    LoggedUserActivity.getPlant().setPlantLocationCountry(country);

                    getOpenWeatherMapData(city);
                }

                break;
            default:
                break;
        }
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(LoggedUserActivity.getLoggedUserActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location currentLocation = task.getResult();

                    if (currentLocation != null) {

                        Geocoder geocoder = new Geocoder(LoggedUserActivity.getLoggedUserActivity(), Locale.getDefault());

                        List<Address> listAddresses = null;

                        try {

                            listAddresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);

                            city = listAddresses.get(0).getLocality();
                            country = listAddresses.get(0).getCountryName();
                            currentLocationLabel = city + ", " + country;

                            spinnerPlantLocation.setText(currentLocationLabel);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } else {
            ActivityCompat.requestPermissions(LoggedUserActivity.getLoggedUserActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
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

        }
    }

    public void getWeatherStateList() {

        plant = LoggedUserActivity.getPlant();

        sharedPreferences = LoggedUserActivity.getLoggedUserActivity().getSharedPreferences("city", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city", null);

        if (city != null) {

            spinnerPlantLocation.setText(city);
            getOpenWeatherMapData(city);

            listWeatherState = new ArrayList<>();

            listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
            listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
            listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
            listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
            listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastSnowAmount());
            listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());

            initializeRecyclerView(listWeatherState);
        }
    }

    private void initializeRecyclerView(List<PlantStateModel> listPlantState) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_weather_state);

        if (adapter == null) {
            adapter = new PlantStateAdapter(listWeatherState);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getOpenWeatherMapData(String locationCity) {

        Call<OpenWeatherMapJSON> httpRequest = getStateServices().getWeatherData(locationCity);

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

                    listWeatherState = new ArrayList<>();

                    listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
                    listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
                    listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
                    listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
                    listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastSnowAmount());
                    listWeatherState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("city", locationCity);
                    editor.commit();

                    initializeRecyclerView(listWeatherState);

                } else if(response.code()==404){
                    new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                            .setIcon(android.R.drawable.stat_notify_error)
                            .setTitle("Invalid Location")
                            .setMessage("Please enter an existing city name, the one entered not exists.")
                            .setPositiveButton("OK", null).show();

                }else {
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

                listWeatherState = new ArrayList<>();

                initializeRecyclerView(listWeatherState);

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

    @Override
    public void onResume() {
        super.onResume();

        arrayPlantLocations = getResources().getStringArray(R.array.plantLocation);
        adapterPlantLocations = new ArrayAdapter<>(LoggedUserActivity.getLoggedUserActivity(), R.layout.menu_plant_types, arrayPlantLocations);
        spinnerPlantLocation.setAdapter(adapterPlantLocations);
    }
}