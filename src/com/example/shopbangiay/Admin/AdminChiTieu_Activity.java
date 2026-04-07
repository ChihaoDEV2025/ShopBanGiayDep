package com.example.shopbangiay.Admin;

import Models.ChiTieu;
import TienIchMoRong.KetNoiDB;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopbangiay.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminChiTieu_Activity extends Activity {
    ListView lvChiTieu;
    Button btnThemMoi;
    TextView tvTongChiTieu;
    List<ChiTieu> danhSach = new ArrayList<ChiTieu>();
    AdminChiTieuAdapter adapter;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chi_tieu);

        lvChiTieu = (ListView) findViewById(R.id.lvChiTieu);
        btnThemMoi = (Button) findViewById(R.id.btnThemMoi);
        tvTongChiTieu = (TextView) findViewById(R.id.tvTongChiTieu);

        adapter = new AdminChiTieuAdapter(this, danhSach);
        lvChiTieu.setAdapter(adapter);
        taiDanhSach();

        btnThemMoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hienDialogThemSua(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_chi_tieu, menu);
        return true;
    }

    private void taiDanhSach() {
        danhSach.clear();
        Connection con = null;
        ResultSet rs = null;
        try {
            con = KetNoiDB.layKetNoi();
            if (con == null) {
                hienThiLoiKetNoi("Không tải được danh sách chi tiêu.");
                adapter.notifyDataSetChanged();
                return;
            }

            rs = con.prepareStatement("SELECT * FROM ChiTieu ORDER BY NgayChi DESC").executeQuery();
            double tong = 0;
            while (rs.next()) {
                ChiTieu ct = new ChiTieu(
                        rs.getString("MaChiTieu").trim(),
                        rs.getString("LiDoChi"),
                        rs.getDouble("TongCong"),
                        rs.getString("NgayChi"),
                        rs.getString("ChiTiet")
                );
                danhSach.add(ct);
                tong += ct.getTongCong();
            }

            tvTongChiTieu.setText("Tổng chi: " + formatTien.format(tong) + " đ");
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải chi tiêu: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    void hienDialogThemSua(final ChiTieu ctEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(ctEdit == null ? "Thêm chi tiêu" : "Sửa chi tiêu");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_them_chi_tieu, null);
        builder.setView(view);

        final EditText edtLiDo = (EditText) view.findViewById(R.id.edtLiDo);
        final EditText edtTongCong = (EditText) view.findViewById(R.id.edtTongCong);
        final EditText edtChiTiet = (EditText) view.findViewById(R.id.edtChiTiet);

        if (ctEdit != null) {
            edtLiDo.setText(ctEdit.getLiDoChi());
            edtTongCong.setText(String.valueOf(ctEdit.getTongCong()));
            edtChiTiet.setText(ctEdit.getChiTiet());
        }

        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String liDo = edtLiDo.getText().toString().trim();
                String tongStr = edtTongCong.getText().toString().trim();
                String chiTiet = edtChiTiet.getText().toString().trim();

                if (liDo.length() == 0 || tongStr.length() == 0) {
                    Toast.makeText(AdminChiTieu_Activity.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Connection con = null;
                try {
                    double tongCong = Double.parseDouble(tongStr);
                    con = KetNoiDB.layKetNoi();
                    if (con == null) {
                        hienThiLoiKetNoi("Không lưu được chi tiêu.");
                        return;
                    }

                    if (ctEdit == null) {
                        ResultSet rs = con.prepareStatement("SELECT COUNT(*) FROM ChiTieu").executeQuery();
                        rs.next();
                        String maMoi = String.format("CT%04d", rs.getInt(1) + 1);

                        PreparedStatement ps = con.prepareStatement("INSERT INTO ChiTieu VALUES (?,?,?,GETDATE(),?)");
                        ps.setString(1, maMoi);
                        ps.setString(2, liDo);
                        ps.setDouble(3, tongCong);
                        ps.setString(4, chiTiet);
                        ps.executeUpdate();
                        ps.close();
                    } else {
                        PreparedStatement ps = con.prepareStatement(
                                "UPDATE ChiTieu SET LiDoChi=?,TongCong=?,ChiTiet=? WHERE MaChiTieu=?");
                        ps.setString(1, liDo);
                        ps.setDouble(2, tongCong);
                        ps.setString(3, chiTiet);
                        ps.setString(4, ctEdit.getMaChiTieu());
                        ps.executeUpdate();
                        ps.close();
                    }

                    Toast.makeText(AdminChiTieu_Activity.this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                    taiDanhSach();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AdminChiTieu_Activity.this, "Lỗi lưu chi tiêu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } finally {
                    try {
                        if (con != null) {
                            con.close();
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
        builder.setNegativeButton("Hủy", null).show();
    }

    void xoaChiTieu(final String maChiTieu) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn chắc chắn muốn xóa khoản chi này?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Connection con = null;
                        PreparedStatement ps = null;
                        try {
                            con = KetNoiDB.layKetNoi();
                            if (con == null) {
                                hienThiLoiKetNoi("Không xóa được chi tiêu.");
                                return;
                            }
                            ps = con.prepareStatement("DELETE FROM ChiTieu WHERE MaChiTieu=?");
                            ps.setString(1, maChiTieu);
                            ps.executeUpdate();
                            Toast.makeText(AdminChiTieu_Activity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
                            taiDanhSach();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AdminChiTieu_Activity.this, "Lỗi xóa chi tiêu: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
