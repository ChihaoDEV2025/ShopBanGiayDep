package com.example.shopbangiay;

import Models.GioHangItem;
import TienIchMoRong.GioHang;
import TienIchMoRong.KetNoiDB;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;

public class ThanhToan_Activity extends Activity {
    EditText edtDiaChi, edtGhiChu;
    TextView tvTongTien;
    Button btnXacNhanDatHang;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        edtDiaChi = (EditText) findViewById(R.id.edtDiaChi);
        edtGhiChu = (EditText) findViewById(R.id.edtGhiChu);
        tvTongTien = (TextView) findViewById(R.id.tvTongTien);
        btnXacNhanDatHang = (Button) findViewById(R.id.btnXacNhanDatHang);

        double tongTien = GioHang.getInstance().getTongTien();
        tvTongTien.setText("Tổng thanh toán: " + formatTien.format(tongTien) + " đ");

        btnXacNhanDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String diaChi = edtDiaChi.getText().toString().trim();
                if (diaChi.isEmpty()) {
                    Toast.makeText(ThanhToan_Activity.this, "Vui lòng nhập địa chỉ!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Gọi lớp xử lý chạy ngầm thay vì gọi trực tiếp datHang()
                new XuLyThanhToanTask().execute(diaChi, edtGhiChu.getText().toString().trim());
            }
        });
    }

    // Lớp xử lý chạy ngầm để tránh lỗi NetworkOnMainThread
    private class XuLyThanhToanTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ThanhToan_Activity.this);
            progressDialog.setMessage("Đang xử lý đơn hàng...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String diaChi = params[0];
            String ghiChu = params[1];
            Connection con = null;

            try {
                con = KetNoiDB.layKetNoi();
                if (con == null) return "Không thể kết nối cơ sở dữ liệu!";

                con.setAutoCommit(false); // Bắt đầu Transaction để đảm bảo an toàn dữ liệu

                // 1. Tạo mã đơn hàng
                String maDonHang = taoMaDonHang(con);
                double tongTien = GioHang.getInstance().getTongTien();
                String maNguoiDung = NguoiDungHienTai_Activity.nguoiDungHienTai.getMaNguoiDung();

                // 2. Lưu đơn hàng (INSERT DonHang)
                String sqlDH = "INSERT INTO DonHang (MaDonHang, MaNguoiDung, TongHoaDon, TrangThaiDonHang, " +
                               "DiaChiGiaoHang, PhuongThucThanhToan, GhiChu) VALUES (?, ?, ?, 0, ?, 'COD', ?)";
                PreparedStatement psDH = con.prepareStatement(sqlDH);
                psDH.setString(1, maDonHang);
                psDH.setString(2, maNguoiDung);
                psDH.setDouble(3, tongTien);
                psDH.setString(4, diaChi);
                psDH.setString(5, ghiChu.isEmpty() ? null : ghiChu);
                psDH.executeUpdate();

                // 3. Lưu chi tiết (INSERT ChiTietHoaDon)
                for (GioHangItem item : GioHang.getInstance().getDanhSachItem()) {
                    String maCT = String.format("HT%03d", taoSoChiTiet(con));
                    String sqlCT = "INSERT INTO ChiTietHoaDon VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement psCT = con.prepareStatement(sqlCT);
                    psCT.setString(1, maCT);
                    psCT.setString(2, maDonHang);
                    psCT.setString(3, item.getSanPham().getMaSanPham());
                    psCT.setInt(4, item.getSoLuong());
                    psCT.setDouble(5, item.getSanPham().getGiaHienThi());
                    psCT.executeUpdate();
                }

                con.commit(); // Hoàn tất lưu dữ liệu
                return "SUCCESS";
            } catch (Exception e) {
                if (con != null) { try { con.rollback(); } catch (Exception ex) {} }
                return "Lỗi: " + e.getMessage();
            } finally {
                if (con != null) { try { con.close(); } catch (Exception e) {} }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result.equals("SUCCESS")) {
                GioHang.getInstance().xoaGio();
                Toast.makeText(ThanhToan_Activity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(ThanhToan_Activity.this, TrangChu_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ThanhToan_Activity.this, result, Toast.LENGTH_LONG).show();
            }
        }
    }

    private String taoMaDonHang(Connection con) throws Exception {
        String sql = "SELECT MAX(MaDonHang) FROM DonHang";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next() && rs.getString(1) != null) {
            int soMax = Integer.parseInt(rs.getString(1).trim().substring(2));
            return String.format("DH%03d", soMax + 1);
        }
        return "DH001";
    }

    private int taoSoChiTiet(Connection con) throws Exception {
        String sql = "SELECT COUNT(*) FROM ChiTietHoaDon";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) + 1;
    }
}