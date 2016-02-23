package com.muckwarrior.rainfallradarwidget.services;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.muckwarrior.rainfallradarwidget.Log;
import com.muckwarrior.rainfallradarwidget.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class UpdateRadarService extends Service {

    private AppWidgetManager mAppWidgetManager;
    private int[] mAllWidgetIds;

    public UpdateRadarService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mAppWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        mAllWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        RemoteViews views = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.rainfall_radar_app_widget);

        new DownloadBitmap(views, mAllWidgetIds, mAppWidgetManager).execute("gjhgjhg");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class DownloadBitmap extends AsyncTask<String, Void, Bitmap> {

        /**
         * The url from where to download the image.
         */
        private String url = "http://images.hgmsites.net/lrg/mazda-rx-vision-concept-tokyo-motor-show_100531839_l.jpg";

        private RemoteViews views;
        private int[] widgetIds;
        private AppWidgetManager widgetManager;

        public DownloadBitmap(RemoteViews views, int[] appWidgetIDs, AppWidgetManager appWidgetManager) {
            Log.d(this, "DownloadBitmap");
            this.views = views;
            this.widgetIds = appWidgetIDs;
            this.widgetManager = appWidgetManager;
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
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(this, "Setting image bitmap. PostExecute" + bitmap.getByteCount());
            views.setImageViewUri(R.id.imageViewMap, Uri.parse("content://com.muckwarrior.rainfallradarwidget.map.provider/whatever.png"));
            widgetManager.updateAppWidget(widgetIds, views);
        }
    }
}
