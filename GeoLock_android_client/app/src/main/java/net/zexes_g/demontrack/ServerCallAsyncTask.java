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

class ServerCallAsyncTask extends AsyncTask<String, Void, String> {

      ///////////////
     // Attribute //
    ///////////////

    /* Http Client */
    private final OkHttpClient client = new OkHttpClient();
    //----------------------------------------------------------------------------------------------


      ////////////////////////
     // AsyncTask Methodes //
    ////////////////////////

    /* Task Objectif */
    @Override
    protected String doInBackground(String... data) {
        return /*res.body*/sendData(data[0], data[1], data[2], data[3], data[4], data[5], data[6]);
    }

    /* TasK Responce */
    @Override
    protected void onPostExecute(String response) {
        String res = response;
        //Json pars(res)
        /*parsing*/
        Log.d("networking"," RESPONCE SERVER " +  response);
    }
    //----------------------------------------------------------------------------------------------


      //////////////
     // Methodes //
    //////////////

    /* Send Data to the Server */
    public String sendData(String imei, String provider, String lat, String lon, String alt, String sat, String calender){

        try {
            /* Create body */
            RequestBody body = new FormBody.Builder()
                    .add("imei", imei)
                    .add("provider", provider)
                    .add("lat_lon", lat)
                    .add("lat_lon", lon)
                    .add("alt", alt)
                    .add("sat", sat)
                    .add("calender", calender)
                    .build();

            /* Create request */
            Request request = new Request.Builder()
                    .url("http://geomeet.ngrok.io/Android/Client_data")
                    .post(body)
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
    //----------------------------------------------------------------------------------------------
}