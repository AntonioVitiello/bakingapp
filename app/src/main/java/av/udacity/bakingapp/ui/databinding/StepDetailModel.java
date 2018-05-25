package av.udacity.bakingapp.ui.databinding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import av.udacity.bakingapp.BR;

/**
 * Created by Antonio Vitiello
 */
public class StepDetailModel extends BaseObservable {
    private String recipeDetail;
    private boolean detailVisible;
    private boolean nextEnabled;
    private boolean prevEnabled;
    private boolean playerVisible;
    private boolean playerContainerVisible;
    private boolean stepImageVisible;

    @Bindable
    public String getRecipeDetail() {
        return recipeDetail;
    }

    public void setRecipeDetail(String recipeDetail) {
        this.recipeDetail = recipeDetail;
        notifyPropertyChanged(BR.recipeDetail);
    }

    @Bindable
    public boolean getDetailVisible() {
        return detailVisible;
    }

    public void setDetailVisible(boolean visible) {
        this.detailVisible = visible;
        notifyPropertyChanged(BR.recipeDetail);
    }

    @Bindable
    public boolean getPrevEnabled() {
        return prevEnabled;
    }

    public void setPrevEnabled(boolean prevEnabled) {
        this.prevEnabled = prevEnabled;
        notifyPropertyChanged(BR.prevEnabled);
    }

    @Bindable
    public boolean getNextEnabled() {
        return nextEnabled;
    }

    public void setNextEnabled(boolean nextEnabled) {
        this.nextEnabled = nextEnabled;
        notifyPropertyChanged(BR.nextEnabled);
    }

    @Bindable
    public boolean getPlayerVisible() {
        return playerVisible;
    }

    public void setPlayerVisible(boolean playerVisible) {
        this.playerVisible = playerVisible;
        notifyPropertyChanged(BR.playerVisible);
        setPlayerContainerVisible(playerVisible);
        setStepImageVisible(!playerVisible);
    }

    @Bindable
    public boolean getPlayerContainerVisible() {
        return playerContainerVisible;
    }

    public void setPlayerContainerVisible(boolean playerContainerVisible) {
        this.playerContainerVisible = playerContainerVisible;
        notifyPropertyChanged(BR.playerContainerVisible);
    }

    @Bindable
    public boolean getStepImageVisible() {
        return stepImageVisible;
    }

    public void setStepImageVisible(boolean stepImageVisible) {
        this.stepImageVisible = stepImageVisible;
        notifyPropertyChanged(BR.stepImageVisible);
    }
}
