package av.udacity.bakingapp.ui.main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import av.udacity.bakingapp.R;
import av.udacity.bakingapp.databinding.RawRecipeBinding;
import av.udacity.bakingapp.net.NetworkUtils;
import av.udacity.bakingapp.net.jsonstubs.Recipe;
import av.udacity.bakingapp.utils.Utils;

/**
 * Created by Antonio Vitiello
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final RecipeClickListener mListener;
    private List<Recipe> mLists;

    public RecipeAdapter(RecipeClickListener listener) {
        mLists = new ArrayList<>();
        mListener = listener;
    }

    public Recipe getSelectedItem(int position) {
        return mLists.get(position);
    }

    public void reset() {
        mLists.clear();
        notifyDataSetChanged();
    }

    public void addItem(Recipe item) {
        mLists.add(item);
        notifyDataSetChanged();
    }

    public void setData(List<Recipe> data) {
        for (int i = 0; i < data.size(); i++) {
            mLists.add(data.get(i));
        }

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RawRecipeBinding binding = DataBindingUtil.inflate(inflater, R.layout.raw_recipe, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Recipe recipe = mLists.get(position);
        String recipeName = recipe.getName();
        holder.tvTitle.setText(recipeName);

        int steps = recipe.getSteps().size();

        holder.tvSteps.setText(steps + " Steps");

        String imgUrl = recipe.getImage();

        if (!TextUtils.isEmpty(imgUrl)) {
            Picasso.get()
                    .load(imgUrl)
                    .fit()
                    .centerInside()
                    .error(R.drawable.no_video_placeholder)
                    .placeholder(R.drawable.cake_placeholder)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception exc) {
                            Context context = holder.imageView.getContext();
                            NetworkUtils.checkConnection(context);
                        }
                    });
        } else {
            int resId = Utils.getImageResId(recipeName);
            Picasso.get()
                    .load(resId)
                    .fit()
                    .centerInside()
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public interface RecipeClickListener {
        void onClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitle;
        private TextView tvSteps;
        private ImageView imageView;

        public ViewHolder(RawRecipeBinding binding) {
            super(binding.getRoot());
            tvTitle = binding.tvTitle;
            tvSteps = binding.tvSteps;
            imageView = binding.ivCakeImage;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(getLayoutPosition());
        }
    }
}
