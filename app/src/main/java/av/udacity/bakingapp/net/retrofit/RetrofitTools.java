package av.udacity.bakingapp.net.retrofit;

import android.widget.Toast;

import java.util.List;

import av.udacity.bakingapp.R;
import av.udacity.bakingapp.net.jsonstubs.Recipe;
import av.udacity.bakingapp.ui.main.MainApplication;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Antonio Vitiello
 */
public class RetrofitTools {
    private volatile static RetrofitTools sInstance;
    private String mRecipesBaseUrl;
    private Toast mToast;
    private RecipeService mRecipeService;

    private RetrofitTools() {
    }

    public static final RetrofitTools getInstance() {
        if (sInstance == null) {
            sInstance = new RetrofitTools();
            sInstance.mRecipesBaseUrl = MainApplication.getStringResource(R.string.recipes_base_url);
            sInstance.mRecipeService = sInstance.getClient().create(RecipeService.class);
        }
        return sInstance;
    }

    private Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl(mRecipesBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static void doRecipesRequest(Callback<List<Recipe>> recipesCallback) {
        getInstance().mRecipeService.getRecipes().enqueue(recipesCallback);
    }

}
