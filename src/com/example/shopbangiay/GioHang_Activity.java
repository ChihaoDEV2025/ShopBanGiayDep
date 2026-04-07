package com.example.shopbangiay;

import Models.NguoiDung;
import TienIchMoRong.GioHang;
import TienIchMoRong.KetNoiDB;
import TienIchMoRong.MaHoaMatKhau;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;

public class GioHang_Activity extends Activity {
	ListView lvGioHang;
    TextView tvTongTien, tvGioTrong;
    Button   btnThanhToan;
    GioHangAdapter adapter;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));
  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gio_hang);
		
		 lvGioHang   = (ListView) findViewById(R.id.lvGioHang);
	        tvTongTien  = (TextView) findViewById(R.id.tvTongTien);
	        tvGioTrong  = (TextView) findViewById(R.id.tvGioTrong);
	        btnThanhToan= (Button)   findViewById(R.id.btnThanhToan);
	 
	        hienThiGioHang();
	 
	        btnThanhToan.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                if (GioHang_Activity.getInstance().isEmpty()) {
	                    Toast.makeText(GioHang_Activity.this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
	                    return;
	                }
	                startActivity(new Intent(GioHang_Activity.this, ThanhToan_Activity.class));
	            }
	        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gio_hang, menu);
		return true;
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        hienThiGioHang();
    }
	
	void hienThiGioHang() {
        if (GioHang_Activity.getInstance().isEmpty()) {
            tvGioTrong.setVisibility(View.VISIBLE);
            lvGioHang.setVisibility(View.GONE);
            btnThanhToan.setEnabled(false);
        } else {
            tvGioTrong.setVisibility(View.GONE);
            lvGioHang.setVisibility(View.VISIBLE);
            btnThanhToan.setEnabled(true);
        }
 
        adapter = new GioHangAdapter(this, GioHang_Activity.getInstance().getDanhSachItem(), this);
        lvGioHang.setAdapter(adapter);
 
        double tongTien = GioHang_Activity.getInstance().getTongTien();
        tvTongTien.setText("Tổng: " + formatTien.format(tongTien) + " đ");
    }

	public static GioHang getInstance() {
		return GioHang.getInstance();
	}
}
 
