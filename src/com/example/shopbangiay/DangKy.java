package com.example.shopbangiay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import android.app.Activity;
import android.view.Menu;

public class DangKy extends Activity {
	EditText edtHovaTen, edtEmail, edtPhone, edtMatKhau, edtXacNhanMatKhau;
    Button   btnDangKy;
    TextView tvThongBao, tvDiDangNhap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dang_ky);
		
		edtHovaTen        = (EditText) findViewById(R.id.edtHovaTen);
        edtEmail          = (EditText) findViewById(R.id.edtEmail);
        edtPhone          = (EditText) findViewById(R.id.edtPhone);
        edtMatKhau        = (EditText) findViewById(R.id.edtMatKhau);
        edtXacNhanMatKhau = (EditText) findViewById(R.id.edtXacNhanMatKhau);
        btnDangKy         = (Button)   findViewById(R.id.btnDangKy);
        tvThongBao        = (TextView) findViewById(R.id.tvThongBao);
        tvDiDangNhap      = (TextView) findViewById(R.id.tvDiDangNhap);
        
        
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangKy();
            }
        });
 
        tvDiDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lai man hinh dang nhap
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dang_ky, menu);
		return true;
	}
	
	
	private void dangKy() {
        String hovaTen        = edtHovaTen.getText().toString().trim();
        String email          = edtEmail.getText().toString().trim();
        String phone          = edtPhone.getText().toString().trim();
        String matKhau        = edtMatKhau.getText().toString().trim();
        String xacNhanMatKhau = edtXacNhanMatKhau.getText().toString().trim();
 
        // Kiem tra nhap lieu
        if (hovaTen.isEmpty() || email.isEmpty() || matKhau.isEmpty()) {
            hienThiThongBao("Vui lòng nhập đầy đủ thông tin bắt buộc!", true);
            return;
        }
 
        if (!matKhau.equals(xacNhanMatKhau)) {
            hienThiThongBao("Mật khẩu xác nhận không khớp!", true);
            return;
        }
 
        if (matKhau.length() < 6) {
            hienThiThongBao("Mật khẩu phải có ít nhất 6 ký tự!", true);
            return;
        }
 
        try {
            Connection con = KetNoiCSDL.layKetNoi();
            if (con == null) {
                hienThiThongBao("Không kết nối được máy chủ!", true);
                return;
            }
 
            // Kiem tra email da ton tai chua
            String sqlKiem = "SELECT COUNT(*) FROM NguoiDung WHERE Email = ?";
            PreparedStatement psKiem = con.prepareStatement(sqlKiem);
            psKiem.setString(1, email);
            ResultSet rsKiem = psKiem.executeQuery();
            rsKiem.next();
            if (rsKiem.getInt(1) > 0) {
                con.close();
                hienThiThongBao("Email này đã được đăng ký!", true);
                return;
            }
 
            // Tao ma nguoi dung moi
            String maMoi = taoMaNguoiDung(con);
            String matKhauHash = MaHoaHelper.maHoaSHA256(matKhau);
 
            // Insert nguoi dung moi
            String sqlThem = "INSERT INTO NguoiDung " +
                "(MaNguoiDung, HovaTen, Email, Phone, PasswordHash, VaiTro, Anh) " +
                "VALUES (?, ?, ?, ?, ?, 1, 'images/default_avatar.png')";
            PreparedStatement psThem = con.prepareStatement(sqlThem);
            psThem.setString(1, maMoi);
            psThem.setString(2, hovaTen);
            psThem.setString(3, email);
            psThem.setString(4, phone.isEmpty() ? null : phone);
            psThem.setString(5, matKhauHash);
            psThem.executeUpdate();
 
            con.close();
 
            hienThiThongBao("Đăng ký thành công! Vui lòng đăng nhập.", false);
 
            // Chuyen sang man hinh dang nhap sau 1.5 giay
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(DangKyActivity.this, DangNhapActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1500);
 
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi: " + e.getMessage(), true);
        }
    }
 
    // Tao ma nguoi dung moi: ND001, ND002, ...
    private String taoMaNguoiDung(Connection con) throws Exception {
        String sql = "SELECT MAX(MaNguoiDung) FROM NguoiDung";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next() && rs.getString(1) != null) {
            String maMax = rs.getString(1).trim();
            int soMax = Integer.parseInt(maMax.substring(2));
            return String.format("ND%03d", soMax + 1);
        }
        return "ND001";
    }
 
    private void hienThiThongBao(String thongBao, boolean laLoi) {
        tvThongBao.setVisibility(View.VISIBLE);
        tvThongBao.setText(thongBao);
        if (laLoi) {
            tvThongBao.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvThongBao.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

}
