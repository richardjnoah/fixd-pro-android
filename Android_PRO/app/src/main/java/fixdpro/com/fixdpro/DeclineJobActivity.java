package fixdpro.com.fixdpro;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Singleton;
import fixdpro.com.fixdpro.utilites.Utilities;

public class DeclineJobActivity extends AppCompatActivity implements View.OnClickListener{
    TextView cancel;
    TextView yesdecline_cancel_img;
    TextView title, areusure_text, backup_text;
    EditText editText;
    String JobType, objectType, JobId, message;
    String error_message =  "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decline_job);
//        getSupportActionBar().hide();
        setWidgets();
        setListeners();
        JobType = getIntent().getStringExtra("JobType");
        JobId = getIntent().getStringExtra("JobId");
        if(JobType.equals("Scheduled")){
            title.setText(getString(R.string.canceljob));
            areusure_text.setText(getString(R.string.areusure_cancel));
            backup_text.setText(getString(R.string.backup_cancel));
            editText.setHint(getString(R.string.canceledittext));
            editText.setVisibility(View.VISIBLE);
            yesdecline_cancel_img.setText("CANCEL");
//            yesdecline_cancel_img.setImageResource(R.drawable.yescancel);
        }else if (JobType.equals("Available")){
            editText.setVisibility(View.GONE);
            yesdecline_cancel_img.setText("DECLINE");
            title.setText(getString(R.string.declinejob));
        }
    }

    public void setWidgets(){
        cancel = (TextView)findViewById(R.id.cancel);
        title = (TextView)findViewById(R.id.title);
        areusure_text = (TextView)findViewById(R.id.areusure_text);
        backup_text = (TextView)findViewById(R.id.backup_text);
        editText = (EditText)findViewById(R.id.edittext);
        yesdecline_cancel_img = (TextView)findViewById(R.id.yesdecline_cancel_img);
    }

    public void setListeners(){
        cancel.setOnClickListener(this);
        yesdecline_cancel_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                finish();
                break;
            case R.id.yesdecline_cancel_img:
                if (!JobType.equals("Available")){
                    if (editText.getText().toString().trim().length() == 0){
                        showAlertDialog("Fixd-Pro","Please enter the reason before proceeding");
                        return;
                    }
                }

                HashMap<String,String> hashMap = null ;
                if (JobType.equals("Scheduled")) {
                    hashMap = getRequestParamsScheduled();
                }
                else {
                    hashMap = getRequestParams();
                }
                GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerDeclineCancel, this, "Loading");
                responseAsync.execute(hashMap);
                break;
        }
    }

    private HashMap<String,String> getRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","create");
        hashMap.put("object","declined_jobs");
        hashMap.put("data[job_id]",JobId);
        hashMap.put("data[reason]",editText.getText().toString());
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));

        return hashMap;
    }
    private HashMap<String,String> getRequestParamsScheduled(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","cancel");
        hashMap.put("object","jobs");
        hashMap.put("data[job_id]",JobId);
        hashMap.put("data[reason]",editText.getText().toString());
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));

        return hashMap;
    }

    ResponseListener responseListenerDeclineCancel = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    if (JobType.equals("Scheduled")) {
                        handler.sendEmptyMessage(2);
                    }else {
                        handler.sendEmptyMessage(0);
                    }
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

    private void removeJobFromList(){
        for (int i = 0; i < Singleton.getInstance().getAvailablejoblist().size() ; i++){
            AvailableJobModal modal = Singleton.getInstance().getAvailablejoblist().get(i);
            if (modal.getId().equals(JobId)){
                Singleton.getInstance().getAvailablejoblist().remove(i);
                break;
            }
        }
    }
    private void removeJobFromScheduledList(){
        for (int i = 0; i < Singleton.getInstance().getAvailablejoblist().size() ; i++){
            AvailableJobModal modal = Singleton.getInstance().getSchedulejoblist().get(i);
            if (modal.getId().equals(JobId)){
                modal.setStatus("Open");
                Singleton.getInstance().getAvailablejoblist().add(modal);
                Singleton.getInstance().getSchedulejoblist().remove(i);
                break;
            }
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
//                    Toast.makeText(getApplicationContext(),"Job is Scheduled",2000).show();
                    removeJobFromList();
                    Intent intent = new Intent(DeclineJobActivity.this,HomeScreenNew.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;
                }
                case 1:{
//                    Toast.makeText(getApplicationContext(),"Error!! Job is not Scheduled",2000).show();
                    showAlertDialog("FAILED", error_message);
                    break;
                }
                case 2:{
                    removeJobFromScheduledList();
                    finishAffinity();
                    Intent intent = new Intent(DeclineJobActivity.this,HomeScreenNew.class);
                    intent.putExtra("switch_tab","Scheduled");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;
                }
                case 3:{

                    break;
                }
            }
        }
    };

    private void showAlertDialog(String Title,String Message){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                DeclineJobActivity.this);

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
//                        Intent intent = new Intent(DeclineJobActivity.this,HomeScreenNew.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
                    }
                });

        // create alert dialog
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
