package fixtpro.com.fixtpro.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.Register_Activity;
import fixtpro.com.fixtpro.UserProfileScreen;
import fixtpro.com.fixtpro.net.GetApiResponseAsyncNew;
import fixtpro.com.fixtpro.net.IHttpExceptionListener;
import fixtpro.com.fixtpro.net.IHttpResponseListener;
import fixtpro.com.fixtpro.utilites.CheckAlertDialog;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;


public class SignUp_Account_Activity extends AppCompatActivity {
    Activity activity = SignUp_Account_Activity.this;
    public static final String TAG = "SignUp_Account_Activity";
    CheckAlertDialog checkALert;
    boolean iscompleting = false;
    EditText txtFirstName, txtLastName, txtMobile, txtEmail, txtPassword, txtConfirmPassword, txtYearExp;
    ImageView imgClose, imgNext;
    LinearLayout layoutNext;
    public String FirstName = "", LastName = "", EmailAddress = "", Password = "", ConfirmPassword = "", MobileNumber = "",YearsExp = "",
            Address= "", Description = "", PlaceID = "", ValueName = "", ZipCode = "", City = "", State = "", error_message = "", Address1 = "", Address2 = "", StateAbre = "", Id = "";
    HashMap<String,String> finalRequestParams = null ;
    boolean ispro = false ;
    SharedPreferences _prefs = null ;
    View years_exp_view;
    TextView txtAccountInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__account_);
        checkALert = new CheckAlertDialog();
        /*Getting Object in Intent*/
        Description = getIntent().getStringExtra("description");
        PlaceID = getIntent().getStringExtra("placeid");
        ValueName = getIntent().getStringExtra("valuename");
        _prefs = Utilities.getSharedPreferences(this);
        setWdigets();

        setCLickListner();
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")){
                finalRequestParams = (HashMap<String,String>)bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")){
                ispro = bundle.getBoolean("ispro");

            } if (bundle.containsKey("iscompleting")){
                iscompleting = bundle.getBoolean("iscompleting");
            }
        }

        if (iscompleting && ispro){
            txtPassword.setVisibility(View.GONE);
            txtConfirmPassword.setVisibility(View.GONE);
            txtAccountInfo.setText("Account Information (Review)");
            iniLayout();
        }else if(!ispro){
           txtYearExp.setVisibility(View.VISIBLE);
           years_exp_view.setVisibility(View.VISIBLE);
            txtEmail.setEnabled(false);
            txtMobile.setEnabled(false);
            iniLayout();
        }
    }
    private void iniLayout(){
        FirstName = _prefs.getString(Preferences.FIRST_NAME,"");
        LastName = _prefs.getString(Preferences.LAST_NAME,"");
        MobileNumber = _prefs.getString(Preferences.PHONE,"");
        EmailAddress = _prefs.getString(Preferences.EMAIL, "");
        txtFirstName.setText(FirstName);
        txtLastName.setText(LastName);
        txtMobile.setText(MobileNumber);
        txtEmail.setText(EmailAddress);
    }
    private void setWdigets() {
        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtMobile = (EditText) findViewById(R.id.txtMobile);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        txtYearExp = (EditText) findViewById(R.id.txtYearExp);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgClose = (ImageView) findViewById(R.id.imgClose);
        layoutNext = (LinearLayout) findViewById(R.id.layoutNext);

        years_exp_view = (View)findViewById(R.id.years_exp_view);
        txtAccountInfo = (TextView)findViewById(R.id.txtAccountInfo);
    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            }
        });
        layoutNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstName = txtFirstName.getText().toString().trim();
                LastName = txtLastName.getText().toString().trim();
                EmailAddress = txtEmail.getText().toString().trim();
                Password = txtPassword.getText().toString().trim();
                ConfirmPassword = txtConfirmPassword.getText().toString().trim();
                MobileNumber = txtMobile.getText().toString().trim();
                YearsExp = txtYearExp.getText().toString().trim();

                if (FirstName.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter your first name.");
                } else if (false) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter last name.");
                } else if (MobileNumber.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter mobile number.");
                } else if (MobileNumber.length() < 10) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Your phone number seems to invalid, Please try again.");
                }else if (EmailAddress.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter email address.");
                } else if (!Utilities.isValidEmail(EmailAddress)) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter valid email address.");
                } else if (!ispro && YearsExp.length() == 0) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter a value of 'Years Experience' before proceeding.");
                }else if (Password.equals("") && !iscompleting) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter password.");
                } else if (Password.length() < 6 && !iscompleting) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Your password should be 6 or more characters long, Please try again.");
                } else if (ConfirmPassword.equals("") && !iscompleting) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter confirm password.");
                } else if (!ConfirmPassword.equals(Password) && !iscompleting) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Confirm password is not match the password,Please enter the valid password.");
                }  else {
                    if (!iscompleting && ispro){
                        GetApiResponseAsyncNew responseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",checkIfPhoneExistsListener,exceptionListener,SignUp_Account_Activity.this,"Loading");
                        responseAsync.execute(getCheckPhoneRequestParams());
                    }else{
                        gotoNextStep();
                    }

//                        gotoNextStep();
                }
            }
        });
    }

    IHttpResponseListener checkIfPhoneExistsListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {
            Log.e("", "Response" + response.toString());
            try {
                String status = response.getString("STATUS");
                if (status.equals("SUCCESS")){
                    JSONArray Data = response.getJSONArray("RESPONSE");
//                    Check if Phone Already Exist
                    if (Data.length() == 0){
                        // check for email
                        handler.sendEmptyMessage(2);
                    }else{
                        error_message = "Phone number already exist.";
                        handler.sendEmptyMessage(1);
                    }
                }else{
//              Check for Errors
                    JSONObject errors = response.getJSONObject("ERRORS");
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
                    gotoNextStep();
                    break;
                }
                case 1:{
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }
                case 2:{
                    GetApiResponseAsyncNew responseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", checkIfEmailExistsListener,exceptionListener,SignUp_Account_Activity.this, "Loading");
                     responseAsync.execute(getCheckEmailRequestParams());
                    break;
                }
                default:{
                    showAlertDialog("Fixd-pro",error_message);
                    break;
                }
            }
        }
    };
    IHttpResponseListener checkIfEmailExistsListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {
            Log.e("", "Response" + response.toString());
            try {
                String status = response.getString("STATUS");
                if (status.equals("SUCCESS")) {
                    JSONArray Data = response.getJSONArray("RESPONSE");
//                    Check if Phone Already Exist
                    if (Data.length() == 0) {
                        handler.sendEmptyMessage(0);
                    } else {
                        error_message = "Email already exist.";
                        handler.sendEmptyMessage(1);
                    }
                } else {
//              Check for Errors
                    JSONObject errors = response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                        handler.sendEmptyMessage(2);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private void showAlertDialog(String Title,String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SignUp_Account_Activity.this);

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
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {

        }
    };
    private void gotoNextStep(){
        if (ispro){
            finalRequestParams.put("data[technicians][first_name]", FirstName);
            finalRequestParams.put("data[technicians][last_name]", LastName);
            finalRequestParams.put("data[users][email]", EmailAddress);
            finalRequestParams.put("data[users][phone]", MobileNumber);
            if (!iscompleting){
                Intent intent = new Intent(SignUp_Account_Activity.this, TradeSkills_Activity.class);
                finalRequestParams.put("data[users][password]", Password);
                intent.putExtra("finalRequestParams", finalRequestParams);
                intent.putExtra("ispro",ispro);
                intent.putExtra("iscompleting",iscompleting);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }else {
                Intent intent = new Intent(SignUp_Account_Activity.this, CompanyInformation_Activity.class);
                intent.putExtra("finalRequestParams",finalRequestParams);
                intent.putExtra("ispro",ispro);
                intent.putExtra("iscompleting",iscompleting);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }

        }else {
            Intent intent = new Intent(SignUp_Account_Activity.this,LicensePicture_Activity.class);
            finalRequestParams.put("data[technicians][first_name]", FirstName);
            finalRequestParams.put("data[technicians][last_name]", LastName);
            finalRequestParams.put("data[email]", EmailAddress);
            finalRequestParams.put("data[phone]",MobileNumber);
            finalRequestParams.put("password",Password);
            finalRequestParams.put("years_in_business",YearsExp);
            intent.putExtra("ispro", ispro);
            intent.putExtra("finalRequestParams", finalRequestParams);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    private HashMap<String,String> getCheckPhoneRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
        hashMap.put("object","users");
        hashMap.put("select", "id");
        hashMap.put("where[phone]", MobileNumber);
        return hashMap;
    }

    private HashMap<String, String> getCheckEmailRequestParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "users");
        hashMap.put("select", "id");
        hashMap.put("where[email]", EmailAddress);
        return hashMap;
    }

}
