package dezz.status.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class Helpers {

    private static final String TAG = "Helpers";

    private static final TwilightCalculator twilightCalculator = new TwilightCalculator();

    /**
     * Calculates the sunrise and sunset times (civil twilight) for the given location and current time.
     * <p>
     * This method uses the {@link TwilightCalculator} to determine the daily twilight period based on
     * the provided latitude and longitude. If the sun never rises or never sets at the given location
     * (e.g., polar day or polar night), the calculation will fail, and the method returns {@code false}.
     * <p>
     * After calling this method, the results can be retrieved using:
     * <ul>
     *     <li>{@link #getDayNightState()} — to get the current day/night state.</li>
     *     <li>{@link #getMillisecondsToNextTwilight()} — to get the time until the next transition.</li>
     * </ul>
     *
     * @param latitude  the geographical latitude of the location in degrees (positive for north,
     *                  negative for south)
     * @param longitude the geographical longitude of the location in degrees (positive for east,
     *                  negative for west)
     * @return {@code true} if the twilight calculation was successful and sunrise/sunset times are valid;
     *         {@code false} if the sun never rises or never sets at this location and time (e.g.,
     *         polar regions during polar day/night)
     *
     * @see TwilightCalculator#calculateTwilight(long, double, double)
     * @see #getDayNightState()
     * @see #getMillisecondsToNextTwilight()
     */
    public static boolean calculateTwilight(double latitude, double longitude) {
        Log.d(TAG, "calculateTwilight called");
        twilightCalculator.calculateTwilight(System.currentTimeMillis(), latitude, longitude);
        if (twilightCalculator.mSunrise == -1 || twilightCalculator.mSunset == -1) {
            Log.d(TAG, "Twilight will never occur");
            return false;
        }
        return true;
    }
    /**
     * Returns the current day/night state as determined by the latest twilight calculation.
     * <p>
     * The returned value is one of:
     * <ul>
     *     <li>{@link TwilightCalculator#DAY} — if it is currently daytime (between sunrise and sunset)</li>
     *     <li>{@link TwilightCalculator#NIGHT} — if it is currently nighttime (before sunrise or after sunset)</li>
     * </ul>
     * <p>
     * This state is calculated based on civil twilight and assumes that night begins in the evening
     * after sunset and ends in the morning at sunrise.
     * <p>
     * Note: The state is only valid after a successful call to {@link #calculateTwilight(double, double)}.
     * If no calculation has been performed yet, the returned value may be outdated or incorrect.
     *
     * @return the current day/night state, either {@link TwilightCalculator#DAY} or {@link TwilightCalculator#NIGHT}
     *
     * @see #calculateTwilight(double, double)
     * @see #getMillisecondsToNextTwilight()
     */
    public static int getDayNightState() {
        return twilightCalculator.mState;
    }

    /**
     * Returns the number of milliseconds until the next twilight transition (sunrise or sunset).
     * <p>
     * If it is currently night, this method returns the time until sunrise.
     * If it is currently day, it returns the time until sunset.
     * <p>
     * This value can be used to schedule updates or alarms for dynamic theme switching
     * or UI changes based on day/night cycles.
     * <p>
     * If the twilight calculation has not been performed yet or resulted in invalid data
     * (e.g., polar day/night where sunrise/sunset does not occur), the return value
     * may be negative or inaccurate. It is recommended to call {@link #calculateTwilight(double, double)}
     * first to ensure valid results.
     *
     * @return the number of milliseconds until the next transition (sunrise or sunset),
     *         which may be negative if the event has already passed, or zero if imminent
     *
     * @see #calculateTwilight(double, double)
     * @see #getDayNightState()
     */
    public static long getMillisecondsToNextTwilight() {
        if (twilightCalculator.mState == TwilightCalculator.NIGHT) {
            // It is night → wait for sunrise
            return twilightCalculator.mSunrise - System.currentTimeMillis();
        } else {
            // It is day → wait for sunset
            return twilightCalculator.mSunset - System.currentTimeMillis();
        }
    }

    /**
     * Returns the theme style resource ID (day or night) depending on the current application and system settings.
     * <p>
     * This method determines which theme should be used based on:
     * <ul>
     * <li>The user's selected theme mode (light, dark, system),</li>
     * <li>The current system mode (night/day).</li>
     * </ul>
     *
     * If the user selected the dark theme or "follow system" mode while the system night theme is enabled,
     * the night theme resource {@link R.style#AppTheme_Night} is returned. Otherwise, the day theme
     * resource {@link R.style#AppTheme_Day} is returned.
     *
     * @param context - the application context needed to access system settings and configuration
     * @return the theme style resource ID ({@link R.style#AppTheme_Night} or {@link R.style#AppTheme_Day})
     *
     * @see Preferences#savedNightMode
     * @see AppCompatDelegate#MODE_NIGHT_YES
     * @see AppCompatDelegate#MODE_NIGHT_FOLLOW_SYSTEM
     * @see Configuration#UI_MODE_NIGHT_MASK
     * @see Configuration#UI_MODE_NIGHT_YES
     */
    public static int getThemeResId(Context context) {
        int nightMode = Preferences.getInstance(context).savedNightMode.get();
        boolean isSystemInNightMode =
                (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES ||
                (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM && isSystemInNightMode)) {
            return R.style.AppTheme_Night; // Night theme style
        } else {
            return R.style.AppTheme_Day; // Day theme style
        }
    }

    /**
     * Returns the color associated with the specified theme attribute.
     * Instead of using {@link ContextCompat#getColor(Context, int)} directly, this method is used to resolve color attributes.
     * <p>
     * ContextCompat.getColor(this, R.color.text_primary) should be replaced with getColorFromAttr(themedContext, R.attr.text_primary)
     * <p>
     * The method attempts to resolve the theme attribute to color. If the attribute refers directly to a color value
     * (eg #FF0000), this value is returned. If the attribute refers to a color resource (for example, @color/red),
     * method resolves this resource and returns the corresponding color.
     *
     * @param context the themed context of the application or activity used to access the topic and resources
     * @param attr identifier of the theme attribute (e.g. R.attr.text_primary) that should be resolved to color
     * @return the color in int format (e.g. 0xFFAARRGGBB) corresponding to the specified attribute
     * @throws IllegalArgumentException if the specified attribute cannot be found or resolved
     *
     * @see TypedValue#TYPE_FIRST_COLOR_INT
     * @see TypedValue#TYPE_LAST_COLOR_INT
     * @see ContextCompat#getColor(Context, int)
     */
    public static int getColorFromAttr(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attr, typedValue, true)) {
            if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT &&
                    typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return typedValue.data; // Color linked to the attr
            } else {
                // else, try to resolve as a resource ID
                return ContextCompat.getColor(context, typedValue.resourceId);
            }
        }
        throw new IllegalArgumentException("Was not able to resolve attribute with ID: " + attr);
    }



    public static float parseAsFloat(Object obj, float defaultValue) {
        if (obj == null) {
            return defaultValue;
        } else if (obj instanceof String s && !s.isEmpty()) {
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                LogsActivity.log(TAG, "Cannot parse as float: " + s, e);
                return defaultValue;
            }
        } else if (obj instanceof Float f) {
            return f;
        } else if (obj instanceof Double d) {
            return d.floatValue();
        } else if (obj instanceof Integer i) {
            return i.floatValue();
        } else if (obj instanceof Long l) {
            return l.floatValue();
        } else {
            LogsActivity.log(TAG, "Unsupported type: " + obj.getClass().getSimpleName());
            return defaultValue;
        }
    }

    public static int parseAsInt(Object obj, int defaultValue) {
        if (obj == null) {
            return defaultValue;
        } else if (obj instanceof String s && !s.isEmpty()) {
            try {
                // At first, parse as double (in case of a fractional string)
                double doubleValue = Double.parseDouble(s);
                // Then, round to the nearest integer and convert to int
                return (int) Math.round(doubleValue);
            } catch (NumberFormatException e) {
                LogsActivity.log(TAG, "Cannot parse as number: " + s, e);
                return defaultValue;
            }
        } else if (obj instanceof Integer i) {
            return i;
        } else if (obj instanceof Double d) {
            return d.intValue();
        } else if (obj instanceof Float f) {
            return f.intValue();
        } else if (obj instanceof Long l) {
            return l.intValue();
        } else {
            LogsActivity.log(TAG, "Unsupported type: " + obj.getClass().getSimpleName());
            return defaultValue;
        }
    }
}
