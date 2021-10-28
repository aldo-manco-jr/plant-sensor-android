package org.aldomanco.plantsensor.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.plant_state.PlantModel;
import org.aldomanco.plantsensor.plant_state.PlantStateModel;

import java.util.List;

public class SetPlantInfoActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText editTextPlantName;
    AutoCompleteTextView spinnerPlantType;
    AutoCompleteTextView spinnerPlantLocation;

    String plantName;
    String plantType;
    String plantLocationCity;
    String plantLocationCountry;

    Button buttonSetPlantInfo;

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

        arrayPlantTypes = getResources().getStringArray(R.array.plantTypes);
        adapterPlantTypes = new ArrayAdapter<>(this, R.layout.menu_plant_types, arrayPlantTypes);
        spinnerPlantType.setAdapter(adapterPlantTypes);

        arrayPlantLocations = getResources().getStringArray(R.array.plantLocation);
        adapterPlantLocations = new ArrayAdapter<>(this, R.layout.menu_plant_location, arrayPlantLocations);
        spinnerPlantLocation.setAdapter(adapterPlantLocations);

        plant = new PlantModel(
                2,
                "plant2",
                "B",
                "Roma",
                "Italy",
                "aldo",
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
                Toast.makeText(this, editTextPlantName.getText().toString().trim(),Toast.LENGTH_LONG).show();
                Toast.makeText(this, spinnerPlantType.getText().toString().trim(),Toast.LENGTH_LONG).show();
                Toast.makeText(this, spinnerPlantLocation.getText().toString().trim(),Toast.LENGTH_LONG).show();
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
}