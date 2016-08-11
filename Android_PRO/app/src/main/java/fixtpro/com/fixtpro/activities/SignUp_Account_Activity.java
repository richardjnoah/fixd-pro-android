package fixtpro.com.fixtpro.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.HashMap;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.utilites.CheckAlertDialog;
import fixtpro.com.fixtpro.utilites.Utilities;


public class SignUp_Account_Activity extends AppCompatActivity {
    Activity activity = SignUp_Account_Activity.this;
    public static final String TAG = "SignUp_Account_Activity";
    CheckAlertDialog checkALert;
    EditText txtFirstName, txtLastName, txtMobile, txtEmail, txtPassword, txtConfirmPassword;
    ImageView imgClose, imgNext;
    LinearLayout layoutNext;
    public String FirstName = "", LastName = "", EmailAddress = "", Password = "", ConfirmPassword = "", MobileNumber = "",
            Address = "", Description = "", PlaceID = "", ValueName = "", ZipCode = "", City = "", State = "", error_message = "", Address1 = "", Address2 = "", StateAbre = "", Id = "";
    HashMap<String,String> finalRequestParams = null ;
    boolean ispro = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__account_);
        checkALert = new CheckAlertDialog();
        /*Getting Object in Intent*/
        Description = getIntent().getStringExtra("description");
        PlaceID = getIntent().getStringExtra("placeid");
        ValueName = getIntent().getStringExtra("valuename");

        setWdigets();

        setCLickListner();
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")){
                finalRequestParams = (HashMap<String,String>)bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")){
                ispro = bundle.getBoolean("ispro");
            }
        }
    }

    private void setWdigets() {
        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtMobile = (EditText) findViewById(R.id.txtMobile);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgClose = (ImageView) findViewById(R.id.imgClose);
        layoutNext = (LinearLayout) findViewById(R.id.layoutNext);
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
                Password = txtPassword.getText().toString().trim();
                ConfirmPassword = txtConfirmPassword.getText().toString().trim();
                MobileNumber = txtMobile.getText().toString().trim();

                if (FirstName.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter first name.");
                } else if (LastName.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter last name.");
                } else if (EmailAddress.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter email address.");
                } else if (!Utilities.isValidEmail(EmailAddress)) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter valid email address.");
                } else if (Password.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter password.");
                } else if (Password.length() < 6) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Your password should be 6 or more characters long, Please try again.");
                } else if (ConfirmPassword.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter confirm password.");
                } else if (!ConfirmPassword.equals(Password)) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Confirm password is not match the password,Please enter the valid password.");
                } else if (MobileNumber.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter mobile number.");
                } else if (MobileNumber.length() < 10) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Your phone number seems to invalid, Please try again.");
                } else {
                    finalRequestParams.put("data[technicians][first_name]", FirstName);
                    finalRequestParams.put("data[technicians][last_name]", MobileNumber);
                    finalRequestParams.put("data[users][email]", EmailAddress);
                    finalRequestParams.put("data[users][phone]",MobileNumber);
                    finalRequestParams.put("data[users][password]",Password);
                    Intent intent = new Intent(SignUp_Account_Activity.this, TradeSkills_Activity.class);
                    intent.putExtra("finalRequestParams",finalRequestParams);
                    intent.putExtra("ispro",ispro);
                    startActivity(intent);

                }
            }
        });
    }
}
