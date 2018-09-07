package elice18pjt.deukryeol.fashionretrieval;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.sql.Blob;

public class ClothInfo {
    private String url;
    private String feature;
    private int price;
    private String filename;

    public ClothInfo(){

    }

    public ClothInfo(String filename, int price, String url, String feature){
        this.url = url;
        this.feature = feature;
        this.price = price;
        this.filename = filename;
    }

    public String getUrl(){
        return url;
    }

    public String getFeature(){
        return feature;
    }


    public String getFilename() { return filename; }

    public int getPrice(){
        return price;
    }

}
