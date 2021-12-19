package org.aldomanco.plantsensor.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.weather_state.ManualMapsActivity;
import org.aldomanco.plantsensor.weather_state.MapsActivity;

public class SetPlantInfoActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText editTextPlantName;
    AutoCompleteTextView spinnerPlantType;
    AutoCompleteTextView spinnerPlantLocation;

    String plantName;
    String plantType;
    String plantLocationCity;
    String plantLocationCountry;

    Button buttonSetPlantInfo;
    ImageView buttonSetAutomaticLocation;
    ImageView buttonSetManualLocation;
    Intent intentOpenMap;

    String city;
    String country;

    PlantModel plant;

    String[] arrayPlantLocations;
    ArrayAdapter<String> adapterPlantLocations;

    String[] arrayPlantTypes;
    ArrayAdapter<String> adapterPlantTypes;

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

        plant = new PlantModel(
                1,
                "Plant Name",
                "D",
                "Isernia",
                "Italy",
                "Aldo",
                2,
                30,
                2.3,
                4.3,
                5
        );

        if (plant.getPlantName()==null || plant.getPlantName().isEmpty()){
            plantName="";
        }else {
            plantName=plant.getPlantName();
        }

        if (plant.getPlantType()==null || plant.getPlantType().isEmpty()){
            plantType="";
        }else {
            plantType=plant.getPlantType();
        }

        if (plant.getPlantLocationCity()==null || plant.getPlantName().isEmpty()){
            plantLocationCity="";
        }else {
            plantLocationCity = plant.getPlantLocationCity();
        }

        if(plant.getPlantLocationCountry()==null || plant.getPlantLocationCountry().isEmpty()){
            plantLocationCountry="";
        }else {
            plantLocationCountry = plant.getPlantLocationCountry();
        }

        initializePlantInfo(plantName, plantType, plantLocationCity, plantLocationCountry);
    }

    private void initializePlantInfo(String plantName, String plantType, String plantCity, String plantCountry){

        editTextPlantName.setText(plantName);

        int indexPlantType = -1;

        switch (plantType){
            case "A":
                indexPlantType = 0;
                break;
            case "B":
                indexPlantType = 1;
                break;
            case "C":
                indexPlantType = 2;
                break;
            case "D":
                indexPlantType = 3;
                break;
            default:
                break;
        }

        spinnerPlantType.setListSelection(indexPlantType);
        spinnerPlantType.setText(plantType);

        int indexPlantLocation = -1;

        String plantLocation = plantCity + ", " + plantCountry;

        switch (plantLocation){
            case "Isernia, Italy":
                indexPlantLocation = 0;
                break;
            case "Roma, Italy":
                indexPlantLocation = 1;
                break;
            case "Milano, Italy":
                indexPlantLocation = 2;
                break;
            case "Torino, Italy":
                indexPlantLocation = 3;
                break;
            default:
                break;
        }

        spinnerPlantLocation.setListSelection(indexPlantLocation);
        spinnerPlantLocation.setText(plantLocation);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.button_set_plant_info_initial:
                plant.setPlantName(editTextPlantName.getText().toString().trim());
                plant.setPlantType(spinnerPlantType.getText().toString().trim());
                plant.setPlantLocationCity(spinnerPlantLocation.getText().toString().trim().split(",")[0]);
                plant.setPlantLocationCountry(spinnerPlantLocation.getText().toString().trim().split(" ")[1]);
                break;
            case R.id.button_drawable_right_gps_initial:
                intentOpenMap = new Intent(this, MapsActivity.class);
                startActivityForResult(intentOpenMap, 1);
                break;
            case R.id.button_drawable_right_maps_initial:
                intentOpenMap = new Intent(this, ManualMapsActivity.class);
                startActivityForResult(intentOpenMap, 1);
                break;
            default:
                break;
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