package TienIchMoRong;

import android.os.StrictMode;
import java.sql.Connection;
import java.sql.DriverManager;

public class KetNoiDB {

	//DB Set up
	private static final String IP_SERVER   = "192.168.1.5";
    private static final String TEN_SERVER  = "SQLEXPRESS";
    private static final String TEN_DB      = "ShopBanGiay";
    private static final String USER        = "sa";
    private static final String PASS        = "";
    
    
    public static Connection layKetNoi() {
        Connection ketNoi = null;
        try {
           
 
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
 
            String url = "jdbc:jtds:sqlserver://"
                    + IP_SERVER + "\\" + TEN_SERVER
                    + "/" + TEN_DB
                    + ";encrypt=false";
 
            ketNoi = DriverManager.getConnection(url, USER, PASS);
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ketNoi;
    }

    public static boolean kiemTraKetNoi() {
        Connection con = layKetNoi();
        if (con != null) {
            try { con.close(); } catch (Exception e) { e.printStackTrace(); }
            return true;
        }
        return false;
    }
}
