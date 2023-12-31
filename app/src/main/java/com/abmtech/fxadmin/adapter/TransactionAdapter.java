package com.abmtech.fxadmin.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ItemTransactionListBinding;
import com.abmtech.fxadmin.model.TransactionModel;
import com.abmtech.fxadmin.model.UserModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private final Context context;
    private final List<TransactionModel> data;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    public TransactionAdapter(Context context, List<TransactionModel> data) {
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
        return new ViewHolder(ItemTransactionListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionModel current = data.get(position);

        holder.binding.textMode.setText(current.getType());
        holder.binding.textName.setText(current.getMessage());
        holder.binding.textTransactionAmount.setText(String.format("Transaction Amount: %s", current.getAmount()));
        holder.binding.textTransactionId.setText(String.format("Transaction Id: %s", current.getTransactionId()));
        holder.binding.textTransactionStatus.setText(String.format("Status: %s", current.getStatus()));
        holder.binding.textTransactionDate.setText(String.format("Date: %s", current.getDate()));

        holder.binding.cardComplete.setOnClickListener(v -> updateUser(current, position));

        holder.binding.cardReject.setOnClickListener(v -> setStatus(current, position, "REJECTED"));

        if (current.getType().equals("Paid")) {
            holder.binding.image.setImageResource(R.drawable.ic_top_right_arrow);
        } else {
            holder.binding.image.setImageResource(R.drawable.ic_bottom_left_arrow);
        }
        if (current.getStatus().equals("PENDING")) {
            holder.binding.llBtns.setVisibility(View.VISIBLE);

        } else {
            holder.binding.llBtns.setVisibility(View.GONE);
        }
    }

    private void updateUser(TransactionModel current, int position) {
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
                               long a = Long.parseLong(model.getInvestedAmount()) + Long.parseLong(current.getAmount());
                               long b = Long.parseLong(model.getMarketValue()) + Long.parseLong(current.getAmount());
                                Map<String, Object> transaction = new HashMap<>();

                                transaction.put("investedAmount", String.valueOf(a));
                                transaction.put("marketValue", String.valueOf(b));

                                ref.document(current.getUserId())
                                        .update(transaction)
                                        .addOnCompleteListener(task1 -> {
                                            pd.dismiss();
                                            if (task1.isSuccessful()) {
                                                setStatus(current, position, "COMPLETE");
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



    private void setStatus(TransactionModel current, int position, String status) {
        pd.show();

        Map<String, Object> transaction = new HashMap<>();

        transaction.put("status", status);

        DocumentReference transactionRef = db.collection("transactions").document(current.getId());

        transactionRef
                .update(transaction)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        current.setStatus(status);
                        notifyItemChanged(position);
                    } else {
                        Log.e("TransactionStatus", "Error adding document", task.getException());
                    }
                })
                .addOnFailureListener(e -> setStatus(current, position, status));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemTransactionListBinding binding;
        public ViewHolder(@NonNull ItemTransactionListBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
