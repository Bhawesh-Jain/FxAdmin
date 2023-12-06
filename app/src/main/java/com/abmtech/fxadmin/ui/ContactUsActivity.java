package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ActivityContactUsBinding;
import com.abmtech.fxadmin.model.ContactUsModel;
import com.abmtech.fxadmin.model.ServiceModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity {
    private ActivityContactUsBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        pd =  new ProgressDialog(this);

        binding.icBack.setOnClickListener(v -> onBackPressed());

        getAboutUs();
        binding.cardSubmit.setOnClickListener(v -> {
            if (binding.edtPhoneNumber.getText().toString().isEmpty())
                Toast.makeText(this, "Enter Phone Number!", Toast.LENGTH_SHORT).show();
            else if (binding.edtAddress.getText().toString().isEmpty())
                Toast.makeText(this, "Enter Address!", Toast.LENGTH_SHORT).show();
            else if (binding.edtEmail.getText().toString().isEmpty())
                Toast.makeText(this, "Enter Email!", Toast.LENGTH_SHORT).show();
            else updateAboutUs();
        });

    }

    private void getAboutUs() {
        CollectionReference ref = db.collection("contact_us");

        ref.document("contact")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            ContactUsModel data = task.getResult().toObject(ContactUsModel.class);

                            if (data != null) {
                                binding.edtPhoneNumber.setText(data.getPhone());
                                binding.edtEmail.setText(data.getEmail());
                                binding.edtAddress.setText(data.getAddress());
                            }
                        } else {
                            Toast.makeText(this, "No Text found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }

    private void updateAboutUs() {
        pd.show();
        Map<String, Object> map = new HashMap<>();
        map.put("email", binding.edtEmail.getText().toString());
        map.put("phone", binding.edtPhoneNumber.getText().toString());
        map.put("address", binding.edtAddress.getText().toString());

        CollectionReference ref = db.collection("contact_us");

        ref.document("contact")
                .set(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Update Successful!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Update Failed!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }
}