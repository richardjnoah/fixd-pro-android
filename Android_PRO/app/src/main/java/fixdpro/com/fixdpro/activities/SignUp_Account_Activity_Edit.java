package fixdpro.com.fixdpro.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.CheckAlertDialog;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

public class SignUp_Account_Activity_Edit extends AppCompatActivity {
    Activity activity = SignUp_Account_Activity_Edit.this;
    public static final String TAG = "SignUp_Account_Activity_Edit";
    CheckAlertDialog checkALert;
    EditText txtFirstName, txtLastName, txtMobile, txtEmail, txtPassword, txtConfirmPassword, txtYearExp;
    ImageView imgClose, imgNext;
    LinearLayout layoutNext;
    public String FirstName = "", LastName = "", EmailAddress = "", Password = "", ConfirmPassword = "", MobileNumber = "",YearsExp = "",
            Address= "", Description = "", PlaceID = "", ValueName = "", ZipCode = "", City = "", State = "", error_message = "", Address1 = "", Address2 = "", StateAbre = "", Id = "";
    HashMap<String,String> finalRequestParams = null ;

    SharedPreferences _prefs = null ;
    View years_exp_view;
    TextView txtAccountInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__account__activity__edit);
        checkALert = new CheckAlertDialog();
        _prefs = Utilities.getSharedPreferences(this);
        setWdigets();
        setCLickListner();
        if (_prefs.getString(Preferences.ROLE,"").equals("pro")){
            txtPassword.setVisibility(View.GONE);
            txtConfirmPassword.setVisibility(View.GONE);
            txtYearExp.setVisibility(View.GONE);
            txtMobile.setEnabled(false);
        }else
        {
            txtPassword.setVisibility(View.GONE);
            txtConfirmPassword.setVisibility(View.GONE);
            txtYearExp.setVisibility(View.VISIBLE);
            txtMobile.setEnabled(false);
            txtEmail.setEnabled(false);
        }
        iniLayout();
    }
    private void iniLayout(){
        FirstName = _prefs.getString(Preferences.FIRST_NAME,"");
        LastName = _prefs.getString(Preferences.LAST_NAME,"");
        MobileNumber = _prefs.getString(Preferences.PHONE,"");
        EmailAddress = _prefs.getString(Preferences.EMAIL, "");
        YearsExp = _prefs.getString(Preferences.YEARS_IN_BUSINESS, "");
        txtFirstName.setText(FirstName);
        txtLastName.setText(LastName);
        txtMobile.setText(MobileNumber);
        txtEmail.setText(EmailAddress);
        txtYearExp.setText(YearsExp);
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
            }
        });
        layoutNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstName = txtFirstName.getText().toString().trim();
                LastName = txtLastName.getText().toString().trim();
                EmailAddress = txtEmail.getText().toString().trim();
                YearsExp = txtYearExp.getText().toString().trim();
                if (FirstName.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter first name.");
                } else if (LastName.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter last name.");
                } else if (EmailAddress.equals("") && _prefs.getString(Preferences.ROLE,"").equals("pro")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter email address.");
                } else if (!Utilities.isValidEmail(EmailAddress) && _prefs.getString(Preferences.ROLE,"pro").equals("pro")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter valid email address.");
                } else if (YearsExp.equals("") &&  !_prefs.getString(Preferences.ROLE,"pro").equals("pro")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter experience.");
                } else {
                    Intent intent = new Intent(SignUp_Account_Activity_Edit.this,LicensePicture_Activity_Edit.class);
                    intent.putExtra("finalRequestParams",getProfileUpdateParameters());
                    startActivity(intent);
                    finish();
//                        GetApiResponseAsyncNew apiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL, "POST", updateResponseListener, updateExceptionListener, SignUp_Account_Activity_Edit.this, "");
//                        apiResponseAsyncNew.execute(getProfileUpdateParameters());
                }
            }
        });
    }
    IHttpResponseListener updateResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    SharedPreferences.Editor editor = _prefs.edit();
                    editor.putString(Preferences.FIRST_NAME, FirstName);
                    editor.putString(Preferences.LAST_NAME, LastName);
                    editor.putString(Preferences.EMAIL, EmailAddress);
                    JSONObject profile_image = null ;
                    profile_image = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").getJSONObject("profile_image");
                    if (!profile_image.isNull("original")){
                        String image_original =  profile_image.getString("original");
                        editor.putString(Preferences.PROFILE_IMAGE, image_original);
                    }
                    editor.commit();
                    finish();
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                        handler.sendEmptyMessage(0);
                    }
                }

            } catch (JSONException e) {

            }
        }
    };
    IHttpExceptionListener updateExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception;
            handler.sendEmptyMessage(0);
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showAlertDialog("Fixd-Pro",error_message);
        }
    };
    private void showAlertDialog(String Title, String Message) {
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
    private HashMap<String, String> getProfileUpdateParameters() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "update");
        if (_prefs.getString(Preferences.ROLE, "").equals("pro")){
            hashMap.put("object", "pros");
            hashMap.put("data[email]", EmailAddress);
        }
        else{
            hashMap.put("object", "technicians");
            hashMap.put("years_in_business", YearsExp);
        }
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("data[technicians][first_name]", FirstName);
        hashMap.put("data[technicians][last_name]", LastName);


        return hashMap;
    }

}
