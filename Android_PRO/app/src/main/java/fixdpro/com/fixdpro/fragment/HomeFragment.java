package fixdpro.com.fixdpro.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.server.BaseService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import fixdpro.com.fixdpro.AvailableJobListClickActivity;
import fixdpro.com.fixdpro.CalendarActivity;
import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.activities.SetupCompleteAddressActivity;
import fixdpro.com.fixdpro.adapters.JobsPagingAdapter;
import fixdpro.com.fixdpro.adapters.MyInfoWindowAdapter;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNoProgress;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.CheckIfUserVarified;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.HandlePagingResponse;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Singleton;
import fixdpro.com.fixdpro.utilites.Utilities;
import main.java.com.mindscapehq.android.raygun4android.RaygunClient;

public class HomeFragment extends Fragment implements View.OnClickListener{
    MapView mapView;
    GoogleMap map;
    LinearLayout availScheduleLayout, calenderviewall, progressBottomLayout, availableBottomLayout;
    TextView available, scheduled,inProgress, txtStartInAvailable, txtStartInProgress;
    ListView availableJob_listView;
    ListView scheduleJob_listView;
    ListView inProgressJob_list_view;
    public static int pageAvaileble = 1 ;
    public static int pageSheduled = 1 ;
    public static int pageProgress = 1 ;

    private String tab_selected = "Available";

    ArrayList<AvailableJobModal> availablejoblist, schedulejoblist,progressJobList;

    RelativeLayout scheduleLayout, availablelayout, progresslayout;
//    String nextScheduled = "null";
//    String nextAvaileble = "null";
    public static boolean isStateAvailable =  true ;
    String error_message =  "";
    private Context _context = null ;
    Singleton singleton = null ;
    SharedPreferences _prefs = null;
    String role = "pro";
    Fragment fragment = null ;
    private Dialog dialog,dialogSuccess;
    int PERMISSION_ACCESS_FINE_LOCATION, PERMISSION_ACCESS_COARSE_LOCATION;
    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 101;
    List<String> permissionsNeeded = new ArrayList<String>();
    Bundle savedInstanceState = null ;
    private CoordinatorLayout coordinatorLayout;
    JobsPagingAdapter adapterAvailable ;
    JobsPagingAdapter adapterSchedule ;
    JobsPagingAdapter adapterProgress ;
    fixdpro.com.fixdpro.views.CircularProgressView progressView ;
    SwipeRefreshLayout swipe_refresh_layout_schedule, swipe_refresh_layout_available,swipe_refresh_layout_inProgress;
    String switch_tab = "Available", pendingRescheduleJobId = "";
    boolean isFirstTechReg = false ;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        _context = getActivity();
        if (getArguments() != null){
            Bundle b = getArguments();
            if (b.containsKey("switch_tab"))
                switch_tab = b.getString("switch_tab");
            if (b.containsKey("first_tech_reg"))
                isFirstTechReg = b.getBoolean("first_tech_reg");
        }
        RaygunClient.Init(getActivity());
        RaygunClient.AttachExceptionHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        singleton = Singleton.getInstance();
//        this.nextScheduled =  singleton.nextSchduled;
//        this.nextAvaileble =  singleton.nextAvailable;
//        this.pageAvaileble = singleton.pageAvaileble;
//        this.pageSheduled = singleton.pageSheduled;
        _prefs = Utilities.getSharedPreferences(_context);
        role = _prefs.getString(Preferences.ROLE, "pro");
        setWidgets(rootView);
        this.savedInstanceState = savedInstanceState;
        // Check for permissions on runtime for android 6.0
//        if (Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1){
//            PERMISSION_ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(getActivity(),
//                    android.Manifest.permission.ACCESS_FINE_LOCATION);
//            PERMISSION_ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(getActivity(),
//                    android.Manifest.permission.ACCESS_COARSE_LOCATION);
//            if (PERMISSION_ACCESS_FINE_LOCATION !=  PackageManager.PERMISSION_GRANTED)
//                permissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
//            if (PERMISSION_ACCESS_COARSE_LOCATION !=  PackageManager.PERMISSION_GRANTED)
//                permissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
//
//            if (permissionsNeeded.size() > 0){
//                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
//                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
//            }else {
//                setMap(savedInstanceState);
//            }
//        }else {
            setMap(savedInstanceState);
//        }
        setListeners();
        try {
            String token = BaseService.getBaseService().getToken();
            Date expirationDate = BaseService.getBaseService().getTokenExpirationDate();
        } catch (BaseServiceException e) {
            e.printStackTrace();
        }


        availablejoblist = singleton.getAvailablejoblist();
        schedulejoblist = singleton.getSchedulejoblist();;
        progressJobList = singleton.getProgressjoblist();;

        adapterAvailable = new JobsPagingAdapter(getActivity(),availablejoblist,getResources(),pagingResponseAvailable,"Open");
        availableJob_listView.setAdapter(adapterAvailable);

        adapterSchedule = new JobsPagingAdapter(getActivity(),schedulejoblist,getResources(),pagingResponseScheduled,"Schduled");
        scheduleJob_listView.setAdapter(adapterSchedule);

        adapterProgress = new JobsPagingAdapter(getActivity(),progressJobList,getResources(),pagingResponseProgress,"Progress");
        inProgressJob_list_view.setAdapter(adapterProgress);

        availableJob_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // send where details is object

                if (_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.ROLE, "pro").equals("pro")) {
                    showAccountSetupDialog();
                    return;
                }
                if (_prefs.getString(Preferences.IS_VARIFIED, "").equals("0")) {
                    showAlertBackGroundSaftyDialog();
                    return;
                }

                switch_tab = "Available";
//                AvailableJobModal job_detail = new AvailableJobModal();
//                job_detail = availablejoblist.get(position);
                Intent i = new Intent(getActivity(), AvailableJobListClickActivity.class);
                i.putExtra("JOB_DETAIL", availablejoblist.get(position));
                startActivity(i);
            }
        });

        scheduleJob_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch_tab = "Scheduled";
                AvailableJobModal job_detail = new AvailableJobModal();
                job_detail = schedulejoblist.get(position);
                fragment = new ScheduledListDetailsFragment();
//                CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(job_detail);
                Bundle bundle = new Bundle();
                bundle.putSerializable("modal", schedulejoblist.get(position));
                ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
            }
        });
        inProgressJob_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch_tab = "Progress";
                AvailableJobModal job_detail = new AvailableJobModal();
                job_detail = progressJobList.get(position);
                fragment = new ScheduledListDetailsFragment();
