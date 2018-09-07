package elice18pjt.deukryeol.fashionretrieval;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBManager {
    private static final String dbName = "eliceFinal.db";
    private static final String tableName = "eliceFinal";
    public static final int dbVersion = 1;

    private OpenHelper opener;
    private SQLiteDatabase db;

    private Context context;
    public DBManager(Context context) {
        this.context = context;
        this.opener = new OpenHelper(context, dbName, null, dbVersion);
        db = opener.getWritableDatabase();
        //removeAllData();
        //insertTestData();
    }

    private class OpenHelper extends SQLiteOpenHelper {
        public OpenHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version){
            super(context, dbName, factory, version);
        }
        // 생성된 DB가 없을 때 한 번만 호출됨
        @Override
        public void onCreate(SQLiteDatabase db) {
            String createSql = "create table " + tableName  + " ("
                    + "filename text primary key, "
                    + "url text, "
                    + "price integer, "
                    + "feature text)";
            Log.e("Create SQL", createSql);
            db.execSQL(createSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public void insertData(ClothInfo info){
        String sql = "insert into " + tableName + " (filename, url, price, sex, feature) " +" values ('"
                + info.getFilename() + "', '"
                + info.getUrl() + "', "
                + info.getPrice() + ", '"
                + info.getFeature() + "')";
        Log.e("Insert SQL", sql);
        db.execSQL(sql);
    }

    public void removeAllData(){
        String sql = "delete from " + tableName;
        db.execSQL(sql);
    }

    public void removeData(int index) {
        String sql = "delete from " + tableName + "where id = " + index + ";";
        db.execSQL(sql);
    }

    public ArrayList<ClothInfo> selectAll() {
        String sql = "select * from " + tableName +";";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        ArrayList<ClothInfo> infos = new ArrayList<ClothInfo>();

        while(!c.isAfterLast()){

        }
        c.close();
        return infos;
    }

    public ClothInfo selectData(String filename){
        String sql = "select * from "+ tableName + " where filename = '" + filename + "'";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()){
            String fname = c.getString(0);
            String url = c.getString(1);
            int price = c.getInt(2);
            char sex = c.getString(3).charAt(0);
            String feature = c.getString(4);
            ClothInfo cloth = new ClothInfo(fname, price, url, feature);
            return cloth;
        }
        c.close();
        return null;
    }
    public int getMaxID() {
        String sql = "SELECT MAX(" + "id" + ") FROM "+ tableName + ";";
        Cursor c = db.rawQuery(sql, null);

        // result(Cursor 객체)가 비어 있으면 false 리턴
        if (c.moveToFirst()) {
            int sid = c.getInt(0);
            c.close();
            return sid;
        }
        c.close();

        return 0;

    }
}
