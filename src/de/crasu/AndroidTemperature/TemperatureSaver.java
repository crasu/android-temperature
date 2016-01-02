package de.crasu.AndroidTemperature;

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

public class TemperatureSaver {
    public TemperatureSaver() {
    }

    public Boolean saveTemperature(Integer temperature) {
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