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
            IO.Options options = new IO.Options();
//            options.sslContext = SSLContext.getDefault();
            options.reconnection = true;
            JSONObject postData = new JSONObject();

            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbnRpdHlfbmFtZSI6Ik1vdGEiLCJlbnRpdHlfdHlwZSI6IlNtYXJ0cGhvbmUiLCJlbnRpdHlfbWFjIjoiYzA6Yzk6NzY6Mjg6M2E6OTUiLCJjX21hbmFnZXIiOiI1Y2RiYTY4ZmUxZTZlMzBhOTk4YTI5ZmQiLCJvcGVyYXRvciI6IjVjZGJhNmNmZTFlNmUzMGE5OThhMjlmZSIsInBvb2xfbmFtZSI6InAxIiwiaWF0IjoxNTU4MzUyNzQxfQ.-vNREU4eBJYT30A82-iMIS2IicKIju12YKd5e528Jsw";
            try {
                postData.put("token", token);
            }catch (Exception e){}

            options.secure = true;
            options.query = "data= "+postData;


            mSocket = IO.socket("https://758bc672.ngrok.io/crowd", options);
        } catch (URISyntaxException e) {}

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
