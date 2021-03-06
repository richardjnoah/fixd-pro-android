package fixdpro.com.fixdpro.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.adapters.CityListAdapter;
import fixdpro.com.fixdpro.adapters.StateListAdapter;
import fixdpro.com.fixdpro.beans.CityBeans;
import fixdpro.com.fixdpro.beans.GoogleResponseBean;
import fixdpro.com.fixdpro.net.GetApiResponseAsync;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.CheckAlertDialog;

import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.Utilities;

public class New_Address_Activity extends AppCompatActivity  implements TextView.OnEditorActionListener {
    Activity activity = New_Address_Activity.this;
    public static final String TAG = "New_Address_Activity";
    ImageView imgClose,imgFinish;
    TextView txtFinish,txtCity,txtState,txtAddressBar;
    EditText txtAddress1,txtAddress2,txtZipCode;
    LinearLayout layoutFinish;
    ArrayList<CityBeans> arrayListCityBeans  = new ArrayList<CityBeans>();
    Dialog dialog1 = null ;
    String Address1 = "";
    String Address2 = "";
    String ZipCode = "";
    String City = "";
    String State = "";
    String StateAbre = "";
    String error_message = "",Id = "";
    private String error_msg_update_profile = "";
    CheckAlertDialog checkALert;
    SharedPreferences _prefs = null ;
    GoogleResponseBean modal = null ;
    boolean ispro = false ;
    boolean iscompleting = false ;
    HashMap<String,String> finalRequestParams = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__address_);
        checkALert = new CheckAlertDialog();
        _prefs = Utilities.getSharedPreferences(activity);
        setWidgets();
        setClickListner();
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras() ;
            if (bundle.containsKey("modal")){
                modal = (GoogleResponseBean)getIntent().getSerializableExtra("modal");
                initLayout();
            }

            if (bundle.containsKey("ispro"))
                ispro = bundle.getBoolean("ispro");
            if (bundle.containsKey("iscompleting"))
                iscompleting = bundle.getBoolean("iscompleting");
            if (bundle.containsKey("finalRequestParams"))
                finalRequestParams = (HashMap<String,String>)getIntent().getSerializableExtra("finalRequestParams");
            if (iscompleting){
                txtAddressBar.setText("Address (Review)");
            }
        }
    }
    private void initLayout(){
        if (modal.getmAddress1()  != null){
            txtAddress1.setText(modal.getmAddress1());
        }if (modal.getmAddress2()  != null) {
            txtAddress2.setText(modal.getmAddress2());
        }if (modal.getmZip()  != null){
            txtZipCode.setText(modal.getmZip());
        }if (modal.getmCity()  != null){
            txtCity.setText(modal.getmCity());
        }if (modal.getmState()  != null){
            txtState.setText(modal.getmState());
        }

    }
    private void setWidgets() {
        imgClose = (ImageView)findViewById(R.id.imgClose);
        imgFinish = (ImageView)findViewById(R.id.imgFinish);
        txtAddress1 = (EditText)findViewById(R.id.txtAddress1);
        txtAddress2 = (EditText)findViewById(R.id.txtAddress2);
        txtZipCode = (EditText)findViewById(R.id.txtZipCode);
        txtZipCode.setOnEditorActionListener(this);
        txtCity = (TextView)findViewById(R.id.txtCity);
        txtState = (TextView)findViewById(R.id.txtState);
        txtFinish = (TextView)findViewById(R.id.txtFinish);
        txtAddressBar = (TextView)findViewById(R.id.txtAddressBar);
        layoutFinish = (LinearLayout)findViewById(R.id.layoutFinish);
    }
    private void setClickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayListCityBeans.size() == 0) {
                    if (txtZipCode.getText().toString().trim().equals("")) {
                        checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter zip code.");
                    } else {
                        Utilities.hideKeyBoad(activity, v);
                        GetApiResponseAsync apiResponseAsync = new GetApiResponseAsync(Constants.BASE_URL, "POST", zipResponseListener, zipExceptionListener, activity, "Getting");
                        apiResponseAsync.execute(getZipRequestParams());
                    }
                }else {
                    showcityList();
                }
            }
        });
        txtState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStateList();
            }
        });
        imgFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address1 = txtAddress1.getText().toString().trim();
                Address2 = txtAddress2.getText().toString().trim();
                ZipCode = txtZipCode.getText().toString().trim();
                City = txtCity.getText().toString().trim();
                State = txtState.getText().toString().trim();
                if (Address1.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter Address1.");
                } else if (false) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter Address2.");
                } else  if (ZipCode.equals("")){
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter zip code.");
                } else if (City.equals("")){
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter city.");
                } else if (State.equals("")){
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter state.");
                } else {
                    GetApiResponseAsync apiResponseAsync = new GetApiResponseAsync(Constants.BASE_URL, "POST", profileUpdateResponseListener, profileExceptionListener, activity, "Getting");
                    apiResponseAsync.execute(getProfileUpdateRequestParams());
                    // TODO: MH -- Check Address validity from server here
//                    startNextActivity();
                }
            }
        });
    }

    private void startNextActivity() {
        finalRequestParams.put("data[pros][address]", Address1);
        finalRequestParams.put("data[pros][address_2]", Address2);
        finalRequestParams.put("data[pros][city]", City);
        finalRequestParams.put("data[pros][zip]", ZipCode);
        finalRequestParams.put("data[pros][state]", State);
        Intent intent = new Intent(activity,SignUp_Account_Activity.class);
        intent.putExtra("finalRequestParams",finalRequestParams);
        intent.putExtra("ispro",ispro);
        intent.putExtra("ispro",ispro);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.txtZipCode && actionId == EditorInfo.IME_ACTION_DONE ){
            Utilities.hideKeyBoad(activity,v);
            GetApiResponseAsync apiResponseAsync = new GetApiResponseAsync(Constants.BASE_URL,"POST",zipResponseListener,zipExceptionListener,activity,"Getting");
            apiResponseAsync.execute(getZipRequestParams());
        }
        return true;
    }

    IHttpResponseListener zipResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {
            Log.e("", "response" + response);
            try {
                if (response.getString("STATUS").equals("SUCCESS")) {
                    JSONArray results = response.getJSONObject("RESPONSE").getJSONArray("results");
                    if (results.length() == 0){
                        error_message = getResources().getString(R.string.error_msg_enter_valid_zip);
                        handler.sendEmptyMessage(4);
                    } else {
                        arrayListCityBeans.clear();
                        for (int i = 0 ; i < results.length() ; i++) {
                            JSONObject jsonObject = results.getJSONObject(i);
                            CityBeans cityBeans = new CityBeans();
                            City = jsonObject.getString("cityname");
                            State = jsonObject.getString("statename");
                            StateAbre = jsonObject.getString("stateabbr");
                            Id = jsonObject.getString("id");
                            cityBeans.setId(Id);
                            cityBeans.setCityname(City);
                            cityBeans.setStateabbr(StateAbre);
                            cityBeans.setStatename(State);
                            arrayListCityBeans.add(cityBeans);
                        }

                        handler.sendEmptyMessage(2);
                    }

                }
                else{
                    JSONObject errors = response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    IHttpExceptionListener zipExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            Log.e("","response"+exception);
            error_message = exception ;
            handler.sendEmptyMessage(1);
        }
    };

    IHttpResponseListener profileUpdateResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {
            try {
                Log.i(TAG, "handleResponse: " + response);
                if (response.getString("STATUS").equals("SUCCESS")) {
                    startNextActivity();
                } else {
                    JSONObject errors = response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(3);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    IHttpExceptionListener profileExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {

        }
    };

    private HashMap<String,String> getZipRequestParams() {
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
        hashMap.put("object", "zipcodes");
        hashMap.put("per_page", 20 + "");
        hashMap.put("page", 1 + "");
        hashMap.put("where[zipcode]", txtZipCode.getText().toString());
        return hashMap;
    }

    private HashMap<String,String> getProfileUpdateRequestParams() {
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","update");
        hashMap.put("object", "pros");
        hashMap.put("token", "$2y$10$mG.FhawLE.YovBDLHyapZubRPLJkexyQm8upWn44EQgQwGdCmoWo2");
        hashMap.put("data|user_services]|0]", "6");
        hashMap.put("data[pros][address]", Address1);
        hashMap.put("data[pros][address_2]", Address2);
        hashMap.put("data[pros][zip]", ZipCode);
        hashMap.put("data[pros][city]", City);
        hashMap.put("data[pros][state]", State);
        return hashMap;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                case 4: {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title),
                            activity.getResources().getString(R.string.error_msg_enter_valid_zip));
                    break;
                }
                case 2: {
                    if (arrayListCityBeans.size() > 1) {
                        showcityList();
                    } else {
                        txtState.setText(State);
                        txtCity.setText(City);
                    }
                    break;
                }
                case 3: {
                    checkALert.showcheckAlert(activity,
                            activity.getResources().getString(R.string.alert_title),
                            error_msg_update_profile);
                    break;
                }
            }
        }
    };


    private void showcityList(){
        dialog1 = new Dialog(activity);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.layout_tellus_more);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog1 components -listview
        ListView lst_TellUsMore = (ListView) dialog1.findViewById(R.id.lst_TellUsMore);
                /*To set the Adapter*/
        CityListAdapter mAdp = new CityListAdapter(arrayListCityBeans, activity);
        lst_TellUsMore.setAdapter(mAdp);
        lst_TellUsMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog1.dismiss();
                txtState.setText(arrayListCityBeans.get(position).getStateabbr());
                State = arrayListCityBeans.get(position).getStateabbr();
                txtCity.setText(arrayListCityBeans.get(position).getCityname());
                City = arrayListCityBeans.get(position).getCityname();
            }
        });
        dialog1.show();
    }
    private void showStateList(){
        dialog1 = new Dialog(activity);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.layout_tellus_more);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog1 components -listview
        ListView lst_TellUsMore = (ListView) dialog1.findViewById(R.id.lst_TellUsMore);
                /*To set the Adapter*/
        StateListAdapter mAdp = new StateListAdapter(Utilities.getStateList(), New_Address_Activity.this);
        lst_TellUsMore.setAdapter(mAdp);
        lst_TellUsMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog1.dismiss();
                txtState.setText(Utilities.getStateList().get(position));
                State = Utilities.getStateList().get(position);
//                txtCity.setText(arrayListCityBeans.get(position).getCityname());
//                City = arrayListCityBeans.get(position).getCityname();
            }
        });
        dialog1.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }
}
