package dezz.status.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;

import java.util.Locale;


public class GibManager {
    private static final String TAG = "GibManager";
    private static GibManager instance;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isListening = false;
    private final Context context;
    private final Preferences prefs;
    private final int deviceType;

    private float indoorTemp = 0f;
    private float outdoorTemp = 0f;
    private float fuelLevel = 0f;
    private float fuelPercentOfFilled = 0f;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GibCommunicationHandler.getInstance(context).getIntent(intent);
        }
    };

    private GibManager(Context context) {
        this.prefs = Preferences.getInstance(context);
        this.deviceType = prefs.deviceType.get();
        this.context = context.getApplicationContext();
        registerReceiver();
    }

    public static synchronized GibManager getInstance(Context context) {
        if (instance == null) {
            instance = new GibManager(context);
            LogsActivity.log(TAG, "GibManager instance created: device type = " + instance.deviceType);
        } else if (instance.deviceType != instance.prefs.deviceType.get()) {
            if (instance.isListening()) {
                instance.unregister();
                instance = new GibManager(context);
                LogsActivity.log(TAG, "GibManager instance recreated due to device type change: device type = " + instance.deviceType);
            }
        }
        if (!instance.isListening()) instance.registerReceiver();
        return instance;
    }

    private void registerReceiver() {
        if (!isListening) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.IGibConstants.INTENT_ACTION_PROPERTY_CHANGED);
            filter.addAction(Constants.IGibConstants.INTENT_ACTION_SENSOR_RESULT);
            filter.addAction(Constants.IGibConstants.INTENT_ACTION_PROPERTY_RESULT);
            try {
                context.registerReceiver(receiver, filter);
                isListening = true;
                LogsActivity.log(TAG, "BroadcastReceiver is registered");
                sendIntentsToListenGibChanges();
                sendIntentsToGetCurrentGibProperties();
                sendIntentsToGetCurrentGibSensors();
            } catch (Exception e) {
                LogsActivity.log(TAG, "Error registering BroadcastReceiver", e);
            }
        }
    }

    protected void sendIntentsToGetCurrentGibProperties() {
        Constants.IGibConstants constants = GibCommunicationHandler.getInstance(context).getDeviceSpecificConstants();
        GibCommunicationHandler.getInstance(context)
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getCirculationId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getAcMaxId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getElectricDefrostId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getFrontDefrostId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getBehindDefrostId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getSteeringWheelId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getSeatHeatId()).setArea(constants.getSeatAreaFrontLeft()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getSeatCoolId()).setArea(constants.getSeatAreaFrontLeft()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getSeatHeatId()).setArea(constants.getSeatAreaFrontRight()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getSeatCoolId()).setArea(constants.getSeatAreaFrontRight()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getSeatHeatId()).setArea(constants.getSeatAreaRearLeft()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_GET, GibIntentExtra.create().setId(constants.getSeatHeatId()).setArea(constants.getSeatAreaRearRight()));
//        LogsActivity.log(TAG, "Intents for GIB getting current properties are sent");
    }

    protected void sendIntentsToGetCurrentGibSensors() {
        Constants.IGibConstants constants = GibCommunicationHandler.getInstance(context).getDeviceSpecificConstants();
        GibCommunicationHandler.getInstance(context)
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_SENSOR_GET, GibIntentExtra.create().setId(constants.getIndoorTempId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_SENSOR_GET, GibIntentExtra.create().setId(constants.getOutdoorTempId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_SENSOR_GET, GibIntentExtra.create().setId(constants.getFuelLevelId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_SENSOR_GET, GibIntentExtra.create().setId(constants.getFuelPercentageId()));
//        LogsActivity.log(TAG, "Intents for GIB getting current temperatures are sent");
    }

    private void sendIntentsToListenGibChanges() {
        Constants.IGibConstants constants = GibCommunicationHandler.getInstance(context).getDeviceSpecificConstants();
        GibCommunicationHandler.getInstance(context)
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getCirculationId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getAcMaxId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getElectricDefrostId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getFrontDefrostId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getBehindDefrostId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getSteeringWheelId()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getSeatHeatId()).setArea(constants.getSeatAreaFrontLeft()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getSeatCoolId()).setArea(constants.getSeatAreaFrontLeft()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getSeatHeatId()).setArea(constants.getSeatAreaFrontRight()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getSeatCoolId()).setArea(constants.getSeatAreaFrontRight()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getSeatHeatId()).setArea(constants.getSeatAreaRearLeft()))
                .sendIntent(Constants.IGibConstants.INTENT_ACTION_PROPERTY_LISTEN, GibIntentExtra.create().setId(constants.getSeatHeatId()).setArea(constants.getSeatAreaRearRight()));
//        LogsActivity.log(TAG, "Intents for GIB listening are sent");
    }

    public void unregister() {
        if (isListening && context != null) {
            try {
                context.unregisterReceiver(receiver);
                isListening = false;
                LogsActivity.log(TAG, "BroadcastReceiver is unregistered");
            } catch (Exception e) {
                LogsActivity.log(TAG, "Error unregistering BroadcastReceiver", e);
            }
        }
    }

    public boolean isListening() {
        return isListening;
    }

    public void setIndoorTemp(float indoorTemp) {
        this.indoorTemp = indoorTemp;
        mainHandler.postDelayed(() -> WidgetService.getInstance().setGibIndicatorTemperatures(buildTemperatureString()), 2_000);
    }

    public void setOutdoorTemp(float outdoorTemp) {
        this.outdoorTemp = outdoorTemp;
//        WidgetService.getInstance().setGibIndicatorTemperatures(buildTemperatureString()); // setIndoorTemp() will update value on screen
    }

    public void setFuelLevel(float fuelLevel) { // TODO: Remove, since it is not in use on Atlas
        this.fuelLevel = fuelLevel;
//        WidgetService.getInstance().setGibIndicatorTemperatures(buildTemperatureString()); // setIndoorTemp() will update value on screen
    }

    public void setFuelPercent(float percentOfFilled) {
        this.fuelPercentOfFilled = percentOfFilled;
//        WidgetService.getInstance().setGibIndicatorTemperatures(buildTemperatureString()); // setIndoorTemp() will update value on screen
    }

    private String buildTemperatureString() {
        float gasFilled = fuelPercentOfFilled * 0.49f + 5;
        float gasEmpty = GibCommunicationHandler.getInstance(context).getDeviceSpecificConstants().getFuelTankVolume() - gasFilled;
        return String.format(Locale.getDefault(), "%.1fL %.1f°C", gasEmpty, outdoorTemp) +
                "\n" +
                String.format(Locale.getDefault(), "%.1fL %.1f°C", gasFilled, indoorTemp);
    }
}