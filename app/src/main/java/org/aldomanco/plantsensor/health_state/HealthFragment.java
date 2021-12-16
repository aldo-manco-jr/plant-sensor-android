package org.aldomanco.plantsensor.health_state;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.plant_state.PlantModel;
import org.aldomanco.plantsensor.plant_state.PlantStateModel;
import org.aldomanco.plantsensor.watering_state.SmallPlantStateAdapter;
import org.aldomanco.plantsensor.watering_state.WateringFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthFragment extends Fragment {

    private View view;
    private static HealthFragment healthFragment;

    SmallPlantStateAdapter adapterSmallPlantState;

    RecyclerView recyclerViewDangerSmallPlantState;
    RecyclerView recyclerViewWarningSmallPlantState;
    RecyclerView recyclerViewNormalSmallPlantState;

    private List<PlantStateModel> listDangerSmallPlantState;
    private List<PlantStateModel> listWarningSmallPlantState;
    private List<PlantStateModel> listNormalSmallPlantState;

    PlantModel plant;

    public HealthFragment() { }

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        healthFragment = this;

        plant = LoggedUserActivity.getPlant();

        getDangerSmallPlantStateList();
        getWarningSmallPlantStateList();
        getNormalSmallPlantStateList();
    }

    public void getDangerSmallPlantStateList() {

        listDangerSmallPlantState = new ArrayList<>();

        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureAir());
        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureSoil());
        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureAir());
        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureSoil());
        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getLightIntensity());

      /*  listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationProbability());
        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());
        listDangerSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastIndexPollution());
*/
        initializeDangerRecyclerView(listDangerSmallPlantState);
    }

    public void getWarningSmallPlantStateList() {

        listWarningSmallPlantState = new ArrayList<>();

        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureAir());
        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureSoil());
        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureAir());
        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureSoil());
        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getLightIntensity());

        /*listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationProbability());
        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());
        listWarningSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastIndexPollution());
*/
        initializeWarningRecyclerView(listWarningSmallPlantState);
    }

    public void getNormalSmallPlantStateList() {

        listNormalSmallPlantState = new ArrayList<>();

        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureAir());
        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getTemperatureSoil());
        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureAir());
        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getRelativeMoistureSoil());
        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getLightIntensity());

       /* listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationAmount());
        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPrecipitationProbability());
        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastHumidityAir());
        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastTemperatureAir());
        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastWindSpeed());
        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastPressureAir());
        listNormalSmallPlantState.add(LoggedUserActivity.getLoggedUserActivity().getForecastIndexPollution());
*/
        initializeNormalRecyclerView(listNormalSmallPlantState);
    }

    private void initializeDangerRecyclerView(List<PlantStateModel> listPlantState) {

        recyclerViewDangerSmallPlantState = view.findViewById(R.id.recyclerview_health_danger);

        if (adapterSmallPlantState == null) {
            adapterSmallPlantState = new SmallPlantStateAdapter(listDangerSmallPlantState);
        }

        recyclerViewDangerSmallPlantState.setAdapter(adapterSmallPlantState);
        recyclerViewDangerSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initializeWarningRecyclerView(List<PlantStateModel> listPlantState) {

        recyclerViewWarningSmallPlantState = view.findViewById(R.id.recyclerview_health_warning);

        if (adapterSmallPlantState == null) {
            adapterSmallPlantState = new SmallPlantStateAdapter(listWarningSmallPlantState);
        }

        recyclerViewWarningSmallPlantState.setAdapter(adapterSmallPlantState);
        recyclerViewWarningSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initializeNormalRecyclerView(List<PlantStateModel> listPlantState) {

        recyclerViewNormalSmallPlantState = view.findViewById(R.id.recyclerview_health_normal);

        if (adapterSmallPlantState == null) {
            adapterSmallPlantState = new SmallPlantStateAdapter(listNormalSmallPlantState);
        }

        recyclerViewNormalSmallPlantState.setAdapter(adapterSmallPlantState);
        recyclerViewNormalSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}