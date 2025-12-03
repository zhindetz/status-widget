package dezz.status.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class GibManager {
    private static final String TAG = "GibManager";
    private static GibManager instance;
    private boolean isListening = false;
    private final Context context;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GibCommunicationHandler.getInstance().getIntent(context, intent);
        }
    };

    private GibManager(Context context) {
        this.context = context.getApplicationContext(); // Сохраняем Application Context
        registerReceiver();
    }

    public static synchronized GibManager getInstance(Context context) {
        if (instance == null) {
            instance = new GibManager(context);
        }
        if (!instance.isListening()) instance.registerReceiver();
        return instance;
    }

    private void registerReceiver() {
        if (!isListening) {
            Constants.IGibConstants constants = GibCommunicationHandler.getInstance().getConstants();
            IntentFilter filter = new IntentFilter(Constants.IGibConstants.INTENT_PROPERTY_RESULT_ACTION);
            try {
                context.registerReceiver(receiver, filter);

                isListening = true;
                Log.d(TAG, "BroadcastReceiver успешно зарегистрирован");

                // Отправляем запрос на подписку изменения значений
                GibCommunicationHandler.getInstance()
                        .sendIntent(context, GibCommunicationHandler.getInstance().getConstants().getCirculationId())
                        .sendIntent(context, constants.getAcMaxId())
                        .sendIntent(context, constants.getElectricDefrostId())
                        .sendIntent(context, constants.getFrontDefrostId())
                        .sendIntent(context, constants.getBehindDefrostId())
                        .sendIntent(context, constants.getSteeringWheelId())
                        .sendIntent(context, constants.getSeatHeatId(), constants.getSeatAreaFrontLeft())
                        .sendIntent(context, constants.getSeatCoolId(), constants.getSeatAreaFrontLeft())
                        .sendIntent(context, constants.getSeatHeatId(), constants.getSeatAreaFrontRight())
                        .sendIntent(context, constants.getSeatCoolId(), constants.getSeatAreaFrontRight())
                        .sendIntent(context, constants.getSeatHeatId(), constants.getSeatAreaRearLeft())
                        .sendIntent(context, constants.getSeatHeatId(), constants.getSeatAreaRearRight());
                Log.d(TAG, "Запросы на подписку отправлены");
                // TODO Отправить запрос на текущие состояния ГИБов и обновить иконки
            } catch (Exception e) {
                Toast.makeText(context, "Ошибка при регистрации BroadcastReceiver", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Ошибка при регистрации BroadcastReceiver", e);
            }
        }
    }

    public void unregister() {
        if (isListening && context != null) {
            try {
                context.unregisterReceiver(receiver);
                isListening = false;
                Log.d(TAG, "BroadcastReceiver успешно отключен");
            } catch (Exception e) {
                Toast.makeText(context, "Ошибка при отключении от BroadcastReceiver", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Ошибка при отключении от BroadcastReceiver", e);
            }
        }
    }

    public boolean isListening() {
        return isListening;
    }
}