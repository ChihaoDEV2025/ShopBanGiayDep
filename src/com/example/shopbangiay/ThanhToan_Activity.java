package com.example.shopbangiay;

import Models.DonHang;
import Models.GioHangItem;
import TienIchMoRong.GioHang;
import TienIchMoRong.KetNoiDB;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.view.Menu;

public class ThanhToan_Activity extends Activity {
	EditText edtDiaChi, edtGhiChu;
    TextView tvTongTien;
    Button   btnXacNhanDatHang;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thanh_toan);
		
		 edtDiaChi       = (EditText) findViewById(R.id.edtDiaChi);
	        edtGhiChu       = (EditText) findViewById(R.id.edtGhiChu);
	        tvTongTien      = (TextView) findViewById(R.id.tvTongTien);
	        btnXacNhanDatHang = (Button) findViewById(R.id.btnXacNhanDatHang);
	 
	        double tongTien = GioHang.getInstance().getTongTien();
	        tvTongTien.setText("Tổng thanh toán: " + formatTien.format(tongTien) + " đ");
	 
	        btnXacNhanDatHang.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                datHang();
	            }
	        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.thanh_toan, menu);
		return true;
	}

	
	private void datHang() {
        String diaChi = edtDiaChi.getText().toString().trim();
        String ghiChu = edtGhiChu.getText().toString().trim();
 
        if (diaChi.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
            return;
        }
 
        try {
            Connection con = KetNoiDB.layKetNoi();
            if (con == null) {
                Toast.makeText(this, "Không kết nối được!", Toast.LENGTH_SHORT).show();
                return;
            }
 
            // Tao ma don hang moi
            String maDonHangMoi = taoMaDonHang(con);
            double tongTien = GioHang.getInstance().getTongTien();
            String maNguoiDung = NguoiDungHienTai_Activity.nguoiDungHienTai.getMaNguoiDung();
 
            // Insert DonHang
            String sqlDH = "INSERT INTO DonHang (MaDonHang, MaNguoiDung, TongHoaDon, " +
                           "TrangThaiDonHang, DiaChiGiaoHang, PhuongThucThanhToan, GhiChu) " +
                           "VALUES (?, ?, ?, 0, ?, 'COD', ?)";
            PreparedStatement psDH = con.prepareStatement(sqlDH);
            psDH.setString(1, maDonHangMoi);
            psDH.setString(2, maNguoiDung);
            psDH.setDouble(3, tongTien);
            psDH.setString(4, diaChi);
            psDH.setString(5, ghiChu.isEmpty() ? null : ghiChu);
            psDH.executeUpdate();
 
            // Insert ChiTietHoaDon
            int stt = 1;
            for (GioHangItem item : GioHang.getInstance().getDanhSachItem()) {
                String maChiTiet = String.format("HT%03d", taoSoChiTiet(con));
                String sqlCT = "INSERT INTO ChiTietHoaDon VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psCT = con.prepareStatement(sqlCT);
                psCT.setString(1, maChiTiet);
                psCT.setString(2, maDonHangMoi);
                psCT.setString(3, item.getSanPham().getMaSanPham());
                psCT.setInt(4, item.getSoLuong());
                psCT.setDouble(5, item.getSanPham().getGiaHienThi());
                psCT.executeUpdate();
                stt++;
            }
 
            con.close();
 
            // Xoa gio hang
            GioHang.getInstance().xoaGio();
 
            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
 
            // Quay ve man hinh chinh
            new android.os.Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    Intent intent = new Intent(ThanhToan_Activity.this, TrangChu_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }, 1500);
 
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi đặt hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
