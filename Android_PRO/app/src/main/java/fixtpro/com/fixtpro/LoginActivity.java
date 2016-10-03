package fixtpro.com.fixtpro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import fixtpro.com.fixtpro.gcm_components.MessageReceivingService;
import fixtpro.com.fixtpro.utilites.ChatService;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class LoginActivity extends AppCompatActivity {
    TextView txtDone,txtForgetPassword;
    ImageView txtBack;
    EditText txtPhone,txtPassword;
    String Phone,Password;
    Context _context =  this;
    Typeface fontfamily;
    SharedPreferences.Editor editor;
    String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        startService(new Intent(this, MessageReceivingService.class));
        setWidgets();
        setListeners();
        setTypeface();
        editor = Utilities.getSharedPreferences(_context).edit();

    }
    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(
//                mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("gcm_token_receiver"));
        super.onResume();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }
    private  void setTypeface(){
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
//        txtBack.setTypeface(fontfamily);
        txtDone.setTypeface(fontfamily);
        txtPhone.setTypeface(fontfamily);
        txtPassword.setTypeface(fontfamily);

    }
    private void setListeners(){
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Phone = txtPhone.getText().toString().trim();
                Password = txtPassword.getText().toString().trim();

                if (Phone.length() == 0){
                    showAlertDialog("Fixd-Pro","Please enter the Email/Phone number.");
                    return;
                }
//                else if (Phone.length() < 10){
//                    showAlertDialog("Fixd-Pro","Your phone number seems to invalid, Please try again.");
//                    return;
//                }
                else if (Password.length() == 0){
                    showAlertDialog("Fixd-Pro","Please enter the password.");
                    return;
                }
                else if (Password.length() < 6) {
//                    Show Dialog
                    showAlertDialog("Fixd-Pro","Your password should be 6 or more characters long, Please try again.");
                    return;
                }else {
                    Utilities.hideKeyBoad(_context, LoginActivity.this.getCurrentFocus());
//                    do it
                    GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync("POST",loginResponseListener,LoginActivity.this,"Signing In.");
                    getApiResponseAsync.execute(getLoginRequestParams());
                }
            }
        });
    }
    ResponseListener loginResponseListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            try {
                Log.e("",""+Response);
                String STATUS = Response.getString("STATUS");
                if (STATUS.equals("SUCCESS")) {
                    JSONObject RESPONSE = Response.getJSONObject("RESPONSE");
                    String Token = Response.getJSONObject("RESPONSE").getString("token");
                    String id = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("id");
                    String role = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("role");
                    String email = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("email");
                    String phone = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("phone");
                    String  has_card = "0";
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("has_card")){
                        has_card = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("has_card");
                    }

                    String account_status = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("account_status");
                    Log.e("AUTH TOKEN", Token);
                    Log.e("ROLE", role);
                    JSONObject pro_settings;
//                    SharedPreferences.Editor editor = prefs.edit();
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
//                        String isvarified = pros.getString("verified");
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
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("trade_licenses")){
                        editor.putString(Preferences.TRADE_LICENCES_JSON_ARRAY,Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONArray("trade_licenses").toString());
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("technicians")){
                        JSONObject technicians = null;
                        technicians = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians");
                        String first_name = technicians.getString("first_name");
                        String last_name = technicians.getString("last_name");
                        String social_security_number = technicians.getString("social_security_number");
                        String years_in_business = technicians.getString("years_in_business");
//                        String trade_license_number = technicians.getString("trade_license_number");
                        String driver_license_number = technicians.getString("driver_license_number");
                        String driver_license_state = technicians.getString("driver_license_state");
                        String dob = technicians.getString("dob");
                        String isvarified = technicians.getString("verified");
                        editor.putString(Preferences.IS_VARIFIED, isvarified);
                        editor.putString(Preferences.FIRST_NAME, first_name);
                        editor.putString(Preferences.LAST_NAME, last_name);
                        editor.putString(Preferences.SOCIAL_SECURITY_NUMBER, social_security_number);
                        editor.putString(Preferences.YEARS_IN_BUSINESS, years_in_business);
//                        editor.putString(Preferences.TRADE_LICENSE_NUMBER, trade_license_number);
                        editor.putString(Preferences.DRIVER_LICENSE_NUMBER, driver_license_number);
                        editor.putString(Preferences.DRIVER_LICENSE_STATE, driver_license_state);
                        editor.putString(Preferences.DOB, dob);
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
                    String account_id = "";
                    String login = "";
                    String password = "";
                    JSONObject quickblox_accounts = null;
                    if(!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("quickblox_accounts")){
                        quickblox_accounts = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("quickblox_accounts");
                         account_id = quickblox_accounts.getString("account_id");
                         login = quickblox_accounts.getString("login");
                         password = quickblox_accounts.getString("qb_password");
                        editor.putString(Preferences.QB_ACCOUNT_ID, account_id);
                        editor.putString(Preferences.QB_LOGIN, login);
                        editor.putString(Preferences.QB_PASSWORD, password);
                    }

                    editor.commit();
//                    loginToQuickBlox(login, password);


                    // Login to REST API
                    //
                    final QBUser user = new QBUser();
//                    user.setLogin(login);
//                    user.setPassword(password);
                    user.setLogin(login);
                    user.setPassword(password);


                    ChatService.getInstance().login(user, new QBEntityCallback<Void>() {

                        @Override
                        public void onSuccess(Void result, Bundle bundle) {
                            // Go to Dialogs screen
                            //
                            Log.e("","success");
//                            Intent intent = new Intent(SplashActivity.this, DialogsActivity.class);
//                            startActivity(intent);
//                            finish();
                        }

                        @Override
                        public void onError(QBResponseException errors) {
//                            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
//                            dialog.setMessage("chat login errors: " + errors).create().show();
                            Log.e("","error");
                        }
                    });
                    handler.sendEmptyMessage(1);
                }else{
                    handler.sendEmptyMessage(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private void loginToQuickBlox(final String login, final String password){
//        QBAuth.createSession(new QBUser(login, password), new QBEntityCallback<QBSession>() {
//            @Override
//            public void onSuccess(QBSession session, Bundle params) {
//                // success
//                Log.e("","");
//            }
//
//            @Override
//            public void onError(QBResponseException error) {
//                // errors
//                Log.e("","");
//            }
//        });
        QBAuth.createSession(new QBEntityCallback<QBSession>() {

            @Override
            public void onSuccess(QBSession session, Bundle params) {
                // You have successfully created the session
                //
                // Now you can use QuickBlox API!
                Log.e("","Session Success");
                QBUser user = new QBUser(login, password);

                QBUsers.signIn(user, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle params) {
                        Log.e("","Login Success");
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.e("","Login Error");
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {
            Log.e("","Session Error");
            }
        });
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    showAlertDialog("Fixd-Pro","invalid login credentials.");
                    break;
                }case 1:{
//                    showAlertDialog("Fixd-Pro","Success");
                    Intent i = new Intent(LoginActivity.this, HomeScreenNew.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();

                    break;
                }
                default:{

                }
            }
        }
    };
    private void setWidgets(){
        txtBack = (ImageView)findViewById(R.id.txtBack);
        txtDone = (TextView)findViewById(R.id.txtDone);

        txtPhone = (EditText)findViewById(R.id.txtPhone);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtForgetPassword = (TextView)findViewById(R.id.txtForgetPassword);
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
    private HashMap<String,String> getLoginRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();

		 hashMap.put("api", "login");
        hashMap.put("data[phone_email]", Phone);
        hashMap.put("object","users");
        hashMap.put("data[password]", Password);
        hashMap.put("data[role]", "pro");
        return hashMap;
    }
    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            if (intent != null){
                token = intent.getStringExtra("token");
            }
        }
    };

}
