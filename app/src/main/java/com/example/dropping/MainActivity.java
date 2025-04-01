package com.example.dropping;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        requestContactsPermission();
        requestLocationPermissions();
        requestSmsPermission();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            dbHelper.syncContacts(this);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            setupGeofences();
        }

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager.setAdapter(new ViewPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Locations");
            else if (position == 1) tab.setText("Contacts");
        }).attach();

        dbHelper.addContact("Alice", "1234567890");
        dbHelper.addGeofence("Home", 37.7749, -122.4194, 100.0f);
        dbHelper.logGeofences();

        // Simulate geofence trigger for testing
        Intent testIntent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent testPendingIntent = PendingIntent.getBroadcast(this, 0, testIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        try {
            testPendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.e("GeofenceTest", "PendingIntent canceled: " + e.getMessage());
        }
    }

    private void requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    2);
        }
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 1:
                    dbHelper.syncContacts(this);
                    Log.d("Permission", "Contacts granted");
                    break;
                case 2:
                    setupGeofences();
                    Log.d("Permission", "Location granted");
                    break;
                case 3:
                    Log.d("Permission", "SMS granted");
                    break;
            }
        } else {
            Log.d("Permission", "Denied for request code: " + requestCode);
        }
    }

    private void setupGeofences() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            GeofenceHelper geofenceHelper = new GeofenceHelper(this);
            GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
            List<Geofence> geofences = geofenceHelper.buildGeofences(dbHelper);
            Log.d("GeofenceSetup", "Geofences count: " + geofences.size());
            Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            geofencingClient.addGeofences(geofenceHelper.getGeofencingRequest(geofences), pendingIntent)
                    .addOnSuccessListener(aVoid -> Log.d("GeofenceSetup", "Geofences added"))
                    .addOnFailureListener(e -> Log.e("GeofenceSetup", "Failed: " + e.getMessage()));
        }
    }
    // Intent testIntent = new Intent(this, GeofenceBroadcastReceiver.class);
    // PendingIntent testPendingIntent = PendingIntent.getBroadcast(this, 0, testIntent,
    //         PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    // try {
    //     testPendingIntent.send();
    // } catch (PendingIntent.CanceledException e) {
    //     Log.e("GeofenceTest", "PendingIntent canceled: " + e.getMessage());
    // }
}