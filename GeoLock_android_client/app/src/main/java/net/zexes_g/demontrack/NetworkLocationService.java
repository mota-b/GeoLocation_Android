package net.zexes_g.demontrack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;

public class NetworkLocationService extends Service implements LocationListener {

      ///////////////
     // Attribute //
    ///////////////

    /* Refresh params */
    private final int TIME_REFRESH = 1000 * 3; /* 1000 ms = 1s */
    private final int DISTANCE_REFRESH = 0;    /* 0 m */

    /* NETWORK_MANAGER */
    private LocationManager network_locationManager;
    private final String NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;

    /* Data */
    String data;
    String IMEI, lat, lon, alt, calender;

    /* Socket io */
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://geomeet.ngrok.io");
        } catch (URISyntaxException e) {}
    }
    //----------------------------------------------------------------------------------------------

      ////////////////////////
     // Service Life Cycle //
    ////////////////////////

    /* Create Service */
    @Override
    public void onCreate() {

        Log.d("AABBCC","Service Create " + NETWORK_PROVIDER);
        /* Initialise components */
        init();
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("AABBCC","Service Start command " + NETWORK_PROVIDER);
        /* Activate the listeners */
        network_locationManager.requestLocationUpdates(NETWORK_PROVIDER,TIME_REFRESH,DISTANCE_REFRESH,this);

        return startId;
    }

    /* When a client Rebinf to the service */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    /* When a client bind to the service */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* when all clients unbind from the service */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /* Service is about to be destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AABBCC","Service DESTROTY " + NETWORK_PROVIDER);
        /* Desactivate the listener if is activated */
        if (network_locationManager != null){
            network_locationManager.removeUpdates(this);
        }
    }
    //----------------------------------------------------------------------------------------------


      ///////////////
     // Listeners //
    ///////////////

    /* Location listener */
    @Override
    public void onLocationChanged(Location location) {

        /* Get data */
        lat = "" + location.getLatitude();
        lon = "" + location.getLongitude();
        alt = "" + location.getAltitude();
        calender = "" + android.text.format.DateFormat.format("EEEE;d/M/yyyy;H:m:s ",new Date());

        /* Send data */
        JSONObject server_data = new JSONObject();
        try {
            server_data.accumulate("imei",IMEI);
            server_data.accumulate("provider",NETWORK_PROVIDER);
            server_data.accumulate("latLon",lat);
            server_data.accumulate("latLon",lon);
            server_data.accumulate("alt","none");
            server_data.accumulate("satel","none");
            server_data.accumulate("calender",calender);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        data = lat + "\n" + lon + "\n" + alt;
        Log.d("AABBCC",location.getProvider() + " : (lat, lon, alt) : \n" + data);
        mSocket.emit("client_data",server_data);
        //new ServerCallAsyncTask().execute(IMEI, NETWORK_PROVIDER, lat, lon, "none", "none", calender);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    //----------------------------------------------------------------------------------------------


      //////////////
     // Methodes //
    //////////////

    /* Initialise the components */
    private void init() {

        /* Get the IMEI */
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = "" + telephonyManager.getDeviceId();

        /* Initialise the managers */
        network_locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        /* Socket */
        socket_callBacks();
        mSocket.connect();
    }

    /* Socket Events */
    private void socket_callBacks() {

        /* Event declaration */
        Emitter.Listener onGet_something = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                JSONObject data = (JSONObject) args[0];
                //String username;
                String data0;
                try {
                    data0 = data.getString("data0_name");
                } catch (JSONException e) {
                    return;
                }
                // use the data0 some how :p
            }

        /* Seth event on the stack */
            //mSocket.on("message", onGet_something);
        };
    }
    //----------------------------------------------------------------------------------------------
}