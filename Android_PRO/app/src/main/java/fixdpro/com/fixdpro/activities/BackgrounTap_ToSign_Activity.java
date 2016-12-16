package fixdpro.com.fixdpro.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.ResponseListener;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncMutipartNoProgress;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.ExceptionListener;
import fixdpro.com.fixdpro.utilites.MultipartUtility;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;


public class BackgrounTap_ToSign_Activity extends AppCompatActivity {
    Context context = BackgrounTap_ToSign_Activity.this;
    public static final String TAG = "BackgrounTap_ToSign_Activity";
    ImageView imgClose, imgNext,imgMessage;
    TextView txtTapToSign;
    boolean ispro = false ;
    HashMap<String,String> finalRequestParams = null ;
    WebView webview;
    String selectedImagePathUser = null ;
    String  selectedImagePathDriver = null;
    String  selectedImagePathSignature = null;
    boolean iscompleting = false ;
    Dialog progressDialog;
    MultipartUtility multipart = null;
    String error_message = "";
    SharedPreferences _prefs = null ;
    int READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,CAMERA;
    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_ATTACHMENT = 721;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backgroun_tap__to_sign_);
        _prefs = Utilities.getSharedPreferences(this);
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")){
                finalRequestParams = (HashMap<String,String>)bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")){
                ispro = bundle.getBoolean("ispro");
            }
            if (bundle.containsKey("iscompleting")){
                iscompleting = bundle.getBoolean("iscompleting");
            }
            if (bundle.containsKey("driver_image")){
                selectedImagePathDriver = bundle.getString("driver_image");
            }
            if (bundle.containsKey("user_image")){
                selectedImagePathUser = bundle.getString("user_image");
            }
        }
        setWidgets();

        setClickListner();
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgMessage = (ImageView) findViewById(R.id.imgMessage);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtTapToSign = (TextView) findViewById(R.id.txtTapToSign);
        webview = (WebView)findViewById(R.id.webview);
        webview.loadUrl("file:///android_asset/Acknowledgement.html");
    }

    private void setClickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= 23){
                onAttachmentsClickThenShare(imgMessage);
            }else{
                onMessageClickSharePDF();
            }
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ispro){
                    if (selectedImagePathSignature != null){
                        Intent i = new Intent(context,AddTechnicion_Activity.class);
                        i.putExtra("ispro",ispro);
                        i.putExtra("finalRequestParams",finalRequestParams);
                        i.putExtra("iscompleting", iscompleting);
                        i.putExtra("driver_image", selectedImagePathDriver);
                        i.putExtra("user_image", selectedImagePathUser);
                        i.putExtra("user_image_sign", selectedImagePathSignature);
                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }else {
                        showAlertDialog("Fixd-Pro","Please sign to continue");
                    }

                }else {
                    // call api to register tech
                    sendRequest();
                }

            }
        });
        txtTapToSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TapToSignActivity.class);
                startActivityForResult(i, 200);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    public void onMessageClickSharePDF(){
        //Acknowledgement and Authorization
        String  FilePath = Environment.getExternalStorageDirectory()
                + "/Acknowledgement_Authorization";
        File file = new File(FilePath +"/Acknowledgement.pdf");
        File file1 = new File(FilePath, "Acknowledgement.pdf");
        if (file.exists()){
            // get file and attach to email
            sharePDFFileWithGmail(file1);

        }else {
            File Folder = new File(FilePath);
            Folder.mkdir();
            Utilities.copySqltiteFromAssets(BackgrounTap_ToSign_Activity.this, "Acknowledgement.pdf", FilePath);
            // share pdf
            sharePDFFileWithGmail(file1);
        }
    }
    public void onAttachmentsClickThenShare(View view) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> permissionsNeeded = new ArrayList<String>();
            if (READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsNeeded.size() > 0) {
                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            } else {
                // share the pdf
                onMessageClickSharePDF();
            }
        }else{
            // share the pdf
            onMessageClickSharePDF();
        }
    }
    public void sharePDFFileWithGmail(File shareFile){
        Intent intent = new Intent(Intent.ACTION_SEND , Uri.parse("mailto:")); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Fixd - Terms & Conditions");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);
    }

    private void  sendRequest(){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new Dialog(BackgrounTap_ToSign_Activity.this);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.dialog_progress_simple);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                createMultiPartRequest();
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


                GetApiResponseAsyncMutipartNoProgress getApiResponseAsync = new GetApiResponseAsyncMutipartNoProgress(multipart, responseListener,exceptionListener, BackgrounTap_ToSign_Activity.this, "Registering");
                getApiResponseAsync.execute();
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            selectedImagePathSignature = data.getStringExtra("Path");
            Log.e("","Path"+selectedImagePathSignature);
        }
    }
    private MultipartUtility createMultiPartRequest(){

        try {
            multipart = new MultipartUtility(Constants.BASE_URL, Constants.CHARSET);
            for ( String key : finalRequestParams.keySet() ) {
                multipart.addFormField(key, finalRequestParams.get(key));
                Log.e("" + key, "=" + finalRequestParams.get(key));
            }

            if (ispro) {
                if (selectedImagePathDriver != null) {
                    multipart.addFilePart("data[technicians][driver_license_image]", new File(selectedImagePathDriver));
                }
                if (selectedImagePathUser != null) {
                    multipart.addFilePart("data[technicians][profile_image]", new File(selectedImagePathUser));
                }
                if (selectedImagePathSignature != null) {
                    multipart.addFilePart("data[technicians][e_signature_image]", new File(selectedImagePathSignature));
                }
            } else {
                if (selectedImagePathDriver != null) {
                    multipart.addFilePart("driver_license_image", new File(selectedImagePathDriver));
                }
                if (selectedImagePathUser != null) {
                    multipart.addFilePart("profile_image", new File(selectedImagePathUser));
                }
                if (selectedImagePathSignature != null) {
                    multipart.addFilePart("e_signature_image", new File(selectedImagePathSignature));
                }
            }
//            if (ispro){
//                if (selectedImagePathDriver != null){
//                    multipart.addFilePart("data[technicians][driver_license_image]",new File(selectedImagePathDriver));
//                }
//                if (selectedImagePathUser != null){
//                    multipart.addFilePart("data[technicians][profile_image]",new File(selectedImagePathUser));
//                }
//                if (selectedImagePathSignature != null){
//                    multipart.addFilePart("data[technicians][e_signature_image]",new File(selectedImagePathSignature));
//                }
//            }else {
//                if (selectedImagePathDriver != null){
//                    multipart.addFilePart("driver_license_image",new File(selectedImagePathDriver));
//                }
//                if (selectedImagePathUser != null){
//                    multipart.addFilePart("profile_image",new File(selectedImagePathUser));
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return multipart;
    }

    ExceptionListener exceptionListener = new ExceptionListener(){

        @Override
        public void handleException(int exceptionStatus) {
            handler.sendEmptyMessage(exceptionStatus);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            switch (msg.what) {

                case 0: {
                    showAlertDialog("Fixd-pro", error_message);
                    break;
                }
                case 500:{
                    showAlertDialog("Fixd-pro", "Server Error 500");
                    break;

                }
                default:{

                }
            }
        }
    };
    private void showAlertDialog(String Title, String Message) {
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
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    ResponseListener responseListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                String STATUS = Response.getString("STATUS");
                if (STATUS.equals("SUCCESS")) {
                    JSONObject RESPONSE = Response.getJSONObject("RESPONSE");
                    String Token = Response.getJSONObject("RESPONSE").getString("token");
                    String id = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("id");
                    String role = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("role");
                    String email = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("email");
                    String phone = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("phone");
//                    String has_card = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("has_card");
                    String account_status = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("account_status");
                    Log.e("AUTH TOKEN", Token);
                    Log.e("ROLE", role);
                    JSONObject pro_settings;
                    SharedPreferences.Editor editor = _prefs.edit();
                    editor.putString(Preferences.ID, id);
                    editor.putString(Preferences.ROLE, role);
                    editor.putString(Preferences.AUTH_TOKEN, Token);
                    editor.putString(Preferences.EMAIL, email);
                    editor.putString(Preferences.PHONE, phone);
                    editor.putString(Preferences.HAS_CARD, "0");
                    editor.putString(Preferences.ACCOUNT_STATUS, account_status);
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("pros")){
                        JSONObject pros = null;
                        pros = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("pros");
                        String city = pros.getString("city");
                        String state = pros.getString("state");
                        String zip = pros.getString("zip");
                        String address = pros.getString("address");
                        String address2 = pros.getString("address_2");
                        String hourly_rate = pros.getString("hourly_rate");
                        String company_name = pros.getString("company_name");
                        String ein_number="" ;
                        if (!pros.isNull("ein_number"))
                             ein_number = pros.getString("ein_number");
                        String bank_name = pros.getString("bank_name");
                        String bank_routing_number = pros.getString("bank_routing_number");
                        String bank_account_number = pros.getString("bank_account_number");
                        String bank_account_type = pros.getString("bank_account_type");
                        String insurance = pros.getString("insurance");
                        String insurance_policy = pros.getString("insurance_policy");
                        String isvarified = pros.getString("verified");
                        String working_radius_miles = pros.getString("working_radius_miles");
                        String is_pre_registered = pros.getString("is_pre_registered");
                        String latitude = pros.getString("latitude");
                        String longitude = pros.getString("longitude");
                        String avg_rating = pros.getString("avg_rating");
                        editor.putString(Preferences.CITY, city);
                        editor.putString(Preferences.STATE, state);
                        editor.putString(Preferences.ZIP, zip);
                        editor.putString(Preferences.ADDRESS, address);
                        editor.putString(Preferences.ADDRESS2, address2);
                        editor.putString(Preferences.HOURLY_RATE, hourly_rate);
                        editor.putString(Preferences.COMPANY_NAME, company_name);
                        editor.putString(Preferences.EIN_NUMEBR, ein_number);
                        editor.putString(Preferences.BANK_NAME, bank_name);
                        editor.putString(Preferences.BANK_ROUTING_NUMBER, bank_routing_number);
                        editor.putString(Preferences.BANK_ACCOUNT_NUMBER, bank_account_number);
                        editor.putString(Preferences.BANK_ACCOUNT_TYPE, bank_account_type);
                        editor.putString(Preferences.INSURANCE, insurance);
                        editor.putString(Preferences.INSURANCE_POLICY, insurance_policy);
                        editor.putString(Preferences.IS_VARIFIED, isvarified);
                        editor.putString(Preferences.WORKING_RADIUS_MILES, working_radius_miles);
                        editor.putString(Preferences.IS_PRE_REGISTERED, is_pre_registered);
                        editor.putString(Preferences.LATITUDE, latitude);
                        editor.putString(Preferences.LONGITUDE, longitude);
                        editor.putString(Preferences.AVERAGE_RATING, avg_rating);
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("services")){
                        JSONArray services = null;
                        services = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONArray("services");
                        editor.putString(Preferences.SERVICES_JSON_ARRAY, services.toString());
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("pro_settings")) {
                        pro_settings = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("pro_settings");
                        editor.putString(Preferences.SETTING_ID, pro_settings.getString("id"));
                        editor.putString(Preferences.LOCATION_SERVICE, pro_settings.getString("location_services"));
                        editor.putString(Preferences.TEXT_MESSAGING, pro_settings.getString("text_messaging"));
                        editor.putString(Preferences.AVAILABLE_JOBS_NOTIFICATION, pro_settings.getString("available_jobs_notification"));
                        editor.putString(Preferences.JOB_WON_NOTIFICATION, pro_settings.getString("job_won_notification"));
                        editor.putString(Preferences.JOB_LOST_NOTIFICATION, pro_settings.getString("job_lost_notification"));
                        editor.putString(Preferences.JOB_RESECHDULED, pro_settings.getString("job_rescheduled"));
                        editor.putString(Preferences.JOB_CANCELLED, pro_settings.getString("job_canceled"));
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("technicians")){
                        JSONObject technicians = null;
                        technicians = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians");
                        String first_name = technicians.getString("first_name");
                        String last_name = technicians.getString("last_name");
                        String social_security_number = technicians.getString("social_security_number");
                        String years_in_business = technicians.getString("years_in_business");
//                        String trade_license_number = technicians.getString("trade_license_number");
                        editor.putString(Preferences.FIRST_NAME, first_name);
                        editor.putString(Preferences.LAST_NAME, last_name);
                        editor.putString(Preferences.SOCIAL_SECURITY_NUMBER, social_security_number);
                        editor.putString(Preferences.YEARS_IN_BUSINESS, years_in_business);
//                        editor.putString(Preferences.TRADE_LICENSE_NUMBER, trade_license_number);
                        editor.putBoolean(Preferences.ISLOGIN, true);
                        if (!Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").isNull("profile_image")){
                            JSONObject profile_image = null;
                            profile_image = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").getJSONObject("profile_image");
                            if (profile_image.has("original")) {
                                String image_original = profile_image.getString("original");
                                editor.putString(Preferences.PROFILE_IMAGE, image_original);
                            }
                        }
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("quickblox_accounts")) {
                        JSONObject quickblox_accounts = null;
                        quickblox_accounts = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("quickblox_accounts");
                        String account_id = quickblox_accounts.getString("account_id");
                        String login = quickblox_accounts.getString("login");
                        String password = quickblox_accounts.getString("qb_password");
                        editor.putString(Preferences.QB_ACCOUNT_ID, account_id);
                        editor.putString(Preferences.QB_LOGIN, login);
                        editor.putString(Preferences.QB_PASSWORD, password);
                    }
                    if (editor.commit()) {
                            Intent intent = new Intent(BackgrounTap_ToSign_Activity.this, HomeScreenNew.class);
                            intent.putExtra("ispro",ispro);
                            startActivity(intent);
                    }
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
//                handler.sendEmptyMessage(500);
            }

        }
    };
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }
}
