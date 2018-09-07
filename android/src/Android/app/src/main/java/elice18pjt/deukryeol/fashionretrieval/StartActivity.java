package elice18pjt.deukryeol.fashionretrieval;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);

        Handler mHandler = new Handler();
        StartTask task = new StartTask();
        mHandler.postDelayed(task, 2000);
    }

    private class StartTask extends AsyncTask<Void, Void, Integer> implements Runnable {


        @Override
        protected Integer doInBackground(Void... voids) {
            return null;
        }

        @Override
        public void run() {
            this.execute();
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
