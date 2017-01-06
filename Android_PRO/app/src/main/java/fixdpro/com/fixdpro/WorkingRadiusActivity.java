package fixdpro.com.fixdpro;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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

import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.WheelView;

public class WorkingRadiusActivity extends AppCompatActivity {
    private Context context = WorkingRadiusActivity.this;
    private String TAG = "WorkingRadiusActivity";
    private TextView txtBack, txtDone,txtWorkingradius,txthowfar,txtcode,txtMiles;
    private String working_radius = "",error_message = "";
    
    GoogleMap mMap;
    private Typeface fontfamily;
    private  Context _context = this;
    private  LatLng latLng = null ;
    HashMap<String,String> finalRequestParams = null;
    SharedPreferences _prefs = null ;
    String image_profile = null,image_driver = null;
    String city = "";
    String zip = "" ;
    double zoomLevel = 14 ;
    double radius = 100000*1.6;
    String radius_to_send  = "100" ;
    int selectedIndexMain = 3;
    boolean isEditing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_radius);
        _prefs = Utilities.getSharedPreferences(_context);
        radius = Double.parseDouble(_prefs.getString(Preferences.WORKING_RADIUS_MILES, "100.0"));
        radius = radius * 1000 * 1.6;
        latLng = new LatLng(Double.parseDouble(_prefs.getString(Preferences.LATITUDE, "40.712784")),Double.parseDouble(_prefs.getString(Preferences.LONGITUDE,"-74.005941")));
        setWidgets();
        if (getIntent().getExtras() != null) {
            finalRequestParams = (HashMap<String, String>) getIntent().getSerializableExtra("finalRequestParams");
            image_profile = getIntent().getStringExtra("image_profile");
            image_driver = getIntent().getStringExtra("image_driver");
            city = finalRequestParams.get("data[pros][city]");
            zip = finalRequestParams.get("data[pros][zip]");
            txtcode.setText(zip);
        }else {
            isEditing = true ;
        }

        setTypeface();
        setCLickListner();

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
//                latLng = Utilities.getLocationFromAddress(_context,(city + zip).replace(" ","%20"));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                setUpMap();

            }
        }.execute();




    }

    private void setWidgets() {
        txtBack = (TextView) findViewById(R.id.txtBack);
        txtDone = (TextView) findViewById(R.id.txtDone);
        txtWorkingradius = (TextView)findViewById(R.id.txtWorkingradius);
        txthowfar = (TextView)findViewById(R.id.txthowfar);
        txtcode = (TextView)findViewById(R.id.txtcode);
        txtMiles = (TextView)findViewById(R.id.txtMiles);
        txtMiles.setText(Double.parseDouble(_prefs.getString(Preferences.WORKING_RADIUS_MILES, "100.0"))+" Miles");
        txtcode.setText(_prefs.getString(Preferences.ZIP,""));
        ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap ;
            }
        });
    }

    private void setTypeface() {
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");

        txtBack.setTypeface(fontfamily);
        txtDone.setTypeface(fontfamily);
        txtWorkingradius.setTypeface(fontfamily);
        txthowfar.setTypeface(fontfamily);
        txtcode.setTypeface(fontfamily);
        txtMiles.setTypeface(fontfamily);

    }
    private  void setCLickListner(){
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (radius_to_send == null) {
//                    showAlertDialog("Fixd-Pro","please select radius");
//                    return;
//                }
                if (!isEditing) {
                    Intent intent = new Intent(_context, AddBankAccountActivity.class);
                    finalRequestParams.put("data[pros][working_radius_miles]", radius_to_send);
                    intent.putExtra("finalRequestParams", finalRequestParams);
                    intent.putExtra("image_driver", image_driver);
                    intent.putExtra("image_profile", image_profile);
                    startActivity(intent);
                } else {
//                    Update radius and finish
                    GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerSettings, WorkingRadiusActivity.this, "Loading");
                    responseAsync.execute(getSettingRadiusParameters());
                }
            }
        });
        txtMiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHourlyRateDialog();
            }
        });
    }
    ResponseListener responseListenerSettings = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS")){
                    JSONObject pro_settings = Response.getJSONObject("RESPONSE");
                    SharedPreferences.Editor editor = _prefs.edit();


                    editor.commit();
                    handler.sendEmptyMessage(0);
                }else{
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);
                    }

                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    finish();
                    break;
                }
                case 1:{
//                    initSettings();
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }
                default:{
                    break;
                }
            }
        }
    };
    private HashMap<String,String> getSettingRadiusParameters(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "update");
        hashMap.put("object", "pros");
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("data[pros][working_radius_miles]", radius_to_send);
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }
    private void setUpMap() {

        // For showing a move to my loction button
        mMap.setMyLocationEnabled(true);
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

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float)(getZoomLevel(circle) - 0.9)));
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
    private  void showHourlyRateDialog(){
        working_radius = txtMiles.getText().toString().trim();

        final String[] TYPES = new String[]{"25 Miles", "50 Miles","75 Miles","100 Miles","125 Miles","150 Miles","175 Miles","200 Miles"};
        final Double[] RADIUS = new Double[]{25000.00 * 1.6, 50000.00 * 1.6,75000.00 * 1.6,100000.00 * 1.6,125000.00 * 1.6,150000.00 * 1.6,175000.00 * 1.6,200000.00 * 1.6};
        final String[] RADIUS_TO_SEND = new String[]{"25", "50","75","100","125","150","175","200"};
        final Dialog dialog = new Dialog(context);
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
}