//                CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(job_detail);
                Bundle bundle = new Bundle();
                bundle.putSerializable("modal", job_detail);
                ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
            }
        });

        if (false){    //availablejoblist.size() > 0
            handler.sendEmptyMessage(0);
        }else {
            if (getInternetStatus()){
                GetApiResponseAsyncNew responseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerAvailable,iHttpExceptionListener, getActivity(), "Loading");
                responseAsync.execute(getRequestParams("Open"));
            }
        }

        if (_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.ROLE, "pro").equals("pro")) {
            showWelcomeDialog();
            if (availablejoblist.size() == 0){
                availableBottomLayout.setVisibility(View.VISIBLE);
            }
        } else {
            checkIfAnActionsIsPendingForUser();
        }

        if (switch_tab.equals("Scheduled")){
            schedulejoblist.clear();
            onClick(scheduled);
        }
        return rootView;
    }
    private void getNextScheduled(){
        if (!singleton.nextSchduled.equals("null")) {
            pageSheduled = Integer.parseInt(singleton.nextSchduled);
            GetApiResponseAsyncNew responseAsync = new GetApiResponseAsyncNew (Constants.BASE_URL,"POST", responseListenerScheduled,iHttpExceptionListener, getActivity(), "Loading");
            responseAsync.execute(getRequestParams("Scheduled"));
        }
    }
    HandlePagingResponse pagingResponseScheduled = new HandlePagingResponse() {
        @Override
        public void handleChangePage() {
           getNextScheduled();
        }
    };
    HandlePagingResponse pagingResponseAvailable = new HandlePagingResponse() {
        @Override
        public void handleChangePage() {
            if (!singleton.nextAvailable.equals("null")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        progressView.setVisibility(View.VISIBLE);
                    }
                });
             pageAvaileble = Integer.parseInt(singleton.nextAvailable);
            GetApiResponseAsyncNew responseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerAvailable,iHttpExceptionListener, getActivity(), "Loading");
            responseAsync.execute(getRequestParams("Open"));
            }
        }
    };
    HandlePagingResponse pagingResponseProgress = new HandlePagingResponse() {
        @Override
        public void handleChangePage() {
            if (!singleton.nextProgress.equals("null")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        progressView.setVisibility(View.VISIBLE);
                    }
                });
             pageProgress = Integer.parseInt(singleton.nextProgress);
            GetApiResponseAsyncNew responseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerProgress,iHttpExceptionListener, getActivity(), "Loading");
            responseAsync.execute(getRequestParamsPregressJob());
            }
        }
    };
    private HashMap<String,String> getIncompleteAccountParams(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("api", "signup_complete");
        hashMap.put("object", "pros");
        hashMap.put("with_token", "1");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }
    private void showAccountSetupDialog(){
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_hang_tight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        Button btnFinish = (Button)dialog.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetupCompleteAddressActivity.class);
                intent.putExtra("finalRequestParams", getIncompleteAccountParams());
                intent.putExtra("ispro", true);
                intent.putExtra("iscompleting", true);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showWelcomeDialog(){
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_welcom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtNotNow = (TextView)dialog.findViewById(R.id.txtNotNow);
        TextView txtStart = (TextView)dialog.findViewById(R.id.txtStart);
        txtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCompleteAddress();
                dialog.dismiss();
            }
        });
        txtNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void startCompleteAddress(){
        Intent intent = new Intent(getActivity(), SetupCompleteAddressActivity.class);
        intent.putExtra("finalRequestParams", getIncompleteAccountParams());
        intent.putExtra("ispro", true);
        intent.putExtra("iscompleting", true);
        startActivity(intent);
    }

    public void checkIfAnActionsIsPendingForUser(){

        GetApiResponseAsyncNoProgress getApiResponseAsyncNoProgress = new GetApiResponseAsyncNoProgress(Constants.BASE_URL, "POST", new IHttpResponseListener() {
            @Override
            public void handleResponse(JSONObject Response) {
                try {
                    String STATUS = Response.getString("STATUS");
                    if (STATUS.equals("SUCCESS")) {
                        JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject jsonObject = results.getJSONObject(i);
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            HashMap<String, String> dataHashMap = new HashMap<String, String>();
                            for (int j = 0 ; j < dataArray.length() ; j++){
                                JSONObject object = dataArray.getJSONObject(j);
                                String key = object.getString("key");
                                String value = object.getString("value");
                                dataHashMap.put(key, value);
                            }
                            if (dataHashMap.get("t").equals("rjc")){
                                pendingRescheduleJobId = dataHashMap.get("j");
                                handler.sendEmptyMessage(5);
                                break;
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new IHttpExceptionListener() {
                    @Override
                    public void handleException(String exception) {
                    }
                },
                getActivity(),  "Loading");

        getApiResponseAsyncNoProgress.execute(getNotificationParams());


    }

    private HashMap<String, String> getNotificationParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "alerts");
        hashMap.put("select", "^*");
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("order_by", "created_at");
        hashMap.put("order", "DESC");
        hashMap.put("where[is_read]", "0");
        hashMap.put("where[is_active]", "1");
        hashMap.put("_app_id", "FIXD_ANDROID_CONSUMER");
        hashMap.put("_company_id", "FIXD");
//        hashMap.put("page", "1");
//        hashMap.put("per_page", 30 + "");
        return hashMap;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_home_screen, menu);
    }
    public void bellClick(){
        Bundle b = new Bundle();
        fragment = new NotificationListFragment();
        b.putString("title", getString(R.string.nav_item_notifications));
        ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.NOTIFICATION_LIST_FRAGMENT, false, b);
    }
    public void refresh(){
//        if (isStateAvailable){
        if (tab_selected.equals("Available")){
            if (map!= null )
            map.clear();
            pageAvaileble  = 1 ;
            isStateAvailable = true;
            availablejoblist.clear();
            adapterAvailable.notifyDataSetChanged();
            if (getInternetStatus()){
                GetApiResponseAsyncNew responseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerAvailable,iHttpExceptionListener, getActivity(), "Loading");
                responseAsync.execute(getRequestParams("Open"));
            }

        }else if (tab_selected.equals("Scheduled")){
            if (map!= null )
            map.clear();
            pageSheduled  = 1 ;
            isStateAvailable = false;
            schedulejoblist.clear();
            adapterSchedule.notifyDataSetChanged();
            if (getInternetStatus()){
                GetApiResponseAsyncNew responseAsync1 = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerScheduled,iHttpExceptionListener, getActivity(), "Loading");
                responseAsync1.execute(getRequestParams("Scheduled"));
            }

        } else {
            if (map!= null )
                map.clear();
            pageProgress  = 1 ;
            isStateAvailable = false;
            progressJobList.clear();
            adapterProgress.notifyDataSetChanged();
            if (getInternetStatus()){
                GetApiResponseAsyncNew responseAsync1 = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerProgress,iHttpExceptionListener, getActivity(), "Loading");
                responseAsync1.execute(getRequestParamsPregressJob());
            }

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            if (isStateAvailable){
                map.clear();
                pageAvaileble  = 1 ;
                availablejoblist.clear();
                if (Utilities.isNetworkAvailable(getActivity())) {
                    GetApiResponseAsyncNew responseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerAvailable,iHttpExceptionListener, getActivity(), "Loading");
                    responseAsync.execute(getRequestParams("Open"));
                }

            }else{
                map.clear();
                pageSheduled  = 1 ;
                schedulejoblist.clear();
                if (Utilities.isNetworkAvailable(getActivity())) {
                    GetApiResponseAsyncNew responseAsync1 = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerScheduled,iHttpExceptionListener, getActivity(), "Loading");
                    responseAsync1.execute(getRequestParams("Scheduled"));
                }

            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void setWidgets(View view){
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.mapview);
        availScheduleLayout = (LinearLayout) view.findViewById(R.id.availschedule_layout);
