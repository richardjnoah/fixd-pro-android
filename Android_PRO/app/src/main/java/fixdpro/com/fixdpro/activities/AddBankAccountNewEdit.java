package fixdpro.com.fixdpro.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

public class AddBankAccountNewEdit extends AppCompatActivity {
    ImageView imgNext, imgClose;
    HashMap<String,String> finalRequestParams = new HashMap<String,String>() ;
    boolean ispro = false ;
    String error_message = "";
    EditText txtBankName,txtRoutingNumber,txtAccountNumber;
    String bank_name = "",routing_number = "",account_number = "";
    SharedPreferences _prefs = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account_new_edit);
        _prefs = Utilities.getSharedPreferences(this);
        if (_prefs.getString(Preferences.ROLE,"pro").equals("pro")){
            ispro = true ;
        }
        setWidgets();
        setCLickListner();
        initLayout();
    }
    private void initLayout(){
        txtBankName.setText(_prefs.getString(Preferences.BANK_NAME,""));
        txtRoutingNumber.setText(_prefs.getString(Preferences.BANK_ROUTING_NUMBER,""));
        txtAccountNumber.setText(_prefs.getString(Preferences.BANK_ACCOUNT_NUMBER,""));
    }
    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtBankName = (EditText)findViewById(R.id.txtBankName);
        txtRoutingNumber = (EditText)findViewById(R.id.txtRoutingNumber);
        txtAccountNumber = (EditText)findViewById(R.id.txtAccountNumber);

    }
    private void showAlertDialog(String Title,String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AddBankAccountNewEdit.this);
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
                bank_name = txtBankName.getText().toString().trim();
                routing_number = txtRoutingNumber.getText().toString().trim();
                account_number = txtAccountNumber.getText().toString().trim();
                if (bank_name.length() == 0) {
                    showAlertDialog("Fixd-Pro", "Please enter the bank name.");
                    return;
                } else if (routing_number.length() == 0) {
                    showAlertDialog("Fixd-Pro", "Please enter the routing number.");
                    return;
                } else if (account_number.length() == 0) {
                    showAlertDialog("Fixd-Pro", "Please enter the account number.");
                    return;
                }
                finalRequestParams.put("api", "update_bank_account");
                if (ispro)
                    finalRequestParams.put("object", "pros");
                else
                    finalRequestParams.put("object", "technician");
                finalRequestParams.put("data[bank_name]", bank_name);
                finalRequestParams.put("data[bank_routing_number]", routing_number);
                finalRequestParams.put("data[bank_account_number]", account_number);
                finalRequestParams.put(" data[bank_account_type]", "Company");

                finalRequestParams.put("token", _prefs.getString(Preferences.AUTH_TOKEN,""));
                GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",updateResponseListener,exceptionListener,AddBankAccountNewEdit.this,"");
                getApiResponseAsyncNew.execute(finalRequestParams);
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
                    editor.putString(Preferences.BANK_ACCOUNT_NUMBER, account_number);
                    editor.putString(Preferences.BANK_NAME, bank_name);
                    editor.putString(Preferences.BANK_ROUTING_NUMBER, routing_number);
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
            handler.sendEmptyMessage(2);
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showAlertDialog("Fixd-Pro",error_message);
        }
    };
}
