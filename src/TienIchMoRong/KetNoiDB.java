package TienIchMoRong;


import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;

public class KetNoiDB {

    private static final String TAG = "KetNoiDB";

	// Dung 10.0.2.2 khi chay tren Android Emulator de tro ve may host.
	private static final String IP_SERVER   = "10.0.2.2";
    private static final String TEN_SERVER  = "SQLEXPRESS";
    private static final int PORT_SERVER    = 1433;
    private static final String TEN_DB      = "ShopBanGiay";
    private static final String USER        = "hao";
    private static final String PASS        = "1";
    private static final int LOGIN_TIMEOUT_GIAY = 8;
    private static String thongDiepLoiGanNhat = "";
    private static String chiTietLoiGanNhat = "";
    
    
    public static Connection layKetNoi() {
        Connection ketNoi = null;
        xoaThongTinLoi();
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Log.w(TAG, "Canh bao: dang goi SQL tren main thread. Nen chuyen man hinh nay sang AsyncTask/background thread.");
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .permitNetwork()
                        .build());
            }

            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            DriverManager.setLoginTimeout(LOGIN_TIMEOUT_GIAY);

            String url = "jdbc:jtds:sqlserver://"
                    + IP_SERVER + ":" + PORT_SERVER
                    + "/" + TEN_DB
                    + ";instance=" + TEN_SERVER
                    + ";encrypt=false"
                    + ";loginTimeout=" + LOGIN_TIMEOUT_GIAY
                    + ";socketTimeout=" + LOGIN_TIMEOUT_GIAY;

            Log.i(TAG, "Dang thu ket noi SQL Server toi " + IP_SERVER + ":" + PORT_SERVER
                    + "/" + TEN_DB + " instance=" + TEN_SERVER);
            ketNoi = DriverManager.getConnection(url, USER, PASS);
            Log.i(TAG, "Ket noi SQL Server thanh cong.");
        } catch (Throwable t) {
            luuThongTinLoi(t);
            Log.e(TAG, "Ket noi SQL that bai: " + chiTietLoiGanNhat, t);
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

    public static String layThongDiepLoiGanNhat() {
        return thongDiepLoiGanNhat;
    }

    public static String layChiTietLoiGanNhat() {
        return chiTietLoiGanNhat;
    }

    private static void xoaThongTinLoi() {
        thongDiepLoiGanNhat = "";
        chiTietLoiGanNhat = "";
    }

    private static void luuThongTinLoi(Throwable t) {
        thongDiepLoiGanNhat = phanLoaiThongBao(t);
        chiTietLoiGanNhat = taoChiTietLoi(t);
    }

    private static String phanLoaiThongBao(Throwable t) {
        if (t instanceof VerifyError) {
            return "JDBC driver khong tuong thich voi Android runtime hien tai.";
        }
        if (t instanceof ClassNotFoundException) {
            return "Thieu JDBC driver jTDS cho SQL Server.";
        }
        if (t instanceof SecurityException) {
            return "App chua duoc cap quyen INTERNET.";
        }
        if (t instanceof SocketTimeoutException) {
            return "Ket noi SQL Server bi timeout.";
        }
        if (t instanceof UnknownHostException) {
            return "Khong tim thay dia chi may chu SQL Server.";
        }
        if (t instanceof SQLException) {
            String message = t.getMessage() == null ? "" : t.getMessage().toLowerCase();
            if (message.contains("timed out") || message.contains("timeout")) {
                return "Ket noi SQL Server bi timeout.";
            }
            if (message.contains("login failed")) {
                return "Sai tai khoan hoac mat khau SQL Server.";
            }
            if (message.contains("network") || message.contains("refused")) {
                return "Khong the mo ket noi toi SQL Server. Kiem tra host, port 1433, TCP/IP va firewall.";
            }
            return "Loi SQL Server: " + t.getMessage();
        }
        return "Khong the ket noi SQL Server: " + t.getClass().getSimpleName();
    }

    private static String taoChiTietLoi(Throwable t) {
        Throwable goc = t;
        while (goc.getCause() != null) {
            goc = goc.getCause();
        }

        StringBuilder builder = new StringBuilder();
        builder.append(t.getClass().getSimpleName());
        if (t.getMessage() != null && t.getMessage().length() > 0) {
            builder.append(": ").append(t.getMessage());
        }
        if (goc != t) {
            builder.append(" | Root cause: ")
                    .append(goc.getClass().getSimpleName());
            if (goc.getMessage() != null && goc.getMessage().length() > 0) {
                builder.append(": ").append(goc.getMessage());
            }
        }
        return builder.toString();
    }
}
