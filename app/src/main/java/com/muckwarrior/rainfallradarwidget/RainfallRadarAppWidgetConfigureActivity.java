package com.muckwarrior.rainfallradarwidget;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * The configuration screen for the {@link RainfallRadarAppWidget RainfallRadarAppWidget} AppWidget.
 */
public class RainfallRadarAppWidgetConfigureActivity extends Activity {

    private static final String[] INTERVAL_NAMES = {"Manual", "15 min", "30 min", "Hourly"};
    private static final int[] INTERVAL_VALUES = {0, 900000, 1800000, 3600000};

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String PREFS_NAME = "com.muckwarrior.rainfallradarwidget.RainfallRadarAppWidget";
    private static final String PREF_PREFIX_INTERVAL_KEY = "interval_appwidget_";
    private static final String PREF_PREFIX_WIFI_KEY = "wifi_appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner mSpinnerUpdateFrequency;
    private CheckBox mChkWifiOnly;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = RainfallRadarAppWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            saveIntervalPref(context, mAppWidgetId, INTERVAL_VALUES[mSpinnerUpdateFrequency.getSelectedItemPosition()]);
            saveWifiPref(context, mChkWifiOnly.isChecked());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            //RainfallRadarAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public RainfallRadarAppWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveIntervalPref(Context context, int appWidgetId, int interval) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_INTERVAL_KEY, interval);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static int loadIntervalPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_INTERVAL_KEY, 0);
    }

    static void deleteIntervalPref(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_INTERVAL_KEY);
        prefs.apply();
    }

    static void saveWifiPref(Context context, boolean wifiOnly) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(PREF_PREFIX_WIFI_KEY, wifiOnly);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static boolean loadWifiPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREF_PREFIX_WIFI_KEY, true);
    }

    static void deleteWifiPref(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_WIFI_KEY);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.rainfall_radar_app_widget_configure);

        List<String> freqList = new ArrayList<>();
        freqList.add(INTERVAL_NAMES[0]);
        freqList.add(INTERVAL_NAMES[1]);
        freqList.add(INTERVAL_NAMES[2]);
        freqList.add(INTERVAL_NAMES[3]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, freqList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerUpdateFrequency = (Spinner) findViewById(R.id.spinnerUpdateFrequency);
        mSpinnerUpdateFrequency.setAdapter(adapter);
        mSpinnerUpdateFrequency.setSelection(0);

        mChkWifiOnly = (CheckBox) findViewById(R.id.checkBoxWifiOnly);


        findViewById(R.id.buttonAdd).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }


        verifyStoragePermissions(this);

    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}

