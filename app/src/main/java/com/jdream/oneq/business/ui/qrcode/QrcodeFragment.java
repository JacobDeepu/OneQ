package com.jdream.oneq.business.ui.qrcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jdream.oneq.databinding.FragmentScanQrcodeBinding;

public class QrcodeFragment extends Fragment {

    private QrcodeViewModel qrcodeViewModel;
    private FragmentScanQrcodeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        qrcodeViewModel =
                new ViewModelProvider(this).get(QrcodeViewModel.class);

        binding = FragmentScanQrcodeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        qrcodeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}