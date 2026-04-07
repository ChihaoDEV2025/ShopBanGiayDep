package Models;

import com.example.shopbangiay.GioHang_Activity;

public class GioHangItem {
	 private SanPham sanPham;
	    private int     soLuong;
	    private static GioHangItem instance;
	   
	 
	    public GioHangItem(SanPham sanPham, int soLuong) {
	        this.sanPham = sanPham;
	        this.soLuong = soLuong;
	    }
	 
	    public GioHangItem() {
			
		}

		public double getThanhTien() {
	        return sanPham.getGiaHienThi() * soLuong;
	    }
	 
	    public SanPham getSanPham()             { return sanPham; }
	    public void    setSanPham(SanPham v)    { this.sanPham = v; }
	    public int     getSoLuong()             { return soLuong; }
	    public void    setSoLuong(int v)        { this.soLuong = v; }
	    public static GioHangItem getInstance() {
	        if (instance == null) {
	            instance = new GioHangItem();
	        }
	        return instance;
	    }
}
