package dezz.status.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class Helpers {

    private static final String TAG = "Helpers";

    private static final TwilightCalculator twilightCalculator = new TwilightCalculator();

    /**
     * Determines whether the current time ({@link System#currentTimeMillis}) is within the night phase (night) for the given coordinates.
     * <p>
     * The method uses the twilight calculator {@link TwilightCalculator} to calculate the current light state
     * based on the current system time and geographic coordinates (latitude and longitude).
     * </p>
     *
     * @param latitude is the location's latitude (in degrees, for example: 55.7558 for Moscow)
     * @param longitude is the location's longitude (in degrees, for example: 37.6176 for Moscow)
     * @return {@code true} if it is nighttime at the specified location; {@code false} if it is daytime
     *
     * @see TwilightCalculator
     * @see TwilightCalculator#calculateTwilight(long, double, double)
     */
    public static boolean isNightNow(double latitude, double longitude) {
        twilightCalculator.calculateTwilight(System.currentTimeMillis(), latitude, longitude);
        return twilightCalculator.mState == TwilightCalculator.NIGHT;
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
        int nightMode = (new Preferences(context)).savedNightMode.get();
        boolean isSystemInNightMode =
                (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES ||
                (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM && isSystemInNightMode)) {
            return R.style.AppTheme_Night; // Night theme style
        } else {
            return R.style.AppTheme_Day; // Day theme style
        }
    }

    // Required for resolving color attributes programmatically, since R.attr will give ID of attr, but not color.
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
}
