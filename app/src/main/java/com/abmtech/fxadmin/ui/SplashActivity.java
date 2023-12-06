package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.abmtech.fxadmin.databinding.ActivitySplashBinding;
import com.abmtech.fxadmin.util.Session;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private FirebaseAuth mAuth;
    private final int SPLASH_TIMER = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();

        init(1);
    }

    private void init(int count) {
        Session session = new Session(this);
        if (count > 10) {
            Toast.makeText(this, "Something went wrong! Please check internet connection and try again!", Toast.LENGTH_SHORT).show();
        } else
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, task -> {
                        if (!task.isSuccessful()) {
                            init(count + 1);
                        } else {
                            new Handler().postDelayed(() -> {
                                if (session.isLoggedIn())
                                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                                else
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }, SPLASH_TIMER);
                            Log.d(TAG, "onComplete() called with: task = [" + task + "]");
                        }
                    });
    }

}