//        inProgresslayout = (LinearLayout) view.findViewById(R.id.inProgresslayout);
        available = (TextView) view.findViewById(R.id.available);
        scheduled = (TextView) view.findViewById(R.id.scheduled);
        inProgress = (TextView) view.findViewById(R.id.inProgress);
        availableJob_listView = (ListView) view.findViewById(R.id.availableJob_list_view);
        scheduleJob_listView = (ListView) view.findViewById(R.id.scheduleJob_list_view);
        inProgressJob_list_view = (ListView) view.findViewById(R.id.inProgressJob_list_view);
        scheduleLayout = (RelativeLayout) view.findViewById(R.id.schedulelayout);
        progresslayout = (RelativeLayout) view.findViewById(R.id.progresslayout);
        availablelayout = (RelativeLayout) view.findViewById(R.id.availablelayout);
        availableBottomLayout = (LinearLayout) view.findViewById(R.id.layout_available_bottom);
        progressBottomLayout = (LinearLayout) view.findViewById(R.id.layout_progress_bottom);
        txtStartInAvailable = (TextView) view.findViewById(R.id.txt_start_available);
        txtStartInProgress = (TextView) view.findViewById(R.id.txt_start_progress);
        calenderviewall = (LinearLayout) view.findViewById(R.id.viewall);
        swipe_refresh_layout_available = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_available);
        swipe_refresh_layout_schedule = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_schedule);
        swipe_refresh_layout_inProgress = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_inProgress);
//        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id
//                .coordinatorLayout);
//        progressView = (fixtpro.com.fixtpro.views.CircularProgressView)view.findViewById(R.id.progress_view);
    }

    void setMap(Bundle savedInstanceState){
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap ;
                initMap();
            }
        });
