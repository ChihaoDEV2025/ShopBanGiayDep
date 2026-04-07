package com.example.shopbangiay.Admin;

import Models.ChiTieu;
import com.example.shopbangiay.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminChiTieuAdapter extends BaseAdapter {
    android.content.Context context;
    List<ChiTieu> danhSach;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));

    AdminChiTieuAdapter(android.content.Context ctx, List<ChiTieu> ds) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chi_tieu, parent, false);
        }

        final ChiTieu ct = danhSach.get(position);

        TextView tvLiDo = (TextView) convertView.findViewById(R.id.tvLiDo);
        TextView tvTongCong = (TextView) convertView.findViewById(R.id.tvTongCong);
        TextView tvNgay = (TextView) convertView.findViewById(R.id.tvNgay);
        Button btnSua = (Button) convertView.findViewById(R.id.btnSua);
        Button btnXoa = (Button) convertView.findViewById(R.id.btnXoa);

        tvLiDo.setText(ct.getLiDoChi());
        tvTongCong.setText(formatTien.format(ct.getTongCong()) + " đ");
        tvNgay.setText(ct.getNgayChi());

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AdminChiTieu_Activity) context).hienDialogThemSua(ct);
            }
        });
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AdminChiTieu_Activity) context).xoaChiTieu(ct.getMaChiTieu());
            }
        });

        return convertView;
    }
}
