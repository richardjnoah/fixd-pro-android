package fixtpro.com.fixtpro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.paging.listview.PagingListView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixtpro.com.fixtpro.adapters.AvailableJobsPagingAdaper;
import fixtpro.com.fixtpro.adapters.HorizontalScrollApplianceAdapter;
import fixtpro.com.fixtpro.beans.AvailableJobModal;
import fixtpro.com.fixtpro.beans.JobAppliancesModal;
import fixtpro.com.fixtpro.fragment.ProblemImageActivity;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class AvailableJobListClickActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    TextView contactName, address, date, timeinterval, txtDecline, txtPickup;
    ImageView cancel, pickupimg, declineimg, appliance_type_img,transparentImageView;
    AvailableJobModal model;
    private static GoogleMap mMap;
    HorizontalScrollView horizontal_Scroll_view ;
    HorizontalScrollApplianceAdapter horizontalScrollApplianceAdapter = null;
    LinearLayout appliance_layout,scrollViewLatout, layoutServiceDescription ;
    View divider_scrollview;
    LayoutInflater inflater ;
    ImageLoader imageLoader ;
    DisplayImageOptions defaultOptions;
    SupportMapFragment mMapFragment = null ;
    String error_message = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_job_list_click);
//        getSupportActionBar().hide();
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
        setWidgets();
        setListeners();
        model = (AvailableJobModal) getIntent().getSerializableExtra("JOB_DETAIL");
        FixdProApplication.SelectedAvailableJobId = model.getId();
        Log.e("Avail CONTACT NAME PICK", model.getContact_name());

        contactName.setText(model.getContact_name());
        address.setText(model.getJob_customer_addresses_zip() + " - " + model.getJob_customer_addresses_city() + "," + model.getJob_customer_addresses_state());
        date.setText(Utilities.convertDate(model.getRequest_date()));
        timeinterval.setText(Utilities.getFormattedTimeSlots(model.getTimeslot_start()) + " - " + Utilities.getFormattedTimeSlots(model.getTimeslot_end()));

