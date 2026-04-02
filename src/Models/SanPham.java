package Models;

public class SanPham {
	private String  maSanPham;
    private String  tenSP;
    private double  gia;
    private double  giaSale;       // 0 = khong co sale
    private String  imageURL;      // ten file trong assets/images/
    private String  gioiThieu;
    private int     soLuong;
    private int     dangBan;       // 1=DangBan, 0=NgungBan
    private String  thuongHieu;
    private int     soLuotReview;
    private boolean daYeuThich;    // trang thai yeu thich cua nguoi dung hien tai
 
    public SanPham() {}
 
    public SanPham(String maSanPham, String tenSP, double gia, double giaSale,
                   String imageURL, String gioiThieu, int soLuong,
                   int dangBan, String thuongHieu, int soLuotReview) {
        this.maSanPham    = maSanPham;
        this.tenSP        = tenSP;
        this.gia          = gia;
        this.giaSale      = giaSale;
        this.imageURL     = imageURL;
        this.gioiThieu    = gioiThieu;
        this.soLuong      = soLuong;
        this.dangBan      = dangBan;
        this.thuongHieu   = thuongHieu;
        this.soLuotReview = soLuotReview;
        this.daYeuThich   = false;
    }
 
    // Lay gia hien thi (uu tien giaSale neu co)
    public double getGiaHienThi() {
        return (giaSale > 0) ? giaSale : gia;
    }
 
    // Kiem tra co dang sale khong
    public boolean dangSale() {
        return giaSale > 0 && giaSale < gia;
    }
 
    // Getters & Setters
    public String  getMaSanPham()               { return maSanPham; }
    public void    setMaSanPham(String v)       { this.maSanPham = v; }
 
    public String  getTenSP()                   { return tenSP; }
    public void    setTenSP(String v)           { this.tenSP = v; }
 
    public double  getGia()                     { return gia; }
    public void    setGia(double v)             { this.gia = v; }
 
    public double  getGiaSale()                 { return giaSale; }
    public void    setGiaSale(double v)         { this.giaSale = v; }
 
    public String  getImageURL()                { return imageURL; }
    public void    setImageURL(String v)        { this.imageURL = v; }
 
    public String  getGioiThieu()               { return gioiThieu; }
    public void    setGioiThieu(String v)       { this.gioiThieu = v; }
 
    public int     getSoLuong()                 { return soLuong; }
    public void    setSoLuong(int v)            { this.soLuong = v; }
 
    public int     getDangBan()                 { return dangBan; }
    public void    setDangBan(int v)            { this.dangBan = v; }
 
    public String  getThuongHieu()              { return thuongHieu; }
    public void    setThuongHieu(String v)      { this.thuongHieu = v; }
 
    public int     getSoLuotReview()            { return soLuotReview; }
    public void    setSoLuotReview(int v)       { this.soLuotReview = v; }
 
    public boolean isDaYeuThich()               { return daYeuThich; }
    public void    setDaYeuThich(boolean v)     { this.daYeuThich = v; }
}
