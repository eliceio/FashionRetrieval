package elice18pjt.deukryeol.fashionretrieval;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ListActivity extends Activity {
    ListView listview;
    private static final String dirName = "eliceGallary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_list);
        listview = (ListView)findViewById(R.id.listview);

        ArrayList<ListViewItem> items = new ArrayList<ListViewItem>();
        String path = Environment.getExternalStorageDirectory().getPath().toString()+ "/" + dirName + "/";
        File direct = new File(path);
        if (!direct.exists()) {
            File wallpaperDirectory = new File(path);
            wallpaperDirectory.mkdirs();
            Log.e("mkdir", path);
        }

        File[] files = direct.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
            items.add(new ListViewItem(files[i], files[i].getName()));

        }
        ListViewAdapter adapter = new ListViewAdapter(this, R.layout.list_item, items);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), ListItemActivity.class);
                intent.putExtra("filename", (String)adapterView.getAdapter().getItem(i));
                startActivity(intent);
            }
        });
    }





    public class ListViewItem {
        private Bitmap image;
        private String name;
        public Bitmap getImage(){return image;}
        public String getName(){return name;}
        public ListViewItem(File image,String name){
            this.image = BitmapFactory.decodeFile(image.getPath().toString());
            this.name=name;
        }
    }

    public class ListViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<ListViewItem> data;
        private int layout;
        public ListViewAdapter(Context context, int layout, ArrayList<ListViewItem> data){
            this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.data=data;
            this.layout=layout;
        }
        @Override
        public int getCount(){return data.size();}
        @Override
        public String getItem(int position){return data.get(position).getName();}
        @Override
        public long getItemId(int position){return position;}

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null){
                view=inflater.inflate(layout,viewGroup,false);
            }
            ListViewItem listviewitem=data.get(i);
            ImageView icon=(ImageView)view.findViewById(R.id.imageview);
            icon.setImageBitmap(listviewitem.getImage());
            return view;
        }
    }
}
