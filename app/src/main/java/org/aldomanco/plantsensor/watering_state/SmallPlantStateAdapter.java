package org.aldomanco.plantsensor.watering_state;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;
import org.aldomanco.plantsensor.plant_state.PlantStateAdapter;
import org.aldomanco.plantsensor.plant_state.PlantStateModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SmallPlantStateAdapter extends RecyclerView.Adapter<SmallPlantStateAdapter.SmallPlantStateItemHolder> {

    private List<PlantStateModel> listPlantState;
    private String imagePlantStatePath;

    public SmallPlantStateAdapter(List<PlantStateModel> listPlantState) {
        this.listPlantState = listPlantState;
    }

    @NonNull
    @Override
    public SmallPlantStateAdapter.SmallPlantStateItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_small_plant_state, parent, false);

        SmallPlantStateAdapter.SmallPlantStateItemHolder viewHolder = new SmallPlantStateAdapter.SmallPlantStateItemHolder(itemView);

        return viewHolder;
    }

    /**
     * Questo metodo viene eseguito per ogni elemento nella lista, ogni elemento quindi viene
     * processato e aggiunto alla lista.
     */
    @Override
    public void onBindViewHolder(@NonNull final SmallPlantStateAdapter.SmallPlantStateItemHolder holder, final int position) {

        PlantStateModel plantState = listPlantState.get(position);

        Glide.with(LoggedUserActivity.getLoggedUserActivity())
                .asBitmap()
                .load(plantState.getIconStatePath())
                .placeholder(plantState.getIconStatePath())
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.iconPlantState);

        holder.namePlantState.setText(plantState.getNameState());
        holder.valuePlantState.setText(String.valueOf(plantState.getValueState()));

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

    public class SmallPlantStateItemHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layoutItem;

        CircleImageView iconPlantState;

        TextView namePlantState;
        TextView valuePlantState;

        public SmallPlantStateItemHolder(@NonNull View itemView) {
            super(itemView);

            layoutItem = itemView.findViewById(R.id.layout_item_comment);

            iconPlantState = itemView.findViewById(R.id.image_profile_circle);

            namePlantState = itemView.findViewById(R.id.name_plant_state_id);
            valuePlantState = itemView.findViewById(R.id.value_plant_state_id);
        }
    }

    List<PlantStateModel> getPlantStateList() {
        return this.listPlantState;
    }
}
