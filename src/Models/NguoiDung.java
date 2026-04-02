package Models;

public class NguoiDung {
	 private String maNguoiDung;
	    private String hovaTen;
	    private String email;
	    private String phone;
	    private String passwordHash;
	    private int vaiTro;       // 0=Admin, 1=KhachHang
	    private String anh;
	    private String ngayTao;
	    private int biKhoa;       // 0=HoatDong, 1=BiKhoa
	 
	    public NguoiDung() {}
	 
	    public NguoiDung(String maNguoiDung, String hovaTen, String email,
	                     String phone, String passwordHash, int vaiTro, String anh) {
	        this.maNguoiDung  = maNguoiDung;
	        this.hovaTen      = hovaTen;
	        this.email        = email;
	        this.phone        = phone;
	        this.passwordHash = passwordHash;
	        this.vaiTro       = vaiTro;
	        this.anh          = anh;
	    }
	 
	    // Getters & Setters
	    public String getMaNguoiDung()              { return maNguoiDung; }
	    public void   setMaNguoiDung(String v)      { this.maNguoiDung = v; }
	 
	    public String getHovaTen()                  { return hovaTen; }
	    public void   setHovaTen(String v)          { this.hovaTen = v; }
	 
	    public String getEmail()                    { return email; }
	    public void   setEmail(String v)            { this.email = v; }
	 
	    public String getPhone()                    { return phone; }
	    public void   setPhone(String v)            { this.phone = v; }
	 
	    public String getPasswordHash()             { return passwordHash; }
	    public void   setPasswordHash(String v)     { this.passwordHash = v; }
	 
	    public int    getVaiTro()                   { return vaiTro; }
	    public void   setVaiTro(int v)              { this.vaiTro = v; }
	 
	    public String getAnh()                      { return anh; }
	    public void   setAnh(String v)              { this.anh = v; }
	 
	    public String getNgayTao()                  { return ngayTao; }
	    public void   setNgayTao(String v)          { this.ngayTao = v; }
	 
	    public int    getBiKhoa()                   { return biKhoa; }
	    public void   setBiKhoa(int v)              { this.biKhoa = v; }
	 
	    public boolean laAdmin()                    { return vaiTro == 0; }
	    public boolean laKhachHang()                { return vaiTro == 1; }
}
