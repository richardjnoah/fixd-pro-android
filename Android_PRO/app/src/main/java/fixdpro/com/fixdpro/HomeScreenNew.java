package fixdpro.com.fixdpro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import fixdpro.com.fixdpro.activities.SetupCompleteAddressActivity;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.beans.NotificationModal;
import fixdpro.com.fixdpro.fragment.AddServiceFragment;
import fixdpro.com.fixdpro.fragment.ChatUserFragment;
import fixdpro.com.fixdpro.fragment.EquipmentInfoFragment;
import fixdpro.com.fixdpro.fragment.HasPowerSourceFragment;
import fixdpro.com.fixdpro.fragment.HomeFragment;
import fixdpro.com.fixdpro.fragment.InstallorRepairFragment;
import fixdpro.com.fixdpro.fragment.JobSearchFragment;
import fixdpro.com.fixdpro.fragment.MyJobsFragment;
import fixdpro.com.fixdpro.fragment.PartsFragment;
import fixdpro.com.fixdpro.fragment.RepairFragment;
import fixdpro.com.fixdpro.fragment.RepairInfoFragment;
import fixdpro.com.fixdpro.fragment.ScheduledListDetailsFragment;
import fixdpro.com.fixdpro.fragment.SignatureFragment;
import fixdpro.com.fixdpro.fragment.StartJobFragment;
import fixdpro.com.fixdpro.fragment.TellUsWhatsWrongFragment;
import fixdpro.com.fixdpro.fragment.WhatTypeOfServiceFragment;
import fixdpro.com.fixdpro.fragment.WhatsWrongFragment;
import fixdpro.com.fixdpro.fragment.WhichApplianceAddServiceFragment;
import fixdpro.com.fixdpro.fragment.WorkOrderFragment;
import fixdpro.com.fixdpro.gcm_components.MessageReceivingService;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNoProgress;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.ChatService;
import fixdpro.com.fixdpro.utilites.ChatSingleton;
import fixdpro.com.fixdpro.utilites.CheckAlertDialog;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.JSONParser;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Singleton;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.utilites.chat_utils.SharedPreferencesUtil;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//http://stackoverflow.com/questions/13895149/sliding-menu-locks-touch-event-on-upper-view
public class HomeScreenNew extends BaseActivity implements ScheduledListDetailsFragment.OnFragmentInteractionListener, FragmentManager.OnBackStackChangedListener, StartJobFragment.OnFragmentInteractionListener, ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener, InstallorRepairFragment.OnFragmentInteractionListener, AddServiceFragment.OnFragmentInteractionListener, WhatTypeOfServiceFragment.OnFragmentInteractionListener, WhichApplianceAddServiceFragment.OnFragmentInteractionListener, HasPowerSourceFragment.OnFragmentInteractionListener,
        WhatsWrongFragment.OnFragmentInteractionListener, TellUsWhatsWrongFragment.OnFragmentInteractionListener, RepairFragment.OnFragmentInteractionListener, PartsFragment.OnFragmentInteractionListener, WorkOrderFragment.OnFragmentInteractionListener, RepairInfoFragment.OnFragmentInteractionListener, SignatureFragment.OnFragmentInteractionListener,
        ChatUserFragment.OnFragmentInteractionListener, EquipmentInfoFragment.OnFragmentInteractionListener, JobSearchFragment.OnFragmentInteractionListener,
        ResultCallback<LocationSettingsResult> {
    public String currentFragmentTag = "";
    int CONTACTUS_REQUESTCODE = 1;
    private ImageView img_Toggle, img_Right;
    private TextView titletext, txtDone, txtBack, continue_job,badge_home;
    FragmentManager fragmentManager;
    SlidingMenu slidingMenu = null;
    String token = "";
    public static Boolean inBackground = true;

    //    Location Service Variable Declaration
    protected static final String TAG = "location-updates";
    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;


    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;


    protected LocationSettingsRequest mLocationSettingsRequest;

    private LocationResponseListener locationResponseListener = null;

    Fragment continue_job_fragment = null;
    String continue_job_tag = "";
    private Dialog dialog;
    private Context _context = this;
    boolean isChatNoti = false ;
    int count  = 0 ;
    ArrayList<AvailableJobModal>  schedulejoblist;
    ArrayList<AvailableJobModal>  availablejoblist;
    public HomeScreenNew() {
        super();
    }

    SharedPreferences _prefs = null;
    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS_BY_START_JOB_CLASS = 100;
    int PERMISSION_ACCESS_FINE_LOCATION, PERMISSION_ACCESS_COARSE_LOCATION, PERMISSION_PRIORITY_HIGH_ACCURACY;
    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 101;

    Fragment fragment;
    private CoordinatorLayout coordinatorLayout;
    NotificationModal modal = null;
    Singleton singletonJobs = null ;
    AvailableJobModal jobModal = null ;
    CheckAlertDialog checkALert;
    String role = "pro";
    String error_message = "";
    String switch_tab_value = "Available" ;
    boolean isFirstTechReg = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slidingMenu = getSlidingMenu();
        getSlidingMenu().setMode(SlidingMenu.LEFT);
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        setContentView(R.layout.activity_home_screen_new);
        _prefs = Utilities.getSharedPreferences(_context);
        role = _prefs.getString(Preferences.ROLE,"");
        checkALert = new CheckAlertDialog();
        singletonJobs = Singleton.getInstance();
        schedulejoblist = singletonJobs.getSchedulejoblist();;
        availablejoblist = singletonJobs.getAvailablejoblist();;
//        if (Build.VERSION.SDK_INT < 16) { //ye olde method
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        } else { // Jellybean and up, new hotness
//            View decorView = getWindow().getDecorView();
//            // Hide the status bar.
//            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//            // Remember that you should never show the action bar if the
//            // status bar is hidden, so hide that too if necessary.
////            ActionBar actionBar = getActionBar();
////            actionBar.hide();
//        }
        _prefs = Utilities.getSharedPreferences(_context);
        fragmentManager = getSupportFragmentManager();
        setWidgets();
        setListeners();

        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null && bundle1.containsKey("switch_tab")) {
            switch_tab_value = bundle1.getString("switch_tab");
        }
        if (bundle1 != null && bundle1.containsKey("ispro") && !bundle1.getBoolean("bundle1")){
            isFirstTechReg = true;
        }
        initLayout();

       /* String  FilePath = Environment.getExternalStorageDirectory()
                + "/FIXD";
        File file = new File(FilePath +"/FCRAAgreement.pdf");
        if (file.exists()){
            // get file and attach to email

            File file1 = new File(FilePath, "FCRAAgreement.pdf");
            Intent intent = new Intent(Intent.ACTION_SEND ,Uri.parse("mailto:")); // it's not ACTION_SEND
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Card Set ");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file1));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            startActivity(intent);
        }else {
            File Folder = new File(FilePath);
            Folder.mkdir();
            Utilities.copySqltiteFromAssets(HomeScreenNew.this, "FCRAAgreement.pdf", FilePath);
            File file1 = new File(FilePath, "FCRAAgreement.pdf");
            Intent intent = new Intent(Intent.ACTION_SEND ,Uri.parse("mailto:")); // it's not ACTION_SEND
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Card Set ");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file1));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            startActivity(intent);
        }*/

