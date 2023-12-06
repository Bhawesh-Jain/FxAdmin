package com.abmtech.fxadmin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abmtech.fxadmin.databinding.DialogPriceLayBinding;
import com.abmtech.fxadmin.databinding.ItemPriceLayBinding;
import com.abmtech.fxadmin.model.PriceModel;
import com.abmtech.fxadmin.model.TransactionModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.ViewHolder> {
    private final Context context;
    private final List<PriceModel> data;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    public PriceListAdapter(Context context, List<PriceModel> data) {
        this.context = context;
        this.data = data;

        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPriceLayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PriceModel current = data.get(position);

        holder.binding.textAskPrice.setText("Ask Price: " + current.getAskPrice());
        holder.binding.textHeadPrice.setText("Head Price: " + current.getHeadPrice());
        holder.binding.textBidPrice.setText("Bid Price: " + current.getBidPrice());
        holder.binding.textHeading.setText(current.getHeading());
        holder.binding.textSubHeading.setText(current.getSubHeading());

        holder.binding.cardDel.setOnClickListener(v -> deletePrice(current, position));
        holder.binding.cardView.setOnClickListener(v -> viewDetail(position, current));
    }

    private void viewDetail(int position, PriceModel current) {
        DialogPriceLayBinding bb = DialogPriceLayBinding.inflate(LayoutInflater.from(context));

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bb.getRoot());

        bb.edtHeading.setText(current.getHeading());
        bb.edtSubHeading.setText(current.getSubHeading());
        bb.edtHeadPrice.setText(current.getHeadPrice());
        bb.edtAskPrice.setText(current.getAskPrice());
        bb.edtBidPrice.setText(current.getBidPrice());

        bb.cardSubmit.setOnClickListener(v -> {
            if (bb.edtHeading.getText().toString().isEmpty()) {
                bb.edtHeading.setError("Field Can't be empty!");
                bb.edtHeading.requestFocus();
            } else if (bb.edtSubHeading.getText().toString().isEmpty()) {
                bb.edtSubHeading.setError("Field Can't be empty!");
                bb.edtSubHeading.requestFocus();
            } else if (bb.edtHeadPrice.getText().toString().isEmpty()) {
                bb.edtHeadPrice.setError("Field Can't be empty!");
                bb.edtHeadPrice.requestFocus();
            } else if (bb.edtAskPrice.getText().toString().isEmpty()) {
                bb.edtAskPrice.setError("Field Can't be empty!");
                bb.edtAskPrice.requestFocus();
            } else if (bb.edtBidPrice.getText().toString().isEmpty()) {
                bb.edtBidPrice.setError("Field Can't be empty!");
                bb.edtBidPrice.requestFocus();
            } else {
                String edtHeading = bb.edtHeading.getText().toString();
                String edtSubHeading = bb.edtSubHeading.getText().toString();
                String edtHeadPrice = bb.edtHeadPrice.getText().toString();
                String edtAskPrice = bb.edtAskPrice.getText().toString();
                String edtBidPrice = bb.edtBidPrice.getText().toString();
                updatePrice(position, current, edtHeading, edtSubHeading, edtHeadPrice, edtAskPrice, edtBidPrice, bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
    }



    private void deletePrice(PriceModel current, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete " + current.getHeading() + "?")
                .setMessage(current.getHeading() + " will be permanently deleted!! Are you sure?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    db.collection("prices").document(current.getId()).delete();

                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(0, data.size());
                })
                .setNegativeButton("Cancel", null).create().show();
    }

    private void updatePrice(int position, PriceModel current, String edtHeading, String edtSubHeading, String edtHeadPrice, String edtAskPrice, String edtBidPrice, BottomSheetDialog bottomSheetDialog) {
        pd.show();

        Map<String, Object> map = new HashMap<>();

        map.put("heading", edtHeading);
        map.put("subHeading", edtSubHeading);
        map.put("askPrice", edtAskPrice);
        map.put("bidPrice", edtBidPrice);
        map.put("headPrice", edtHeadPrice);

        DocumentReference transactionRef = db.collection("prices").document(current.getId());

        transactionRef
                .update(map)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        bottomSheetDialog.dismiss();
                        current.setHeading(edtHeading);
                        current.setSubHeading(edtSubHeading);
                        current.setAskPrice(edtAskPrice);
                        current.setBidPrice(edtBidPrice);
                        current.setHeadPrice(edtHeadPrice);

                        notifyItemChanged(position);
                    } else {
                        Log.e("TransactionStatus", "Error adding document", task.getException());
                    }
                })
                .addOnFailureListener(e -> updatePrice(position, current, edtHeading, edtSubHeading, edtHeadPrice, edtAskPrice, edtBidPrice, bottomSheetDialog));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemPriceLayBinding binding;
        public ViewHolder(@NonNull ItemPriceLayBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
