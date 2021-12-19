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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.models.PlantStateModel;

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

        listSmallPlantState = new ArrayList<>();

        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureAir());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureSoil());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureAir());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureSoil());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getLightIntensity());

        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
        listSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());

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

        recyclerViewSmallPlantState = view.findViewById(R.id.recyclerview_watering);

        if (adapterSmallPlantState == null) {
            adapterSmallPlantState = new SmallPlantStateAdapter(listPlantState);
        }

        recyclerViewSmallPlantState.setAdapter(adapterSmallPlantState);
        recyclerViewSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}