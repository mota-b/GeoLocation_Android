package net.zexes_g.demontrack;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.Date;

public class GPS_Activity extends AppCompatActivity{




    /* Text views */
    TextView coord;
    TextView id;
    TextView imei;

    /* Buttons */
    ImageButton location_state_btn;

    /* Services receivers */
    BroadcastReceiver gps_service_location_receiver;
    BroadcastReceiver gps_service_status_receiver;

    /* Location state */
    String gps_provider ;
    LocationManager lm;
    String gps_ACTIF_STATE ;
    String last_known_location;

    String lat,lon,alt,sat;

    /* Telephony */
    TelephonyManager telephonyManager;

    //--------------------



    // Activity life cycle


    /* Start activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /* Full screan mode */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gps_main);




        // Is need permissions
        is_need_permissions();

        /* Initialise component */
        init();

        /* Test if the GPS is already actif */
        if (is_gps_on() && !is_need_permissions()){
            start_gps_service();
            gps_ACTIF_STATE = "searching";
            draw_state(3);
        }

    }

    /* About to start != first time */
    @Override
    protected void onRestart() {
        super.onRestart();

        /* Last location */
        if(!last_known_location.equals("")){
            /* Resume service */
            start_gps_service();

            /* get back the location */
            coord.setText(last_known_location);
        }
    }

    /* Activity is about to be displayed */
    @Override
    protected void onStart() {
        super.onStart();

        /* Use previeu activity data */
        //id.setText(""+login.getExtras().get("country_code")+" "+login.getExtras().get("phone_nbr"));

        /* Activate buttons */
        enable_buttons();
    }

    /* Restore Instance ( if saved ) */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        /* Last location */
        coord.setText(savedInstanceState.getString("saved_coord"));
        gps_ACTIF_STATE = "searshing";
    }

    /* Activity is displayed */
    @Override
    protected void onResume() {
        super.onResume();
        /* Resume receiver */
        resume_receivers();

    }

    /* Activity is about to be hide */
    @Override
    protected void onPause() {
        super.onPause();

        /* Test last known location */
        if(!coord.getText().toString().equals("Unknown\nUnknown\nUnknown\n0/?"))
            last_known_location = coord.getText().toString();
    }

    /* Save instance */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("saved_gps_ACTIF_STATE", gps_ACTIF_STATE);
        outState.putString("saved_coord", ""+coord.getText().toString());
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


        /* If the gps_receiver doesn existe */
        if (gps_service_location_receiver != null){
            /* Than unregister it */
            unregisterReceiver(gps_service_location_receiver);
        }

        /* If the service is GPS IS ON */
        if(is_gps_on())
            stop_gps_service();
    }


    //--------------------------------------------------



    // Methodes




    /*
   * If we are under SDK version mor than 23
   * We need to check user1 permission manually
   * */
    public boolean is_need_permissions() {
        /* If the SDK version is Under 23
        * AND the permissions are not GRANTED
        * */
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED/*&&
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED*/){
            /* Than we request These permissions */
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    /*Manifest.permission.INTERNET*/

            },100);
            return true; /* YES we need to ask permission */
        }
        return false; /* NO we don't need to ask permission */
    }


    /* When we have a request Permission */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /* If it's our request */
        if(requestCode == 100){
            /* If the permissions are GRANTED */
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED /*&&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED */){
                /* You can use the Application */
            }else {

                /* we have to ask */
                is_need_permissions();
            }
        }
    }




    /* Initialise components */
    private void init() {

        /* Text views */
        coord = (TextView) findViewById(R.id.coord);
        //id = (TextView) findViewById(R.id.id);

        /* Buttons */
        location_state_btn = (ImageButton) findViewById(R.id.gps_state);

        /* Location Manager */
        lm =  (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);


        /* */
        last_known_location = "";
        gps_provider = LocationManager.GPS_PROVIDER;
        gps_ACTIF_STATE = "none";

        /* IMEI */
        imei = (TextView) findViewById(R.id.IMEI);
        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imei.setText(telephonyManager.getDeviceId()+"");
    }

    /* We can use buttons */
    private void enable_buttons() {

        /* Location service controle */
        location_state_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Gps_state_btn handler */
                if (!is_gps_on()){
                    /* Try to Activate your GPS First */
                    Intent settings_gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(settings_gps,1);
                }else {
                    switch (gps_ACTIF_STATE){
                        case "searching":
                            // NOTHING TODO
                            break;
                        case "found":
                            // ZOOM TODO
                            break;
                        case "none":
                            /* Try to Activate your GPS First */
                            Intent settings_gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(settings_gps,1);
                            break;
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1)
        if(is_gps_on()){
            start_gps_service();
            gps_ACTIF_STATE = "searching";
            draw_state(3);
        }
    }

    /* Stop GPS_service */
    private void stop_gps_service() {

        /* stop the gps service */
        stopService(new Intent(getApplicationContext(), GPS_Service.class));
    }

    /* Start GPS_service */
    private void start_gps_service() {
        /* start the gps service */
        startService(new Intent(this, GPS_Service.class));
    }

    /* Resume receivers */
    private void resume_receivers() {

        /* Location update receiver */
        if (gps_service_location_receiver == null) {
            /* If the gps_location_receiver doesn't existe
            *   Than create one
            * */
            gps_service_location_receiver = new BroadcastReceiver() {

                /* Set the receiv Event in the creation of the receiver */
                @Override
                public void onReceive(Context context, Intent location_gps_update) {

                    if(gps_ACTIF_STATE != "found")
                        /* Change icon state */
                        draw_state(4);

                    /* And change the state */
                    gps_ACTIF_STATE = "found";

                    /* Get the position */



                    lat = "" + location_gps_update.getExtras().get("lat");
                    lon = "" + location_gps_update.getExtras().get("lon");
                    alt = "" + location_gps_update.getExtras().get("alt");
                    sat = "" + location_gps_update.getExtras().get("sat");






                    /* Append coords */ /* TO desplay the trace in Scrollable Text View */
                    // coord.setText(lat+"\n"+lon+"\n------------\n"+coord.getText());

                    /* Set coords */
                    coord.setText(lat + "\n" + lon + "\n" + alt +"\n"+ sat);

                    /* POST to server in ASYNCH */
                    // ID;lat;lon;Day_weak;dd/MM/yyy;H:mm:ss
                    new Call_serverAsyncTask().execute(imei.getText().toString(),lat,lon,alt,sat,""+android.text.format.DateFormat.format("EEEE;d/M/yyyy;H:m:s ",new Date()));
                }
            };
            /* AND regester it */
            registerReceiver(gps_service_location_receiver, new IntentFilter("location_update"));
        }

        /* Location status receiver */
        if (gps_service_status_receiver == null) {
            /* If the gps_status_receiver doesn't existe
            *   Than create one
            */
            gps_service_status_receiver = new BroadcastReceiver() {

                /* Set the receiv Event in the creation of the receiver */
                @Override
                public void onReceive(Context context, Intent gps_status) {

                    Log.d("STATUS","IN ACTIVITY :"+gps_status.getExtras().get("status"));
                    /* Change state from service*/
                    String status = ""+gps_status.getExtras().get("status");
                    gps_ACTIF_STATE = status;
                    if(status.equals("off"))
                        draw_state(2);
                    else
                        if (status.equals("on"))
                            draw_state(3);
                }
            };
            /* AND regester it */
            registerReceiver(gps_service_status_receiver, new IntentFilter("location_status"));
        }


    }

    /* Draw state Icon */
    private void draw_state(int n){
        switch (n){
            case 1:
                location_state_btn.setImageResource(R.drawable.gps_on);
                break;
            case 2:
                location_state_btn.setImageResource(R.drawable.gps_of);
                coord.setText("0.0\n0.0");
                break;
            case 3:
                location_state_btn.setImageResource(R.drawable.location_in_search);
                break;
            case 4:
                location_state_btn.setImageResource(R.drawable.location_found);
                break;
        }
    }

    /* GPS state */
    public boolean is_gps_on() {
        return lm.isProviderEnabled(gps_provider);
    }


    //-----------------------------------------------------------------------------------------



}
