package com.abmtech.fxadmin.ui;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.adapter.TransactionAdapter;
import com.abmtech.fxadmin.adapter.UserListAdapter;
import com.abmtech.fxadmin.databinding.FragmentHomeBinding;
import com.abmtech.fxadmin.databinding.FragmentTransactionBinding;
import com.abmtech.fxadmin.model.TransactionModel;
import com.abmtech.fxadmin.model.UserModel;
import com.abmtech.fxadmin.util.ProgressDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    private FirebaseFirestore db;
    private ProgressDialog pd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(getContext());

        getTransaction();

        return binding.getRoot();
    }

    private void getTransaction() {
        CollectionReference ref = db.collection("users");

        ref.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(getContext(), "No User Found!", Toast.LENGTH_SHORT).show();
                        } else {
                            List<UserModel> data = task.getResult().toObjects(UserModel.class);

                            if (data.size() > 0) {
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                binding.recyclerView.setAdapter(new UserListAdapter(getContext(), data));
                            } else {
                                Toast.makeText(getContext(), "No User found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.e(TAG, "Error =>", task.getException());
                        pd.dismiss();
                    }
                });
    }

}