package fixdpro.com.fixdpro.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.AddCardScreen;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.beans.TechnicianModal;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.singleton.TechniciansListSinglton;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

public class TechnicianInformation_Activity extends AppCompatActivity {
    Context context = this;
    ImageView imgClose, imgNext;
    EditText txtFirstName, txtLastName, txtEmailAdd, txtMobile,txtExperiance;
    CheckBox checkJobPickUp;
    public String firstName ="", lastName ="", email ="", mobileNumber ="", middleName ="",technician_id ="",YearsExp = "";
    boolean ispickUp_jobs = false;
    boolean isedit = false;
    SharedPreferences _prefs = null ;
    ArrayList<TechnicianModal> technicianModalsList = TechniciansListSinglton.getInstance().technicianModalsList;
    TechnicianModal modal;
    String Error = "";
    int position = 0 ;
    LinearLayout layout_card;
    TextView txtDeactivate, txtCardNumber, txtEditCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_information_);
        _prefs = Utilities.getSharedPreferences(this);

        setWidgets();
        if (_prefs.getString(Preferences.IS_VARIFIED,"0").equals("1")){
            layout_card.setVisibility(View.VISIBLE);
            txtExperiance.setVisibility(View.GONE);
        }
        setCLickListner();
        if (getIntent().getExtras() != null){
            if (getIntent().getExtras().containsKey("isedit"))

            isedit = getIntent().getBooleanExtra("isedit",isedit);
            if (isedit){
                    position = getIntent().getIntExtra("position", 0);
                    modal = (TechnicianModal)getIntent().getSerializableExtra("modal");
                    txtDeactivate.setVisibility(View.VISIBLE);
                    initLayout();
                }else {
                txtDeactivate.setVisibility(View.GONE);
            }


        }
    }
    private void initLayout(){
        txtFirstName.setText(modal.getFirstName());
        txtLastName.setText(modal.getLastName());
        txtEmailAdd.setText(modal.getEmail());
        txtMobile.setText(modal.getPhone());
        txtExperiance.setText(modal.getExperience());
        if (modal.ispickjob()){
            checkJobPickUp.setChecked(true);
        }else {
            checkJobPickUp.setChecked(false);
        }

    }
    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtEmailAdd = (EditText) findViewById(R.id.txtEmail);
        txtMobile = (EditText) findViewById(R.id.txtPhone);
        txtExperiance = (EditText) findViewById(R.id.txtExperiance);
        txtDeactivate = (TextView) findViewById(R.id.txtDeactivate);
        txtCardNumber = (TextView) findViewById(R.id.txtCardNumber);
        txtEditCard = (TextView) findViewById(R.id.txtEditCard);
        layout_card = (LinearLayout) findViewById(R.id.layout_card);
        checkJobPickUp = (CheckBox) findViewById(R.id.checkJobPickUp);

    }

    @Override
    protected void onResume() {
        super.onResume();
        txtCardNumber.setText("\u25CF\u25CF\u25CF\u25CF"+_prefs.getString(Preferences.CREDIT_CARD_NUMBER,""));
    }

    private void setCLickListner() {
        txtEditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TechnicianInformation_Activity.this, AddCardScreen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        txtDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",iHttpResponseListenerDeactivate,exceptionListener, TechnicianInformation_Activity.this, "Adding");
                getApiResponseAsyncNew.execute(getDeactivateTechParam());
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        checkJobPickUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ispickUp_jobs = isChecked;
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = txtFirstName.getText().toString().trim();
                lastName = txtLastName.getText().toString().trim();
                email = txtEmailAdd.getText().toString().trim();
                mobileNumber = txtMobile.getText().toString().trim();
                YearsExp = txtExperiance.getText().toString().trim();
                if (firstName.equals("")) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Please enter the first name.");
                } else if (lastName.equals("")) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Please enter the last name.");
                } else if (email.equals("")) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Please enter the email address.");
                } else if (!Utilities.isValidEmail(email)) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Please enter the valid email address.");
                } else if (mobileNumber.equals("")) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Please enter the mobile number.");
                } else if ((mobileNumber.length() < 10)) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Your phone number seems to invalid, Please try again.");
                } else if (YearsExp.equals("") &&  !_prefs.getString(Preferences.ROLE,"pro").equals("pro")) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title), "Please enter experience.");
                } else {
                    if (!isedit) {
                        addTech();
                    } else {
                        updateTech();
                    }
                }
            }
        });
    }

    IHttpResponseListener iHttpResponseListenerDeactivate = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            try {
                if(Response.getString("STATUS").equals("SUCCESS")){
                        technicianModalsList.remove(position);
                        handler.sendEmptyMessage(1);

                    finish();
                }else{
                    JSONObject ERRORS = Response.getJSONObject("ERRORS");
                    Iterator<String> keys2 = ERRORS.keys();
                    if (keys2.hasNext())
                        Error = ERRORS.getString(keys2.next());
                    handler.sendEmptyMessage(2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private void updateTech(){
        GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",responseListener,exceptionListener, TechnicianInformation_Activity.this, "Adding");
        getApiResponseAsyncNew.execute(getUpdateTechParam());
    }

    private HashMap<String,String> getAddTechParam(){
        HashMap<String,String> paramHashMap = new HashMap<String,String>();
        paramHashMap.put("object", "technicians");
        paramHashMap.put("api", "register");
        paramHashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN,""));
//        if (profile_image_file  != null)
//        paramHashMap.put("profile_image",new String(Utilities.convertFileToBytes(profile_image_file)));
        paramHashMap.put("data[first_name]", firstName);
        paramHashMap.put("data[last_name]", lastName);
        paramHashMap.put("data[email]", email);
        paramHashMap.put("data[phone]", mobileNumber);
        paramHashMap.put("data[years_in_business]",YearsExp);
        if (ispickUp_jobs)
            paramHashMap.put("data[pickup_jobs]", "1");
        else
            paramHashMap.put("data[pickup_jobs]", "0");
        return paramHashMap;
    }
    private HashMap<String,String> getUpdateTechParam(){
        HashMap<String,String> paramHashMap = new HashMap<String,String>();
        paramHashMap.put("object", "technicians");

        paramHashMap.put("data[first_name]", firstName);
        paramHashMap.put("data[last_name]", lastName);
        paramHashMap.put("api", "update");
        paramHashMap.put("data[user_id]", modal.getId());
        paramHashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        paramHashMap.put("data[years_in_business]",YearsExp);
//        if (profile_image_file  != null)
//        paramHashMap.put("profile_image",new String(Utilities.convertFileToBytes(profile_image_file)));

//        paramHashMap.put("data[email]", email);
//        paramHashMap.put("data[phone]", phone);
        if (ispickUp_jobs)
            paramHashMap.put("data[pickup_jobs]", "1");
        else
            paramHashMap.put("data[pickup_jobs]", "0");
        return paramHashMap;
    }
    private HashMap<String,String> getDeactivateTechParam(){
        HashMap<String,String> paramHashMap = new HashMap<String,String>();
        paramHashMap.put("object", "technicians");
        paramHashMap.put("api", "deactivate");
        paramHashMap.put("data[tech_user_id]", modal.getId());
        paramHashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN,""));
        paramHashMap.put("data[years_in_business]",YearsExp);
        return paramHashMap;
    }

    private void addTech(){
         GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",responseListener,exceptionListener, TechnicianInformation_Activity.this, "Adding");
         getApiResponseAsyncNew.execute(getAddTechParam());
    }

    IHttpResponseListener responseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS")){
                    TechnicianModal technicianModal = new TechnicianModal();
                    JSONObject RESPONSE = Response.getJSONObject("RESPONSE");
                    String id = RESPONSE.getString("user_id");
