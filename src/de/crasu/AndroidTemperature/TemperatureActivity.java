package de.crasu.AndroidTemperature;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TemperatureActivity extends Activity
{
    public static final int DELAY = 3600*1000;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temperatureactivity);

        Button updateButton = (Button) findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(new Intent(getApplicationContext(), TemperatureSaveService.class));
            }
        });

        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(getApplicationContext(), WakefulReceiver.class), 0);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
    }

    @Override
    public void onDestroy() {
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
        super.onDestroy();
    }

    private void saveAndUpdate() {
        Integer temperature = getBatteryTemperature();

        new TemperatureSaveTask(getApplicationContext(), getActivity()).execute(temperature);
        updateView(temperature);
    }

    private void updateView(Integer temperature) {
        TextView mainTextView = (TextView) findViewById(R.id.mainTextView);

        CharSequence text = mainTextView.getText();
        mainTextView.setText(text.subSequence(Math.max(text.length() - 150, 0), text.length()));
        mainTextView.append("Aktuelle Temperatur: " + temperature + "\n");

        Log.i("AndroidTemperature", "saved temperature is " + temperature);
    }

    protected TemperatureActivity getActivity() {
        return TemperatureActivity.this;
    }

    private Integer getBatteryTemperature() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -100);
    }
}
