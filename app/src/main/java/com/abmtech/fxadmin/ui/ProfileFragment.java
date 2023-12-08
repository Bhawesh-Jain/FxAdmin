package com.abmtech.fxadmin.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.FragmentProfileBinding;
import com.abmtech.fxadmin.util.Session;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        Session session = new Session(getContext());

        binding.rlAddFunds.setOnClickListener(v -> startActivity(new Intent(getContext(), PriceListActivity.class)));
        binding.rlServices.setOnClickListener(v -> startActivity(new Intent(getContext(), ServiceActivity.class)));
        binding.rlAboutUs.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutUsActivity.class)));
        binding.rlContactUs.setOnClickListener(v -> startActivity(new Intent(getContext(), ContactUsActivity.class)));
        binding.rlAllOrder.setOnClickListener(v -> startActivity(new Intent(getContext(), AllOrderActivity.class)));
        binding.rlLogout.setOnClickListener(v -> session.logout());

        return binding.getRoot();
    }
}