package com.abmtech.fxadmin.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.abmtech.fxadmin.R;
import com.abmtech.fxadmin.databinding.ActivityUserDetailBinding;
import com.abmtech.fxadmin.model.UserModel;

public class UserDetailActivity extends AppCompatActivity {
    private ActivityUserDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserModel model = (UserModel) getIntent().getSerializableExtra("model");

        binding.edtFName.setText(model.getName());
        binding.edtEmail.setText(model.getEmail());
        binding.edtPhoneNumber.setText(model.getPhone());
        binding.edtAadharNumber.setText(model.getAadhar());
        binding.textDob.setText(model.getDateOfBirth());
        binding.edtNomineeName.setText(model.getNomineeName());
        binding.textNomineeDob.setText(model.getNomineeDob());
        binding.edtNomineeRelation.setText(model.getNomineeRelation());
        binding.edtPanNumber.setText(model.getPanNumber());
        binding.edtPassword.setText(model.getPassword());
        binding.edtAddress.setText(model.getAddress());
        binding.edtBankName.setText(model.getBankName());
        binding.edtAccNumber.setText(model.getAccountNumber());
        binding.edtAccHolder.setText(model.getAccountHolder());
        binding.edtIfscCode.setText(model.getIfscCode());
        binding.edtGender.setText(model.getGender());

        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
}