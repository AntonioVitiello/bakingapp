package av.udacity.bakingapp.ui.recipe;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import av.udacity.bakingapp.R;
import av.udacity.bakingapp.databinding.ActivityRecipeListBinding;
import av.udacity.bakingapp.message.StepChangeEvent;
import av.udacity.bakingapp.net.jsonstubs.Ingredient;
import av.udacity.bakingapp.net.jsonstubs.Recipe;
import av.udacity.bakingapp.net.jsonstubs.Step;
import av.udacity.bakingapp.ui.databinding.RecipeDetailModel;
import av.udacity.bakingapp.ui.splash.BaseActivity;
import av.udacity.bakingapp.ui.steps.StepDetailActivity;
import av.udacity.bakingapp.ui.steps.StepDetailFragment;
import av.udacity.bakingapp.utils.Utils;
import av.udacity.bakingapp.widget.AppWidgetProvider;
import timber.log.Timber;

import static av.udacity.bakingapp.ui.Constants.DATA_ARGUMENTS_KEY;
import static av.udacity.bakingapp.ui.Constants.STEPS_COUNT;
import static av.udacity.bakingapp.ui.Constants.STEP_INDEX_KEY;


/**
 * This Activity contains a list of Steps with a different presentations for tablet devices where using two vertical panes.
 */
public class RecipeDetailActivity extends BaseActivity implements RecipeStepsAdapter.StepClickListener {
    public static final String RECIPE_DATA_EXTRA = "recipe_data";
    private boolean mTwoPaneFlag; // true if the activity is in two-pane mode eg running on a tablet
    private static RecipeStepsAdapter mRecipeStepsAdapter;
    private Recipe mRecipe;
    private StringBuilder ingredientList;
    private RecyclerView mStepsRecyclerView;
    private ActionBar mActionBar;
    private Snackbar mSnackbar;
    private ActivityRecipeListBinding mBinding;
    private RecipeDetailModel mRecipeDetailModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set Data Binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_list);
        mRecipeDetailModel = new RecipeDetailModel();
        mBinding.setDetailModel(mRecipeDetailModel);

        initComponents();

        String recipeData = getIntent().getStringExtra(RECIPE_DATA_EXTRA);
        mRecipe = new Gson().fromJson(recipeData, Recipe.class);

        if (mRecipe != null) {
            mActionBar.setTitle(mRecipe.getName());
            setupIngredient(mRecipe.getIngredients());
            loadSteps();
            setBackground(mRecipe.getName());
        }

        // if RecipeDetailView is present we are on a large screen (res/values-w900dp): start two-pane mode!
        if (mBinding.recipeListIncl.recipeDetailContainer != null) {
            mTwoPaneFlag = true;
            StepDetailFragment fragment = new StepDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

    }

    private void initComponents() {
        // ActionBar and show the Up button
        setSupportActionBar(mBinding.toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(getTitle());
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // Steps RecyclerView
        mRecipeStepsAdapter = new RecipeStepsAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mStepsRecyclerView = mBinding.recipeListIncl.recipeList; //R.id.recipe_list
        mStepsRecyclerView.setLayoutManager(linearLayoutManager);
        mStepsRecyclerView.setHasFixedSize(true);
        mStepsRecyclerView.setNestedScrollingEnabled(false);
        mStepsRecyclerView.setAdapter(mRecipeStepsAdapter);
    }

    private void setBackground(String recipeName) {
        int resId = Utils.getBackgroundResId(recipeName);
        Drawable drawableBackgr = getResources().getDrawable(resId);
        mBinding.getRoot().setBackground(drawableBackgr);
    }

    private void setupIngredient(List<Ingredient> ingredients) {
        ingredientList = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            String quantity = ingredients.get(i).getQuantity();
            String measure = ingredients.get(i).getMeasure();
            String name = ingredients.get(i).getIngredient();
            ingredientList.append(" - ")
                    .append(quantity)
                    .append(" ")
                    .append(measure)
                    .append(" ")
                    .append(name).append("\n");
        }
        mRecipeDetailModel.setIngredients(ingredientList);
    }


    private void loadSteps() {
        List<Step> stepList = mRecipe.getSteps();
        for (int i = 0; i < stepList.size(); i++) {
            mRecipeStepsAdapter.addItem(stepList.get(i));
        }
        mRecipeStepsAdapter.notifyDataSetChanged();
    }


    @Override
    public void onStepClick(int position) {
        String stepData = new Gson().toJson(mRecipeStepsAdapter.getSelectedItem(position));
        if (mTwoPaneFlag) {
            StepChangeEvent stepChangeEvent = new StepChangeEvent.Builder()
                    .stepData(stepData)
                    .currentStep(position)
                    .stepsCount(mRecipeStepsAdapter.getItemCount())
                    .build();
            EventBus.getDefault().post(stepChangeEvent);
        } else {
            Intent intent = new Intent(getApplicationContext(), StepDetailActivity.class);
            intent.putExtra(DATA_ARGUMENTS_KEY, stepData);
            intent.putExtra(STEP_INDEX_KEY, position);
            intent.putExtra(STEPS_COUNT, mRecipeStepsAdapter.getItemCount());
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isConsumed = false;
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home: {
                isConsumed = true;
                onBackPressed();
                break;
            }
            case R.id.menu_widget: {
                isConsumed = true;
                int[] ids = AppWidgetManager.getInstance(getApplicationContext())
                        .getAppWidgetIds(new ComponentName(getApplicationContext(), AppWidgetProvider.class));

                Intent intent = new Intent(this, AppWidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);

                Utils.shareRecipe(mRecipe.getName(), String.valueOf(ingredientList), this);

                showSnackbar(getString(R.string.added_to_widget));
                break;
            }
            default:
                Timber.e("Unimplemented menu item: %d", id);
        }
        return isConsumed ? isConsumed : super.onOptionsItemSelected(item);
    }

    private void showSnackbar(String message) {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
        mSnackbar = Snackbar.make(mBinding.coordinatorLayout, message, Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }

    public static String getStep(Integer position) {
        Step step = mRecipeStepsAdapter.getSelectedItem(position);
        return new Gson().toJson(step);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String stepData = new Gson().toJson(mRecipe.getSteps().get(0));
        StepChangeEvent stepChangeEvent = new StepChangeEvent.Builder()
                .stepData(stepData)
                .currentStep(0)
                .stepsCount(mRecipe.getSteps().size())
                .build();
        EventBus.getDefault().post(stepChangeEvent);
    }
}
