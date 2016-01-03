package de.crasu.AndroidTemperature;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TemperatureActivity extends Activity
{
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private ProgressDialog progressDialog;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            TextView mainTextView = (TextView) findViewById(R.id.mainTextView);

            Integer type = intent.getIntExtra("type", TemperatureSaveService.SAVE_STOP);

            if(type == TemperatureSaveService.SAVE_START) {
                mainTextView.append("Speichern gestartet\n");
                progressDialog = ProgressDialog.show(getActivity(), "", "Daten werden übertragen", true);
            } else {
                Boolean result = intent.getBooleanExtra("result", false);
                Integer temperature = intent.getIntExtra("temperature", -100);
                CharSequence text = mainTextView.getText();
                mainTextView.setText(text.subSequence(Math.max(text.length() - 150, 0), text.length()));
                mainTextView.append(String.format("Aktuelle Temperatur: %d\n", temperature, result ? "" : " Fehler beim Speichern"));
                progressDialog.dismiss();

                Log.i("AndroidTemperature", String.format("saved temperature is %d", temperature));
            }
        }
    };

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

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(TemperatureSaveService.class.getName()));

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

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    protected TemperatureActivity getActivity() {
        return TemperatureActivity.this;
    }
}
