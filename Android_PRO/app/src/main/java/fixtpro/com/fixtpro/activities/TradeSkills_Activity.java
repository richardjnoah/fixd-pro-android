package fixtpro.com.fixtpro.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixtpro.com.fixtpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixtpro.com.fixtpro.ResponseListener;
import fixtpro.com.fixtpro.UserProfileScreen;
import fixtpro.com.fixtpro.adapters.TradeSkillAdapter;
import fixtpro.com.fixtpro.beans.SkillTrade;
import fixtpro.com.fixtpro.net.GetApiResponseAsync;
import fixtpro.com.fixtpro.net.GetApiResponseAsyncMutipartNoProgress;
import fixtpro.com.fixtpro.net.IHttpExceptionListener;
import fixtpro.com.fixtpro.net.IHttpResponseListener;
import fixtpro.com.fixtpro.singleton.TradeSkillSingleTon;
import fixtpro.com.fixtpro.utilites.CheckAlertDialog;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.ExceptionListener;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsyncMutipart;
import fixtpro.com.fixtpro.utilites.MultipartUtility;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class TradeSkills_Activity extends AppCompatActivity {
    Context context = this;
    ArrayList<SkillTrade> skillTrades;
    SkillTrade selectedTradeSkill = null;
    TradeSkillAdapter tradeSkillAdapter = null;
    SharedPreferences _prefs = null;

    ImageView imgClose, imgFinish;
    GridView gridView;
    TextView txtFinish;
    String error_message = "";
    CheckAlertDialog checkALert;
    HashMap<String,String> finalRequestParams = null ;
    boolean ispro = false ;
    boolean iscompleting = false ;
    MultipartUtility multipart = null;
    Dialog progressDialog;
    String  selectedImagePathDriver = null;
    String selectedImagePathUser = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_skills_);
        checkALert = new CheckAlertDialog();
        skillTrades = TradeSkillSingleTon.getInstance().getList();
        _prefs = Utilities.getSharedPreferences(context);

        setWidgets();

        setCLickListner();
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")){
                finalRequestParams = (HashMap<String,String>)bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")){
                ispro = bundle.getBoolean("ispro");
            }
            if (bundle.containsKey("driver_image")){
                selectedImagePathDriver = bundle.getString("driver_image");
            }
            if (bundle.containsKey("user_image")){
                selectedImagePathUser = bundle.getString("user_image");
            }
            if (bundle.containsKey("iscompleting")){
                iscompleting = bundle.getBoolean("iscompleting");
            }
        }
        if (skillTrades.size() == 0) {
            GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync(Constants.BASE_URL,"POST", getTradeSkillListener,iHttpExceptionListener, TradeSkills_Activity.this, "Getting.");
            getApiResponseAsync.execute(getTradeSkillRequestParams());
        } else {
            setListAdapter();
        }
        if (ispro && !iscompleting){
            txtFinish.setText("Finish");
        }else{
            txtFinish.setText("Next");
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTradeSkill = skillTrades.get(position);
                if (selectedTradeSkill.isChecked()) {
                    skillTrades.get(position).setIsChecked(false);
                } else {
                    skillTrades.get(position).setIsChecked(true);
                }
                tradeSkillAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgFinish = (ImageView) findViewById(R.id.imgFinish);
        gridView = (GridView) findViewById(R.id.gridView);
        txtFinish = (TextView) findViewById(R.id.txtFinish);
    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            }
        });
        imgFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ispro)
                    registerPro();
                else {
                    Intent intent = new Intent(TradeSkills_Activity.this,TradeLiecenseNo_Activity.class);
                    intent.putExtra("ispro",ispro);
                    finalRequestParams.putAll(getProRegisterParameters());
                    intent.putExtra("finalRequestParams", finalRequestParams);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
    }
    IHttpExceptionListener iHttpExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception ;
            handler.sendEmptyMessage(1);
        }
    };
    IHttpResponseListener getTradeSkillListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response);
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    JSONArray jsonArray = Response.getJSONArray("RESPONSE");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        SkillTrade modal = new SkillTrade();
                        modal.setId(Integer.parseInt(jsonObj.getString("id")));
                        modal.setTitle(jsonObj.getString("name"));
                        modal.setDisplay_order(jsonObj.getString("display_order"));
                        modal.setFor_consumer(jsonObj.getString("for_consumer"));
                        modal.setIsChecked(false);

                        skillTrades.add(modal);
                    }
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            switch (msg.what) {


                case 0: {
                    setListAdapter();
                    break;
                }
                case 1:{
                    checkALert.showcheckAlert(TradeSkills_Activity.this, getResources().getString(R.string.app_name), error_message);
                    break;
                }
                case 2:{
                    Intent intent = new Intent(TradeSkills_Activity.this, HomeScreenNew.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;
                }
            }
        }
    };

    private HashMap<String, String> getTradeSkillRequestParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "services");
        hashMap.put("select", "^*");
        hashMap.put("per_page", "999");
        hashMap.put("page", "1");
        hashMap.put("where[for_pro]", "1");
        hashMap.put("where[status]", "ACTIVE");
        return hashMap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }

    private void setListAdapter() {
        tradeSkillAdapter = new TradeSkillAdapter(this, skillTrades, getResources());
        gridView.setAdapter(tradeSkillAdapter);
    }


    private void registerPro(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = new Dialog(TradeSkills_Activity.this);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.dialog_progress_simple);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                createMultiPartRequest(getProRegisterParameters());
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                GetApiResponseAsyncMutipartNoProgress getApiResponseAsync = new GetApiResponseAsyncMutipartNoProgress(multipart, proRegistrationListener,exceptionListenerProRegistration, TradeSkills_Activity.this, "Registring");
                getApiResponseAsync.execute();
            }
        }.execute();
    }

    ExceptionListener exceptionListenerProRegistration = new ExceptionListener(){

        @Override
        public void handleException(int exceptionStatus) {
            handler.sendEmptyMessage(exceptionStatus);
        }
    };
    ResponseListener proRegistrationListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                String STATUS = Response.getString("STATUS");
                if (STATUS.equals("SUCCESS")) {
                    JSONObject RESPONSE = Response.getJSONObject("RESPONSE");
                    String Token = Response.getJSONObject("RESPONSE").getString("token");
                    String id = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("id");
                    String role = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("role");
                    String email = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("email");
                    String phone = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("phone");
                    String has_card = "0";
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("has_card")){
                         has_card = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("has_card");
                    }

                    String account_status = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("account_status");
                    Log.e("AUTH TOKEN", Token);
                    Log.e("ROLE", role);
                    JSONObject pro_settings;
                    SharedPreferences.Editor editor = _prefs.edit();
                    editor.putString(Preferences.ID, id);
                    editor.putString(Preferences.ROLE, role);
                    editor.putString(Preferences.AUTH_TOKEN, Token);
                    editor.putString(Preferences.EMAIL, email);
                    editor.putString(Preferences.PHONE, phone);
                    editor.putString(Preferences.HAS_CARD, has_card);
                    editor.putString(Preferences.ACCOUNT_STATUS, account_status);
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("pros")){
                        JSONObject pros = null;
                        pros = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("pros");
                        String city = pros.getString("city");
                        String state = pros.getString("state");
                        String zip = pros.getString("zip");
                        String address = pros.getString("address");
                        String address2 = pros.getString("address_2");
                        String hourly_rate = pros.getString("hourly_rate");
                        String company_name = pros.getString("company_name");
                        String ein_number="" ;
                        if (!pros.isNull("ein_number"))
                            ein_number = pros.getString("ein_number");
                        String bank_name = pros.getString("bank_name");
                        String bank_routing_number = pros.getString("bank_routing_number");
                        String bank_account_number = pros.getString("bank_account_number");
                        String bank_account_type = pros.getString("bank_account_type");
                        String insurance = pros.getString("insurance");
                        String insurance_policy = pros.getString("insurance_policy");
                        String isvarified = pros.getString("verified");
                        String working_radius_miles = pros.getString("working_radius_miles");
                        String is_pre_registered = pros.getString("is_pre_registered");
                        String latitude = pros.getString("latitude");
                        String longitude = pros.getString("longitude");
                        String avg_rating = pros.getString("avg_rating");
                        editor.putString(Preferences.CITY, city);
                        editor.putString(Preferences.STATE, state);
                        editor.putString(Preferences.ZIP, zip);
                        editor.putString(Preferences.ADDRESS, address);
                        editor.putString(Preferences.ADDRESS2, address2);
                        editor.putString(Preferences.HOURLY_RATE, hourly_rate);
                        editor.putString(Preferences.COMPANY_NAME, company_name);
                        editor.putString(Preferences.EIN_NUMEBR, ein_number);
                        editor.putString(Preferences.BANK_NAME, bank_name);
                        editor.putString(Preferences.BANK_ROUTING_NUMBER, bank_routing_number);
                        editor.putString(Preferences.BANK_ACCOUNT_NUMBER, bank_account_number);
                        editor.putString(Preferences.BANK_ACCOUNT_TYPE, bank_account_type);
                        editor.putString(Preferences.INSURANCE, insurance);
                        editor.putString(Preferences.INSURANCE_POLICY, insurance_policy);
                        editor.putString(Preferences.IS_VARIFIED, isvarified);
                        editor.putString(Preferences.WORKING_RADIUS_MILES, working_radius_miles);
                        editor.putString(Preferences.IS_PRE_REGISTERED, is_pre_registered);
                        editor.putString(Preferences.LATITUDE, latitude);
                        editor.putString(Preferences.LONGITUDE, longitude);
                        editor.putString(Preferences.AVERAGE_RATING, avg_rating);
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("services")){
                        JSONArray services = null;
                        services = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONArray("services");
                        editor.putString(Preferences.SERVICES_JSON_ARRAY, services.toString());
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("pro_settings")) {
                        pro_settings = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("pro_settings");
                        editor.putString(Preferences.SETTING_ID, pro_settings.getString("id"));
                        editor.putString(Preferences.LOCATION_SERVICE, pro_settings.getString("location_services"));
                        editor.putString(Preferences.TEXT_MESSAGING, pro_settings.getString("text_messaging"));
                        editor.putString(Preferences.AVAILABLE_JOBS_NOTIFICATION, pro_settings.getString("available_jobs_notification"));
                        editor.putString(Preferences.JOB_WON_NOTIFICATION, pro_settings.getString("job_won_notification"));
                        editor.putString(Preferences.JOB_LOST_NOTIFICATION, pro_settings.getString("job_lost_notification"));
                        editor.putString(Preferences.JOB_RESECHDULED, pro_settings.getString("job_rescheduled"));
                        editor.putString(Preferences.JOB_CANCELLED, pro_settings.getString("job_canceled"));

                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("technicians")){
                        JSONObject technicians = null;
                        technicians = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians");
                        String first_name = technicians.getString("first_name");
                        String last_name = technicians.getString("last_name");
                        String social_security_number = technicians.getString("social_security_number");
                        String years_in_business = technicians.getString("years_in_business");
//                        String trade_license_number = technicians.getString("trade_license_number");
                        editor.putString(Preferences.FIRST_NAME, first_name);
                        editor.putString(Preferences.LAST_NAME, last_name);
                        editor.putString(Preferences.SOCIAL_SECURITY_NUMBER, social_security_number);
                        editor.putString(Preferences.YEARS_IN_BUSINESS, years_in_business);
//                        editor.putString(Preferences.TRADE_LICENSE_NUMBER, trade_license_number);
                        editor.putBoolean(Preferences.ISLOGIN, true);
                        if (!Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").isNull("profile_image")){
                            JSONObject profile_image = null;
                            profile_image = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").getJSONObject("profile_image");
                            if (profile_image.has("original")) {
                                String image_original = profile_image.getString("original");
                                editor.putString(Preferences.PROFILE_IMAGE, image_original);
                            }
                        }
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("quickblox_accounts")){
                        JSONObject quickblox_accounts = null;
                        quickblox_accounts = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("quickblox_accounts");
                        String account_id = quickblox_accounts.getString("account_id");
                        String login = quickblox_accounts.getString("login");
                        String password = quickblox_accounts.getString("qb_password");
                        editor.putString(Preferences.QB_ACCOUNT_ID, account_id);
                        editor.putString(Preferences.QB_LOGIN, login);
                        editor.putString(Preferences.QB_PASSWORD, password);
                    }
                    editor.commit();
                    handler.sendEmptyMessage(2);
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error_message = "Server not responding" ;
                handler.sendEmptyMessage(1);
            }
        }
    };

    private HashMap<String,String> getProRegisterParameters(){

        for (int i = 0 ; i < TradeSkillSingleTon.getInstance().getListChecked().size() ; i++){
            finalRequestParams.put("data[services]["+i+"]",TradeSkillSingleTon.getInstance().getListChecked().get(i).getId()+"");
        }
        return finalRequestParams;
    }
    private MultipartUtility createMultiPartRequest(HashMap<String,String> hashMap){

        try {
            multipart = new MultipartUtility(Constants.BASE_URL, Constants.CHARSET);
            for ( String key : hashMap.keySet() ) {
                multipart.addFormField(key, hashMap.get(key));
//                Log.e("key"+key,"finalRequestParams.get(key)"+finalRequestParams.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return multipart;
    }
}
