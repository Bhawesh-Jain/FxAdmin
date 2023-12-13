package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abmtech.fxadmin.adapter.TransactionAdapter;
import com.abmtech.fxadmin.adapter.WithdrawAdapter;
import com.abmtech.fxadmin.databinding.FragmentTransactionBinding;
import com.abmtech.fxadmin.databinding.FragmentWithdrawBinding;
import com.abmtech.fxadmin.model.TransactionModel;
import com.abmtech.fxadmin.model.WithdrawModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;


public class WithdrawFragment extends Fragment {
    private FragmentWithdrawBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWithdrawBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(getContext());

        getTransaction();

        return binding.getRoot();
    }

    private void getTransaction() {
        Query query = db.collection("withdraw").orderBy("time", Query.Direction.DESCENDING);
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "No Transaction Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<WithdrawModel> data = task.getResult().toObjects(WithdrawModel.class);

                            if (data.size() > 0) {
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                binding.recyclerView.setAdapter(new WithdrawAdapter(getContext(), data));
                            } else {
                                Toast.makeText(getContext(), "No transaction found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }

}