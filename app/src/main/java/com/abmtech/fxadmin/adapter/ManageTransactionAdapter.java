package com.abmtech.fxadmin.adapter;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ItemManageTransactionListBinding;
import com.abmtech.fxadmin.databinding.ItemTransactionListBinding;
import com.abmtech.fxadmin.model.ManageTransactionInterface;
import com.abmtech.fxadmin.model.TransactionModel;
import com.abmtech.fxadmin.model.UserModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageTransactionAdapter extends RecyclerView.Adapter<ManageTransactionAdapter.ViewHolder> {
    private final Context context;
    private final ManageTransactionInterface manageTransactionInterface;
    private final List<TransactionModel> data;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    public ManageTransactionAdapter(Context context, List<TransactionModel> data, ManageTransactionInterface manageTransactionInterface) {
        this.context = context;
        this.data = data;
        this.manageTransactionInterface = manageTransactionInterface;

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
        return new ViewHolder(ItemManageTransactionListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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

        holder.binding.cardEdit.setOnClickListener(v -> manageTransactionInterface.onEdit(current, position));

        holder.binding.cardDelete.setOnClickListener(v -> deleteTransaction(current, position));

        if (current.getType().equals("Paid")) {
            holder.binding.image.setImageResource(R.drawable.ic_top_right_arrow);
        } else {
            holder.binding.image.setImageResource(R.drawable.ic_bottom_left_arrow);
        }

    }

    private void deleteTransaction(TransactionModel current, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to permanently delete this transaction?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("transactions").document(current.getId()).delete();
                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(0, data.size());
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemManageTransactionListBinding binding;

        public ViewHolder(@NonNull ItemManageTransactionListBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
