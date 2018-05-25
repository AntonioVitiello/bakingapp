package av.udacity.bakingapp.ui.main;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import av.udacity.bakingapp.R;
import av.udacity.bakingapp.data.RecipeContract;
import av.udacity.bakingapp.databinding.ActivityMainBinding;
import av.udacity.bakingapp.net.NetworkUtils;
import av.udacity.bakingapp.net.jsonstubs.Recipe;
import av.udacity.bakingapp.net.retrofit.RecipeCallback;
import av.udacity.bakingapp.net.retrofit.RetrofitTools;
import av.udacity.bakingapp.net.volley.VolleyTools;
import av.udacity.bakingapp.ui.recipe.RecipeDetailActivity;
import av.udacity.bakingapp.ui.splash.BaseActivity;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements RecipeAdapter.RecipeClickListener, RecipeCallback.OnResponse {
    private static final String RECIPES_KEY = "recipes_data";
    private static final String CURRENT_POSITION_KEY = "current_position";

    private ActivityMainBinding mBinding;
    private RecipeAdapter mRecipeAdapter;
    private boolean mIsPortrait = true;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private Snackbar mSnackbar;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initComponent();
        setupList();

        if(getResources().getBoolean(R.bool.using_retrofit_lib)) {
            loadRecipesWithRetrofit();
        } else {
            loadRecipesWithVolley();
        }
    }

    private void initComponent() {
        mRecyclerView = mBinding.contentMain.rvMainRecipes;
        setSupportActionBar(mBinding.toolbar);
        mIsPortrait = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int firstVisiblePosition = 0;
        if (mIsPortrait) {
            LinearLayoutManager layoutManager = ((LinearLayoutManager) mRecyclerView.getLayoutManager());
            firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        } else {
            GridLayoutManager layoutManager = ((GridLayoutManager) mRecyclerView.getLayoutManager());
            firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        }

        outState.putInt(CURRENT_POSITION_KEY, firstVisiblePosition);
        outState.putParcelableArrayList(RECIPES_KEY, recipes);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        recipes = savedInstanceState.getParcelableArrayList(RECIPES_KEY);
        int position = savedInstanceState.getInt(CURRENT_POSITION_KEY);
        mRecyclerView.setAdapter(mRecipeAdapter);
        mRecipeAdapter.reset();
        mRecipeAdapter.setData(recipes);
        mRecipeAdapter.notifyDataSetChanged();

        if (mIsPortrait) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }

        mRecyclerView.scrollToPosition(position);
    }

    private void setupList() {
        mRecipeAdapter = new RecipeAdapter(this);

        if (mIsPortrait) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setAdapter(mRecipeAdapter);
    }

    private void loadRecipesWithRetrofit() {
        Timber.d("Using Retrofit lib for network invocations.");
        if (NetworkUtils.isConnected(this)) {
            RetrofitTools.doRecipesRequest(new RecipeCallback(this));
        } else {
            showSnackbar(R.string.no_connection_msg);
        }
    }

    private void loadRecipesWithVolley() {
        Timber.d("Using Volley lib for network invocations.");
        if (NetworkUtils.isConnected(this)) {
            VolleyTools.doRecipesRequest(new VolleyTools.OnRemoteServiceResponse() {
                @Override
                public void onResponse(Recipe[] recipes) {
                    for (int i = 0; i < recipes.length; i++) {
                        mRecipeAdapter.addItem(recipes[i]);
                        getContentResolver().delete(uriBuilder(recipes[i].getId()), null, null);
                        insertDb(recipes[i]);
                    }
                    mRecipeAdapter.notifyDataSetChanged();
                }
            }, this);
        } else {
            showSnackbar(R.string.no_connection_msg);
        }
    }

    private void showSnackbar(int resId) {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
        String msg = getString(resId);
        mSnackbar = Snackbar.make(mBinding.coordinatorLayout, msg, Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }

    private Uri uriBuilder(long id) {
        return ContentUris.withAppendedId(RecipeContract.RecipeEntry.CONTENT_URI, id);
    }


    private void insertDb(Recipe recipe) {

        String ingridients = new Gson().toJson(recipe.getIngredients());

        ContentValues cv = new ContentValues();
        cv.put(RecipeContract.RecipeEntry._ID, recipe.getId());
        cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, recipe.getName());
        cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE, recipe.getImage());
        cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_INGRIDIENT, ingridients);
        getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, cv);
    }

    @Override
    public void onClick(int position) {
        Recipe recipe = mRecipeAdapter.getSelectedItem(position);
        String recipeData = new Gson().toJson(recipe);

        if (recipeData != null) {
            Intent intent = new Intent(getApplicationContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.RECIPE_DATA_EXTRA, recipeData);
            startActivity(intent);
        }

    }

    @Override
    public void onRecipeList(List<Recipe> recipeList) {
        if (recipeList == null) {
            showSnackbar(R.string.network_error_retry);
            return;
        }

        for (int i = 0; i < recipeList.size(); i++) {
            mRecipeAdapter.addItem(recipeList.get(i));
            getContentResolver().delete(uriBuilder(recipeList.get(i).getId()), null, null);
            insertDb(recipeList.get(i));
        }
        mRecipeAdapter.notifyDataSetChanged();
    }

}
