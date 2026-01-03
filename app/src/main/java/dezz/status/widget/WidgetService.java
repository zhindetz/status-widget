/*
 * Copyright 2025 Dezz (https://github.com/DezzK)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dezz.status.widget;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.NotificationCompat;
import androidx.core.os.HandlerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dezz.status.widget.databinding.OverlayStatusWidgetBinding;

public class WidgetService extends Service {
    enum GnssState {
        OFF, BAD, GOOD
    }

    enum WiFiState {
        OFF, NO_INTERNET, INTERNET
    }

    private static final int[] GNSS_ICONS_MONO = {
            R.drawable.ic_mono_gps_off,
            R.drawable.ic_mono_gps_bad,
            R.drawable.ic_mono_gps_good
    };
    private static final int[] WIFI_ICONS_MONO = {
            R.drawable.ic_mono_wifi_off,
            R.drawable.ic_mono_wifi_no_internet,
            R.drawable.ic_mono_wifi_internet
    };

    private static final int[] GNSS_ICONS_COLOR = {
            R.drawable.ic_color_gps_off,
            R.drawable.ic_color_gps_bad,
            R.drawable.ic_color_gps_good
    };
    private static final int[] WIFI_ICONS_COLOR = {
            R.drawable.ic_color_wifi_off,
            R.drawable.ic_color_wifi_no_internet,
            R.drawable.ic_color_wifi_internet
    };

    private static final int[] GNSS_ICONS_MONOCOLOR = {
            R.drawable.ic_monocolor_gps_off,
            R.drawable.ic_monocolor_gps_bad,
            R.drawable.ic_monocolor_gps_good
    };
    private static final int[] WIFI_ICONS_MONOCOLOR = {
            R.drawable.ic_monocolor_wifi_off,
            R.drawable.ic_monocolor_wifi_no_internet,
            R.drawable.ic_monocolor_wifi_internet
    };

    private static final int[] GNSS_ICONS_MATERIAL = {
            R.drawable.ic_material_gps_off,
            R.drawable.ic_material_gps_bad,
            R.drawable.ic_material_gps_good
    };
    private static final int[] WIFI_ICONS_MATERIAL = {
            R.drawable.ic_material_wifi_off,
            R.drawable.ic_material_wifi_no_internet,
            R.drawable.ic_material_wifi_internet
    };

    private static final int[] GNSS_ICONS_SMALL = {
            R.drawable.ic_small_gps_off,
            R.drawable.ic_small_gps_bad,
            R.drawable.ic_small_gps_good
    };
    private static final int[] WIFI_ICONS_SMALL = {
            R.drawable.ic_small_wifi_off,
            R.drawable.ic_small_wifi_no_internet,
            R.drawable.ic_small_wifi_internet
    };


    protected enum GibCycleState {
        OUTER, INNER
    }
    protected static final int[] GIB_ICONS_CYCLE = {
            R.drawable.ic_gib_cycle_outer,
            R.drawable.ic_gib_cycle_inner
    };
    protected enum GibPlainState {
        OFF, ON
    }
    protected static final int[] GIB_ICONS_AC_MAX = {
            R.drawable.ic_gib_ac_max_off,
            R.drawable.ic_gib_ac_max_on
    };
    protected static final int[] GIB_ICONS_ELECTRIC_DEFROST = {
            R.drawable.ic_gib_electric_defrost_off,
            R.drawable.ic_gib_electric_defrost_on
    };
    protected static final int[] GIB_ICONS_FRONT_DEFROST = {
            R.drawable.ic_gib_front_defrost_off,
            R.drawable.ic_gib_front_defrost_on
    };
    protected static final int[] GIB_ICONS_BEHIND_DEFROST = {
            R.drawable.ic_gib_behind_defrost_off,
            R.drawable.ic_gib_behind_defrost_on
    };
    protected enum GibSeatState {
        OFF, COOL0, COOL1, COOL2, COOL3, HEAT0, HEAT1, HEAT2, HEAT3
    }

    protected static final int[] GIB_ICONS_SEAT_LEFT = {
            R.drawable.ic_gib_seat_left_off,
            R.drawable.ic_gib_seat_left_off, //ic_gib_seat_left_cool0,
            R.drawable.ic_gib_seat_left_cool1,
            R.drawable.ic_gib_seat_left_cool2,
            R.drawable.ic_gib_seat_left_cool3,
            R.drawable.ic_gib_seat_left_off, //ic_gib_seat_left_heat0,
            R.drawable.ic_gib_seat_left_heat1,
            R.drawable.ic_gib_seat_left_heat2,
            R.drawable.ic_gib_seat_left_heat3
    };

    protected static final int[] GIB_ICONS_SEAT_RIGHT = {
            R.drawable.ic_gib_seat_right_off,
            R.drawable.ic_gib_seat_right_off, //ic_gib_seat_right_cool0,
            R.drawable.ic_gib_seat_right_cool1,
            R.drawable.ic_gib_seat_right_cool2,
            R.drawable.ic_gib_seat_right_cool3,
            R.drawable.ic_gib_seat_right_off, //ic_gib_seat_right_heat0,
            R.drawable.ic_gib_seat_right_heat1,
            R.drawable.ic_gib_seat_right_heat2,
            R.drawable.ic_gib_seat_right_heat3
    };

    protected enum GibSteeringWheelState {
        OFF, HEAT0, HEAT1, HEAT2, HEAT3
    }
    protected static final int[] GIB_ICONS_STEERING_WHEEL = {
            R.drawable.ic_gib_steering_wheel_off,
            R.drawable.ic_gib_steering_wheel_heat0,
            R.drawable.ic_gib_steering_wheel_heat1,
            R.drawable.ic_gib_steering_wheel_heat2,
            R.drawable.ic_gib_steering_wheel_heat3
    };

    private static final String TAG = "WidgetService";
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "WidgetServiceChannel";
    private static final long GNSS_STATUS_CHECK_INTERVAL = 1_000;
    private static final long TWILIGHT_CALC_INTERVAL = 900_000;
    private static final long GIB_REFRESH_CHECK_INTERVAL = 21_000;
    private static final long GIB_TEMPERATURE_CHECK_INTERVAL = 15_000;

    private static WidgetService instance;

    private Preferences prefs;

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;

    private OverlayStatusWidgetBinding binding;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private GnssState gnssState = GnssState.OFF;
    private WiFiState wifiState = WiFiState.OFF;

    // TODO Is it will be good to move GIB states to GibManage?
    private GibCycleState gibCycleState = GibCycleState.OUTER;
    private GibPlainState gibAcMaxState = GibPlainState.OFF;
    private GibPlainState gibElectricDefrostState = GibPlainState.OFF;
    private GibPlainState gibFrontDefrostState = GibPlainState.OFF;
    private GibPlainState gibBehindDefrostState = GibPlainState.OFF;
    private GibSteeringWheelState gibSteeringWheelState = GibSteeringWheelState.OFF;
    private GibSeatState gibSeatFLState = GibSeatState.OFF;
    private GibSeatState gibSeatFRState = GibSeatState.OFF;
    private GibSeatState gibSeatRLState = GibSeatState.OFF;
    private GibSeatState gibSeatRRState = GibSeatState.OFF;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private LocationManager locationManager = null;
    private ConnectivityManager connectivityManager = null;
    private long lastLocationUpdateTime = 0;

    private int locationUpdateCount = 300;

    private GradientDrawable background = null;
    private int bgColor = -1;
    private int bgCornerRadius = -1;

    private GibManager gibManager;

    private final Runnable updateDateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateDateTime();
            mainHandler.postDelayed(this, 1000);
        }
    };

    private final Runnable updateGnssStatusRunnable = new Runnable() {
        @Override
        public void run() {
//            LogActivity.log(TAG, "Interval check of GNSS relevance (if it is outdated)");
            if (System.currentTimeMillis() - lastLocationUpdateTime > 10000) {
                setGnssStatus(GnssState.OFF);
            } else if (System.currentTimeMillis() - lastLocationUpdateTime > 5000) {
                setGnssStatus(GnssState.BAD);
            }

            mainHandler.postDelayed(this, GNSS_STATUS_CHECK_INTERVAL);
        }
    };

    private final Runnable updateTwilightTimeRunnable = new Runnable() {
        @Override
        public void run() {
            LogsActivity.log(TAG, "Interval check of twilight time");
            boolean isTwilightCalculationWasSuccessful = calculateTwilightAtCurrentLocation();
            // Let's schedule the next check here, so it will not be duplicated in updateOverlay()
            if (isTwilightCalculationWasSuccessful) {
                mainHandler.postDelayed(this, TWILIGHT_CALC_INTERVAL);
            } else {
                mainHandler.postDelayed(this, 5000);
                return;
            }

            int initialNightMode = prefs.savedNightMode.get();
            // Set night mode based on calculated state from TwilightCalculator, if different from current
            if (initialNightMode == AppCompatDelegate.MODE_NIGHT_YES && Helpers.getDayNightState() == TwilightCalculator.DAY) {
                LogsActivity.log(TAG, "Setting night mode to NO");
                prefs.savedNightMode.set(AppCompatDelegate.MODE_NIGHT_NO);
                updateOverlay(); // Update the overlay if night mode has changed. This will call applyPreferences() again
            } else if (initialNightMode == AppCompatDelegate.MODE_NIGHT_NO && Helpers.getDayNightState() == TwilightCalculator.NIGHT) {
                LogsActivity.log(TAG, "Setting night mode to YES");
                prefs.savedNightMode.set(AppCompatDelegate.MODE_NIGHT_YES);
                updateOverlay(); // Update the overlay if night mode has changed. This will call applyPreferences() again
            }

            // Schedule night mode update on next twilight, if it will occur before the next interval check for twilight
            long millisecondsToNextTwilight = Helpers.getMillisecondsToNextTwilight();
            if (millisecondsToNextTwilight > 0) {
                if (millisecondsToNextTwilight < TWILIGHT_CALC_INTERVAL) {
                    if (Helpers.getDayNightState() == TwilightCalculator.NIGHT) { // Night now, then set schedule to set NO when day comes
                        if (HandlerCompat.hasCallbacks(mainHandler, setNightModeNoRunnable)) {
                            mainHandler.removeCallbacks(setNightModeNoRunnable);
                        }
                        mainHandler.postDelayed(setNightModeNoRunnable, millisecondsToNextTwilight);
                        LogsActivity.log(TAG, "Scheduled night mode set NO in " + millisecondsToNextTwilight + " milliseconds");
                    } else { // Day now, then set schedule to set YES when night comes
                        if (HandlerCompat.hasCallbacks(mainHandler, setNightModeYesRunnable)) {
                            mainHandler.removeCallbacks(setNightModeYesRunnable);
                        }
                        mainHandler.postDelayed(setNightModeYesRunnable, millisecondsToNextTwilight);
                        LogsActivity.log(TAG, "Scheduled night mode set YES in " + millisecondsToNextTwilight + " milliseconds");
                    }
                } else {
                    LogsActivity.log(TAG, "It is not required to schedule night mode update for now since twilight will occur after the next interval check");
                }
            } else {
                LogsActivity.log(TAG, "TwilightCalculator returned 0 or negative milliseconds to next twilight");
            }

        }
    };

    private final Runnable setNightModeYesRunnable = new Runnable() {
        @Override
        public void run() {
            if (prefs.savedNightMode.get() != AppCompatDelegate.MODE_NIGHT_YES) {
                LogsActivity.log(TAG, "Setting night mode to YES");
                prefs.savedNightMode.set(AppCompatDelegate.MODE_NIGHT_YES);
                updateOverlay(); // Update the overlay if night mode has changed
            }
        }
    };

    private final Runnable setNightModeNoRunnable = new Runnable() {
        @Override
        public void run() {
            if (prefs.savedNightMode.get() != AppCompatDelegate.MODE_NIGHT_NO) {
                LogsActivity.log(TAG, "Setting night mode to NO");
                prefs.savedNightMode.set(AppCompatDelegate.MODE_NIGHT_NO);
                updateOverlay(); // Update the overlay if night mode has changed
            }
        }
    };

    private final GnssStatus.Callback gnssStatusCallback = new GnssStatus.Callback() {
        @Override
        public void onStarted() {
            LogsActivity.log(TAG, "GNSS is started");
            setGnssStatus(GnssState.BAD);
        }

        @Override
        public void onStopped() {
            LogsActivity.log(TAG, "GNSS is stopped");
            setGnssStatus(GnssState.OFF);
        }

        @Override
        public void onFirstFix(int ttffMillis) {
            LogsActivity.log(TAG, "GNSS has first fix");
            setGnssStatus(GnssState.BAD);
        }
    };

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
//            LogActivity.log(TAG, "Location changed: " + location);
            lastLocationUpdateTime = System.currentTimeMillis();
            if (location.hasAccuracy() && location.getAccuracy() < 20.0) {
                setGnssStatus(GnssState.GOOD);
            } else {
                setGnssStatus(GnssState.BAD);
            }

            if (++locationUpdateCount > 300) {
                prefs.latitude.set(location.getLatitude());
                prefs.longitude.set(location.getLongitude());
                locationUpdateCount = 0;
            }
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            LogsActivity.log(TAG, "Provider enabled: " + provider);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            LogsActivity.log(TAG, "Provider disabled: " + provider);
        }
    };

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            LogsActivity.log(TAG, "Wi-Fi is connected");
            if (wifiState == WiFiState.OFF) {
                setWifiStatus(WiFiState.NO_INTERNET);
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            LogsActivity.log(TAG, "Wi-Fi is lost");
            setWifiStatus(WiFiState.OFF);
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, NetworkCapabilities networkCapabilities) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                boolean hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                LogsActivity.log(TAG, "Wi-Fi capabilities changed, has internet = " + hasInternet);
                setWifiStatus(hasInternet ? WiFiState.INTERNET : WiFiState.NO_INTERNET);
            } else {
                setWifiStatus(WiFiState.OFF);
            }
        }
    };

    private final Runnable updatePropertiesFromGibRunnable = new Runnable() {
        @Override
        public void run() {
//            LogsActivity.log(TAG, "Interval check of properties from GIB");
            GibManager.getInstance(getBaseContext()).sendIntentsToGetCurrentGibProperties();

            if (prefs.scheduleRefreshGibProperties.get()) {
                mainHandler.postDelayed(this, GIB_REFRESH_CHECK_INTERVAL);
            }
        }
    };

    private final Runnable updateTemperaturesFromGibRunnable = new Runnable() {
        @Override
        public void run() {
//            LogsActivity.log(TAG, "Interval check of temperatures from GIB");
            GibManager.getInstance(getBaseContext()).sendIntentsToGetCurrentGibSensors();

            mainHandler.postDelayed(this, GIB_TEMPERATURE_CHECK_INTERVAL);
        }
    };

    @Override
    public void onCreate() {
        prefs = Preferences.getInstance(this);
        if (!Permissions.allPermissionsGranted(this)) {
            prefs.widgetEnabled.set(false);
            Toast.makeText(this, R.string.permissions_required, Toast.LENGTH_LONG).show();
            startMainActivity();
            stopSelf();
            return;
        }

        instance = this;

        windowManager = getSystemService(WindowManager.class);

        createOverlayView();

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    private void createOverlayView() {
        // Create the overlay view
        LogsActivity.log(TAG, "Creating overlay view");
        Context themedContext = new ContextThemeWrapper(this, Helpers.getThemeResId(this));

        LayoutInflater layoutInflater = LayoutInflater.from(themedContext);

        binding = OverlayStatusWidgetBinding.inflate(layoutInflater);
        binding.getRoot().setVisibility(View.VISIBLE);
        binding.getRoot().addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int backgroundCornerRadius = Math.min(binding.getRoot().getWidth(), binding.getRoot().getHeight()) / 2;
            int backgroundColor = Helpers.getColorFromAttr(themedContext, R.attr.widget_background) & 0x00FFFFFF | (prefs.backgroundAlpha.get() << 24);
            binding.overlayContainer.setBackground(getBackground(backgroundColor, backgroundCornerRadius));
        });

        applyPreferences();

        updateWifiStatus();
        updateGnssStatus();

        // Set up drag listener
        setupDragListener();

        // Add the view to the window
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = prefs.overlayX.get();
        params.y = prefs.overlayY.get();

        try {
            windowManager.addView(binding.getRoot(), params);
        } catch (Exception e) {
            LogsActivity.log(TAG, "Failed to add view to window manager", e);
            Toast.makeText(this, R.string.overlay_permission_required, Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogsActivity.log(TAG, "Configuration changed");
        updateOverlay();
    }

    protected void updateOverlay() {
        LogsActivity.log(TAG, "Updating overlay view");
        if (binding != null) {
            windowManager.removeView(binding.getRoot());
            createOverlayView();
        }
    }

    @SuppressLint("MissingPermission")
    public void applyPreferences() {
        updateDateTime();

        int iconSize = prefs.iconSize.get();
        int timeFontSize = prefs.timeFontSize.get();
        int dateFontSize = prefs.dateFontSize.get();
        int padding = Math.max(iconSize, Math.max(timeFontSize, dateFontSize)) / 2;

        binding.getRoot().setPadding(padding, 0, padding, 0);

        ViewGroup.LayoutParams iconParams = binding.wifiStatusIcon.getLayoutParams();
        iconParams.width = iconSize;
        iconParams.height = iconSize;
        binding.wifiStatusIcon.setLayoutParams(iconParams);

        iconParams = binding.gnssStatusIcon.getLayoutParams();
        iconParams.width = iconSize;
        iconParams.height = iconSize;
        binding.gnssStatusIcon.setLayoutParams(iconParams);

        float timeOutlineWidth = Math.max(2F, prefs.timeFontSize.get() / 32F);
        float dateOutlineWidth = Math.max(2F, prefs.dateFontSize.get() / 32F);
        Context themedContext = new ContextThemeWrapper(this, Helpers.getThemeResId(this));
        int outlineColor = Helpers.getColorFromAttr(themedContext, R.attr.text_outline) & 0x00FFFFFF | (prefs.textOutlineAlpha.get() << 24); // Direct use of R.attr will give ID of attr, but not color
        binding.timeText.setOutlineColor(outlineColor);
        binding.timeText.setOutlineWidth(timeOutlineWidth);
        binding.dateText.setOutlineColor(outlineColor);
        binding.dateText.setOutlineWidth(dateOutlineWidth);

        binding.timeText.setTextSize(TypedValue.COMPLEX_UNIT_PX, prefs.timeFontSize.get());
        binding.dateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, prefs.dateFontSize.get());
        binding.timeText.setVisibility(prefs.showTime.get() ? View.VISIBLE : View.GONE);
        binding.dateText.setVisibility(prefs.showDate.get() || prefs.showDayOfTheWeek.get() ? View.VISIBLE : View.GONE);

        // Calendar alignment
        switch (prefs.calendarAlignment.get()) {
            case 1 -> binding.dateText.setGravity(Gravity.CENTER_HORIZONTAL);
            case 2 -> binding.dateText.setGravity(Gravity.END);
            default -> binding.dateText.setGravity(Gravity.START);
        }

        // Icons (GPS and Wi-Fi)
        binding.wifiStatusIcon.setVisibility(prefs.showWifiIcon.get() ? View.VISIBLE : View.GONE);
        binding.gnssStatusIcon.setVisibility(prefs.showGnssIcon.get() ? View.VISIBLE : View.GONE);
        binding.gibIndicators.setVisibility(prefs.showGibIndicators.get() ? View.VISIBLE : View.GONE);

        LinearLayout.LayoutParams dateTimeLayoutParams = (LinearLayout.LayoutParams) binding.dateTimeContainer.getLayoutParams();
        dateTimeLayoutParams.setMargins(0, 0, prefs.spacingBetweenTextsAndIcons.get(), 0);
        binding.dateTimeContainer.setLayoutParams(dateTimeLayoutParams);

        ((LinearLayout.LayoutParams) binding.gibIndicators.getLayoutParams())
                .setMargins(0, 0, prefs.spacingBetweenTextsAndIcons.get(), 0);

        binding.timeText.setTranslationY(prefs.adjustTimeY.get());
        binding.dateText.setTranslationY(prefs.adjustDateY.get());
        binding.gibIndicators.setTranslationY(prefs.adjustGibIndicatorsY.get());

        mainHandler.removeCallbacks(updateDateTimeRunnable);
        if (prefs.showDate.get() || prefs.showTime.get()) {
            mainHandler.postDelayed(updateDateTimeRunnable, 1000);
        }

        if (prefs.showWifiIcon.get()) {
            if (connectivityManager == null) {
                connectivityManager = getSystemService(ConnectivityManager.class);

                for (Network net : connectivityManager.getAllNetworks()) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(net);
                    if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        boolean hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                        setWifiStatus(hasInternet ? WiFiState.INTERNET : WiFiState.NO_INTERNET);
                        break;
                    }
                }

                NetworkRequest networkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
                connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
            }
            updateWifiStatus();
        } else if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
            connectivityManager = null;
        }

        if (prefs.showGnssIcon.get() || prefs.nightModeSpinnerOption.get() == 3) { // Settings related to locationManager

            LogsActivity.log(TAG, "Location settings are triggered");
            if (locationManager == null) { // Setup locationManager
                locationManager = getSystemService(LocationManager.class);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener, Looper.getMainLooper());
            }
            if (prefs.showGnssIcon.get()) { // Monitor GNSS status
                locationManager.registerGnssStatusCallback(gnssStatusCallback, mainHandler);
                if (!HandlerCompat.hasCallbacks(mainHandler, updateGnssStatusRunnable)) {
                    mainHandler.postDelayed(updateGnssStatusRunnable, GNSS_STATUS_CHECK_INTERVAL);
                    updateGnssStatus();
                }
            } else { // Do not monitor GNSS status, but still monitor location updates for day/night mode
                if (HandlerCompat.hasCallbacks(mainHandler, updateGnssStatusRunnable)) {
                    LogsActivity.log(TAG, "Removing location updates and GNSS status callback");
                    mainHandler.removeCallbacks(updateGnssStatusRunnable);
                }
                locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
            }
            if (prefs.nightModeSpinnerOption.get() == 3) { // Monitor day/nighttime at current location
                if (!HandlerCompat.hasCallbacks(mainHandler, updateTwilightTimeRunnable)) {
                    LogsActivity.log(TAG, "Registered twilight time update runnable");
                    mainHandler.postDelayed(updateTwilightTimeRunnable, 2000);
                }
            } else { // Do not monitor day/nighttime but still monitor location updates for GNSS status
                if (HandlerCompat.hasCallbacks(mainHandler, updateTwilightTimeRunnable)) {
                    LogsActivity.log(TAG, "Removing twilight and day/night mode update");
                    mainHandler.removeCallbacks(updateTwilightTimeRunnable);
                }
                mainHandler.removeCallbacks(setNightModeYesRunnable);
                mainHandler.removeCallbacks(setNightModeNoRunnable);
            }
        } else if (locationManager != null) { // then locationManager is not needed anymore
            LogsActivity.log(TAG, "Removing all location callbacks");
            mainHandler.removeCallbacks(updateTwilightTimeRunnable);
            mainHandler.removeCallbacks(setNightModeYesRunnable);
            mainHandler.removeCallbacks(setNightModeNoRunnable);
            mainHandler.removeCallbacks(updateGnssStatusRunnable);
            locationManager.removeUpdates(locationListener);
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
            locationManager = null;
        }

        if (prefs.showGibIndicators.get() && prefs.deviceType.get() != Constants.DEVICE_TYPE_DEFAULT) {
            if (gibManager == null) {
                gibManager = GibManager.getInstance(this);
            }
            binding.gibCycle.setLayoutParams(iconParams);
            binding.gibAcMax.setLayoutParams(iconParams);
            binding.gibElectricDefrost.setLayoutParams(iconParams);
            binding.gibFrontDefrost.setLayoutParams(iconParams);
            binding.gibBehindDefrost.setLayoutParams(iconParams);
            binding.gibSteeringWheel.setLayoutParams(iconParams);
            binding.gibSeatFrontLeft.setLayoutParams(iconParams);
            binding.gibSeatFrontRight.setLayoutParams(iconParams);
            binding.gibSeatRearLeft.setLayoutParams(iconParams);
            binding.gibSeatRearRight.setLayoutParams(iconParams);
            if (!mainHandler.hasCallbacks(updatePropertiesFromGibRunnable)) {
                mainHandler.postDelayed(updatePropertiesFromGibRunnable, GIB_REFRESH_CHECK_INTERVAL);
            }
            if (!mainHandler.hasCallbacks(updateTemperaturesFromGibRunnable)) {
                mainHandler.postDelayed(updateTemperaturesFromGibRunnable, GIB_TEMPERATURE_CHECK_INTERVAL);
            }

            LogsActivity.log(TAG, "GIB indicators are turned ON");
        } else if (gibManager != null) {
            mainHandler.removeCallbacks(updatePropertiesFromGibRunnable);
            mainHandler.removeCallbacks(updateTemperaturesFromGibRunnable);
            gibManager.unregister();
            gibManager = null;
            LogsActivity.log(TAG, "GIB indicators are turned OFF");
        }

        if (prefs.showGibIndicatorsAlways.get()) {
            binding.gibCycle.setVisibility(View.VISIBLE);
            binding.gibAcMax.setVisibility(View.VISIBLE);
            binding.gibElectricDefrost.setVisibility(View.VISIBLE);
            binding.gibFrontDefrost.setVisibility(View.VISIBLE);
            binding.gibBehindDefrost.setVisibility(View.VISIBLE);
            binding.gibSteeringWheel.setVisibility(View.VISIBLE);
            binding.gibSeatFrontLeft.setVisibility(View.VISIBLE);
            binding.gibSeatFrontRight.setVisibility(View.VISIBLE);
            binding.gibSeatRearLeft.setVisibility(View.VISIBLE);
            binding.gibSeatRearRight.setVisibility(View.VISIBLE);
        } else {
            if (gibCycleState == GibCycleState.OUTER) {
                binding.gibCycle.setVisibility(View.INVISIBLE);
            }
            if (gibAcMaxState == GibPlainState.OFF) {
                binding.gibAcMax.setVisibility(View.INVISIBLE);
            }
            if (gibElectricDefrostState == GibPlainState.OFF) {
                binding.gibElectricDefrost.setVisibility(View.INVISIBLE);
            }
            if (gibFrontDefrostState == GibPlainState.OFF) {
                binding.gibFrontDefrost.setVisibility(View.INVISIBLE);
            }
            if (gibBehindDefrostState == GibPlainState.OFF) {
                binding.gibBehindDefrost.setVisibility(View.INVISIBLE);
            }
            if (List.of(GibSteeringWheelState.OFF, GibSteeringWheelState.HEAT0).contains(gibSteeringWheelState)) {
                binding.gibSteeringWheel.setVisibility(View.INVISIBLE);
            }
            if (List.of(GibSeatState.OFF, GibSeatState.COOL0, GibSeatState.HEAT0).contains(gibSeatFLState)) {
                binding.gibSeatFrontLeft.setVisibility(View.INVISIBLE);
            }
            if (List.of(GibSeatState.OFF, GibSeatState.COOL0, GibSeatState.HEAT0).contains(gibSeatFRState)) {
                binding.gibSeatFrontRight.setVisibility(View.INVISIBLE);
            }
            if (List.of(GibSeatState.OFF, GibSeatState.HEAT0).contains(gibSeatRLState)) {
                binding.gibSeatRearLeft.setVisibility(View.INVISIBLE);
            }
            if (List.of(GibSeatState.OFF, GibSeatState.HEAT0).contains(gibSeatRRState)) {
                binding.gibSeatRearRight.setVisibility(View.INVISIBLE);
            }
        }

        binding.gibTemperatures.setOutlineColor(outlineColor);
        binding.gibTemperatures.setOutlineWidth(dateOutlineWidth);
        binding.gibTemperatures.setTextSize(TypedValue.COMPLEX_UNIT_PX, prefs.dateFontSize.get());
    }

    protected boolean calculateTwilightAtCurrentLocation() {
        LogsActivity.log(TAG, "Calculating twilight based on current location");
        double latitude = prefs.latitude.get();
        double longitude = prefs.longitude.get();

        if (latitude != 0 && longitude != 0) {
            return Helpers.calculateTwilight(latitude, longitude);
        }
        return false;
    }

    private Drawable getBackground(int color, int cornerRadius) {
        if (this.background == null || color != this.bgColor || cornerRadius != this.bgCornerRadius) {
            this.background = new GradientDrawable();
            this.background.setColor(color);
            this.background.setCornerRadius(cornerRadius);
            this.bgColor = color;
            this.bgCornerRadius = cornerRadius;
        }

        return this.background;
    }

    private void updateDateTime() {
        boolean showTime = prefs.showTime.get();
        boolean showDate = prefs.showDate.get();
        boolean showDayOfTheWeek = prefs.showDayOfTheWeek.get();

        if (!showTime && !showDate && !showDayOfTheWeek) {
            return;
        }

        boolean showFullDayAndMonth = prefs.showFullDayAndMonth.get();

        String divider = (showDate && showDayOfTheWeek) ? (prefs.oneLineLayout.get() ? "," : " \n") : "";
        String dayOfTheWeekFormatStr = showFullDayAndMonth ? "EEEE" : "EEE";
        String dateFormatStr = showFullDayAndMonth ? "d MMMM" : "d MMM";

        // We add spaces at the start/end to avoid outline cropping by canvas which is not ready for the outline
        String fullFormatStr = (showDayOfTheWeek ? " " + dayOfTheWeekFormatStr + divider : "") + (showDate ? " " + dateFormatStr : "") + " ";

        Date now = new Date();
        String timeStr = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(now);
        String dateStr = new SimpleDateFormat(fullFormatStr, Locale.getDefault()).format(now);
        if (showTime && !timeStr.contentEquals(binding.timeText.getText())) {
            binding.timeText.setText(timeStr);
        }
        if ((showDate || showDayOfTheWeek) && !dateStr.contentEquals(binding.dateText.getText())) {
            binding.dateText.setText(dateStr);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupDragListener() {
        binding.getRoot().setOnTouchListener((v, event) -> {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) binding.getRoot().getLayoutParams();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(binding.getRoot(), params);
                    return true;

                case MotionEvent.ACTION_UP:
                    savePosition();

                    // Handle click
                    if (Math.abs(event.getRawX() - initialTouchX) < 5 && Math.abs(event.getRawY() - initialTouchY) < 5) {
                        if (binding.wifiStatusIcon.getVisibility() == View.VISIBLE &&
                                getBounds(binding.wifiStatusIcon).contains((int) event.getX(), (int) event.getY())) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            return true;
                        }
                        if (binding.gnssStatusIcon.getVisibility() == View.VISIBLE &&
                                getBounds(binding.gnssStatusIcon).contains((int) event.getX(), (int) event.getY())) {
                            Intent intent = getPackageManager().getLaunchIntentForPackage("dezz.gnssshare.client");
                            if (intent == null) {
                                intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            return true;
                        }

                        startMainActivity();
                    }
                    return true;
            }
            return false;
        });
    }

    private void startMainActivity() {
        Intent startIntent = new Intent(WidgetService.this, MainActivity.class);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
    }

    private void setWifiStatus(WiFiState newState) {
        wifiState = newState;
        updateWifiStatus();
    }

    private void updateWifiStatus() {
        updateIconStatus(WIFI_ICONS_MONO, WIFI_ICONS_COLOR, WIFI_ICONS_MONOCOLOR, WIFI_ICONS_MATERIAL, WIFI_ICONS_SMALL, binding.wifiStatusIcon, wifiState.ordinal());
    }

    private void setGnssStatus(GnssState newState) {
        gnssState = newState;
        updateGnssStatus();
    }

    private void updateGnssStatus() {
        updateIconStatus(GNSS_ICONS_MONO, GNSS_ICONS_COLOR, GNSS_ICONS_MONOCOLOR, GNSS_ICONS_MATERIAL, GNSS_ICONS_SMALL, binding.gnssStatusIcon, gnssState.ordinal());
    }

    protected void setGibCycleStatus(GibCycleState newState) {
        gibCycleState = newState;
        updateIconStatus(GIB_ICONS_CYCLE, binding.gibCycle, gibCycleState.ordinal());
        binding.gibCycle.setVisibility(
                (gibCycleState != GibCycleState.OUTER || prefs.showGibIndicatorsAlways.get())
                        ? View.VISIBLE
                        : View.INVISIBLE
        );
    }

    protected void setGibAcMaxStatus(GibPlainState newState) {
        gibAcMaxState = newState;
        updateIconStatus(GIB_ICONS_AC_MAX, binding.gibAcMax, gibAcMaxState.ordinal());
        binding.gibAcMax.setVisibility(
                (gibAcMaxState != GibPlainState.OFF || prefs.showGibIndicatorsAlways.get())
                        ? View.VISIBLE
                        : View.INVISIBLE
        );
    }

    protected void setGibElectricDefrostStatus(GibPlainState newState) {
        gibElectricDefrostState = newState;
        updateIconStatus(GIB_ICONS_ELECTRIC_DEFROST, binding.gibElectricDefrost, gibElectricDefrostState.ordinal());
        binding.gibElectricDefrost.setVisibility(
                (gibElectricDefrostState != GibPlainState.OFF || prefs.showGibIndicatorsAlways.get())
                        ? View.VISIBLE
                        : View.INVISIBLE
        );
    }

    protected void setGibFrontDefrostStatus(GibPlainState newState) {
        gibFrontDefrostState = newState;
        updateIconStatus(GIB_ICONS_FRONT_DEFROST, binding.gibFrontDefrost, gibFrontDefrostState.ordinal());
        binding.gibFrontDefrost.setVisibility(
                (gibFrontDefrostState != GibPlainState.OFF || prefs.showGibIndicatorsAlways.get())
                        ? View.VISIBLE
                        : View.INVISIBLE
        );
    }

    protected void setGibBehindDefrostStatus(GibPlainState newState) {
        gibBehindDefrostState = newState;
        updateIconStatus(GIB_ICONS_BEHIND_DEFROST, binding.gibBehindDefrost, gibBehindDefrostState.ordinal());
        binding.gibBehindDefrost.setVisibility(
                (gibBehindDefrostState != GibPlainState.OFF || prefs.showGibIndicatorsAlways.get())
                        ? View.VISIBLE
                        : View.INVISIBLE
        );
    }

    protected void setGibIconsSteeringWheel(GibSteeringWheelState newState) {
        gibSteeringWheelState = newState;
        updateIconStatus(GIB_ICONS_STEERING_WHEEL, binding.gibSteeringWheel, gibSteeringWheelState.ordinal());
        binding.gibSteeringWheel.setVisibility(
                (gibSteeringWheelState == GibSteeringWheelState.OFF || gibSteeringWheelState == GibSteeringWheelState.HEAT0)
                        ? (prefs.showGibIndicatorsAlways.get() ? View.VISIBLE : View.INVISIBLE)
                        : View.VISIBLE
        );
    }

    private boolean isHeatOverridingCool(GibSeatState oldState, GibSeatState newState) {
        return (oldState.name().startsWith("HEAT") && newState == GibSeatState.COOL0) ||
                (oldState.name().startsWith("COOL") && newState == GibSeatState.HEAT0);
    }

    protected void setGibIconsSeatFrontLeft(GibSeatState newState) {
        if (isHeatOverridingCool(gibSeatFLState, newState)) return;
        gibSeatFLState = newState;
        updateIconStatus(GIB_ICONS_SEAT_LEFT, binding.gibSeatFrontLeft, gibSeatFLState.ordinal());
        binding.gibSeatFrontLeft.setVisibility(
                (gibSeatFLState == GibSeatState.OFF || gibSeatFLState == GibSeatState.COOL0 || gibSeatFLState == GibSeatState.HEAT0)
                        ? (prefs.showGibIndicatorsAlways.get() ? View.VISIBLE : View.INVISIBLE)
                        : View.VISIBLE
        );
    }

    protected void setGibIconsSeatFrontRight(GibSeatState newState) {
        if (isHeatOverridingCool(gibSeatFRState, newState)) return;
        gibSeatFRState = newState;
        updateIconStatus(GIB_ICONS_SEAT_RIGHT, binding.gibSeatFrontRight, gibSeatFRState.ordinal());
        binding.gibSeatFrontRight.setVisibility(
                (gibSeatFRState == GibSeatState.OFF || gibSeatFRState == GibSeatState.COOL0 || gibSeatFRState == GibSeatState.HEAT0)
                        ? (prefs.showGibIndicatorsAlways.get() ? View.VISIBLE : View.INVISIBLE)
                        : View.VISIBLE
        );
    }

    protected void setGibIconsSeatRearLeft(GibSeatState newState) {
        gibSeatRLState = newState;
        updateIconStatus(GIB_ICONS_SEAT_LEFT, binding.gibSeatRearLeft, gibSeatRLState.ordinal());
        binding.gibSeatRearLeft.setVisibility(
                (gibSeatRLState == GibSeatState.OFF || gibSeatRLState == GibSeatState.COOL0 || gibSeatRLState == GibSeatState.HEAT0)
                        ? (prefs.showGibIndicatorsAlways.get() ? View.VISIBLE : View.INVISIBLE)
                        : View.VISIBLE
        );
    }

    protected void setGibIconsSeatRearRight(GibSeatState newState) {
        gibSeatRRState = newState;
        updateIconStatus(GIB_ICONS_SEAT_RIGHT, binding.gibSeatRearRight, gibSeatRRState.ordinal());
        binding.gibSeatRearRight.setVisibility(
                (gibSeatRRState == GibSeatState.OFF || gibSeatRRState == GibSeatState.COOL0 || gibSeatRRState == GibSeatState.HEAT0)
                        ? (prefs.showGibIndicatorsAlways.get() ? View.VISIBLE : View.INVISIBLE)
                        : View.VISIBLE
        );
    }

    protected void setGibIndicatorTemperatures(String value) {
        binding.gibTemperatures.setText(value);
    }

    private void updateIconStatus(int[] resources, ImageView icon, int state) {
            icon.setImageResource(resources[state]);
    }
    private void updateIconStatus(int[] monoResources, int[] colorResources, int[] monocolorResources, int[] materialResources, int[] smallResources, ImageView icon, int state) {
        switch (prefs.iconStyle.get()) {
            case 0 -> icon.setImageResource(monoResources[state]);
            case 1 -> icon.setImageResource(colorResources[state]);
            case 2 -> icon.setImageResource(monocolorResources[state]);
            case 3 -> icon.setImageResource(materialResources[state]);
            case 4 -> icon.setImageResource(smallResources[state]);
        }
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.notification_channel_title), NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle(getString(R.string.app_name)).setContentText(getString(R.string.notification_content)).setSmallIcon(R.drawable.ic_mono_gps_good).setContentIntent(pendingIntent).setOngoing(true).build();
    }

    // Add this method to save position
    private void savePosition() {
        if (params != null) {
            prefs.overlayX.set(params.x);
            prefs.overlayY.set(params.y);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;

        mainHandler.removeCallbacks(updateTwilightTimeRunnable);
        mainHandler.removeCallbacks(setNightModeYesRunnable);
        mainHandler.removeCallbacks(setNightModeNoRunnable);
        mainHandler.removeCallbacks(updateGnssStatusRunnable);
        mainHandler.removeCallbacks(updateDateTimeRunnable);

        if (binding != null
                && binding.getRoot().getParent() != null // Check if the view is attached to a parent to avoid IllegalArgumentException
                && windowManager != null) {
            windowManager.removeView(binding.getRoot());
        }

        if (locationManager != null) {
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback);
            locationManager.removeUpdates(locationListener);
        }

        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }

        if (gibManager != null) {
            gibManager.unregister();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static WidgetService getInstance() {
        return instance;
    }

    public static boolean isRunning() {
        return instance != null;
    }

    private static Rect getBounds(View view) {
        return new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }
}
