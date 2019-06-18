package net.zexes_g.main.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import net.zexes_g.demontrack.R;
import net.zexes_g.main.services.ServiceLocation;


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
    Button scan_QRCservice;

    /* Notification */
    NotificationManager notificationManager;
    NotificationCompat.Builder notifyBuilder;
    String CHANNEL_ID = "C28";

    /* Permission codes */
    int PERMISSION_LOCATION_CODE = 100;
    int PERMISSION_CAMERA_CODE = 200;
    boolean isPermission_LocationGranted;
    boolean isPermission_CameraGranted;

    /* Other */
    SharedPreferences permissionPreferences;
    boolean isFirstPermission_LocationRequest = false;
    boolean isFirstPermission_CameraRequest = false;


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
        setContentView(R.layout.activity_main);

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
    /* Initialisation */

    // Initialise the ui components
    private void initialise_ui() {

        /* Link layouta */
        start_GPSservice = (Button) findViewById(R.id.go);
        stop_GPSservice = (Button) findViewById(R.id.stop);

        scan_QRCservice = (Button) findViewById(R.id.btnScanBarcode);

        /* Enable the buttons */
        start_GPSservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /* Request location permission*/
                location_permission();

                /* Ckeck the location permission */
                if (isPermission_LocationGranted) {
                    // The permission is granted
                    // Check if the GPS location service is running
//                    if (!isMyServiceRunning(ServiceGpsLocation______________________.class)) {
                    if (!isMyServiceRunning(ServiceLocation.class)) {

                        // GPS service is not running
                        // Check the GPS Provider is ON/OFF
                        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            // Ask for GPS Provider manual activation
                            Intent gps_provider = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            gps_provider.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(gps_provider , 1);
                        } else{
                            // Start GPS Location Service
                            startLocation_service();
                        }
                    }
                    else {
                        // GPS service is already running
                        Toast.makeText(getApplicationContext(), "The service is Already Activated", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        scan_QRCservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /* Request location permission*/
                camera_permission();

                /* Ckeck the location permission */
                if (isPermission_CameraGranted) {

                    // The permission is granted
                    scanQR();
                    // Check if the GPS location service is running
//                    if (!isMyServiceRunning(ServiceGpsLocation______________________.class)) {
//                    if (!isMyServiceRunning(ServiceLocation.class)) {
//
//                        // GPS service is not running
//                        // Check the GPS Provider is ON/OFF
//                        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                            // Ask for GPS Provider manual activation
//                            Intent gps_provider = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            gps_provider.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivityForResult(gps_provider , 1);
//                        } else{
//                            // Start GPS Location Service
//                            startLocation_service();
//                        }
//                    }
//                    else {
//                        // GPS service is already running
//                        Toast.makeText(getApplicationContext(), "The service is Already Activated", Toast.LENGTH_SHORT).show();
//                    }
                }

            }
        });
        stop_GPSservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* User Location Service */
