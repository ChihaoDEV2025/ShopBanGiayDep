package com.example.shopbangiay;

import Models.SanPham;
import TienIchMoRong.DocAnh;
import TienIchMoRong.KetNoiDB;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SanPhamAdapter extends BaseAdapter {

    public interface OnYeuThichChangedListener {
        void onYeuThichChanged(SanPham sanPham, boolean daYeuThich);
    }

    Context context;
    List<SanPham> danhSach;
    NumberFormat formatTien;
    OnYeuThichChangedListener onYeuThichChangedListener;

    public SanPhamAdapter(Context context, List<SanPham> danhSach) {
        this.context = context;
        this.danhSach = danhSach;
        this.formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    public void setOnYeuThichChangedListener(OnYeuThichChangedListener listener) {
        this.onYeuThichChangedListener = listener;
    }

    @Override
    public int getCount() {
        return danhSach.size();
    }

    @Override
    public Object getItem(int position) {
        return danhSach.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_san_pham, parent, false);
            holder = new ViewHolder();
            holder.imgSanPham = (ImageView) convertView.findViewById(R.id.imgSanPham);
            holder.imgYeuThich = (ImageView) convertView.findViewById(R.id.imgYeuThich);
            holder.tvTenSP = (TextView) convertView.findViewById(R.id.tvTenSP);
            holder.tvGiaSale = (TextView) convertView.findViewById(R.id.tvGiaSale);
            holder.tvGiaGoc = (TextView) convertView.findViewById(R.id.tvGiaGoc);
            holder.tvSoLuotReview = (TextView) convertView.findViewById(R.id.tvSoLuotReview);
            holder.tvNhanSale = (TextView) convertView.findViewById(R.id.tvNhanSale);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SanPham sp = danhSach.get(position);

        DocAnh.hienThiAnh(context, sp.getImageURL(), holder.imgSanPham);
        holder.tvTenSP.setText(sp.getTenSP());
        holder.tvSoLuotReview.setText("★ " + sp.getSoLuotReview() + " danh gia");

        if (sp.dangSale()) {
            holder.tvGiaSale.setText(formatTien.format(sp.getGiaSale()) + " đ");
            holder.tvGiaGoc.setVisibility(View.VISIBLE);
            holder.tvGiaGoc.setText(formatTien.format(sp.getGia()) + " đ");
            holder.tvGiaGoc.setPaintFlags(holder.tvGiaGoc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvNhanSale.setVisibility(View.VISIBLE);
        } else {
            holder.tvGiaSale.setText(formatTien.format(sp.getGia()) + " đ");
            holder.tvGiaGoc.setVisibility(View.GONE);
            holder.tvNhanSale.setVisibility(View.GONE);
        }

        if (sp.isDaYeuThich()) {
            holder.imgYeuThich.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.imgYeuThich.setImageResource(android.R.drawable.btn_star_big_off);
        }

        holder.imgYeuThich.setEnabled(true);
        holder.imgYeuThich.setTag(sp.getMaSanPham());
        final ImageView imgYeuThich = holder.imgYeuThich;
        imgYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleYeuThich(sp, imgYeuThich);
            }
        });

        return convertView;
    }

    private void toggleYeuThich(final SanPham sp, final ImageView imgYeuThich) {
        if (NguoiDungHienTai_Activity.nguoiDungHienTai == null) {
            Toast.makeText(context, "Vui long dang nhap de su dung yeu thich.", Toast.LENGTH_SHORT).show();
            return;
        }

        imgYeuThich.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                java.sql.Connection con = null;
                java.sql.PreparedStatement ps = null;

                try {
                    con = KetNoiDB.layKetNoi();
                    if (con == null) {
                        throw new IllegalStateException(KetNoiDB.layThongDiepLoiGanNhat());
                    }

                    String maNguoiDung = NguoiDungHienTai_Activity.nguoiDungHienTai.getMaNguoiDung();
                    String maSanPham = sp.getMaSanPham();

                    if (sp.isDaYeuThich()) {
                        String sql = "DELETE FROM YeuThich WHERE MaNguoiDung=? AND MaSanPham=?";
                        ps = con.prepareStatement(sql);
                        ps.setString(1, maNguoiDung);
                        ps.setString(2, maSanPham);
                        ps.executeUpdate();
                        sp.setDaYeuThich(false);
                    } else {
                        String sql = "INSERT INTO YeuThich (MaNguoiDung, MaSanPham) VALUES (?,?)";
                        ps = con.prepareStatement(sql);
                        ps.setString(1, maNguoiDung);
                        ps.setString(2, maSanPham);
                        ps.executeUpdate();
                        sp.setDaYeuThich(true);
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (sp.getMaSanPham().equals(imgYeuThich.getTag())) {
                                if (sp.isDaYeuThich()) {
                                    imgYeuThich.setImageResource(android.R.drawable.btn_star_big_on);
                                } else {
                                    imgYeuThich.setImageResource(android.R.drawable.btn_star_big_off);
                                }
                            }
                            imgYeuThich.setEnabled(true);
                            if (onYeuThichChangedListener != null) {
                                onYeuThichChangedListener.onYeuThichChanged(sp, sp.isDaYeuThich());
                            }
                        }
                    });
                } catch (Exception e) {
                    final String thongBao = e.getMessage() == null || e.getMessage().trim().length() == 0
                            ? "Khong cap nhat duoc yeu thich."
                            : e.getMessage();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgYeuThich.setEnabled(true);
                            Toast.makeText(context, thongBao, Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    } catch (Exception e) {
                    }
                    try {
                        if (con != null) {
                            con.close();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }

    private static class ViewHolder {
        ImageView imgSanPham;
        ImageView imgYeuThich;
        TextView tvTenSP;
        TextView tvGiaSale;
        TextView tvGiaGoc;
        TextView tvSoLuotReview;
        TextView tvNhanSale;
    }
}
