package com.example.shopbangiay.Admin;

import Models.DonHang;
import TienIchMoRong.KetNoiDB;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shopbangiay.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminDonHang_Activity extends Activity {
    ListView lvDonHang;
    Spinner spinnerLoc;
    List<DonHang> danhSach = new ArrayList<DonHang>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_don_hang);

        lvDonHang = (ListView) findViewById(R.id.lvDonHang);
        spinnerLoc = (Spinner) findViewById(R.id.spinnerLoc);

        String[] trangThaiOptions = {"Tất cả", "Chờ xác nhận", "Đang giao", "Hoàn thành", "Đã hủy"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, trangThaiOptions);
        spinnerLoc.setAdapter(spinAdapter);
        spinnerLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taiDanhSach(position - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lvDonHang.setAdapter(new AdminDonHangAdapter(this, danhSach));
        taiDanhSach(-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_don_hang, menu);
        return true;
    }

    private void taiDanhSach(int trangThai) {
        danhSach.clear();
        Connection con = null;
        ResultSet rs = null;
        try {
            con = KetNoiDB.layKetNoi();
            if (con == null) {
                hienThiLoiKetNoi("Không tải được danh sách đơn hàng.");
                lvDonHang.setAdapter(new AdminDonHangAdapter(this, danhSach));
                return;
            }

            String sql = "SELECT dh.*, nd.HovaTen FROM DonHang dh "
                    + "JOIN NguoiDung nd ON dh.MaNguoiDung = nd.MaNguoiDung ";
            if (trangThai >= 0) {
                sql += "WHERE dh.TrangThaiDonHang = " + trangThai + " ";
            }
            sql += "ORDER BY dh.NgayDatHang DESC";

            rs = con.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                DonHang dh = new DonHang();
                dh.setMaDonHang(rs.getString("MaDonHang").trim());
                dh.setMaNguoiDung(rs.getString("MaNguoiDung").trim());
                dh.setHovaTen(rs.getString("HovaTen"));
                dh.setNgayDatHang(rs.getString("NgayDatHang"));
                dh.setTongHoaDon(rs.getDouble("TongHoaDon"));
                dh.setTrangThaiDonHang(rs.getInt("TrangThaiDonHang"));
                dh.setDiaChiGiaoHang(rs.getString("DiaChiGiaoHang"));
                danhSach.add(dh);
            }

            lvDonHang.setAdapter(new AdminDonHangAdapter(this, danhSach));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải đơn hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    void hienChiTietDonHang(final DonHang dh) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = KetNoiDB.layKetNoi();
            if (con == null) {
                hienThiLoiKetNoi("Không tải được chi tiết đơn hàng.");
                return;
            }

            String sql = "SELECT cthd.SoLuong, cthd.GiaBan, sp.TenSP "
                    + "FROM ChiTietHoaDon cthd JOIN SanPham sp ON cthd.MaSanPham=sp.MaSanPham "
                    + "WHERE cthd.MaDonHang=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, dh.getMaDonHang());
            rs = ps.executeQuery();

            NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));
            StringBuilder sb = new StringBuilder();
            sb.append("Mã đơn: ").append(dh.getMaDonHang()).append("\n");
            sb.append("Khách: ").append(dh.getHovaTen()).append("\n");
            sb.append("Ngày: ").append(dh.getNgayDatHang()).append("\n");
            sb.append("Địa chỉ: ").append(dh.getDiaChiGiaoHang()).append("\n");
            sb.append("Trạng thái: ").append(dh.getTenTrangThai()).append("\n\n");
            sb.append("---- Sản phẩm ----\n");

            while (rs.next()) {
                sb.append(rs.getString("TenSP")).append("\n");
                sb.append("  x").append(rs.getInt("SoLuong"))
                        .append(" x ").append(fmt.format(rs.getDouble("GiaBan")))
                        .append(" đ\n");
            }
            sb.append("\nTổng: ").append(fmt.format(dh.getTongHoaDon())).append(" đ");

            String[] tuyChon = {"Xem chi tiết", "Xác nhận đơn", "Đang giao", "Hoàn thành", "Hủy đơn"};
            new AlertDialog.Builder(this)
                    .setTitle("Chi tiết đơn hàng")
                    .setMessage(sb.toString())
                    .setItems(tuyChon, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 1:
                                    capNhatTrangThai(dh, 0);
                                    break;
                                case 2:
                                    capNhatTrangThai(dh, 1);
                                    break;
                                case 3:
                                    capNhatTrangThai(dh, 2);
                                    break;
                                case 4:
                                    capNhatTrangThai(dh, 3);
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải chi tiết đơn hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    private void capNhatTrangThai(DonHang dh, int trangThaiMoi) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = KetNoiDB.layKetNoi();
            if (con == null) {
                hienThiLoiKetNoi("Không cập nhật được đơn hàng.");
                return;
            }

            ps = con.prepareStatement("UPDATE DonHang SET TrangThaiDonHang=? WHERE MaDonHang=?");
            ps.setInt(1, trangThaiMoi);
            ps.setString(2, dh.getMaDonHang());
            ps.executeUpdate();

            if (trangThaiMoi == 2 && dh.getTrangThaiDonHang() != 2) {
                PreparedStatement psCT = con.prepareStatement(
                        "SELECT MaSanPham, SoLuong FROM ChiTietHoaDon WHERE MaDonHang=?");
                psCT.setString(1, dh.getMaDonHang());
                ResultSet rsCT = psCT.executeQuery();
                while (rsCT.next()) {
                    PreparedStatement psKho = con.prepareStatement(
                            "UPDATE SanPham SET SoLuong = SoLuong - ? WHERE MaSanPham=?");
                    psKho.setInt(1, rsCT.getInt("SoLuong"));
                    psKho.setString(2, rsCT.getString("MaSanPham").trim());
                    psKho.executeUpdate();
                    psKho.close();
                }
                rsCT.close();
                psCT.close();
            }

            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            taiDanhSach(spinnerLoc.getSelectedItemPosition() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi cập nhật đơn hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    void xoaDonHang(final DonHang dh) {
        if (dh.getTrangThaiDonHang() != 3) {
            Toast.makeText(this,
                    "Chỉ có thể xóa vĩnh viễn đơn hàng đã hủy.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xóa đơn hàng")
                .setMessage("Xóa vĩnh viễn đơn hàng \"" + dh.getMaDonHang() + "\"?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Connection con = null;
                        PreparedStatement ps = null;
                        try {
                            con = KetNoiDB.layKetNoi();
                            if (con == null) {
                                hienThiLoiKetNoi("Không xóa được đơn hàng.");
                                return;
                            }

                            ps = con.prepareStatement("DELETE FROM ChiTietHoaDon WHERE MaDonHang=?");
                            ps.setString(1, dh.getMaDonHang());
                            ps.executeUpdate();
                            ps.close();

                            ps = con.prepareStatement("DELETE FROM DonHang WHERE MaDonHang=?");
                            ps.setString(1, dh.getMaDonHang());
                            int soDong = ps.executeUpdate();
                            if (soDong <= 0) {
                                Toast.makeText(AdminDonHang_Activity.this,
                                        "Không tìm thấy đơn hàng để xóa.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            Toast.makeText(AdminDonHang_Activity.this,
                                    "Đã xóa đơn hàng!",
                                    Toast.LENGTH_SHORT).show();
                            taiDanhSach(spinnerLoc.getSelectedItemPosition() - 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            String thongBao = e.getMessage() == null || e.getMessage().trim().length() == 0
                                    ? e.getClass().getSimpleName()
                                    : e.getMessage();
                            Toast.makeText(AdminDonHang_Activity.this,
                                    "Lỗi xóa đơn hàng: " + thongBao,
                                    Toast.LENGTH_LONG).show();
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
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void hienThiLoiKetNoi(String macDinh) {
        String thongBao = KetNoiDB.layThongDiepLoiGanNhat();
        if (thongBao == null || thongBao.trim().length() == 0) {
            thongBao = macDinh;
        }
        Toast.makeText(this, thongBao, Toast.LENGTH_LONG).show();
    }
}
