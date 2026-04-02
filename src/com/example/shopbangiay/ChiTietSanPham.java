package com.example.shopbangiay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
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
 
import android.app.Activity;
import android.view.Menu;



public class ChiTietSanPham extends Activity {
	
	ImageView imgSanPham, imgYeuThich;
    TextView  tvTenSP, tvGia, tvGiaGoc, tvThuongHieu, tvSoLuong, tvGioiThieu, tvSoSao;
    TextView  tvNhanSale;
    Button    btnThemGio, btnMuaNgay;
    ListView  lvDanhGia;
 
    SanPham   sanPham;
    String    maSanPham;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));
 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chi_tiet_san_pham);
		
		
		 maSanPham   = getIntent().getStringExtra("maSanPham");
		 
	        imgSanPham  = (ImageView) findViewById(R.id.imgSanPham);
	        imgYeuThich = (ImageView) findViewById(R.id.imgYeuThich);
	        tvTenSP     = (TextView)  findViewById(R.id.tvTenSP);
	        tvGia       = (TextView)  findViewById(R.id.tvGia);
	        tvGiaGoc    = (TextView)  findViewById(R.id.tvGiaGoc);
	        tvThuongHieu= (TextView)  findViewById(R.id.tvThuongHieu);
	        tvSoLuong   = (TextView)  findViewById(R.id.tvSoLuong);
	        tvGioiThieu = (TextView)  findViewById(R.id.tvGioiThieu);
	        tvSoSao     = (TextView)  findViewById(R.id.tvSoSao);
	        tvNhanSale  = (TextView)  findViewById(R.id.tvNhanSale);
	        btnThemGio  = (Button)    findViewById(R.id.btnThemGio);
	        btnMuaNgay  = (Button)    findViewById(R.id.btnMuaNgay);
	        lvDanhGia   = (ListView)  findViewById(R.id.lvDanhGia);
	 
	        taiChiTietSanPham();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chi_tiet_san_pham, menu);
		return true;
	}

	
	private void taiChiTietSanPham() {
        try {
            Connection con = KetNoiCSDL.layKetNoi();
            if (con == null) return;
 
            String sql = "SELECT * FROM SanPham WHERE MaSanPham = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maSanPham);
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                sanPham = new SanPham(
                    rs.getString("MaSanPham").trim(),
                    rs.getString("TenSP"),
                    rs.getDouble("Gia"),
                    rs.getDouble("GiaSale"),
                    rs.getString("ImageURL"),
                    rs.getString("GioiThieu"),
                    rs.getInt("SoLuong"),
                    rs.getInt("DangBan"),
                    rs.getString("ThuongHieu"),
                    rs.getInt("SoLuotReview")
                );
 
                // Kiem tra yeu thich
                if (AppData.nguoiDungHienTai != null) {
                    String sqlYT = "SELECT COUNT(*) FROM YeuThich WHERE MaNguoiDung=? AND MaSanPham=?";
                    PreparedStatement psYT = con.prepareStatement(sqlYT);
                    psYT.setString(1, AppData.nguoiDungHienTai.getMaNguoiDung());
                    psYT.setString(2, maSanPham);
                    ResultSet rsYT = psYT.executeQuery();
                    rsYT.next();
                    sanPham.setDaYeuThich(rsYT.getInt(1) > 0);
                }
 
                hienThiSanPham();
            }
 
            // Tai danh gia
            taiDanhGia(con);
            con.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    private void hienThiSanPham() {
        AnhHelper.hienThiAnh(this, sanPham.getImageURL(), imgSanPham);
        tvTenSP.setText(sanPham.getTenSP());
        tvThuongHieu.setText("Thương hiệu: " + sanPham.getThuongHieu());
        tvGioiThieu.setText(sanPham.getGioiThieu());
        tvSoLuong.setText("Còn lại: " + sanPham.getSoLuong() + " đôi");
        tvSoSao.setText("★ " + sanPham.getSoLuotReview() + " đánh giá");
 
        if (sanPham.dangSale()) {
            tvGia.setText(formatTien.format(sanPham.getGiaSale()) + " đ");
            tvGiaGoc.setVisibility(View.VISIBLE);
            tvGiaGoc.setText(formatTien.format(sanPham.getGia()) + " đ");
            tvGiaGoc.setPaintFlags(tvGiaGoc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvNhanSale.setVisibility(View.VISIBLE);
        } else {
            tvGia.setText(formatTien.format(sanPham.getGia()) + " đ");
            tvGiaGoc.setVisibility(View.GONE);
            tvNhanSale.setVisibility(View.GONE);
        }
 
        // Icon yeu thich
        capNhatIconYeuThich();
 
        // Nut yeu thich
        imgYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleYeuThich();
            }
        });
 
        // Them vao gio
        btnThemGio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sanPham.getSoLuong() <= 0) {
                    Toast.makeText(ChiTietSanPhamActivity.this, "Hết hàng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                GioHang.getInstance().themSanPham(sanPham, 1);
                Toast.makeText(ChiTietSanPhamActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
 
        // Mua ngay
        btnMuaNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sanPham.getSoLuong() <= 0) {
                    Toast.makeText(ChiTietSanPhamActivity.this, "Hết hàng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                GioHang.getInstance().themSanPham(sanPham, 1);
                startActivity(new Intent(ChiTietSanPhamActivity.this, GioHangActivity.class));
            }
        });
    }
 
    private void taiDanhGia(Connection con) throws Exception {
        String sql = "SELECT dg.SoSao, dg.NoiDung, dg.NgayDanhGia, nd.HovaTen " +
                     "FROM DanhGia dg JOIN NguoiDung nd ON dg.MaNguoiDung=nd.MaNguoiDung " +
                     "WHERE dg.MaSanPham=? ORDER BY dg.NgayDanhGia DESC";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, maSanPham);
        ResultSet rs = ps.executeQuery();
 
        List<DanhGia> dsDanhGia = new ArrayList<>();
        while (rs.next()) {
            DanhGia dg = new DanhGia();
            dg.setSoSao(rs.getInt("SoSao"));
            dg.setNoiDung(rs.getString("NoiDung"));
            dg.setNgayDanhGia(rs.getString("NgayDanhGia"));
            dg.setHovaTen(rs.getString("HovaTen"));
            dsDanhGia.add(dg);
        }
 
        DanhGiaAdapter adapter = new DanhGiaAdapter(this, dsDanhGia);
        lvDanhGia.setAdapter(adapter);
    }
 
    private void toggleYeuThich() {
        if (AppData.nguoiDungHienTai == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = KetNoiCSDL.layKetNoi();
                    if (con == null) return;
                    String maNguoiDung = AppData.nguoiDungHienTai.getMaNguoiDung();
                    if (sanPham.isDaYeuThich()) {
                        String sql = "DELETE FROM YeuThich WHERE MaNguoiDung=? AND MaSanPham=?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, maNguoiDung); ps.setString(2, maSanPham);
                        ps.executeUpdate();
                        sanPham.setDaYeuThich(false);
                    } else {
                        String sql = "INSERT INTO YeuThich (MaNguoiDung, MaSanPham) VALUES (?,?)";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, maNguoiDung); ps.setString(2, maSanPham);
                        ps.executeUpdate();
                        sanPham.setDaYeuThich(true);
                    }
                    con.close();
                    runOnUiThread(new Runnable() {
                        @Override public void run() { capNhatIconYeuThich(); }
                    });
                } catch (Exception e) { e.printStackTrace(); }
            }
        }).start();
    }
 
    private void capNhatIconYeuThich() {
        if (sanPham.isDaYeuThich()) {
            imgYeuThich.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            imgYeuThich.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }
}
