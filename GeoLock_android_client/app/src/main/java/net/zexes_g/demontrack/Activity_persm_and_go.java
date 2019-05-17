package net.zexes_g.demontrack.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import net.zexes_g.demontrack.GpsLocationService;

public class Activity_persm_and_go extends Activity {

      ///////////////
     // Attribute //
    ///////////////

    /* layouta */
    Button go;
    Button stop;

    /* Notification */
    NotificationManager notificationManager;
    NotificationCompat.Builder notifyBuilder;
    //----------------------------------------------------------------------------------------------


      /////////////////////////
     // Activity Life Cycle //
    /////////////////////////

    /* Start activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Full screan mode */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /* Set layouta */
        setContentView(R.layout.activity_perms_and_go);

        /* Is need permissions */
        permissions();

        /* Initialise Components */
        init();
    }

    /* About to start != first time */
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /* Activity is about to be displayed */
    @Override
    protected void onStart() {
        super.onStart();

        /* Activate buttons */
        enable_buttons();
    }

    /* Restore Instance ( if saved ) */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /* Activity is displayed */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /* Activity is about to be hide */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /* Save instance */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /* Activity is hiden */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /* Activity is about to be destroyed */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //----------------------------------------------------------------------------------------------


      //////////////
     // Methodes //
    //////////////

    /*
    * If we are under SDK version mor than 23
    * We need to check user1 permission manually
    *
    */
    public void permissions() {
        /*
        * If the SDK version is Under 23
        * AND the permissions are NOT GRANTED
        */
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(Activity_persm_and_go.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Activity_persm_and_go.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Activity_persm_and_go.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)
        {
            /* Than we have to request These permissions */
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                },100);
        }

        /*
        * You can use the Application
        */
    }

    /* When we have a request Permission */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /* If it's our request */
        if(requestCode == 100){
            /* If the permissions are GRANTED */
            if(grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED ){

                /*
                * You can use the Application
                */
            }else {

                /* spam ask permissions again */
                permissions();
            }
        }
    }

    /* Initialise components */
    private void init() {

        /* Link layouta */
        go = (Button) findViewById(R.id.go);
        stop = (Button) findViewById(R.id.stop);
    }

    /* Initialise the notification */
    private void init_notification() {

        /* Set the intent */
        final Intent emptyIntent = new Intent(); // used to handle exeption in PendingIntent.getActivity
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 2, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Build notification */
        notifyBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_stop)
                        .setContentTitle("Tracker Off")
                        .setContentText("Stop Sending Location.")
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
    }

    /* We can use buttons */
    private void enable_buttons() {
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* User Location Service */
                if(!isMyServiceRunning(GpsLocationService.class)){

                    /* Is GPS On */
                    LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {

                        /* Ask for GPS */
                        Intent gps = new Intent(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        startActivityForResult(gps,1);
                    }
                    else
                        /* Start GPS */
                        startLocation_service();
                }
                else
                    Toast.makeText(getApplicationContext(), "The service is Already Activated", Toast.LENGTH_SHORT).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* User Location Service */
                if(isMyServiceRunning(GpsLocationService.class)){
                    stopLocation_service();
                }
                else
                    Toast.makeText(getApplicationContext(), "The service is Already Stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Result ask GPS */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            if ( manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                startLocation_service();
            }
        }
    }

    /* Start User Location Service */
    private void startLocation_service() {
        startService(new Intent(this, GpsLocationService.class));
        this.finish();
    }

    /* Stop User Location Service */
    private void stopLocation_service() {

        /* Stop Service */
        stopService( new Intent(this, GpsLocationService.class));

        /* notify */
        init_notification();
        pop_notification();
    }

    /* Pop the notification */
    private void pop_notification() {
        /* Activate Notification */
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notifyBuilder.build());
    }

    /* Test the status of a service */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    //----------------------------------------------------------------------------------------------
}
