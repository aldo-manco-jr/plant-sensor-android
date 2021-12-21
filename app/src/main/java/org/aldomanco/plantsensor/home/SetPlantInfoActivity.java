package org.aldomanco.plantsensor.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.weather_state.ManualMapsActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SetPlantInfoActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText editTextPlantName;
    AutoCompleteTextView spinnerPlantType;
    AutoCompleteTextView spinnerPlantLocation;

    String plantName;
    String plantType;
    String plantLocationCity;

    Button buttonSetPlantInfo;
    ImageView buttonSetAutomaticLocation;
    ImageView buttonSetManualLocation;
    Intent intentOpenMap;

    String city;
    String country;

    String[] arrayPlantLocations;
    ArrayAdapter<String> adapterPlantLocations;

    String[] arrayPlantTypes;
    ArrayAdapter<String> adapterPlantTypes;

    private String currentLocationLabel;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_plant_info);

        editTextPlantName = findViewById(R.id.edittext_plant_name_id_initial);
        spinnerPlantType = findViewById(R.id.spinner_plant_type_id_initial);
        spinnerPlantLocation = findViewById(R.id.spinner_plant_location_id_initial);

        buttonSetPlantInfo = findViewById(R.id.button_set_plant_info_initial);
        buttonSetPlantInfo.setOnClickListener(this);

        buttonSetAutomaticLocation = findViewById(R.id.button_drawable_right_gps_initial);
        buttonSetAutomaticLocation.setOnClickListener(this);

        buttonSetManualLocation = findViewById(R.id.button_drawable_right_maps_initial);
        buttonSetManualLocation.setOnClickListener(this);

        arrayPlantTypes = getResources().getStringArray(R.array.plantTypes);
        adapterPlantTypes = new ArrayAdapter<>(this, R.layout.menu_plant_types, arrayPlantTypes);
        spinnerPlantType.setAdapter(adapterPlantTypes);

        arrayPlantLocations = getResources().getStringArray(R.array.plantLocation);
        adapterPlantLocations = new ArrayAdapter<>(this, R.layout.menu_plant_location, arrayPlantLocations);
        spinnerPlantLocation.setAdapter(adapterPlantLocations);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        sharedPreferences = getSharedPreferences("plant_data", Context.MODE_PRIVATE);
        plantName = sharedPreferences.getString("plant_name", "");
        plantType = sharedPreferences.getString("plant_type", "");
        plantLocationCity = sharedPreferences.getString("city", "");

        initializePlantInfo(plantName, plantType, plantLocationCity);
    }

    private void initializePlantInfo(String plantName, String plantType, String plantCity){

        editTextPlantName.setText(plantName);

        int indexPlantType = -1;

        switch (plantType){
            case "Fiori Primaverili":
                indexPlantType = 0;
                break;
            case "Fiori Autunnali":
                indexPlantType = 1;
                break;
            case "Pianta Alimurgica":
                indexPlantType = 2;
                break;
            case "Pianta Grassa":
                indexPlantType = 3;
                break;
            case "Pianta Rampicante":
                indexPlantType = 4;
                break;
            case "Pianta Sempreverde":
                indexPlantType = 5;
                break;
            case "Pianta Tropicale":
                indexPlantType = 6;
                break;
            default:
                break;
        }

        spinnerPlantType.setListSelection(indexPlantType);
        spinnerPlantType.setText(plantType);

        int indexPlantLocation = -1;

        switch (plantCity){
            case "Isernia":
                indexPlantLocation = 0;
                break;
            case "Roma":
                indexPlantLocation = 1;
                break;
            case "Milano":
                indexPlantLocation = 2;
                break;
            case "Torino":
                indexPlantLocation = 3;
                break;
            default:
                break;
        }

        spinnerPlantLocation.setListSelection(indexPlantLocation);
        spinnerPlantLocation.setText(plantCity);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.button_set_plant_info_initial:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("plant_name", editTextPlantName.getText().toString().trim());
                editor.putString("plant_type", spinnerPlantType.getText().toString().trim());
                editor.putString("city", spinnerPlantLocation.getText().toString().trim().split(",")[0]);
                editor.apply();

                break;
            case R.id.button_drawable_right_gps_initial:
                getCurrentLocation();
                break;
            case R.id.button_drawable_right_maps_initial:
                intentOpenMap = new Intent(this, ManualMapsActivity.class);
                startActivityForResult(intentOpenMap, 1);
                break;
            default:
                break;
        }
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location currentLocation = task.getResult();

                    if (currentLocation != null) {

                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        arrayPlantTypes = getResources().getStringArray(R.array.plantTypes);
        adapterPlantTypes = new ArrayAdapter<>(this, R.layout.menu_plant_types, arrayPlantTypes);
        spinnerPlantType.setAdapter(adapterPlantTypes);

        arrayPlantLocations = getResources().getStringArray(R.array.plantLocation);
        adapterPlantLocations = new ArrayAdapter<>(this, R.layout.menu_plant_types, arrayPlantLocations);
        spinnerPlantLocation.setAdapter(adapterPlantLocations);
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
}