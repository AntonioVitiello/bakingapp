package av.udacity.bakingapp.net.retrofit;

import java.util.List;

import av.udacity.bakingapp.net.jsonstubs.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Antonio Vitiello
 */
public interface RecipeService {

    @GET("baking.json")
    Call<List<Recipe>> getRecipes();

}

