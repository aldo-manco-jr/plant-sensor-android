package org.aldomanco.plantsensor.plant_state;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.models.PlantStateModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlantStateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlantStateFragment extends Fragment implements View.OnTouchListener {

    private PlantStateAdapter adapter;

    private View view;

    private static PlantStateFragment plantStateFragment;

    private PlantModel plant;

    private List<PlantStateModel> listPlantState;
    String[] arrayPlantTypes;
    ArrayAdapter<String> adapterPlantTypes;

    AutoCompleteTextView spinnerPlantType;
    EditText editTextPlantName;

    //private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";
    private final String basicUrlImage = "https://upload.wikimedia.org/wikipedia/commons/e/e6/Lol_circle.png";

    private SharedPreferences sharedPreferences;

    public static PlantStateFragment getPlantStateFragment() {
        return plantStateFragment;
    }

    public static PlantStateFragment newInstance() {
        PlantStateFragment fragment = new PlantStateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plant_state, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        plantStateFragment = this;

        sharedPreferences = LoggedUserActivity.getLoggedUserActivity().getSharedPreferences("plant_data", Context.MODE_PRIVATE);

        editTextPlantName = view.findViewById(R.id.edittext_plant_name_id);
        editTextPlantName.setOnTouchListener(this);
        spinnerPlantType = view.findViewById(R.id.spinner_plant_type_id);
        arrayPlantTypes = getResources().getStringArray(R.array.plantTypes);
        adapterPlantTypes = new ArrayAdapter<>(requireContext(), R.layout.menu_plant_types, arrayPlantTypes);
        spinnerPlantType.setAdapter(adapterPlantTypes);

        spinnerPlantType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                switch (i) {
                    case 0:
                        editor.putString("plant_type", "Fiori Primaverili");
                        break;
                    case 1:
                        editor.putString("plant_type", "Fiori Autunnali");
                        break;
                    case 2:
                        editor.putString("plant_type", "Pianta Alimurgica");
                        break;
                    case 3:
                        editor.putString("plant_type", "Pianta Grassa");
                        break;
                    case 4:
                        editor.putString("plant_type", "Pianta Rampicante");
                        break;
                    case 5:
                        editor.putString("plant_type", "Pianta Sempreverde");
                        break;
                    case 6:
                        editor.putString("plant_type", "Pianta Tropicale");
                        break;
                    default:
                        break;
                }

                editor.apply();
            }
        });

        getPlantStateList();
    }

    public void getPlantStateList() {

        plant = LoggedUserActivity.getPlant();

        initializePlantInfo(plant.getPlantName(), plant.getPlantType());

        listPlantState = new ArrayList<>();
        listPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureAir());
        listPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureSoil());
        listPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureAir());
        listPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureSoil());
        listPlantState.add(LoggedUserActivity.getLoggedUserActivity().getLightIntensity());

        initializeRecyclerView(listPlantState);
    }

    private void initializePlantInfo(String plantName, String plantType) {

        editTextPlantName.setText(plantName);

        int indexPlantType = -1;

        switch (plantType) {
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
    }

    private void initializeRecyclerView(List<PlantStateModel> listPlantState) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_plant_state);

        if (adapter == null) {
            adapter = new PlantStateAdapter(listPlantState);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayPlantTypes = getResources().getStringArray(R.array.plantTypes);
        adapterPlantTypes = new ArrayAdapter<>(requireContext(), R.layout.menu_plant_types, arrayPlantTypes);
        spinnerPlantType.setAdapter(adapterPlantTypes);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        boolean isTouched = event.getAction() == MotionEvent.ACTION_DOWN;
        EditText editText = (EditText) v;

        if (isTouched) {
            final int eyeIconSize = 24;
            final int passwordFieldWidth = editText.getWidth();
            final int eyeWidthSize = passwordFieldWidth - eyeIconSize;

            if (event.getRawX() >= eyeWidthSize) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("plant_name", editText.getText().toString().trim());
                editor.apply();

                return true;
            }
        }

        return false;
    }
}