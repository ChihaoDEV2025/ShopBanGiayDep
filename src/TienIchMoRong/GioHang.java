package TienIchMoRong;


import java.util.ArrayList;
import java.util.List;

import Models.GioHangItem;
import Models.SanPham;


public class GioHang {

    private static GioHang instance;
    private List<GioHangItem> danhSachItem;

    private GioHang() {
        danhSachItem = new ArrayList<GioHangItem>();
    }

    public static GioHang getInstance() {
        if (instance == null) {
            instance = new GioHang();
        }
        return instance;
    }

    // Them san pham vao gio
    public void themSanPham(SanPham sanPham, int soLuong) {
        for (GioHangItem item : danhSachItem) {
            if (item.getSanPham().getMaSanPham().equals(sanPham.getMaSanPham())) {
                item.setSoLuong(item.getSoLuong() + soLuong);
                return;
            }
        }
        danhSachItem.add(new GioHangItem(sanPham, soLuong));
    }

    // Xoa san pham khoi gio
    public void xoaSanPham(String maSanPham) {
        for (int i = 0; i < danhSachItem.size(); i++) {
            if (danhSachItem.get(i).getSanPham().getMaSanPham().equals(maSanPham)) {
                danhSachItem.remove(i);
                return;
            }
        }
    }

    // Cap nhat so luong
    public void capNhatSoLuong(String maSanPham, int soLuong) {
        for (GioHangItem item : danhSachItem) {
            if (item.getSanPham().getMaSanPham().equals(maSanPham)) {
                if (soLuong <= 0) {
                    xoaSanPham(maSanPham);
                } else {
                    item.setSoLuong(soLuong);
                }
                return;
            }
        }
    }

    // Tinh tong tien
    public double getTongTien() {
        double tong = 0;
        for (GioHangItem item : danhSachItem) {
            tong += item.getThanhTien();
        }
        return tong;
    }

    // Dem tong so luong san pham
    public int getTongSoLuong() {
        int tong = 0;
        for (GioHangItem item : danhSachItem) {
            tong += item.getSoLuong();
        }
        return tong;
    }

    // Xoa gio hang sau khi dat hang
    public void xoaGio() {
        danhSachItem.clear();
    }

    public List<GioHangItem> getDanhSachItem() {
        return danhSachItem;
    }

    public boolean isEmpty() {
        return danhSachItem.isEmpty();
    }
}