//        / Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
//        if (Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1){
//            PERMISSION_ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION);
//            PERMISSION_ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this,
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
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
//            }
//        }else {
//            buildGoogleApiClient();
//            createLocationRequest();
//            buildLocationSettingsRequest();
//        }
        QBLogin();
        if (bundle1 != null && bundle1.containsKey("isnoty")) {
            modal = (NotificationModal) bundle1.getSerializable("data");
            handleNotificationPopup(false);
        }

    }

    private void handleNotificationPopup(boolean showPopup) {
        String title = "";
        String message = modal.getMessage();
        if (showPopup) {
            if (modal.getType().equals("woa")) {
                title = "Work Order Approved!";
                // define alert...
            }
            if (modal.getType().equals("wod")) {
                title = "Work Order Declined!";
                // define alert...
            }
            if (modal.getType().equals("ja")) {
                title = "Job Available!";
                // define alert...
            }
            if (modal.getType().equals("pjt") || modal.getType().equals("pjp")) {
                title = "Job Picked!";
                // define alert...
            }
            if (modal.getType().equals("ja")) {
                title = "Job Picked!";
                // define alert...
            }
            if (modal.getType().equals("pr")) {
                title = "Pro has Arrived!";
                // define alert...
            }
            if (modal.getType().equals("cja")) {
                title = "Job Appointment Confirmation!";
                // define alert...
            }
            if(modal.getType().equals("cn")){
                int count = _prefs.getInt(Preferences.CHAT_NOTI_COUNT,0);
                count = count + 1;
                _prefs.edit().putInt(Preferences.CHAT_NOTI_COUNT,count).commit() ;
                setNotficationCounts();
                return;
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            //set title
            dialog.setTitle(title);
            // set message...
            dialog.setMessage(message);
            // set button status..onclick
            dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setPositiveButton("View", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    executeNoti();
                }
            });
            AlertDialog alert = dialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
        } else {
            executeNoti();
        }
    }


    private void executeNoti(){
        if (modal.getType().equals("woa") || modal.getType().equals("wod")) {
            Bundle bundle = new Bundle();
            bundle.putString("job_id", modal.getJobId());
            bundle.putString("appliance_id", modal.getJobAppliance());
            switchFragment(new InstallorRepairFragment(), Constants.INSTALL_OR_REPAIR_FRAGMENT, true, bundle);
        }
        if (modal.getType().equals("pr") ) {
            Bundle bundle = new Bundle();

            switchFragment(new StartJobFragment(), Constants.START_JOB_FRAGMENT, true, bundle);
        }
        if (modal.getType().equals("cn")) {
            isChatNoti = true ;
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.dialog_progress_simple, null);
            dialog.setContentView(customView);
            dialog.setCancelable(false);
            dialog.show();

        }
        if (modal.getType().equals("ja")) {
            AvailableJobModal job_detail = getJobforIdAvalable(modal.getJobId());
            if (job_detail != null) {
                Intent i = new Intent(HomeScreenNew.this, AvailableJobListClickActivity.class);
                i.putExtra("JOB_DETAIL", job_detail);
                startActivity(i);

            } else {
                //get the job
                GetApiResponseAsyncNew responseAsync = new  GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerScheduled,exceptionListener, HomeScreenNew.this, "Loading");
                responseAsync.execute(getRequestParams(modal.getJobId(),"read_open"));
            }
        }
        if (modal.getType().equals("pjp") ||modal.getType().equals("pjt") ||modal.getType().equals("cja")) {
            AvailableJobModal job_detail = getJobforId(modal.getJobId());
            if (job_detail != null) {
                ScheduledListDetailsFragment fragment = new ScheduledListDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("modal", job_detail);
                switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
            } else {
                //get the job
                GetApiResponseAsyncNew responseAsync = new  GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerScheduled,exceptionListener, HomeScreenNew.this, "Loading");
                responseAsync.execute(getRequestParams(modal.getJobId(),"read"));
            }
        }
    }
    private HashMap<String, String> getRequestParams(String id,String api) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", api);
        hashMap.put("object", "jobs");
        hashMap.put("expand[0]", "work_order");
        if (!role.equals("pro"))
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*,job_line_items.^*");
        else
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*,job_line_items.^*");
        hashMap.put("where[id]", id + "");
        hashMap.put("token", Utilities.getSharedPreferences(HomeScreenNew.this).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("page", "1");
        hashMap.put("per_page", "20");
        return hashMap;
    }
    private HashMap<String, String> getRequestParamsAvailable(String id) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read_open");
        hashMap.put("object", "jobs");
        hashMap.put("expand[0]", "work_order");
        if (!role.equals("pro"))
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*,job_line_items.^*");
        else
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*,job_line_items.^*");
        hashMap.put("where[id]", id + "");
        hashMap.put("token", Utilities.getSharedPreferences(HomeScreenNew.this).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("page", "1");
        hashMap.put("per_page", "20");
        return hashMap;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0: {
                    
                    break;
                }
                case 1:{
                    checkALert.showcheckAlert(HomeScreenNew.this, HomeScreenNew.this.getResources().getString(R.string.alert_title), error_message);
                    break;
                }
                case 2:{

                    if (jobModal != null){
                        if (modal.getType().equals("ja")){
//                            Singleton.getInstance().getAvailablejoblist().add(0, jobModal);
                            Intent i = new Intent(HomeScreenNew.this, AvailableJobListClickActivity.class);
                            i.putExtra("JOB_DETAIL", jobModal);
                            startActivity(i);
                        }else {
//                            Singleton.getInstance().getSchedulejoblist().add(0, jobModal);
                            ScheduledListDetailsFragment fragment = new ScheduledListDetailsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("modal", jobModal);
                            switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
                        }

                    }
                    break;
                }
                case 3:{
                    checkALert.showcheckAlert(HomeScreenNew.this, HomeScreenNew.this.getResources().getString(R.string.alert_title), error_message);
                    break;
                }
                case 4:{
                    Intent intent1 = new Intent("new_job_available_notification");
                    // You can also include some extra data.
//                MessageReceivingService.sendToApp(extras, context);
                    LocalBroadcastManager.getInstance(HomeScreenNew.this).sendBroadcast(intent1);
                }
            }
        }
    };
    IHttpResponseListener responseListenerAvailable = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
