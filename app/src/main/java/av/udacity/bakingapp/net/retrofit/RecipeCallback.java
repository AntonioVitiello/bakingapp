package av.udacity.bakingapp.net.retrofit;

import java.util.List;

import av.udacity.bakingapp.net.jsonstubs.Recipe;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello
 */
public class RecipeCallback implements Callback<List<Recipe>> {
    private final OnResponse mOnResponse;

    public interface OnResponse{
        void onRecipeList(List<Recipe> recipeList);
    }

    public RecipeCallback(OnResponse onResponse) {
        mOnResponse = onResponse;
    }

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        HttpUrl url = call.request().url();
        Timber.d("HTTP Request: %s", url.toString());

        // handle request errors depending on status code
        if (response.isSuccessful()) {
            List<Recipe> recipeList = response.body();
            mOnResponse.onRecipeList(recipeList);
        } else {
            int statusCode = response.code();
            Timber.e("Received HTTP %d on %s", statusCode, url.toString());
        }
    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable thr) {
        Timber.e(thr, "Network error: %s", thr.getMessage());
        mOnResponse.onRecipeList(null);
    }

}
