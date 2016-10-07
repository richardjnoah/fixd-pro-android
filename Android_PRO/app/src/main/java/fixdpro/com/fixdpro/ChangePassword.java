package fixdpro.com.fixdpro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;


public class ChangePassword extends AppCompatActivity {
    TextView  txtDone;
    EditText txtCurrentPassword, txtNewPassword, txtConfirmPassword,lblPhone;
    SharedPreferences _prefs = null ;
    Context _context  = this ;
    String Phone  = "";
    String authToken  =  "";
    String current_password = "",new_password = "",confirm_password  = "";
    String message = "";
    ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_new);
        setWidgets();
        _prefs = Utilities.getSharedPreferences(_context);
        authToken  = _prefs.getString(Preferences.AUTH_TOKEN, null) ;
        Phone  = _prefs.getString(Preferences.PHONE,"");
        lblPhone.setText(Phone);

        setListeners();

    }
    private  void setWidgets(){

        imgBack = (ImageView)findViewById(R.id.imgBack);
        txtDone = (TextView)findViewById(R.id.txtDone);
        lblPhone = (EditText)findViewById(R.id.lblPhone);

        txtCurrentPassword = (EditText)findViewById(R.id.txtCurrentPassword);
        txtNewPassword = (EditText)findViewById(R.id.txtNewPassword);
        txtConfirmPassword = (EditText)findViewById(R.id.txtConfirmPassword);


    }
    private void setListeners(){
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_password =  txtCurrentPassword.getText().toString().trim();
                new_password =  txtNewPassword.getText().toString().trim();
                confirm_password =  txtConfirmPassword.getText().toString().trim();
                if (current_password.length() == 0){
                    showAlertDialog("Fixd-Pro", "Please enter the old password.",false);
                    return;
                }else if (current_password.length() < 6){
                    showAlertDialog("Fixd-Pro", "Your old password should be 6 or more characters long, Please try again.",false);
                    return;
                }
                else if (new_password.length() == 0){
                    showAlertDialog("Fixd-Pro", "Please enter the new password.",false);
                    return;
                }
                else if (new_password.length() < 6){
                    showAlertDialog("Fixd-Pro", "Your new password should be 6 or more characters long, Please try again.",false);
                    return;
                }
                else if (confirm_password.length() == 0){
                    showAlertDialog("Fixd-Pro", "Please re-enter the password to confirm.",false);
                    return;
                }
                else if (confirm_password.length() < 6){
                    showAlertDialog("Fixd-Pro", "Your confirm password should be 6 or more characters long, Please try again.",false);
                    return;
                }
                else if (!confirm_password.equals(new_password)){
                    showAlertDialog("Fixd-Pro", "password not matched",false);
                    return;
                }else {
//                    do it
                    Utilities.hideKeyBoad(_context, ChangePassword.this.getCurrentFocus());
                    GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST",changePasswordListener,ChangePassword.this,"Loading");
                    responseAsync.execute(getChangePasswordParametrs());
                }


            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                finish();

            }
        });
    }
    
    ResponseListener changePasswordListener  = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject jsonObject) {
            Log.e("","Response"+jsonObject.toString());
            try {
                String STATUS = jsonObject.getString("STATUS");
                if (STATUS.equals("SUCCESS")){
                    message = "Password changed successfully";
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
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    finish();
                    break;
                }
                case 1:{
                    showAlertDialog("Fixed-Pro",message,false);
                    break;
                }
                default:{
                    showAlertDialog("Fixed-Pro","Error! Try later",false);
                }
            }
        }
    };
    private void showAlertDialog(String Title,String Message, final boolean dofinish){
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
                        if (dofinish){
                            finish();
                        }
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    private HashMap<String,String> getChangePasswordParametrs(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "change_password");
        hashMap.put("object", "account");
        hashMap.put("new_password",new_password);
        hashMap.put("password", current_password);
        hashMap.put("token", authToken);
        return hashMap;
    }

}