//                    nextScheduled = pagination.getString("next");
                    for(int i = 0; i < results.length(); i++){
                        JSONObject obj = results.getJSONObject(i);
                        jobModal = new AvailableJobModal();
                        jobModal.setContact_name(obj.getString("contact_name"));
                        jobModal.setCreated_at(obj.getString("created_at"));
                        jobModal.setCustomer_id(obj.getString("customer_id"));
                        jobModal.setCustomer_notes(obj.getString("customer_notes"));
                        jobModal.setFinished_at(obj.getString("finished_at"));
                        jobModal.setId(obj.getString("id"));
                        jobModal.setJob_id(obj.getString("job_id"));
//                        jobModal.setLatitude(obj.getDouble("latitude"));
                        jobModal.setLocked_by(obj.getString("locked_by"));
                        jobModal.setLocked_on(obj.getString("locked_on"));
//                        jobModal.setLongitude(obj.getDouble("longitude"));
                        jobModal.setPhone(obj.getString("phone"));
                        jobModal.setPro_id(obj.getString("pro_id"));
                        jobModal.setRequest_date(obj.getString("request_date"));
//                        jobModal.setService_id(obj.getString("service_id"));
//                        jobModal.setService_type(obj.getString("service_type"));
                        jobModal.setStarted_at(obj.getString("started_at"));
                        jobModal.setStatus(obj.getString("status"));
                        jobModal.setTechnician_id(obj.getString("technician_id"));
                        jobModal.setTime_slot_id(obj.getString("time_slot_id"));
                        jobModal.setTitle(obj.getString("title"));
//                        jobModal.setTotal_cost(obj.getString("total_cost"));
                        jobModal.setUpdated_at(obj.getString("updated_at"));
//                        jobModal.setWarranty(obj.getString("warranty"));
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
                                }


//                                JSONObject services_obj = jsonObject.getJSONObject("services");
//                                mod.setService_id(services_obj.getString("id"));
//                                mod.setService_name(services_obj.getString("name"));
//                                mod.setService_created_at(services_obj.getString("created_at"));
//                                mod.setService_updated_at(services_obj.getString("updated_at"));
                                jobapplianceslist.add(mod);
                            }
