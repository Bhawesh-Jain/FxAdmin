package com.abmtech.fxadmin.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ActivityDashboardBinding;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                changeFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.nav_profile) {
                changeFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.nav_withdraw) {
                changeFragment(new WithdrawFragment());
            } else if (item.getItemId() == R.id.nav_transaction) {
                changeFragment(new TransactionFragment());
            }
            return true;
        });

        changeFragment(new HomeFragment());
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(binding.container.getId(), fragment);

        ft.commit();
    }
}