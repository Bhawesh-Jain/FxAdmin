package com.abmtech.fxadmin.adapter;

import static android.content.ContentValues.TAG;
import static com.abmtech.fxadmin.util.Constants.getCurrentTimeStamp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.fxadmin.databinding.DialogWithdrawTransactionIdLayBinding;
import com.abmtech.fxadmin.databinding.ItemWithdrawListBinding;
import com.abmtech.fxadmin.model.UserModel;
import com.abmtech.fxadmin.model.WithdrawModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.ViewHolder> {
    private final Context context;
    private final List<WithdrawModel> data;
    private final FirebaseFirestore db;
    private ProgressDialog pd;

    public WithdrawAdapter(Context context, List<WithdrawModel> data) {
        this.context = context;
        this.data = data;

        db = FirebaseFirestore.getInstance();
        try {
            pd = new ProgressDialog(context);
        } catch (Exception e) {
            Log.e("TAG", "TransactionAdapter: ", e);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemWithdrawListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WithdrawModel current = data.get(position);

        holder.binding.textMode.setText(current.getType());
        holder.binding.textName.setText(current.getName());
        holder.binding.textTransactionAmount.setText(String.format("Transaction Amount: %s", current.getAmount()));
        holder.binding.textAccountHolder.setText(String.format("Account Holder: %s", current.getAccHolder()));
        holder.binding.textAccountNumber.setText(String.format("Account Number: %s", current.getAccNumber()));
        holder.binding.textIfsc.setText(String.format("IFSC Code: %s", current.getIfscCode()));
        holder.binding.textBankName.setText(String.format("Bank Name: %s", current.getBankName()));
        holder.binding.textTransactionStatus.setText(String.format("Status: %s", current.getStatus()));
        holder.binding.textTransactionDate.setText(String.format("Date: %s", current.getDate()));

        holder.binding.cardComplete.setOnClickListener(v -> askTransactionId(current, position));

        holder.binding.cardReject.setOnClickListener(v -> setStatus(current, position, "REJECTED", "WITHDRAW-REJECTED"));

        if (current.getStatus().equals("PENDING")) {
            holder.binding.llBtns.setVisibility(View.VISIBLE);
        } else {
            holder.binding.llBtns.setVisibility(View.GONE);
        }
    }

    private void askTransactionId(WithdrawModel current, int position) {
        DialogWithdrawTransactionIdLayBinding bb = DialogWithdrawTransactionIdLayBinding.inflate(LayoutInflater.from(context));

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.cardSubmit.setOnClickListener(v -> {
            if (bb.edtTransactionId.getText().toString().isEmpty()) {
                bb.edtTransactionId.setError("Enter Transaction Id!");
                bb.edtTransactionId.requestFocus();
            } else {
                updateUser(current, position, bb.edtTransactionId.getText().toString().trim());
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void updateUser(WithdrawModel current, int position, String transactionId) {
        pd.show();
        CollectionReference ref = db.collection("users");

        ref.document(current.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "User Not Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            UserModel model = task.getResult().toObject(UserModel.class);

                            if (model != null) {
                                long a = Long.parseLong(model.getInvestedAmount()) - Long.parseLong(current.getAmount());
                                long b = Long.parseLong(model.getMarketValue()) - Long.parseLong(current.getAmount());
                                Map<String, Object> transaction = new HashMap<>();

                                transaction.put("investedAmount", String.valueOf(a));
                                transaction.put("marketValue", String.valueOf(b));

                                ref.document(current.getUserId())
                                        .update(transaction)
                                        .addOnCompleteListener(task1 -> {
                                            pd.dismiss();
                                            if (task1.isSuccessful()) {
                                                setStatus(current, position, "COMPLETE", transactionId);
                                            } else {
                                                Log.e("TransactionStatus", "Error adding document", task1.getException());
                                            }
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(context, "Failed! Try again later!", Toast.LENGTH_SHORT).show());
                            } else {
                                Toast.makeText(context, "Invalid User!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }


    private void setStatus(WithdrawModel current, int position, String status, String transactionId) {
        pd.show();

        Map<String, Object> transaction = new HashMap<>();

        transaction.put("status", status);

        DocumentReference transactionRef = db.collection("withdraw").document(current.getId());

        transactionRef
                .update(transaction)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        addTransaction(status, current, transactionId);
                        current.setStatus(status);
                        notifyItemChanged(position);
                    } else {
                        Log.e("TransactionStatus", "Error adding document", task.getException());
                    }
                })
                .addOnFailureListener(e -> setStatus(current, position, status, transactionId));
    }

    private void addTransaction(String status, WithdrawModel current, String transactionId) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("userId", current.getUserId());
        map.put("date", getCurrentTimeStamp());
        map.put("amount", current.getAmount());
        map.put("time", System.currentTimeMillis());
        map.put("type", "Receive");
        map.put("transactionId", transactionId);
        map.put("message", "Withdraw request " + status + " by Admin");
        map.put("status", status);


        String id = db.collection("transactions").document().getId();

        map.put("id", id);

        DocumentReference userRef = db.collection("transactions").document(id);

        userRef.set(map).addOnCompleteListener(task -> pd.dismiss())
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Log.e("TAG", "onFailure: Signup", e);
                });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemWithdrawListBinding binding;

        public ViewHolder(@NonNull ItemWithdrawListBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