//                            }
                            jobModal.setJob_appliances_arrlist(jobapplianceslist);
                        }
                        if (!obj.isNull("technicians")){
                            JSONObject technician_object  =  obj.getJSONObject("technicians");
                            jobModal.setTechnician_technicians_id(technician_object.getString("id"));
                            jobModal.setTechnician_user_id(technician_object.getString("user_id"));
                            jobModal.setTechnician_fname(technician_object.getString("first_name"));
                            jobModal.setTechnician_lname(technician_object.getString("last_name"));
                            jobModal.setTechnician_pickup_jobs(technician_object.getString("pickup_jobs"));
                            jobModal.setTechnician_avg_rating(technician_object.getString("avg_rating"));
                            jobModal.setTechnician_scheduled_job_count(technician_object.getString("scheduled_jobs_count"));
                            jobModal.setTechnician_completed_job_count(technician_object.getString("completed_jobs_count"));
                            if (!technician_object.isNull("profile_image")){
                                JSONObject object_profile_image  = technician_object.getJSONObject("profile_image");
                                if (!object_profile_image.isNull("original"))
                                    jobModal.setTechnician_profile_image(object_profile_image.getString("original"));
                            }

                        }
                        if (!obj.isNull("time_slots")){
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            jobModal.setTime_slot_id(time_slot_obj.getString("id"));
                            jobModal.setTimeslot_start(time_slot_obj.getString("start"));
                            jobModal.setTimeslot_end(time_slot_obj.getString("end"));
                            jobModal.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));
                        }


                        if (!obj.isNull("job_customer_addresses")){
                            JSONObject job_customer_addresses_obj = obj.getJSONObject("job_customer_addresses");
                            jobModal.setJob_customer_addresses_id(job_customer_addresses_obj.getString("id"));
                            jobModal.setJob_customer_addresses_zip(job_customer_addresses_obj.getString("zip"));
                            jobModal.setJob_customer_addresses_city(job_customer_addresses_obj.getString("city"));
                            jobModal.setJob_customer_addresses_state(job_customer_addresses_obj.getString("state"));
                            jobModal.setJob_customer_addresses_address(job_customer_addresses_obj.getString("address"));
                            jobModal.setJob_customer_addresses_address_2(job_customer_addresses_obj.getString("address_2"));
                            jobModal.setJob_customer_addresses_updated_at(job_customer_addresses_obj.getString("updated_at"));
                            jobModal.setJob_customer_addresses_created_at(job_customer_addresses_obj.getString("created_at"));
                            jobModal.setJob_customer_addresses_job_id(job_customer_addresses_obj.getString("job_id"));
                            jobModal.setJob_customer_addresses_latitude(job_customer_addresses_obj.getDouble("latitude"));
                            jobModal.setJob_customer_addresses_longitude(job_customer_addresses_obj.getDouble("longitude"));
                        }

                    }
                    if (jobModal == null)
                        return;
                    if(!checkIfAvailablejobExistsInList(jobModal.getId())){
                        Singleton.getInstance().getAvailablejoblist().add(0,jobModal);
                    }
                    handler.sendEmptyMessage(4);
                }else {
//                    handler.sendEmptyMessage(3);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
//                    nextScheduled = pagination.getString("next");
                    for(int i = 0; i < results.length(); i++){
                        JSONObject obj = results.getJSONObject(i);
                        jobModal = new AvailableJobModal();
                        jobModal.setContact_name(obj.getString("contact_name"));
                        jobModal.setCreated_at(obj.getString("created_at"));
                        jobModal.setCustomer_id(obj.getString("customer_id"));
                        jobModal.setCustomer_notes(obj.getString("customer_notes"));
                        jobModal.setFinished_at(obj.getString("finished_at"));
                        jobModal.setId(obj.getString("id"));
                        jobModal.setJob_id(obj.getString("job_id"));
//                        jobModal.setLatitude(obj.getDouble("latitude"));
                        jobModal.setLocked_by(obj.getString("locked_by"));
                        jobModal.setLocked_on(obj.getString("locked_on"));
//                        jobModal.setLongitude(obj.getDouble("longitude"));
                        jobModal.setPhone(obj.getString("phone"));
                        jobModal.setPro_id(obj.getString("pro_id"));
                        jobModal.setRequest_date(obj.getString("request_date"));
//                        jobModal.setService_id(obj.getString("service_id"));
//                        jobModal.setService_type(obj.getString("service_type"));
                        jobModal.setStarted_at(obj.getString("started_at"));
                        jobModal.setStatus(obj.getString("status"));
                        jobModal.setTechnician_id(obj.getString("technician_id"));
                        jobModal.setTime_slot_id(obj.getString("time_slot_id"));
                        jobModal.setTitle(obj.getString("title"));
//                        jobModal.setTotal_cost(obj.getString("total_cost"));
                        jobModal.setUpdated_at(obj.getString("updated_at"));
//                        jobModal.setWarranty(obj.getString("warranty"));
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

                                if (!jsonObject.isNull("description")){
                                    mod.setJob_appliances_appliance_description(jsonObject.getString("description"));
                                }
                                if (!jsonObject.isNull("power_source")){
                                    mod.setJob_appliances_power_source(jsonObject.getString("power_source"));
                                }
                                if (!jsonObject.isNull("brand_name")){
                                    mod.setJob_appliances_brand_name(jsonObject.getString("brand_name"));
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


//                                JSONObject services_obj = jsonObject.getJSONObject("services");
//                                mod.setService_id(services_obj.getString("id"));
//                                mod.setService_name(services_obj.getString("name"));
//                                mod.setService_created_at(services_obj.getString("created_at"));
//                                mod.setService_updated_at(services_obj.getString("updated_at"));
                                jobapplianceslist.add(mod);
                            }
//                            }
                            jobModal.setJob_appliances_arrlist(jobapplianceslist);
                        }
                        if (!obj.isNull("technicians")){
                            JSONObject technician_object  =  obj.getJSONObject("technicians");
                            jobModal.setTechnician_technicians_id(technician_object.getString("id"));
                            jobModal.setTechnician_user_id(technician_object.getString("user_id"));
                            jobModal.setTechnician_fname(technician_object.getString("first_name"));
                            jobModal.setTechnician_lname(technician_object.getString("last_name"));
                            jobModal.setTechnician_pickup_jobs(technician_object.getString("pickup_jobs"));
                            jobModal.setTechnician_avg_rating(technician_object.getString("avg_rating"));
                            jobModal.setTechnician_scheduled_job_count(technician_object.getString("scheduled_jobs_count"));
                            jobModal.setTechnician_completed_job_count(technician_object.getString("completed_jobs_count"));
                            if (!technician_object.isNull("profile_image")){
                                JSONObject object_profile_image  = technician_object.getJSONObject("profile_image");
                                if (!object_profile_image.isNull("original"))
                                    jobModal.setTechnician_profile_image(object_profile_image.getString("original"));
                            }

                        }
                        if (!obj.isNull("time_slots")){
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            jobModal.setTime_slot_id(time_slot_obj.getString("id"));
                            jobModal.setTimeslot_start(time_slot_obj.getString("start"));
                            jobModal.setTimeslot_end(time_slot_obj.getString("end"));
                            jobModal.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));
                        }


                        if (!obj.isNull("job_customer_addresses")){
                            JSONObject job_customer_addresses_obj = obj.getJSONObject("job_customer_addresses");
                            jobModal.setJob_customer_addresses_id(job_customer_addresses_obj.getString("id"));
                            jobModal.setJob_customer_addresses_zip(job_customer_addresses_obj.getString("zip"));
                            jobModal.setJob_customer_addresses_city(job_customer_addresses_obj.getString("city"));
                            jobModal.setJob_customer_addresses_state(job_customer_addresses_obj.getString("state"));
                            jobModal.setJob_customer_addresses_address(job_customer_addresses_obj.getString("address"));
                            jobModal.setJob_customer_addresses_address_2(job_customer_addresses_obj.getString("address_2"));
                            jobModal.setJob_customer_addresses_updated_at(job_customer_addresses_obj.getString("updated_at"));
                            jobModal.setJob_customer_addresses_created_at(job_customer_addresses_obj.getString("created_at"));
                            jobModal.setJob_customer_addresses_job_id(job_customer_addresses_obj.getString("job_id"));
                            jobModal.setJob_customer_addresses_latitude(job_customer_addresses_obj.getDouble("latitude"));
                            jobModal.setJob_customer_addresses_longitude(job_customer_addresses_obj.getDouble("longitude"));
                        }

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
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception;
            handler.sendEmptyMessage(1);
        }
    };
    public AvailableJobModal getJobforId(String id){
        AvailableJobModal jobModal = null ;
        for (int i = 0 ; i < schedulejoblist.size() ; i++ ){
            if (schedulejoblist.get(i).getId().equals(id)){
                jobModal = schedulejoblist.get(i);
                break;
            }
        }
        return jobModal;
    }
    public AvailableJobModal getJobforIdAvalable(String id){
        AvailableJobModal jobModal = null ;
        for (int i = 0 ; i < availablejoblist.size() ; i++ ){
            if (availablejoblist.get(i).getId().equals(id)){
                jobModal = availablejoblist.get(i);
                break;
            }
        }
        return jobModal;
    }
    private void QBLogin(){
        QBAuth.createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // success
                Log.e("", "params.getString(\"token\")" + params.getString("token"));
                _prefs.edit().putString(Preferences.QB_TOKEN, session.getToken()).commit();
//                    _prefs.edit().putString()
                createSession();
            }

            @Override
            public void onError(QBResponseException error) {
                // errors
                Log.e("", "");

            }
        });
    }


    private void createSession() {
        final QBUser user = new QBUser();
//                    user.setLogin(login);
//                    user.setPassword(password);
        user.setLogin(_prefs.getString(Preferences.QB_LOGIN, ""));
        user.setPassword(_prefs.getString(Preferences.QB_PASSWORD, ""));


        ChatService.getInstance().login(user, new QBEntityCallback<Void>() {

            @Override
            public void onSuccess(Void result, Bundle bundle) {
                // Go to Dialogs screen
                //
                Log.e("" + bundle, "success" + result);
                SharedPreferencesUtil.saveQbUser(user);
                HomeScreenNew.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            isChatNoti = false;
                            dialog.dismiss();
                            Bundle data = new Bundle();
                            data.putSerializable("data", modal);
                            switchFragment(new ChatUserFragment(), Constants.CHATUSER_FRAGMENT, true, data);

                        } else {
                            ChatSingleton.getInstance().dataSourceUsers.clear();
                            getChatUsers();
                        }
                    }
                });

