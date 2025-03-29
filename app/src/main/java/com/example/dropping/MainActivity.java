package com.example.dropping;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestContactsPermission();
        dbHelper = new DatabaseHelper(this);
        dbHelper.syncContacts(this);
        long contactId = dbHelper.addContact("Alice", "1234567890");

        long geofenceId = dbHelper.addGeofence("Home", 37.7749, -122.4194, 100.0f);

        dbHelper.setOptStatus("1234567890", (int) geofenceId, "opted_out");
        dbHelper.setOptStatus("1234567890", (int) geofenceId, "opted_in");

        String status = dbHelper.getOptStatus("1234567890", (int) geofenceId);
        Log.d("DatabaseTest", "Opt Status: " + status); // Should print "opted_in"

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        viewPager.setAdapter(new ViewPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Locations");
            else if (position == 1) tab.setText("Contacts");
        }).attach();

    }

    private void requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
    }

}