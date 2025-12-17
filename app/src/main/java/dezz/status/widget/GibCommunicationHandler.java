package dezz.status.widget;

import static dezz.status.widget.Constants.ATLAS;
import static dezz.status.widget.Constants.DEVICE_TYPE_ATLAS;
import static dezz.status.widget.Helpers.parseAsFloat;
import static dezz.status.widget.Helpers.parseAsInt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class GibCommunicationHandler {

    private final static String TAG = "GibCommunicationHandler";
    private final Context context; // Application context, not Activity context! It is used to send broadcasts to Gib app.
    private final Preferences prefs;
    private Constants.IGibConstants deviceSpecificConstants;
    
    public Constants.IGibConstants getDeviceSpecificConstants() {
        return deviceSpecificConstants;
    }
    
    private static GibCommunicationHandler instance;
    public static GibCommunicationHandler getInstance(Context context) {
        if (instance == null) {
            instance = new GibCommunicationHandler(context.getApplicationContext());
            updateConstants();
        }
        return instance;
    }

    private GibCommunicationHandler(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = Preferences.getInstance(context);
    }

    // Use it to set constants according to device type
    private static void updateConstants() {
        if (instance != null) {
            switch (instance.prefs.deviceType.get()) {
                case DEVICE_TYPE_ATLAS -> instance.deviceSpecificConstants = ATLAS;
                default -> instance.deviceSpecificConstants = null; // Could lead to NullPointerException
            }
        }
    }

    protected GibCommunicationHandler sendIntent(String action, GibIntentExtra extra) {
        Intent intent = new Intent();
        intent.setPackage(Constants.IGibConstants.PACKAGE_NAME);
        intent.setAction(action);
        if (extra != null) {
            if (extra.getId() != null) {
                intent.putExtra("id", String.valueOf(extra.getId()));
            }
            if (extra.getArea() != null) {
                intent.putExtra("area", String.valueOf(extra.getArea()));
            }
            if (extra.getValue() != null) {
                intent.putExtra("value", String.valueOf(extra.getValue()));
            }
        }
        context.sendBroadcast(intent);
//        LogsActivity.log(TAG, "sendIntent: action = " + action + ", extra = " + extra);
        return this;
    }

    protected void getIntent(Intent intent) {
        if (intent != null) {

            StringBuilder extrasLog = new StringBuilder();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    Object value = extras.get(key);
                    extrasLog.append("    ").append(key).append(" = ").append(value != null ? value : "null")
                            .append(" [").append(value != null ? value.getClass().getSimpleName() : "null").append("]\n");
                }
            } else {
                extrasLog.append("null");
            }

            LogsActivity.log(TAG, "getIntent: action: " + intent.getAction()
//                    + ", data: " + intent.getData() // It is null anyway
//                    + ", package: " + intent.getPackage()
                    + ", extras: {\n" + extrasLog.toString() + "}"
            );


            Object idObj = intent.getExtras() != null ? intent.getExtras().get("id") : null;
            int id = parseAsInt(idObj, -1);
            Object valueObj = intent.getExtras() != null ? intent.getExtras().get("value") : null;
            int value = parseAsInt(valueObj, -999);
            Object areaObj = intent.getExtras() != null ? intent.getExtras().get("area") : null;
            int area = parseAsInt(areaObj, -1);

            if (id != -1 && value != -999) {
                if (id == deviceSpecificConstants.getCirculationId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        LogsActivity.log(TAG, "Air circulation: off");
                    } else if (value == deviceSpecificConstants.getCirculationInner()) {
                        LogsActivity.log(TAG, "Air circulation: inner air");
                        WidgetService.getInstance().setGibCycleStatus(WidgetService.GibCycleState.INNER);
                    } else if (value == deviceSpecificConstants.getCirculationOutside()) {
                        LogsActivity.log(TAG, "Air circulation: outer air");
                        WidgetService.getInstance().setGibCycleStatus(WidgetService.GibCycleState.OUTER);
                    } else if (value == deviceSpecificConstants.getCirculationAuto()) {
                        LogsActivity.log(TAG, "Air circulation: auto");
                    } else {
                        LogsActivity.log(TAG, "Air circulation: unknown value = " + value);
                    }
                } else if (id == deviceSpecificConstants.getAcMaxId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        LogsActivity.log(TAG, "A/C max: Off");
                        WidgetService.getInstance().setGibAcMaxStatus(WidgetService.GibPlainState.OFF);
                    } else if (value == Constants.IGibConstants.VALUE_ON) {
                        LogsActivity.log(TAG, "A/C max: On");
                        WidgetService.getInstance().setGibAcMaxStatus(WidgetService.GibPlainState.ON);
                    } else {
                        LogsActivity.log(TAG, "A/C max: unknown value = " + value);
                    }
                } else if (id == deviceSpecificConstants.getElectricDefrostId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        LogsActivity.log(TAG, "Electric defrost: Off");
                        WidgetService.getInstance().setGibElectricDefrostStatus(WidgetService.GibPlainState.OFF);
                    } else if (value == Constants.IGibConstants.VALUE_ON) {
                        LogsActivity.log(TAG, "Electric defrost: On");
                        WidgetService.getInstance().setGibElectricDefrostStatus(WidgetService.GibPlainState.ON);
                    } else {
                        LogsActivity.log(TAG, "Electric defrost: unknown value = " + value);
                    }
                } else if (id == deviceSpecificConstants.getFrontDefrostId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        LogsActivity.log(TAG, "Front defrost: Off");
                        WidgetService.getInstance().setGibFrontDefrostStatus(WidgetService.GibPlainState.OFF);
                    } else if (value == Constants.IGibConstants.VALUE_ON) {
                        LogsActivity.log(TAG, "Front defrost: On");
                        WidgetService.getInstance().setGibFrontDefrostStatus(WidgetService.GibPlainState.ON);
                    } else {
                        LogsActivity.log(TAG, "Front defrost: unknown value = " + value);
                    }
                } else if (id == deviceSpecificConstants.getBehindDefrostId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        LogsActivity.log(TAG, "Rear defrost: Off");
                        WidgetService.getInstance().setGibBehindDefrostStatus(WidgetService.GibPlainState.OFF);
                    } else if (value == Constants.IGibConstants.VALUE_ON) {
                        LogsActivity.log(TAG, "Rear defrost: On");
                        WidgetService.getInstance().setGibBehindDefrostStatus(WidgetService.GibPlainState.ON);
                    } else {
                        LogsActivity.log(TAG, "Rear defrost: unknown value = " + value);
                    }
                } else if (id == deviceSpecificConstants.getSteeringWheelId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        LogsActivity.log(TAG, "Steering wheel heat: 0");
                        WidgetService.getInstance().setGibIconsSteeringWheel(WidgetService.GibSteeringWheelState.OFF);
                    } else if (value == deviceSpecificConstants.getSteeringWheelHeat1()) {
                        LogsActivity.log(TAG, "Steering wheel heat: 1");
                        WidgetService.getInstance().setGibIconsSteeringWheel(WidgetService.GibSteeringWheelState.HEAT1);
                    } else if (value == deviceSpecificConstants.getSteeringWheelHeat2()) {
                        LogsActivity.log(TAG, "Steering wheel heat: 2");
                        WidgetService.getInstance().setGibIconsSteeringWheel(WidgetService.GibSteeringWheelState.HEAT2);
                    } else if (value == deviceSpecificConstants.getSteeringWheelHeat3()) {
                        LogsActivity.log(TAG, "Steering wheel heat: 3");
                        WidgetService.getInstance().setGibIconsSteeringWheel(WidgetService.GibSteeringWheelState.HEAT3);
                    } else {
                        LogsActivity.log(TAG, "Steering wheel heat: unknown value = " + value);
                    }
                } else if (id == deviceSpecificConstants.getSeatCoolId()) {
                    if (deviceSpecificConstants.getSeatAreaFrontLeft() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            LogsActivity.log(TAG, "Seat cool driver: 0");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.OFF);
                        } else if (value == deviceSpecificConstants.getSeatCool1()) {
                            LogsActivity.log(TAG, "Seat cool driver: 1");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.COOL1);
                        } else if (value == deviceSpecificConstants.getSeatCool2()) {
                            LogsActivity.log(TAG, "Seat cool driver: 2");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.COOL2);
                        } else if (value == deviceSpecificConstants.getSeatCool3()) {
                            LogsActivity.log(TAG, "Seat cool driver: 3");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.COOL3);
                        } else {
                            LogsActivity.log(TAG, "Seat cool driver: unknown value = " + value);
                        }
                    } else if (deviceSpecificConstants.getSeatAreaFrontRight() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            LogsActivity.log(TAG, "Seat cool passenger: 0");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.OFF);
                        } else if (value == deviceSpecificConstants.getSeatCool1()) {
                            LogsActivity.log(TAG, "Seat cool passenger: 1");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.COOL1);
                        } else if (value == deviceSpecificConstants.getSeatCool2()) {
                            LogsActivity.log(TAG, "Seat cool passenger: 2");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.COOL2);
                        } else if (value == deviceSpecificConstants.getSeatCool3()) {
                            LogsActivity.log(TAG, "Seat cool passenger: 3");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.COOL3);
                        } else {
                            LogsActivity.log(TAG, "Seat cool passenger: unknown value = " + value);
                        }
                    } else {
                        LogsActivity.log(TAG, "Seat cool passenger: unknown area = " + area);
                    }
                } else if (id == deviceSpecificConstants.getSeatHeatId()) {
                    if (deviceSpecificConstants.getSeatAreaFrontLeft() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            LogsActivity.log(TAG, "Seat heat driver: 0");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.OFF);
                        } else if (value == deviceSpecificConstants.getSeatHeat1()) {
                            LogsActivity.log(TAG, "Seat heat driver: 1");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.HEAT1);
                        } else if (value == deviceSpecificConstants.getSeatHeat2()) {
                            LogsActivity.log(TAG, "Seat heat driver: 2");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.HEAT2);
                        } else if (value == deviceSpecificConstants.getSeatHeat3()) {
                            LogsActivity.log(TAG, "Seat heat driver: 3");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.HEAT3);
                        } else {
                            LogsActivity.log(TAG, "Seat heat driver: unknown value = " + value);
                        }
                    } else if (deviceSpecificConstants.getSeatAreaFrontRight() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            LogsActivity.log(TAG, "Seat heat passenger: 0");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.OFF);
                        } else if (value == deviceSpecificConstants.getSeatHeat1()) {
                            LogsActivity.log(TAG, "Seat heat passenger: 1");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.HEAT1);
                        } else if (value == deviceSpecificConstants.getSeatHeat2()) {
                            LogsActivity.log(TAG, "Seat heat passenger: 2");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.HEAT2);
                        } else if (value == deviceSpecificConstants.getSeatHeat3()) {
                            LogsActivity.log(TAG, "Seat heat passenger: 3");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.HEAT3);
                        } else {
                            LogsActivity.log(TAG, "Seat heat passenger: unknown value = " + value);
                        }
                    } else if (deviceSpecificConstants.getSeatAreaRearLeft() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            LogsActivity.log(TAG, "Seat heat rear left: 0");
                            WidgetService.getInstance().setGibIconsSeatRearLeft(WidgetService.GibSeatState.OFF);
                        } else if (value == deviceSpecificConstants.getSeatHeat1()) {
                            LogsActivity.log(TAG, "Seat heat rear left: 1");
                            WidgetService.getInstance().setGibIconsSeatRearLeft(WidgetService.GibSeatState.HEAT1);
                        } else if (value == deviceSpecificConstants.getSeatHeat2()) {
                            LogsActivity.log(TAG, "Seat heat rear left: 2");
                            WidgetService.getInstance().setGibIconsSeatRearLeft(WidgetService.GibSeatState.HEAT2);
                        } else if (value == deviceSpecificConstants.getSeatHeat3()) {
                            LogsActivity.log(TAG, "Seat heat rear left: 3");
                            WidgetService.getInstance().setGibIconsSeatRearLeft(WidgetService.GibSeatState.HEAT3);
                        } else {
                            LogsActivity.log(TAG, "Seat heat rear left: unknown value = " + value);
                        }
                    } else if (deviceSpecificConstants.getSeatAreaRearRight() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            LogsActivity.log(TAG, "Seat heat rear right: 0");
                            WidgetService.getInstance().setGibIconsSeatRearRight(WidgetService.GibSeatState.OFF);
                        } else if (value == deviceSpecificConstants.getSeatHeat1()) {
                            LogsActivity.log(TAG, "Seat heat rear right: 1");
                            WidgetService.getInstance().setGibIconsSeatRearRight(WidgetService.GibSeatState.HEAT1);
                        } else if (value == deviceSpecificConstants.getSeatHeat2()) {
                            LogsActivity.log(TAG, "Seat heat rear right: 2");
                            WidgetService.getInstance().setGibIconsSeatRearRight(WidgetService.GibSeatState.HEAT2);
                        } else if (value == deviceSpecificConstants.getSeatHeat3()) {
                            LogsActivity.log(TAG, "Seat heat rear right: 3");
                            WidgetService.getInstance().setGibIconsSeatRearRight(WidgetService.GibSeatState.HEAT3);
                        } else {
                            LogsActivity.log(TAG, "Seat heat rear right: unknown value = " + value);
                        }
                    } else {
                        LogsActivity.log(TAG, "Seat heat: unknown area = " + area);
                    }
                } else if (id == deviceSpecificConstants.getIndoorTempId()) {
                    GibManager.getInstance(context).setIndoorTemp(parseAsFloat(valueObj, -99.9f));
                } else if (id == deviceSpecificConstants.getOutdoorTempId()) {
                    GibManager.getInstance(context).setOutdoorTemp(parseAsFloat(valueObj, -99.9f));
                } else {
                    LogsActivity.log(TAG, "Unknown: ID = " + id);
                }
            }
        } else {
            LogsActivity.log(TAG, "GIB intent is null");
        }
    }
}
