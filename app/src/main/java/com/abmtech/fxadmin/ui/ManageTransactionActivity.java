package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.adapter.ManageTransactionAdapter;
import com.abmtech.fxadmin.adapter.TransactionAdapter;
import com.abmtech.fxadmin.databinding.ActivityManageTransactionBinding;
import com.abmtech.fxadmin.databinding.DialogAddTransactionLayBinding;
import com.abmtech.fxadmin.model.ManageTransactionInterface;
import com.abmtech.fxadmin.model.TransactionModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageTransactionActivity extends AppCompatActivity implements ManageTransactionInterface {
    private ActivityManageTransactionBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;
    private String userId = "";
    private String time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        userId = getIntent().getStringExtra("userId");

        binding.add.setOnClickListener(v -> addTransaction());

        getTransaction();
    }

    private void getTransaction() {
        Query query = db.collection("transactions").orderBy("time", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "No Transaction Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<TransactionModel> data = task.getResult().toObjects(TransactionModel.class);

                            if (data.size() > 0) {

                                List<TransactionModel> finalList = new ArrayList<>();
                                for (TransactionModel datum : data) {
                                    if (datum.getUserId().equals(userId))
                                        finalList.add(datum);
                                }

                                if (finalList.size() > 0) {
                                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                                    binding.recyclerView.setAdapter(new ManageTransactionAdapter(this, finalList, this));
                                } else {
                                    Toast.makeText(this, "No transaction found!", Toast.LENGTH_SHORT).show();
                                }
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

    @Override
    public void onEdit(TransactionModel current, int pos) {
        DialogAddTransactionLayBinding bb = DialogAddTransactionLayBinding.inflate(getLayoutInflater());

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.edtDate.setOnClickListener(v -> showDatePickerDialog(bb.edtDate));

        bb.edtType.setText(current.getType());
        bb.edtTransactionId.setText(current.getTransactionId());
        bb.edtTransactionAmount.setText(current.getAmount());
        bb.edtDate.setText(current.getDate());
        bb.edtMessage.setText(current.getMessage());

        bb.cardSubmit.setOnClickListener(v -> {
            if (bb.edtType.getText().toString().isEmpty()) {
                bb.edtType.setError("Enter Type!");
                bb.edtType.requestFocus();
            } else if (bb.edtTransactionId.getText().toString().isEmpty()) {
                bb.edtTransactionId.setError("Enter Transaction Id!");
                bb.edtTransactionId.requestFocus();
            } else if (bb.edtTransactionAmount.getText().toString().isEmpty()) {
                bb.edtTransactionAmount.setError("Enter Transaction Amount!");
                bb.edtTransactionAmount.requestFocus();
            } else if (bb.edtDate.getText().toString().isEmpty()) {
                showDatePickerDialog(bb.edtDate);
            } else if (bb.edtMessage.getText().toString().isEmpty()) {
                bb.edtMessage.setError("Enter Transaction Message!");
                bb.edtMessage.requestFocus();
            } else if (!bb.edtType.getText().toString().equals("Paid")) {
                if (!bb.edtType.getText().toString().equals("Receive")) {
                    bb.edtType.setError("Enter Valid Type!");
                    bb.edtType.requestFocus();
                } else {
                    editData(
                            bb.edtDate.getText().toString().trim(),
                            bb.edtTransactionAmount.getText().toString().trim(),
                            bb.edtType.getText().toString().trim(),
                            bb.edtTransactionId.getText().toString().trim(),
                            bb.edtMessage.getText().toString().trim(),
                            bottomSheetDialog,
                            current
                    );
                }
            } else {
                editData(
                        bb.edtDate.getText().toString().trim(),
                        bb.edtTransactionAmount.getText().toString().trim(),
                        bb.edtType.getText().toString().trim(),
                        bb.edtTransactionId.getText().toString().trim(),
                        bb.edtMessage.getText().toString().trim(),
                        bottomSheetDialog,
                        current
                );
            }
        });

        bottomSheetDialog.show();
    }

    private void addTransaction() {
        DialogAddTransactionLayBinding bb = DialogAddTransactionLayBinding.inflate(getLayoutInflater());

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.edtDate.setOnClickListener(v -> showDatePickerDialog(bb.edtDate));

        bb.cardSubmit.setOnClickListener(v -> {
            if (bb.edtType.getText().toString().isEmpty()) {
                bb.edtType.setError("Enter Type!");
                bb.edtType.requestFocus();
            } else if (bb.edtTransactionId.getText().toString().isEmpty()) {
                bb.edtTransactionId.setError("Enter Transaction Id!");
                bb.edtTransactionId.requestFocus();
            } else if (bb.edtTransactionAmount.getText().toString().isEmpty()) {
                bb.edtTransactionAmount.setError("Enter Transaction Amount!");
                bb.edtTransactionAmount.requestFocus();
            } else if (bb.edtDate.getText().toString().isEmpty()) {
                showDatePickerDialog(bb.edtDate);
            } else if (bb.edtMessage.getText().toString().isEmpty()) {
                bb.edtMessage.setError("Enter Transaction Message!");
                bb.edtMessage.requestFocus();
            } else if (!bb.edtType.getText().toString().equals("Paid")) {
                if (!bb.edtType.getText().toString().equals("Receive")) {
                    bb.edtType.setError("Enter Valid Type!");
                    bb.edtType.requestFocus();
                } else {
                    saveData(
                            bb.edtDate.getText().toString().trim(),
                            bb.edtTransactionAmount.getText().toString().trim(),
                            bb.edtType.getText().toString().trim(),
                            bb.edtTransactionId.getText().toString().trim(),
                            bb.edtMessage.getText().toString().trim(),
                            bottomSheetDialog
                    );
                }
            } else {
                saveData(
                        bb.edtDate.getText().toString().trim(),
                        bb.edtTransactionAmount.getText().toString().trim(),
                        bb.edtType.getText().toString().trim(),
                        bb.edtTransactionId.getText().toString().trim(),
                        bb.edtMessage.getText().toString().trim(),
                        bottomSheetDialog
                );
            }
        });

        bottomSheetDialog.show();
    }

    private void saveData(String date, String amount, String type, String transactionId,
                          String message, BottomSheetDialog dialog) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("userId", userId);
        map.put("date", date);
        map.put("amount", amount);
        map.put("type", type);
        map.put("time", time);
        map.put("transactionId", transactionId);
        map.put("message", message);
        map.put("status", "COMPLETE");

        String id = db.collection("transactions").document().getId();

        map.put("id", id);

        DocumentReference userRef = db.collection("transactions").document(id);

        userRef.set(map).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        getTransaction();
                        dialog.dismiss();
                        Toast.makeText(this, "Transaction added!", Toast.LENGTH_SHORT).show();
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

    private void editData(String date, String amount, String type, String transactionId,
                          String message, BottomSheetDialog dialog, TransactionModel current) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("userId", current.getUserId());
        map.put("date", date);
        map.put("amount", amount);
        map.put("type", type);
        map.put("time", current.getTime());
        map.put("transactionId", transactionId);
        map.put("message", message);


        DocumentReference userRef = db.collection("transactions").document(current.getId());

        userRef.update(map).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        getTransaction();
                        dialog.dismiss();
                        Toast.makeText(this, "Transaction added!", Toast.LENGTH_SHORT).show();
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

    public void showDatePickerDialog(TextView textView) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {

                        String h = String.valueOf(hourOfDay);
                        String m = String.valueOf(minute);
                        if (h.length() == 1) {
                            h = "0" + h;
                        }
                        if (m.length() == 1) {
                            m = "0" + m;
                        }

                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1 + " " + h + ":" + m;
                        textView.setText(selectedDate);

                        final Calendar c1 = Calendar.getInstance();
                        c1.set(Calendar.YEAR, year1);
                        c1.set(Calendar.MONTH, monthOfYear);
                        c1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        c1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c1.set(Calendar.MINUTE, minute);

                        time = String.valueOf(c1.getTimeInMillis());
                    }, hour, min, true);
                    timePickerDialog.show();
                }, year, month, day);

        datePickerDialog.show();
    }

}