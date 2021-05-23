package com.jdream.oneq.business;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jdream.oneq.R;
import com.jdream.oneq.databinding.ActivityMainBusinessBinding;

public class MainBusinessActivity extends AppCompatActivity {

    private ActivityMainBusinessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBusinessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view_business);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_business_home, R.id.navigation_scan_qrcode, R.id.navigation_business_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_business);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navViewBusiness, navController);
    }

}