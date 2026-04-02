package com.example.shopbangiay;

import android.os.Bundle;

import android.app.Activity;
import android.view.Menu;


import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DangNhap extends Activity {
	EditText edtEmail, edtMatKhau;
    Button   btnDangNhap;
    TextView tvThongBaoLoi, tvDiDangKy;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dang_nhap);
		
		
		 edtEmail       = (EditText) findViewById(R.id.edtEmail);
	        edtMatKhau     = (EditText) findViewById(R.id.edtMatKhau);
	        btnDangNhap    = (Button)   findViewById(R.id.btnDangNhap);
	        tvThongBaoLoi  = (TextView) findViewById(R.id.tvThongBaoLoi);
	        tvDiDangKy     = (TextView) findViewById(R.id.tvDiDangKy);
	 
	        btnDangNhap.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                dangNhap();
	            }
	        });
	 
	        tvDiDangKy.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                Intent intent = new Intent(DangNhapActivity.this, DangKyActivity.class);
	                startActivity(intent);
	                finish();
	            }
	        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dang_nhap, menu);
		return true;
	}
	
	private void dangNhap() {
        String email    = edtEmail.getText().toString().trim();
        String matKhau  = edtMatKhau.getText().toString().trim();
 
        // Kiem tra nhap lieu
        if (email.isEmpty() || matKhau.isEmpty()) {
            tvThongBaoLoi.setVisibility(View.VISIBLE);
            tvThongBaoLoi.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
 
        // Ma hoa mat khau
        String matKhauHash = MaHoaHelper.maHoaSHA256(matKhau);
 
        // Ket noi CSDL va kiem tra
        try {
            Connection con = KetNoiCSDL.layKetNoi();
            if (con == null) {
                tvThongBaoLoi.setVisibility(View.VISIBLE);
                tvThongBaoLoi.setText("Không kết nối được máy chủ!");
                return;
            }
 
            String sql = "SELECT MaNguoiDung, HovaTen, Email, Phone, VaiTro, Anh, BiKhoa " +
                         "FROM NguoiDung WHERE Email = ? AND PasswordHash = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, matKhauHash);
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                int biKhoa = rs.getInt("BiKhoa");
                if (biKhoa == 1) {
                    tvThongBaoLoi.setVisibility(View.VISIBLE);
                    tvThongBaoLoi.setText("Tài khoản đã bị khóa!");
                    con.close();
                    return;
                }
 
                // Luu thong tin nguoi dung vao bien toan cuc
                NguoiDung nguoiDung = new NguoiDung();
                nguoiDung.setMaNguoiDung(rs.getString("MaNguoiDung").trim());
                nguoiDung.setHovaTen(rs.getString("HovaTen"));
                nguoiDung.setEmail(rs.getString("Email"));
                nguoiDung.setPhone(rs.getString("Phone"));
                nguoiDung.setVaiTro(rs.getInt("VaiTro"));
                nguoiDung.setAnh(rs.getString("Anh"));
 
                // Luu vao Application (bien toan cuc)
                AppData.nguoiDungHienTai = nguoiDung;
 
                con.close();
 
                // Dieu huong theo vai tro
                if (nguoiDung.laAdmin()) {
                    Intent intent = new Intent(DangNhapActivity.this, AdminDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DangNhapActivity.this, ManHinhChinhActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
 
            } else {
                con.close();
                tvThongBaoLoi.setVisibility(View.VISIBLE);
                tvThongBaoLoi.setText("Email hoặc mật khẩu không đúng!");
            }
 
        } catch (Exception e) {
            e.printStackTrace();
            tvThongBaoLoi.setVisibility(View.VISIBLE);
            tvThongBaoLoi.setText("Lỗi kết nối: " + e.getMessage());
        }
    }

}
