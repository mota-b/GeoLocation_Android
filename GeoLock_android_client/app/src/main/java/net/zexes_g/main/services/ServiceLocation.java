package net.zexes_g.main.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.zexes_g.demontrack.ApplicationManager;
import net.zexes_g.demontrack.R;
import net.zexes_g.main.activities.ActivityMain;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;

public class ServiceLocation extends Service {

    /**
     * Attributes
     */
    /* Application manager */
    ApplicationManager app;

    /* Notification */
    NotificationManager notificationManager;
    NotificationCompat.Builder notifyBuilder;
    String CHANNEL_ID = "C28";

    /* Location */
    LocationManager gps_locationManager;
    LocationManager network_locationManager;

    LocationListener locationListener;
    LocationListener network_locationListener;

    String GPS_PROVIDER = LocationManager.GPS_PROVIDER;
    String NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;

    int GPS_REFRESH_TIME = 1000 * 3; /* 1000 ms = (X) s */
    int GPS_REFRESH_DISTANCE = 0;    /* 0 m */
    GpsStatus.Listener gpsStatusListener;
    String satellites;

    /* User data */
    String IMEI;


    // NOTE
    //* location disabled ==> GpS && Network providers are disabled
    //* location enabled ==> get Network location (one time )THEN searsh for gps
    //* Network location ==> can be from (cell towers,  internet or MAC ) so the cahnges wont be at same rate as GPS
    //* From any provider ==> if no changes in the location MEANS no update

    /**
     * Lyfe Cycle
     */
    // Create Service
    @Override
    public void onCreate() {
        /* Initialise components */
        initialise_components();
    }

    // The service is starting, due to a call to startService()
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /* Activate the listeners*/
        //Location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // Do something with the location
                useLocation(location);
//                    if (location.getProvider() == "gps"){
//                        System.out.println("\n\n !! GPS location !!"+
//                                "\nlat = "+location.getLatitude()+
//                                "\nlon = "+location.getLongitude()+
//                                "\nprovider = "+location.getProvider()+
//                                "\nsatellites = "+satellites+
//                                "\ndate= "+ android.text.format.DateFormat.format("EEEE;d/M/yyyy;H:m:s ",new Date())
//                        );
//                    }
//                    else{
//                        System.out.println("\n\n !! NETWORK location !!"+
//                                "\nlat = "+location.getLatitude()+
//                                "\nlon = "+location.getLongitude()+
//                                "\nprovider = "+location.getProvider()+
//                                "\nsatellites = "+satellites+
//                                "\ndate= "+ android.text.format.DateFormat.format("EEEE;d/M/yyyy;H:m:s ",new Date())
//                        );
//                    }


                // Try to reconnect when server crash
//                    if(!app.getSocket().connected())
//                        app.getSocket().connect();

                // Check Network location service state
//                    if (networkLocationService != null){
//                        stopService(networkLocationService);
//                        networkLocationService = null;
//                    }


                // Get date
//                    calender = "" + android.text.format.DateFormat.format("EEEE;d/M/yyyy;H:m:s ",new Date());

                // Send data
//                    JSONObject server_data = new JSONObject();
//                    try {
//
//
//                        server_data.accumulate("imei", IMEI);
//                        server_data.accumulate("model_", Build.MANUFACTURER);
//                        server_data.accumulate("model", Build.MODEL);
//                        server_data.accumulate("provider", GPS_PROVIDER);
//                        server_data.accumulate("latLon", "" + location.getLatitude());
//                        server_data.accumulate("latLon", "" + location.getLongitude());
//                        server_data.accumulate("alt", "" + location.getAltitude());
////                        server_data.accumulate("satel", satel);
//                        server_data.accumulate("calender", calender);
//
//                        /*if(!satel.equals(""))*/
//                            app.getSocket().emit("entity_location",server_data);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                System.out.println("AAAA: "+provider + " is enabled");
//        if (Build.VERSION.SDK_INT >= 23 &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED ) {

//                    gps_locationManager.addGpsStatusListener(gpsStatusListener);
//                    gps_locationManager.requestLocationUpdates(GPS_PROVIDER, GPS_REFRESH_TIME, GPS_REFRESH_DISTANCE, gps_locationListener);

//        }

                /* Reacticate socket */
//                    app.init_events();
//                    app.getSocket().connect();

