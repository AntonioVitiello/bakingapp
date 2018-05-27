package av.udacity.bakingapp.net.volley;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import av.udacity.bakingapp.R;
import av.udacity.bakingapp.net.jsonstubs.Recipe;
import av.udacity.bakingapp.ui.main.MainApplication;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello
 * I used Volley and Retrofit for purely demonstrative, didactic reasons.
 */
public class VolleyTools {
    // SERVICE_BASE_URL : "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String RECIPES_PATH = "baking.json";

    private static VolleyTools mInstance;
    private RequestQueue mRequestQueue;

    public interface OnRemoteServiceResponse {
        void onResponse(Recipe[] recipes);
    }


    private VolleyTools(Context context) {
        mRequestQueue = getRequestQueue(context);
    }

    public static synchronized VolleyTools getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyTools(context);
        }
        return mInstance;
    }

    public static Uri buildImageUrl(String uriString) {
        return Uri.parse(uriString)
                .buildUpon()
                .build();
    }

    public static void loadImage(Context context, String imagePath, ImageView view) {
        final Uri imageUrl = buildImageUrl(imagePath);
        Timber.d("Loading Image: %s", imageUrl);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.cake_placeholder)
                .into(view, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception exc) {
                        Timber.e(exc, "Error while loading image: %s", imageUrl);
                    }
                });
    }

    public RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            // Using ApplicationContext and not just Context
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, Context context) {
        getRequestQueue(context).add(request);
    }

    public static String buildRecipesUrl(String path) {
        String baseUrl = MainApplication.getStringResource(R.string.recipes_base_url);
        return Uri.parse(baseUrl)
                .buildUpon()
                .appendEncodedPath(path)
                .build()
                .toString();
    }

    public static void doRecipesRequest(final OnRemoteServiceResponse callback, Context context) {
        final String url = buildRecipesUrl(RECIPES_PATH);
        Timber.d("Loading: %s", url);
        GsonRequest<Recipe[]> gsonRequest = new GsonRequest<>(url, Recipe[].class, null,
                new Response.Listener<Recipe[]>() {
                    @Override
                    public void onResponse(Recipe[] recipes) {
                        Timber.d("Success loading: %s", url);
                        callback.onResponse(recipes);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Timber.e(error, "Errore while loading: %s", url);
                    }
                });

        // Start JSON Request
        getInstance(context).addToRequestQueue(gsonRequest, context);
    }

}
