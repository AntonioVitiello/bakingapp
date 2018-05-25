package av.udacity.bakingapp.ui.databinding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import av.udacity.bakingapp.BR;

/**
 * Created by Antonio Vitiello
 */
public class RecipeDetailModel extends BaseObservable {
    private StringBuilder ingredients;

    @Bindable
    public StringBuilder getIngredients() {
        return ingredients;
    }

    public void setIngredients(StringBuilder ingredients) {
        this.ingredients = ingredients;
        notifyPropertyChanged(BR.ingredients);
    }

}
