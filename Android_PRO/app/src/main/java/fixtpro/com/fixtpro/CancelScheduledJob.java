package fixtpro.com.fixtpro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import fixtpro.com.fixtpro.utilites.CurrentScheduledJobSingleTon;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class CancelScheduledJob extends AppCompatActivity {
    EditText txtCancelReason;
    TextView back,done,txtSignature;
    Context _context = this ;
    String error_message = "",reason = "";
    String JobId = "" ;
    RelativeLayout tap_Signature;
    ImageView imgSignature ;
    String Path = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_scheduled_job);
        setWidgets();
        setListeners();
        JobId = CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId() ;
    }
    private void setWidgets(){
        txtCancelReason = (EditText)findViewById(R.id.txtCancelReason);
        back = (TextView)findViewById(R.id.back);
        txtSignature = (TextView)findViewById(R.id.txtSignature);
        done = (TextView)findViewById(R.id.done);
        tap_Signature = (RelativeLayout)findViewById(R.id.tap_Signature);
        imgSignature = (ImageView)findViewById(R.id.imgSignature);
    }
    private void setListeners(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = txtCancelReason.getText().toString().trim();
                if (reason.length() == 0){
                    showAlertDialog("Fixd","Reason field is required.",false);
                    return;
                } else if (Path.length() == 0){
                    showAlertDialog("Fixd","Please sign below to proceed.",false);
                    return;
                }
                GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerDeclineCancel, CancelScheduledJob.this, "Loading");
                responseAsync.execute(getRequestParams());
            }
        });
        tap_Signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CancelScheduledJob.this,SignatureActivity.class);
                intent.putExtra("isCancel",true);
                startActivityForResult(intent, 200);
            }
        });
        imgSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CancelScheduledJob.this,SignatureActivity.class);
                intent.putExtra("isCancel",true);
                startActivityForResult(intent, 200);
            }
        });
    }
    ResponseListener responseListenerDeclineCancel = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
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
                    showAlertDialog("SUCCESS", "Job is Cancelled.",true);
                    break;
                }
                case 1:{
                    showAlertDialog("FAILED", error_message,false);
                    break;
                }
                case 2:{

                    break;
                }
                case 3:{

                    break;
                }
            }
        }
    };

    private HashMap<String,String> getRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","cancel");
        hashMap.put("object","jobs");
        hashMap.put("data[job_id]", JobId);
        hashMap.put("data[reason]", txtCancelReason.getText().toString());
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));

        return hashMap;
    }

    private void showAlertDialog(String Title,String Message, final boolean success){
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
                        if (success){
                            Intent intent = new Intent(CancelScheduledJob.this,HomeScreenNew.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200){
            if (data != null){
                Path = data.getStringExtra("Path");
//                Picasso.with(this).load(Uri.fromFile(new File(Path)))
//                        .resize(172, 172).centerCrop().into(imgSignature);
                txtSignature.setVisibility(View.GONE);

                if (new File(Path).exists()) {


                    tap_Signature.setBackground(Drawable.createFromPath(Path));

                }
//                imgSignature.setVisibility(View.VISIBLE);
            }
        }
    }
}
