package com.abmtech.fxadmin.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.rlAddFunds.setOnClickListener(v -> startActivity(new Intent(getContext(), PriceListActivity.class)));
        binding.rlServices.setOnClickListener(v -> startActivity(new Intent(getContext(), ServiceActivity.class)));

        return binding.getRoot();
    }
}