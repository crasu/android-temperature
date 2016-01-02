package de.crasu.AndroidTemperature;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by christian on 02.01.16.
 */
public class TemperatureSaveService  extends IntentService {
    public TemperatureSaveService() {
        super("TemperatureSaveService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TemperatureSaveService", "Intent received");
        TemperatureSaver.saveTemperature(getBatteryTemperature());
        Log.i("TemperatureSaveService", "Finished request");
        WakefulReceiver.completeWakefulIntent(intent);
    }

    private Integer getBatteryTemperature() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -100);
    }
}
