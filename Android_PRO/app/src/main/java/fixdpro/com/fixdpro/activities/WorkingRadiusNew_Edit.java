package fixdpro.com.fixdpro.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.WheelView;

public class WorkingRadiusNew_Edit extends AppCompatActivity {
    ImageView imgClose, imgNext;
    double zoomLevel = 14 ;
    double radius = 100000*1.6;
    String radius_to_send  = "100" ;
    int selectedIndexMain = 3;
    HashMap<String,String> finalRequestParams = new HashMap<String,String>() ;
    boolean ispro = false ;

    SharedPreferences _prefs = null ;
    private String working_radius = "",error_message = "";

    GoogleMap mMap;
    private Typeface fontfamily;
    private Context _context = this;
    private LatLng latLng = null ;
    TextView txtMiles;
    TextView txtYourRightsUnder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_radius_new__edit);
        _prefs = Utilities.getSharedPreferences(_context);
        if (_prefs.getString(Preferences.ROLE,"pro").equals("pro")){
            ispro = true ;
        }
        radius = Double.parseDouble(_prefs.getString(Preferences.WORKING_RADIUS_MILES, "100.0"));
        radius = radius * 1000 * 1.6;
        latLng = new LatLng(Double.parseDouble(_prefs.getString(Preferences.LATITUDE, "40.712784")),Double.parseDouble(_prefs.getString(Preferences.LONGITUDE,"-74.005941")));
        setWidgets();
        setCLickListner();
        setUpMap();
        txtYourRightsUnder.setText("How far are you willing to travel from "+_prefs.getString(Preferences.ZIP,""));
    }
    private void setUpMap() {

        // For showing a move to my loction button
//        mMap.setMyLocationEnabled(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawCircle();
            }
        }, 300);
        // For dropping a marker at a point on the Map
        //mMap.addMarker(new MarkerOptions().position(new LatLng(model.getLatitude(), model.getLongitude())).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(model.getLatitude(),
        //      model.getLongitude()), 12.0f));
    }
    private void drawCircle(){
        if (latLng !=  null){
            // Instantiates a new CircleOptions object and defines the center and radius
            mMap.clear();
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.parseColor("#810000ff"))
                    .radius(radius);


            // In meters

// Get back the mutable Circle
            Circle circle =  mMap.addCircle(circleOptions);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) (getZoomLevel(circle) - 0.9)));
        }else{
//            Location location  = mMap.getMyLocation();
//            if (location != null){
//                latLng = new LatLng(location.getLatitude(),location.getLongitude());
//            }else {
//                latLng = new LatLng(52.636397,-107.402344);
//            }

            mMap.clear();
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.parseColor("#810000ff"))
                    .radius(radius);


            // In meters

// Get back the mutable Circle
            Circle circle =  mMap.addCircle(circleOptions);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float)(getZoomLevel(circle) - 0.8)));
        }

    }
    public double getZoomLevel(Circle circle) {
        if (circle != null){
            double radius = circle.getRadius();

            double scale = radius / 500;
            zoomLevel =(double) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }
    private void showAlertDialog(String Title,String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                _context);

        // set title
        alertDialogBuilder.setTitle(Title);
        // set dialog message
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtMiles = (TextView)findViewById(R.id.txtMiles);
        txtYourRightsUnder = (TextView)findViewById(R.id.txtYourRightsUnder);
        txtMiles.setText(Double.parseDouble(_prefs.getString(Preferences.WORKING_RADIUS_MILES, "100.0"))+" Miles");

        ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap ;
            }
        });

    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalRequestParams.put("api", "update");
                if (ispro)
                    finalRequestParams.put("object", "pros");
                else
                    finalRequestParams.put("object", "technician");
                finalRequestParams.put("data[pros][working_radius_miles]",radius_to_send);
                finalRequestParams.put("token",_prefs.getString(Preferences.AUTH_TOKEN,""));
                GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",updateResponseListener,exceptionListener,WorkingRadiusNew_Edit.this,"");
                getApiResponseAsyncNew.execute(finalRequestParams);

            }
        });
        txtMiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHourlyRateDialog();
            }
        });
    }
    IHttpResponseListener updateResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response.toString()" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    SharedPreferences.Editor editor = _prefs.edit();
                    editor.putString(Preferences.WORKING_RADIUS_MILES, radius_to_send);

                    editor.commit();
                    finish();
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                        handler.sendEmptyMessage(1);
                    }
                }

            } catch (JSONException e) {

            }
        }
    };
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener(){

        @Override
        public void handleException(String exception) {
            error_message = exception;
            handler.sendEmptyMessage(2);
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showAlertDialog("Fixd-Pro",error_message);
        }
    };
    private  void showHourlyRateDialog(){
        working_radius = txtMiles.getText().toString().trim();

        final String[] TYPES = new String[]{"25 Miles", "50 Miles","75 Miles","100 Miles","125 Miles","150 Miles","175 Miles","200 Miles"};
        final Double[] RADIUS = new Double[]{25000.00 * 1.6, 50000.00 * 1.6,75000.00 * 1.6,100000.00 * 1.6,125000.00 * 1.6,150000.00 * 1.6,175000.00 * 1.6,200000.00 * 1.6};
        final String[] RADIUS_TO_SEND = new String[]{"25", "50","75","100","125","150","175","200"};
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wheelviewdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        ImageView imgok = (ImageView) dialog.findViewById(R.id.img_ok);
        WheelView wheelView = (WheelView) dialog.findViewById(R.id.wheelView);
        wheelView.setOffset(1);
//        wheelView.setSeletion(2);
        wheelView.setItems(Arrays.asList(TYPES));
        wheelView.setSeletion(selectedIndexMain);
        if (working_radius.length() == 0){
            working_radius = TYPES[1].toString();
            txtMiles.setText(working_radius);
        }

        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d("", "selectedIndex: " + selectedIndex + ", item: " + item);
                working_radius = TYPES[selectedIndex - 1].toString();
                txtMiles.setText(working_radius);
                radius = RADIUS[selectedIndex - 1] ;
                radius_to_send = RADIUS_TO_SEND[selectedIndex - 1] ;
                selectedIndexMain = selectedIndex - 1;
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        imgok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                drawCircle();
            }
        });
        dialog.show();
    }
}


