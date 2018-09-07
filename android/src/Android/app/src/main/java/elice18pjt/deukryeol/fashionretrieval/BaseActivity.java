package elice18pjt.deukryeol.fashionretrieval;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.tsengvn.typekit.TypekitContextWrapper;

public class BaseActivity extends Activity {

    @Override
    protected void attachBaseContext(Context newBase) {
        // 커스텀 폰트 로드
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
