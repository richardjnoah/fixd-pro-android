package fixtpro.com.fixtpro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.R;
import fixtpro.com.fixtpro.activities.AddBankAccountNewEdit;
import fixtpro.com.fixtpro.adapters.AssignTechAdapter;
import fixtpro.com.fixtpro.beans.AssignTechModal;
import fixtpro.com.fixtpro.beans.AvailableJobModal;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;


public class AssignTechnicianActivity extends AppCompatActivity {
    private Context context = AssignTechnicianActivity.this;
    private String TAG  = "AssignTechnicianActivity";

    private Typeface fontfamily;
    private Toolbar toolbar;
    private com.paging.listview.PagingListView assignTechList;
    private ImageView img_Cancel;
    private TextView titletext;
    private AssignTechAdapter mAdp;
    private ArrayList<AssignTechModal> modalList;
    AssignTechModal modal = null ;
    private SharedPreferences _prefs = null ;
    private Context _context  = this;
    String error_message = "";
    String next = "";
    String api =  "assign";
    boolean isAvailable = true ;
    AvailableJobModal modelAvail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_technician);
        modalList = new ArrayList<AssignTechModal>();
        modelAvail = (AvailableJobModal) getIntent().getSerializableExtra("JOB_DETAIL");
        _prefs = Utilities.getSharedPreferences(_context);
        if (getIntent() != null){
            api = getIntent().getStringExtra("api");
            isAvailable = getIntent().getBooleanExtra("isAvailable",true);
        }
        setTypeface();
        setToolbar();
        setWidgets();

        GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync("POST",getTechniciansListener,AssignTechnicianActivity.this,"Getting.");
        getApiResponseAsync.execute(getTechnicianRequestParams());

    }
    ResponseListener getTechniciansListener = new ResponseListener() {
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
                    AssignTechModal modal = new AssignTechModal();
                        JSONObject jsonObject = results.getJSONObject(i);
                        modal.setTechId(jsonObject.getString("id"));
                        modal.setTech_User_id(jsonObject.getString("user_id"));
                        modal.setFirstName(jsonObject.getString("first_name"));
                        modal.setLasttName(jsonObject.getString("last_name"));
                        if (!jsonObject.isNull("profile_image")){
                            JSONObject profile_image = jsonObject.getJSONObject("profile_image");
                            if (!profile_image.isNull("original"))
                            modal.setImage(profile_image.getString("original"));
                        }
                        modal.setRating(jsonObject.getString("avg_rating"));
                        modal.setJobSchedule(jsonObject.getString("scheduled_jobs_count"));
                        modalList.add(modal);
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
                    AssignTechAdapter assignTechAdapter = new AssignTechAdapter(AssignTechnicianActivity.this,modalList);
                    assignTechList.setAdapter(assignTechAdapter);
                    assignTechList.setHasMoreItems(false);
                    assignTechList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            modal = modalList.get(position);
                            GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST",assignTechListener,AssignTechnicianActivity.this,"Assigning");
                            responseAsync.execute(getAssignTechParametrs());
                        }
                    });
                    break;
                }
                case 1:{
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }
                case 4:{
                    showAddBankAccoutAlertDialog("Fixd-Pro",error_message);
                    break;
                }
            }
        }
    };
    private void showAddBankAccoutAlertDialog(String Title,String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle(Title);
        // set dialog message
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("Add Bank Info", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        if(Utilities.getSharedPreferences(AssignTechnicianActivity.this).getString(Preferences.ROLE, null).equals("pro")){
                            dialog.cancel();
                            Intent intent = new Intent(AssignTechnicianActivity.this,AddBankAccountNewEdit.class);
                            startActivity(intent);
                        }else {
                            dialog.cancel();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    private HashMap<String,String> getAssignTechParametrs(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", api);
        hashMap.put("object", "jobs");
        hashMap.put("data[technician_id]", modal.getTech_User_id());
        hashMap.put("data[id]", FixdProApplication.SelectedAvailableJobId);
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN,""));
        return hashMap;
    }
    ResponseListener assignTechListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "" + Response);
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    api = "re_assign";
                    if (isAvailable){
                        Intent intent = new Intent(context, ConfirmAssignTechActivity.class);
                        intent.putExtra("TechInfo", modal);
                        intent.putExtra("JOB_DETAIL",modelAvail);
                        context.startActivity(intent);
                    }else {
                        Intent intent = new Intent(context,HomeScreenNew.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                } else if(!Response.isNull("NOTICES")){
                    JSONObject notices = Response.getJSONObject("NOTICES");
                    Iterator<String> keys = notices.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = notices.getString(key);
                        if(key.equals("340067")){

                            handler.sendEmptyMessage(4);
                            return;
                        }
                    }
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                        if(key.equals("340067")){

                            handler.sendEmptyMessage(4);
                            return;
                        }
                    }
                    handler.sendEmptyMessage(1);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };
    private void showAlertDialog(String Title,String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                _context);
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
    private void setTypeface() {
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
    }
    private void setToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        img_Cancel = (ImageView)toolbar.findViewById(R.id.img_Cancel);
        img_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titletext = (TextView)toolbar.findViewById(R.id.titletext);
        titletext.setTypeface(fontfamily);

    }
    private void setWidgets(){
        assignTechList = (com.paging.listview.PagingListView)findViewById(R.id.assignTechList);
    }
    private void setAdapter(){
        modalList = new ArrayList<AssignTechModal>();
    }
    private HashMap<String,String> getTechnicianRequestParams(){
            HashMap<String,String> hashMap = new HashMap<String,String>();
            hashMap.put("api","read_currently_scheduled");
            hashMap.put("object","technicians");
            hashMap.put("select","^*");
            hashMap.put("token",Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN,""));
            hashMap.put("per_page","999");
            hashMap.put("where[verified]","1");
            hashMap.put("page", "1");
            hashMap.put("data[job_id]", FixdProApplication.SelectedAvailableJobId);
            return hashMap;
    }
}