//        setupAppliancesImages();
          setUpApplianceDescription();
    }

    private void setUpMap() {
        // For showing a move to my loction button
//        mMap.setMyLocationEnabled(true);
        // For dropping a marker at a point on the Map
        mMap.addMarker(new MarkerOptions().position(new LatLng(model.getJob_customer_addresses_latitude(), model.getJob_customer_addresses_longitude())).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(model.getJob_customer_addresses_latitude(),
                model.getJob_customer_addresses_longitude()), 12.0f));
    }

    public void setWidgets(){
        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.location_map, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
        cancel = (ImageView) findViewById(R.id.cancel);
        pickupimg = (ImageView) findViewById(R.id.pickupimg);
        declineimg = (ImageView) findViewById(R.id.declineimg);
        contactName = (TextView) findViewById(R.id.contactname);
        address = (TextView) findViewById(R.id.address);
        date = (TextView) findViewById(R.id.date);
        timeinterval = (TextView) findViewById(R.id.timeinterval);
        txtDecline = (TextView) findViewById(R.id.txtDecline);
        txtPickup = (TextView) findViewById(R.id.txtPickup);

        transparentImageView = (ImageView) findViewById(R.id.transparent_image);
        appliance_type_img = (ImageView) findViewById(R.id.appliance_type_img);
        horizontal_Scroll_view = (HorizontalScrollView)findViewById(R.id.horizontal_Scroll_view);
        appliance_layout = (LinearLayout)findViewById(R.id.appliance_layout);
        divider_scrollview =(View)findViewById(R.id.divider_scrollview);
        scrollViewLatout = (LinearLayout)findViewById(R.id.scrollViewLatout);
        layoutServiceDescription = (LinearLayout)findViewById(R.id.layoutServiceDescription);
    }

    public void setListeners(){
        cancel.setOnClickListener(this);
        pickupimg.setOnClickListener(this);
        declineimg.setOnClickListener(this);
        txtDecline.setOnClickListener(this);
        txtPickup.setOnClickListener(this);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_available_job_list_click, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override



    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                finish();
                break;
            case R.id.pickupimg:
                if(Utilities.getSharedPreferences(this).getString(Preferences.ROLE, null).equals("pro"))
                {
                    GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerProPickup, this, "Loading");
                    responseAsync.execute(getRequestParamsPro());
                }else if(Utilities.getSharedPreferences(this).getString(Preferences.ROLE, null).equals("technician")){
                    GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerTechnicianPickup, this, "Loading");
                    responseAsync.execute(getRequestParamsTechnician());
                }
                break;
            case R.id.declineimg:
                Intent i = new Intent(AvailableJobListClickActivity.this, DeclineJobActivity.class);
                i.putExtra("JobType","Available");
                i.putExtra("JobId",model.getId());
                startActivity(i);
                finish();
                break;
            case R.id.txtPickup:
                if(Utilities.getSharedPreferences(this).getString(Preferences.ROLE, null).equals("pro"))
                {
                    GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerProPickup, this, "Loading");
                    responseAsync.execute(getRequestParamsPro());
                }else if(Utilities.getSharedPreferences(this).getString(Preferences.ROLE, null).equals("technician")){
                    GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerTechnicianPickup, this, "Loading");
                    responseAsync.execute(getRequestParamsTechnician());
                }
                break;
            case R.id.txtDecline:
                Intent intent = new Intent(AvailableJobListClickActivity.this, DeclineJobActivity.class);
                intent.putExtra("JobType","Available");
                intent.putExtra("JobId",model.getId());
                startActivity(intent);
                finish();
                break;
        }
    }

    ResponseListener responseListenerTechnicianPickup = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    handler.sendEmptyMessage(1);
                }else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);
                        if (key.equals("168")){
                            handler.sendEmptyMessage(3);
                            return;
                        }
                    }
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    ResponseListener responseListenerProPickup = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    handler.sendEmptyMessage(0);
                }else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);
                        if (key.equals("168")){
                            handler.sendEmptyMessage(3);
                            return;
                        }
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
                    Intent j = new Intent(AvailableJobListClickActivity.this, ConfirmationActivity.class);
                    j.putExtra("JOB_DETAIL",model);
                    startActivity(j);
                    break;
                }
                case 1:{
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }
                case 3:{
                    Intent j = new Intent(AvailableJobListClickActivity.this, BeatActivity.class);
                    startActivity(j);
                    break;
                }
            }
        }
    };
    private void showAlertDialog(String Title,String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
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
    private HashMap<String,String> getRequestParamsPro(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","jobs");
        hashMap.put("object","lock");
        hashMap.put("job_id", model.getId());
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));
        return hashMap;
    }

    private HashMap<String,String> getRequestParamsTechnician(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","pickup_job");
        hashMap.put("object","jobs");
        hashMap.put("data[id] ", model.getId());
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));

        return hashMap;
    }
    private void setupAppliancesImages(){
        ArrayList<JobAppliancesModal> arrayList = model.getJob_appliances_arrlist();
        if (model.getJob_appliances_arrlist().size() > 0){
            inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0 ; i < model.getJob_appliances_arrlist().size() ; i++){
                View child = getLayoutInflater().inflate(R.layout.jobs_image_title_item, null);
                View child1 = getLayoutInflater().inflate(R.layout.problem_text_item, null);
                TextView txtTitle = (TextView)child.findViewById(R.id.txtTypeTitle);
                TextView txtProblem = (TextView)child1.findViewById(R.id.txtProblem);
                txtProblem.setText(model.getJob_appliances_arrlist().get(i).getAppliance_type_name() + " " +model.getJob_appliances_arrlist().get(i).getJob_appliances_service_type() + " - " +model.getJob_appliances_arrlist().get(i).getJob_appliances_appliance_description());
                final ImageView imgType = (ImageView)child.findViewById(R.id.imgType);
                txtTitle.setText(model.getJob_appliances_arrlist().get(i).getAppliance_type_name() + "\n" + model.getJob_appliances_arrlist().get(i).getJob_appliances_service_type());
                scrollViewLatout.addView(child);
//                layout_problem.addView(child1);
                imageLoader.loadImage(model.getJob_appliances_arrlist().get(i).getImg_original(), defaultOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Do whatever you want with Bitmap
                        imgType.setImageBitmap(loadedImage);
                    }
                });
            }
        }else {
            appliance_layout.setVisibility(View.GONE);
            divider_scrollview.setVisibility(View.GONE);
        }
    }
    private void setUpApplianceDescription(){
        final ArrayList<JobAppliancesModal> arrayList = model.getJob_appliances_arrlist();
        if (arrayList.size() > 0){
            inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0 ; i <arrayList.size() ; i++){
                View child = getLayoutInflater().inflate(R.layout.item_available_job_service_desc, null);
                TextView txtServiceType = (TextView)child.findViewById(R.id.txtServiceType);
                TextView txtPowerSourceName = (TextView)child.findViewById(R.id.txtPowerSourceName);
                TextView txtApplianceName = (TextView)child.findViewById(R.id.txtApplianceName);
                TextView txtProblem = (TextView)child.findViewById(R.id.txtProblem);
                TextView txtBrand = (TextView)child.findViewById(R.id.txtBrand);
                TextView txtDesc = (TextView)child.findViewById(R.id.txtDesc);
                View divider = (View)child.findViewById(R.id.divider);
                final ImageView imgShowProblem = (ImageView)child.findViewById(R.id.imgShowProblem);
                imgShowProblem.setTag(i + "");
                txtServiceType.setText( arrayList.get(i).getJob_appliances_service_type() +":");
                txtPowerSourceName.setText(" "+arrayList.get(i).getJob_appliances_power_source());
                txtApplianceName.setText(" "+arrayList.get(i).getAppliance_type_name());
                txtBrand.setText(" "+arrayList.get(i).getJob_appliances_brand_name());
                txtDesc.setText(arrayList.get(i).getJob_appliances_appliance_description());
                txtProblem.setText(" " + arrayList.get(i).getJob_appliances_customer_compalint());
                if (arrayList.get(i).getImg_original().length() == 0)
                    imgShowProblem.setVisibility(View.GONE);
                imgShowProblem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AvailableJobListClickActivity.this,ProblemImageActivity.class);
                        intent.putExtra("problemImageURL", arrayList.get(Integer.parseInt((String)imgShowProblem.getTag())).getImg_original());
                        startActivity(intent);

                    }
                });
                if (i == arrayList.size() - 1)
                    divider.setVisibility(View.GONE);
                layoutServiceDescription.addView(child);
            }
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap ;
        setUpMap();
    }
}
