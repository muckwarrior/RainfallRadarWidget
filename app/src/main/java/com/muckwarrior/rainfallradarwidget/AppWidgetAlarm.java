package com.muckwarrior.rainfallradarwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by aaronsmith on 13/08/2016.
 */

public class AppWidgetAlarm {

    private final int ALARM_ID = 0;
    private final int INTERVAL_MILLIS = 900000;

    private Context mContext;


    public AppWidgetAlarm(Context context)
    {
        mContext = context;
    }


    public void startAlarm(int interval)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, interval);

        Intent alarmIntent = new Intent(RainfallRadarAppWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // RTC does not wake the device up
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), interval, pendingIntent);
    }


    public void stopAlarm()
    {
        Intent alarmIntent = new Intent(RainfallRadarAppWidget.ACTION_AUTO_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
