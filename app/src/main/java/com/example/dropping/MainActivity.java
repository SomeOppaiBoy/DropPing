package com.example.dropping;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Add a test contact
        long contactId = dbHelper.addContact("Alice", "1234567890");

        // Add a test geofence
        long geofenceId = dbHelper.addGeofence("Home", 37.7749, -122.4194, 100.0f);

        // Opt out, then opt back in (for testing)
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

}