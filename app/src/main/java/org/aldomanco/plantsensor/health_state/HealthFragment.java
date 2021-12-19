package org.aldomanco.plantsensor.health_state;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.models.PlantModel;
import org.aldomanco.plantsensor.models.PlantStateModel;
import org.aldomanco.plantsensor.watering_state.SmallPlantStateAdapter;

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

    private List<PlantStateModel> listSmallPlantState;

    private List<PlantStateModel> listDangerSmallPlantState;
    private List<PlantStateModel> listWarningSmallPlantState;
    private List<PlantStateModel> listNormalSmallPlantState;

    PlantModel plant;

    ImageButton infoIndexOfHealth;
    ImageButton infoPhMeters;

    public HealthFragment() {
    }

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

        infoIndexOfHealth = view.findViewById(R.id.info_index_of_health);
        infoPhMeters = view.findViewById(R.id.info_ph_meters);

        infoIndexOfHealth.setOnClickListener(listenerInfoIndexOfHealth);
        infoPhMeters.setOnClickListener(listenerInfoPhMeters);

        plant = LoggedUserActivity.getPlant();

        getSmallPlantStateList();

        getNormalSmallPlantStateList();
        getDangerSmallPlantStateList();
        getWarningSmallPlantStateList();
    }

    View.OnClickListener listenerInfoIndexOfHealth = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                    .setIcon(R.drawable.ic_baseline_privacy_tip_24)
                    .setTitle("Index Of Health")
                    .setMessage("index of health")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    };

    View.OnClickListener listenerInfoPhMeters = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                    .setIcon(R.drawable.ic_baseline_privacy_tip_24)
                    .setTitle("PH Meters")
                    .setMessage("ph meters")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    };

    public void getSmallPlantStateList() {

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
    }

    public void getDangerSmallPlantStateList() {

        listDangerSmallPlantState = new ArrayList<>();

        for (PlantStateModel plantState : listSmallPlantState) {

            if (((double) plantState.getValueState()) > ((double) plantState.getEndingYellowValueState())
                    || ((double) plantState.getValueState()) < ((double) plantState.getStartingYellowValueState())) {

                listDangerSmallPlantState.add(plantState);
            }
        }

        initializeDangerRecyclerView(listDangerSmallPlantState);
    }

    public void getWarningSmallPlantStateList() {

        listWarningSmallPlantState = new ArrayList<>();

        for (PlantStateModel plantState : listSmallPlantState) {

            if ((((double) plantState.getValueState()) > ((double) plantState.getStartingYellowValueState())
                    && ((double) plantState.getValueState()) < ((double) plantState.getStartingGreenValueState()))
                    || (((double) plantState.getValueState()) > ((double) plantState.getEndingGreenValueState())
                    && ((double) plantState.getValueState()) < ((double) plantState.getEndingYellowValueState()))) {

                listWarningSmallPlantState.add(plantState);
            }
        }

        initializeWarningRecyclerView(listWarningSmallPlantState);
    }

    public void getNormalSmallPlantStateList() {

        listNormalSmallPlantState = new ArrayList<>();

        for (PlantStateModel plantState : listSmallPlantState) {

            if (((double) plantState.getValueState()) > ((double) plantState.getStartingGreenValueState())
                    || ((double) plantState.getValueState()) < ((double) plantState.getEndingGreenValueState())) {

                listNormalSmallPlantState.add(plantState);
            }
        }

        initializeNormalRecyclerView(listNormalSmallPlantState);
    }

    private void initializeDangerRecyclerView(List<PlantStateModel> listDangerSmallPlantState) {

        recyclerViewDangerSmallPlantState = view.findViewById(R.id.recyclerview_health_danger);

        if (adapterSmallPlantState == null) {
            adapterSmallPlantState = new SmallPlantStateAdapter(listDangerSmallPlantState);
        }

        recyclerViewDangerSmallPlantState.setAdapter(adapterSmallPlantState);
        recyclerViewDangerSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initializeWarningRecyclerView(List<PlantStateModel> listWarningSmallPlantState) {

        recyclerViewWarningSmallPlantState = view.findViewById(R.id.recyclerview_health_warning);

        if (adapterSmallPlantState == null) {
            adapterSmallPlantState = new SmallPlantStateAdapter(listWarningSmallPlantState);
        }

        recyclerViewWarningSmallPlantState.setAdapter(adapterSmallPlantState);
        recyclerViewWarningSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initializeNormalRecyclerView(List<PlantStateModel> listNormalSmallPlantState) {

        recyclerViewNormalSmallPlantState = view.findViewById(R.id.recyclerview_health_normal);

        if (adapterSmallPlantState == null) {
            adapterSmallPlantState = new SmallPlantStateAdapter(listNormalSmallPlantState);
        }

        recyclerViewNormalSmallPlantState.setAdapter(adapterSmallPlantState);
        recyclerViewNormalSmallPlantState.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}