package fixtpro.com.fixtpro.fragment;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import fixtpro.com.fixtpro.HomeScreenNew;
import fixtpro.com.fixtpro.LocationResponseListener;
import fixdpro.com.fixdpro.R;
import fixtpro.com.fixtpro.ResponseListener;
import fixtpro.com.fixtpro.beans.AvailableJobModal;
import fixtpro.com.fixtpro.beans.NotificationModal;
import fixtpro.com.fixtpro.beans.SkillTrade;
import fixtpro.com.fixtpro.singleton.TradeSkillSingleTon;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.CurrentScheduledJobSingleTon;
import fixtpro.com.fixtpro.utilites.GMapV2GetRouteDirection;
import fixtpro.com.fixtpro.utilites.GPSTracker;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.JSONParser;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import com.google.android.gms.maps.model.PolylineOptions;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartJobFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartJobFragment extends Fragment implements OnMapReadyCallback,LocationResponseListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    AvailableJobModal modal = null ;

    private OnFragmentInteractionListener mListener;
        SupportMapFragment mMapFragment = null ;
        private static GoogleMap mMap;
    Document document;
    GMapV2GetRouteDirection v2GetRouteDirection;
    LatLng fromPosition;
    LatLng toPosition;
    LatLng currntPosition;

    MarkerOptions markerOptions;
    Location location ;
    TextView txttime, txtDistance, txttolocation, txtfromlocation;

    private Dialog dialog;
    private  Context _context ;
    Fragment fragment = null ;
    LayoutInflater inflater ;
    ImageLoader imageLoader = null ;
    DisplayImageOptions defaultOptions;
    String error_message = "";
    ImageView imgNavigate, imgYes, imgNo;
    RelativeLayout layout_heading ;
    int PERMISSION_ACCESS_FINE_LOCATION, PERMISSION_ACCESS_COARSE_LOCATION;
    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS_BY_START_JOB_CLASS = 100;

    GPSTracker gpsTracker = null ;
    boolean isnotificationClicked = false;

    public StartJobFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartJobFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartJobFragment newInstance(String param1, String param2) {
        StartJobFragment fragment = new StartJobFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modal = CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal();
        _context  = getActivity() ;

        defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
        gpsTracker = new GPSTracker(getActivity());
        if (getArguments() != null) {
            // handle notification click
            isnotificationClicked = true ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start_job, container, false);
        setWidgets(view);
        v2GetRouteDirection = new GMapV2GetRouteDirection();
        return view;
    }
    private void setWidgets(View v){
        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.location_map, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);


        txtDistance = (TextView)v.findViewById(R.id.txtDistance);
        txttime = (TextView)v.findViewById(R.id.txttime);
        txtfromlocation = (TextView)v.findViewById(R.id.txtfromlocation);
        txttolocation = (TextView)v.findViewById(R.id.txttolocation);
        imgNavigate = (ImageView)v.findViewById(R.id.imgNavigate);
        imgNo = (ImageView)v.findViewById(R.id.imgNo);
        imgYes = (ImageView)v.findViewById(R.id.imgYes);
        CurrentScheduledJobSingleTon.getInstance().LastFragment = Constants.START_JOB_FRAGMENT ;
        layout_heading = (RelativeLayout)v.findViewById(R.id.layout_heading);
        imgNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show open direction Dialog....
                showAlertDialogForDirection("Fixd-Pro", "Open Direction with:");
            }
        });
        imgYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show open direction Dialog....
                layout_heading.setVisibility(View.GONE);
                ((HomeScreenNew) getActivity()).switchFragment(new HomeFragment(), Constants.HOME_FRAGMENT, false, null);
            }
        });


        imgNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_heading.setVisibility(View.GONE);
                if (checkIfProArrived()) {
//                        showStartJobDialog();
                    showNotification();
                } else {
                    getLocationRecursively();
                }
