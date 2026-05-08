package com.ptithcm.payptithcm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SupportFragment extends Fragment {

    TextView tvPhone, tvEmail, tvAddress, tvHours, tvFax, tvWebsite;
    Button btnCall, btnEmail;

    private static final String PHONE   = "0901234567";
    private static final String EMAIL   = "hotro@ptithcm.edu.vn";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        tvPhone   = view.findViewById(R.id.tvPhone);
        tvEmail   = view.findViewById(R.id.tvEmail);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvHours   = view.findViewById(R.id.tvHours);
        tvFax     = view.findViewById(R.id.tvFax);
        tvWebsite = view.findViewById(R.id.tvWebsite);
        btnCall   = view.findViewById(R.id.btnCall);
        btnEmail  = view.findViewById(R.id.btnEmailSupport);

        tvPhone.setText("090 123 4567");
        tvEmail.setText(EMAIL);
        tvAddress.setText("122 Hoàng Diệu 2, Thủ Đức, TP. Hồ Chí Minh");
        tvHours.setText("Thứ 2 - Thứ 6: 07:30 - 17:00");
        tvFax.setText("(028) 3897 0601");
        tvWebsite.setText("www.ptithcm.edu.vn");

        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE));
            startActivity(intent);
        });

        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + EMAIL));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ thanh toán học phí");
            startActivity(Intent.createChooser(intent, "Gửi email"));
        });

        return view;
    }
}
