package fixtpro.com.fixtpro.activities;

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

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.adapters.CityListAdapter;
import fixtpro.com.fixtpro.adapters.StateListAdapter;
import fixtpro.com.fixtpro.beans.CityBeans;
import fixtpro.com.fixtpro.fragment.CompanyInformation_Activity_Edit;
import fixtpro.com.fixtpro.net.GetApiResponseAsync;
import fixtpro.com.fixtpro.net.GetApiResponseAsyncNew;
import fixtpro.com.fixtpro.net.IHttpExceptionListener;
import fixtpro.com.fixtpro.net.IHttpResponseListener;
import fixtpro.com.fixtpro.utilites.CheckAlertDialog;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class SetupCompleteAddressActivity_Edit extends AppCompatActivity implements TextView.OnEditorActionListener {
    Activity activity = SetupCompleteAddressActivity_Edit.this;
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
    CheckAlertDialog checkALert;
    SharedPreferences _prefs = null ;
    String mAddress1 = "",mAddress2 = "",mState = "",mZip = "",mCity = "";
    boolean ispro = false ;
    boolean iscompleting = false ;
    HashMap<String,String> finalRequestParams = new HashMap<String,String>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_complete_address_activity__edit);
        checkALert = new CheckAlertDialog();
        _prefs = Utilities.getSharedPreferences(activity);
        if (_prefs.getString(Preferences.ROLE,"pro").equals("pro")){
            ispro = true ;
        }
        setWidgets();
        setClickListner();
        initLayout();
    }
    private void initLayout(){
        mAddress1 = _prefs.getString(Preferences.ADDRESS,"");
        txtAddress1.setText(mAddress1);
        mAddress2 = _prefs.getString(Preferences.ADDRESS2,"");
        txtAddress2.setText(mAddress2);
        mZip = _prefs.getString(Preferences.ZIP,"");
        txtZipCode.setText(mZip);
        mCity = _prefs.getString(Preferences.CITY,"");
        txtCity.setText(mCity);
        mState = _prefs.getString(Preferences.STATE,"");
        txtState.setText(mState);
    }

    private void setWidgets() {
        imgClose = (ImageView)findViewById(R.id.imgClose);
        imgFinish = (ImageView)findViewById(R.id.imgFinish);
        txtAddress1 = (EditText)findViewById(R.id.txtAddress1);
        txtAddress2 = (EditText)findViewById(R.id.txtAddress2);
        txtZipCode = (EditText)findViewById(R.id.txtZipCode);
        txtZipCode.setOnEditorActionListener(this);
        txtCity = (TextView)findViewById(R.id.txtCity);
        txtAddressBar = (TextView)findViewById(R.id.txtAddressBar);
        txtState = (TextView)findViewById(R.id.txtState);
        txtFinish = (TextView)findViewById(R.id.txtFinish);
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
                if (Address1.equals("")){
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter Address1.");
                }else if (false){
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter Address2.");
                }else  if (ZipCode.equals("")){
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter zip code.");
                }else if (City.equals("")){
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter city.");
                }else if (State.equals("")){
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.alert_title), "Please enter state.");
                }else{
                    finalRequestParams.put("api", "update");
                    if (ispro)
                        finalRequestParams.put("object", "pros");
                    else
                        finalRequestParams.put("object", "technician");
                    finalRequestParams.put("data[pros][address]", Address1);
                    finalRequestParams.put("data[pros][address_2]", Address2);
                    finalRequestParams.put("data[pros][city]", City);
                    finalRequestParams.put("data[pros][zip]", ZipCode);
                    finalRequestParams.put("data[pros][state]", State);
                    finalRequestParams.put("token",_prefs.getString(Preferences.AUTH_TOKEN,""));
                    GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",updateResponseListener,exceptionListener,SetupCompleteAddressActivity_Edit.this,"");
                    getApiResponseAsyncNew.execute(finalRequestParams);

                }
            }
        });
    }
    IHttpResponseListener updateResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response.toString()" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    SharedPreferences.Editor editor = _prefs.edit();
                    editor.putString(Preferences.ADDRESS, Address1);
                    editor.putString(Preferences.ADDRESS2, Address2);
                    editor.putString(Preferences.CITY, City);
                    editor.putString(Preferences.ZIP, ZipCode);
                    editor.putString(Preferences.STATE, State);
                    editor.commit();
                    finish();
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                        handler.sendEmptyMessage(1);
                    }
                }

            } catch (JSONException e) {

            }
        }
    };
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener(){

        @Override
        public void handleException(String exception) {
            error_message = exception;
            handler.sendEmptyMessage(1);
        }
    };
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
                        error_message = "Please enter valid zip code" ;
                        handler.sendEmptyMessage(4);
                    }else {
                        arrayListCityBeans.clear();
                        for (int i = 0 ; i < results.length() ; i++){
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


    private HashMap<String,String> getZipRequestParams() {
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
        hashMap.put("object", "zipcodes");
        hashMap.put("per_page", 20 + "");
        hashMap.put("page", 1 + "");
        hashMap.put("where[zipcode]", txtZipCode.getText().toString());
        return hashMap;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:{
                    checkALert.showcheckAlert(activity,activity.getResources().getString(R.string.alert_title),"Please enter the valid zip code.");
                    break;
                }
                case 2:{
                    if (arrayListCityBeans.size() > 1){
                        showcityList();
                    }else {
                        txtState.setText(State);
                        txtCity.setText(City);
                    }
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
        StateListAdapter mAdp = new StateListAdapter(Utilities.getStateList(), SetupCompleteAddressActivity_Edit.this);
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
        finish();
    }
}


