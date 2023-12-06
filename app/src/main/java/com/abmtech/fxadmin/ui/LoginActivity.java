package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ActivityLoginBinding;
import com.abmtech.fxadmin.model.UserModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.abmtech.fxadmin.util.Session;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);
        session = new Session(this);

        binding.textLogin.setOnClickListener(v -> validate());
    }

    private void validate() {
        if (binding.edtEmail.getText().toString().isEmpty()) {
            binding.edtEmail.setError("Email can't be empty!");
            binding.edtEmail.requestFocus();
        } else if (binding.edtPassword.getText().toString().isEmpty()) {
            binding.edtPassword.setError("Password can't be empty!");
            binding.edtPassword.requestFocus();
        } else {
            getUser(binding.edtEmail.getText().toString());
        }
    }

    private void getUser(String value) {
        pd.show();
        CollectionReference ref = db.collection("admin");

        ref.whereEqualTo("email", value)
                .whereEqualTo("password", binding.edtPassword.getText().toString().trim())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Invalid User or Password!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<UserModel> data = task.getResult().toObjects(UserModel.class);

                            if (data.size() > 0) {
                                UserModel model = data.get(0);

                                session.setLogin(true);
                                startActivity(new Intent(this, DashboardActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid User or Password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }
}