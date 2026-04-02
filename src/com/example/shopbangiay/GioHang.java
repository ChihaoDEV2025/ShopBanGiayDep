package com.example.shopbangiay;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GioHang extends Activity {
	ListView lvGioHang;
    TextView tvTongTien, tvGioTrong;
    Button   btnThanhToan;
    GioHangAdapter adapter;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));
 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gio_hang);
		
		 lvGioHang   = (ListView) findViewById(R.id.lvGioHang);
	        tvTongTien  = (TextView) findViewById(R.id.tvTongTien);
	        tvGioTrong  = (TextView) findViewById(R.id.tvGioTrong);
	        btnThanhToan= (Button)   findViewById(R.id.btnThanhToan);
	 
	        hienThiGioHang();
	 
	        btnThanhToan.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                if (GioHang.getInstance().isEmpty()) {
	                    Toast.makeText(GioHangActivity.this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
	                    return;
	                }
	                startActivity(new Intent(GioHangActivity.this, ThanhToanActivity.class));
	            }
	        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gio_hang, menu);
		return true;
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        hienThiGioHang();
    }
	
	void hienThiGioHang() {
        if (GioHang.getInstance().isEmpty()) {
            tvGioTrong.setVisibility(View.VISIBLE);
            lvGioHang.setVisibility(View.GONE);
            btnThanhToan.setEnabled(false);
        } else {
            tvGioTrong.setVisibility(View.GONE);
            lvGioHang.setVisibility(View.VISIBLE);
            btnThanhToan.setEnabled(true);
        }
 
        adapter = new GioHangAdapter(this, GioHang.getInstance().getDanhSachItem(), this);
        lvGioHang.setAdapter(adapter);
 
        double tongTien = GioHang.getInstance().getTongTien();
        tvTongTien.setText("Tổng: " + formatTien.format(tongTien) + " đ");
    }
}
 
 
// =============================================
// GioHangAdapter
// =============================================
class GioHangAdapter extends BaseAdapter {
 
    android.content.Context context;
    java.util.List<GioHangItem> danhSach;
    GioHangActivity gioHangActivity;
    NumberFormat formatTien = NumberFormat.getInstance(new Locale("vi", "VN"));
 
    GioHangAdapter(android.content.Context context,
                   java.util.List<GioHangItem> danhSach,
                   GioHangActivity gioHangActivity) {
        this.context          = context;
        this.danhSach         = danhSach;
        this.gioHangActivity  = gioHangActivity;
    }
 
    @Override public int getCount()                   { return danhSach.size(); }
    @Override public Object getItem(int pos)          { return danhSach.get(pos); }
    @Override public long getItemId(int pos)          { return pos; }
 
    @Override
    public View getView(final int position, View convertView, android.view.ViewGroup parent) {
        if (convertView == null) {
            convertView = android.view.LayoutInflater.from(context)
                    .inflate(R.layout.item_gio_hang, parent, false);
        }
 
        GioHangItem item = danhSach.get(position);
        SanPham sp = item.getSanPham();
 
        ImageView imgSP     = (ImageView) convertView.findViewById(R.id.imgSP);
        TextView  tvTenSP   = (TextView)  convertView.findViewById(R.id.tvTenSP);
        TextView  tvGia     = (TextView)  convertView.findViewById(R.id.tvGia);
        TextView  tvSoLuong = (TextView)  convertView.findViewById(R.id.tvSoLuong);
        Button    btnTang   = (Button)    convertView.findViewById(R.id.btnTang);
        Button    btnGiam   = (Button)    convertView.findViewById(R.id.btnGiam);
        Button    btnXoa    = (Button)    convertView.findViewById(R.id.btnXoa);
 
        AnhHelper.hienThiAnh(context, sp.getImageURL(), imgSP);
        tvTenSP.setText(sp.getTenSP());
        tvGia.setText(formatTien.format(item.getThanhTien()) + " đ");
        tvSoLuong.setText(String.valueOf(item.getSoLuong()));
 
        btnTang.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                GioHang.getInstance().capNhatSoLuong(sp.getMaSanPham(), item.getSoLuong() + 1);
                gioHangActivity.hienThiGioHang();
            }
        });
 
        btnGiam.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                GioHang.getInstance().capNhatSoLuong(sp.getMaSanPham(), item.getSoLuong() - 1);
                gioHangActivity.hienThiGioHang();
            }
        });
 
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                GioHang.getInstance().xoaSanPham(sp.getMaSanPham());
                gioHangActivity.hienThiGioHang();
            }
        });
 
        return convertView;
    }

}
