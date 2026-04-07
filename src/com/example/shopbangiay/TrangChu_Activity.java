package com.example.shopbangiay;

import Models.SanPham;
import TienIchMoRong.GioHang;
import TienIchMoRong.KetNoiDB;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TrangChu_Activity extends Activity {
    private static final int MENU_TAI_KHOAN = 1;
    private static final int MENU_DON_HANG = 2;
    private static final int MENU_DANG_XUAT = 3;
    private static final int MENU_THOAT = 4;
    private static final int MENU_ADMIN = 5;

    TextView tvTenShop, tvTenNguoiDung;
    ImageView imgAvatar;

    EditText edtTimKiem;
    ImageView imgGioHang;
    TextView tvSoLuongGio;
    ImageView imgYeuThich;

    LinearLayout layoutThuongHieu;
    Button btnTatCa, btnNike, btnAdidas, btnVans, btnConverse, btnPuma, btnNewBalance;

    ListView lvSanPham;

    List<SanPham> danhSachSanPham = new ArrayList<SanPham>();
    List<SanPham> danhSachGoc = new ArrayList<SanPham>();
    SanPhamAdapter adapter;

    String thuongHieuDangLoc = "";
    boolean dangLocYeuThich = false;
    boolean dangTaiDuLieu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);

        khoiTaoView();
        adapter = new SanPhamAdapter(this, danhSachSanPham);
        adapter.setOnYeuThichChangedListener(new SanPhamAdapter.OnYeuThichChangedListener() {
            @Override
            public void onYeuThichChanged(SanPham sanPham, boolean daYeuThich) {
                capNhatIconLocYeuThich();
                if (dangLocYeuThich && !daYeuThich) {
                    locDanhSach();
                }
            }
        });
        lvSanPham.setAdapter(adapter);

        hienThiThongTinNguoiDung();
        thietLapSuKien();
        taiDanhSachSanPham();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capNhatBadgeGioHang();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trang_chu, menu);
        return true;
    }

    private void khoiTaoView() {
        tvTenShop = (TextView) findViewById(R.id.tvTenShop);
        tvTenNguoiDung = (TextView) findViewById(R.id.tvTenNguoiDung);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        edtTimKiem = (EditText) findViewById(R.id.edtTimKiem);
        imgGioHang = (ImageView) findViewById(R.id.imgGioHang);
        tvSoLuongGio = (TextView) findViewById(R.id.tvSoLuongGio);
        imgYeuThich = (ImageView) findViewById(R.id.imgYeuThich);
        layoutThuongHieu = (LinearLayout) findViewById(R.id.layoutThuongHieu);
        btnTatCa = (Button) findViewById(R.id.btnTatCa);
        btnNike = (Button) findViewById(R.id.btnNike);
        btnAdidas = (Button) findViewById(R.id.btnAdidas);
        btnVans = (Button) findViewById(R.id.btnVans);
        btnConverse = (Button) findViewById(R.id.btnConverse);
        btnPuma = (Button) findViewById(R.id.btnPuma);
        btnNewBalance = (Button) findViewById(R.id.btnNewBalance);
        lvSanPham = (ListView) findViewById(R.id.lvSanPham);
    }

    private void hienThiThongTinNguoiDung() {
        if (NguoiDungHienTai_Activity.nguoiDungHienTai != null) {
            tvTenNguoiDung.setText(NguoiDungHienTai_Activity.nguoiDungHienTai.getHovaTen());
        }
        tvTenShop.setText("HHK - Shoe Shop");
        capNhatIconLocYeuThich();
    }

    private void taiDanhSachSanPham() {
        new TaiSanPhamTask().execute();
    }

    private void thietLapSuKien() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hienMenuNguoiDung(v);
            }
        });

        imgGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TrangChu_Activity.this, GioHang_Activity.class));
            }
        });

        imgYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangLocYeuThich = !dangLocYeuThich;
                capNhatIconLocYeuThich();
                locDanhSach();
            }
        });

        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                locDanhSach();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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

        lvSanPham.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SanPham sp = danhSachSanPham.get(position);
                Intent intent = new Intent(TrangChu_Activity.this, ChiTietSanPham_Activity.class);
                intent.putExtra("maSanPham", sp.getMaSanPham());
                startActivity(intent);
            }
        });
    }

    private void locDanhSach() {
        if (adapter == null) {
            return;
        }

        if (dangTaiDuLieu) {
            return;
        }

        String tuKhoa = edtTimKiem.getText().toString().trim().toLowerCase();
        danhSachSanPham.clear();

        for (SanPham sp : danhSachGoc) {
            boolean hopTuKhoa = tuKhoa.isEmpty() || sp.getTenSP().toLowerCase().contains(tuKhoa);
            boolean hopThuongHieu = thuongHieuDangLoc.isEmpty()
                    || (sp.getThuongHieu() != null && sp.getThuongHieu().equals(thuongHieuDangLoc));
            boolean hopYeuThich = !dangLocYeuThich || sp.isDaYeuThich();

            if (hopTuKhoa && hopThuongHieu && hopYeuThich) {
                danhSachSanPham.add(sp);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void hienMenuNguoiDung(View anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenu().add(0, MENU_TAI_KHOAN, 0, "Thong tin tai khoan");
        popup.getMenu().add(0, MENU_DON_HANG, 0, "Don hang cua toi");
        if (NguoiDungHienTai_Activity.nguoiDungHienTai != null
                && NguoiDungHienTai_Activity.nguoiDungHienTai.laAdmin()) {
            popup.getMenu().add(0, MENU_ADMIN, 0, "Trang quan tri");
        }
        popup.getMenu().add(0, MENU_DANG_XUAT, 0, "Dang xuat");
        popup.getMenu().add(0, MENU_THOAT, 0, "Thoat");

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_TAI_KHOAN:
                        startActivity(new Intent(TrangChu_Activity.this, HoSoNguoiDung_Activity.class));
                        return true;
                    case MENU_DON_HANG:
                        startActivity(new Intent(TrangChu_Activity.this, DonHangCuaToi.class));
                        return true;
                    case MENU_ADMIN:
                        startActivity(new Intent(TrangChu_Activity.this,
                                com.example.shopbangiay.Admin.AdminTrangChu_Activity.class));
                        return true;
                    case MENU_DANG_XUAT:
                        NguoiDungHienTai_Activity.dangXuat();
                        Intent i = new Intent(TrangChu_Activity.this, TrangChaoMung_Activity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        return true;
                    case MENU_THOAT:
                        finish();
                        System.exit(0);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void capNhatIconLocYeuThich() {
        imgYeuThich.setImageResource(dangLocYeuThich
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
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

    private class TaiSanPhamTask extends AsyncTask<Void, Void, List<SanPham>> {
        private String thongBaoLoi;

        @Override
        protected void onPreExecute() {
            dangTaiDuLieu = true;
            danhSachSanPham.clear();
            danhSachGoc.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(TrangChu_Activity.this, "Dang tai danh sach san pham...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<SanPham> doInBackground(Void... params) {
            Connection con = null;
            PreparedStatement ps = null;
            PreparedStatement psYT = null;
            ResultSet rs = null;
            ResultSet rsYT = null;

            try {
                con = KetNoiDB.layKetNoi();
                if (con == null) {
                    thongBaoLoi = KetNoiDB.layThongDiepLoiGanNhat();
                    return null;
                }

                List<SanPham> ketQua = new ArrayList<SanPham>();
                String sql = "SELECT sp.MaSanPham, sp.TenSP, sp.Gia, sp.GiaSale, sp.ImageURL, "
                        + "sp.GioiThieu, sp.SoLuong, sp.DangBan, sp.ThuongHieu, sp.SoLuotReview "
                        + "FROM SanPham sp WHERE sp.DangBan = 1 ORDER BY sp.TenSP";
                ps = con.prepareStatement(sql);
                rs = ps.executeQuery();

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
                            rs.getInt("SoLuotReview"));
                    ketQua.add(sp);
                }

                if (NguoiDungHienTai_Activity.nguoiDungHienTai != null) {
                    String sqlYT = "SELECT MaSanPham FROM YeuThich WHERE MaNguoiDung = ?";
                    psYT = con.prepareStatement(sqlYT);
                    psYT.setString(1, NguoiDungHienTai_Activity.nguoiDungHienTai.getMaNguoiDung());
                    rsYT = psYT.executeQuery();

                    List<String> dsYeuThich = new ArrayList<String>();
                    while (rsYT.next()) {
                        dsYeuThich.add(rsYT.getString("MaSanPham").trim());
                    }

                    for (SanPham sp : ketQua) {
                        sp.setDaYeuThich(dsYeuThich.contains(sp.getMaSanPham()));
                    }
                }

                return ketQua;
            } catch (Exception e) {
                thongBaoLoi = "Loi tai du lieu: " + e.getMessage();
                return null;
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                }
                try {
                    if (rsYT != null) {
                        rsYT.close();
                    }
                } catch (Exception e) {
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Exception e) {
                }
                try {
                    if (psYT != null) {
                        psYT.close();
                    }
                } catch (Exception e) {
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception e) {
                }
            }
        }

        @Override
        protected void onPostExecute(List<SanPham> ketQua) {
            dangTaiDuLieu = false;

            if (ketQua == null) {
                if (thongBaoLoi == null || thongBaoLoi.length() == 0) {
                    thongBaoLoi = "Khong tai duoc du lieu san pham.";
                }
                Toast.makeText(TrangChu_Activity.this, thongBaoLoi, Toast.LENGTH_SHORT).show();
                return;
            }

            danhSachGoc.clear();
            danhSachGoc.addAll(ketQua);
            locDanhSach();
        }
    }
}