                /* update notification */
//                    notifyBuilder.setContentText("Sending Location ...");
//                    notificationManager.notify(1, notifyBuilder.build());
            }

            @Override
            public void onProviderDisabled(String provider) {
                System.out.println("AAAA: "+provider + " is disabled");
//                    if (gps_locationManager != null){
//                        gps_locationManager.removeUpdates(gps_locationListener);
////                        gps_locationManager.removeGpsStatusListener(gpsStatusListener);
//                    }

                /* Desactivate socket */
//        app.getSocket().disconnect();
//        app.kill_events();

                /* Update notification */
//                    notifyBuilder.setContentText("GPS or Network disabled. In Stand-by ...");
//                    notificationManager.notify(1, notifyBuilder.build());
            }
        };
        // gps status listener
        gpsStatusListener = new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {

                GpsStatus gpsStatus = gps_locationManager.getGpsStatus(null);

                /* If gps is on */
                if (gpsStatus != null) {

                    /* From Gps satellite get Iterable Satellites Objects */
                    Iterable<GpsSatellite> gpsSatellites = gpsStatus.getSatellites();
                    Iterator<GpsSatellite> sat = gpsSatellites.iterator();

                    /* The satellites list */
                    String lSatellites = null;

                    /* Counters */
                    int nb_sat_found = 0;
                    int nb_sat_used = 0;

                    /* Iterate on satellites */
                    while (sat.hasNext()) {
                        GpsSatellite satellite = sat.next();
                        lSatellites = "";
                        if (satellite.usedInFix())
                            lSatellites += (nb_sat_used++);
                        else
                            lSatellites += (nb_sat_used);
                        lSatellites += "/" + (nb_sat_found++);
                    }

                    /* Check if we have satellites to locate user */
                    if (nb_sat_used != 0){
                        // There are satellites to fix the location
                        satellites = "" + lSatellites;

                    }
                    else{
                        // There are no satellites to fix the location
                        satellites = "N/A";


                    }

                    /*System.out.println("\n\nAAAA: !! Satellites !! "+
                            "\n satellites :  "+ satellites +
                            "\n used for the location : "+ nb_sat_used+ "/" + nb_sat_found);*/
                }

            }
        };

        /* Request location*/
        // Network location first
        network_locationManager.requestLocationUpdates(NETWORK_PROVIDER, GPS_REFRESH_TIME, GPS_REFRESH_DISTANCE, locationListener);

        // Gps location NEXT ==> for more acuracy
        gps_locationManager.addGpsStatusListener(gpsStatusListener);
        gps_locationManager.requestLocationUpdates(GPS_PROVIDER, GPS_REFRESH_TIME, GPS_REFRESH_DISTANCE, locationListener);

        return startId;
    }

    // When a client Rebinf to the service
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    // When a client bind to the service
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // when all clients unbind from the service
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    // Service is about to be destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the listener from GPS location manager
        if (gps_locationManager != null){
            gps_locationManager.removeUpdates(locationListener);
            gps_locationManager.removeGpsStatusListener(gpsStatusListener);
        }
        // Remove the listener from NETWORK location manager
        if (network_locationManager != null){
            network_locationManager.removeUpdates(locationListener);
        }

        // Desactivate the socket
        app.getSocket().disconnect();
        app.kill_events();

        /* UnNotify */
        notificationManager.cancel(1);
    }

    // Restart service if OS stop it due to need memory
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


    /**
     * Methodes
     */
    /* Initialisation */

    // Initialise the components
    private void initialise_components() {

        // Get an instance for the Application
        app = (ApplicationManager) this.getApplication();

        // Initialise the location managers
        gps_locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        network_locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        // Activate the server Socket
        if (gps_locationManager.isProviderEnabled(GPS_PROVIDER) || network_locationManager.isProviderEnabled(NETWORK_PROVIDER)){
            app.init_events();
            app.getSocket().connect();
        }

        // Get the IMEI
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = "" + telephonyManager.getDeviceId();

        // Initialise & Pop notification
        init_notification();
        pop_notification();


    }
    // Initialise the notification
    private void init_notification() {

        /* Set the intent */
        final Intent emptyIntent = new Intent(this, ActivityMain.class); // used to handle exeption in PendingIntent.getActivity
        emptyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 1, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Build notification */
        notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Tracker On")
                .setContentText("Location Service is working ...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
    // Pop the notification
    private void pop_notification() {
        /* Activate Notification */
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notifyBuilder.build());
    }


    /* Other */
    public void useLocation (Location location){

        /* Try to reconnect when server crash */
        if(!app.getSocket().connected())
            app.getSocket().connect();

        /* Gathering Location data */
        String provider = location.getProvider();
        String curent_satellites = satellites;

        String latitude = location.getLatitude() +"";
        String longitude = location.getLongitude() +"";

        String altitude = location.getAltitude() +"";
        String accuracy = location.getAccuracy() +"";
        String speed = location.getSpeed() +"";

        String time = location.getTime() +"";
        String elapsedRealTimeNanostime = location.getElapsedRealtimeNanos() +"";

        String bearing = location.getBearing() +"";
        String extras = location.getExtras() +"";

        String date = android.text.format.DateFormat.format("EEEE;d/M/yyyy;H:m:s ",new Date()) +"";

        /* Log the location data*/
        System.out.println("\n\n !! "+location.getProvider()+" location !!"+
                "\nprovider : "+provider+
                "\ncurent_satellites : "+curent_satellites+

                "\nlat : "+latitude+
                "\nlon : "+longitude+

                "\nAltitude : "+altitude+
                "\nAccuracy : "+accuracy+
                "\nSpeed : "+speed+

                "\nTime : "+time+
                "\nElapsedRealtimeNanos = "+elapsedRealTimeNanostime+

                "\nBearing : "+bearing+
                "\nExtras : "+extras+


                "\ndate: "+ date
        );

        /* Send the Location data */
        JSONObject server_data = new JSONObject();
        try {

            // User Json data
            server_data.accumulate("imei", IMEI);
            server_data.accumulate("manufacture", Build.MANUFACTURER);
            server_data.accumulate("model", Build.MODEL);

            // location Json data
            server_data.accumulate("provider", provider);
            server_data.accumulate("curent_satellites", curent_satellites);

            server_data.accumulate("latLon", latitude);
            server_data.accumulate("latLon", longitude);

            server_data.accumulate("altitude", altitude);
            server_data.accumulate("accuracy", accuracy);
            server_data.accumulate("speed", speed);

            server_data.accumulate("time", time);
            server_data.accumulate("elapsedRealTimeNanostime", elapsedRealTimeNanostime);

            server_data.accumulate("date", date);

            if( (provider.equals("network")) ||
                    ( (provider.equals("gps")) && !curent_satellites.equals("N/A") && !curent_satellites.equals("0/0")) ){

                app.getSocket().emit("entity_location", server_data);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
