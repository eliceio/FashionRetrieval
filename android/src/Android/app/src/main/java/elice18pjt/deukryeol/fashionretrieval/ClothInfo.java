package elice18pjt.deukryeol.fashionretrieval;

import android.media.Image;

import java.io.Serializable;
import java.sql.Blob;

public class ClothInfo {
    private String url;
    private int feature;
    private Image image;
    private char sex;
    private int price;

    public ClothInfo(){

    }

    public ClothInfo(String featureVector, String url, int feature){
        this.url = url;
        this.feature = feature;
    }

    public String getUrl(){
        return url;
    }

    public int getFeature(){
        return feature;
    }

    public Blob getImg(){
        //return image;
        Blob nullBlob = null;
        return nullBlob;
    }

    public int getPrice(){
        return price;
    }

    public char getSex(){
        return sex;
    }
}
