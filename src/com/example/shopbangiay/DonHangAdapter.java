package com.example.shopbangiay;
import Models.DonHang;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
public class DonHangAdapter extends BaseAdapter {
	 
    Context       context;
    List<DonHang> danhSach;
    NumberFormat  formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));
 
    DonHangAdapter(Context context, List<DonHang> danhSach) {
        this.context  = context;
        this.danhSach = danhSach;
    }
 
    @Override public int    getCount()              { return danhSach.size(); }
    @Override public Object getItem(int pos)        { return danhSach.get(pos); }
    @Override public long   getItemId(int pos)      { return pos; }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_don_hang, parent, false);
        }
 
        DonHang dh = danhSach.get(position);
 
        TextView tvMaDon      = (TextView) convertView.findViewById(R.id.tvMaDon);
        TextView tvNgay       = (TextView) convertView.findViewById(R.id.tvNgay);
        TextView tvTongTien   = (TextView) convertView.findViewById(R.id.tvTongTien);
        TextView tvTrangThai  = (TextView) convertView.findViewById(R.id.tvTrangThai);
        TextView tvNguoiDat   = (TextView) convertView.findViewById(R.id.tvNguoiDat);
 
        tvMaDon.setText("Mã ĐH: " + dh.getMaDonHang());
        tvNgay.setText("Ngày: " + dh.getNgayDatHang());
        tvTongTien.setText("Tổng: " + formatTien.format(dh.getTongHoaDon()) + " đ");
        tvTrangThai.setText(dh.getTenTrangThai());
 
        // Mau trang thai
        switch (dh.getTrangThaiDonHang()) {
            case 0: tvTrangThai.setTextColor(0xFFFF8C00); break; // Cam - cho xac nhan
            case 1: tvTrangThai.setTextColor(0xFF1E90FF); break; // Xanh - dang giao
            case 2: tvTrangThai.setTextColor(0xFF228B22); break; // Xanh la - hoan thanh
            case 3: tvTrangThai.setTextColor(0xFFDC143C); break; // Do - huy
        }
 
        if (tvNguoiDat != null && dh.getHovaTen() != null) {
            tvNguoiDat.setText("KH: " + dh.getHovaTen());
        }
 
        return convertView;
    }
}
