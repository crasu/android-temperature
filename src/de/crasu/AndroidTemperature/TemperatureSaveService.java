package de.crasu.AndroidTemperature;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christian on 02.01.16.
 */
public class TemperatureSaveService  extends IntentService {
    public static final int SAVE_START = 0;
    public static final int SAVE_STOP = 1;

    public TemperatureSaveService() {
        super("TemperatureSaveService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TemperatureSaveService", "Intent received");
        sendSaveStartToActivity();
        Integer temperature = getBatteryTemperature();
        Boolean result = this.saveTemperature(temperature);
        Log.i("TemperatureSaveService", "Finished request");
        sendSaveStopToActivity(temperature, result);
        WakefulReceiver.completeWakefulIntent(intent);
    }

    private void sendSaveStartToActivity() {
        Intent intent = new Intent(TemperatureSaveService.class.getName());
        intent.putExtra("type", this.SAVE_START);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendSaveStopToActivity(Integer temperature, Boolean result) {
        Intent intent = new Intent(TemperatureSaveService.class.getName());
        intent.putExtra("type", this.SAVE_STOP);
        intent.putExtra("result", result);
        intent.putExtra("temperature", temperature);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private Integer getBatteryTemperature() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -100);
    }

    public static Boolean saveTemperature(Integer temperature) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("http://android-temperature.appspot.com/temperature");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("temperature", temperature.toString()));
            request.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse response = client.execute(request);

            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != 200) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
