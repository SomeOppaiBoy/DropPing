package com.example.dropping;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GeofencesAdapter extends RecyclerView.Adapter<GeofencesAdapter.ViewHolder> {
    private List<String[]> geofencesList;

    public GeofencesAdapter(List<String[]> geofencesList) {
        this.geofencesList = geofencesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.geofence_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] geofence = geofencesList.get(position);
        holder.nameText.setText(geofence[0]);
        holder.coordsText.setText("Lat: " + geofence[1] + ", Lon: " + geofence[2]);
    }

    @Override
    public int getItemCount() {
        return geofencesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, coordsText;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.geofenceName);
            coordsText = itemView.findViewById(R.id.geofenceCoords);
        }
    }
}