//                // show open direction Dialog....
//                layout_heading.setVisibility(View.GONE);
//                ((HomeScreenNew) getActivity()).switchFragment(new HomeFragment(), Constants.HOME_FRAGMENT, false, null);
            }
        });


    }
    private void showNotification(){
        NotificationModal modal = new NotificationModal();
        modal.setType("pr");// pro reached
        modal.setMessage("You Have Arrived");
        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Date now = new Date();
        long uniqueId = now.getTime();//use date to generate an unique id to differentiate the notifications.
        Intent notificationIntent = new Intent(getActivity(), HomeScreenNew.class);
        notificationIntent.putExtra("data", modal);
        notificationIntent.putExtra("isnoty","yes");
        notificationIntent.setAction("com.fixtconsumer" + uniqueId);

        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(),
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(getActivity());

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle("Fixd")
                .setContentText(modal.getMessage());
        Notification n = builder.build();
        notificationManager.notify((111111 + (int)(Math.random() * 999999)), n);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    // check if user is in range

    private boolean checkIfProArrived(){
        Location loc = new Location("dummyprovider");
        loc.setLatitude(modal.getJob_customer_addresses_latitude());
        loc.setLongitude(modal.getJob_customer_addresses_longitude());//
//        loc.setLatitude(30.710940);
//        loc.setLongitude(76.686212);
        float distance  =gpsTracker.getLocation().distanceTo(loc);

//        Toast.makeText(getActivity(),distance+"",Toast.LENGTH_LONG).show();
//        Toast.makeText(getActivity(),gpsTracker.getLocation().getLongitude()+"",Toast.LENGTH_LONG).show();
        if (distance < 61){
            return true;
        }else {
            return false ;
        }

//        if (true)

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void handleLocationResponse(Location location) {
        // got the location

        if (location != null){
            this.location= location ;
            handler.sendEmptyMessage(0);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.START_JOB_FRAGMENT);
         setupToolBar();
        /**
         * Get Location to show Path on map
         */
        if (Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1) {
            List<String> permissionsNeeded = new ArrayList<String>();
            PERMISSION_ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
            PERMISSION_ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION);
            if (PERMISSION_ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (PERMISSION_ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionsNeeded.size() > 0) {
                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS_BY_START_JOB_CLASS);
            } else {
                ((HomeScreenNew)getActivity()).getLocation(StartJobFragment.this);
            }
        }else {
            ((HomeScreenNew)getActivity()).getLocation(StartJobFragment.this);
        }
        if (isnotificationClicked){
            layout_heading.setVisibility(View.GONE);
            if (checkIfProArrived()){
                showStartJobDialog();

            }else {
                getLocationRecursively();
            }
        }

    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).setRightToolBarText("Start Job");
        ((HomeScreenNew)getActivity()).setTitletext(modal.getContact_name() + " - " + modal.getJob_appliances_arrlist().get(0).getAppliance_type_name()+"..");
        ((HomeScreenNew)getActivity()).setLeftToolBarImage(R.drawable.menu_icon);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap ;
        setUpMap();
    }
    private void setUpMap() {
        // Enabling MyLocation in Google Map
//        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        markerOptions = new MarkerOptions();
    }


    /**
     *
     *
     * This class Get Route on the map
     *
     */
    private class GetRouteTask extends AsyncTask<String, Void, String> {

        private Dialog dialog;
        String response = "";
        @Override
        protected void onPreExecute() {
            txttolocation.setText(modal.getJob_customer_addresses_address() +" - "+modal.getJob_customer_addresses_address_2());
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_progress_simple);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values

            response = v2GetRouteDirection.getDocument(fromPosition, toPosition, GMapV2GetRouteDirection.MODE_DRIVING);;
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            mMap.clear();
            if (response == null)
                return;
            if (response.length() == 0 || result.equals("null"))
                return;
            JSONObject jsonObject = null ;
            ArrayList<LatLng> directionPoint = new ArrayList<LatLng>();

            try {
                jsonObject = new JSONObject(response);
                String distance = "";
                String duration = "";
                String location = "Your Location";
                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                if (jsonArray.length() > 0){
                    JSONObject object = jsonArray.getJSONObject(0);
                    JSONArray legs = object.getJSONArray("legs");
                    if (legs.length() > 0){
                        JSONObject legObject = legs.getJSONObject(0);
                         distance = legObject.getJSONObject("distance").getString("text");
                         duration = legObject.getJSONObject("duration").getString("text");
                         location =legObject.getString("start_address");
                        JSONArray steps = legObject.getJSONArray("steps");
                        for (int i = 0 ; i < steps.length() ; i++){
//                            directionPoint.add(new LatLng(steps.getJSONObject(i).getJSONObject("start_location").getDouble("lat"),steps.getJSONObject(i).getJSONObject("start_location").getDouble("lng")));
//                            directionPoint.add(new LatLng(steps.getJSONObject(i).getJSONObject("end_location").getDouble("lat"),steps.getJSONObject(i).getJSONObject("end_location").getDouble("lng")));
                            directionPoint.addAll(decodePoly(steps.getJSONObject(i).getJSONObject("polyline").getString("points")));
                        }
                    }
                    PolylineOptions rectLine = new PolylineOptions().width(10).color(
                            Color.parseColor("#0098CC"));

                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    // Adding route on the map
                    mMap.addPolyline(rectLine);
                    markerOptions.position(toPosition);
                    markerOptions.draggable(false);
                    mMap.addMarker(markerOptions);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(fromPosition);
                    builder.include(toPosition);
                    final LatLngBounds bounds = builder.build();
                    final int padding = 96; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                    String newDuration = "";
//                    if (document != null){
                        txtDistance.setText("("+distance+")");
                    if (duration.contains("hours")){
                        newDuration = duration.replace("hours","hrs");
                    }
                    if (duration.contains("hour")){
                        newDuration = duration.replace("hour","hr");
                    }
                    if (newDuration.contains("mins")){
                        newDuration = newDuration.replace("mins","ms");
                    }
                    if (newDuration.contains("min")){
                        newDuration = newDuration.replace("min","m");
                    }
                        txtfromlocation.setText(location);
                        txttime.setText(newDuration);
//                        showHeadingTowardsJobDialog();
                    if (!isnotificationClicked)
                        layout_heading.setVisibility(View.VISIBLE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if(response.equalsIgnoreCase("Success")){
//                ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
//                PolylineOptions rectLine = new PolylineOptions().width(15).color(
//                        Color.parseColor("#0098CC"));
//
//                for (int i = 0; i < directionPoint.size(); i++) {
//                    rectLine.add(directionPoint.get(i));
//                }
//                // Adding route on the map
//                mMap.addPolyline(rectLine);
//                markerOptions.position(toPosition);
//                markerOptions.draggable(false);
//                mMap.addMarker(markerOptions);
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                builder.include(fromPosition);
//                builder.include(toPosition);
//                final LatLngBounds bounds = builder.build();
//                final int padding = 96; // offset from edges of the map in pixels
//                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                mMap.animateCamera(cu);
//                Log.e("", "document" + document);
//                if (document != null){
//                    txtDistance.setText(v2GetRouteDirection.getDistanceTextNew(document));
//                    txtfromlocation.setText(v2GetRouteDirection.getStartAddress(document));
//                    txttime.setText(v2GetRouteDirection.getDurationText(document));
//                    txtDistance.setText("("+v2GetRouteDirection.getDistanceText(document)+")");
//                }
////                doSomething(document.getDocumentElement());
//                  txttolocation.setText(modal.getJob_customer_addresses_address() +" - "+modal.getJob_customer_addresses_address_2());
//
//            }

            dialog.dismiss();
            //Show are you heading Dialog...
        }
    }

    private void doSomething(Node node){
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Log.e("", "node-----" + node.getNodeName());
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                doSomething(currentNode);
            }
        }
    }


    private void getLocationRecursively(){

//         Getting Trade Skills on App Start
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(Constants.BASE_URL, "POST", getEnrouteParams());
                if (jsonObject != null) {
                    try {
                        String STATUS = jsonObject.getString("STATUS");
                        if (STATUS.equals("SUCCESS")) {
//                            JSONObject RESPONSE = jsonObject.getJSONObject("RESPONSE");
                               handler.sendEmptyMessage(3);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(3);
                    }

                }

                return null;
            }
        }.execute();
    }


    private HashMap<String,String> getEnrouteParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "update");
        hashMap.put("object", "tech_routes");
        hashMap.put("data[travel_type]","CUSTOMER_HOME");
        hashMap.put("data[job_id]",CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId());
        hashMap.put("data[stream][0][lat]", gpsTracker.getLocation().getLatitude() + "");
        hashMap.put("data[stream][0][lng]", gpsTracker.getLocation().getLongitude() + "");
        hashMap.put("data[stream][0][utime]", System.currentTimeMillis() / 1000 + "");
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        return hashMap;
    }
    public void submitPost(){
        Location loc = new Location("dummyprovider");
        loc.setLatitude(modal.getJob_customer_addresses_latitude());
        loc.setLongitude(modal.getJob_customer_addresses_longitude());

        float distance  = gpsTracker.getLocation().distanceTo(loc);
        String requestedDateStart = "";
        String requestDateEnd = "";
        String currentTime = "";
//        showStartJobDialog();
//        return;
        //check time
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            requestedDateStart = CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getRequest_date() + " " + CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getTimeslot_start();
            requestDateEnd = CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getRequest_date() + " " + CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getTimeslot_end();
            Date startDate = formatter.parse(requestedDateStart);
            Date endDate = formatter.parse(requestDateEnd);
            Date currentDate = formatter.parse( formatter.format(new Date()));
            Log.e("",""+startDate);
            Log.e("",""+endDate);
            Log.e("",""+currentDate);
            if (startDate.before(currentDate) && currentDate.before(endDate)){
                if (distance < 61){
                    showStartJobDialog();
                }else {
                    showAlertDialog("Fixd-Pro","You cannot start job untill you reach the distace between 0 to 60");
                }
            }else {
                 showStartJobAlertDialog("Fixd-Pro", "Are you sure you want to start this job? It is outside of scheduled time.",distance);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
//        if ((requestedDateStart < currentTime) && (currentTime  < requestDateEnd) ){
//            if (distance < 61){
////        if (true)
//                showStartJobDialog();
//        }
//
//        else {
////            showAlertDialog("Fixd-Pro","You cannot start job untill you reach the distace between 0 to 60");
//
//        }
    }
    public void showStartJobDialog(){
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_startjob);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        Button btnStartJob = (Button)dialog.findViewById(R.id.btnStartJob);
        TextView txtUserName = (TextView)dialog.findViewById(R.id.txtUserName);
        TextView txtUserDetails = (TextView)dialog.findViewById(R.id.txtUserDetails);
        txtUserDetails.setText(modal.getJob_customer_addresses_address() + " - "+modal.getJob_customer_addresses_address_2());
        txtUserName.setText(modal.getContact_name());
        btnStartJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                    handler.sendEmptyMessage(2);
                    GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerStartJob, getActivity(), "Loading");
                    responseAsync.execute(getRequestParams());


            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        setUpHorizontalScrollView(dialog);
        dialog.show();
    }
    ResponseListener responseListenerStartJob = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    handler.sendEmptyMessage(2);
                }else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);
                        if (key.equals("181"))
                        {
                            handler.sendEmptyMessage(2);
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
                case 2:{
                    fragment = new InstallorRepairFragment();
                    ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.INSTALL_OR_REPAIR_FRAGMENT, true, null);
                    break;
                }
                case 1:{
                    showAlertDialog("FAILED", error_message);
                    break;
                }
                case 0:{
                    fromPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    toPosition = new LatLng(modal.getJob_customer_addresses_latitude(), modal.getJob_customer_addresses_longitude());
//                    toPosition = new LatLng(30.378179, 76.776697);
                    GetRouteTask getRoute = new GetRouteTask();
                    getRoute.execute();

                    break;
                }
                case 3:{
                    if (checkIfProArrived()){
                        showStartJobDialog();
                    }else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getLocationRecursively();
                            }
                        }, 10000);
                    }

                    break;
                }
            }
        }
    };
    private HashMap<String,String> getRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "start");
        hashMap.put("object", "jobs");
        hashMap.put("data[id]", CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId());
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));

        return hashMap;
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
    private void showStartJobAlertDialog(String Title,String Message, final float dis){
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

                        if (dis < 61) {
                            showStartJobDialog();
                        } else {
                            showAlertDialog("Fixd-Pro", "You cannot start job untill you reach the distace between 0 to 60");
                        }
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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
    private void showAlertDialogForDirection(String Title,String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                _context);

        // set title
        alertDialogBuilder.setTitle(Title);

        // set dialog message
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("Google Map", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + fromPosition.latitude + "," + fromPosition.longitude + "&daddr=" + toPosition.latitude + "," + toPosition.longitude));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Fixd-Pro", new DialogInterface.OnClickListener() {
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
    private void setUpHorizontalScrollView(Dialog dialog) {
        if (modal.getJob_appliances_arrlist().size() > 0) {
            LinearLayout scrollViewLatout = (LinearLayout)dialog.findViewById(R.id.scrollViewLatout);
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < modal.getJob_appliances_arrlist().size(); i++) {
                View child = getLayoutInflater(null).inflate(R.layout.jobs_image_title_item, null);
                View child1 = getLayoutInflater(null).inflate(R.layout.problem_text_item, null);
                TextView txtTitle = (TextView) child.findViewById(R.id.txtTypeTitle);
                TextView txtProblem = (TextView) child1.findViewById(R.id.txtProblem);
                txtProblem.setText(modal.getJob_appliances_arrlist().get(i).getAppliance_type_name() + " " + modal.getJob_appliances_arrlist().get(i).getJob_appliances_service_type() + " - " + modal.getJob_appliances_arrlist().get(i).getJob_appliances_appliance_description());
                final ImageView imgType = (ImageView) child.findViewById(R.id.imgType);
                txtTitle.setText(modal.getJob_appliances_arrlist().get(i).getAppliance_type_name() + "\n" + modal.getJob_appliances_arrlist().get(i).getJob_appliances_service_type());
                scrollViewLatout.addView(child);
                imageLoader.loadImage(modal.getJob_appliances_arrlist().get(i).getImg_original(), defaultOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Do whatever you want with Bitmap
                        imgType.setImageBitmap(loadedImage);
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS_BY_START_JOB_CLASS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ((HomeScreenNew)getActivity()).getLocation(StartJobFragment.this);
//                    startLocationUpdates();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}