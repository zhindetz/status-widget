package dezz.status.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import java.time.LocalDateTime;

public class Helpers {

    private static String TAG = "Helpers";

    public static LocalDateTime getSunriseTime(double latitude, double longitude) {
        return LocalDateTime.now().minusHours(1); // Example of now is DAY
    }
    public static LocalDateTime getSunsetTime(double latitude, double longitude) {
        return LocalDateTime.now().plusHours(1); // Example of now is DAY
    }
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
