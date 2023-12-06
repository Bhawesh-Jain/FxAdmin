package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abmtech.fxadmin.adapter.ServiceListAdapter;
import com.abmtech.fxadmin.databinding.ActivityServiceBinding;
import com.abmtech.fxadmin.databinding.DialogServiceLayBinding;
import com.abmtech.fxadmin.model.ServiceModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceActivity extends AppCompatActivity {
    private ActivityServiceBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        binding.fabAddNew.setOnClickListener(v -> addNew());
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void addNew() {
        DialogServiceLayBinding bb = DialogServiceLayBinding.inflate(getLayoutInflater());

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.cardSubmit.setOnClickListener(v -> {
            if (bb.edtHeading.getText().toString().isEmpty()) {
                bb.edtHeading.setError("Field Can't be empty!");
                bb.edtHeading.requestFocus();
            } else if (bb.edtSubHeading.getText().toString().isEmpty()) {
                bb.edtSubHeading.setError("Field Can't be empty!");
                bb.edtSubHeading.requestFocus();
            } else {
                String edtHeading = bb.edtHeading.getText().toString();
                String edtSubHeading = bb.edtSubHeading.getText().toString();
                addTransaction(edtHeading, edtSubHeading, bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
    }

    private void addTransaction(String edtHeading, String edtSubHeading, BottomSheetDialog dialog) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("heading", edtHeading);
        map.put("description", edtSubHeading);

        String id = db.collection("services").document().getId();

        map.put("id", id);

        DocumentReference userRef = db.collection("services").document(id);

        userRef.set(map).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(this, "Price Added!", Toast.LENGTH_SHORT).show();
                        getTransaction();
                    } else {
                        Toast.makeText(this, "Error! Try Again", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "Error adding document", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error! Try Again", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    Log.e("TAG", "onFailure: Signup", e);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTransaction();
    }

    private void getTransaction() {
        CollectionReference ref = db.collection("services");

        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "No Service Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<ServiceModel> data = task.getResult().toObjects(ServiceModel.class);

                            if (data.size() > 0) {
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                                binding.recyclerView.setAdapter(new ServiceListAdapter(this, data));
                            } else {
                                Toast.makeText(this, "No Service found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }


}