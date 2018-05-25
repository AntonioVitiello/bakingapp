package av.udacity.bakingapp.ui.recipe;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import av.udacity.bakingapp.R;
import av.udacity.bakingapp.databinding.RawStepBinding;
import av.udacity.bakingapp.net.jsonstubs.Step;

/**
 * Created by Antonio Vitiello
 */

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder> {
    private final StepClickListener mClickListener;
    private List<Step> mLists;

    public interface StepClickListener {
        void onStepClick(int position);
    }


    public RecipeStepsAdapter(StepClickListener clickListener) {
        mLists = new ArrayList<>();
        mClickListener = clickListener;
    }

    public Step getSelectedItem(int position) {
        return mLists.get(position);
    }

    public void reset() {
        mLists.clear();
        notifyDataSetChanged();
    }

    public void addItem(Step item) {
        mLists.add(item);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RawStepBinding binding = DataBindingUtil.inflate(inflater, R.layout.raw_step, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Step step = mLists.get(position);
        String videoUrl = step.getVideoURL();
        String title = position == 0 ? step.getShortDescription() : position + ": " + step.getShortDescription();
        holder.txtTitle.setText(title);
        if (TextUtils.isEmpty(videoUrl)) {
            holder.imgThumb.setImageResource(R.drawable.video_off);
        }
    }


    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtTitle;
        private ImageView imgThumb;

        public ViewHolder(RawStepBinding binding) {
            super(binding.getRoot());
            txtTitle = binding.tvTitle; //R.id.tv_title
            imgThumb = binding.ivVideoPreview; //iv_video_preview
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onStepClick(getLayoutPosition());
        }
    }
}
