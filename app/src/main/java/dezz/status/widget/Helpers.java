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
}
