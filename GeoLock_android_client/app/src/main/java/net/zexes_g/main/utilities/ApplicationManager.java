package net.zexes_g.main.utilities;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by zexes-g on 24/05/17.
 */


public class ApplicationManager extends Application {

      ///////////////
     // Attribute //
    ///////////////

    /* Preferneces */
    SharedPreferences deviceRegistrationPreferences;
    String TOKEN;
    String SERVER_URL;
    String socket_nsp = "/";

    private static Socket mSocket;

    public void reloadData(){
        /* Get preferences */
        deviceRegistrationPreferences = getApplicationContext().getSharedPreferences("deviceRegistrationPreferences", MODE_PRIVATE);
        TOKEN = deviceRegistrationPreferences.getString("token", "");
        SERVER_URL = deviceRegistrationPreferences.getString("server_url", "");
        Log.d("AAAA", TOKEN);
        Log.d("AAAA", SERVER_URL);


        {


            try {
                IO.Options options = new IO.Options();
                options.reconnection = true;
                options.secure = true;

                /*JSon Query for socket hand shake*/
                JSONObject jsonQuery= new JSONObject();
                try {
                    jsonQuery.accumulate("token", TOKEN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                String socket_url = SERVER_URL + socket_nsp;

                options.query = "data= "+ jsonQuery;


                mSocket = IO.socket(socket_url, options);
            } catch (URISyntaxException e) {}

        }
    }
    @Override
    public void onCreate() {
        super.onCreate();




       reloadData();

    }

    Emitter.Listener onGet_something;
    //----------------------------------------------------------------------------------------------

      /////////////
     // Getters //
    /////////////

    /* Get the socket */
    public Socket getSocket(){
        return mSocket;
    }

    /* Get the events */
    public Emitter.Listener getOnGet_something() {
        return onGet_something;
    }
    //----------------------------------------------------------------------------------------------


      //////////////
     // Methodes //
    //////////////

    /* Initialise the callback */
    public void init_events() {

        /* Events declaration */
        onGet_something = new Emitter.Listener() {
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

    /* Remove the callbacks */
    public void kill_events(){
        mSocket.off("new message", onGet_something);
    }
    //----------------------------------------------------------------------------------------------
}
