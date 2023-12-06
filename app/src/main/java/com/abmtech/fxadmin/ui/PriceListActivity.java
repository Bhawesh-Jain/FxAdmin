package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;
import static com.abmtech.fxadmin.util.Constants.getCurrentTimeStamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.abmtech.fxadmin.adapter.PriceListAdapter;
import com.abmtech.fxadmin.adapter.TransactionAdapter;
import com.abmtech.fxadmin.databinding.ActivityPriceListBinding;
import com.abmtech.fxadmin.databinding.DialogPriceLayBinding;
import com.abmtech.fxadmin.model.PriceModel;
import com.abmtech.fxadmin.model.TransactionModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceListActivity extends AppCompatActivity {
    private ActivityPriceListBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPriceListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        binding.fabAddNew.setOnClickListener(v -> addNew());
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void addNew() {
        DialogPriceLayBinding bb = DialogPriceLayBinding.inflate(getLayoutInflater());

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.cardSubmit.setOnClickListener(v -> {
            if (bb.edtHeading.getText().toString().isEmpty()) {
                bb.edtHeading.setError("Field Can't be empty!");
                bb.edtHeading.requestFocus();
            } else if (bb.edtSubHeading.getText().toString().isEmpty()) {
                bb.edtSubHeading.setError("Field Can't be empty!");
                bb.edtSubHeading.requestFocus();
            } else if (bb.edtHeadPrice.getText().toString().isEmpty()) {
                bb.edtHeadPrice.setError("Field Can't be empty!");
                bb.edtHeadPrice.requestFocus();
            } else if (bb.edtAskPrice.getText().toString().isEmpty()) {
                bb.edtAskPrice.setError("Field Can't be empty!");
                bb.edtAskPrice.requestFocus();
            } else if (bb.edtBidPrice.getText().toString().isEmpty()) {
                bb.edtBidPrice.setError("Field Can't be empty!");
                bb.edtBidPrice.requestFocus();
            } else {
                String edtHeading = bb.edtHeading.getText().toString();
                String edtSubHeading = bb.edtSubHeading.getText().toString();
                String edtHeadPrice = bb.edtHeadPrice.getText().toString();
                String edtAskPrice = bb.edtAskPrice.getText().toString();
                String edtBidPrice = bb.edtBidPrice.getText().toString();
                addTransaction(edtHeading, edtSubHeading, edtHeadPrice, edtAskPrice, edtBidPrice, bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
    }

    private void addTransaction(String edtHeading, String edtSubHeading, String edtHeadPrice, String edtAskPrice, String edtBidPrice, BottomSheetDialog dialog) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("heading", edtHeading);
        map.put("subHeading", edtSubHeading);
        map.put("askPrice", edtAskPrice);
        map.put("bidPrice", edtBidPrice);
        map.put("headPrice", edtHeadPrice);

        String id = db.collection("prices").document().getId();

        map.put("id", id);

        DocumentReference userRef = db.collection("prices").document(id);

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
        CollectionReference ref = db.collection("prices");

        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "No Transaction Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<PriceModel> data = task.getResult().toObjects(PriceModel.class);

                            if (data.size() > 0) {
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                                binding.recyclerView.setAdapter(new PriceListAdapter(this, data));
                            } else {
                                Toast.makeText(this, "No transaction found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }


}