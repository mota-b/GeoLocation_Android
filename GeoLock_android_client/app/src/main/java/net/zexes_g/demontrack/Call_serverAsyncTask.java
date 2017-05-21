package net.zexes_g.demontrack;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zexes-g on 06/04/17.
 */

class Call_serverAsyncTask extends AsyncTask<String, Void, String> {


    private final OkHttpClient client = new OkHttpClient();

    public String sendData(String imei, String lat, String lon, String alt, String sat, String calender){

        try {
            /* Create body */
            RequestBody body = new FormBody.Builder()
                    .add("imei", imei)
                    .add("lat_lon", lat)
                    .add("lat_lon", lon)
                    .add("alt", alt)
                    .add("sat", sat)
                    .add("Calender", calender)
                    .build();

            /* Create request */
            Request request = new Request.Builder()
                    .url("http://geomeet.ngrok.io/Android")
                    //.post(body)
                    .build();

            /*
            *  Send ----> Request
            *  Get  ----> Response
            */
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected String doInBackground(String... data) {
        return /*res.body*/sendData(data[0],data[1],data[2],data[3],data[4],data[5]);

    }

    @Override
    protected void onPostExecute(String response) {
        String res = response;
        //Json pars(res)
        /*parsing*/
        Log.d("networking", response);
    }


}