package elice18pjt.deukryeol.fashionretrieval;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraActivity extends Activity implements View.OnClickListener{
    private ImageButton btnList;
    private ImageButton btnCamera;
    private ImageButton btnAlbum;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int SELECT_PICTURE = 1;
    private ImageView mImageView;
    private ConnectionTask connectionTask;
    private HttpClient client = AndroidHttpClient.newInstance("Android");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        btnList = (ImageButton)findViewById(R.id.btnList);
        btnCamera = (ImageButton)findViewById(R.id.btnCamera);
        btnAlbum = (ImageButton) findViewById(R.id.btnAlbum);
        mImageView = (ImageView)findViewById(R.id.imageView);
        btnCamera.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
        client = AndroidHttpClient.newInstance("Android");

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnList:
                break;
            case R.id.btnCamera:
                dispatchTakePictureIntent();
                break;
            case R.id.btnAlbum:
                break;
        }
    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    Uri photoURI;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.e("TAG", "ConnectToServer");
                //connectToServer(photoFile);
            } catch (Exception ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.getContentResolver().notifyChange(photoURI, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, photoURI);
            //File file = new File(photoURI.toString());
            connectionTask = new ConnectionTask();
            connectionTask.execute(bitmap);
            Log.e("TAG", "Success");
            mImageView.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            Log.e("Exception TAG", e.toString());
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
        }
    }

    private class ConnectionTask extends AsyncTask<Bitmap, Void, File>{

        public ConnectionTask(){
            client = AndroidHttpClient.newInstance("Android");
        }
        @Override
        protected File doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            byte[] data = bos.toByteArray();
            ByteArrayBody bab = new ByteArrayBody(data, "image.png");


            HttpPost post = new HttpPost("http://218.235.176.227:8000/upload");
            try{
                reqEntity.addPart("file", bab);

                post.setEntity(reqEntity);
                HttpResponse httpRes;
                httpRes = client.execute(post);
                HttpEntity resEntity = httpRes.getEntity();

                if (resEntity != null) {
                    Log.e("result", EntityUtils.toString(resEntity));
                }
                else {
                    Log.e("result", "is null");
                }
                if (resEntity != null) {
                    resEntity.consumeContent();
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    private void loadImage(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }
}
