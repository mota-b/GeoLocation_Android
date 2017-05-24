package net.zexes_g.demontrack;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Activity_persm_and_go extends Activity {

      ///////////////
     // Attribute //
    ///////////////

    /* layout */
    Button go;
    Button stop;
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

        /* Set layout */
        //setContentView(R.layout.activity_gps_main);
        setContentView(R.layout.activity_perms_and_go);

        // Is need permissions
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

        /* Link layout */
        go = (Button) findViewById(R.id.go);
        stop = (Button) findViewById(R.id.stop);
    }

    /* We can use buttons */
    private void enable_buttons() {
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* User Location Service */
                startLocation_service();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* User Location Service */
                stopLocation_service();
            }
        });
    }

    /* Start User Location Service */
    private void startLocation_service() {
        startService(new Intent(this, GpsLocationService.class));
        this.finish();
    }

    /* Stop User Location Service */
    private void stopLocation_service() {
        stopService(new Intent(this, GpsLocationService.class));
        //this.finish();
    }


    /* Resume receivers */
    private void resume_receivers() {

        /* Location update receiver *//**//*
        if (gps_service_location_receiver == null) {
            *//**//* If the gps_location_receiver doesn't existe
            *   Than create one
            * *//**//*
            gps_service_location_receiver = new BroadcastReceiver() {

                *//**//* Set the receiv Event in the creation of the receiver *//**//*
                @Override
                public void onReceive(Context context, Intent location_gps_update) {

                    if(gps_ACTIF_STATE != "found")
                        *//**//* Change icon state *//**//*
                        draw_state(4);

                    *//**//* And change the state *//**//*
                    gps_ACTIF_STATE = "found";

                    *//**//* Get the position *//**//*



                    lat = "" + location_gps_update.getExtras().get("lat");
                    lon = "" + location_gps_update.getExtras().get("lon");
                    alt = "" + location_gps_update.getExtras().get("alt");
                    sat = "" + location_gps_update.getExtras().get("sat");






                    *//**//* Append coords *//**//* *//**//* TO desplay the trace in Scrollable Text View *//**//*
                    // coord.setText(lat+"\n"+lon+"\n------------\n"+coord.getText());

                    *//**//* Set coords *//**//*
                    coord.setText(lat + "\n" + lon + "\n" + alt +"\n"+ sat);

                    *//**//* POST to server in ASYNCH *//**//*
                    // ID;lat;lon;Day_weak;dd/MM/yyy;H:mm:ss
                    new ServerCallAsyncTask().execute(imei.getText().toString(),lat,lon,alt,sat,""+android.text.format.DateFormat.format("EEEE;d/M/yyyy;H:m:s ",new Date()));
                }
            };
            *//**//* AND regester it *//**//*
            registerReceiver(gps_service_location_receiver, new IntentFilter("location_update"));*//*
        }

        *//* Location status receiver *//*
        if (gps_service_status_receiver == null) {
            *//* If the gps_status_receiver doesn't existe
            *   Than create one
            *//*
            gps_service_status_receiver = new BroadcastReceiver() {

                *//* Set the receiv Event in the creation of the receiver *//*
                @Override
                public void onReceive(Context context, Intent gps_status) {

                    Log.d("STATUS","IN ACTIVITY :"+gps_status.getExtras().get("status"));
                    *//* Change state from service*//*
                    String status = ""+gps_status.getExtras().get("status");
                    gps_ACTIF_STATE = status;
                    if(status.equals("off"))
                        draw_state(2);
                    else
                        if (status.equals("on"))
                            draw_state(3);
                }
            };
            *//* AND regester it *//*
            registerReceiver(gps_service_status_receiver, new IntentFilter("location_status"));
        }

*/
    }
    //----------------------------------------------------------------------------------------------
}
