package fixtpro.com.fixtpro.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixtpro.com.fixtpro.Add_TechScreen;
import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.ResponseListener;
import fixtpro.com.fixtpro.adapters.TechnicianAdapter;
import fixtpro.com.fixtpro.beans.TechnicianModal;
import fixtpro.com.fixtpro.net.GetApiResponseAsyncNew;
import fixtpro.com.fixtpro.net.IHttpExceptionListener;
import fixtpro.com.fixtpro.net.IHttpResponseListener;
import fixtpro.com.fixtpro.singleton.TechniciansListSinglton;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class AddTechnicion_Activity_Edit extends AppCompatActivity {
    ImageView imgClose;
    TextView txtYes, txtNO;
    boolean ispro = false ;
    HashMap<String,String> finalRequestParams = null ;
    String selectedImagePathUser = null ;
    String  selectedImagePathDriver = null;
    String  selectedImagePathSignature = null;
    boolean iscompleting = false ;
    TechnicianAdapter adapter ;
    ListView lstTechnicians ;
    ArrayList<TechnicianModal> technicianModalsList = TechniciansListSinglton.getInstance().technicianModalsList;
    String error_message = "";
    String next = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_technicion__activity__edit);
        setWidgets();
        setClickListner();
        if (technicianModalsList.size() != 0){
            handler.sendEmptyMessage(0);
        }else{
            GetApiResponseAsyncNew getApiResponseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",getTechniciansListener,exceptionListener,AddTechnicion_Activity_Edit.this,"Getting.");
            getApiResponseAsync.execute(getTechnicianRequestParams());
        }
    }
    private HashMap<String,String> getTechnicianRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
        hashMap.put("object","technicians");
        hashMap.put("select","^*,users.email,users.phone");
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("per_page", "999");
        hashMap.put("where[user_id@!=]", Utilities.getSharedPreferences(this).getString(Preferences.ID, ""));
        hashMap.put("page", "1");
        return hashMap;
    }
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
         error_message = exception;
            handler.sendEmptyMessage(1);
        }
    };
    IHttpResponseListener getTechniciansListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "" + Response.toString());
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
                    next = pagination.getString("next");
                    for (int i = 0 ; i < results.length() ; i++){
                        TechnicianModal modal = new TechnicianModal();
                        JSONObject jsonObject = results.getJSONObject(i);
                        modal.setId(jsonObject.getString("user_id"));
                        modal.setFirstName(jsonObject.getString("first_name"));
                        modal.setLastName(jsonObject.getString("last_name"));
                        if (jsonObject.getString("pickup_jobs").equals("0"))
                            modal.setIspickjob(false);
                        else
                            modal.setIspickjob(true);
                        JSONObject users = jsonObject.getJSONObject("users");
                        modal.setEmail(users.getString("email"));
                        modal.setPhone(users.getString("phone"));
                        if (!jsonObject.isNull("profile_image")){
                            JSONObject profile_image = jsonObject.getJSONObject("profile_image");
                            if (!profile_image.isNull("original"))
                                modal.setProfile_image(profile_image.getString("original"));
                        }
                        technicianModalsList.add(modal);
                    }
                    handler.sendEmptyMessage(0);
                }else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
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
//                    set ADAPTER
                  onResume();
                    break;
                }
                case 1:{
//                    noty
                    showAlertDialog("Fixd-Pro", error_message);
                    adapter.notifyDataSetChanged();
                    break;
                }
                case 2:{

                    showAlertDialog("Fixd-Pro", error_message);
                    break;
                }
                case 500: {

                    showAlertDialog("Fixd-pro", "Server Error 500");
                    break;
                }
            }
        }
    };

    private void showAlertDialog(String Title,String Message){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
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
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new TechnicianAdapter(AddTechnicion_Activity_Edit.this,TechniciansListSinglton.getInstance().technicianModalsList,getResources());
        lstTechnicians.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        txtYes = (TextView) findViewById(R.id.txtYes);
        txtNO = (TextView) findViewById(R.id.txtNO);
        lstTechnicians = (ListView) findViewById(R.id.lstTechnicians);
    }

    private void setClickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddTechnicion_Activity_Edit.this, TechnicianInformation_Activity.class);
                i.putExtra("isedit",false);
                startActivity(i);
            }
        });
        txtNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

