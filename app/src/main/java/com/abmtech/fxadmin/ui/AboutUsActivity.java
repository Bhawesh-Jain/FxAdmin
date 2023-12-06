package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.adapter.ServiceListAdapter;
import com.abmtech.fxadmin.databinding.ActivityAboutUsBinding;
import com.abmtech.fxadmin.model.ServiceModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AboutUsActivity extends AppCompatActivity {
    private ActivityAboutUsBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        getAboutUs();

        binding.cardSubmit.setOnClickListener(v -> {
            if (binding.edtSubHeading.getText().toString().isEmpty())
                Toast.makeText(this, "Enter About Us!", Toast.LENGTH_SHORT).show();
            else updateAboutUs();
        });

        binding.icBack.setOnClickListener(v -> onBackPressed());
    }

    private void getAboutUs() {
        CollectionReference ref = db.collection("about_us");

        ref.document("about")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            ServiceModel data = task.getResult().toObject(ServiceModel.class);

                            if (data != null)
                                binding.edtSubHeading.setText(data.getDescription());
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
        Map<String, Object> map = new HashMap<>();
        map.put("description", binding.edtSubHeading.getText().toString());

        CollectionReference ref = db.collection("about_us");

        ref.document("about")
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