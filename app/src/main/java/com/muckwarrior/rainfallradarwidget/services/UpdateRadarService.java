package com.muckwarrior.rainfallradarwidget.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ContentProvider;
import android.content.Context;
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
import com.muckwarrior.rainfallradarwidget.RainfallRadarAppWidget;
import com.muckwarrior.rainfallradarwidget.api.MetClient;
import com.muckwarrior.rainfallradarwidget.api.ServiceGenerator;
import com.muckwarrior.rainfallradarwidget.models.Image;
import com.muckwarrior.rainfallradarwidget.models.Radar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRadarService extends Service implements Callback<Radar> {

    private int[] mAllWidgetIds;
    private int count = 0;
    private Context mContext;

    public UpdateRadarService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            Log.d(this, "Oh noes, intent is null");
        }
        mAllWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        mContext = this;

        Log.d(this, "About to call radar");
        MetClient client = ServiceGenerator.createService(MetClient.class);
        Call<Radar> call  = client.getRadar();
        call.enqueue(this);

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



    @Override
    public void onResponse(Call<Radar> call, Response<Radar> response) {
        Log.v(this, "onResponse");
        Radar radar = response.body();
        Log.v(this, "Response:" + radar);

        saveImages(radar);
    }

    @Override
    public void onFailure(Call<Radar> call, Throwable t) {
        Log.e(this, "onFailure", t);
    }


    private void saveImages(Radar radar) {

        count = 0;
        final int imageCount = radar.getImages().size();

        File sd = Environment.getExternalStorageDirectory().getAbsoluteFile();
        final File dest = new File(sd, "radar/");

        if (!dest.exists()) {
            dest.mkdirs();
        }

        //delete any old files
        if (dest.listFiles() != null) {
            for (File f : dest.listFiles()) {
                f.delete();
            }
        }

        for (final Image image: radar.getImages()) {
            Log.v(this, "Downloading image:" + "http://www.met.ie/weathermaps/radar2/" + image.getSrc());



            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://www.met.ie/weathermaps/radar2/" + image.getSrc())
                    .addHeader("Referer", "http://www.met.ie/latest/rainfall_radar.asp")
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(this, "onFailure" + e.getMessage(), e);

                    Intent i = new Intent(mContext, RainfallRadarAppWidget.class);
                    i.setAction(RainfallRadarAppWidget.SYNC_FAILED);
                    i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, mAllWidgetIds);
                    mContext.sendBroadcast(i);

                    stopSelf();
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                    Log.v(this, "Image onResponse");

                    String filename = image.getSrc();


                    File file = new File(dest, filename);

                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();

                        count++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.v(this, "Count:" + count + " Imagecount:" + imageCount);
                    if (count == imageCount) {
                        Log.d(this, "Updating widgets. Src:" + image.getSrc());

                        Intent i = new Intent(mContext, RainfallRadarAppWidget.class);
                        i.setAction(RainfallRadarAppWidget.SYNC_COMPLETE);
                        i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, mAllWidgetIds);
                        mContext.sendBroadcast(i);

                        stopSelf();
                    }
                }
            });
        }
    }
}
