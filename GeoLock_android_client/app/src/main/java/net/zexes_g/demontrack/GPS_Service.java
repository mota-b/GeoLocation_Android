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
import android.util.Log;

import java.util.Iterator;

/**
 * Created by zexes-g on 06/04/17.
 */

public class GPS_Service extends Service implements LocationListener ,GpsStatus.Listener{



    // Attribute


    /* Services */
    private LocationManager gps_locationManager;
    private final String gps_provider = LocationManager.GPS_PROVIDER;
    private final int gps_refresh_TIME = 1000 * 3; /* 1000 ms = 1s */
    private final int gps_refresh_DISTANCE = 0;    /* 0 m */

    /* Intents */
    Intent location_update ;
    Intent location_status ;



    //----------------------



    // Service cycle


    /* Create Service */


    @Override
    public void onCreate() {

        /* Initialise components */
        init();

    }


    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /* Activate the listener */
        gps_locationManager.requestLocationUpdates(gps_provider,gps_refresh_TIME,gps_refresh_DISTANCE,this);
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

        /* Desactivate the listener if is activated */
        if (gps_locationManager != null){
            gps_locationManager.removeUpdates(this);
        }
    }


    //-----------------------------------------------------------



    // Location listener


    @Override
    public void onLocationChanged(Location location) {

        /* Update and send Location */
        location_update.putExtra("lat",location.getLatitude());
        location_update.putExtra("lon",location.getLongitude());
        location_update.putExtra("alt",location.getAltitude());

        Log.d("MAX","ALL_location : "+ location.getTime() );
        sendBroadcast(location_update);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("MAX","provider : "+provider+", status : "+status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        /* Send the changed state ON */
        location_status.putExtra("status","on");
        sendBroadcast(location_status);
    }

    @Override
    public void onProviderDisabled(String provider) {
        /* Send the changed state OFF */
        location_status.putExtra("status","off");
        sendBroadcast(location_status);

    }


    //------------------------------------------------------------------------------



    // Methodes


    /* Initialise the components */
    private void init() {

        /* Initialise the manager */
        gps_locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        gps_locationManager.addGpsStatusListener(this);
        /* Initialise intents */
        location_update = new Intent("location_update");
        location_status = new Intent("location_status");
    }

    @Override
    public void onGpsStatusChanged(int event) {
        GpsStatus gpsStatus = gps_locationManager.getGpsStatus(null);

        /* If gps is on */
        if(gpsStatus != null) {

            /* From Gps satellite get Iterable Satellites Objects */
            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();

            /* Fhe satellites list */
            String lSatellites = null;

            /* Counters */
            int nb_sat_found = 0;
            int nb_sat_used = 0;
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                lSatellites = "Satellite : ";
                if(satellite.usedInFix())
                    lSatellites += (nb_sat_used++);
                else
                    lSatellites += (nb_sat_used);
                lSatellites += "/"+(nb_sat_found++) ;

            }
            location_update.putExtra("sat",lSatellites+"");
            Log.d("SATELLITE",lSatellites+"");
        }
    }


    //------------------------------------------------------------


}
