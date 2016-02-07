package com.muckwarrior.rainfallradarwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.RemoteViews;

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



        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(this, "onUpdate");

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rainfall_radar_app_widget);
            new DownloadBitmap(views, appWidgetId, appWidgetManager).execute("MyTestString");
        }
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

    public class DownloadBitmap extends AsyncTask<String, Void, Bitmap> {

        /**
         * The url from where to download the image.
         */
        private String url = "http://dreamatico.com/data_images/car/car-6.jpg";

        private RemoteViews views;
        private int WidgetID;
        private AppWidgetManager WidgetManager;

        public DownloadBitmap(RemoteViews views, int appWidgetID, AppWidgetManager appWidgetManager) {
            Log.d(this, "DownloadBitmap");
            this.views = views;
            this.WidgetID = appWidgetID;
            this.WidgetManager = appWidgetManager;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                InputStream in = new java.net.URL(url).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                Log.v("ImageDownload", "download succeeded");
                Log.v("ImageDownload", "Param 0 is: " + params[0]);

                return bitmap;
                //NOTE:  it is not thread-safe to set the ImageView from inside this method.  It must be done in onPostExecute()
            } catch (Exception e) {
                Log.e("ImageDownload", "Download failed: " + e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            String filename = "map.png";
            File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
            File dest = new File(sd, filename);

            try {
                FileOutputStream out = new FileOutputStream(dest);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(this, "Setting image bitmap. PostExecute" + bitmap.getByteCount());
            views.setImageViewUri(R.id.imageViewMap, Uri.parse("content://com.muckwarrior.rainfallradarwidget.map.provider/whatever"));
            WidgetManager.updateAppWidget(WidgetID, views);
        }
    }
}

