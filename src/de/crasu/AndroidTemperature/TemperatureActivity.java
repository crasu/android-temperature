package de.crasu.AndroidTemperature;

import android.app.Activity;
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
    private Handler timerHandler;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temperatureactivity);

        Button updateButton = (Button) findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndUpdate();
            }
        });

        timerHandler = new Handler();
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                saveAndUpdate();
                timerHandler.postDelayed(this, DELAY);
            }
        }, DELAY);

        saveAndUpdate();
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
