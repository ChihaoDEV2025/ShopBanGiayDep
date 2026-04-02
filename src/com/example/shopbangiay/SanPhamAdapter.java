package com.example.shopbangiay;


import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
public class SanPhamAdapter extends BaseAdapter {
 
    Context       context;
    List<SanPham> danhSach;
    NumberFormat  formatTien;
 
    public SanPhamAdapter(Context context, List<SanPham> danhSach) {
        this.context   = context;
        this.danhSach  = danhSach;
        this.formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));
    }
 
    @Override
    public int getCount()                       { return danhSach.size(); }
    @Override
    public Object getItem(int position)         { return danhSach.get(position); }
    @Override
    public long getItemId(int position)         { return position; }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_san_pham, parent, false);
        }
 
        SanPham sp = danhSach.get(position);
 
        ImageView imgSanPham    = (ImageView) convertView.findViewById(R.id.imgSanPham);
        TextView  tvTenSP       = (TextView)  convertView.findViewById(R.id.tvTenSP);
        TextView  tvGiaSale     = (TextView)  convertView.findViewById(R.id.tvGiaSale);
        TextView  tvGiaGoc      = (TextView)  convertView.findViewById(R.id.tvGiaGoc);
        TextView  tvSoLuotReview= (TextView)  convertView.findViewById(R.id.tvSoLuotReview);
        TextView  tvNhanSale    = (TextView)  convertView.findViewById(R.id.tvNhanSale);
        ImageView imgYeuThich   = (ImageView) convertView.findViewById(R.id.imgYeuThich);
 
        // Hien thi anh tu assets
        AnhHelper.hienThiAnh(context, sp.getImageURL(), imgSanPham);
 
        tvTenSP.setText(sp.getTenSP());
        tvSoLuotReview.setText("★ " + sp.getSoLuotReview() + " đánh giá");
 
        // Hien thi gia
        if (sp.dangSale()) {
            tvGiaSale.setText(formatTien.format(sp.getGiaSale()) + " đ");
            tvGiaGoc.setVisibility(View.VISIBLE);
            tvGiaGoc.setText(formatTien.format(sp.getGia()) + " đ");
            tvGiaGoc.setPaintFlags(tvGiaGoc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvNhanSale.setVisibility(View.VISIBLE);
        } else {
            tvGiaSale.setText(formatTien.format(sp.getGia()) + " đ");
            tvGiaGoc.setVisibility(View.GONE);
            tvNhanSale.setVisibility(View.GONE);
        }
 
        // Icon yeu thich
        if (sp.isDaYeuThich()) {
            imgYeuThich.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            imgYeuThich.setImageResource(android.R.drawable.btn_star_big_off);
        }
 
        // Bam yeu thich
        imgYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleYeuThich(sp, imgYeuThich);
            }
        });
 
        return convertView;
    }
 
    private void toggleYeuThich(final SanPham sp, final ImageView imgYeuThich) {
        if (AppData.nguoiDungHienTai == null) return;
 
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    java.sql.Connection con = KetNoiCSDL.layKetNoi();
                    if (con == null) return;
 
                    String maNguoiDung = AppData.nguoiDungHienTai.getMaNguoiDung();
                    String maSanPham   = sp.getMaSanPham();
 
                    if (sp.isDaYeuThich()) {
                        // Xoa khoi yeu thich
                        String sql = "DELETE FROM YeuThich WHERE MaNguoiDung=? AND MaSanPham=?";
                        java.sql.PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, maNguoiDung);
                        ps.setString(2, maSanPham);
                        ps.executeUpdate();
                        sp.setDaYeuThich(false);
                    } else {
                        // Them vao yeu thich
                        String sql = "INSERT INTO YeuThich (MaNguoiDung, MaSanPham) VALUES (?,?)";
                        java.sql.PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, maNguoiDung);
                        ps.setString(2, maSanPham);
                        ps.executeUpdate();
                        sp.setDaYeuThich(true);
                    }
                    con.close();
 
                    // Cap nhat UI tren main thread
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (sp.isDaYeuThich()) {
                                imgYeuThich.setImageResource(android.R.drawable.btn_star_big_on);
                            } else {
                                imgYeuThich.setImageResource(android.R.drawable.btn_star_big_off);
                            }
                        }
                    });
 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}