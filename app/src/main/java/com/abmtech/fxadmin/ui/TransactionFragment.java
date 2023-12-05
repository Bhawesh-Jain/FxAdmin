package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.adapter.TransactionAdapter;
import com.abmtech.fxadmin.databinding.FragmentTransactionBinding;
import com.abmtech.fxadmin.model.TransactionModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class TransactionFragment extends Fragment {
    private FragmentTransactionBinding binding;
    private FirebaseFirestore db;
    private ProgressDialog pd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransactionBinding.inflate(inflater, container, false);

        getTransaction();

        return binding.getRoot();
    }

    private void getTransaction() {
        CollectionReference ref = db.collection("transactions");

        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "No Transaction Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<TransactionModel> data = task.getResult().toObjects(TransactionModel.class);

                            if (data.size() > 0) {
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                binding.recyclerView.setAdapter(new TransactionAdapter(getContext(), data));
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