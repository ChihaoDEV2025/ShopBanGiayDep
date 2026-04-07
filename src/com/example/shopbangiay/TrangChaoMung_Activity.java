package com.example.shopbangiay;


import com.example.shopbangiay.R;
import com.example.shopbangiay.R.layout;
import com.example.shopbangiay.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.content.Intent;

import android.view.View;
import android.widget.Button;

public class TrangChaoMung_Activity extends Activity {
	Button btnDangNhap, btnDangKy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chao_mung);
        
        btnDangNhap = (Button) findViewById(R.id.btnDangNhap);
        btnDangKy   = (Button) findViewById(R.id.btnDangKy);
 
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrangChaoMung_Activity.this, DangNhap_Activity.class);
                startActivity(intent);
            }
        });
 
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrangChaoMung_Activity.this, DangKy_Activity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trang_chao_mung, menu);
        return true;
    }
    
}
