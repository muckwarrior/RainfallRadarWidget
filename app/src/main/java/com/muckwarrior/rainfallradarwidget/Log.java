package com.muckwarrior.rainfallradarwidget;


/**
 * @author Aaron
 * 
 *         This class should be used in place of the standard android.util.Log
 *         class. It lets us quickly and easily change the level of logs that
 *         are output to the LogCat, thus letting us hide any sensitive data
 *         and/or avoid spamming the LogCat with too much data. During
 *         development LOG_LEVEL should typically be set to VERBOSE. This means
 *         all logged messages will be output to the LogCat. For Release
 *         LOG_LEVEL should be set to INFO. This effectively hides all VERBOSE
 *         and DEBUG level log messages. Note that this means that any
 *         potentially sensitive data (URLs, passwords etc.) should only ever be
 *         logged at VERBOSE or DEBUG level. Only data potentially useful to a
 *         user should be logged above these levels; warnings, errors etc.
 * 
 *         Usage This class can be called in the exact same way as the standard
 *         android log class. Additionally, the 'this' keyword can be used in
 *         place of the TAG parameter in most cases, thus avoiding the need to
 *         specify a static TAG variable.
 */

public final class Log {

    private static final int VERBOSE = 0;
    private static final int DEBUG = 1;
    private static final int INFO = 2;
    private static final int WARN = 3;
    private static final int ERROR = 4;
    private static final int ASSERT = 5;
    private static final int OFF = 6;

    // Log level is set in the build.gradle for each buildType
    private static final int LOG_LEVEL = BuildConfig.LOG_LEVEL;

    private Log() {
    }

    public static void v(String tag, String message) {
        if (VERBOSE >= LOG_LEVEL) {
            android.util.Log.v(tag, message);
        }
    }

    public static void v(Object callingObject, String message) {
        if (VERBOSE >= LOG_LEVEL) {
            android.util.Log.v(callingObject.getClass().getName(), message);
        }
    }

    public static void d(String tag, String message) {
        if (DEBUG >= LOG_LEVEL) {
            android.util.Log.d(tag, message);
        }
    }

    public static void d(Object callingObject, String message) {
        if (DEBUG >= LOG_LEVEL) {
            android.util.Log.d(callingObject.getClass().getName(), message);
        }
    }

    public static void i(String tag, String message) {
        if (INFO >= LOG_LEVEL) {
            android.util.Log.i(tag, message);
        }
    }

    public static void i(Object callingObject, String message) {
        if (INFO >= LOG_LEVEL) {
            android.util.Log.i(callingObject.getClass().getName(), message);
        }
    }

    public static void w(String tag, String message) {
        if (WARN >= LOG_LEVEL) {
            android.util.Log.w(tag, message);
        }
    }

    public static void w(Object callingObject, String message) {
        if (WARN >= LOG_LEVEL) {
            android.util.Log.w(callingObject.getClass().getName(), message);
        }
    }

    public static void e(String tag, String message) {
        if (ERROR >= LOG_LEVEL) {
            android.util.Log.e(tag, message);
        }
    }

    public static void e(Object callingObject, String message) {
        if (ERROR >= LOG_LEVEL) {
            android.util.Log.e(callingObject.getClass().getName(), message);
        }
    }

    public static void e(String tag, String message, Throwable tr) {
        if (ERROR >= LOG_LEVEL) {
            android.util.Log.e(tag, message, tr);
        }
    }

    public static void e(Object callingObject, String message, Throwable tr) {
        if (ERROR >= LOG_LEVEL) {
            android.util.Log.e(callingObject.getClass().getName(), message, tr);
        }
    }

    public static void wtf(String tag, String message) {
        if (ASSERT >= LOG_LEVEL) {
            android.util.Log.wtf(tag, message);
        }
    }

    public static void wtf(Object callingObject, String message) {
        if (ASSERT >= LOG_LEVEL) {
            android.util.Log.wtf(callingObject.getClass().getName(), message);
        }
    }
}
