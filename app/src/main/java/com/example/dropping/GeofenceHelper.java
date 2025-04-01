package com.example.dropping;

import android.content.Context;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.List;

public class GeofenceHelper {
    private GeofencingClient geofencingClient;
    private Context context;

    public GeofenceHelper(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public GeofencingRequest getGeofencingRequest(List<Geofence> geofences) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofences)
                .build();
    }

    public List<Geofence> buildGeofences(DatabaseHelper dbHelper) {
        List<Geofence> geofences = new ArrayList<>();
        List<String[]> geofenceData = dbHelper.getAllGeofences();
        for (String[] data : geofenceData) {
            String name = data[0];
            double lat = Double.parseDouble(data[1]);
            double lon = Double.parseDouble(data[2]);
            float radius = 200.0f; // Was 100f before
            geofences.add(new Geofence.Builder()
                    .setRequestId(name)
                    .setCircularRegion(lat, lon, radius)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build());
        }
        return geofences;
    }
}