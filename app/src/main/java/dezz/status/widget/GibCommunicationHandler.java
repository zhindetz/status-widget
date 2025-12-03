package dezz.status.widget;

import static dezz.status.widget.Constants.ATLAS;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

public class GibCommunicationHandler {

    private final static String TAG = "IntentHandler";
    private final Constants.IGibConstants constants;
    
    public Constants.IGibConstants getConstants() {
        return constants;
    }
    
    private static GibCommunicationHandler instance;
    public static GibCommunicationHandler getInstance() {
        if (instance == null) {
            instance = new GibCommunicationHandler(ATLAS); // TODO: Получать тип (АТЛАС или другой) из настроек
            return instance;
        } else {
            return instance;
        }
    }

    private GibCommunicationHandler(Constants.IGibConstants constants) {
        this.constants = constants;
    }

    protected GibCommunicationHandler sendIntent(Context context, int id) {
        Intent intent = new Intent();
        intent.setAction(Constants.IGibConstants.INTENT_PROPERTY_REQUEST_ACTION);
        intent.putExtra("id", id);
        intent.setPackage(Constants.IGibConstants.PACKAGE_NAME);
        context.sendBroadcast(intent);
        Log.d(TAG, "sendIntent: " + id);
        return this;
    }

    protected GibCommunicationHandler sendIntent(Context context, int id, int area) {
        Intent intent = new Intent();
        intent.setAction(Constants.IGibConstants.INTENT_PROPERTY_REQUEST_ACTION);
        intent.putExtra("id", id);
        intent.putExtra("area", area);
        intent.setPackage(Constants.IGibConstants.PACKAGE_NAME);
        context.sendBroadcast(intent);
        Log.d(TAG, "sendIntent: " + id + " " + area);
        return this;
    }

