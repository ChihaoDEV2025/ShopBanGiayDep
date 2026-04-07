package com.example.shopbangiay.Admin;

import TienIchMoRong.KetNoiDB;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopbangiay.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;

public class AdminThongKe_Activity extends Activity {
    TextView tvDoanhThuHomNay, tvDoanhThuThang, tvDoanhThuNam;
    TextView tvDonChoXN, tvDonDangGiao, tvDonHoanThanh, tvDonHuy;
    TextView tvSPBanChay, tvSPHetHang;
    TextView tvTongChiThang, tvLoiNhuanThang;

    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_thong_ke);

        khoiTaoView();
        taiThongKe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_thong_ke, menu);
        return true;
    }

    private void khoiTaoView() {
        tvDoanhThuHomNay = (TextView) findViewById(R.id.tvDoanhThuHomNay);
        tvDoanhThuThang = (TextView) findViewById(R.id.tvDoanhThuThang);
        tvDoanhThuNam = (TextView) findViewById(R.id.tvDoanhThuNam);
        tvDonChoXN = (TextView) findViewById(R.id.tvDonChoXN);
        tvDonDangGiao = (TextView) findViewById(R.id.tvDonDangGiao);
        tvDonHoanThanh = (TextView) findViewById(R.id.tvDonHoanThanh);
        tvDonHuy = (TextView) findViewById(R.id.tvDonHuy);
        tvSPBanChay = (TextView) findViewById(R.id.tvSPBanChay);
        tvSPHetHang = (TextView) findViewById(R.id.tvSPHetHang);
        tvTongChiThang = (TextView) findViewById(R.id.tvTongChiThang);
        tvLoiNhuanThang = (TextView) findViewById(R.id.tvLoiNhuanThang);
    }

    private void taiThongKe() {
        Connection con = null;
        try {
            con = KetNoiDB.layKetNoi();
            if (con == null) {
                hienThiLoiKetNoi("Không tải được thống kê.");
                return;
            }

            String sqlDT = "SELECT "
                    + "ISNULL(SUM(CASE WHEN CAST(NgayDatHang AS DATE)=CAST(GETDATE() AS DATE) AND TrangThaiDonHang=2 THEN TongHoaDon ELSE 0 END),0) AS HomNay,"
                    + "ISNULL(SUM(CASE WHEN MONTH(NgayDatHang)=MONTH(GETDATE()) AND YEAR(NgayDatHang)=YEAR(GETDATE()) AND TrangThaiDonHang=2 THEN TongHoaDon ELSE 0 END),0) AS Thang,"
                    + "ISNULL(SUM(CASE WHEN YEAR(NgayDatHang)=YEAR(GETDATE()) AND TrangThaiDonHang=2 THEN TongHoaDon ELSE 0 END),0) AS Nam "
                    + "FROM DonHang";
            ResultSet rs1 = con.prepareStatement(sqlDT).executeQuery();
            double doanhThuThang = 0;
            if (rs1.next()) {
                tvDoanhThuHomNay.setText(formatTien.format(rs1.getDouble("HomNay")) + " đ");
                doanhThuThang = rs1.getDouble("Thang");
                tvDoanhThuThang.setText(formatTien.format(doanhThuThang) + " đ");
                tvDoanhThuNam.setText(formatTien.format(rs1.getDouble("Nam")) + " đ");
            }

            String sqlTT = "SELECT TrangThaiDonHang, COUNT(*) AS SoDon FROM DonHang GROUP BY TrangThaiDonHang";
            ResultSet rs2 = con.prepareStatement(sqlTT).executeQuery();
            int[] demDon = new int[4];
            while (rs2.next()) {
                int tt = rs2.getInt("TrangThaiDonHang");
                if (tt >= 0 && tt <= 3) {
                    demDon[tt] = rs2.getInt("SoDon");
                }
            }
            tvDonChoXN.setText("Chờ xác nhận: " + demDon[0]);
            tvDonDangGiao.setText("Đang giao: " + demDon[1]);
            tvDonHoanThanh.setText("Hoàn thành: " + demDon[2]);
            tvDonHuy.setText("Đã hủy: " + demDon[3]);

            String sqlSPBC = "SELECT TOP 1 sp.TenSP, SUM(ct.SoLuong) AS TongBan "
                    + "FROM ChiTietHoaDon ct JOIN SanPham sp ON ct.MaSanPham=sp.MaSanPham "
                    + "GROUP BY sp.TenSP ORDER BY TongBan DESC";
            ResultSet rs3 = con.prepareStatement(sqlSPBC).executeQuery();
            if (rs3.next()) {
                tvSPBanChay.setText(rs3.getString("TenSP") + " (" + rs3.getInt("TongBan") + " đôi)");
            } else {
                tvSPBanChay.setText("Chưa có dữ liệu");
            }

            ResultSet rs4 = con.prepareStatement("SELECT COUNT(*) FROM SanPham WHERE SoLuong=0 AND DangBan=1").executeQuery();
            if (rs4.next()) {
                tvSPHetHang.setText(rs4.getInt(1) + " sản phẩm hết hàng");
            }

            String sqlChi = "SELECT ISNULL(SUM(TongCong),0) FROM ChiTieu "
                    + "WHERE MONTH(NgayChi)=MONTH(GETDATE()) AND YEAR(NgayChi)=YEAR(GETDATE())";
            ResultSet rs5 = con.prepareStatement(sqlChi).executeQuery();
            double tongChi = 0;
            if (rs5.next()) {
                tongChi = rs5.getDouble(1);
                tvTongChiThang.setText(formatTien.format(tongChi) + " đ");
            }

            double loiNhuan = doanhThuThang - tongChi;
            tvLoiNhuanThang.setText(formatTien.format(loiNhuan) + " đ");
            tvLoiNhuanThang.setTextColor(loiNhuan >= 0 ? 0xFF2E7D32 : 0xFFC62828);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải thống kê: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private void hienThiLoiKetNoi(String macDinh) {
        String thongBao = KetNoiDB.layThongDiepLoiGanNhat();
        if (thongBao == null || thongBao.trim().length() == 0) {
            thongBao = macDinh;
        }
        Toast.makeText(this, thongBao, Toast.LENGTH_LONG).show();
    }
}
