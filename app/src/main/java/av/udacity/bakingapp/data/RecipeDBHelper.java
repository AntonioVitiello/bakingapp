package av.udacity.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static av.udacity.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_IMAGE;
import static av.udacity.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_INGRIDIENT;
import static av.udacity.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;
import static av.udacity.bakingapp.data.RecipeContract.RecipeEntry.TABLE_RECIPES_NAME;

/**
 * Created by Antonio Vitiello
 */

public class RecipeDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bakingapp.db";
    private static final int DATABASE_VERSION = 1;

    public RecipeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE_RECIPES = "CREATE TABLE " + TABLE_RECIPES_NAME + "(" +
                RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_RECIPE_INGRIDIENT + " TEXT, " +
                COLUMN_RECIPE_IMAGE + " TEXT);";
        db.execSQL(SQL_CREATE_TABLE_RECIPES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.TABLE_RECIPES_NAME);
        onCreate(db);
    }

}
