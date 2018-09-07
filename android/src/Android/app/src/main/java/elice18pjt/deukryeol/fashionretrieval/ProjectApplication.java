package elice18pjt.deukryeol.fashionretrieval;

import android.app.Application;
import android.content.SharedPreferences;

import com.tsengvn.typekit.Typekit;

public class ProjectApplication extends Application {
    private DBManager dbm;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    public void onCreate() {
        super.onCreate();
        dbm = new DBManager(getApplicationContext());
        Typekit.getInstance().addNormal(Typekit.createFromAsset(this, "BauhausStd-Demi.otf"))
                .addBold(Typekit.createFromAsset(this, "BauhausStd-Bold.otf"))
                .addCustom1(Typekit.createFromAsset(this, "klavika-light-opentype.otf"))
                .addCustom2(Typekit.createFromAsset(this, "klavika-medium-opentype.otf"));

        int first = 1;
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        int last = pref.getInt("first", 0);
        editor = pref.edit();
        if (first > last){
            editor.putInt("first", 10);
            editor.putInt("count", 0);
            editor.commit();
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public DBManager getDbm() {
        return dbm;
    }
}