    protected void getIntent(Context context, Intent intent) {
        Log.d(TAG, "getIntent: " + intent);
        if (intent != null) {// && Objects.equals(intent.getAction(), Constants.IGibConstants.INTENT_PROPERTY_RESULT_ACTION)) { // По идее в GibManager.registerReceiver() и так есть IntentFilter на INTENT_RESULT_ACTION
            int id = intent.getIntExtra("id", -1);
            int value = intent.getIntExtra("value", -1);
            int area = intent.getIntExtra("area", -1);
            if (id != -1 && value != -1) {
                if (id == constants.getCirculationId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        Toast.makeText(context, "Циркуляция: выключено", Toast.LENGTH_SHORT).show();
                    } else if (value == constants.getCirculationInner()) {
                        Log.d(TAG, "Циркуляция: внутренний воздух");
                        WidgetService.getInstance().setGibCycleStatus(WidgetService.GibCycleState.INNER);
                    } else if (value == constants.getCirculationOutside()) {
                        Log.d(TAG, "Циркуляция: внешний воздух");
                        WidgetService.getInstance().setGibCycleStatus(WidgetService.GibCycleState.OUTER);
                    } else if (value == constants.getCirculationAuto()) {
                        Toast.makeText(context, "Циркуляция: авто", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Циркуляция: неизвестно " + value, Toast.LENGTH_LONG).show();
                    }
                } else if (id == constants.getAcMaxId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        Log.d(TAG, "Макс. охлаждение: выключено");
                        WidgetService.getInstance().setGibAcMaxStatus(WidgetService.GibPlainState.OFF);
                    } else if (value == Constants.IGibConstants.VALUE_ON) {
                        Log.d(TAG, "Макс. охлаждение: включено");
                        WidgetService.getInstance().setGibAcMaxStatus(WidgetService.GibPlainState.ON);
                    } else {
                        Toast.makeText(context, "Макс. охлаждение: неизвестно " + value, Toast.LENGTH_LONG).show();
                    }
                } else if (id == constants.getElectricDefrostId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        Log.d(TAG, "Обогрев лобового: выключено");
                        WidgetService.getInstance().setGibElectricDefrostStatus(WidgetService.GibPlainState.OFF);
                    } else if (value == Constants.IGibConstants.VALUE_ON) {
                        Log.d(TAG, "Обогрев лобового: включено");
                        WidgetService.getInstance().setGibElectricDefrostStatus(WidgetService.GibPlainState.ON);
                    } else {
                        Toast.makeText(context, "Обогрев лобового: неизвестно " + value, Toast.LENGTH_LONG).show();
                    }
                } else if (id == constants.getFrontDefrostId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        Log.d(TAG, "Обдув лобового: выключено");
                        WidgetService.getInstance().setGibFrontDefrostStatus(WidgetService.GibPlainState.OFF);
                    } else if (value == Constants.IGibConstants.VALUE_ON) {
                        Log.d(TAG, "Обдув лобового: включено");
                        WidgetService.getInstance().setGibFrontDefrostStatus(WidgetService.GibPlainState.ON);
                    } else {
                        Toast.makeText(context, "Обдув лобового: неизвестно " + value, Toast.LENGTH_LONG).show();
                    }
                } else if (id == constants.getBehindDefrostId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        Log.d(TAG, "Обогрев заднего: выключено");
                        WidgetService.getInstance().setGibBehindDefrostStatus(WidgetService.GibPlainState.OFF);
                    } else if (value == Constants.IGibConstants.VALUE_ON) {
                        Log.d(TAG, "Обогрев заднего: включено");
                        WidgetService.getInstance().setGibBehindDefrostStatus(WidgetService.GibPlainState.ON);
                    } else {
                        Toast.makeText(context, "Обогрев заднего: неизвестно " + value, Toast.LENGTH_LONG).show();
                    }
                } else if (id == constants.getSteeringWheelId()) {
                    if (value == Constants.IGibConstants.VALUE_OFF) {
                        Log.d(TAG, "Обогрев руля: выключено");
                        WidgetService.getInstance().setGibIconsSteeringWheel(WidgetService.GibSteeringWheelState.OFF);
                    } else if (value == constants.getSteeringWheelHeat1()) {
                        Log.d(TAG, "Обогрев руля: 1");
                        WidgetService.getInstance().setGibIconsSteeringWheel(WidgetService.GibSteeringWheelState.HEAT1);
                    } else if (value == constants.getSteeringWheelHeat2()) {
                        Log.d(TAG, "Обогрев руля: 2");
                        WidgetService.getInstance().setGibIconsSteeringWheel(WidgetService.GibSteeringWheelState.HEAT2);
                    } else if (value == constants.getSteeringWheelHeat3()) {
                        Log.d(TAG, "Обогрев руля: 3");
                        WidgetService.getInstance().setGibIconsSteeringWheel(WidgetService.GibSteeringWheelState.HEAT3);
                    } else {
                        Toast.makeText(context, "Обогрев руля: неизвестно " + value, Toast.LENGTH_LONG).show();
                    }
                } else if (id == constants.getSeatCoolId()) {
                    if (constants.getSeatAreaFrontLeft() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            Log.d(TAG, "Вентиляция водителя: выключено");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.OFF);
                        } else if (value == constants.getSeatCool1()) {
                            Log.d(TAG, "Вентиляция водителя: 1");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.COOL1);
                        } else if (value == constants.getSeatCool2()) {
                            Log.d(TAG, "Вентиляция водителя: 2");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.COOL2);
                        } else if (value == constants.getSeatCool3()) {
                            Log.d(TAG, "Вентиляция водителя: 3");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.COOL3);
                        } else {
                            Toast.makeText(context, "Вентиляция водителя: неизвестно " + value, Toast.LENGTH_LONG).show();
                        }
                    } else if (constants.getSeatAreaFrontRight() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            Log.d(TAG, "Вентиляция пассажира: выключено");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.OFF);
                        } else if (value == constants.getSeatCool1()) {
                            Log.d(TAG, "Вентиляция пассажира: 1");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.COOL1);
                        } else if (value == constants.getSeatCool2()) {
                            Log.d(TAG, "Вентиляция пассажира: 2");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.COOL2);
                        } else if (value == constants.getSeatCool3()) {
                            Log.d(TAG, "Вентиляция пассажира: 3");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.COOL3);
                        } else {
                            Toast.makeText(context, "Вентиляция пассажира: неизвестно " + value, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, "Вентиляция сидений: неизвестно " + area, Toast.LENGTH_LONG).show();
                    }
                } else if (id == constants.getSeatHeatId()) {
                    if (constants.getSeatAreaFrontLeft() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            Log.d(TAG, "Обогрев водителя: выключено");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.OFF);
                        } else if (value == constants.getSeatHeat1()) {
                            Log.d(TAG, "Обогрев водителя: 1");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.HEAT1);
                        } else if (value == constants.getSeatHeat2()) {
                            Log.d(TAG, "Обогрев водителя: 2");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.HEAT2);
                        } else if (value == constants.getSeatHeat3()) {
                            Log.d(TAG, "Обогрев водителя: 3");
                            WidgetService.getInstance().setGibIconsSeatFrontLeft(WidgetService.GibSeatState.HEAT3);
                        } else {
                            Toast.makeText(context, "Обогрев водителя: неизвестно " + value, Toast.LENGTH_LONG).show();
                        }
                    } else if (constants.getSeatAreaFrontRight() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            Log.d(TAG, "Обогрев сиденья пассажира: выключено");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.OFF);
                        } else if (value == constants.getSeatHeat1()) {
                            Log.d(TAG, "Обогрев сиденья пассажира: 1");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.HEAT1);
                        } else if (value == constants.getSeatHeat2()) {
                            Log.d(TAG, "Обогрев сиденья пассажира: 2");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.HEAT2);
                        } else if (value == constants.getSeatHeat3()) {
                            Log.d(TAG, "Обогрев сиденья пассажира: 3");
                            WidgetService.getInstance().setGibIconsSeatFrontRight(WidgetService.GibSeatState.HEAT3);
                        } else {
                            Toast.makeText(context, "Сиденья пассажира: неизвестно " + value, Toast.LENGTH_LONG).show();
                        }
                    } else if (constants.getSeatAreaRearLeft() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            Log.d(TAG, "Обогрев сиденья зад лево: выключено");
                            WidgetService.getInstance().setGibIconsSeatRearLeft(WidgetService.GibSeatState.OFF);
                        } else if (value == constants.getSeatHeat1()) {
                            Log.d(TAG, "Обогрев сиденья зад лево: 1");
                            WidgetService.getInstance().setGibIconsSeatRearLeft(WidgetService.GibSeatState.HEAT1);
                        } else if (value == constants.getSeatHeat2()) {
                            Log.d(TAG, "Обогрев сиденья зад лево: 2");
                            WidgetService.getInstance().setGibIconsSeatRearLeft(WidgetService.GibSeatState.HEAT2);
                        } else if (value == constants.getSeatHeat3()) {
                            Log.d(TAG, "Обогрев сиденья зад лево: 3");
                            WidgetService.getInstance().setGibIconsSeatRearLeft(WidgetService.GibSeatState.HEAT3);
                        } else {
                            Toast.makeText(context, "Обогрев сиденья зад лево: неизвестно " + value, Toast.LENGTH_LONG).show();
                        }
                    } else if (constants.getSeatAreaRearRight() == area) {
                        if (value == Constants.IGibConstants.VALUE_OFF) {
                            Log.d(TAG, "Обогрев сиденья зад право: выключено");
                            WidgetService.getInstance().setGibIconsSeatRearRight(WidgetService.GibSeatState.OFF);
                        } else if (value == constants.getSeatHeat1()) {
                            Log.d(TAG, "Обогрев сиденья зад право: 1");
                            WidgetService.getInstance().setGibIconsSeatRearRight(WidgetService.GibSeatState.HEAT1);
                        } else if (value == constants.getSeatHeat2()) {
                            Log.d(TAG, "Обогрев сиденья зад право: 2");
                            WidgetService.getInstance().setGibIconsSeatRearRight(WidgetService.GibSeatState.HEAT2);
                        } else if (value == constants.getSeatHeat3()) {
                            Log.d(TAG, "Обогрев сиденья зад право: 3");
                            WidgetService.getInstance().setGibIconsSeatRearRight(WidgetService.GibSeatState.HEAT3);
                        } else {
                            Toast.makeText(context, "Обогрев сиденья зад право: неизвестно " + value, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(context, "Неизвестный GIB AREA: " + area, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Неизвестный GIB ID: " + id, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
