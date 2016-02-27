package com.muckwarrior.rainfallradarwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.RemoteViews;

import com.muckwarrior.rainfallradarwidget.services.UpdateRadarService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link RainfallRadarAppWidgetConfigureActivity RainfallRadarAppWidgetConfigureActivity}
 */
public class RainfallRadarAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d("RainfallRadarAppWidget", "updateAppWidget");

        CharSequence widgetText = RainfallRadarAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rainfall_radar_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setImageViewUri(R.id.imageViewMap, Uri.parse("content://com.muckwarrior.rainfallradarwidget.map.provider/WEB_radar4_201602272045.png"));


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(this, "onUpdate");

        // There may be multiple widgets active, so update all of them
        /*for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rainfall_radar_app_widget);
            new DownloadBitmap(views, appWidgetId, appWidgetManager).execute("MyTestString");
        }*/

        ComponentName thisWidget = new ComponentName(context,
                RainfallRadarAppWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(),
                UpdateRadarService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Update the widgets via the service
        context.startService(intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            RainfallRadarAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

