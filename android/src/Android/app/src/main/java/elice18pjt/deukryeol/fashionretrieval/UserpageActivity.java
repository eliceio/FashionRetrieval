package elice18pjt.deukryeol.fashionretrieval;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class UserpageActivity extends BaseActivity {
    private static final String dirName = "eliceGallary";
    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    ImageView image5;
    Bitmap bitmap1;
    Bitmap bitmap2;
    Bitmap bitmap3;
    Bitmap bitmap4;
    Bitmap bitmap5;
    ImageView imageSelected;
    MyOnClickListener myOnClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userpage);
        image1 = (ImageView)findViewById(R.id.imageRecent1);
        image2 = (ImageView)findViewById(R.id.imageRecent2);
        image3 = (ImageView)findViewById(R.id.imageRecent3);
        image4 = (ImageView)findViewById(R.id.imageRecent4);
        image5 = (ImageView)findViewById(R.id.imageRecent5);
        imageSelected = (ImageView)findViewById(R.id.imgRecentSelected);


        String path = Environment.getExternalStorageDirectory().getPath().toString()+ "/" + dirName + "/";
        File direct = new File(path);
        if (!direct.exists()) {
            File wallpaperDirectory = new File(path);
            wallpaperDirectory.mkdirs();
            Log.e("mkdir", path);
        }
        bitmap1 = BitmapFactory.decodeFile(path + "image0.jpeg");
        bitmap2 = BitmapFactory.decodeFile(path + "image1.jpeg");
        bitmap3 = BitmapFactory.decodeFile(path + "image2.jpeg");
        bitmap4 = BitmapFactory.decodeFile(path + "image3.jpeg");
        bitmap5 = BitmapFactory.decodeFile(path + "image4.jpeg");

        image1.setImageBitmap(bitmap1);
        image2.setImageBitmap(bitmap2);
        image3.setImageBitmap(bitmap3);
        image4.setImageBitmap(bitmap4);
        image5.setImageBitmap(bitmap5);

        imageSelected.setImageBitmap(bitmap1);
         myOnClickListener = new MyOnClickListener();
        image1.setOnClickListener(myOnClickListener);
        image2.setOnClickListener(myOnClickListener);
        image3.setOnClickListener(myOnClickListener);
        image4.setOnClickListener(myOnClickListener);
        image5.setOnClickListener(myOnClickListener);





    }

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.imageRecent1:
                    imageSelected.setImageBitmap(bitmap1);
                    break;
                case R.id.imageRecent2:
                    imageSelected.setImageBitmap(bitmap2);
                    break;
                case R.id.imageRecent3:
                    imageSelected.setImageBitmap(bitmap3);
                    break;
                case R.id.imageRecent4:
                    imageSelected.setImageBitmap(bitmap4);
                    break;
                case R.id.imageRecent5:
                    imageSelected.setImageBitmap(bitmap5);
                    break;
            }
        }
    }

}
