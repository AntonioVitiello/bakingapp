package av.udacity.bakingapp.ui.steps;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import av.udacity.bakingapp.R;
import av.udacity.bakingapp.databinding.ActivityRecipeDetailBinding;
import av.udacity.bakingapp.ui.splash.BaseActivity;

import static av.udacity.bakingapp.ui.Constants.DATA_ARGUMENTS_KEY;
import static av.udacity.bakingapp.ui.Constants.STEPS_COUNT;
import static av.udacity.bakingapp.ui.Constants.STEP_INDEX_KEY;

public class StepDetailActivity extends BaseActivity {

    private ActivityRecipeDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set Data Binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_detail);

        initComponent();

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            Integer position = getIntent().getIntExtra(STEP_INDEX_KEY, 0);
            Integer length = getIntent().getIntExtra(STEPS_COUNT, 0);

            Bundle arguments = new Bundle();
            arguments.putString(DATA_ARGUMENTS_KEY, getIntent().getStringExtra(DATA_ARGUMENTS_KEY));
            arguments.putInt(STEP_INDEX_KEY, position);
            arguments.putInt(STEPS_COUNT, length);
            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment).commit();

        }
    }

    private void initComponent() {
        setSupportActionBar(mBinding.toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
