package net.zexes_g.demontrack;

import android.app.Application;

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

    private static Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://geomeet.ngrok.io");
        } catch (URISyntaxException e) {}

    }
    Emitter.Listener onIs_serverActif;
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
        return onIs_serverActif;
    }
    //----------------------------------------------------------------------------------------------


      //////////////
     // Methodes //
    //////////////

    /* Initialise the callback */
    public void init_events() {

        /* Events declaration */
        onIs_serverActif = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                JSONObject data = (JSONObject) args[0];
                //String username;
                String isActif;
                try {
                    isActif = data.getString("isActif");
                } catch (JSONException e) {
                    return;
                }
                if (isActif=="false"){
                    restart_Service();
                }
            }

        /* Seth event on the stack */
            //mSocket.on("message", onGet_something);
        };
    }

    /* Restart the location service */
    private void restart_Service() {
    }

    /* Remove the callbacks */
    public void kill_events(){
        mSocket.off("new message", onIs_serverActif);
    }
    //----------------------------------------------------------------------------------------------
}
