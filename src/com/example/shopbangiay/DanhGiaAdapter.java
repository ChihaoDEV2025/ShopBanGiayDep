package com.example.shopbangiay;


import Models.DanhGia;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
public class DanhGiaAdapter extends BaseAdapter {
	 
    Context       context;
    List<DanhGia> danhSach;
 
    public DanhGiaAdapter(Context context, List<DanhGia> danhSach) {
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
                    .inflate(R.layout.item_danh_gia, parent, false);
        }
 
        DanhGia dg = danhSach.get(position);
 
        TextView tvHovaTen    = (TextView) convertView.findViewById(R.id.tvHovaTen);
        TextView tvSoSao      = (TextView) convertView.findViewById(R.id.tvSoSao);
        TextView tvNoiDung    = (TextView) convertView.findViewById(R.id.tvNoiDung);
        TextView tvNgay       = (TextView) convertView.findViewById(R.id.tvNgay);
 
        tvHovaTen.setText(dg.getHovaTen());
        tvNoiDung.setText(dg.getNoiDung() != null ? dg.getNoiDung() : "");
        tvNgay.setText(dg.getNgayDanhGia());
 
        // Hien thi sao
        StringBuilder sao = new StringBuilder();
        for (int i = 0; i < dg.getSoSao(); i++) sao.append("★");
        for (int i = dg.getSoSao(); i < 5; i++) sao.append("☆");
        tvSoSao.setText(sao.toString());
 
        return convertView;
    }
}
 
 