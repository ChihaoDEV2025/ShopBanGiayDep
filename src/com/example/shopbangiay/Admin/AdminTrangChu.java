package com.example.shopbangiay.Admin;

import com.example.shopbangiay.R;
import com.example.shopbangiay.R.layout;
import com.example.shopbangiay.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AdminTrangChu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_trang_chu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin_trang_chu, menu);
		return true;
	}

}
