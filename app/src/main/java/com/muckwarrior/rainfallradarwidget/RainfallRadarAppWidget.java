package com.muckwarrior.rainfallradarwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.RemoteViews;

import android.widget.Toast;
import com.muckwarrior.rainfallradarwidget.services.UpdateRadarService;

import java.io.File;
import java.util.Arrays;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link RainfallRadarAppWidgetConfigureActivity RainfallRadarAppWidgetConfigureActivity}
 */
public class RainfallRadarAppWidget extends AppWidgetProvider {

    public static final String ACTION_AUTO_UPDATE = "rainfallradar.AUTO_UPDATE";
    public static final String SYNC_CLICKED    = "rainfallradar.automaticWidgetSyncButtonClick";
    public static final String SYNC_COMPLETE    = "rainfallradar.automaticWidgetSyncComplete";
    public static final String SYNC_FAILED    = "rainfallradar.automaticWidgetSyncFailed";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(this, "onUpdate");


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rainfall_radar_app_widget);
        views.setOnClickPendingIntent(R.id.imageButtonRefresh, getPendingSelfIntent(context, SYNC_CLICKED));


        appWidgetManager.updateAppWidget(appWidgetIds, views);

        startSyncService(context, appWidgetManager);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.d(this, "onReceive " + intent.getAction());



        if (SYNC_CLICKED.equals(intent.getAction()) || ACTION_AUTO_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rainfall_radar_app_widget);

            views.setViewVisibility(R.id.imageButtonRefresh, View.INVISIBLE);
            views.setViewVisibility(R.id.progressBar, View.VISIBLE);

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), RainfallRadarAppWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);


            for (int widgetId: appWidgetIds) {
                appWidgetManager.updateAppWidget(widgetId, views);
            }

            startSyncService(context, appWidgetManager);
        } else  if (SYNC_COMPLETE.equals(intent.getAction())) {
            // update views
            int[] widgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

            showLastImage(context, widgetIds);

        } else if (SYNC_FAILED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rainfall_radar_app_widget);

            views.setViewVisibility(R.id.imageButtonRefresh, View.VISIBLE);
            views.setViewVisibility(R.id.progressBar, View.GONE);

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), RainfallRadarAppWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

            Toast.makeText(context, "Sync failed", Toast.LENGTH_SHORT);

            for (int widgetId: appWidgetIds) {
                appWidgetManager.updateAppWidget(widgetId, views);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(this, "onEnabled");
        // Enter relevant functionality for when the first widget is created

        // start alarm
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.startAlarm(RainfallRadarAppWidgetConfigureActivity.loadIntervalPref(context));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled

        // stop alarm
        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
        appWidgetAlarm.stopAlarm();
    }


    private void showLastImage(Context context, int[] widgetIds) {

        if (widgetIds == null) {
            Log.w(this, "showLastImage widgetIDs null");
            return;
        }

        File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File dest = new File(sd, "radar/");
        File[] file = dest.listFiles();


        Arrays.sort(file);
        Log.d("Files", "Sorted Size: "+ file.length);
        for (int i=0; i < file.length; i++)
        {
            Log.v("Files", "FileName:" + file[i].getName());
        }


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rainfall_radar_app_widget);

        //Set previous file name before latest file. This is a hack to get around image caching issue
        String fileName = file[file.length -1].getName();
        String prevFileName = file[file.length -2].getName();
        views.setImageViewUri(R.id.imageViewMap, Uri.parse("content://com.muckwarrior.rainfallradarwidget.map.provider/" + prevFileName));
        views.setImageViewUri(R.id.imageViewMap, Uri.parse("content://com.muckwarrior.rainfallradarwidget.map.provider/" + fileName));

        String time = fileName.substring(0, fileName.lastIndexOf("."));
        time = time.substring(time.length() -4, time.length());
        StringBuilder stringBuilder = new StringBuilder(time);
        stringBuilder.insert(2, ':');

        views.setTextViewText(R.id.appwidget_text, stringBuilder.toString());
        views.setOnClickPendingIntent(R.id.imageButtonRefresh, getPendingSelfIntent(context, RainfallRadarAppWidget.SYNC_CLICKED));
        views.setViewVisibility(R.id.imageButtonRefresh, View.VISIBLE);
        views.setViewVisibility(R.id.progressBar, View.GONE);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        for (int widgetId: widgetIds) {
            appWidgetManager.updateAppWidget(widgetId, views);
        }

    }

    private void startSyncService(Context context, AppWidgetManager appWidgetManager) {
        Log.d(this, "startSyncService context:" + context);
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

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


}

