package av.udacity.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello
 */

public class RecipeProvider extends ContentProvider {
    public static final int LISTS = 100;
    public static final int LIST_WITH_ID = 110;

    private RecipeDBHelper dbHelper;
    private static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_LISTS, LISTS);
        matcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_LISTS + "/#", LIST_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new RecipeDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor result = null;
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case LISTS:
                result = db.query(
                        RecipeContract.RecipeEntry.TABLE_RECIPES_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                ContentResolver contentResolver = Objects.requireNonNull(getContext()).getContentResolver();
                result.setNotificationUri(contentResolver, uri);
                break;

            case LIST_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = RecipeContract.RecipeEntry._ID + "=?";
                String[] mSelectionArgs = new String[]{id};

                result = db.query(
                        RecipeContract.RecipeEntry.TABLE_RECIPES_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            default:
                Timber.e("Unmatched URI: %s", uri);
        }

        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri result = null;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case LISTS:
                long id = db.insert(RecipeContract.RecipeEntry.TABLE_RECIPES_NAME, null, values);
                if (id > 0) {
                    result = ContentUris.withAppendedId(RecipeContract.RecipeEntry.CONTENT_URI, id);
                    ContentResolver contentResolver = Objects.requireNonNull(getContext()).getContentResolver();
                    contentResolver.notifyChange(uri, null);
                } else {
                    Timber.e("DB Insert Error: %s", uri);
                }
                break;
            default:
                Timber.e("Unmatched uri: %s", uri);
        }

        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int result = 0;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case LIST_WITH_ID:
                String whereClause = RecipeContract.RecipeEntry._ID + "=?";
                String id = uri.getPathSegments().get(1);
                result = db.delete(RecipeContract.RecipeEntry.TABLE_RECIPES_NAME, whereClause, new String[]{id});
                break;
            default:
                Timber.e("Unmatched uri: %s", uri);
        }

        if (result > 0) {
            ContentResolver contentResolver = Objects.requireNonNull(getContext()).getContentResolver();
            contentResolver.notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

}
