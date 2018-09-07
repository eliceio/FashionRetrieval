package elice18pjt.deukryeol.fashionretrieval;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.os.Environment.*;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageButton btnTakePicture;
    private ImageButton btnGallery;
    private Camera camera;
    private boolean mPreviewRunning;
    private ImageView cameraPreview;
    private int camId = 1;
    private ProjectApplication app;
    private static final String dirName = "eliceGallary";
    private Context context;
    private Bitmap img;
    private HttpClient client;
    private ImageRequest imageRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        setContentView(R.layout.activity_camera);
        surfaceView = (SurfaceView)findViewById(R.id.surface_camera);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mPreviewRunning = false;
        app = (ProjectApplication)getApplicationContext();
        context = this;
        btnTakePicture = (ImageButton)findViewById(R.id.btnTakePicture);
        btnGallery = (ImageButton)findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(this);
        btnTakePicture.setOnClickListener(this);

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


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            releaseCameraAndPreview();
            if (camId == 0) {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
            else {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

    }
    private void releaseCameraAndPreview() {
        //cameraPreview.setCamera(null);
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mPreviewRunning) {
            camera.stopPreview();
        }

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

        // You need to choose the most appropriate previewSize for your app
        Camera.Size previewSize = previewSizes.get(0);
        parameters.setPreviewSize(1024, 768);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        camera.startPreview();
        mPreviewRunning = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        mPreviewRunning = false;
        camera.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap img= null;
        if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            img = BitmapFactory.decodeFile(picturePath);
            int width = img.getWidth();
            int height = img.getHeight();
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            Bitmap resizedBitmap = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
            img.recycle();

            imageRequest = new ImageRequest();
            imageRequest.execute(resizedBitmap);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGallery:
                loadImage();
                break;
            case R.id.btnTakePicture:
                Log.d("OnClick", "btnTakePicture");
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        File pictureFile = null;
                        try {
                            pictureFile = createImageFile();
                            if (pictureFile == null) {
                                Log.d("pictureFile", "null");
                                return;
                            }
                            Log.d("filepath", pictureFile.getAbsolutePath().toString());
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (FileNotFoundException e) {

                        } catch (IOException e) {
                        }

                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "com.example.android.fileprovider",
                                pictureFile);
                        
                        context.getContentResolver().notifyChange(photoURI, null);
                        ContentResolver cr = context.getContentResolver();
                        try {
                            img = android.provider.MediaStore.Images.Media.getBitmap(cr, photoURI);
                            imageRequest = new ImageRequest();
                            imageRequest.execute(img);
                        } catch (Exception e) {
                            Log.e("Exception TAG", e.toString());
                            Toast.makeText(context, "Failed to load", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }

    private class ImageRequest extends AsyncTask<Bitmap, Void, Bitmap> {
        ProgressDialog asyncDialog = new ProgressDialog(
                CameraActivity.this);

        public ImageRequest(){
            client = AndroidHttpClient.newInstance("Android");
        }

        @Override
        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("전송중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            saveCameraImage(bitmap);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            byte[] data = bos.toByteArray();
            ByteArrayBody bab = new ByteArrayBody(data, "image.png");


            HttpPost post = new HttpPost("http://218.235.176.143:8000/upload");
            try{
                reqEntity.addPart("file", bab);

                post.setEntity(reqEntity);
                HttpResponse httpRes;
                httpRes = client.execute(post);
                HttpEntity resEntity = httpRes.getEntity();

                if (resEntity != null) {
                    Log.e("STATE", "resEntity != null");
                    InputStream is = resEntity.getContent();
                    bitmap = BitmapFactory.decodeStream(is);
                    return bitmap;
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

        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);

            Log.e("STATE", "onPostExecute");
            saveTempImage(bitmap);
            client.getConnectionManager().shutdown();
            asyncDialog.dismiss();
            Intent intent = new Intent(CameraActivity.this, SelectedPictureActivity.class);
            startActivity(intent);

        }
    }


    private boolean saveCameraImage(Bitmap img){
        boolean result = false;
        try{
            String filename = "cameraImage.jpeg";
            //Save image to the gallary
            String path = Environment.getExternalStorageDirectory().getPath().toString()+ "/" + dirName + "/";
            File direct = new File(path);
            if (img == null)
                Log.e("TAG", "bitmap is null");
            if (!direct.exists()) {
                File wallpaperDirectory = new File(path);
                wallpaperDirectory.mkdirs();
                Log.e("mkdir", path);
            }

            File file = new File(path + filename);
            FileOutputStream out = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.e("temp Image Save", path +  filename);
            result = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private boolean saveTempImage(Bitmap img) {
        boolean result = false;
        try {
            String filename = "tempImage.jpeg";
            //Save image to the gallary
            String path = Environment.getExternalStorageDirectory().getPath().toString() + "/" + dirName + "/";
            File direct = new File(path);
            if (img == null)
                Log.e("TAG", "bitmap is null");
            if (!direct.exists()) {
                File wallpaperDirectory = new File(path);
                wallpaperDirectory.mkdirs();
                Log.e("mkdir", path);
            }

            File file = new File(path + filename);
            FileOutputStream out = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.e("temp Image Save", path + filename);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static final int REQUEST_LOAD_IMAGE = 1;
    private void loadImage(){

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, REQUEST_LOAD_IMAGE);

    }
}
