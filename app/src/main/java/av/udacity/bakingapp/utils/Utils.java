package av.udacity.bakingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import av.udacity.bakingapp.R;

/**
 * Created by Antonio Vitiello
 */
public class Utils {
    public static final String SHARED_PREF_NAME = "bakingapp.shared.pref";
    public static final String RECIPE_PREF_KEY = "recipe";
    public static final String INGREDIENTS_PREF_KEY = "ingredients";

    public static void shareRecipe(String name, String ingredients, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(RECIPE_PREF_KEY, name)
                .putString(INGREDIENTS_PREF_KEY, ingredients)
                .apply();
    }

    public static String[] readRecipeShared(Context context) {
        String[] recipe = new String[2];
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        recipe[0] = preferences.getString(RECIPE_PREF_KEY, "");
        recipe[1] = preferences.getString(INGREDIENTS_PREF_KEY, "");
        return recipe;
    }

    public static int getImageResId(String recipeName){
        int resId = R.drawable.bakedbakery;
        recipeName = recipeName.toLowerCase();
        if (recipeName.contains("nutella")) {
            resId = R.drawable.nutellapie;
        } else if (recipeName.contains("brownies")) {
            resId = R.drawable.brownies;
        } else if (recipeName.contains("yellow")) {
            resId = R.drawable.yellowcake;
        } else if (recipeName.contains("cheesecake")) {
            resId = R.drawable.cheesecake;
        }
        return resId;
    }

    public static int getBackgroundResId(String recipeName){
        int resId = R.drawable.bakedbakery;
        recipeName = recipeName.toLowerCase();
        if (recipeName.contains("nutella")) {
            resId = R.drawable.nutellapie_gradient_background;
        } else if (recipeName.contains("brownies")) {
            resId = R.drawable.brownies_gradient_background;
        } else if (recipeName.contains("yellow")) {
            resId = R.drawable.yellowcake_gradient_background;
        } else if (recipeName.contains("cheesecake")) {
            resId = R.drawable.cheesecake_gradient_background;
        }
        return resId;
    }

}
