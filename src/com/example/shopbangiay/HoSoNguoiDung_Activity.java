package com.example.shopbangiay;

import Models.NguoiDung;
import TienIchMoRong.DocAnh;
import TienIchMoRong.KetNoiDB;
import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

import android.view.Menu;


public class HoSoNguoiDung_Activity extends Activity {
	
	TextView tvHovaTen, tvEmail, tvPhone;
    EditText edtHovaTenMoi, edtPhoneMoi;
    Button   btnCapNhat;
    ImageView imgAvatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ho_so_nguoi_dung);
		
		tvHovaTen    = (TextView)  findViewById(R.id.tvHovaTen);
        tvEmail      = (TextView)  findViewById(R.id.tvEmail);
        tvPhone      = (TextView)  findViewById(R.id.tvPhone);
        edtHovaTenMoi= (EditText)  findViewById(R.id.edtHovaTenMoi);
        edtPhoneMoi  = (EditText)  findViewById(R.id.edtPhoneMoi);
        btnCapNhat   = (Button)    findViewById(R.id.btnCapNhat);
        imgAvatar    = (ImageView) findViewById(R.id.imgAvatar);
 
        hienThiThongTin();
 
        btnCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { capNhatThongTin(); }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ho_so_nguoi_dung, menu);
		return true;
	}

	private void hienThiThongTin() {
        NguoiDung nd = NguoiDungHienTai_Activity.nguoiDungHienTai;
        tvHovaTen.setText(nd.getHovaTen());
        tvEmail.setText(nd.getEmail());
        tvPhone.setText(nd.getPhone() != null ? nd.getPhone() : "Chưa có");
        edtHovaTenMoi.setText(nd.getHovaTen());
        edtPhoneMoi.setText(nd.getPhone() != null ? nd.getPhone() : "");
        DocAnh.hienThiAnh(this, nd.getAnh(), imgAvatar);
    }
 
    private void capNhatThongTin() {
        String hovaTenMoi = edtHovaTenMoi.getText().toString().trim();
        String phoneMoi   = edtPhoneMoi.getText().toString().trim();
 
        if (hovaTenMoi.isEmpty()) {
            Toast.makeText(this, "Họ tên không được trống!", Toast.LENGTH_SHORT).show();
            return;
        }
 
        try {
            Connection con = KetNoiDB.layKetNoi();
            if (con == null) return;
 
            String sql = "UPDATE NguoiDung SET HovaTen=?, Phone=? WHERE MaNguoiDung=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, hovaTenMoi);
            ps.setString(2, phoneMoi.isEmpty() ? null : phoneMoi);
            ps.setString(3, NguoiDungHienTai_Activity.nguoiDungHienTai.getMaNguoiDung());
            ps.executeUpdate();
            con.close();
 
            // Cap nhat bien toan cuc
            NguoiDungHienTai_Activity.nguoiDungHienTai.setHovaTen(hovaTenMoi);
            NguoiDungHienTai_Activity.nguoiDungHienTai.setPhone(phoneMoi);
 
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            hienThiThongTin();
 
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
