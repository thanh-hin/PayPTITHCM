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

import com.ptithcm.payptithcm.network.ApiClient;
import com.ptithcm.payptithcm.network.models.ContactResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportFragment extends Fragment {

    TextView tvPhone, tvEmail, tvAddress, tvHours, tvFax, tvWebsite;
    Button btnCall, btnEmail;
    String rawPhone = "0901234567";
    String rawEmail = "hotro@ptithcm.edu.vn";

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

        // Hiển thị thông tin mặc định ngay (offline fallback)
        setDefaultContact();

        // Thử lấy thông tin từ API
        loadContact();

        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + rawPhone));
            startActivity(intent);
        });

        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + rawEmail));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ thanh toán học phí");
            startActivity(Intent.createChooser(intent, "Gửi email"));
        });

        return view;
    }

    private void setDefaultContact() {
        rawPhone = "0901234567";
        rawEmail = "hotro@ptithcm.edu.vn";
        if (tvPhone   != null) tvPhone.setText("090 123 4567");
        if (tvEmail   != null) tvEmail.setText(rawEmail);
        if (tvAddress != null) tvAddress.setText("122 Hoàng Diệu 2, Thủ Đức, TP. Hồ Chí Minh");
        if (tvHours   != null) tvHours.setText("Thứ 2 - Thứ 6: 07:30 - 17:00");
        if (tvFax     != null) tvFax.setText("(028) 3897 0601");
        if (tvWebsite != null) tvWebsite.setText("www.ptithcm.edu.vn");
    }

    private void loadContact() {
        ApiClient.getService().getContact().enqueue(new Callback<ContactResponse>() {
            @Override
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (getContext() == null) return;
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    ContactResponse.ContactData c = response.body().contact;
                    rawPhone = c.phone;
                    rawEmail = c.email;
                    if (tvPhone   != null) tvPhone.setText(c.phoneDisplay != null ? c.phoneDisplay : c.phone);
                    if (tvEmail   != null) tvEmail.setText(c.email);
                    if (tvAddress != null) tvAddress.setText(c.address);
                    if (tvHours   != null) tvHours.setText(c.hours);
                    if (tvFax     != null) tvFax.setText(c.fax != null ? c.fax : "");
                    if (tvWebsite != null) tvWebsite.setText(c.website != null ? c.website.replace("https://", "") : "");
                }
            }

            @Override
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                // Giữ thông tin mặc định
            }
        });
    }
}
