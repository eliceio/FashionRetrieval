package elice18pjt.deukryeol.fashionretrieval;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SelectedPictureActivity extends BaseActivity implements View.OnClickListener{
    ImageView imageMain;
    Bitmap image;
    ImageView cameraImage;
    File f;
    private static final String dirName = "eliceGallary";
    private Button btnCancel;
    private Button btnSave;
    String folder = Environment.getExternalStorageDirectory().getPath().toString()+ "/" + dirName + "/";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_selected_picture);
        imageMain = (ImageView)findViewById(R.id.imgMain);
        cameraImage = (ImageView)findViewById(R.id.imgCamera);
        try {
            String file1 = folder + "tempImage.jpeg";
            String file2 = folder + "cameraImage.jpeg";
            image = BitmapFactory.decodeFile(file1);
            imageMain.setImageBitmap(image);
            cameraImage.setImageBitmap(BitmapFactory.decodeFile(file2));
            cameraImage.setRotation(90);


        } catch (Exception e) {
            e.printStackTrace();
        }
        btnSave = (Button)findViewById(R.id.btnSave);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        count = pref.getInt("count", 0);
        count = (count+1) % 5;
        Log.e("count", Integer.toString(count));
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnSave:
                String filename = folder + "image" + Integer.toString(count) + ".jpeg";
                Log.e("filename", filename);
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(filename);
                    image.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                editor.putInt("count", count);
                editor.commit();
                finish();
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    }
}
