package fixtpro.com.fixtpro.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.adapters.GooglePlaceAdapter;
import fixtpro.com.fixtpro.beans.GoogleResponseBean;
import fixtpro.com.fixtpro.net.GetApiResponseAsyncNoProgress;
import fixtpro.com.fixtpro.net.IHttpExceptionListener;
import fixtpro.com.fixtpro.net.IHttpResponseListener;
import fixtpro.com.fixtpro.utilites.CheckAlertDialog;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.Utilities;
import fixtpro.com.fixtpro.views.CircularProgressView;

public class SignUp_AddressActivity extends AppCompatActivity {
    Activity activity = SignUp_AddressActivity.this;
    public static final String TAG = "SignUp_AddressActivityl";
    CheckAlertDialog checkALert;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    EditText txtAddress;
    ImageView imgClose, imgNext;
    TextView txtNext;
    LinearLayout layoutNext;
    CircularProgressView progressView ;
    ArrayList<GoogleResponseBean> arrayList = new ArrayList<GoogleResponseBean>();
    ArrayList<GoogleResponseBean> arrayListTemp = new ArrayList<GoogleResponseBean>();
    ListView lstPlaces ;
    GooglePlaceAdapter adapter = null ;
    String locality,street,mAddress1,mAddress2,mState,mZip,mCity;
    GoogleResponseBean modal = null ;
    String EditAddress,Description,PlaceID,ValueName;
    HashMap<String,String> finalRequestParams = new HashMap<String,String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__address);
        prefs = Utilities.getSharedPreferences(activity);
        editor = prefs.edit();
        checkALert = new CheckAlertDialog();
        GoogleResponseBean modal = new GoogleResponseBean();
        setWidgets();
        setClickListner();
        adapter = new GooglePlaceAdapter(this,arrayList,getResources());
        lstPlaces.setAdapter(adapter);
    }


    private void setWidgets() {
        txtAddress = (EditText) findViewById(R.id.txtAddress);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtNext = (TextView) findViewById(R.id.txtNext);
        layoutNext = (LinearLayout) findViewById(R.id.layoutNext);
        lstPlaces = (ListView) findViewById(R.id.lstPlaces);

    }

    private void setClickListner() {
        txtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtAddress.getText().toString().length() > 0) {
                    lstPlaces.setVisibility(View.VISIBLE);
                    arrayListTemp.clear();
                    getGoogleResults(s.toString());
                } else {
                    lstPlaces.setVisibility(View.GONE);
                }

            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAddress = txtAddress.getText().toString();
                if (EditAddress.equals("")) {
                    checkALert.showcheckAlert(activity, activity.getResources().getString(R.string.app_name), "Please enter address.");
                } else {

//                    Intent intent = new Intent(activity,SignUp_Account_Activity.class);
//                    intent.putExtra("description",Description);
//                    intent.putExtra("placeid",PlaceID);
//                    intent.putExtra("valuename",ValueName);
//                    startActivity(intent);
                }
            }
        });

        lstPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modal = arrayList.get(position);
                getGoogleResultInDetails(modal.getPlace_id());
            }
        });
    }
    private void getGoogleResultInDetails(String id){
        String Url = "https://maps.googleapis.com/maps/api/place/details/json";
//        String Url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+placeName+"&language=en&key="+ Constants.GOOGLE_PLACE_SERVER_KEY+"&components=country:us";
        GetApiResponseAsyncNoProgress getApiResponseAsync = new GetApiResponseAsyncNoProgress(Url,"GET",iHttpResponseListener,exceptionListener,this,"Loading");
        getApiResponseAsync.execute(getRequestParamsForDetails(id));
    }
    private void getGoogleResults(String placeName){
        String Url = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
        GetApiResponseAsyncNoProgress getApiResponseAsync = new GetApiResponseAsyncNoProgress(Url,"GET",googlePlaceResponseListener,exceptionListener,this,"Loading");
        getApiResponseAsync.execute(getRequestParams(placeName));
    }

    private HashMap<String,String> getRequestParams(String input){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("language","en");
        hashMap.put("input",input);
        hashMap.put("key", Constants.GOOGLE_PLACE_SERVER_KEY);
        hashMap.put("components", "country:us");
        return hashMap;
    }
    private HashMap<String,String> getRequestParamsForDetails(String id){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("placeid",id);
        hashMap.put("key", Constants.GOOGLE_PLACE_SERVER_KEY);
        return hashMap;
    }


    IHttpResponseListener iHttpResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {
            Log.e("", "" + response);
            try{
                String status = response.getString("status");
                if (status.equals("OK")){
                    JSONArray address_components = response.getJSONObject("result").getJSONArray("address_components");
                    if (address_components.length() > 0){
                        for (int i = 0 ; i < address_components.length() ; i++){
                            JSONObject jsonObject = address_components.getJSONObject(i);
                            JSONArray types = jsonObject.getJSONArray("types");
                            if (types.length() > 0){
                                if (types.getString(0).equals("street_number")){
                                    street = jsonObject.getString("long_name");
                                }else if (types.getString(0).equals("route")){
                                    mAddress2 = jsonObject.getString("long_name");
                                }else if (types.getString(0).equals("locality")){
                                    locality = jsonObject.getString("long_name");
                                }else if (types.getString(0).equals(" administrative_area_level_1")){
                                    mState = jsonObject.getString("short_name");
                                }else if (types.getString(0).equals("postal_code")){
                                    mZip = jsonObject.getString("long_name");
                                }else if (types.getString(0).equals("administrative_area_level_2")){
                                    mCity = jsonObject.getString("long_name");
                                }
                            }
                        }
                        if (street != null && locality != null){
                            mAddress1 = street+", "+ locality;
                        } else  if (street != null)
                            mAddress1 = street;
                        else if (locality != null)
                            mAddress1 = locality;

                        modal.setmAddress1(mAddress1);
                        modal.setmAddress2(mAddress2);
                        modal.setmZip(mZip);
                        modal.setmState(mState);
                        modal.setmCity(mCity);
                        handler.sendEmptyMessage(1);
                    }else {
                        Intent intent = new Intent(SignUp_AddressActivity.this,New_Address_Activity.class);
                        intent.putExtra("modal",modal);
                        startActivity(intent);
                    }
                }else{
                    Intent intent = new Intent(SignUp_AddressActivity.this,New_Address_Activity.class);
                    intent.putExtra("modal",modal);
                    startActivity(intent);

                }
            }catch (JSONException e){

            }
        }
    };
    IHttpResponseListener googlePlaceResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {

            Log.e("", "response" + response);
            if (!response.isNull("predictions")){
                try {
                    JSONArray  predictions = response.getJSONArray("predictions");
                    for (int i = 0 ; i < predictions.length() ;i++){
                        GoogleResponseBean modal = new GoogleResponseBean();
                        JSONObject jsonObject = predictions.getJSONObject(i);
                        modal.setPlace_id(jsonObject.getString("place_id"));
                        modal.setDescription(jsonObject.getString("description"));
                        JSONArray  terms = jsonObject.getJSONArray("terms");
                        if (terms.length() > 0){
                            modal.setName(terms.getJSONObject(0).getString("value"));
                        }
                        arrayListTemp.add(modal);
                        if (arrayListTemp.size() > 0){
                            handler.sendEmptyMessage(0);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    arrayList.clear();
                    arrayList.addAll(arrayListTemp);
                    adapter.notifyDataSetChanged();

                    break;
                }
                case 1:{
                    finalRequestParams.put("api","signup_demo");
                    finalRequestParams.put("with_token","1");
                    finalRequestParams.put("object","pros");
                    finalRequestParams.put("data[address_uid]", modal.getPlace_id());
                    finalRequestParams.put("data[pros][working_radius_miles]","200");
                    if (mZip == null ||mState == null ||mCity == null  ){
                        Intent intent = new Intent(SignUp_AddressActivity.this,New_Address_Activity.class);
                        intent.putExtra("ispro",true);
                        intent.putExtra("modal",modal);
                        intent.putExtra("finalRequestParams",finalRequestParams);
                        startActivity(intent);
                    }else {

                        Intent intent = new Intent(SignUp_AddressActivity.this,SignUp_Account_Activity.class);
                        intent.putExtra("ispro",true);
                        intent.putExtra("finalRequestParams",finalRequestParams);
                        finalRequestParams.put("data[pros][city]", mCity);
                        finalRequestParams.put("data[pros][zip]", mZip);
                        finalRequestParams.put("data[pros][state]", mState);
                        startActivity(intent);
                    }
                }
            }

        }
    };
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {

        }
    };

}
