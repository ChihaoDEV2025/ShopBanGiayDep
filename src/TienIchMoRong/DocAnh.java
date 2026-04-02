package TienIchMoRong;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.InputStream;
public class DocAnh {
	public static void hienThiAnh(Context context, String imageUrl, ImageView imageView) {
        try {
            InputStream is = context.getAssets().open(imageUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(bitmap);
            is.close();
        } catch (Exception e) {
           
            try {
                InputStream is = context.getAssets().open("images/default_shoe.png");
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bitmap);
                is.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
 
    
    public static Bitmap layBitmap(Context context, String imageUrl) {
        try {
            InputStream is = context.getAssets().open(imageUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
