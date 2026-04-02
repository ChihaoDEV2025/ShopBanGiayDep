package com.example.shopbangiay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.view.Menu;

public class TrangChu extends Activity {
	 // Header
    TextView  tvTenShop, tvTenNguoiDung;
    ImageView imgAvatar;
 
    // Tim kiem
    EditText  edtTimKiem;
    ImageView imgGioHang;
    TextView  tvSoLuongGio;
    ImageView imgYeuThich;
 
    // Loc thuong hieu
    LinearLayout layoutThuongHieu;
    Button btnTatCa, btnNike, btnAdidas, btnVans, btnConverse, btnPuma, btnNewBalance;
 
    // Danh sach san pham
    ListView lvSanPham;
 
    // Du lieu
    List<SanPham> danhSachSanPham     = new ArrayList<>();
    List<SanPham> danhSachGoc         = new ArrayList<>();
    SanPhamAdapter adapter;
 
    String thuongHieuDangLoc = "";
    boolean dangLocYeuThich  = false;
 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trang_chu);
		
		khoiTaoView();
        hienThiThongTinNguoiDung();
        taiDanhSachSanPham();
        thietLapSuKien();
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        // Cap nhat so luong gio hang
        capNhatBadgeGioHang();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trang_chu, menu);
		return true;
	}
	
	private void khoiTaoView() {
        tvTenShop        = (TextView)     findViewById(R.id.tvTenShop);
        tvTenNguoiDung   = (TextView)     findViewById(R.id.tvTenNguoiDung);
        imgAvatar        = (ImageView)    findViewById(R.id.imgAvatar);
        edtTimKiem       = (EditText)     findViewById(R.id.edtTimKiem);
        imgGioHang       = (ImageView)    findViewById(R.id.imgGioHang);
        tvSoLuongGio     = (TextView)     findViewById(R.id.tvSoLuongGio);
        imgYeuThich      = (ImageView)    findViewById(R.id.imgYeuThich);
        layoutThuongHieu = (LinearLayout) findViewById(R.id.layoutThuongHieu);
        btnTatCa         = (Button)       findViewById(R.id.btnTatCa);
        btnNike          = (Button)       findViewById(R.id.btnNike);
        btnAdidas        = (Button)       findViewById(R.id.btnAdidas);
        btnVans          = (Button)       findViewById(R.id.btnVans);
        btnConverse      = (Button)       findViewById(R.id.btnConverse);
        btnPuma          = (Button)       findViewById(R.id.btnPuma);
        btnNewBalance    = (Button)       findViewById(R.id.btnNewBalance);
        lvSanPham        = (ListView)     findViewById(R.id.lvSanPham);
    }
 
    private void hienThiThongTinNguoiDung() {
        if (AppData.nguoiDungHienTai != null) {
            tvTenNguoiDung.setText(AppData.nguoiDungHienTai.getHovaTen());
        }
        tvTenShop.setText("HHK - Shoe Shop");
    }
 
    private void taiDanhSachSanPham() {
        danhSachGoc.clear();
        danhSachSanPham.clear();
 
        try {
            Connection con = KetNoiCSDL.layKetNoi();
            if (con == null) {
                Toast.makeText(this, "Không kết nối được!", Toast.LENGTH_SHORT).show();
                return;
            }
 
            String sql = "SELECT sp.MaSanPham, sp.TenSP, sp.Gia, sp.GiaSale, sp.ImageURL, " +
                         "sp.GioiThieu, sp.SoLuong, sp.DangBan, sp.ThuongHieu, sp.SoLuotReview " +
                         "FROM SanPham sp WHERE sp.DangBan = 1 ORDER BY sp.TenSP";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                SanPham sp = new SanPham(
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
                danhSachGoc.add(sp);
            }
 
            // Kiem tra yeu thich
            if (AppData.nguoiDungHienTai != null) {
                String sqlYT = "SELECT MaSanPham FROM YeuThich WHERE MaNguoiDung = ?";
                PreparedStatement psYT = con.prepareStatement(sqlYT);
                psYT.setString(1, AppData.nguoiDungHienTai.getMaNguoiDung());
                ResultSet rsYT = psYT.executeQuery();
                List<String> dsYeuThich = new ArrayList<>();
                while (rsYT.next()) {
                    dsYeuThich.add(rsYT.getString("MaSanPham").trim());
                }
                for (SanPham sp : danhSachGoc) {
                    sp.setDaYeuThich(dsYeuThich.contains(sp.getMaSanPham()));
                }
            }
 
            con.close();
 
            danhSachSanPham.addAll(danhSachGoc);
            adapter = new SanPhamAdapter(this, danhSachSanPham);
            lvSanPham.setAdapter(adapter);
 
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
        }
    }
 
    private void thietLapSuKien() {
        // Avatar -> Menu popup
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hienMenuNguoiDung(v);
            }
        });
 
        // Gio hang
        imgGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManHinhChinhActivity.this, GioHangActivity.class));
            }
        });
 
        // Loc yeu thich
        imgYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangLocYeuThich = !dangLocYeuThich;
                locDanhSach();
            }
        });
 
        // Tim kiem
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) { locDanhSach(); }
            @Override public void afterTextChanged(Editable s) {}
        });
 
        // Loc thuong hieu
        View.OnClickListener locThuongHieu = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                if (btn.getId() == R.id.btnTatCa) {
                    thuongHieuDangLoc = "";
                } else {
                    thuongHieuDangLoc = btn.getText().toString();
                }
                locDanhSach();
            }
        };
        btnTatCa.setOnClickListener(locThuongHieu);
        btnNike.setOnClickListener(locThuongHieu);
        btnAdidas.setOnClickListener(locThuongHieu);
        btnVans.setOnClickListener(locThuongHieu);
        btnConverse.setOnClickListener(locThuongHieu);
        btnPuma.setOnClickListener(locThuongHieu);
        btnNewBalance.setOnClickListener(locThuongHieu);
 
        // Bam vao san pham -> Chi tiet
        lvSanPham.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SanPham sp = danhSachSanPham.get(position);
                Intent intent = new Intent(ManHinhChinhActivity.this, ChiTietSanPhamActivity.class);
                intent.putExtra("maSanPham", sp.getMaSanPham());
                startActivity(intent);
            }
        });
    }
 
    private void locDanhSach() {
        String tuKhoa = edtTimKiem.getText().toString().trim().toLowerCase();
        danhSachSanPham.clear();
 
        for (SanPham sp : danhSachGoc) {
            boolean hopTuKhoa     = tuKhoa.isEmpty() || sp.getTenSP().toLowerCase().contains(tuKhoa);
            boolean hopThuongHieu = thuongHieuDangLoc.isEmpty() ||
                    (sp.getThuongHieu() != null && sp.getThuongHieu().equals(thuongHieuDangLoc));
            boolean hopYeuThich   = !dangLocYeuThich || sp.isDaYeuThich();
 
            if (hopTuKhoa && hopThuongHieu && hopYeuThich) {
                danhSachSanPham.add(sp);
            }
        }
        adapter.notifyDataSetChanged();
    }
 
    private void hienMenuNguoiDung(View anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenu().add(0, 1, 0, "Thông tin tài khoản");
        popup.getMenu().add(0, 2, 0, "Đơn hàng của tôi");
        popup.getMenu().add(0, 3, 0, "Đăng xuất");
        popup.getMenu().add(0, 4, 0, "Thoát");
 
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        startActivity(new Intent(ManHinhChinhActivity.this, HoSoNguoiDungActivity.class));
                        return true;
                    case 2:
                        startActivity(new Intent(ManHinhChinhActivity.this, DonHangCuaToiActivity.class));
                        return true;
                    case 3:
                        AppData.dangXuat();
                        Intent i = new Intent(ManHinhChinhActivity.this, ManHinhChaoActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        return true;
                    case 4:
                        finish();
                        System.exit(0);
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }
 
    private void capNhatBadgeGioHang() {
        int soLuong = GioHang.getInstance().getTongSoLuong();
        if (soLuong > 0) {
            tvSoLuongGio.setVisibility(View.VISIBLE);
            tvSoLuongGio.setText(String.valueOf(soLuong));
        } else {
            tvSoLuongGio.setVisibility(View.GONE);
        }
    }

}
