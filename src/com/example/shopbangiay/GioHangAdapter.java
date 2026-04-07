package com.example.shopbangiay;

import java.text.NumberFormat;
import java.util.Locale;

import Models.GioHangItem;
import Models.SanPham;
import TienIchMoRong.DocAnh;
import TienIchMoRong.GioHang;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class GioHangAdapter extends BaseAdapter {
 
    android.content.Context context;
    java.util.List<GioHangItem> danhSach;
    GioHang_Activity gioHangActivity;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));
 
    GioHangAdapter(android.content.Context context,
                   java.util.List<GioHangItem> danhSach,
                   GioHang_Activity gioHangActivity) {
        this.context          = context;
        this.danhSach         = danhSach;
        this.gioHangActivity  = gioHangActivity;
    }
 
    @Override public int getCount()                   { return danhSach.size(); }
    @Override public Object getItem(int pos)          { return danhSach.get(pos); }
    @Override public long getItemId(int pos)          { return pos; }
 
    @Override
    public View getView(final int position, View convertView, android.view.ViewGroup parent) {
        if (convertView == null) {
            convertView = android.view.LayoutInflater.from(context)
                    .inflate(R.layout.item_gio_hang, parent, false);
        }
 
        final GioHangItem item = danhSach.get(position);
        final SanPham sp = item.getSanPham();
 
        ImageView imgSP     = (ImageView) convertView.findViewById(R.id.imgSP);
        TextView  tvTenSP   = (TextView)  convertView.findViewById(R.id.tvTenSP);
        TextView  tvGia     = (TextView)  convertView.findViewById(R.id.tvGia);
        TextView  tvSoLuong = (TextView)  convertView.findViewById(R.id.tvSoLuong);
        Button    btnTang   = (Button)    convertView.findViewById(R.id.btnTang);
        Button    btnGiam   = (Button)    convertView.findViewById(R.id.btnGiam);
        ImageView btnXoa    = (ImageView) convertView.findViewById(R.id.btnXoa);
 
        DocAnh.hienThiAnh(context, sp.getImageURL(), imgSP);
        tvTenSP.setText(sp.getTenSP());
        tvGia.setText(formatTien.format(item.getThanhTien()) + " đ");
        tvSoLuong.setText(String.valueOf(item.getSoLuong()));
 
        btnTang.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                GioHang.getInstance().capNhatSoLuong(sp.getMaSanPham(), item.getSoLuong() + 1);
                gioHangActivity.hienThiGioHang();
            }
        });
 
        btnGiam.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                GioHang.getInstance().capNhatSoLuong(sp.getMaSanPham(), item.getSoLuong() - 1);
                gioHangActivity.hienThiGioHang();
            }
        });
 
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                GioHang.getInstance().xoaSanPham(sp.getMaSanPham());
                gioHangActivity.hienThiGioHang();
            }
        });
 
        return convertView;
    }

}
