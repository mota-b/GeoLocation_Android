package net.zexes_g.demontrack;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class ActivityMain extends AppCompatActivity {

    /**
     * Attributes
     */

    /**
     * Lyfe Cycle
     */

    /**
     * Methodes
     */


    /**
     * Attributes
     */
    /* Buttons*/
    Button start_GPSservice;
    Button stop_GPSservice;
    /* Notification */
    NotificationManager notificationManager;
    NotificationCompat.Builder notifyBuilder;

    /* Permission codes */
    int LOCATION_PERMISSION_CODE = 100;

    /* Other */
    SharedPreferences permissionPreferences;
    boolean isFirstPermissionLocationRequest;


    /**
     * Lyfe Cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Full screan mode */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /* Set layouta */
        setContentView(R.layout.activity_perms_and_go);

        /* Initialise Components */
        initialise_ui();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * Methodes
     */

    /* This activity */

    // Initialise the ui components
    private void initialise_ui() {

        /* Link layouta */
        start_GPSservice = (Button) findViewById(R.id.go);
        stop_GPSservice = (Button) findViewById(R.id.stop);

        /* Enable the buttons */
        start_GPSservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*Request location permission*/
                location_permission();

                /* User Location Service */
                /*if(!isMyServiceRunning(GpsLocationService.class)){

                    *//* Is GPS On *//*
                    LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {

                        *//* Ask for GPS *//*
                        Intent gps = new Intent(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        startActivityForResult(gps,1);
                    }
                    else
                        *//* Start GPS *//*
                        startLocation_service();
                }
                else
                    Toast.makeText(getApplicationContext(), "The service is Already Activated", Toast.LENGTH_SHORT).show();*/
            }
        });
        /*stop_GPSservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//* User Location Service *//*
                if(isMyServiceRunning(GpsLocationService.class)){
                    stopLocation_service();
                }
                else
                    Toast.makeText(getApplicationContext(), "The service is Already Stopped", Toast.LENGTH_SHORT).show();
            }
        });*/


        /* Preferences */
        permissionPreferences = getSharedPreferences("permissionPreferences ", MODE_PRIVATE);
        isFirstPermissionLocationRequest = permissionPreferences.getBoolean("isFirstPermissionLocationRequest", true);

    }


    /* Service */

    // Test the status of a service
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    // Start User Location Service
    private void startLocation_service() {
        startService(new Intent(this, GpsLocationService.class));
        this.finish();
    }
    // Stop User Location Service
    private void stopLocation_service() {

        /* Stop Service */
        stopService( new Intent(this, GpsLocationService.class));

        /* Notify Stop Service*/

        // Set the intent
        final Intent emptyIntent = new Intent(); // used to handle exeption in PendingIntent.getActivity
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 2, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Build notification
        notifyBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_stop)
                        .setContentTitle("Tracker Off")
                        .setContentText("Stoped Location Service")
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        // Activate Notification
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notifyBuilder.build());

    }
    // Result ask GPS
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


    /* Permissions */




    // Request Permission
    public void location_permission() {
        /*
         * If the SDK version is Under 23
         * AND the permissions are NOT GRANTED
         */
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(ActivityMain.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ActivityMain.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED /*&&
                ContextCompat.checkSelfPermission(ActivityMain.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED*/) {

            /*Permission is not granted*/

            // Check first time requesting permission
            if (!isFirstPermissionLocationRequest){

                // Not the first time requesting
                // Check if user said to not ask him again
                if (!ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this, // NOTE: [DontShowAgain = shouldShowRequestPermissionRationale]
                        Manifest.permission.ACCESS_FINE_LOCATION ) &&
                        !ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)){
                    // Dont show again
                    // Explain the need of this permission
                    new AlertDialog.Builder(ActivityMain.this)
                            .setMessage("To use this Feature you need to enable Location Permission")
                            .setTitle("Feature requested")
                            .setPositiveButton("Permissions",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    }
                            )
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                            dialog.dismiss();
                                        }
                                    }
                            )
                            .create()
                            .show();
                }
            }else {

                // First time Requesting
                // Will no more be the first request later
                SharedPreferences.Editor editor = permissionPreferences.edit();
                editor.putBoolean("isFirstPermissionLocationRequest", false);
                isFirstPermissionLocationRequest = false;
                editor.apply();
            }

            // Request the permission
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    /*Manifest.permission.READ_PHONE_STATE,*/
            },100);

        }else{
            /*Permission is granted*/
        }
    }
    // Result Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /* If it's our request */
        if(requestCode == LOCATION_PERMISSION_CODE ){
            /* If the permissions are GRANTED */
            if(grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED ){

                /*
                 * You can use the Application
                 */
            }else {
                /*location_permission();*/
                /* disable the feature if actif */
                if (isMyServiceRunning(GpsLocationService.class)){
                    stopLocation_service();
                }

            }
        }
    }


}
