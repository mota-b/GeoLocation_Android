package net.zexes_g.main.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import net.zexes_g.main.utilities.CustomZXingScannerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ActivityQRC_scanner extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;


    /* Preferneces */
    SharedPreferences deviceRegistrationPreferences;
    String token = "";
    String server_url = "";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        mScannerView = new ZXingScannerView(this) {

            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomZXingScannerView(context);
            }

        };    // Programmatically initialize the scanner view

//        mScannerView = new ZXingScannerView(ActivityQRC_scanner.this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

        /* Scanner setting */
        mScannerView.setAutoFocus(true);
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        mScannerView.setFormats(formats);


        /* Preferences */
        deviceRegistrationPreferences = getSharedPreferences("deviceRegistrationPreferences", MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {

        /* Display OK UI*/
        Toast.makeText(getApplicationContext(), "OK Done", Toast.LENGTH_SHORT).show();

        // Do something with the result here
//        Log.v("AAAA", rawResult.getText()); // Prints scan results
//        Log.v("AAAA", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        /* Get the data from QRC */
        JSONObject jsonQRC = null;
        try {
            jsonQRC = new JSONObject(rawResult.getText());
            token = jsonQRC.getString("token");
            server_url = jsonQRC.getString("server_url");

//            Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* Check QR result validity */
        if(!token.equals("")){
            SharedPreferences.Editor editor = deviceRegistrationPreferences.edit();
            editor.putString("token", token);
            editor.putString("server_url", server_url);
            editor.apply();

            this.finish();
        }
        else{
            // If you would like to resume scanning, call this method below:
            mScannerView.resumeCameraPreview(this);
        }


    }
}