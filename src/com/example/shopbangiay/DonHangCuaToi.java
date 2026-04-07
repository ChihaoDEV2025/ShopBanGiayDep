package com.example.shopbangiay;

import Models.DonHang;
import TienIchMoRong.KetNoiDB;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DonHangCuaToi extends Activity {
    ListView lvDonHang;
    TextView tvKhongCoDon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_don_hang_cua_toi);

        lvDonHang = (ListView) findViewById(R.id.lvDonHang);
        tvKhongCoDon = (TextView) findViewById(R.id.tvKhongCoDon);
        taiDonHang();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.don_hang_cua_toi, menu);
		return true;
	}

    private void taiDonHang() {
        try {
            Connection con = KetNoiDB.layKetNoi();
            if (con == null || NguoiDungHienTai_Activity.nguoiDungHienTai == null) return;

            String sql = "SELECT * FROM DonHang WHERE MaNguoiDung=? ORDER BY NgayDatHang DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, NguoiDungHienTai_Activity.nguoiDungHienTai.getMaNguoiDung());
            ResultSet rs = ps.executeQuery();

            List<DonHang> dsDonHang = new ArrayList<DonHang>();
            while (rs.next()) {
                DonHang dh = new DonHang();
                dh.setMaDonHang(rs.getString("MaDonHang").trim());
                dh.setNgayDatHang(rs.getString("NgayDatHang"));
                dh.setTongHoaDon(rs.getDouble("TongHoaDon"));
                dh.setTrangThaiDonHang(rs.getInt("TrangThaiDonHang"));
                dh.setDiaChiGiaoHang(rs.getString("DiaChiGiaoHang"));
                dsDonHang.add(dh);
            }
            con.close();

            if (dsDonHang.isEmpty()) {
                tvKhongCoDon.setVisibility(View.VISIBLE);
                lvDonHang.setVisibility(View.GONE);
            } else {
                tvKhongCoDon.setVisibility(View.GONE);
                lvDonHang.setVisibility(View.VISIBLE);
                lvDonHang.setAdapter(new DonHangAdapter(this, dsDonHang));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
