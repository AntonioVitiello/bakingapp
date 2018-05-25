package av.udacity.bakingapp.ui.splash;

import android.os.Bundle;

import av.udacity.bakingapp.ui.main.MainActivity;

/**
 * Created by Antonio Vitiello
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(MainActivity.class);
        finish();
    }

}
