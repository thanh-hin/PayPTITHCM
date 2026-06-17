package com.ptithcm.payptithcm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InfoFragment extends Fragment {
    TextView tvContactInfo;

    private static final String CONTACT_INFO =
            "So dien thoai sinh vien lien he\n\n" +
            "Truong hop SV/PH can trao doi truc tiep qua dien thoai vui long goi trong gio lam viec den bo phan/nhan vien phu trach (trich tu So tay sinh vien):\n\n" +
            "- Lien quan den tien hoc phi va cac khoan thu khac: 028.37308400 (Phong Kinh te tai chinh).\n\n" +
            "- Lien quan ky tuc xa: 028.37305753 (Trung tam Co so vat chat va Dich vu).\n\n" +
            "- Lien quan ve diem, phuc khao: 028.37308397 (Trung tam khao thi va Dam bao chat luong giao duc).\n\n" +
            "- Lien quan ve tuyen sinh, giay bao trung tuyen, xac nhan nhap hoc: 028.38297220; nhan bang tot nghiep dai hoc: 028.38295092 (Phong Dao tao & Khoa hoc Cong nghe).\n\n" +
            "- Lien quan ve mien hoc mien thi, chuyen diem tieng Anh thanh phan, dang ky mon hoc sinh vien lien he Thay Cuong phong giao vu email: nxcuong@ptithcm.edu.vn\n\n" +
            "- Lien quan ve Chuan tieng Anh dau ra va xet Tot nghiep sinh vien lien he Thay Bang phong giao vu email: nnbang@ptithcm.edu.vn\n\n" +
            "- Sinh vien xin nghi hoc (vai ngay) gui don den thu ky khoa va giao vien bo mon.\n\n" +
            "- Lien quan den van de tai khoan ED, tu van mua tai khoan ED sinh vien lien he co Thuy Trinh email: thuytrinh@ptithcm.edu.vn.\n\n" +
            "- Cac van de khac: 028 38966675 (P.CTSV) hoac email pctsv@ptithcm.edu.vn.\n\n" +
            "- Trong thoi gian hoc, sinh vien tu lien he den thay co bo mon de biet so dien thoai hoac email.";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        tvContactInfo = view.findViewById(R.id.tvContactInfo);

        tvContactInfo.setText(CONTACT_INFO);
        return view;
    }
}
