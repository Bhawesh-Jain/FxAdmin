package com.abmtech.fxadmin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.fxadmin.databinding.DialogServiceLayBinding;
import com.abmtech.fxadmin.databinding.ItemServiceLayBinding;
import com.abmtech.fxadmin.model.ServiceModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder> {
    private final Context context;
    private final List<ServiceModel> data;
    private final FirebaseFirestore db;
    private final ProgressDialog pd;

    public ServiceListAdapter(Context context, List<ServiceModel> data) {
        this.context = context;
        this.data = data;

        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemServiceLayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceModel current = data.get(position);

        holder.binding.textHeading.setText(current.getHeading());
        holder.binding.textSubHeading.setText(current.getDescription());

        holder.binding.cardDel.setOnClickListener(v -> deletePrice(current, position));
        holder.binding.cardView.setOnClickListener(v -> viewDetail(position, current));
    }

    private void viewDetail(int position, ServiceModel current) {
        DialogServiceLayBinding bb = DialogServiceLayBinding.inflate(LayoutInflater.from(context));

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.edtHeading.setText(current.getHeading());
        bb.edtSubHeading.setText(current.getDescription());

        bb.cardSubmit.setOnClickListener(v -> {
            if (bb.edtHeading.getText().toString().isEmpty()) {
                bb.edtHeading.setError("Field Can't be empty!");
                bb.edtHeading.requestFocus();
            } else if (bb.edtSubHeading.getText().toString().isEmpty()) {
                bb.edtSubHeading.setError("Field Can't be empty!");
                bb.edtSubHeading.requestFocus();
            } else {
                String edtHeading = bb.edtHeading.getText().toString();
                String edtSubHeading = bb.edtSubHeading.getText().toString();

                updatePrice(position, current, edtHeading, edtSubHeading, bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
    }



    private void deletePrice(ServiceModel current, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete " + current.getHeading() + "?")
                .setMessage(current.getHeading() + " will be permanently deleted!! Are you sure?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    db.collection("services").document(current.getId()).delete();

                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(0, data.size());
                })
                .setNegativeButton("Cancel", null).create().show();
    }

    private void updatePrice(int position, ServiceModel current, String edtHeading, String edtSubHeading, BottomSheetDialog bottomSheetDialog) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("heading", edtHeading);
        map.put("description", edtSubHeading);

        DocumentReference transactionRef = db.collection("services").document(current.getId());

        transactionRef
                .update(map)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        bottomSheetDialog.dismiss();
                        current.setHeading(edtHeading);
                        current.setDescription(edtSubHeading);

                        notifyItemChanged(position);
                    } else {
                        Log.e("TransactionStatus", "Error adding document", task.getException());
                    }
                })
                .addOnFailureListener(e -> updatePrice(position, current, edtHeading, edtSubHeading, bottomSheetDialog));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemServiceLayBinding binding;
        public ViewHolder(@NonNull ItemServiceLayBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
