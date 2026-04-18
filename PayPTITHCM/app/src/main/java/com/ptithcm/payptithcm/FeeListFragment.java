package com.ptithcm.payptithcm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ptithcm.payptithcm.activities.PaymentActivity;
import com.ptithcm.payptithcm.adapters.FeeAdapter;
import com.ptithcm.payptithcm.models.FeeItem;

import java.util.ArrayList;
import java.util.List;

public class FeeListFragment extends Fragment {
    ListView lvFees;
    List<FeeItem> data;
    Button btnPayNow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fee_list, container, false);

        lvFees = view.findViewById(R.id.lvFees);
        btnPayNow = view.findViewById(R.id.btnPayNow);

        // Khởi tạo danh sách các khoản phí theo yêu cầu
        data = new ArrayList<>();
        data.add(new FeeItem("Học phí học kỳ 1", 12500000, "Chưa đóng"));
        data.add(new FeeItem("Phí Ký túc xá (KTX)", 1500000, "Chưa đóng"));
        data.add(new FeeItem("Phí in bảng điểm", 50000, "Chưa đóng")); //
        data.add(new FeeItem("Bảo hiểm y tế", 702000, "Đã đóng"));

        FeeAdapter adapter = new FeeAdapter(getContext(), data);
        lvFees.setAdapter(adapter);

        // Xử lý nút thanh toán
        btnPayNow.setOnClickListener(v -> {
            long totalAmount = 0;
            // Ở đây bạn có thể duyệt qua danh sách để tính tổng các khoản "Chưa đóng"
            for (FeeItem item : data) {
                if (item.getStatus().equals("Chưa đóng")) {
                    totalAmount += item.getAmount();
                }
            }

            if (totalAmount > 0) {
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra("TOTAL_AMOUNT", totalAmount); // Truyền tổng tiền sang PaymentActivity
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Không có khoản phí nào cần thanh toán!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}