package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ActivityDashboardBinding;
import com.abmtech.fxadmin.model.UserModel;
import com.abmtech.fxadmin.util.Session;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

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

    @Override
    protected void onResume() {
        super.onResume();
        getUser();
    }

    private void getUser() {
        Session session = new Session(this);
        CollectionReference ref = db.collection("admin");

        ref.whereEqualTo("email", session.getUserId())
                .whereEqualTo("password", session.getMobile())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "Invalid User!", Toast.LENGTH_SHORT).show();
                            session.logout();
                        } else {
                            List<UserModel> data = task.getResult().toObjects(UserModel.class);

                            if (data.size() == 0) {
                                Toast.makeText(this, "Invalid User!", Toast.LENGTH_SHORT).show();
                                session.logout();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        session.logout();
                    }
                });
    }

}