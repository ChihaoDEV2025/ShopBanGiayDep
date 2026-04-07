package com.example.shopbangiay.Admin;

import Models.SanPham;
import TienIchMoRong.KetNoiDB;
import com.example.shopbangiay.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminSanPham_Activity extends Activity {
    ListView lvSanPham;
    Button btnThemMoi;
    EditText edtTimKiem;

    List<SanPham> danhSach = new ArrayList<SanPham>();
    AdminSanPhamAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_san_pham);

        lvSanPham = (ListView) findViewById(R.id.lvSanPham);
        btnThemMoi = (Button) findViewById(R.id.btnThemMoi);
        edtTimKiem = (EditText) findViewById(R.id.edtTimKiem);

        adapter = new AdminSanPhamAdapter(this, new ArrayList<SanPham>());
        lvSanPham.setAdapter(adapter);
        taiDanhSach();

        btnThemMoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hienDialogThemSua(null);
            }
        });

        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locDanhSach(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_san_pham, menu);
        return true;
    }

    private void taiDanhSach() {
        danhSach.clear();
        Connection con = null;
        ResultSet rs = null;

        try {
            con = KetNoiDB.layKetNoi();
            if (con == null) {
                hienThiLoiKetNoi("Không tải được danh sách sản phẩm.");
                capNhatAdapter(new ArrayList<SanPham>());
                return;
            }

            rs = con.prepareStatement("SELECT * FROM SanPham ORDER BY TenSP").executeQuery();
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
                danhSach.add(sp);
            }

            locDanhSach(edtTimKiem.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải dữ liệu sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
            capNhatAdapter(new ArrayList<SanPham>());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
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

    private void locDanhSach(String tuKhoa) {
        String tuKhoaLoc = tuKhoa == null ? "" : tuKhoa.trim().toLowerCase();
        List<SanPham> ketQua = new ArrayList<SanPham>();
        for (SanPham sp : danhSach) {
            boolean hopTen = sp.getTenSP() != null && sp.getTenSP().toLowerCase().contains(tuKhoaLoc);
            boolean hopMa = sp.getMaSanPham() != null && sp.getMaSanPham().toLowerCase().contains(tuKhoaLoc);
            if (tuKhoaLoc.length() == 0 || hopTen || hopMa) {
                ketQua.add(sp);
            }
        }
        capNhatAdapter(ketQua);
    }

    private void capNhatAdapter(List<SanPham> ds) {
        adapter = new AdminSanPhamAdapter(this, ds);
        lvSanPham.setAdapter(adapter);
    }

    void hienDialogThemSua(final SanPham spEdit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(spEdit == null ? "Thêm sản phẩm" : "Sửa sản phẩm");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_them_sua_san_pham, null);
        builder.setView(view);
        builder.setNegativeButton("Hủy", null);
        builder.setPositiveButton("Lưu", null);

        final EditText edtTenSP = (EditText) view.findViewById(R.id.edtTenSP);
        final EditText edtGia = (EditText) view.findViewById(R.id.edtGia);
        final EditText edtGiaSale = (EditText) view.findViewById(R.id.edtGiaSale);
        final EditText edtImageURL = (EditText) view.findViewById(R.id.edtImageURL);
        final EditText edtGioiThieu = (EditText) view.findViewById(R.id.edtGioiThieu);
        final EditText edtSoLuong = (EditText) view.findViewById(R.id.edtSoLuong);
        final EditText edtThuongHieu = (EditText) view.findViewById(R.id.edtThuongHieu);
        final CheckBox cbDangBan = (CheckBox) view.findViewById(R.id.cbDangBan);

        if (spEdit != null) {
            edtTenSP.setText(spEdit.getTenSP());
            edtGia.setText(String.valueOf(spEdit.getGia()));
            edtGiaSale.setText(spEdit.getGiaSale() > 0 ? String.valueOf(spEdit.getGiaSale()) : "");
            edtImageURL.setText(spEdit.getImageURL());
            edtGioiThieu.setText(spEdit.getGioiThieu());
            edtSoLuong.setText(String.valueOf(spEdit.getSoLuong()));
            edtThuongHieu.setText(spEdit.getThuongHieu());
            cbDangBan.setChecked(spEdit.getDangBan() == 1);
        } else {
            cbDangBan.setChecked(true);
        }

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenSP = edtTenSP.getText().toString().trim();
                String giaStr = edtGia.getText().toString().trim();
                String giaSaleStr = edtGiaSale.getText().toString().trim();
                String imageURL = edtImageURL.getText().toString().trim();
                String gioiThieu = edtGioiThieu.getText().toString().trim();
                String soLuongStr = edtSoLuong.getText().toString().trim();
                String thuongHieu = edtThuongHieu.getText().toString().trim();
                int dangBan = cbDangBan.isChecked() ? 1 : 0;

                if (tenSP.length() == 0 || giaStr.length() == 0 || soLuongStr.length() == 0) {
                    Toast.makeText(AdminSanPham_Activity.this,
                            "Vui lòng nhập đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double gia = Double.parseDouble(giaStr);
                    double giaSale = giaSaleStr.length() == 0 ? 0 : Double.parseDouble(giaSaleStr);
                    int soLuong = Integer.parseInt(soLuongStr);

                    if (gia <= 0) {
                        Toast.makeText(AdminSanPham_Activity.this, "Giá phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (soLuong < 0) {
                        Toast.makeText(AdminSanPham_Activity.this, "Số lượng không hợp lệ!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (spEdit == null) {
                        themSanPham(tenSP, gia, giaSale, imageURL, gioiThieu, soLuong, dangBan, thuongHieu);
                    } else {
                        suaSanPham(spEdit.getMaSanPham(), tenSP, gia, giaSale, imageURL, gioiThieu, soLuong, dangBan, thuongHieu);
                    }
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(AdminSanPham_Activity.this, "Giá và số lượng phải là số hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void themSanPham(String tenSP, double gia, double giaSale, String imageURL,
                             String gioiThieu, int soLuong, int dangBan, String thuongHieu) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = KetNoiDB.layKetNoi();
            if (con == null) {
                hienThiLoiKetNoi("Không thêm được sản phẩm.");
                return;
            }

            String maMoi = taoMaSanPham(con);
            String sql = "INSERT INTO SanPham (MaSanPham,TenSP,Gia,GiaSale,ImageURL,"
                    + "GioiThieu,SoLuong,DangBan,ThuongHieu,SoLuotReview) VALUES (?,?,?,?,?,?,?,?,?,?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, maMoi);
            ps.setString(2, tenSP);
            ps.setDouble(3, gia);
            if (giaSale > 0) {
                ps.setDouble(4, giaSale);
            } else {
                ps.setNull(4, java.sql.Types.DECIMAL);
            }
            ps.setString(5, imageURL.length() == 0 ? "images/default_shoe.png" : imageURL);
            ps.setString(6, gioiThieu);
            ps.setInt(7, soLuong);
            ps.setInt(8, dangBan);
            ps.setString(9, thuongHieu);
            ps.setInt(10, 0);
            ps.executeUpdate();

            Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
            taiDanhSach();
        } catch (Exception e) {
            e.printStackTrace();
            String thongBao = e.getMessage() == null || e.getMessage().trim().length() == 0
                    ? e.getClass().getSimpleName()
                    : e.getMessage();
            Toast.makeText(this, "Lỗi thêm sản phẩm: " + thongBao, Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
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

    private void suaSanPham(String maSP, String tenSP, double gia, double giaSale, String imageURL,
                            String gioiThieu, int soLuong, int dangBan, String thuongHieu) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = KetNoiDB.layKetNoi();
            if (con == null) {
                hienThiLoiKetNoi("Không cập nhật được sản phẩm.");
                return;
            }

            String sql = "UPDATE SanPham SET TenSP=?,Gia=?,GiaSale=?,ImageURL=?,"
                    + "GioiThieu=?,SoLuong=?,DangBan=?,ThuongHieu=? WHERE MaSanPham=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, tenSP);
            ps.setDouble(2, gia);
            if (giaSale > 0) {
                ps.setDouble(3, giaSale);
            } else {
                ps.setNull(3, java.sql.Types.DECIMAL);
            }
            ps.setString(4, imageURL.length() == 0 ? "images/default_shoe.png" : imageURL);
            ps.setString(5, gioiThieu);
            ps.setInt(6, soLuong);
            ps.setInt(7, dangBan);
            ps.setString(8, thuongHieu);
            ps.setString(9, maSP);
            ps.executeUpdate();

            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            taiDanhSach();
        } catch (Exception e) {
            e.printStackTrace();
            String thongBao = e.getMessage() == null || e.getMessage().trim().length() == 0
                    ? e.getClass().getSimpleName()
                    : e.getMessage();
            Toast.makeText(this, "Lỗi cập nhật sản phẩm: " + thongBao, Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
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

    void xoaVinhVien(final String maSanPham, final String tenSanPham) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Xóa vĩnh viễn sản phẩm \"" + tenSanPham + "\"?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int w) {
                        Connection con = null;
                        PreparedStatement ps = null;
                        ResultSet rs = null;
                        try {
                            con = KetNoiDB.layKetNoi();
                            if (con == null) {
                                hienThiLoiKetNoi("Không xóa được sản phẩm.");
                                return;
                            }

                            ps = con.prepareStatement("SELECT COUNT(*) FROM ChiTietHoaDon WHERE MaSanPham=?");
                            ps.setString(1, maSanPham);
                            rs = ps.executeQuery();
                            if (rs.next() && rs.getInt(1) > 0) {
                                Toast.makeText(AdminSanPham_Activity.this,
                                        "Không thể xóa sản phẩm đã xuất hiện trong đơn hàng.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            rs.close();
                            ps.close();

                            ps = con.prepareStatement("DELETE FROM YeuThich WHERE MaSanPham=?");
                            ps.setString(1, maSanPham);
                            ps.executeUpdate();
                            ps.close();

                            ps = con.prepareStatement("DELETE FROM DanhGia WHERE MaSanPham=?");
                            ps.setString(1, maSanPham);
                            ps.executeUpdate();
                            ps.close();

                            ps = con.prepareStatement("DELETE FROM SanPham WHERE MaSanPham=?");
                            ps.setString(1, maSanPham);
                            int soDong = ps.executeUpdate();
                            if (soDong <= 0) {
                                Toast.makeText(AdminSanPham_Activity.this,
                                        "Không tìm thấy sản phẩm để xóa.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            Toast.makeText(AdminSanPham_Activity.this, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                            taiDanhSach();
                        } catch (Exception e) {
                            e.printStackTrace();
                            String thongBao = e.getMessage() == null || e.getMessage().trim().length() == 0
                                    ? e.getClass().getSimpleName()
                                    : e.getMessage();
                            Toast.makeText(AdminSanPham_Activity.this,
                                    "Lỗi xóa sản phẩm: " + thongBao, Toast.LENGTH_LONG).show();
                        } finally {
                            try {
                                if (rs != null) {
                                    rs.close();
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
                                if (con != null) {
                                    con.close();
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String taoMaSanPham(Connection con) throws Exception {
        ResultSet rs = con.prepareStatement("SELECT MAX(MaSanPham) FROM SanPham").executeQuery();
        if (rs.next() && rs.getString(1) != null) {
            int soMax = Integer.parseInt(rs.getString(1).trim().substring(2));
            return String.format("SP%03d", soMax + 1);
        }
        return "SP001";
    }

    private void hienThiLoiKetNoi(String macDinh) {
        String thongBao = KetNoiDB.layThongDiepLoiGanNhat();
        if (thongBao == null || thongBao.trim().length() == 0) {
            thongBao = macDinh;
        }
        Toast.makeText(this, thongBao, Toast.LENGTH_LONG).show();
    }
}
