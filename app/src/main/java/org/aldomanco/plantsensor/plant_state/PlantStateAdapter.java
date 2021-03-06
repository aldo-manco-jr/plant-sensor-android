package org.aldomanco.plantsensor.plant_state;

import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.models.Color;
import org.aldomanco.plantsensor.models.PlantStateModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlantStateAdapter extends RecyclerView.Adapter<PlantStateAdapter.PlantStateItemHolder> {

    private List<PlantStateModel> listPlantState;
    private String imagePlantStatePath;

    public PlantStateAdapter(List<PlantStateModel> listPlantState) {
        this.listPlantState = listPlantState;
    }

    @NonNull
    @Override
    public PlantStateItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_plant_state, parent, false);

        PlantStateItemHolder viewHolder = new PlantStateItemHolder(itemView);

        return viewHolder;
    }

    /**
     * Questo metodo viene eseguito per ogni elemento nella lista, ogni elemento quindi viene
     * processato e aggiunto alla lista.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final PlantStateItemHolder holder, final int position) {

        PlantStateModel plantState = listPlantState.get(position);

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(plantState.getIconStatePath())
                .placeholder(plantState.getIconStatePath())
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.iconPlantState);

        holder.namePlantState.setText(plantState.getNameState());

        if (plantState.getValueState()==Double.MAX_VALUE){
            holder.valuePlantState.setText("Not Available");
        }else {
            holder.valuePlantState.setText(String.valueOf(plantState.getValueState()));
        }

        holder.progressBarPlantState.setMin(((Double) plantState.getMinValueState()).intValue());
        holder.progressBarPlantState.setMax(((Double) plantState.getMaxValueState()).intValue());
        holder.progressBarPlantState.setProgress(((Double) plantState.getValueState()).intValue());

        plantState.setColorPlantState();

        if (plantState.getColorPlantState()== Color.GREEN){
            holder.progressBarPlantState.setBackgroundDrawable(LoggedUserActivity.getLoggedUserActivity().getDrawable(R.drawable.green_progress_bar));
        }else if (plantState.getColorPlantState()==Color.YELLOW_NEGATIVE || plantState.getColorPlantState()==Color.YELLOW_POSITIVE){
            holder.progressBarPlantState.setBackgroundDrawable(LoggedUserActivity.getLoggedUserActivity().getDrawable(R.drawable.yellow_progress_bar));
        }else if (plantState.getColorPlantState()==Color.RED_NEGATIVE || plantState.getColorPlantState()==Color.RED_POSITIVE){
            holder.progressBarPlantState.setBackgroundDrawable(LoggedUserActivity.getLoggedUserActivity().getDrawable(R.drawable.progress_bar));
        }

        holder.infoPlantStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LoggedUserActivity.getLoggedUserActivity())
                        .setIcon(R.drawable.ic_baseline_privacy_tip_24)
                        .setTitle(plantState.getNameState())
                        .setMessage(plantState.getInfoState())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listPlantState.size();
    }

    public class PlantStateItemHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layoutItem;

        CircleImageView iconPlantState;

        TextView namePlantState;
        TextView valuePlantState;
        ProgressBar progressBarPlantState;

        ImageButton infoPlantStateButton;

        public PlantStateItemHolder(@NonNull View itemView) {
            super(itemView);

            layoutItem = itemView.findViewById(R.id.layout_item_comment);

            iconPlantState = itemView.findViewById(R.id.image_profile_circle);

            namePlantState = itemView.findViewById(R.id.name_plant_state_id);
            valuePlantState = itemView.findViewById(R.id.value_plant_state_id);
            progressBarPlantState = itemView.findViewById(R.id.progress_bar_plant_state_value_id);
            infoPlantStateButton = itemView.findViewById(R.id.info_plant_state_button);
        }
    }

    List<PlantStateModel> getPlantStateList() {
        return this.listPlantState;
    }
}