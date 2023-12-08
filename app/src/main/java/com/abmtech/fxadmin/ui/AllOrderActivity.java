package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ActivityAllOrderBinding;
import com.abmtech.fxadmin.model.AllOrderModel;
import com.abmtech.fxadmin.model.ServiceModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AllOrderActivity extends AppCompatActivity {
    private ActivityAllOrderBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        getAboutUs();

        binding.textContinue.setOnClickListener(v -> updateAllOrder());

        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void updateAllOrder() {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("liveDate", binding.edtLiveDate.getText().toString().trim());
        map.put("closeDate", binding.edtCloseDate.getText().toString().trim());

        map.put("liveQuantity", binding.edtQuantity.getText().toString().trim());
        map.put("closeQuantity", binding.edtCQuantity.getText().toString().trim());

        map.put("liveBuyPrice", binding.edtBuyPrice.getText().toString().trim());
        map.put("closeBuyPrice", binding.edtCBuyPrice.getText().toString().trim());

        map.put("liveSellPrice", binding.edtSellPrice.getText().toString().trim());
        map.put("closeSellPrice", binding.edtCSellPrice.getText().toString().trim());

        map.put("liveCurrencyName", binding.edtCurrencyName.getText().toString().trim());
        map.put("closeCurrencyName", binding.edtCCurrencyName.getText().toString().trim());

        DocumentReference transactionRef = db.collection("all_order").document("all_order");

        transactionRef
                .set(map)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Data Modified!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error! Try Again!", Toast.LENGTH_SHORT).show();
                        Log.e("TransactionStatus", "Error adding document", task.getException());
                    }
                })
                .addOnFailureListener(e -> updateAllOrder());

    }

    private void getAboutUs() {
        pd.show();
        CollectionReference ref = db.collection("about_us");

        ref.document("about")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            AllOrderModel model = task.getResult().toObject(AllOrderModel.class);

                            if (model != null) {

                                binding.edtBuyPrice.setText(model.getLiveBuyPrice());
                                binding.edtCBuyPrice.setText(model.getCloseBuyPrice());

                                binding.edtSellPrice.setText(model.getLiveSellPrice());
                                binding.edtCSellPrice.setText(model.getCloseSellPrice());

                                binding.edtQuantity.setText(model.getLiveQuantity());
                                binding.edtCQuantity.setText(model.getCloseQuantity());

                                binding.edtCurrencyName.setText(model.getLiveCurrencyName());
                                binding.edtCCurrencyName.setText(model.getCloseCurrencyName());

                                binding.edtLiveDate.setText(model.getLiveDate());
                                binding.edtCloseDate.setText(model.getCloseDate());

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

}