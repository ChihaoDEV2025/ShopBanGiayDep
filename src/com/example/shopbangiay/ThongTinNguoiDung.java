package com.example.shopbangiay;

import android.app.Application;
import Models.*;

public class ThongTinNguoiDung extends Application{
	
	 public static NguoiDung nguoiDungHienTai = null;
	 
	 @Override
	    public void onCreate() {
	        super.onCreate();
	    }
	 
	 // Dang xuat
	    public static void dangXuat() {
	        nguoiDungHienTai = null;
	        GioHang.getInstance().xoaGio();
	    }
}
