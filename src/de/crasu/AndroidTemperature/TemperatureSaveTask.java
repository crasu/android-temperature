package de.crasu.AndroidTemperature;

/**
 * Created by christian on 31.12.15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;


public class TemperatureSaveTask extends AsyncTask<Integer, Void, Boolean> {
    Context context;
    private final ProgressDialog progressDialog;

    protected TemperatureSaveTask(Context context, Activity activity) {
        progressDialog = ProgressDialog.show(activity, "", "Daten werden übertragen", true);
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        return TemperatureSaveService.saveTemperature(integers[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.dismiss();

        if(!result) {
            //noinspection ResourceType
            Toast.makeText(context, "Konnte Temperatur nicht speichern", 4).show();
        }
    }
}
