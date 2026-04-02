package Models;

public class ChiTietDonHang {
	   private String maChiTietHD;
	    private String maDonHang;
	    private String maSanPham;
	    private String tenSP;       // Join tu SanPham
	    private String imageURL;    // Join tu SanPham
	    private int    soLuong;
	    private double giaBan;
	 
	    public ChiTietDonHang() {}
	 
	    public ChiTietDonHang(String maChiTietHD, String maDonHang, String maSanPham,
	                         int soLuong, double giaBan) {
	        this.maChiTietHD = maChiTietHD;
	        this.maDonHang   = maDonHang;
	        this.maSanPham   = maSanPham;
	        this.soLuong     = soLuong;
	        this.giaBan      = giaBan;
	    }
	 
	    public double getThanhTien()                { return soLuong * giaBan; }
	 
	    public String getMaChiTietHD()              { return maChiTietHD; }
	    public void   setMaChiTietHD(String v)      { this.maChiTietHD = v; }
	    public String getMaDonHang()                { return maDonHang; }
	    public void   setMaDonHang(String v)        { this.maDonHang = v; }
	    public String getMaSanPham()                { return maSanPham; }
	    public void   setMaSanPham(String v)        { this.maSanPham = v; }
	    public String getTenSP()                    { return tenSP; }
	    public void   setTenSP(String v)            { this.tenSP = v; }
	    public String getImageURL()                 { return imageURL; }
	    public void   setImageURL(String v)         { this.imageURL = v; }
	    public int    getSoLuong()                  { return soLuong; }
	    public void   setSoLuong(int v)             { this.soLuong = v; }
	    public double getGiaBan()                   { return giaBan; }
	    public void   setGiaBan(double v)           { this.giaBan = v; }
}
