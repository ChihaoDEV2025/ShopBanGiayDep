package Models;

public class GioHangItem {
	 private SanPham sanPham;
	    private int     soLuong;
	 
	    public GioHangItem(SanPham sanPham, int soLuong) {
	        this.sanPham = sanPham;
	        this.soLuong = soLuong;
	    }
	 
	    public double getThanhTien() {
	        return sanPham.getGiaHienThi() * soLuong;
	    }
	 
	    public SanPham getSanPham()             { return sanPham; }
	    public void    setSanPham(SanPham v)    { this.sanPham = v; }
	    public int     getSoLuong()             { return soLuong; }
	    public void    setSoLuong(int v)        { this.soLuong = v; }
}
