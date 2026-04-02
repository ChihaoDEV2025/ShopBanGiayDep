package Models;

public class ChiTieu {
	 private String maChiTieu;
	    private String liDoChi;
	    private double tongCong;
	    private String ngayChi;
	    private String chiTiet;
	 
	    public ChiTieu() {}
	 
	    public ChiTieu(String maChiTieu, String liDoChi, double tongCong,
	                   String ngayChi, String chiTiet) {
	        this.maChiTieu = maChiTieu;
	        this.liDoChi   = liDoChi;
	        this.tongCong  = tongCong;
	        this.ngayChi   = ngayChi;
	        this.chiTiet   = chiTiet;
	    }
	 
	    public String getMaChiTieu()            { return maChiTieu; }
	    public void   setMaChiTieu(String v)    { this.maChiTieu = v; }
	    public String getLiDoChi()              { return liDoChi; }
	    public void   setLiDoChi(String v)      { this.liDoChi = v; }
	    public double getTongCong()             { return tongCong; }
	    public void   setTongCong(double v)     { this.tongCong = v; }
	    public String getNgayChi()              { return ngayChi; }
	    public void   setNgayChi(String v)      { this.ngayChi = v; }
	    public String getChiTiet()              { return chiTiet; }
	    public void   setChiTiet(String v)      { this.chiTiet = v; }
}