//        map.getUiSettings().setMyLocationButtonEnabled(false);

    }


    private void initMap(){
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());
        map.setInfoWindowAdapter(new MyInfoWindowAdapter(getActivity()));
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int pos = Integer.parseInt(marker.getSnippet());
                if (tab_selected.equals("Available")) {
                    // send where details is object
                    if (_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.ROLE, "pro").equals("pro")) {
                        showAccountSetupDialog();

                    }
                    if (_prefs.getString(Preferences.IS_VARIFIED, "").equals("0")) {
                        showAlertBackGroundSaftyDialog();

                    }
                    AvailableJobModal job_detail = new AvailableJobModal();
                    job_detail = availablejoblist.get(pos);
                    Intent i = new Intent(getActivity(), AvailableJobListClickActivity.class);
                    i.putExtra("JOB_DETAIL", job_detail);
                    startActivity(i);
                } else if (tab_selected.equals("Scheduled")){
                    AvailableJobModal job_detail = new AvailableJobModal();
                    job_detail = schedulejoblist.get(pos);
                    fragment = new ScheduledListDetailsFragment();
//                CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(job_detail);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("modal", job_detail);
                    ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
                }else {
                    AvailableJobModal job_detail = new AvailableJobModal();
                    job_detail = progressJobList.get(pos);
                    fragment = new ScheduledListDetailsFragment();
//                CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(job_detail);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("modal", job_detail);
                    ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
                }

            }
        });
    }
    void setListeners(){
        available.setOnClickListener(this);
        scheduled.setOnClickListener(this);
        inProgress.setOnClickListener(this);
        calenderviewall.setOnClickListener(this);
        txtStartInProgress.setOnClickListener(this);
        txtStartInAvailable.setOnClickListener(this);
        swipe_refresh_layout_schedule.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh_layout_schedule.setRefreshing(true);

                refresh();
            }
        });
        swipe_refresh_layout_available.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh_layout_available.setRefreshing(true);

                refresh();
            }
        });
        swipe_refresh_layout_inProgress.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh_layout_inProgress.setRefreshing(true);

                refresh();
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
//        if (mapView.isActivated())
        mapView.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.HOME_FRAGMENT);
        setupToolBar();
        adapterAvailable.notifyDataSetChanged();
        adapterSchedule.notifyDataSetChanged();
        int countNormal = _prefs.getInt(Preferences.NORMAL_NOTI_COUNT,0);
        if (countNormal > 0){
            ((HomeScreenNew)getActivity()).setBadgeText(countNormal+"");
        }else {
            ((HomeScreenNew)getActivity()).hideBadge();
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mGcmPushReceiver, new IntentFilter("new_job_available_notification"));
        // check if user varified
        if (!_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.IS_VARIFIED, "").equals("0")){
            new  CheckIfUserVarified(getActivity());
        }
        super.onResume();
    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).setRightToolBarImage(R.drawable.white_bell);
        ((HomeScreenNew)getActivity()).setTitletext("Welcome "+_prefs.getString(Preferences.FIRST_NAME,"") +"!");
        ((HomeScreenNew)getActivity()).setLeftToolBarImage(R.drawable.menu_icon);
    }
    private BroadcastReceiver mGcmPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("","");
            adapterAvailable.notifyDataSetChanged();
            onClick(available);
        }
    };
    @Override
    public void onPause() {
        ((HomeScreenNew)getActivity()).hideBadge();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                mGcmPushReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null)
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
        mapView.onLowMemory();
    }

    IHttpResponseListener responseListenerAvailable = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
                    singleton.nextAvailable = pagination.getString("next");
                    for(int i = 0; i < results.length(); i++){
                        JSONObject obj = results.getJSONObject(i);
                        AvailableJobModal model = new AvailableJobModal();
                        model.setContact_name(obj.getString("contact_name"));
                        model.setCreated_at(obj.getString("created_at"));
                        model.setCustomer_id(obj.getString("customer_id"));
                        model.setCustomer_notes(obj.getString("customer_notes"));
                        model.setFinished_at(obj.getString("finished_at"));
                        model.setId(obj.getString("id"));
                        model.setJob_id(obj.getString("job_id"));
//                        model.setLatitude(obj.getDouble("latitude"));
                        model.setLocked_by(obj.getString("locked_by"));
                        model.setLocked_on(obj.getString("locked_on"));
//                        model.setLongitude(obj.getDouble("longitude"));
                        model.setPhone(obj.getString("phone"));
                        model.setPro_id(obj.getString("pro_id"));
                        model.setRequest_date(obj.getString("request_date"));
//                        model.setService_id(obj.getString("service_id"));
//                        model.setService_type(obj.getString("service_type"));
                        model.setStarted_at(obj.getString("started_at"));
                        model.setStatus(obj.getString("status"));
                        model.setTechnician_id(obj.getString("technician_id"));
                        model.setTime_slot_id(obj.getString("time_slot_id"));
                        model.setTitle(obj.getString("title"));
//                        model.setTotal_cost(obj.getString("total_cost"));
                        model.setUpdated_at(obj.getString("updated_at"));
//                        model.setWarranty(obj.getString("warranty"));
//                      if(Utilities.getSharedPreferences(getContext()).getString(Preferences.ROLE, null).equals("pro")) {
                            JSONArray jobAppliances = obj.getJSONArray("job_appliances");
                            ArrayList<JobAppliancesModal> jobapplianceslist = new ArrayList<JobAppliancesModal>();
                            for (int j = 0; j < jobAppliances.length(); j++) {
                                JSONObject jsonObject = jobAppliances.getJSONObject(j);
                                JobAppliancesModal mod = new JobAppliancesModal();
                                mod.setJob_appliances_job_id(jsonObject.getString("job_id"));
                                mod.setJob_appliances_appliance_id(jsonObject.getString("appliance_id"));
                                mod.setJob_appliances_brand_name(jsonObject.getString("brand_name"));

                                if (!jsonObject.isNull("description")){
                                    mod.setJob_appliances_appliance_description(jsonObject.getString("description"));
                                }
                                if (!jsonObject.isNull("service_type")){
                                    mod.setJob_appliances_service_type(jsonObject.getString("service_type"));
                                }
                                if (!jsonObject.isNull("customer_complaint")) {
                                    mod.setJob_appliances_customer_compalint(jsonObject.getString("customer_complaint"));
                                }
                                if (!jsonObject.isNull("power_source")) {
                                    mod.setJob_appliances_power_source(jsonObject.getString("power_source"));
                                }
                                if (!jsonObject.isNull("image")){
                                    JSONObject image_obj = jsonObject.getJSONObject("image");
                                    if(!image_obj.isNull("original")){
                                        mod.setImg_original(image_obj.getString("original"));
//                                        mod.setImg_160x170(image_obj.getString("160x170"));
//                                        mod.setImg_150x150(image_obj.getString("150x150"));
//                                        mod.setImg_75x75(image_obj.getString("75x75"));
//                                        mod.setImg_30x30(image_obj.getString("30x30"));
                                    }
                                }
                                if (!jsonObject.isNull("appliance_types")){
                                    JSONObject appliance_type_obj = jsonObject.getJSONObject("appliance_types");
                                    mod.setAppliance_type_id(appliance_type_obj.getString("id"));
                                    mod.setAppliance_type_has_power_source(appliance_type_obj.getString("has_power_source"));
                                    mod.setAppliance_type_service_id(appliance_type_obj.getString("service_id"));
                                    mod.setAppliance_type_name(appliance_type_obj.getString("name"));
                                    mod.setAppliance_type_soft_deleted(appliance_type_obj.getString("_soft_deleted"));
                                    if (!appliance_type_obj.isNull("image")){
                                        JSONObject image_obj = appliance_type_obj.getJSONObject("image");
                                        if(!image_obj.isNull("original")){
                                            mod.setAppliance_type_image_original(image_obj.getString("original"));

                                        }
                                    }
                                    if (!appliance_type_obj.isNull("services")){
                                        JSONObject services_obj = appliance_type_obj.getJSONObject("services");
                                        mod.setService_id(services_obj.getString("id"));
                                        mod.setService_name(services_obj.getString("name"));
                                        mod.setService_created_at(services_obj.getString("created_at"));
                                        mod.setService_updated_at(services_obj.getString("updated_at"));
                                    }

                                }

                                jobapplianceslist.add(mod);
//                            }
                            model.setJob_appliances_arrlist(jobapplianceslist);
                        }
                        if (!obj.isNull("time_slots")){
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            model.setTime_slot_id(time_slot_obj.getString("id"));
                            model.setTimeslot_start(time_slot_obj.getString("start"));
                            model.setTimeslot_end(time_slot_obj.getString("end"));
                            model.setTimeslot_name(time_slot_obj.getString("name"));
                            model.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));
                        }


                        if (!obj.isNull("job_customer_addresses")){
                            JSONObject job_customer_addresses_obj = obj.getJSONObject("job_customer_addresses");
                            model.setJob_customer_addresses_id(job_customer_addresses_obj.getString("id"));
                            model.setJob_customer_addresses_zip(job_customer_addresses_obj.getString("zip"));
                            model.setJob_customer_addresses_city(job_customer_addresses_obj.getString("city"));
                            model.setJob_customer_addresses_state(job_customer_addresses_obj.getString("state"));
                            model.setJob_customer_addresses_address(job_customer_addresses_obj.getString("address"));
                            model.setJob_customer_addresses_address_2(job_customer_addresses_obj.getString("address_2"));
                            model.setJob_customer_addresses_updated_at(job_customer_addresses_obj.getString("updated_at"));
                            model.setJob_customer_addresses_created_at(job_customer_addresses_obj.getString("created_at"));
                            model.setJob_customer_addresses_job_id(job_customer_addresses_obj.getString("job_id"));
                            model.setJob_customer_addresses_latitude(job_customer_addresses_obj.getDouble("latitude"));
                            model.setJob_customer_addresses_longitude(job_customer_addresses_obj.getDouble("longitude"));
                        }
                        availablejoblist.add(model);
                    }
                    handler.sendEmptyMessage(0);
                }else {
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
            if (swipe_refresh_layout_available.isRefreshing()){
                swipe_refresh_layout_available.setRefreshing(false);
            }
            if (swipe_refresh_layout_schedule.isRefreshing()){
                swipe_refresh_layout_schedule.setRefreshing(false);
            } if (swipe_refresh_layout_inProgress.isRefreshing()){
                swipe_refresh_layout_inProgress.setRefreshing(false);
            }
            switch (msg.what){
                case 0:{
                    adapterAvailable.notifyDataSetChanged();
                    setMarkers(availablejoblist);
                    if (_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.ROLE, "pro").equals("pro") && availablejoblist.size() == 0) {
                        availableBottomLayout.setVisibility(View.VISIBLE);
                    } else {
                        availableBottomLayout.setVisibility(View.GONE);
                    }
//                    if (_prefs.getBoolean(Preferences.IS_ACCOUNT_SETUP_COMPLETD_SHOWED,false)){
//                        showAccountSetupCompletdDialog();
//                    }
                    if (isFirstTechReg){
                        showDialogVerificaitonTokenSuccess();
                    }
                    break;
                }
                case 1:{
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }
                case 2:{
                    adapterSchedule.notifyDataSetChanged();
                    setMarkers(schedulejoblist);
                    break;
                }
                case 3:{
                    break;
                } case 4:{
                    adapterProgress.notifyDataSetChanged();
                    setMarkers(progressJobList);
                    if (_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.ROLE, "pro").equals("pro") && progressJobList.size() == 0) {
                        progressBottomLayout.setVisibility(View.VISIBLE);
                    } else {
                        progressBottomLayout.setVisibility(View.GONE);
                    }
                    break;
                }
                case 5:{
                    AvailableJobModal job_detail = ((HomeScreenNew)getActivity()).getJobforId(pendingRescheduleJobId);
                    if (job_detail != null) {
                        ScheduledListDetailsFragment fragment = new ScheduledListDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("modal", job_detail);
                        ((HomeScreenNew)getActivity()).switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
                    }
                }
            }
        }
    };

    IHttpExceptionListener iHttpExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception ;
            handler.sendEmptyMessage(1);
        }
    };
    IHttpResponseListener responseListenerScheduled = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
                    singleton.nextSchduled = pagination.getString("next");
                    for(int i = 0; i < results.length(); i++){
                        JSONObject obj = results.getJSONObject(i);
                        AvailableJobModal model = new AvailableJobModal();
                        model.setContact_name(obj.getString("contact_name"));
                        model.setCreated_at(obj.getString("created_at"));
                        model.setCustomer_id(obj.getString("customer_id"));
                        model.setCustomer_notes(obj.getString("customer_notes"));
                        model.setFinished_at(obj.getString("finished_at"));
                        model.setId(obj.getString("id"));
                        model.setJob_id(obj.getString("job_id"));
//                        model.setLatitude(obj.getDouble("latitude"));
                        model.setLocked_by(obj.getString("locked_by"));
                        model.setLocked_on(obj.getString("locked_on"));
//                        model.setLongitude(obj.getDouble("longitude"));
                        model.setPhone(obj.getString("phone"));
                        model.setPro_id(obj.getString("pro_id"));
                        model.setRequest_date(obj.getString("request_date"));
//                        model.setService_id(obj.getString("service_id"));
//                        model.setService_type(obj.getString("service_type"));
                        model.setStarted_at(obj.getString("started_at"));
                        model.setStatus(obj.getString("status"));
                        model.setTechnician_id(obj.getString("technician_id"));
                        model.setTime_slot_id(obj.getString("time_slot_id"));
                        model.setTitle(obj.getString("title"));
//                        model.setTotal_cost(obj.getString("total_cost"));
                        model.setUpdated_at(obj.getString("updated_at"));
//                        model.setWarranty(obj.getString("warranty"));
                        model.setIs_claim(obj.getString("is_claim"));
//                        if(Utilities.getSharedPreferences(getContext()).getString(Preferences.ROLE, null).equals("pro")) {
                            JSONArray jobAppliances = obj.getJSONArray("job_appliances");
                            ArrayList<JobAppliancesModal>  jobapplianceslist = new ArrayList<JobAppliancesModal>();
                            if(jobAppliances != null){
                            for (int j = 0; j < jobAppliances.length(); j++) {
                                JSONObject jsonObject = jobAppliances.getJSONObject(j);
                                JobAppliancesModal mod = new JobAppliancesModal();
                                mod.setJob_appliances_id(jsonObject.getString("id"));
                                mod.setJob_appliances_job_id(jsonObject.getString("job_id"));
                                mod.setJob_appliances_appliance_id(jsonObject.getString("appliance_id"));
                                mod.setJob_appliances_brand_name(jsonObject.getString("brand_name"));

                                if (!jsonObject.isNull("description")){
                                    mod.setJob_appliances_appliance_description(jsonObject.getString("description"));
                                }
                                if (!jsonObject.isNull("service_type")){
                                    mod.setJob_appliances_service_type(jsonObject.getString("service_type"));
                                }
                                if (!jsonObject.isNull("customer_complaint")) {
                                    mod.setJob_appliances_customer_compalint(jsonObject.getString("customer_complaint"));
                                }
                                if (!jsonObject.isNull("image")){
                                    JSONObject image_obj = jsonObject.getJSONObject("image");
                                    if(!image_obj.isNull("original")){
                                        mod.setImg_original(image_obj.getString("original"));
                                        mod.setImg_160x170(image_obj.getString("160x170"));
                                        mod.setImg_150x150(image_obj.getString("150x150"));
                                        mod.setImg_75x75(image_obj.getString("75x75"));
                                        mod.setImg_30x30(image_obj.getString("30x30"));
                                    }
                                }
                                if (!jsonObject.isNull("appliance_types")){
                                    JSONObject appliance_type_obj = jsonObject.getJSONObject("appliance_types");
                                    mod.setAppliance_type_id(appliance_type_obj.getString("id"));
                                    mod.setAppliance_type_has_power_source(appliance_type_obj.getString("has_power_source"));
                                    mod.setAppliance_type_service_id(appliance_type_obj.getString("service_id"));
                                    mod.setAppliance_type_name(appliance_type_obj.getString("name"));
                                    mod.setAppliance_type_soft_deleted(appliance_type_obj.getString("_soft_deleted"));
                                    if (!appliance_type_obj.isNull("image")){
                                        JSONObject image_obj = appliance_type_obj.getJSONObject("image");
                                        if( !image_obj.isNull("original")){
                                            mod.setAppliance_type_image_original(image_obj.getString("original"));

                                        }
                                    }
                                    if (!appliance_type_obj.isNull("services")){
                                        JSONObject services_obj = appliance_type_obj.getJSONObject("services");
                                        mod.setService_id(services_obj.getString("id"));
                                        mod.setService_name(services_obj.getString("name"));
                                        mod.setService_created_at(services_obj.getString("created_at"));
                                        mod.setService_updated_at(services_obj.getString("updated_at"));
                                    }

                                }



                                jobapplianceslist.add(mod);
                            }
//                            }
                            model.setJob_appliances_arrlist(jobapplianceslist);
                        }
                        if (!obj.isNull("technicians")){
                            JSONObject technician_object  =  obj.getJSONObject("technicians");
                            model.setTechnician_technicians_id(technician_object.getString("id"));
                            model.setTechnician_user_id(technician_object.getString("user_id"));
                            model.setTechnician_fname(technician_object.getString("first_name"));
                            model.setTechnician_lname(technician_object.getString("last_name"));
                            model.setTechnician_pickup_jobs(technician_object.getString("pickup_jobs"));
                            model.setTechnician_avg_rating(technician_object.getString("avg_rating"));
                            model.setCurrent_technician_scheduled_job_count("1");
                            model.setTechnician_scheduled_job_count(technician_object.getString("scheduled_jobs_count"));
                            model.setTechnician_completed_job_count(technician_object.getString("completed_jobs_count"));
                            if (!technician_object.isNull("profile_image")){
                                JSONObject object_profile_image  = technician_object.getJSONObject("profile_image");
                                if (!object_profile_image.isNull("original"))
                                    model.setTechnician_profile_image(object_profile_image.getString("original"));
                            }

                        }
                        if (!obj.isNull("time_slots")){
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            model.setTime_slot_id(time_slot_obj.getString("id"));
                            model.setTimeslot_start(time_slot_obj.getString("start"));
                            model.setTimeslot_name(time_slot_obj.getString("name"));
                            model.setTimeslot_end(time_slot_obj.getString("end"));
                            model.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));
                        }


                        if (!obj.isNull("job_customer_addresses")){
                            JSONObject job_customer_addresses_obj = obj.getJSONObject("job_customer_addresses");
                            model.setJob_customer_addresses_id(job_customer_addresses_obj.getString("id"));
                            model.setJob_customer_addresses_zip(job_customer_addresses_obj.getString("zip"));
                            model.setJob_customer_addresses_city(job_customer_addresses_obj.getString("city"));
                            model.setJob_customer_addresses_state(job_customer_addresses_obj.getString("state"));
                            model.setJob_customer_addresses_address(job_customer_addresses_obj.getString("address"));
                            model.setJob_customer_addresses_address_2(job_customer_addresses_obj.getString("address_2"));
                            model.setJob_customer_addresses_updated_at(job_customer_addresses_obj.getString("updated_at"));
                            model.setJob_customer_addresses_created_at(job_customer_addresses_obj.getString("created_at"));
                            model.setJob_customer_addresses_job_id(job_customer_addresses_obj.getString("job_id"));
                            model.setJob_customer_addresses_latitude(job_customer_addresses_obj.getDouble("latitude"));
                            model.setJob_customer_addresses_longitude(job_customer_addresses_obj.getDouble("longitude"));
                        }
                        schedulejoblist.add(model);
                    }

                    handler.sendEmptyMessage(2);
                }else {
                    handler.sendEmptyMessage(3);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    IHttpResponseListener responseListenerProgress = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
                    singleton.nextProgress = pagination.getString("next");
                    progressJobList.clear();
                    for(int i = 0; i < results.length(); i++){
                        JSONObject obj = results.getJSONObject(i);
                        AvailableJobModal model = new AvailableJobModal();
                        model.setContact_name(obj.getString("contact_name"));
                        model.setCreated_at(obj.getString("created_at"));
                        model.setCustomer_id(obj.getString("customer_id"));
                        model.setCustomer_notes(obj.getString("customer_notes"));
                        model.setFinished_at(obj.getString("finished_at"));
                        model.setId(obj.getString("id"));
                        model.setJob_id(obj.getString("job_id"));
//                        model.setLatitude(obj.getDouble("latitude"));
                        model.setLocked_by(obj.getString("locked_by"));
                        model.setLocked_on(obj.getString("locked_on"));
//                        model.setLongitude(obj.getDouble("longitude"));
                        model.setPhone(obj.getString("phone"));
                        model.setPro_id(obj.getString("pro_id"));
                        model.setRequest_date(obj.getString("request_date"));
//                        model.setService_id(obj.getString("service_id"));
//                        model.setService_type(obj.getString("service_type"));
                        model.setStarted_at(obj.getString("started_at"));
                        model.setStatus(obj.getString("status"));
                        model.setTechnician_id(obj.getString("technician_id"));
                        model.setTime_slot_id(obj.getString("time_slot_id"));
                        model.setTitle(obj.getString("title"));
//                        model.setTotal_cost(obj.getString("total_cost"));
                        model.setUpdated_at(obj.getString("updated_at"));
//                        model.setWarranty(obj.getString("warranty"));
                        model.setIs_claim(obj.getString("is_claim"));
                        if (!obj.isNull("customers")){
                            if (!obj.getJSONObject("customers").isNull("users")){
                                if (obj.getJSONObject("customers").getJSONObject("users").getString("company_id").equals("FE")){
                                    model.setIs_fe_job("1");
                                } else {
                                    model.setIs_fe_job("0");
                                }
                            }
                        }
//                        if(Utilities.getSharedPreferences(getContext()).getString(Preferences.ROLE, null).equals("pro")) {
                            JSONArray jobAppliances = obj.getJSONArray("job_appliances");
                            ArrayList<JobAppliancesModal>  jobapplianceslist = new ArrayList<JobAppliancesModal>();
                            if(jobAppliances != null){
                            for (int j = 0; j < jobAppliances.length(); j++) {
                                JSONObject jsonObject = jobAppliances.getJSONObject(j);
                                JobAppliancesModal mod = new JobAppliancesModal();
                                mod.setJob_appliances_id(jsonObject.getString("id"));
                                mod.setJob_appliances_job_id(jsonObject.getString("job_id"));
                                mod.setJob_appliances_appliance_id(jsonObject.getString("appliance_id"));
                                mod.setJob_appliances_brand_name(jsonObject.getString("brand_name"));

                                if (!jsonObject.isNull("description")){
                                    mod.setJob_appliances_appliance_description(jsonObject.getString("description"));
                                }
                                if (!jsonObject.isNull("service_type")){
                                    mod.setJob_appliances_service_type(jsonObject.getString("service_type"));
                                }
                                if (!jsonObject.isNull("customer_complaint")) {
                                    mod.setJob_appliances_customer_compalint(jsonObject.getString("customer_complaint"));
                                }
                                if (!jsonObject.isNull("image")){
                                    JSONObject image_obj = jsonObject.getJSONObject("image");
                                    if(!image_obj.isNull("original")){
                                        mod.setImg_original(image_obj.getString("original"));
                                        mod.setImg_160x170(image_obj.getString("160x170"));
                                        mod.setImg_150x150(image_obj.getString("150x150"));
                                        mod.setImg_75x75(image_obj.getString("75x75"));
                                        mod.setImg_30x30(image_obj.getString("30x30"));
                                    }
                                }
                                if (!jsonObject.isNull("appliance_types")){
                                    JSONObject appliance_type_obj = jsonObject.getJSONObject("appliance_types");
                                    mod.setAppliance_type_id(appliance_type_obj.getString("id"));
                                    mod.setAppliance_type_has_power_source(appliance_type_obj.getString("has_power_source"));
                                    mod.setAppliance_type_service_id(appliance_type_obj.getString("service_id"));
                                    mod.setAppliance_type_name(appliance_type_obj.getString("name"));
                                    mod.setAppliance_type_soft_deleted(appliance_type_obj.getString("_soft_deleted"));
                                    if (!appliance_type_obj.isNull("image")){
                                        JSONObject image_obj = appliance_type_obj.getJSONObject("image");
                                        if( !image_obj.isNull("original")){
                                            mod.setAppliance_type_image_original(image_obj.getString("original"));

                                        }
                                    }
                                    if (!appliance_type_obj.isNull("services")){
                                        JSONObject services_obj = appliance_type_obj.getJSONObject("services");
                                        mod.setService_id(services_obj.getString("id"));
                                        mod.setService_name(services_obj.getString("name"));
                                        mod.setService_created_at(services_obj.getString("created_at"));
                                        mod.setService_updated_at(services_obj.getString("updated_at"));
                                    }

                                }



                                jobapplianceslist.add(mod);
                            }
//                            }
                            model.setJob_appliances_arrlist(jobapplianceslist);
                        }
                        if (!obj.isNull("technicians")){
                            JSONObject technician_object  =  obj.getJSONObject("technicians");
                            model.setTechnician_technicians_id(technician_object.getString("id"));
                            model.setTechnician_user_id(technician_object.getString("user_id"));
                            model.setTechnician_fname(technician_object.getString("first_name"));
                            model.setTechnician_lname(technician_object.getString("last_name"));
                            model.setTechnician_pickup_jobs(technician_object.getString("pickup_jobs"));
                            model.setTechnician_avg_rating(technician_object.getString("avg_rating"));
                            model.setCurrent_technician_scheduled_job_count("1");
                            model.setTechnician_scheduled_job_count(technician_object.getString("scheduled_jobs_count"));
                            model.setTechnician_completed_job_count(technician_object.getString("completed_jobs_count"));
                            if (!technician_object.isNull("profile_image")){
                                JSONObject object_profile_image  = technician_object.getJSONObject("profile_image");
                                if (!object_profile_image.isNull("original"))
                                    model.setTechnician_profile_image(object_profile_image.getString("original"));
                            }

                        }
                        if (!obj.isNull("time_slots")){
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            model.setTime_slot_id(time_slot_obj.getString("id"));
                            model.setTimeslot_start(time_slot_obj.getString("start"));
                            model.setTimeslot_name(time_slot_obj.getString("name"));
                            model.setTimeslot_end(time_slot_obj.getString("end"));
                            model.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));
                        }


                        if (!obj.isNull("job_customer_addresses")){
                            JSONObject job_customer_addresses_obj = obj.getJSONObject("job_customer_addresses");
                            model.setJob_customer_addresses_id(job_customer_addresses_obj.getString("id"));
                            model.setJob_customer_addresses_zip(job_customer_addresses_obj.getString("zip"));
                            model.setJob_customer_addresses_city(job_customer_addresses_obj.getString("city"));
                            model.setJob_customer_addresses_state(job_customer_addresses_obj.getString("state"));
                            model.setJob_customer_addresses_address(job_customer_addresses_obj.getString("address"));
                            model.setJob_customer_addresses_address_2(job_customer_addresses_obj.getString("address_2"));
                            model.setJob_customer_addresses_updated_at(job_customer_addresses_obj.getString("updated_at"));
                            model.setJob_customer_addresses_created_at(job_customer_addresses_obj.getString("created_at"));
                            model.setJob_customer_addresses_job_id(job_customer_addresses_obj.getString("job_id"));
                            model.setJob_customer_addresses_latitude(job_customer_addresses_obj.getDouble("latitude"));
                            model.setJob_customer_addresses_longitude(job_customer_addresses_obj.getDouble("longitude"));
                        }
                        progressJobList.add(model);
                    }

                    handler.sendEmptyMessage(4);
                }else {
                    handler.sendEmptyMessage(3);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private boolean getInternetStatus(){
        if (!Utilities.isNetworkAvailable(getActivity())){
//            Snackbar snackbar = Snackbar
//                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
//                    .setAction("OK", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                        }
//                    });
//
//            // Changing message text color
//            snackbar.setActionTextColor(Color.parseColor("#fa7507"));
//
//            // Changing action button text color
//            View sbView = snackbar.getView();
//            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//            textView.setTextColor(Color.WHITE);
//
//            snackbar.show();
            return false;
        }else {
            return true ;
        }
    }
    private HashMap<String,String> getRequestParams(String Status){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        if (Status.equals("Open")){
            hashMap.put("api","read_open");
            hashMap.put("order_by", "created_at");
            hashMap.put("order", "DESC");
        }

        else {
            hashMap.put("api","read");
            if (!Status.equals("Scheduled")){
                hashMap.put("where[status]", Status);

            }
            else{
                hashMap.put("where[status@NOT_IN]", "Complete,Open,Canceled");
                hashMap.put("order_by", "date_time_combined");
                hashMap.put("order", "ASC");
            }
        }
        hashMap.put("object","jobs");

        if (!role.equals("pro"))
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.services.^*,job_appliances.appliance_types.^*,time_slots.^*,job_customer_addresses.^*,customers.users.company_id");
        else
            hashMap.put("select", "^*,job_appliances.^*,technicians.^*,job_appliances.appliance_types.services.^*,job_appliances.appliance_types.^*,time_slots.^*,job_customer_addresses.^*,customers.users.company_id");
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        if (isStateAvailable)
        hashMap.put("page", pageAvaileble+"");
        else
        hashMap.put("page", pageSheduled+"");
        hashMap.put("per_page", "15");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }
    private HashMap<String,String> getRequestParamsPregressJob(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
        hashMap.put("where[status@NOT_IN]", "Complete,Open,Canceled,Scheduled");
        hashMap.put("order_by", "date_time_combined");
        hashMap.put("order", "ASC");
        hashMap.put("object","jobs");
        if (!role.equals("pro"))
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.services.^*,job_appliances.appliance_types.^*,time_slots.^*,job_customer_addresses.^*");
        else
            hashMap.put("select", "^*,job_appliances.^*,technicians.^*,job_appliances.appliance_types.services.^*,job_appliances.appliance_types.^*,time_slots.^*,job_customer_addresses.^*");
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("page", pageProgress+"");
        hashMap.put("per_page", "15");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.available:
//                pageAvaileble = 1;
                if (map != null)
                map.clear();
                isStateAvailable  = true ;
                tab_selected = "Available";
                availScheduleLayout.setBackgroundResource(R.drawable.available);
                progresslayout.setVisibility(View.GONE);
                scheduleLayout.setVisibility(View.GONE);
                availablelayout.setVisibility(View.VISIBLE);
                setMarkers(availablejoblist);
                if (_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.ROLE, "pro").equals("pro") && availablejoblist.size() == 0) {
                    availableBottomLayout.setVisibility(View.VISIBLE);
                } else {
                    availableBottomLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.scheduled:
                tab_selected = "Scheduled";
                availScheduleLayout.setBackgroundResource(R.drawable.scheduled);
                progresslayout.setVisibility(View.GONE);
                scheduleLayout.setVisibility(View.VISIBLE);
                availablelayout.setVisibility(View.GONE);
                refresh();
                break;
            case R.id.inProgress:
                tab_selected = "Progress";
                availScheduleLayout.setBackgroundResource(R.drawable.in_progress);
                progresslayout.setVisibility(View.VISIBLE);
                scheduleLayout.setVisibility(View.GONE);
                availablelayout.setVisibility(View.GONE);
                if (_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.ROLE, "pro").equals("pro")) {
                    progressBottomLayout.setVisibility(View.VISIBLE);
                } else {
                    progressBottomLayout.setVisibility(View.GONE);
                }
                refresh();
                break;
            case R.id.txt_start_available:
            case R.id.txt_start_progress:
                startCompleteAddress();
                break;
            case R.id.viewall:
                Intent i = new Intent(getActivity(), CalendarActivity.class);
                i.putExtra("Rescheduling","0");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
                break;
        }
    }

    public void setMarkers(ArrayList<AvailableJobModal> arrayList){
        if (map == null)
            return;
        double intLatitude = 0, intLongitude = 0;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int k = 0; k < arrayList.size(); k++){
            intLatitude = arrayList.get(k).getJob_customer_addresses_latitude();
            intLongitude = arrayList.get(k).getJob_customer_addresses_longitude();
            // Add a marker
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(intLatitude, intLongitude)));
            if (tab_selected.equals("Scheduled") || tab_selected.equals("Progress")){
                marker.setTitle(arrayList.get(k).getContact_name() +"\n"+ arrayList.get(k).getJob_customer_addresses_address() +" "+arrayList.get(k).getJob_customer_addresses_city());
            }else {
                marker.setTitle(arrayList.get(k).getContact_name());
            }

            marker.setSnippet(k+"");
            builder.include(marker.getPosition());

        }
        if (arrayList.size() > 0){
            final LatLngBounds bounds = builder.build();
            final int padding = 32; // offset from edges of the map in pixels
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    map.animateCamera(cu);
                }
            });
        }

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

    private void showAccountSetupCompletdDialog(){
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_setup_complete);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        Button btnFinish = (Button)dialog.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _prefs.edit().putBoolean(Preferences.IS_ACCOUNT_SETUP_COMPLETD_SHOWED,true).commit();
                dialog.dismiss();
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _prefs.edit().putBoolean(Preferences.IS_ACCOUNT_SETUP_COMPLETD_SHOWED,true).commit();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private  void showDialogVerificaitonTokenSuccess(){
        dialogSuccess = new Dialog(_context);
        dialogSuccess.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSuccess.setContentView(R.layout.dialog_tokensuccess);
        dialogSuccess.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView img_close = (ImageView)dialogSuccess.findViewById(R.id.img_close);
        Button btnFinish = (Button)dialogSuccess.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _prefs.edit().putBoolean(Preferences.IS_ACCOUNT_SETUP_COMPLETD_SHOWED,true).commit();
                dialogSuccess.dismiss();
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _prefs.edit().putBoolean(Preferences.IS_ACCOUNT_SETUP_COMPLETD_SHOWED,true).commit();
                dialogSuccess.dismiss();
            }
        });
        dialogSuccess.show();
    }
    private void showAlertBackGroundSaftyDialog(){
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert_background_check);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        Button btnFinish = (Button)dialog.findViewById(R.id.btnFinish);
        btnFinish.setVisibility(View.GONE);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setMap(savedInstanceState);

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
}