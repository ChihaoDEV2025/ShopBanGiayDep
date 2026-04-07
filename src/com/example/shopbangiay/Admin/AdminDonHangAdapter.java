package com.example.shopbangiay.Admin;

import Models.DonHang;
import com.example.shopbangiay.R;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminDonHangAdapter extends BaseAdapter {

    android.content.Context context;
    List<DonHang> danhSach;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));

    AdminDonHangAdapter(android.content.Context ctx, List<DonHang> ds) {
        this.context = ctx;
        this.danhSach = ds;
    }

    @Override
    public int getCount() {
        return danhSach.size();
    }

    @Override
    public Object getItem(int pos) {
        return danhSach.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, android.view.ViewGroup parent) {
        if (convertView == null) {
            convertView = android.view.LayoutInflater.from(context)
                    .inflate(R.layout.item_don_hang, parent, false);
        }

        final DonHang dh = danhSach.get(position);

        TextView tvMaDon = (TextView) convertView.findViewById(R.id.tvMaDon);
        TextView tvNgay = (TextView) convertView.findViewById(R.id.tvNgay);
        TextView tvTongTien = (TextView) convertView.findViewById(R.id.tvTongTien);
        TextView tvTrangThai = (TextView) convertView.findViewById(R.id.tvTrangThai);
        TextView tvNguoiDat = (TextView) convertView.findViewById(R.id.tvNguoiDat);
        Button btnChiTiet = (Button) convertView.findViewById(R.id.btnChiTiet);
        Button btnXoaDon = (Button) convertView.findViewById(R.id.btnXoaDon);

        tvMaDon.setText("Mã DH: " + dh.getMaDonHang());
        tvNgay.setText("Ngày: " + dh.getNgayDatHang());
        tvTongTien.setText("Tổng: " + formatTien.format(dh.getTongHoaDon()) + " đ");
        tvTrangThai.setText(dh.getTenTrangThai());
        if (tvNguoiDat != null) {
            tvNguoiDat.setText("KH: " + dh.getHovaTen());
        }

        switch (dh.getTrangThaiDonHang()) {
            case 0:
                tvTrangThai.setTextColor(0xFFF9A825);
                break;
            case 1:
                tvTrangThai.setTextColor(0xFF1565C0);
                break;
            case 2:
                tvTrangThai.setTextColor(0xFF2E7D32);
                break;
            case 3:
                tvTrangThai.setTextColor(0xFFC62828);
                break;
            default:
                break;
        }

        View.OnClickListener moChiTiet = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AdminDonHang_Activity) context).hienChiTietDonHang(dh);
            }
        };
        convertView.setOnClickListener(moChiTiet);
        btnChiTiet.setOnClickListener(moChiTiet);
        btnXoaDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AdminDonHang_Activity) context).xoaDonHang(dh);
            }
        });

        return convertView;
    }
}
