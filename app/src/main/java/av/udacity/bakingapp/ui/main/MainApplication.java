package av.udacity.bakingapp.ui.main;

import android.app.Application;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import av.udacity.bakingapp.logger.TimberLogImplementation;

/**
 * Created by Antonio Vitiello
 */
public class MainApplication extends Application {

    private static Resources mResources;

    @Override
    public void onCreate() {
        super.onCreate();
        mResources = this.getApplicationContext().getResources();

        // Timber initialization
        TimberLogImplementation.init("AVDEBUG");
    }

    @NonNull
    public static String getStringResource(int resId) {
        return mResources.getString(resId);
    }

    @NonNull
    public static String getStringResource(int resId, Object... formatArgs) {
        return mResources.getString(resId, formatArgs);
    }

    public static int getIntResource(int resId) {
        return mResources.getInteger(resId);
    }

}