//                            Intent intent = new Intent(SplashActivity.this, DialogsActivity.class);
//                            startActivity(intent);
//                            finish();

            }

            @Override
            public void onError(QBResponseException errors) {
//                            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
//                            dialog.setMessage("chat login errors: " + errors).create().show();
                if (errors.getMessage().equals("You have already logged in chat")) {
                    HomeScreenNew.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null && dialog.isShowing()) {
                                isChatNoti = false;
                                dialog.dismiss();
                                Bundle data = new Bundle();
                                data.putSerializable("data", modal);
                                switchFragment(new ChatUserFragment(), Constants.CHATUSER_FRAGMENT, true, data);

                            }
                        }
                    });

                }
                ChatSingleton.getInstance().dataSourceUsers.clear();
                getChatUsers();
                Log.e("", "error" + errors.getMessage());
            }
        });

    }
    private void initLayout() {
        Bundle b = new Bundle();
        b.putString("title", getString(R.string.app_name));
        b.putString("switch_tab", switch_tab_value);
        b.putBoolean("first_tech_reg",isFirstTechReg);
        titletext.setText(b.getString("title", ""));
        fragment = new HomeFragment();
        fragment.setArguments(b);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment, Constants.HOME_FRAGMENT);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
        }
    }

    public void hideRight() {
        img_Right.setVisibility(View.INVISIBLE);
        txtDone.setVisibility(View.INVISIBLE);
    }

    private void setListeners() {
        img_Toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleLeftClick();
            }
        });
        img_Right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRightClick();
            }
        });
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLeftClick();
            }
        });
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRightClick();
            }
        });
        continue_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continue_job.setVisibility(View.GONE);
                if (fragmentManager.findFragmentByTag(Constants.INSTALL_OR_REPAIR_FRAGMENT) != null) {
                    switchFragment(fragmentManager.findFragmentByTag(Constants.INSTALL_OR_REPAIR_FRAGMENT), Constants.INSTALL_OR_REPAIR_FRAGMENT, true, null);
                } else {
                    switchFragment(fragmentManager.findFragmentByTag(Constants.START_JOB_FRAGMENT), Constants.START_JOB_FRAGMENT, true, null);
                }
