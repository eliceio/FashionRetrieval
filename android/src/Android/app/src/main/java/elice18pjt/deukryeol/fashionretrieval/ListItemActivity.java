package elice18pjt.deukryeol.fashionretrieval;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ListItemActivity extends Activity {
    private ImageView imgView;
    private TextView txtSex;
    private TextView txtPrice;
    private TextView txtFeature;
    private TextView txtURL;
    private static final String dirName = "eliceGallary";
    private static final String path = Environment.getExternalStorageDirectory().getPath().toString()+ "/" + dirName + "/";
    private ClothInfo cloth;
    ProjectApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_info);
        app = (ProjectApplication)getApplicationContext();

        Intent intent = getIntent();
        String pictureName = intent.getStringExtra("filename");
        cloth = app.getDbm().selectData(pictureName);
        Log.e("price", String.valueOf(cloth.getPrice()));
        Log.e("Feature", String.valueOf(cloth.getFeature()));
        Log.e("URL", String.valueOf(cloth.getUrl()));


        imgView = (ImageView)findViewById(R.id.itemImageView);
        Bitmap image = BitmapFactory.decodeFile(path+pictureName);
        imgView.setImageBitmap(image);

        txtSex = (TextView) findViewById(R.id.textViewSex);
        txtPrice = (TextView) findViewById(R.id.textViewPrice);
        txtFeature = (TextView) findViewById(R.id.textViewFeature);
        txtURL = (TextView) findViewById(R.id.textViewURL);

        txtURL.setText("주소: " + cloth.getUrl());
        txtPrice.setText("가격: " + String.valueOf(cloth.getPrice()));
        txtFeature.setText("특징: " + String.valueOf(cloth.getFeature()));

        txtURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.textViewURL:

                        Uri uri = Uri.parse("https://" + cloth.getUrl());
                        Intent i = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(i);
                        break;

                }
            }
        });

    }
}
