package com.example.shopbangiay.Admin;

import Models.NguoiDung;
import TienIchMoRong.KetNoiDB;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopbangiay.NguoiDungHienTai_Activity;
import com.example.shopbangiay.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminNguoiDung_Activity extends Activity {
    ListView lvNguoiDung;
    List<NguoiDung> danhSach = new ArrayList<NguoiDung>();
    AdminNguoiDungAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_nguoi_dung);

        lvNguoiDung = (ListView) findViewById(R.id.lvNguoiDung);
        adapter = new AdminNguoiDungAdapter(this, danhSach);
        lvNguoiDung.setAdapter(adapter);
        taiDanhSach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_nguoi_dung, menu);
        return true;
    }

    private void taiDanhSach() {
        danhSach.clear();
        Connection con = null;
        ResultSet rs = null;
        try {
            con = KetNoiDB.layKetNoi();
            if (con == null) {
                hienThiLoiKetNoi("Không tải được danh sách người dùng.");
                adapter.notifyDataSetChanged();
                return;
            }

            rs = con.prepareStatement("SELECT * FROM NguoiDung ORDER BY MaNguoiDung DESC").executeQuery();
            while (rs.next()) {
                NguoiDung nd = new NguoiDung();
                nd.setMaNguoiDung(rs.getString("MaNguoiDung").trim());
                nd.setHovaTen(rs.getString("HovaTen"));
                nd.setEmail(rs.getString("Email"));
                nd.setPhone(rs.getString("Phone"));
                nd.setVaiTro(rs.getInt("VaiTro"));
                try {
                    nd.setNgayTao(rs.getString("NgayTao"));
                } catch (Exception ignored) {
                }
                try {
                    nd.setBiKhoa(rs.getInt("BiKhoa"));
                } catch (Exception ignored) {
                    nd.setBiKhoa(0);
                }
                danhSach.add(nd);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải người dùng: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    void khoaTaiKhoan(final String maNguoiDung, int biKhoaHienTai) {
        final int biKhoaMoi = biKhoaHienTai == 0 ? 1 : 0;
        String thongBao = biKhoaMoi == 1 ? "Khóa tài khoản này?" : "Mở khóa tài khoản này?";

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage(thongBao)
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Connection con = null;
                        PreparedStatement ps = null;
                        try {
                            con = KetNoiDB.layKetNoi();
                            if (con == null) {
                                hienThiLoiKetNoi("Không cập nhật được trạng thái tài khoản.");
                                return;
                            }

                            if (NguoiDungHienTai_Activity.nguoiDungHienTai != null
                                    && maNguoiDung.equals(NguoiDungHienTai_Activity.nguoiDungHienTai.getMaNguoiDung())) {
                                Toast.makeText(AdminNguoiDung_Activity.this,
                                        "Không thể khóa tài khoản của chính mình!",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            ps = con.prepareStatement("UPDATE NguoiDung SET BiKhoa=? WHERE MaNguoiDung=?");
                            ps.setInt(1, biKhoaMoi);
                            ps.setString(2, maNguoiDung);
                            ps.executeUpdate();

                            Toast.makeText(AdminNguoiDung_Activity.this,
                                    biKhoaMoi == 1 ? "Đã khóa!" : "Đã mở khóa!",
                                    Toast.LENGTH_SHORT).show();
                            taiDanhSach();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AdminNguoiDung_Activity.this,
                                    "Lỗi cập nhật người dùng: " + e.getMessage(),
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

    void phanQuyen(final String maNguoiDung, int vaiTroHienTai) {
        if (NguoiDungHienTai_Activity.nguoiDungHienTai != null
                && maNguoiDung.equals(NguoiDungHienTai_Activity.nguoiDungHienTai.getMaNguoiDung())) {
            Toast.makeText(this, "Không thể thay đổi quyền của chính mình!", Toast.LENGTH_SHORT).show();
            return;
        }

        final int vaiTroMoi = vaiTroHienTai == 0 ? 1 : 0;
        String thongBao = vaiTroMoi == 0 ? "Nâng lên Admin?" : "Hạ xuống Khách hàng?";

        new AlertDialog.Builder(this)
                .setTitle("Phân quyền")
                .setMessage(thongBao)
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Connection con = null;
                        PreparedStatement ps = null;
                        try {
                            con = KetNoiDB.layKetNoi();
                            if (con == null) {
                                hienThiLoiKetNoi("Không cập nhật được vai trò.");
                                return;
                            }

                            ps = con.prepareStatement("UPDATE NguoiDung SET VaiTro=? WHERE MaNguoiDung=?");
                            ps.setInt(1, vaiTroMoi);
                            ps.setString(2, maNguoiDung);
                            ps.executeUpdate();

                            Toast.makeText(AdminNguoiDung_Activity.this,
                                    "Phân quyền thành công!",
                                    Toast.LENGTH_SHORT).show();
                            taiDanhSach();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AdminNguoiDung_Activity.this,
                                    "Lỗi phân quyền: " + e.getMessage(),
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

    void xoaTaiKhoan(final String maNguoiDung, final String hoVaTen) {
        if (NguoiDungHienTai_Activity.nguoiDungHienTai != null
                && maNguoiDung.equals(NguoiDungHienTai_Activity.nguoiDungHienTai.getMaNguoiDung())) {
            Toast.makeText(this, "Không thể xóa tài khoản của chính mình!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xóa tài khoản")
                .setMessage("Xóa vĩnh viễn tài khoản \"" + hoVaTen + "\"?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Connection con = null;
                        PreparedStatement ps = null;
                        ResultSet rs = null;
                        try {
                            con = KetNoiDB.layKetNoi();
                            if (con == null) {
                                hienThiLoiKetNoi("Không xóa được tài khoản.");
                                return;
                            }

                            ps = con.prepareStatement("SELECT COUNT(*) FROM DonHang WHERE MaNguoiDung=?");
                            ps.setString(1, maNguoiDung);
                            rs = ps.executeQuery();
                            if (rs.next() && rs.getInt(1) > 0) {
                                Toast.makeText(AdminNguoiDung_Activity.this,
                                        "Không thể xóa tài khoản đã có đơn hàng.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            rs.close();
                            ps.close();

                            ps = con.prepareStatement("DELETE FROM YeuThich WHERE MaNguoiDung=?");
                            ps.setString(1, maNguoiDung);
                            ps.executeUpdate();
                            ps.close();

                            ps = con.prepareStatement("DELETE FROM DanhGia WHERE MaNguoiDung=?");
                            ps.setString(1, maNguoiDung);
                            ps.executeUpdate();
                            ps.close();

                            ps = con.prepareStatement("DELETE FROM NguoiDung WHERE MaNguoiDung=?");
                            ps.setString(1, maNguoiDung);
                            int soDong = ps.executeUpdate();
                            if (soDong <= 0) {
                                Toast.makeText(AdminNguoiDung_Activity.this,
                                        "Không tìm thấy tài khoản để xóa.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            Toast.makeText(AdminNguoiDung_Activity.this, "Đã xóa tài khoản!", Toast.LENGTH_SHORT).show();
                            taiDanhSach();
                        } catch (Exception e) {
                            e.printStackTrace();
                            String thongBao = e.getMessage() == null || e.getMessage().trim().length() == 0
                                    ? e.getClass().getSimpleName()
                                    : e.getMessage();
                            Toast.makeText(AdminNguoiDung_Activity.this,
                                    "Lỗi xóa tài khoản: " + thongBao,
                                    Toast.LENGTH_LONG).show();
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

    private void hienThiLoiKetNoi(String macDinh) {
        String thongBao = KetNoiDB.layThongDiepLoiGanNhat();
        if (thongBao == null || thongBao.trim().length() == 0) {
            thongBao = macDinh;
        }
        Toast.makeText(this, thongBao, Toast.LENGTH_LONG).show();
    }
}

class AdminNguoiDungAdapter extends BaseAdapter {
    android.content.Context context;
    List<NguoiDung> danhSach;

    AdminNguoiDungAdapter(android.content.Context ctx, List<NguoiDung> ds) {
        this.context = ctx;
        this.danhSach = ds;
    }

    @Override
    public int getCount() {
        return danhSach.size();
    }

    @Override
    public Object getItem(int pos) {
        return danhSach.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = android.view.LayoutInflater.from(context).inflate(R.layout.item_nguoi_dung, parent, false);
        }

        final NguoiDung nd = danhSach.get(position);

        TextView tvHovaTen = (TextView) convertView.findViewById(R.id.tvHovaTen);
        TextView tvEmail = (TextView) convertView.findViewById(R.id.tvEmail);
        TextView tvVaiTro = (TextView) convertView.findViewById(R.id.tvVaiTro);
        TextView tvTrangThai = (TextView) convertView.findViewById(R.id.tvTrangThai);
        Button btnKhoa = (Button) convertView.findViewById(R.id.btnKhoa);
        Button btnPhanQuyen = (Button) convertView.findViewById(R.id.btnPhanQuyen);
        Button btnXoaTaiKhoan = (Button) convertView.findViewById(R.id.btnXoaTaiKhoan);

        tvHovaTen.setText(nd.getHovaTen());
        tvEmail.setText(nd.getEmail());
        tvVaiTro.setText(nd.laAdmin() ? "Admin" : "Khách hàng");
        tvTrangThai.setText(nd.getBiKhoa() == 0 ? "Hoạt động" : "Bị khóa");
        tvTrangThai.setTextColor(nd.getBiKhoa() == 0 ? 0xFF2E7D32 : 0xFFC62828);
        btnKhoa.setText(nd.getBiKhoa() == 0 ? "Khóa" : "Mở khóa");

        btnKhoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AdminNguoiDung_Activity) context).khoaTaiKhoan(nd.getMaNguoiDung(), nd.getBiKhoa());
            }
        });

        btnPhanQuyen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AdminNguoiDung_Activity) context).phanQuyen(nd.getMaNguoiDung(), nd.getVaiTro());
            }
        });

        btnXoaTaiKhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AdminNguoiDung_Activity) context).xoaTaiKhoan(nd.getMaNguoiDung(), nd.getHovaTen());
            }
        });

        return convertView;
    }
}