//                popInclusiveFragment(CurrentScheduledJobSingleTon.getInstance().LastFragment);
//                    if (CurrentScheduledJobSingleTon.getInstance().LastFragment.equals(Constants.START_JOB_FRAGMENT))
//                        switchFragment(new StartJobFragment(),Constants.START_JOB_FRAGMENT,true,null);
//                    else if (CurrentScheduledJobSingleTon.getInstance().LastFragment.equals(Constants.INSTALL_OR_REPAIR_FRAGMENT))
//                        switchFragment(new InstallorRepairFragment(),Constants.INSTALL_OR_REPAIR_FRAGMENT,true,null);

            }
        });
    }

    public void popStack() {
        fragmentManager.popBackStack();
    }

    private void handleLeftClick() {
        if (currentFragmentTag.equals(Constants.HOME_FRAGMENT) || currentFragmentTag.equals(Constants.MYJOB_FRAGMENT)
                || currentFragmentTag.equals(Constants.PAYMENT_FRAGMENT) || currentFragmentTag.equals(Constants.RATING_FRAGMENT)
                || currentFragmentTag.equals(Constants.SETTING_FRAGMENT) || currentFragmentTag.equals(Constants.START_JOB_FRAGMENT)
                || currentFragmentTag.equals(Constants.INSTALL_OR_REPAIR_FRAGMENT)|| currentFragmentTag.equals(Constants.NOTIFICATION_LIST_FRAGMENT)|| currentFragmentTag.equals(Constants.CHATUSER_FRAGMENT)) {
            toggle();
        } else if (currentFragmentTag.equals(Constants.SCHEDULED_LIST_DETAILS_FRAGMENT)) {
            popStack();
        } else if (currentFragmentTag.equals(Constants.PARTS_FRAGMENT)) {
            ((PartsFragment) fragmentManager.findFragmentByTag(Constants.PARTS_FRAGMENT)).clearList();
            popStack();
        } else {
            popStack();
        }
    }

    private void handleRightClick() {
        if (currentFragmentTag.equals(Constants.HOME_FRAGMENT)) {
            Log.e("", "-----" + ((HomeFragment) fragmentManager.findFragmentByTag(Constants.HOME_FRAGMENT)));
            ((HomeFragment) fragmentManager.findFragmentByTag(Constants.HOME_FRAGMENT)).bellClick();
        } else if (currentFragmentTag.equals(Constants.START_JOB_FRAGMENT)) {
            //Show start job dialog
            ((StartJobFragment) fragmentManager.findFragmentByTag(Constants.START_JOB_FRAGMENT)).submitPost();
        } else if (currentFragmentTag.equals(Constants.TELL_US_WHATS_WRONG_FRAGMENT)) {
            ((TellUsWhatsWrongFragment) fragmentManager.findFragmentByTag(Constants.TELL_US_WHATS_WRONG_FRAGMENT)).submitPost();
        } else if (currentFragmentTag.equals(Constants.PARTS_FRAGMENT)) {
            ((PartsFragment) fragmentManager.findFragmentByTag(Constants.PARTS_FRAGMENT)).submitPost();
        } else if (currentFragmentTag.equals(Constants.WORK_ORDER_FRAGMENT)) {
            ((WorkOrderFragment) fragmentManager.findFragmentByTag(Constants.WORK_ORDER_FRAGMENT)).submitPost();
        } else if (currentFragmentTag.equals(Constants.EQUIPMENT_FRAGMENT)) {
            ((EquipmentInfoFragment) fragmentManager.findFragmentByTag(Constants.EQUIPMENT_FRAGMENT)).submitPost();
        } else if (currentFragmentTag.equals(Constants.WHATS_WRONG_FRAGMENT)) {
            ((WhatsWrongFragment) fragmentManager.findFragmentByTag(Constants.WHATS_WRONG_FRAGMENT)).submitPost();
        } else if (currentFragmentTag.equals(Constants.MYJOB_FRAGMENT)) {
            ((MyJobsFragment) fragmentManager.findFragmentByTag(Constants.MYJOB_FRAGMENT)).submitPost();
        }
    }

    private void setWidgets() {
        img_Toggle = (ImageView) findViewById(R.id.img_Toggle);
        img_Right = (ImageView) findViewById(R.id.img_Right);
        badge_home = (TextView) findViewById(R.id.badge_home);
        titletext = (TextView) findViewById(R.id.titletext);
        txtBack = (TextView) findViewById(R.id.txtBack);
        txtDone = (TextView) findViewById(R.id.txtDone);
        continue_job = (TextView) findViewById(R.id.continue_job);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
    }

    public void switchFragment(final Fragment fragment, final String Tag, final boolean addToStack, final Bundle bundle) {
        if (slidingMenu.isMenuShowing())
            toggle();
        if (Tag.equals(currentFragmentTag))
            return;
        if (CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal() != null) {
            if (Tag.equals(Constants.HOME_FRAGMENT) || Tag.equals(Constants.MYJOB_FRAGMENT) || Tag.equals(Constants.SCHEDULED_LIST_DETAILS_FRAGMENT)) {
                continue_job.setVisibility(View.VISIBLE);
            } else {
                continue_job.setVisibility(View.GONE);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fragment != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment, Tag);
                    if (addToStack)
                        fragmentTransaction.addToBackStack(Tag);
                    if (bundle != null)
                        fragment.setArguments(bundle);
                    fragmentTransaction.commit();
                    fragmentManager.executePendingTransactions();
                }
            }
        }, 500);

    }

    public void logOut() {
        toggle();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utilities.getSharedPreferences(HomeScreenNew.this).edit().clear().commit();
                Intent j = new Intent(HomeScreenNew.this, Login_Register_Activity.class);
                startActivity(j);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        }, 500);

    }

    public void contactUs() {
        toggle();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(HomeScreenNew.this, ContactUsActivity.class);
                startActivityForResult(i, CONTACTUS_REQUESTCODE);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        }, 500);
    }

    public void accoutSetUpDialog() {
        toggle();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showAccountSetupDialog();
            }
        }, 200);
    }

    private void showAccountSetupDialog() {
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_hang_tight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        Button btnFinish = (Button) dialog.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenNew.this, SetupCompleteAddressActivity.class);
                intent.putExtra("finalRequestParams", getIncompleteAccountParams());
                intent.putExtra("ispro", true);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
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

    public void setCurrentFragmentTag(String currentFragmentTag) {
        this.currentFragmentTag = currentFragmentTag;
//        setToolBar(currentFragmentTag);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    private void setToolBar(String Tag) {
        if (Tag.equals(Constants.HOME_FRAGMENT)) {
            txtBack.setVisibility(View.GONE);
            txtDone.setVisibility(View.GONE);
            img_Right.setVisibility(View.VISIBLE);
            img_Toggle.setVisibility(View.VISIBLE);
            img_Toggle.setImageResource(R.drawable.menu_icon);
            img_Right.setImageResource(R.drawable.refresh);

        } else if (Tag.equals(Constants.MYJOB_FRAGMENT)
                || Tag.equals(Constants.PAYMENT_FRAGMENT) || Tag.equals(Constants.RATING_FRAGMENT)
                || Tag.equals(Constants.SETTING_FRAGMENT)) {
            txtBack.setVisibility(View.GONE);
            txtDone.setVisibility(View.GONE);
            img_Right.setVisibility(View.GONE);
            img_Toggle.setVisibility(View.VISIBLE);
            img_Toggle.setImageResource(R.drawable.menu_icon);
        } else if (Tag.equals(Constants.SCHEDULED_LIST_DETAILS_FRAGMENT)) {
            img_Toggle.setImageResource(R.drawable.screen_cross);
        }
    }

    @Override
    public void onBackStackChanged() {
//        setToolBar(currentFragmentTag);
    }

    public void setTitletext(String Text) {
        titletext.setText(Text);
    }

    public void setLeftToolBarText(String Text) {

        img_Toggle.setVisibility(View.GONE);
        txtBack.setVisibility(View.VISIBLE);
        txtBack.setText(Text);
    }

    public void setRightToolBarText(String Text) {
        img_Right.setVisibility(View.GONE);
        txtDone.setVisibility(View.VISIBLE);
        txtDone.setText(Text);
    }

    public void setLeftToolBarImage(int resId) {
        txtBack.setVisibility(View.GONE);
        img_Toggle.setVisibility(View.VISIBLE);
        img_Toggle.setImageResource(resId);
    }

    public void setRightToolBarImage(int resId) {
        txtDone.setVisibility(View.GONE);
        img_Right.setVisibility(View.VISIBLE);
        img_Right.setImageResource(resId);
    }
    public void hideBadge(){
        badge_home.setVisibility(View.GONE);
    }
    public void showBadge(){
        badge_home.setVisibility(View.VISIBLE);
    }
    public void setBadgeText(String values){
        badge_home.setVisibility(View.VISIBLE);
        badge_home.setText(values);
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        handleLeftClick();
//        int count = getFragmentManager().getBackStackEntryCount();
//
//        if (count == 0) {
//            super.onBackPressed();
//            //additional code
//        } else {
//            getFragmentManager().popBackStack();
//        }
    }


    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            PERMISSION_ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
            PERMISSION_ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION);
            List<String> permissionsNeeded = new ArrayList<String>();
            if (PERMISSION_ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (PERMISSION_ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionsNeeded.size() > 0) {
                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {

        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
//        if (Utilities.getSharedPreferences(_context).getString(Preferences.GCM_TOKEN, "").equals(""))
//            startService(new Intent(this, MessageReceivingService.class));
        token = FirebaseInstanceId.getInstance().getToken();
        String userId = _prefs.getString(Preferences.ID,"");
        if (token != null){
            updateToken();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("gcm_token_receiver"));
        inBackground = false;
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mGcmPushReceiver, new IntentFilter("gcm_push_notification_receiver"));
//        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
        if (!Utilities.isNetworkAvailable(_context)) {
//            Snackbar snackbar = Snackbar
//                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
//                    .setAction("OK", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
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
        } else {

        }
        count =  0 ;
        for (int i = 0 ; i < ChatSingleton.getInstance().dataSourceUsers.size() ; i++){
            count = count + ChatSingleton.getInstance().dataSourceUsers.get(i).getUnreadMessageCount() ;
        }
        if (count > 0){

            _prefs.edit().putInt(Preferences.CHAT_NOTI_COUNT,count).commit();
        }
        setNotficationCounts();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mGcmPushReceiver);
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        inBackground = true;
        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        startLocationUpdates();

    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        if (locationResponseListener != null) {
            locationResponseListener.handleLocationResponse(location);
            locationResponseListener = null;
        }


    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Stores activity data in the Bundle.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    private BroadcastReceiver mGcmPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            if (intent != null) {
                NotificationModal notificationModal = (NotificationModal) intent.getSerializableExtra("data");
                if (notificationModal.getType().equals("woa") || notificationModal.getType().equals("wod")) {
                    Intent intent1 = new Intent("gcm_push_notification_work_order_approved");
                    // You can also include some extra data.
                    intent1.putExtra("data", notificationModal);
//                MessageReceivingService.sendToApp(extras, context);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
                }else if (notificationModal.getType().equals("ja")) {
                    // check if new available job is already  in the list  if not then get it
                    if (!checkIfAvailablejobExistsInList(notificationModal.getJobId())){
                        GetApiResponseAsyncNoProgress responseAsync = new GetApiResponseAsyncNoProgress(Constants.BASE_URL,"POST", responseListenerAvailable,exceptionListener, HomeScreenNew.this, "Loading");
                        responseAsync.execute(getRequestParamsAvailable(notificationModal.getJobId()));
                    }
                }
            }
        }
    };

    private boolean checkIfAvailablejobExistsInList(String JobId){
        boolean isexist = false ;
        ArrayList<AvailableJobModal> list = Singleton.getInstance().getAvailablejoblist();
        for (int i = 0 ; i < list.size() ; i++){
            if (list.get(i).getId().equals(JobId)){
                isexist = true;
                break;
            }
        }
        return isexist ;
    }
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
//            savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
//            savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
//            super.onSaveInstanceState(savedInstanceState);
//        }
//    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest).setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(HomeScreenNew.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User choose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    public void getLocation(LocationResponseListener locationResponseListener) {
        this.locationResponseListener = locationResponseListener;
        checkLocationSettings();
    }

    public void popInclusiveFragment(String TAG) {
        fragmentManager.popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private HashMap<String, String> getIncompleteAccountParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("api", "signup_complete");
        hashMap.put("object", "pros");
        hashMap.put("with_token", "1");
        return hashMap;
    }


    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            if (intent != null) {
                token = intent.getStringExtra("token");
                updateToken();
            }
          }
    };

    private void updateToken() {
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(Constants.BASE_URL, "POST", getTokenUpdateParameters());
                if (jsonObject != null) {
                    try {
                        String STATUS = jsonObject.getString("STATUS");
                        if (STATUS.equals("SUCCESS")) {
                            if (token != null) {
                                if (!token.equals("null") && !token.equals("")) {
                                    Utilities.getSharedPreferences(_context).edit().putString(Preferences.GCM_TOKEN, token).commit();
                                }
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return null;
            }
        }.execute();
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    private HashMap<String, String> getTokenUpdateParameters() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
//        hashMap.put("api", "update");
//        if (_prefs.getString(Preferences.ROLE, "").equals("pro"))
//            hashMap.put("object", "pros");
//        else
//            hashMap.put("object", "technicians");
//        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
//        hashMap.put("data[technicians][gcm_token]", token);

         hashMap.put("api", "save_pn_token");
         hashMap.put("object", "users");
         hashMap.put("data[token_type]", "gcm");
         hashMap.put("data[token]", token);
         hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        return hashMap;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS_BY_START_JOB_CLASS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationUpdates();

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
    private void getChatUsers(){
        // Get dialogs
        //

        ChatService.getInstance().getDialogs(new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle bundle1) {
                Log.e("", "");
                count = 0;
                ChatSingleton.getInstance().dataSourceUsers.addAll(dialogs);
                HomeScreenNew.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < dialogs.size(); i++) {
                            count = count + dialogs.get(i).getUnreadMessageCount();
                        }
                        if (count > 0) {
                            _prefs.edit().putInt(Preferences.CHAT_NOTI_COUNT,count).commit();
                            setNotficationCounts();
                        }

                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {

                Log.e("", "");
            }
        }, null);
    }
}