//                    String email = RESPONSE.getString("email");
//                    String phone = RESPONSE.getString("phone");
//                    JSONObject technicians = RESPONSE.getJSONObject("users");
                    String fname = RESPONSE.getString("first_name");
                    String lname = RESPONSE.getString("last_name");
                    String pick_up = RESPONSE.getString("pickup_jobs");
                    technicianModal.setId(id);
                    technicianModal.setEmail(txtEmailAdd.getText().toString().trim());
                    technicianModal.setPhone(txtMobile.getText().toString().trim());
                    technicianModal.setFirstName(fname);
                    technicianModal.setLastName(lname);
                    technicianModal.setExperience(YearsExp);
//                    if (!technicians.isNull("profile_image")){
//                        JSONObject profile_image = technicians.getJSONObject("profile_image");
//                        if (!profile_image.isNull("original"))
//                            technicianModal.setProfile_image(profile_image.getString("original"));
//                    }
                    if (pick_up.equals("1")){
                        technicianModal.setIspickjob(true);
                    }else{
                        technicianModal.setIspickjob(false);
                    }
                    if (isedit){
                        technicianModalsList.remove(position);
                        technicianModalsList.add(position,technicianModal);
                        handler.sendEmptyMessage(1);
                    }else{
                        technicianModalsList.add(technicianModal);
                        handler.sendEmptyMessage(0);
                    }
                    finish();
                }else{
                    JSONObject ERRORS = Response.getJSONObject("ERRORS");
                    Iterator<String> keys2 = ERRORS.keys();
                    if (keys2.hasNext())
                        Error = ERRORS.getString(keys2.next());
                    handler.sendEmptyMessage(2);
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
//                    set ADAPTER

//                    adapter = new TechnicianAdapter(Add_TechScreen.this,technicianModalsList,getResources());
//                    lstTechnicians.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                    finish();
                    break;
                }
                case 1:{
//                    noty
                    finish();
//                    adapter.notifyDataSetChanged();
                    break;
                }
                case 2:{

                    showAlertDialog("Fixd-Pro", Error);
                    break;
                }
                case 500: {

                    showAlertDialog("Fixd-pro", "Server Error 500");
                    break;
                }
            }
        }
    };
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }

    private void showAlertDialog(String Title, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                TechnicianInformation_Activity.this);

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
}
