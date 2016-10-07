package fixdpro.com.fixdpro;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.Constants;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextView txtDone ,txtReset;
    EditText txtPhoneEmail;
    ImageView imgCrossExit;
    String Phone;
    String message = "";
    Typeface fontfamily;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        /*******Set Wedgets IDs******/
        setWidgetsIDs();
        setTypeface();
        setClickListner();

    }
    private  void setTypeface(){
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
        txtReset.setTypeface(fontfamily);
        txtPhoneEmail.setTypeface(fontfamily);


    }
    private void setClickListner() {
        imgCrossExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            }
        });
        txtReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Phone = txtPhoneEmail.getText().toString().trim();
                if (Phone.length() == 0) {
                    showAlertDialog("Fixd-Pro", "Please enter the Email/Phone number.",false);
                    return;
                }
                GetApiResponseAsyncNew getApiResponseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",iHttpResponseListener,exceptionListener,ForgotPasswordActivity.this,"Signing In.");
                getApiResponseAsync.execute(getLoginRequestParams());
            }
        });
    }
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            message = exception;
        }
    };
    private HashMap<String,String> getLoginRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();

        hashMap.put("api", "send_token");
        hashMap.put("data[phone_email]", Phone);
        hashMap.put("object","password");
        return hashMap;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    showAlertDialog("Success!", message,true);
                    break;
                }
                case  1:{
                    showAlertDialog("Fixd-Pro", message,false);
                    break;
                }
            }
        }
    };
    IHttpResponseListener iHttpResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject jsonObject) {
            try {
                String STATUS = jsonObject.getString("STATUS");
                if (STATUS.equals("SUCCESS")){
                    message = "You will shorty receive an email with the resent link and further instructions.";
                    handler.sendEmptyMessage(0);
                }else {
                    JSONObject errors = jsonObject.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private void showAlertDialog(String Title,String Message, final boolean doFinish){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
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
                        if (doFinish){
                            onBackPressed();
                        }
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }

    private void setWidgetsIDs() {
        txtPhoneEmail = (EditText)findViewById(R.id.txtPhoneEmail);

        txtReset = (TextView)findViewById(R.id.txtReset);
        imgCrossExit = (ImageView)findViewById(R.id.imgCrossExit);
    }


}
