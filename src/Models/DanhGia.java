package Models;

public class DanhGia {
	private String maDanhGia;
    private String maNguoiDung;
    private String hovaTen;       // Join tu NguoiDung
    private String maSanPham;
    private int    soSao;
    private String noiDung;
    private String ngayDanhGia;
 
    public DanhGia() {}
 
    public String getMaDanhGia()                { return maDanhGia; }
    public void   setMaDanhGia(String v)        { this.maDanhGia = v; }
    public String getMaNguoiDung()              { return maNguoiDung; }
    public void   setMaNguoiDung(String v)      { this.maNguoiDung = v; }
    public String getHovaTen()                  { return hovaTen; }
    public void   setHovaTen(String v)          { this.hovaTen = v; }
    public String getMaSanPham()                { return maSanPham; }
    public void   setMaSanPham(String v)        { this.maSanPham = v; }
    public int    getSoSao()                    { return soSao; }
    public void   setSoSao(int v)               { this.soSao = v; }
    public String getNoiDung()                  { return noiDung; }
    public void   setNoiDung(String v)          { this.noiDung = v; }
    public String getNgayDanhGia()              { return ngayDanhGia; }
    public void   setNgayDanhGia(String v)      { this.ngayDanhGia = v; }
}
