package com.abmtech.fxadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ItemTransactionListBinding;
import com.abmtech.fxadmin.model.TransactionModel;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private final Context context;
    private final List<TransactionModel> data;
    public TransactionAdapter(Context context, List<TransactionModel> data) {
        this.context = context;
        this.data = data;
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

        if (current.getType().equals("Paid")) {
            holder.binding.image.setImageResource(R.drawable.ic_top_right_arrow);
        } else {
            holder.binding.image.setImageResource(R.drawable.ic_bottom_left_arrow);
        }
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
