package com.example.shopbangiay;

import Models.NguoiDung;
import TienIchMoRong.KetNoiDB;
import TienIchMoRong.MaHoaMatKhau;
import com.example.shopbangiay.Admin.AdminTrangChu_Activity;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DangNhap_Activity extends Activity {

    private static final String TAG = "DangNhap";

    EditText edtEmail, edtMatKhau;
    Button btnDangNhap;
    TextView tvLoi, tvDiDangKy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtMatKhau = (EditText) findViewById(R.id.edtMatKhau);
        btnDangNhap = (Button) findViewById(R.id.btnDangNhap);
        tvLoi = (TextView) findViewById(R.id.tvLoi);
        tvDiDangKy = (TextView) findViewById(R.id.tvDiDangKy);

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangNhap();
            }
        });

        tvDiDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangNhap_Activity.this, DangKy_Activity.class);
                startActivity(intent);
            }
        });
    }

    private void dangNhap() {
        String email = edtEmail.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();

        if (email.isEmpty() || matKhau.isEmpty()) {
            tvLoi.setVisibility(View.VISIBLE);
            tvLoi.setText("Vui long nhap day du thong tin!");
            return;
        }

        String matKhauHash = MaHoaMatKhau.maHoaSHA256(matKhau);
        new DangNhapTask(email, matKhauHash).execute();
    }

    private class DangNhapTask extends AsyncTask<Void, Void, NguoiDung> {
        private final String email;
        private final String matKhauHash;
        private String thongBaoLoi;
        private String chiTietLoi;

        DangNhapTask(String email, String matKhauHash) {
            this.email = email;
            this.matKhauHash = matKhauHash;
        }

        @Override
        protected void onPreExecute() {
            btnDangNhap.setEnabled(false);
            tvLoi.setVisibility(View.VISIBLE);
            tvLoi.setText("Dang ket noi...");
        }

        @Override
        protected NguoiDung doInBackground(Void... params) {
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;

            try {
                con = KetNoiDB.layKetNoi();
                if (con == null) {
                    thongBaoLoi = KetNoiDB.layThongDiepLoiGanNhat();
                    chiTietLoi = KetNoiDB.layChiTietLoiGanNhat();
                    return null;
                }

                String sql = "SELECT * FROM NguoiDung WHERE Email=? AND PasswordHash=?";
                ps = con.prepareStatement(sql);
                ps.setString(1, email);
                ps.setString(2, matKhauHash);
                rs = ps.executeQuery();

                if (!rs.next()) {
                    thongBaoLoi = "Sai email hoac mat khau!";
                    chiTietLoi = "Khong tim thay tai khoan phu hop voi thong tin dang nhap.";
                    return null;
                }

                NguoiDung nd = new NguoiDung();
                nd.setMaNguoiDung(rs.getString("MaNguoiDung"));
                nd.setHovaTen(rs.getString("HovaTen"));
                nd.setEmail(rs.getString("Email"));
                nd.setPhone(rs.getString("Phone"));
                nd.setVaiTro(rs.getInt("VaiTro"));
                nd.setAnh(rs.getString("Anh"));
                return nd;
            } catch (Throwable e) {
                thongBaoLoi = "Loi trong qua trinh dang nhap.";
                chiTietLoi = e.getClass().getSimpleName() + ": " + e.getMessage();
                Log.e(TAG, "Dang nhap that bai. " + chiTietLoi, e);
                return null;
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Khong dong duoc ResultSet", e);
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Khong dong duoc PreparedStatement", e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Khong dong duoc Connection", e);
                }
            }
        }

        @Override
        protected void onPostExecute(NguoiDung nd) {
            btnDangNhap.setEnabled(true);

            if (nd != null) {
                NguoiDungHienTai_Activity.nguoiDungHienTai = nd;

                Class<?> manHinhDich = nd.laAdmin()
                        ? AdminTrangChu_Activity.class
                        : TrangChu_Activity.class;
                Intent intent = new Intent(DangNhap_Activity.this, manHinhDich);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return;
            }

            tvLoi.setVisibility(View.VISIBLE);
            tvLoi.setText(thongBaoLoi == null || thongBaoLoi.length() == 0
                    ? "Khong ket noi duoc may chu!"
                    : thongBaoLoi);

            if (chiTietLoi != null && chiTietLoi.length() > 0) {
                Log.e(TAG, "Chi tiet dang nhap: " + chiTietLoi);
            }
        }
    }
}
