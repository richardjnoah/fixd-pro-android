package fixtpro.com.fixtpro.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.R;
import fixtpro.com.fixtpro.beans.SkillTrade;
import fixtpro.com.fixtpro.net.GetApiResponseAsyncNew;
import fixtpro.com.fixtpro.net.IHttpExceptionListener;
import fixtpro.com.fixtpro.net.IHttpResponseListener;
import fixtpro.com.fixtpro.singleton.TradeSkillSingleTon;
import fixtpro.com.fixtpro.utilites.CheckAlertDialog;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.JSONParser;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class TradeLiecenseNo_Activity_Edit extends AppCompatActivity {
    Context context = TradeLiecenseNo_Activity_Edit.this;
    public static final String TAG = "TradeLiecenseNo_Activity_Edit";
    ImageView imgClose, imgNext;
    LinearLayout layout1 ;

    SharedPreferences _prefs = null ;
    Context _context = this ;
    boolean ispro = false ;

    HashMap<String,String> finalRequestParams = new HashMap<String,String>() ;
    ArrayList<SkillTrade> arrayList ;
    String error_message = "";
    CheckAlertDialog checkALert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_liecense_no__activity__edit);
        _prefs = Utilities.getSharedPreferences(this);
        checkALert = new CheckAlertDialog();
        if (_prefs.getString(Preferences.ROLE,"pro").equals("pro")){
            ispro = true ;
        }
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            arrayList = (ArrayList<SkillTrade>)bundle.getSerializable("list");
        }
        _prefs = Utilities.getSharedPreferences(_context);

        setWidgets();
        setCLickListner();

        setTradeLicenseNumbers();

    }
    private void setTradeLicenseNumbers(){
        if (arrayList.size() > 0){
            layout1.removeAllViews();
            setViews();
        }
    }

    private HashMap<String, String> getTradeSkillRequestParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "services");
        hashMap.put("select", "^*");
        hashMap.put("per_page", "999");
        hashMap.put("page", "1");
//        hashMap.put("where[status]", "ACTIVE");
        hashMap.put("where[for_pro]", "1");
        return hashMap;
    }
    private void setViews(){
        for (int i = 0 ; i <arrayList.size() ; i++){

            if (arrayList.get(i).isChecked()){
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.trade_license_text_item, null);
                final EditText txtItem = (EditText)addView.findViewById(R.id.txtItem);
                final TextView txtName = (TextView)addView.findViewById(R.id.txtName);
                txtItem.setTag(i+"");
                txtItem.setHint(arrayList.get(i).getTitle());
                txtName.setText(arrayList.get(i).getTitle() + ":");
                txtItem.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        arrayList.get(Integer.parseInt(txtItem.getTag().toString())).setNumber(txtItem.getText().toString().trim());
                    }
                });
                layout1.addView(addView);
            }

        }
    }

    private String getServiceName(String id){
        String title = "";
        for (int i = 0 ; i < arrayList.size() ; i++){
            if ((arrayList.get(i).getId()+"").equals(id)){
                title  = arrayList.get(i).getTitle();
                break;
            }
        }
        return title;
    }
    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        layout1 = (LinearLayout) findViewById(R.id.layout1);


    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkForEmptyValues()){
                    getFinalRequestParams();
                    for (int i = 0 ; i < arrayList.size() ; i++){
                        if (arrayList.get(i).isChecked())
                        finalRequestParams.put("data[user_services]["+i+"]",arrayList.get(i).getId()+"");
                    }
                    finalRequestParams.put("api", "update");
                    if (ispro)
                        finalRequestParams.put("object", "pros");
                    else
                        finalRequestParams.put("object", "technicians");
                        finalRequestParams.put("token", _prefs.getString(Preferences.AUTH_TOKEN,""));
                    GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",updateResponseListener,exceptionListener,TradeLiecenseNo_Activity_Edit.this,"");
                    getApiResponseAsyncNew.execute(finalRequestParams);
                }else {
                    showAlertDialog("Fixd","Please enter Trade's License Number before proceeding");
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
                    editor.putString(Preferences.SERVICES_JSON_ARRAY, Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONArray("services").toString());
//                    editor.putString(Preferences.TRADE_LICENCES_JSON_ARRAY, Address2);
//                    editor.putString(Preferences.CITY, City);
//                    editor.putString(Preferences.ZIP, ZipCode);
//                    editor.putString(Preferences.STATE, State);
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
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:{
                    checkALert.showcheckAlert(TradeLiecenseNo_Activity_Edit.this,TradeLiecenseNo_Activity_Edit.this.getResources().getString(R.string.alert_title),error_message);
                    break;
                }
            }
        }
    };
    private boolean checkForEmptyValues(){
        boolean isEmpty = false ;
        for (int i = 0 ; i < arrayList.size() ; i++){
            if (arrayList.get(i).isChecked()){
                if (arrayList.get(i).getNumber().length() == 0){
                    isEmpty = true;
                    break;
                }
            }
        }
        return isEmpty;
    }
    private void getFinalRequestParams(){
        for (int i = 0 ; i <arrayList.size() ; i++){
            if (arrayList.get(i).isChecked()){
                finalRequestParams.put("data[trade_licenses]["+ i + "][service_id]",arrayList.get(i).getId()+"");
                finalRequestParams.put("data[trade_licenses]["+ i + "][license_number]",arrayList.get(i).getNumber()+"");
            }


        }
    }
    private void showAlertDialog(String Title, String Message) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                TradeLiecenseNo_Activity_Edit.this);

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
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
