package org.aldomanco.plantsensor.plant_state;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlantStateAdapter extends RecyclerView.Adapter<PlantStateAdapter.PlantStateItemHolder> {

    private List<PlantStateModel> listPlantState;

    //private final String basicUrlImage = "http://res.cloudinary.com/dfn8llckr/image/upload/v";
    private final String basicUrlImage = "https://upload.wikimedia.org/wikipedia/commons/e/e6/Lol_circle.png";

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
    @Override
    public void onBindViewHolder(@NonNull final PlantStateItemHolder holder, final int position) {

        PlantStateModel plantState = listPlantState.get(position);

        String urlIconPlantState = this.basicUrlImage;

        Toast.makeText(LoggedUserActivity.getLoggedUserActivity(), position+"", Toast.LENGTH_LONG).show();

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(urlIconPlantState)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.iconPlantState);

        if (plantState.getPlantName() != null && plantState.getPlantType() != null) {
            holder.plantNameText.setText(plantState.getPlantName());
            holder.plantTypeText.setText(plantState.getPlantType());
        }else {
            holder.plantNameText.setText("Plant");
            holder.plantTypeText.setText("Plant Type");
        }

        //holder.relativeMoistureSoilText.setText(listPlantState.get(position).getRelativeMoistureSoil());
        //holder.relativeMoistureAirText.setText(listPlantState.get(position).getRelativeMoistureAir());
        holder.temperatureSoilText.setText(String.valueOf(listPlantState.get(position).getTemperatureSoil()));
        //holder.temperatureAirText.setText((int) listPlantState.get(position).getTemperatureAir());
        //holder.lightIntensityText.setText(listPlantState.get(position).getLightIntensity());

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

        TextView plantNameText;
        TextView plantTypeText;

        TextView relativeMoistureSoilText;
        TextView relativeMoistureAirText;
        TextView temperatureSoilText;
        TextView temperatureAirText;
        TextView lightIntensityText;

        public PlantStateItemHolder(@NonNull View itemView) {
            super(itemView);

            layoutItem = itemView.findViewById(R.id.layout_item_comment);
            iconPlantState = itemView.findViewById(R.id.image_profile_circle);

            plantNameText = itemView.findViewById(R.id.plant_name_id);
            plantTypeText = itemView.findViewById(R.id.plant_type_id);

            //relativeMoistureSoilText = itemView.findViewById(R.id.email_comment_text);
            //relativeMoistureAirText = itemView.findViewById(R.id.email_comment_text);
            temperatureSoilText = itemView.findViewById(R.id.temperature_soil_id);
            //temperatureAirText = itemView.findViewById(R.id.email_comment_text);
            //lightIntensityText = itemView.findViewById(R.id.email_comment_text);
        }
    }

    List<PlantStateModel> getPlantStateList() {
        return this.listPlantState;
    }
}