//                if(isMyServiceRunning(ServiceGpsLocation______________________.class)){
                if(isMyServiceRunning(ServiceLocation.class)){
                    stopLocation_service();
                }
                else
                    Toast.makeText(getApplicationContext(), "The service is Already Stopped", Toast.LENGTH_SHORT).show();
            }
        });


        /* Preferences */
        permissionPreferences = getSharedPreferences("permissionPreferences ", MODE_PRIVATE);
        isFirstPermission_LocationRequest = permissionPreferences.getBoolean("isFirstPermission_LocationRequest", true);
        isFirstPermission_CameraRequest = permissionPreferences.getBoolean("isFirstPermission_CameraRequest", true);

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

        startService(new Intent(getApplicationContext(), ServiceLocation.class));
        this.finish();
    }
    // Stop User Location Service
    private void stopLocation_service() {

        /* Stop Service */
//        stopService( new Intent(this, ServiceGpsLocation______________________.class));
        stopService( new Intent(this, ServiceLocation.class));

        /* Notify Stop Service*/

        // Activate Notification
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Set the intent
        final Intent emptyIntent = new Intent(); // used to handle exeption in PendingIntent.getActivity
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 2, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Build notification
        notifyBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_stop)
                        .setContentTitle("Tracker Off")
                        .setContentText("Stoped Location Service")
                        .setContentIntent(pendingIntent)
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


        // Pop the notification
        notificationManager.notify(2, notifyBuilder.build());

    }
    // Result ask GPS activation
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
//                ContextCompat.checkSelfPermission(ActivityMain.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ActivityMain.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {

            /*Permission is not granted*/

            // Check first time requesting permission
            if (!isFirstPermission_LocationRequest){

                // Not the first time requesting
                // Check if user said to not ask him again
                if (!ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this, // NOTE: [DontShowAgain = shouldShowRequestPermissionRationale]
                        Manifest.permission.ACCESS_FINE_LOCATION ) &&
                        !ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)){
                    // Dont show again
                    // Explain the need of this permission
                    new AlertDialog.Builder(ActivityMain.this)
                            .setMessage("To use this Feature you need to enable The following Permissions" +
                                    "\n* Location: User in the GPS Service" +
                                    "\n* Phone State: Used to authentify your device by EMAI")
                            .setIcon(R.drawable.ic_location_on_black_24dp)

                   /* dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
                    dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.your_icon); */
                            .setTitle("Permission requested")
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
            }
            else {

                // First time Requesting
                // Will no more be the first request later
                SharedPreferences.Editor editor = permissionPreferences.edit();
                editor.putBoolean("isFirstPermission_LocationRequest", false);
                isFirstPermission_LocationRequest = false;
                editor.apply();
            }

            // Request the permission
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
            },PERMISSION_LOCATION_CODE);

        }else{
            /* Permission is granted */
            isPermission_LocationGranted = true;
        }
    }

    // Request Permission
    public void camera_permission() {
        /*
         * If the SDK version is Under 23
         * AND the permissions are NOT GRANTED
         */
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(ActivityMain.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ) {

            /*Permission is not granted*/

            // Check first time requesting permission
            if (!isFirstPermission_CameraRequest){

                // Not the first time requesting
                // Check if user said to not ask him again
                if (!ActivityCompat.shouldShowRequestPermissionRationale(ActivityMain.this, // NOTE: [DontShowAgain = shouldShowRequestPermissionRationale]
                        Manifest.permission.CAMERA )){
                    // Dont show again
                    // Explain the need of this permission
                    new AlertDialog.Builder(ActivityMain.this)
                            .setMessage("To use this Feature you need to enable The following Permissions" +
                                    "\n* Camera: Scan the authentification token" +
                                    "\n From a QR Code")
                            .setIcon(R.drawable.ic_location_on_black_24dp)

                            /* dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
                             dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.your_icon); */
                            .setTitle("Permission requested")
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
            }
            else {

                // First time Requesting
                // Will no more be the first request later
                SharedPreferences.Editor editor = permissionPreferences.edit();
                editor.putBoolean("isFirstPermission_CameraRequest", false);
                isFirstPermission_CameraRequest = false;
                editor.apply();
            }

            // Request the permission
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA
            },PERMISSION_CAMERA_CODE);

        }else{
            /* Permission is granted */
            isPermission_CameraGranted = true;
        }
    }


    // Result Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /* If it's our request */
        if(requestCode == PERMISSION_LOCATION_CODE ){
            /* If the permissions are GRANTED */
            if(grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED /*&&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED*/){
                // Positif responce to the Anroid request Dialog
            }else {
                // Negatif responce to the Anroid request Dialog
            }
        }
        if(requestCode == PERMISSION_CAMERA_CODE){
            /* If the permissions are GRANTED */
            if(grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED /*&&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED*/){
                // Positif responce to the Anroid request Dialog
            }else {
                // Negatif responce to the Anroid request Dialog
            }
        }
    }


    /* Other */
    public void scanQR(View v){
        startActivity(new Intent(ActivityMain.this, ActivityQRC_scanner.class));
    }
    public void scanQR(){
        startActivity(new Intent(ActivityMain.this, ActivityQRC_scanner.class));
    }
}
