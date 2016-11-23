 package fixtpro.com.fixtpro.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import fixtpro.com.fixtpro.AddBankAccountActivity;
import fixtpro.com.fixtpro.Add_Driver_LicScreen;
import fixtpro.com.fixtpro.Add_TechScreen;
import fixtpro.com.fixtpro.ChangePassword;
import fixtpro.com.fixtpro.HomeScreenNew;
import fixtpro.com.fixtpro.Login_Register_Activity;
import fixdpro.com.fixdpro.R;
import fixtpro.com.fixtpro.ResponseListener;
import fixtpro.com.fixtpro.UserProfileScreen;
import fixtpro.com.fixtpro.WorkingRadiusActivity;
import fixtpro.com.fixtpro.activities.AddBankAccountNew;
import fixtpro.com.fixtpro.activities.AddBankAccountNewEdit;
import fixtpro.com.fixtpro.activities.AddTechnicion_Activity;
import fixtpro.com.fixtpro.activities.AddTechnicion_Activity_Edit;
import fixtpro.com.fixtpro.activities.CompanyInformation_Activity;
import fixtpro.com.fixtpro.activities.SetupCompleteAddressActivity;
import fixtpro.com.fixtpro.activities.SetupCompleteAddressActivity_Edit;
import fixtpro.com.fixtpro.activities.SignUp_Account_Activity;
import fixtpro.com.fixtpro.activities.SignUp_Account_Activity_Edit;
import fixtpro.com.fixtpro.activities.TradeSkills_Activity;
import fixtpro.com.fixtpro.activities.TradeSkills_Activity_Edit;
import fixtpro.com.fixtpro.activities.WorkingRadiusNew;
import fixtpro.com.fixtpro.activities.WorkingRadiusNew_Edit;
import fixtpro.com.fixtpro.beans.SkillTrade;
import fixtpro.com.fixtpro.singleton.TradeSkillSingleTon;
import fixtpro.com.fixtpro.utilites.CheckIfUserVarified;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.JSONParser;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Singleton;
import fixtpro.com.fixtpro.utilites.Utilities;
import fixtpro.com.fixtpro.views.SwitchButton;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }
    LinearLayout layout_change_password, layout_edit_profile, layout_edit_comp_info, layout_edit_bank_info, layout_edit_tech, layout_change_radius,layout_edit_address,layout_edit_tradeskills;
    SwitchButton swhLocation, swhTextMessaging ;
    ImageView img_available_jobs_email, img_available_jobs_phone, img_job_won_email, img_job_won_phone,
              img_job_lost_email, img_job_lost_phone, img_job_reschduled_email, img_job_reschduled_phone,
              img_job_cancelled_email, img_job_cancelled_phone;

    View bank_account_divider, edit_tech_divider,edit_company_divider,edit_address_divider,divider;

    SharedPreferences _prefs  = null ;

    SwitchButton sb_custom_miui;

    String userRole  = "";
    String authToken  = "";
    String notif_AvlJobs = "none", notif_JobWon = "none", notif_JobLost = "none",
           notif_JobResch = "none", notif_JobCancel = "none", loc_Settings = "0",
           text_Settings = "0";
    Context _context ;
    String error_message =  "";
    TextView txtSignOut;
    private Dialog dialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = getActivity() ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView  = inflater.inflate(R.layout.fragment_settings, container, false);
        _prefs = Utilities.getSharedPreferences(_context);
        userRole  = _prefs.getString(Preferences.ROLE, null);
        authToken  = _prefs.getString(Preferences.AUTH_TOKEN, null);
        setWidgets(rootView);
        setListeners();
        initSettings();
        if (_prefs.getString(Preferences.IS_VARIFIED,"0").equals("1")){
            getPrimaryCard();
        }
        return rootView;
    }

    private  void  setWidgets(View view){
        layout_change_password  =  (LinearLayout) view.findViewById(R.id.layout_change_password);
        layout_edit_profile  =  (LinearLayout) view.findViewById(R.id.layout_edit_profile);
        layout_edit_comp_info  =  (LinearLayout) view.findViewById(R.id.layout_edit_comp_info);
        layout_edit_bank_info  =  (LinearLayout) view.findViewById(R.id.layout_edit_bank_info);
        layout_edit_tech =  (LinearLayout) view.findViewById(R.id.layout_edit_tech);
        layout_change_radius =  (LinearLayout) view.findViewById(R.id.layout_change_radius);
        layout_edit_address =  (LinearLayout) view.findViewById(R.id.layout_edit_address);
        layout_edit_tradeskills =  (LinearLayout) view.findViewById(R.id.layout_edit_tradeskills);
        bank_account_divider = (View)view.findViewById(R.id.bank_account_divider);
        edit_company_divider = (View)view.findViewById(R.id.edit_company_divider);
        edit_address_divider = (View)view.findViewById(R.id.edit_address_divider);
        txtSignOut = (TextView)view.findViewById(R.id.txtSignOut);
        edit_tech_divider = (View)view.findViewById(R.id.edit_tech_divider);
        divider = (View)view.findViewById(R.id.divider);
        if (!_prefs.getString(Preferences.ROLE,"").equals("pro")){
            layout_edit_bank_info.setVisibility(View.GONE);
            bank_account_divider.setVisibility(View.GONE);
            layout_edit_tech.setVisibility(View.GONE);
            edit_tech_divider.setVisibility(View.GONE);
            layout_change_radius.setVisibility(View.GONE);
            layout_edit_comp_info.setVisibility(View.GONE);
            layout_edit_address.setVisibility(View.GONE);
            edit_company_divider.setVisibility(View.GONE);
            edit_address_divider.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        swhLocation  = (SwitchButton)view.findViewById(R.id.swhLocation);
        swhTextMessaging  = (SwitchButton)view.findViewById(R.id.swhTextMessaging);

        img_available_jobs_email = (ImageView)view.findViewById(R.id.img_available_jobs_email);
        img_available_jobs_phone = (ImageView)view.findViewById(R.id.img_available_jobs_phone);
        img_job_won_email = (ImageView)view.findViewById(R.id.img_job_won_email);
        img_job_won_phone = (ImageView)view.findViewById(R.id.img_job_won_phone);
        img_job_lost_email = (ImageView)view.findViewById(R.id.img_job_lost_email);
        img_job_lost_phone = (ImageView)view.findViewById(R.id.img_job_lost_phone);
        img_job_reschduled_email = (ImageView)view.findViewById(R.id.img_job_reschduled_email);
        img_job_reschduled_phone = (ImageView)view.findViewById(R.id.img_job_reschduled_phone);
        img_job_cancelled_email = (ImageView)view.findViewById(R.id.img_job_cancelled_email);
        img_job_cancelled_phone = (ImageView)view.findViewById(R.id.img_job_cancelled_phone);
    }

    private  void initSettings(){
        notif_AvlJobs  = _prefs.getString(Preferences.AVAILABLE_JOBS_NOTIFICATION,"none");
        notif_JobWon  = _prefs.getString(Preferences.JOB_WON_NOTIFICATION,"none");
        notif_JobLost  = _prefs.getString(Preferences.JOB_LOST_NOTIFICATION,"none");
        notif_JobResch = _prefs.getString(Preferences.JOB_RESECHDULED,"none");
        notif_JobCancel = _prefs.getString(Preferences.JOB_CANCELLED,"none");
        loc_Settings = _prefs.getString(Preferences.LOCATION_SERVICE,"0");
        text_Settings = _prefs.getString(Preferences.TEXT_MESSAGING,"0");

        setLayout();
    }
    private  void setLayout(){
        if (notif_AvlJobs.equals("none")){
            img_available_jobs_email.setImageResource(R.drawable.white_radiobutton);
            img_available_jobs_phone.setImageResource(R.drawable.white_radiobutton);
        }else  if (notif_AvlJobs.equals("phone")){
            img_available_jobs_email.setImageResource(R.drawable.white_radiobutton);
            img_available_jobs_phone.setImageResource(R.drawable.radiobutton);
        }else  if (notif_AvlJobs.equals("email")){
            img_available_jobs_email.setImageResource(R.drawable.radiobutton);
            img_available_jobs_phone.setImageResource(R.drawable.white_radiobutton);
        }else{
            img_available_jobs_email.setImageResource(R.drawable.radiobutton);
            img_available_jobs_phone.setImageResource(R.drawable.radiobutton);
        }

        if (notif_JobWon.equals("none")){
            img_job_won_email.setImageResource(R.drawable.white_radiobutton);
            img_job_won_phone.setImageResource(R.drawable.white_radiobutton);
        }else  if (notif_JobWon.equals("phone")){
            img_job_won_email.setImageResource(R.drawable.white_radiobutton);
            img_job_won_phone.setImageResource(R.drawable.radiobutton);
        }else  if (notif_JobWon.equals("email")){
            img_job_won_email.setImageResource(R.drawable.radiobutton);
            img_job_won_phone.setImageResource(R.drawable.white_radiobutton);
        }else{
            img_job_won_email.setImageResource(R.drawable.radiobutton);
            img_job_won_phone.setImageResource(R.drawable.radiobutton);
        }

        if (notif_JobLost.equals("none")){
            img_job_lost_email.setImageResource(R.drawable.white_radiobutton);
            img_job_lost_phone.setImageResource(R.drawable.white_radiobutton);
        }else  if (notif_JobLost.equals("phone")){
            img_job_lost_email.setImageResource(R.drawable.white_radiobutton);
            img_job_lost_phone.setImageResource(R.drawable.radiobutton);
        }else  if (notif_JobLost.equals("email")){
            img_job_lost_email.setImageResource(R.drawable.radiobutton);
            img_job_lost_phone.setImageResource(R.drawable.white_radiobutton);
        }else{
            img_job_lost_email.setImageResource(R.drawable.radiobutton);
            img_job_lost_phone.setImageResource(R.drawable.radiobutton);
        }

        if (notif_JobResch.equals("none")){
            img_job_reschduled_email.setImageResource(R.drawable.white_radiobutton);
            img_job_reschduled_phone.setImageResource(R.drawable.white_radiobutton);
        }else  if (notif_JobResch.equals("phone")){
            img_job_reschduled_email.setImageResource(R.drawable.white_radiobutton);
            img_job_reschduled_phone.setImageResource(R.drawable.radiobutton);
        }else  if (notif_JobResch.equals("email")){
            img_job_reschduled_email.setImageResource(R.drawable.radiobutton);
            img_job_reschduled_phone.setImageResource(R.drawable.white_radiobutton);
        }else{
            img_job_reschduled_email.setImageResource(R.drawable.radiobutton);
            img_job_reschduled_phone.setImageResource(R.drawable.radiobutton);
        }

        if (notif_JobCancel.equals("none")){
            img_job_cancelled_email.setImageResource(R.drawable.white_radiobutton);
            img_job_cancelled_phone.setImageResource(R.drawable.white_radiobutton);
        }else  if (notif_JobCancel.equals("phone")){
            img_job_cancelled_email.setImageResource(R.drawable.white_radiobutton);
            img_job_cancelled_phone.setImageResource(R.drawable.radiobutton);
        }else  if (notif_JobCancel.equals("email")){
            img_job_cancelled_email.setImageResource(R.drawable.radiobutton);
            img_job_cancelled_phone.setImageResource(R.drawable.white_radiobutton);
        }else{
            img_job_cancelled_email.setImageResource(R.drawable.radiobutton);
            img_job_cancelled_phone.setImageResource(R.drawable.radiobutton);
        }
        if (loc_Settings.equals("0")){
            swhLocation.setChecked(false);
        }else {
            swhLocation.setChecked(true);
        }

        if (text_Settings.equals("0")){
            swhTextMessaging.setChecked(false);
        }else {
            swhTextMessaging.setChecked(true);
        }
    }
    private  void setListeners(){
        txtSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcheckAlert(getActivity(), "SIGN OUT", "Are you sure you want to Sign Out?");
            }
        });
        layout_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForProfileCompletion()){
                    Intent intent =  new Intent(getActivity(), ChangePassword.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter,R.anim.exit);
                }

            }
        });
        layout_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForProfileCompletion()){
                    Intent intent =  new Intent(getActivity(), SignUp_Account_Activity_Edit.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                }

            }
        });
        layout_edit_comp_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForProfileCompletion()){
                    Intent intent =  new Intent(getActivity(), CompanyInformation_Activity_Edit.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
        layout_edit_bank_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForProfileCompletion()) {
                    Intent intent = new Intent(getActivity(), AddBankAccountNewEdit.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                }

            }
        });
        layout_change_radius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForProfileCompletion()) {
                    Intent intent = new Intent(getActivity(), WorkingRadiusNew_Edit.class);
                    startActivity(intent);getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

                }

            }
        });
        layout_edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForProfileCompletion()) {
                    Intent intent = new Intent(getActivity(), SetupCompleteAddressActivity_Edit.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
        layout_edit_tradeskills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForProfileCompletion()){
                    Intent intent =  new Intent(getActivity(), TradeSkills_Activity_Edit.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                }

            }
        });
        swhLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    loc_Settings = "1";
                else
                    loc_Settings = "0";
                saveSettings();
            }
        });
        swhTextMessaging.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    text_Settings = "1";
                else
                    text_Settings = "0";
                saveSettings();
            }
        });
        layout_edit_tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForProfileCompletion()) {
                    Intent intent = new Intent(getActivity(), AddTechnicion_Activity_Edit.class);
                    intent.putExtra("isEdit", true);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
        img_available_jobs_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notif_AvlJobs.equals("none")){
                    notif_AvlJobs = "email" ;
                }else if (notif_AvlJobs.equals("both")){
                    notif_AvlJobs = "phone" ;
                }else if (notif_AvlJobs.equals("phone")){
                    notif_AvlJobs = "both" ;
                }else {
                    notif_AvlJobs = "none" ;
                }
                saveSettings();
            }
        });img_available_jobs_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notif_AvlJobs.equals("none")){
                    notif_AvlJobs = "phone" ;
                }else if (notif_AvlJobs.equals("both")){
                    notif_AvlJobs = "email" ;
                }else if (notif_AvlJobs.equals("email")){
                    notif_AvlJobs = "both" ;
                }else {
                    notif_AvlJobs = "none" ;
                }
                saveSettings();
            }
        });img_job_won_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notif_JobWon.equals("none")){
                    notif_JobWon = "email" ;
                }else if (notif_JobWon.equals("both")){
                    notif_JobWon = "phone" ;
                }else if (notif_JobWon.equals("phone")){
                    notif_JobWon = "both" ;
                }else {
                    notif_JobWon = "none" ;
                }
                saveSettings();
            }
        });img_job_won_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notif_JobWon.equals("none")){
                    notif_JobWon = "phone" ;
                }else if (notif_JobWon.equals("both")){
                    notif_JobWon = "email" ;
                }else if (notif_JobWon.equals("email")){
                    notif_JobWon = "both" ;
                }else {
                    notif_JobWon = "none" ;
                }
                saveSettings();
            }
        });img_job_lost_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notif_JobLost.equals("none")){
                    notif_JobLost = "email" ;
                }else if (notif_JobLost.equals("both")){
                    notif_JobLost = "phone" ;
                }else if (notif_JobLost.equals("phone")){
                    notif_JobLost = "both" ;
                }else {
                    notif_JobLost = "none" ;
                }
                saveSettings();
            }
        });img_job_lost_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notif_JobLost.equals("none")){
                    notif_JobLost = "phone" ;
                }else if (notif_JobLost.equals("both")){
                    notif_JobLost = "email" ;
                }else if (notif_JobLost.equals("email")){
                    notif_JobLost = "both" ;
                }else {
                    notif_JobLost = "none" ;
                }
                saveSettings();
            }
        });img_job_reschduled_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notif_JobResch.equals("none")){
                    notif_JobResch = "email" ;
                }else if (notif_JobResch.equals("both")){
                    notif_JobResch = "phone" ;
                }else if (notif_JobResch.equals("phone")){
                    notif_JobResch = "both" ;
                }else {
                    notif_JobResch = "none" ;
                }
                saveSettings();
            }
        });img_job_reschduled_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notif_JobResch.equals("none")){
                    notif_JobResch = "phone" ;
                }else if (notif_JobResch.equals("both")){
                    notif_JobResch = "email" ;
                }else if (notif_JobResch.equals("email")){
                    notif_JobResch = "both" ;
                }else {
                    notif_JobResch = "none" ;
                }
                saveSettings();
            }
        });img_job_cancelled_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notif_JobCancel.equals("none")){
                    notif_JobCancel = "email" ;
                }else if (notif_JobCancel.equals("both")){
                    notif_JobCancel = "phone" ;
                }else if (notif_JobCancel.equals("phone")){
                    notif_JobCancel = "both" ;
                }else {
                    notif_JobCancel = "none" ;
                }
                Log.e("going",notif_JobCancel);
                saveSettings();
            }
        });img_job_cancelled_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notif_JobCancel.equals("none")){
                    notif_JobCancel = "phone" ;
                }else if (notif_JobCancel.equals("both")){
                    notif_JobCancel = "email" ;
                }else if (notif_JobCancel.equals("email")){
                    notif_JobCancel = "both" ;
                }else {
                    notif_JobCancel = "none" ;
                }
                Log.e("going",notif_JobCancel);
                saveSettings();
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

    private HashMap<String,String> getSettingParameters(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "update");
        hashMap.put("object", "pro_settings");
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("data[text_messaging]", text_Settings);
        hashMap.put("data[location_services]", loc_Settings);
        hashMap.put("data[job_canceled]", notif_JobCancel);
        hashMap.put("data[job_rescheduled]", notif_JobResch);
        hashMap.put("data[job_lost_notification]", notif_JobLost);
        hashMap.put("data[job_won_notification]", notif_JobWon);
        hashMap.put("data[available_jobs_notification]", notif_AvlJobs);
        return hashMap;
    }
    private void saveSettings(){
        GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerSettings, getActivity(), "Loading");
        responseAsync.execute(getSettingParameters());
    }
    ResponseListener responseListenerSettings = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS")){
                    JSONObject pro_settings = Response.getJSONObject("RESPONSE");
                    SharedPreferences.Editor editor = _prefs.edit();
                    editor.putString(Preferences.SETTING_ID, pro_settings.getString("id"));
                    editor.putString(Preferences.LOCATION_SERVICE, pro_settings.getString("location_services"));
                    editor.putString(Preferences.TEXT_MESSAGING, pro_settings.getString("text_messaging"));
                    editor.putString(Preferences.AVAILABLE_JOBS_NOTIFICATION, pro_settings.getString("available_jobs_notification"));
                    editor.putString(Preferences.JOB_WON_NOTIFICATION, pro_settings.getString("job_won_notification"));
                    editor.putString(Preferences.JOB_LOST_NOTIFICATION, pro_settings.getString("job_lost_notification") );
                    editor.putString(Preferences.JOB_RESECHDULED, pro_settings.getString("job_rescheduled"));
                    editor.putString(Preferences.JOB_CANCELLED, pro_settings.getString("job_canceled"));

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
                    initSettings();
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
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.SETTING_FRAGMENT);
        setupToolBar();
        if (!_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.IS_VARIFIED, "").equals("0")){
            new CheckIfUserVarified(getActivity());
        }
    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).hideRight();
        ((HomeScreenNew)getActivity()).setTitletext("Settings");
        ((HomeScreenNew)getActivity()).setLeftToolBarImage(R.drawable.menu_icon);
    }
    public void showcheckAlert(final Activity context,final String title, final String message) {
        // define alert...
        final android.support.v7.app.AlertDialog.Builder dialog1 ;
        //set title
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog1 = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog1 = new AlertDialog.Builder(context);
        }
        dialog1.setTitle(title);
        // set message...
        dialog1.setMessage(message);
        // set button status..onclick
        dialog1.setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                dialog.dismiss();
                if (_prefs.edit().clear().commit()){
                    Singleton.getInstance().doLogout();
                    Intent intent = new Intent(getActivity(), Login_Register_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
        dialog1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                dialog.dismiss();

            }
        });
        android.support.v7.app.AlertDialog alert = dialog1.create();
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();
    }
    private void getPrimaryCard(){
        //         Getting Trade Skills on App Start
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(Constants.BASE_URL, "POST", getPrimaryCardParams());
                if (jsonObject != null) {
                    try {
                        String STATUS = jsonObject.getString("STATUS");
                        if (STATUS.equals("SUCCESS")) {
                            JSONObject RESPONSE = jsonObject.getJSONObject("RESPONSE");
                            String id = RESPONSE.getString("id");
                            String card_number = RESPONSE.getString("card_number");
                            String firstname = RESPONSE.getString("firstname");
                            String cvv = RESPONSE.getString("cvv");
                            String lastname = RESPONSE.getString("lastname");
                            String month = RESPONSE.getString("month");
                            String year = RESPONSE.getString("year");
                            SharedPreferences.Editor editor = _prefs.edit() ;
                            editor.putString(Preferences.CREDIT_CARD_ID,id);
                            editor.putString(Preferences.CREDIT_CARD_NUMBER,card_number);
                            editor.putString(Preferences.CREDIT_CARD_FIRST_NAME,firstname);
                            editor.putString(Preferences.CREDIT_CARD_LAST_NAME,lastname);
                            editor.putString(Preferences.CREDIT_CARD_CVV,cvv);
                            editor.putString(Preferences.CREDIT_CARD_MONTH,month);
                            editor.putString(Preferences.CREDIT_CARD_YEAR,year);
                            editor.commit();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return null;
            }
        }.execute();
    }

    private HashMap<String, String> getPrimaryCardParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "primary_card");
        hashMap.put("object", "cards");
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));

        return hashMap;
    }
    private boolean checkForProfileCompletion(){
        boolean isComplete = true;
        if (_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.ROLE, "pro").equals("pro")) {
            showAccountSetupDialog();
            isComplete = false ;

        }
        return isComplete;
    }
    private HashMap<String,String> getIncompleteAccountParams(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("api", "signup_complete");
        hashMap.put("object", "pros");
        hashMap.put("with_token", "1");

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
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
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
}
