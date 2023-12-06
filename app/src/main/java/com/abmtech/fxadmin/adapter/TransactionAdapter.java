package com.abmtech.fxadmin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ItemTransactionListBinding;
import com.abmtech.fxadmin.model.TransactionModel;
import com.abmtech.fxadmin.util.ProgressDialog;
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
        pd = new ProgressDialog(context);
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
        holder.binding.textTransactionAmount.setText("Transaction Amount: " + current.getAmount());
        holder.binding.textTransactionId.setText("Transaction Id: " + current.getTransactionId());
        holder.binding.textTransactionStatus.setText("Status: " + current.getStatus());
        holder.binding.textTransactionDate.setText("Date: " + current.getDate());

        holder.binding.cardComplete.setOnClickListener(v -> setStatus(current, position, "COMPLETE"));
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
