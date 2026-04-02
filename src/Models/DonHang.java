package Models;

public class DonHang {
	private String maDonHang;
    private String maNguoiDung;
    private String hovaTen;          // Join tu NguoiDung
    private String ngayDatHang;
    private double tongHoaDon;
    private int    trangThaiDonHang; // 0=ChoXacNhan,1=DangGiao,2=HoanThanh,3=Huy
    private String diaChiGiaoHang;
    private String phuongThucThanhToan;
    private String ghiChu;
 
    public DonHang() {}
 
    public DonHang(String maDonHang, String maNguoiDung, String ngayDatHang,
                   double tongHoaDon, int trangThaiDonHang, String diaChiGiaoHang) {
        this.maDonHang          = maDonHang;
        this.maNguoiDung        = maNguoiDung;
        this.ngayDatHang        = ngayDatHang;
        this.tongHoaDon         = tongHoaDon;
        this.trangThaiDonHang   = trangThaiDonHang;
        this.diaChiGiaoHang     = diaChiGiaoHang;
        this.phuongThucThanhToan = "COD";
    }
 
    // Lay ten trang thai dang hien thi
    public String getTenTrangThai() {
        switch (trangThaiDonHang) {
            case 0: return "Chờ xác nhận";
            case 1: return "Đang giao";
            case 2: return "Hoàn thành";
            case 3: return "Đã hủy";
            default: return "Không xác định";
        }
    }
 
    // Getters & Setters
    public String getMaDonHang()                    { return maDonHang; }
    public void   setMaDonHang(String v)            { this.maDonHang = v; }
    public String getMaNguoiDung()                  { return maNguoiDung; }
    public void   setMaNguoiDung(String v)          { this.maNguoiDung = v; }
    public String getHovaTen()                      { return hovaTen; }
    public void   setHovaTen(String v)              { this.hovaTen = v; }
    public String getNgayDatHang()                  { return ngayDatHang; }
    public void   setNgayDatHang(String v)          { this.ngayDatHang = v; }
    public double getTongHoaDon()                   { return tongHoaDon; }
    public void   setTongHoaDon(double v)           { this.tongHoaDon = v; }
    public int    getTrangThaiDonHang()             { return trangThaiDonHang; }
    public void   setTrangThaiDonHang(int v)        { this.trangThaiDonHang = v; }
    public String getDiaChiGiaoHang()               { return diaChiGiaoHang; }
    public void   setDiaChiGiaoHang(String v)       { this.diaChiGiaoHang = v; }
    public String getPhuongThucThanhToan()          { return phuongThucThanhToan; }
    public void   setPhuongThucThanhToan(String v)  { this.phuongThucThanhToan = v; }
    public String getGhiChu()                       { return ghiChu; }
    public void   setGhiChu(String v)               { this.ghiChu = v; }
}
