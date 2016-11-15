package fixdpro.com.fixdpro.activities;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.HashMap;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.adapters.GooglePlaceAdapter;
import fixdpro.com.fixdpro.beans.GoogleResponseBean;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNoProgress;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.CheckAlertDialog;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.CircularProgressView;

public class SignUp_AddressActivity extends AppCompatActivity {
    Activity activity = SignUp_AddressActivity.this;
    public static final String TAG = "SignUp_AddressActivityl";
    CheckAlertDialog checkALert;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    EditText txtAddress;
    ImageView imgClose, imgNext;
    TextView txtNext,txtdontSee;
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
    SharedPreferences _prefs = null ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__address);
        prefs = Utilities.getSharedPreferences(activity);
        editor = prefs.edit();
        checkALert = new CheckAlertDialog();
        modal = new GoogleResponseBean();
        setWidgets();
        setClickListner();
        adapter = new GooglePlaceAdapter(this,arrayList,getResources());
        lstPlaces.setAdapter(adapter);

    }

    private void setWidgets() {
        txtAddress = (EditText) findViewById(R.id.txtAddress);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgClose = (ImageView) findViewById(R.id.imgClose);
        txtNext = (TextView) findViewById(R.id.txtNext);
        txtdontSee = (TextView) findViewById(R.id.txtdontSee);
        layoutNext = (LinearLayout) findViewById(R.id.layoutNext);
        lstPlaces = (ListView) findViewById(R.id.lstPlaces);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }

    private void setClickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            }
        });
        txtdontSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(1);
            }
        });
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
                    lstPlaces.setVisibility(View.INVISIBLE);
                }

            }
        });

        layoutNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAddress = txtAddress.getText().toString();
//                if (EditAddress.equals("")) {
                handler.sendEmptyMessage(1);
//                }
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

    private void getGoogleResultInDetails(String id) {
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
                    mZip = mState = mCity = "";
                    JSONArray address_components = response.getJSONObject("result").getJSONArray("address_components");
                    if (address_components.length() > 0){
                        for (int i = 0 ; i < address_components.length() ; i++){
                            JSONObject jsonObject = address_components.getJSONObject(i);
                            JSONArray types = jsonObject.getJSONArray("types");
                            if (types.length() > 0){
                                if (types.getString(0).equals("street_number")){
                                    street = jsonObject.getString("long_name");
                                }else if (types.getString(0).equals("route")){
                                    mAddress1 = jsonObject.getString("long_name");
                                }else if (types.getString(0).equals("locality")){
                                    locality = jsonObject.getString("long_name");
                                }else if (types.getString(0).equals("administrative_area_level_1")){
                                    mState = jsonObject.getString("short_name");
                                }else if (types.getString(0).equals("postal_code")){
                                    mZip = jsonObject.getString("long_name");
                                }else if (types.getString(0).equals("administrative_area_level_2")){
                                    mCity = jsonObject.getString("long_name");
                                }
                            }
                        }
                        if (street != null && locality != null){
                            mAddress2 = street+", "+ locality;
                        } else  if (street != null)
                            mAddress2 = street;
                        else if (locality != null)
                            mAddress2 = locality;

                        modal.setmAddress1(mAddress1);
                        modal.setmAddress2(mAddress2);
                        modal.setmZip(mZip);
                        modal.setmState(mState);
                        modal.setmCity(mCity);
                        /*Set the Addess in Prefrances to show in Address (Review) Screen When
                        * Search the address and select the address from the list in fi*/


                        handler.sendEmptyMessage(1);
                    }else {
                        Intent intent = new Intent(SignUp_AddressActivity.this,New_Address_Activity.class);
                        intent.putExtra("modal",modal);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }else{
                    Intent intent = new Intent(SignUp_AddressActivity.this,New_Address_Activity.class);
                    intent.putExtra("modal",modal);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

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
//                        if(!jsonObject.isNull("description")){
//                            String actualFullAddress = jsonObject.getString("description");
//                            String [] splitAddressArray = actualFullAddress.split(",");
//                            for ( int j = 0; j < splitAddressArray.length; j++){
//                                if (splitAddressArray[j].length() > 0){
//                                    modal.setmAddress1(splitAddressArray[0]);
//                                    modal.setmAddress2(splitAddressArray[1]);
//                                }
//                            }
//                        }
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
                    if (modal.getPlace_id().length() > 0)
                    finalRequestParams.put("data[address_uid]", modal.getPlace_id());
                    finalRequestParams.put("data[pros][working_radius_miles]","200");
                    if (mZip == null ||mState == null ||mCity == null
                            || mZip.isEmpty() || mState.isEmpty() || mCity.isEmpty()){
                        Intent intent = new Intent(SignUp_AddressActivity.this,New_Address_Activity.class);
                        intent.putExtra("ispro",true);
                        intent.putExtra("modal",modal);
                        intent.putExtra("finalRequestParams",finalRequestParams);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }else {

                        Intent intent = new Intent(SignUp_AddressActivity.this,SignUp_Account_Activity.class);
                        intent.putExtra("ispro",true);
                        intent.putExtra("finalRequestParams",finalRequestParams);
                        finalRequestParams.put("data[pros][address]", mAddress1);
                        finalRequestParams.put("data[pros][address_2]", mAddress2);
                        finalRequestParams.put("data[pros][city]", mCity);
                        finalRequestParams.put("data[pros][zip]", mZip);
                        finalRequestParams.put("data[pros][state]", mState);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
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
