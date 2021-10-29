package org.aldomanco.plantsensor.plant_state;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlantStateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlantStateFragment extends Fragment {

    private PlantStateAdapter adapter;

    private View view;

    private static PlantStateFragment plantStateFragment;

    private PlantModel plant;

    private PlantStateModel relativeMoistureSoil;
    private PlantStateModel relativeMoistureAir;
    private PlantStateModel temperatureSoil;
    private PlantStateModel temperatureAir;
    private PlantStateModel lightIntensity;

    private List<PlantStateModel> listPlantState;
    String[] arrayPlantTypes;
    ArrayAdapter<String> adapterPlantTypes;

    AutoCompleteTextView spinnerPlantType;
    EditText editTextPlantName;

    //private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";
    private final String basicUrlImage = "https://upload.wikimedia.org/wikipedia/commons/e/e6/Lol_circle.png";

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

        editTextPlantName = view.findViewById(R.id.edittext_plant_name_id);
        spinnerPlantType = view.findViewById(R.id.spinner_plant_type_id);
        arrayPlantTypes = getResources().getStringArray(R.array.plantTypes);
        adapterPlantTypes = new ArrayAdapter<>(requireContext(), R.layout.menu_plant_types, arrayPlantTypes);
        spinnerPlantType.setAdapter(adapterPlantTypes);

        getPlantStateList();
    }

    /**
     * Consente di recuperare tutti i post:
     * - streams: lista dei post dell'utente e dei suoi following
     * - favorites: sono i post a cui l'utente ha espresso la preferenza
     * Viene mandata una richiesta http per recuperati i dal server.
     */
    public void getPlantStateList() {

        plant = LoggedUserActivity.getPlant();

        initializePlantInfo(plant.getPlantName(), plant.getPlantType());

        temperatureAir = new PlantStateModel("Air Temperature", R.drawable.temperatura, plant.getTemperatureAir(), "desc");
        temperatureSoil = new PlantStateModel("Soil Temperature", R.drawable.temperatura_suolo, plant.getTemperatureSoil(), "desc");
        relativeMoistureAir = new PlantStateModel("Air Moisture", R.drawable.umidita, plant.getRelativeMoistureAir(), "desc");
        relativeMoistureSoil = new PlantStateModel("Soil Moisture", R.drawable.umidita_suolo, plant.getRelativeMoistureSoil(), "desc");
        lightIntensity = new PlantStateModel("Light Intensity", R.drawable.luminosita, plant.getLightIntensity(), "desc");

        listPlantState = new ArrayList<>();
        listPlantState.add(temperatureAir);
        listPlantState.add(temperatureSoil);
        listPlantState.add(relativeMoistureAir);
        listPlantState.add(relativeMoistureSoil);
        listPlantState.add(lightIntensity);

        initializeRecyclerView(listPlantState);

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

    private void initializePlantInfo(String plantName, String plantType){

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
}