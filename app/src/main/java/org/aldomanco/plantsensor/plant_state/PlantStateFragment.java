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
import android.widget.TextView;
import android.widget.Toast;

import org.aldomanco.plantsensor.R;

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

    public static PlantStateFragment getPeopleListFragment() {
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

        getPlantStateList();
    }

    /*Emitter.Listener updateUsersList = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            if (LoggedUserActivity.getLoggedUserActivity() != null) {
                LoggedUserActivity.getLoggedUserActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // quando un follow viene aggiunto/rimosso la socket avvisa del necessario aggiornmento
                        //ProfileFragment.getProfileFragment().getAllUsersFragment().newGetAllUsers();
                        //ProfileFragment.getProfileFragment().getProfileFollowingFragment(username).newGetAllUsers();
                        //ProfileFragment.getProfileFragment().getProfileFollowersFragment(username).newGetAllUsers();
                    }
                });
            }
        }
    };*/

    /**
     * Consente di recuperare tutti i post:
     * - streams: lista dei post dell'utente e dei suoi following
     * - favorites: sono i post a cui l'utente ha espresso la preferenza
     * Viene mandata una richiesta http per recuperati i dal server.
     */
    public void getPlantStateList() {

        PlantStateModel p1 = new PlantStateModel(
                1,
                "plant1",
                "type1",
                "isernia",
                "italy",
                "aldo",
                3,
                35,
                12.3,
                13.4,
                35
        );

        PlantStateModel p2 = new PlantStateModel(
                2,
                "plant2",
                "type2",
                "isernia",
                "italy",
                "aldo",
                4,
                45,
                13.3,
                13.5,
                31
        );

        PlantStateModel p3 = new PlantStateModel(
                2,
                "plant3",
                "type3",
                "isernia",
                "italy",
                "aldo",
                4,
                45,
                13.43,
                13.5,
                31
        );

        PlantStateModel p4 = new PlantStateModel(
                1,
                "plant1",
                "type1",
                "isernia",
                "italy",
                "aldo",
                3,
                35,
                12.3,
                13.4,
                35
        );

        PlantStateModel p5 = new PlantStateModel(
                2,
                "plant2",
                "type2",
                "isernia",
                "italy",
                "aldo",
                4,
                45,
                13.3,
                13.5,
                31
        );

        PlantStateModel p6 = new PlantStateModel(
                2,
                "plant3",
                "type3",
                "isernia",
                "italy",
                "aldo",
                4,
                45,
                13.43,
                13.5,
                31
        );

        List<PlantStateModel> listPlantState = new ArrayList<>();
        listPlantState.add(p1);
        listPlantState.add(p2);
        listPlantState.add(p3);
        listPlantState.add(p4);
        listPlantState.add(p5);
        listPlantState.add(p6);
        listPlantState.add(p6);

        for (PlantStateModel tmp:
             listPlantState) {

            Toast.makeText(getActivity(), tmp.getPlantName(), Toast.LENGTH_LONG).show();
        }

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


    /**
     * Viene collegata la recycler view con l'adapter
     */
    private void initializeRecyclerView(List<PlantStateModel> listPlantState) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_plant_state);

        if (adapter == null){
            adapter = new PlantStateAdapter(listPlantState);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}