package com.abmtech.fxadmin.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ActivityInvestmentManagmentBinding;
import com.abmtech.fxadmin.model.UserModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InvestmentManagementActivity extends AppCompatActivity {
    private ActivityInvestmentManagmentBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;
    private UserModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInvestmentManagmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        binding.imageBack.setOnClickListener(v -> onBackPressed());

        model = (UserModel) getIntent().getSerializableExtra("model");

        binding.edtInvestmentAmount.setText(model.getInvestedAmount());
        binding.edtMarketValue.setText(model.getMarketValue());
        binding.edtTodayLoss.setText(model.getTodayLoss());
        binding.edtOverallGain.setText(model.getOverallGain());

        binding.textContinue.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("investedAmount", binding.edtInvestmentAmount.getText().toString().trim());
        map.put("marketValue", binding.edtMarketValue.getText().toString().trim());
        map.put("todayLoss", binding.edtTodayLoss.getText().toString().trim());
        map.put("overallGain", binding.edtOverallGain.getText().toString().trim());

        DocumentReference transactionRef = db.collection("users").document(model.getId());

        transactionRef
                .update(map)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Data Modified!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error! Try Again!", Toast.LENGTH_SHORT).show();
                        Log.e("TransactionStatus", "Error adding document", task.getException());
                    }
                })
                .addOnFailureListener(e -> saveData());

    }
}