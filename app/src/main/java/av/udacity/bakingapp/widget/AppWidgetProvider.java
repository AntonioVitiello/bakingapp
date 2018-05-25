package av.udacity.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import av.udacity.bakingapp.R;
import av.udacity.bakingapp.ui.main.MainActivity;
import av.udacity.bakingapp.utils.Utils;

/**
 * Created by Antonio Vitiello
 */

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        // Widget RemoteViews
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.recipe_list_widget);

        String[] recipe = Utils.readRecipeShared(context);

        // Intent to starts the StackViewService, for the views of this collection.
        Intent intent = new Intent(context, AppWidgetIntentService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        // On click open Baking app
        Intent mainAppIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainAppIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_recipe_title, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.widget_recipe_body, pendingIntent);

        String recipeName = recipe[0];
        if (recipeName.equals("")) { // if no recipe added
            remoteViews.setTextViewText(R.id.widget_recipe_title, context.getString(R.string.widget_norecipe_title));
            remoteViews.setTextViewText(R.id.widget_recipe_body, context.getString(R.string.widget_norecipe_body));
        } else {
            String ingredients = recipe[1];
            remoteViews.setTextViewText(R.id.widget_recipe_title, context.getString(R.string.widget_recipe_title, recipeName));
            remoteViews.setTextViewText(R.id.widget_recipe_body, ingredients);
        }

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {

    }

}
