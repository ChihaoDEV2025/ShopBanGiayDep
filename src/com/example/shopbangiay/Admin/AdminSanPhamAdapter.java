package com.example.shopbangiay.Admin;

import Models.SanPham;
import TienIchMoRong.DocAnh;
import com.example.shopbangiay.R;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminSanPhamAdapter extends BaseAdapter {

    android.content.Context context;
    List<SanPham> danhSach;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));

    AdminSanPhamAdapter(android.content.Context ctx, List<SanPham> ds) {
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
                    .inflate(R.layout.item_admin_san_pham, parent, false);
        }

        final SanPham sp = danhSach.get(position);

        ImageView imgSP = (ImageView) convertView.findViewById(R.id.imgSP);
        TextView tvMaSP = (TextView) convertView.findViewById(R.id.tvMaSP);
        TextView tvTenSP = (TextView) convertView.findViewById(R.id.tvTenSP);
        TextView tvGia = (TextView) convertView.findViewById(R.id.tvGia);
        TextView tvSoLuong = (TextView) convertView.findViewById(R.id.tvSoLuong);
        TextView tvTrangThai = (TextView) convertView.findViewById(R.id.tvTrangThai);
        Button btnSua = (Button) convertView.findViewById(R.id.btnSua);
        Button btnXoa = (Button) convertView.findViewById(R.id.btnXoa);

        DocAnh.hienThiAnh(context, sp.getImageURL(), imgSP);
        tvMaSP.setText(sp.getMaSanPham());
        tvTenSP.setText(sp.getTenSP());
        tvGia.setText(formatTien.format(sp.getGiaHienThi()) + " đ");
        tvSoLuong.setText("Kho: " + sp.getSoLuong());
        tvTrangThai.setText(sp.getDangBan() == 1 ? "Đang bán" : "Ngừng bán");
        tvTrangThai.setTextColor(sp.getDangBan() == 1 ? 0xFF2E7D32 : 0xFFC62828);

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AdminSanPham_Activity) context).hienDialogThemSua(sp);
            }
        });

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AdminSanPham_Activity) context).xoaVinhVien(sp.getMaSanPham(), sp.getTenSP());
            }
        });

        return convertView;
    }
}
