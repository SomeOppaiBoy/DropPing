package com.example.dropping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Geofence", "Receiver triggered");
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event == null) {
            // Simulation case
            Log.d("Geofence", "Simulating Home entry");
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.sendNotification("Home", context);
            return;
        }
        // Real geofence event
        if (event.hasError()) {
            Log.e("Geofence", "Error: " + GeofenceStatusCodes.getStatusCodeString(event.getErrorCode()));
            return;
        }
        int transition = event.getGeofenceTransition();
        Log.d("Geofence", "Transition: " + transition);
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            String geofenceId = event.getTriggeringGeofences().get(0).getRequestId();
            Log.d("Geofence", "Entered: " + geofenceId);
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.sendNotification(geofenceId, context);
        }
    }
}