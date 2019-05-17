package net.zexes_g.demontrack;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;

import net.zexes_g.demontrack.ApplicationManager;
import net.zexes_g.demontrack.NetworkLocationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by zexes-g on 06/04/17.
 */

public class GpsLocationService extends Service implements LocationListener ,GpsStatus.Listener{

      ///////////////
     // Attribute //
    ///////////////

    /* Refresh params */
    private int TIME_REFRESH = 1000 * 3; /* 1000 ms = 1s */
    private int DISTANCE_REFRESH = 0;    /* 0 m */

    /* GPS_MANAGER */
    private LocationManager gps_locationManager;
    private String GPS_PROVIDER = LocationManager.GPS_PROVIDER;

    /* Data */
    private String IMEI, satel;

    /* Intents */
    private Intent networkLocationService;

    /* Static Socket from app */
    private ApplicationManager app;

    /* Notification */
    NotificationManager notificationManager;
    NotificationCompat.Builder notifyBuilder;
    //----------------------------------------------------------------------------------------------

      ////////////////////////
     // Service Life Cycle //
    ////////////////////////

    /* Create Service */
    @Override
    public void onCreate() {

        /* Initialise components */
        init();
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /* Activate the listeners */
        gps_locationManager.requestLocationUpdates(GPS_PROVIDER,TIME_REFRESH,DISTANCE_REFRESH,this);
        gps_locationManager.addGpsStatusListener(this);
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

        /* Remove the GPS location manager */
        if (gps_locationManager != null){
            gps_locationManager.removeUpdates(this);
        }

        /* Desactivate the socket */
        app.getSocket().disconnect();
        app.kill_events();

        /* UnNotify */
        notificationManager.cancel(1);
    }

    /* Restart service if OS stop it due to need memory */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
    }
    //----------------------------------------------------------------------------------------------


      ///////////////
     // Listeners //
    ///////////////

    /* Location listener */
    @Override
    public void onLocationChanged(Location location) {

        /* Try to reconnect when server crash */
        if(!app.getSocket().connected())
            app.getSocket().connect();

        /* Check Network location service state */
        if (networkLocationService != null){
            stopService(networkLocationService);
            networkLocationService = null;
        }

        /* Get data */
        String calender = "" + android.text.format.DateFormat.format("EEEE;d/M/yyyy;H:m:s ",new Date());

        /* Send data */
        JSONObject server_data = new JSONObject();
        try {
            server_data.accumulate("imei", IMEI);
            server_data.accumulate("model", Build.MANUFACTURER);
            server_data.accumulate("model", Build.MODEL);
            server_data.accumulate("provider", GPS_PROVIDER);
            server_data.accumulate("latLon", "" + location.getLatitude());
            server_data.accumulate("latLon", "" + location.getLongitude());
            server_data.accumulate("alt", "" + location.getAltitude());
            server_data.accumulate("satel", satel);
            server_data.accumulate("calender", calender);

            if(!satel.equals(""))
                app.getSocket().emit("client_data",server_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
        gps_locationManager.requestLocationUpdates(GPS_PROVIDER,TIME_REFRESH,DISTANCE_REFRESH,this);
        gps_locationManager.addGpsStatusListener(this);

        /* Reacticate socket */
        app.init_events();
        app.getSocket().connect();

        /* update notification */
        notifyBuilder.setContentText("Sending Location ...");
        notificationManager.notify(1, notifyBuilder.build());
    }
    @Override
    public void onProviderDisabled(String provider) {

        if (gps_locationManager != null){
            gps_locationManager.removeGpsStatusListener(this);
        }

        /* Desactivate socket */
        app.getSocket().disconnect();
        app.kill_events();

        /* Update notification */
        notifyBuilder.setContentText("GPS or Network disabled. In Stand-by ...");
        notificationManager.notify(1, notifyBuilder.build());
    }

    /* Gps status listener */
    @Override
    public void onGpsStatusChanged(int event) {
        GpsStatus gpsStatus = gps_locationManager.getGpsStatus(null);

        /* If gps is on */
        if(gpsStatus != null) {

            /* From Gps satellite get Iterable Satellites Objects */
            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();

            /* The satellites list */
            String lSatellites = null;

            /* Counters */
            int nb_sat_found = 0;
            int nb_sat_used = 0;

            /* Iterate on satellites */
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                lSatellites = "";
                if(satellite.usedInFix())
                    lSatellites += (nb_sat_used++);
                else
                    lSatellites += (nb_sat_used);
                lSatellites += "/"+(nb_sat_found++) ;
            }

            /* Use the NetworkLocation service if no satellite used to fix location*/
            if(nb_sat_used == 0 && networkLocationService == null){
                networkLocationService = new Intent(this, NetworkLocationService.class);
                startService(networkLocationService);
            }
            if(nb_sat_used != 0)
                satel = "" + lSatellites;
            else
                satel = "";

        }
    }
    //----------------------------------------------------------------------------------------------


      //////////////
     // Methodes //
    //////////////

    /* Initialise the components */
    private void init() {

        /* Get an instance for the Application */
        app = (ApplicationManager) this.getApplication();

        /* Get the IMEI */
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = "" + telephonyManager.getDeviceId();

        /* Set the network Location Service intent */
        networkLocationService = null;

        /* Initialise the managers */
        gps_locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        /* Acticate Socket */
        if (gps_locationManager.isProviderEnabled(GPS_PROVIDER)){
            app.init_events();
            app.getSocket().connect();
        }

        /* Init & Pop notification */
        init_notification();
        pop_notification();
    }

    /* Initialise the notification */
    private void init_notification() {

        /* Set the intent */
        final Intent emptyIntent = new Intent(); // used to handle exeption in PendingIntent.getActivity
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 1, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Build notification */
        notifyBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("Tracker On")
                        .setContentText("Sending Location ...")
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
    }

    /* Pop the notification */
    private void pop_notification() {
        /* Activate Notification */
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notifyBuilder.build());
    }
    //----------------------------------------------------------------------------------------------
}
