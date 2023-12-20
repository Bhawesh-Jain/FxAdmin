package com.abmtech.fxadmin.adapter;

import static com.abmtech.fxadmin.util.Constants.getCurrentTimeStamp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.fxadmin.databinding.DialogLoanLayBinding;
import com.abmtech.fxadmin.databinding.ItemUserLayBinding;
import com.abmtech.fxadmin.model.UserModel;
import com.abmtech.fxadmin.ui.AllOrderActivity;
import com.abmtech.fxadmin.ui.InvestmentManagementActivity;
import com.abmtech.fxadmin.ui.ManageTransactionActivity;
import com.abmtech.fxadmin.ui.UserDetailActivity;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private final Context context;
    private final List<UserModel> data;
    private final FirebaseFirestore db;
    private ProgressDialog pd;

    public UserListAdapter(Context context, List<UserModel> data) {
        this.context = context;
        this.data = data;

        this.db = FirebaseFirestore.getInstance();
        try {
            pd = new ProgressDialog(context);
        } catch (Exception e) {
            Log.e("TAG", "TransactionAdapter: ", e);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemUserLayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel current = data.get(position);

        holder.binding.textName.setText(current.getName());
        holder.binding.textEmail.setText(current.getEmail());
        holder.binding.textPhone.setText(current.getPhone());

        holder.binding.cardDel.setOnClickListener(v -> deleteUser(current, position));
        holder.binding.cardLoan.setOnClickListener(v -> addFunds(current));
        holder.binding.cardView.setOnClickListener(v -> context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("model", current)));
        holder.binding.cardTrade.setOnClickListener(v -> context.startActivity(new Intent(context, AllOrderActivity.class).putExtra("model", current)));
        holder.binding.cardTransactions.setOnClickListener(v -> context.startActivity(new Intent(context, ManageTransactionActivity.class).putExtra("userId", current.getId())));
        holder.binding.getRoot().setOnClickListener(v -> context.startActivity(new Intent(context, InvestmentManagementActivity.class).putExtra("model", current)));
    }

    private void deleteUser(UserModel current, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete " + current.getName() + "?")
                .setMessage("User " + current.getName() + " will be permanently deleted!! Are you sure?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    db.collection("users").document(current.getId()).delete();

                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(0, data.size());
                })
                .setNegativeButton("Cancel", null).create().show();
    }

    private void addFunds(UserModel current) {
        DialogLoanLayBinding bb = DialogLoanLayBinding.inflate(LayoutInflater.from(context));

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.cardSubmit.setOnClickListener(v -> {
            if (bb.edtTransactionAmount.getText().toString().isEmpty()) {
                bb.edtTransactionAmount.setError("Enter Loan Amount!");
                bb.edtTransactionAmount.requestFocus();
            }  else {
                addTransaction(current, bb.edtTransactionAmount.getText().toString(), bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
    }

    private void addTransaction(UserModel current, String transactionAmount, BottomSheetDialog dialog) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("userId", current.getId());
        map.put("date", getCurrentTimeStamp());
        map.put("amount", transactionAmount);
        map.put("type", "Loan");
        map.put("status", "Pending");

        DocumentReference userRef = db.collection("loan").document(current.getId());

        userRef.set(map).addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(context, "Loan added!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error! Try Again", Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "Error adding document", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error! Try Again", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    Log.e("TAG", "onFailure: Signup", e);
                });
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemUserLayBinding binding;

        public ViewHolder(@NonNull ItemUserLayBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
