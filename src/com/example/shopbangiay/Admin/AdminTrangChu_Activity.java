package com.example.shopbangiay.Admin;

import Models.NguoiDung;
import TienIchMoRong.KetNoiDB;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopbangiay.NguoiDungHienTai_Activity;
import com.example.shopbangiay.R;
import com.example.shopbangiay.TrangChaoMung_Activity;

import java.sql.Connection;
import java.sql.ResultSet;

public class AdminTrangChu_Activity extends Activity {
    TextView tvTenAdmin;
    TextView tvTongSanPham, tvTongDonHang, tvTongNguoiDung, tvDoanhThuHomNay;
    Button btnQuanLySanPham, btnQuanLyDonHang, btnQuanLyNguoiDung;
    Button btnQuanLyChiTieu, btnThongKe, btnDangXuat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_trang_chu);

        khoiTaoView();
        hienThiTenAdmin();
        taiThongKeTongQuan();
        thietLapSuKien();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_trang_chu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiThongKeTongQuan();
    }

    private void khoiTaoView() {
        tvTenAdmin = (TextView) findViewById(R.id.tvTenAdmin);
        tvTongSanPham = (TextView) findViewById(R.id.tvTongSanPham);
        tvTongDonHang = (TextView) findViewById(R.id.tvTongDonHang);
        tvTongNguoiDung = (TextView) findViewById(R.id.tvTongNguoiDung);
        tvDoanhThuHomNay = (TextView) findViewById(R.id.tvDoanhThuHomNay);
        btnQuanLySanPham = (Button) findViewById(R.id.btnQuanLySanPham);
        btnQuanLyDonHang = (Button) findViewById(R.id.btnQuanLyDonHang);
        btnQuanLyNguoiDung = (Button) findViewById(R.id.btnQuanLyNguoiDung);
        btnQuanLyChiTieu = (Button) findViewById(R.id.btnQuanLyChiTieu);
        btnThongKe = (Button) findViewById(R.id.btnThongKe);
        btnDangXuat = (Button) findViewById(R.id.btnDangXuat);
    }

    private void hienThiTenAdmin() {
        NguoiDung nd = NguoiDungHienTai_Activity.nguoiDungHienTai;
        if (nd != null) {
            tvTenAdmin.setText("Xin chào, " + nd.getHovaTen());
        }
    }

    private void taiThongKeTongQuan() {
        Connection con = null;
        try {
            con = KetNoiDB.layKetNoi();
            if (con == null) {
                hienThiLoiKetNoi("Không tải được thống kê tổng quan.");
                return;
            }

            ResultSet rs1 = con.prepareStatement("SELECT COUNT(*) FROM SanPham WHERE DangBan = 1").executeQuery();
            if (rs1.next()) {
                tvTongSanPham.setText(rs1.getInt(1) + "\nSản phẩm");
            }

            ResultSet rs2 = con.prepareStatement("SELECT COUNT(*) FROM DonHang").executeQuery();
            if (rs2.next()) {
                tvTongDonHang.setText(rs2.getInt(1) + "\nĐơn hàng");
            }

            ResultSet rs3 = con.prepareStatement("SELECT COUNT(*) FROM NguoiDung WHERE VaiTro = 1").executeQuery();
            if (rs3.next()) {
                tvTongNguoiDung.setText(rs3.getInt(1) + "\nKhách hàng");
            }

            String sqlDT = "SELECT ISNULL(SUM(TongHoaDon),0) FROM DonHang "
                    + "WHERE CAST(NgayDatHang AS DATE) = CAST(GETDATE() AS DATE) "
                    + "AND TrangThaiDonHang = 2";
            ResultSet rs4 = con.prepareStatement(sqlDT).executeQuery();
            if (rs4.next()) {
                java.text.NumberFormat fmt = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                tvDoanhThuHomNay.setText(fmt.format(rs4.getDouble(1)) + " đ\nHôm nay");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải thống kê tổng quan: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private void thietLapSuKien() {
        btnQuanLySanPham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminTrangChu_Activity.this, AdminSanPham_Activity.class));
            }
        });

        btnQuanLyDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminTrangChu_Activity.this, AdminDonHang_Activity.class));
            }
        });

        btnQuanLyNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminTrangChu_Activity.this, AdminNguoiDung_Activity.class));
            }
        });

        btnQuanLyChiTieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminTrangChu_Activity.this, AdminChiTieu_Activity.class));
            }
        });

        btnThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminTrangChu_Activity.this, AdminThongKe_Activity.class));
            }
        });

        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NguoiDungHienTai_Activity.dangXuat();
                Intent i = new Intent(AdminTrangChu_Activity.this, TrangChaoMung_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }

    private void hienThiLoiKetNoi(String macDinh) {
        String thongBao = KetNoiDB.layThongDiepLoiGanNhat();
        if (thongBao == null || thongBao.trim().length() == 0) {
            thongBao = macDinh;
        }
        Toast.makeText(this, thongBao, Toast.LENGTH_LONG).show();
    }
}
