package fixtpro.com.fixtpro.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import fixtpro.com.fixtpro.Add_TechScreen;

import fixtpro.com.fixtpro.HomeScreenNew;
import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.ResponseListener;
import fixtpro.com.fixtpro.net.GetApiResponseAsyncMutipartNoProgress;
import fixtpro.com.fixtpro.net.GetApiResponseAsyncNew;
import fixtpro.com.fixtpro.net.IHttpExceptionListener;
import fixtpro.com.fixtpro.net.IHttpResponseListener;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.ExceptionListener;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsyncMutipart;
import fixtpro.com.fixtpro.utilites.MultipartUtility;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;


public class LastStep_Activity extends AppCompatActivity {
    Context context = LastStep_Activity.this;
    public static final String TAG = "LastStep_Activity";
    ImageView imgClose, imgFinish;
    TextView txtCheckforPrice, txtDoller, txtApply, txtTotal, txtTotalDoller, txtScanCard, txtTermsCondition,txtDealerServices;
    EditText txtCardNumber, txtExpirationYear, txtExpirationMonth, txtSecurityCode, txtDiscountCode, txtFirstName, txtLastName;
    boolean ispro = false;
    HashMap<String, String> finalRequestParams = null;
    boolean iscompleting = false;
    String selectedImagePathUser = null;
    String selectedImagePathDriver = null;
    String selectedImagePathSignature = null;
    String selectedImageDriver = null;
    String card_number = "", month = "", year = "", cvv = "", zip_code = "", first_name = "", last_name = "", error_message = "";
    MultipartUtility multipart = null;
    SharedPreferences _prefs = null;
    Dialog progressDialog;
    private static final int MY_SCAN_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_step_);
        _prefs = Utilities.getSharedPreferences(this);
        setWidgets();

        setCLickListner();
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")) {
                finalRequestParams = (HashMap<String, String>) bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")) {
                ispro = bundle.getBoolean("ispro");
            }
            if (bundle.containsKey("iscompleting")) {
                iscompleting = bundle.getBoolean("iscompleting");
            }
            if (bundle.containsKey("driver_image")) {
                selectedImagePathDriver = bundle.getString("driver_image");
            }
            if (bundle.containsKey("user_image")) {
                selectedImagePathUser = bundle.getString("user_image");
            }
            if (bundle.containsKey("user_image_sign")) {
                selectedImagePathSignature = bundle.getString("user_image_sign");
            }
        }
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgFinish = (ImageView) findViewById(R.id.imgFinish);

        txtCheckforPrice = (TextView) findViewById(R.id.txtCheckforPrice);
        txtDoller = (TextView) findViewById(R.id.txtDoller);
        txtDiscountCode = (EditText) findViewById(R.id.txtDiscountCode);
        txtApply = (TextView) findViewById(R.id.txtApply);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        txtTotalDoller = (TextView) findViewById(R.id.txtTotalDoller);
        txtScanCard = (TextView) findViewById(R.id.txtScanCard);
        txtTermsCondition = (TextView) findViewById(R.id.txtTermsCondition);
        txtDealerServices = (TextView) findViewById(R.id.txtDealerServices);

        txtCardNumber = (EditText) findViewById(R.id.txtCardNumber);
        txtExpirationYear = (EditText) findViewById(R.id.txtExpirationYear);
        txtExpirationMonth = (EditText) findViewById(R.id.txtExpirationMonth);
        txtSecurityCode = (EditText) findViewById(R.id.txtSecurityCode);
        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);

    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtScanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanPress(v);
            }
        });
        txtTermsCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = "Terms and Conditions";
                Intent intent = new Intent(LastStep_Activity.this, TermsAndConditions_Activity.class);
                intent.putExtra("TermCondition_DealerServices",txt);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        txtDealerServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = "Dealer Service Agreement";
                Intent intent = new Intent(LastStep_Activity.this, TermsAndConditions_Activity.class);
                intent.putExtra("TermCondition_DealerServices",txt);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        imgFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card_number = txtCardNumber.getText().toString().trim();
                month = txtExpirationMonth.getText().toString().trim();
                year = txtExpirationYear.getText().toString().trim();
                ;
                first_name = txtFirstName.getText().toString().trim();
                last_name = txtLastName.getText().toString().trim();
                ;
                cvv = txtSecurityCode.getText().toString().trim();
                if (card_number.length() == 0) {
                    showAlertDialog("Fixd-pro", "please enter card number");
                } else if (card_number.length() != 16) {
                    showAlertDialog("Fixd-pro", "please enter a valid card number");
                } else if (month.length() == 0) {
                    showAlertDialog("Fixd-pro", "please enter month");
                } else if (year.length() == 16) {
                    showAlertDialog("Fixd-pro", "please enter a year");
                } else if (year.length() < 4) {
                    showAlertDialog("Fixd-pro", "please enter a valid a valid year");
                } else if (cvv.length() == 0) {
                    showAlertDialog("Fixd-pro", "please enter CVV");
                } else if (cvv.length() < 3) {
                    showAlertDialog("Fixd-pro", "please enter a valid a CVV number");
                } else if (false) {
                    showAlertDialog("Fixd-pro", "please enter Zip");
                } else if (first_name.length() == 0) {
                    showAlertDialog("Fixd-pro", "please enter first name");
                } else if (last_name.length() == 0) {
                    showAlertDialog("Fixd-pro", "please enter last name");
                } else {
                    finalRequestParams.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
                    finalRequestParams.put("data[credit_card][card_number]", card_number);
                    finalRequestParams.put("data[credit_card][cvv]", cvv);
                    finalRequestParams.put("data[credit_card][first_name]", first_name);
                    finalRequestParams.put("data[credit_card][last_name]", last_name);
                    finalRequestParams.put("data[credit_card][month]", month);
                    finalRequestParams.put("data[credit_card][year]", year);
                    sendRequest();
                }


//                GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",iHttpResponseListener,iHttpExceptionListener,LastStep_Activity.this,"");
//                getApiResponseAsyncNew.execute(finalRequestParams);
            }
        });
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);
        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, true); // default: false
        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private MultipartUtility createMultiPartRequest() {

        try {
            multipart = new MultipartUtility(Constants.BASE_URL, Constants.CHARSET);
            for (String key : finalRequestParams.keySet()) {
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
                    multipart.addFilePart("data[technicians][e_signature_image]", new File(selectedImagePathSignature));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return multipart;
    }

    private void sendRequest() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new Dialog(LastStep_Activity.this);
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


                GetApiResponseAsyncMutipartNoProgress getApiResponseAsync = new GetApiResponseAsyncMutipartNoProgress(multipart, responseListener, exceptionListener, LastStep_Activity.this, "Registering");
                getApiResponseAsync.execute();
            }
        }.execute();
    }

    IHttpResponseListener iHttpResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {

        }
    };
    IHttpExceptionListener iHttpExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception;
            handler.sendEmptyMessage(0);
        }
    };
    ExceptionListener exceptionListener = new ExceptionListener() {

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
                case 500: {
                    showAlertDialog("Fixd-pro", "Server Error 500");
                    break;

                }
                default: {

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
                    String has_card = "0";
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("has_card")) {
                        has_card = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("has_card");
                    }

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
                    editor.putString(Preferences.HAS_CARD, has_card);
                    editor.putString(Preferences.ACCOUNT_STATUS, account_status);
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("pros")) {
                        JSONObject pros = null;
                        pros = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("pros");
                        String city = pros.getString("city");
                        String state = pros.getString("state");
                        String zip = pros.getString("zip");
                        String address = pros.getString("address");
                        String address2 = pros.getString("address_2");
                        String hourly_rate = pros.getString("hourly_rate");
                        String company_name = pros.getString("company_name");
                        String ein_number = "";
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
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("services")) {
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
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("technicians")) {
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
                        if (!Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").isNull("profile_image")) {
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
                    editor.putString(Preferences.CREDIT_CARD_NUMBER, card_number);
                    editor.putString(Preferences.CREDIT_CARD_MONTH, month);
                    editor.putString(Preferences.CREDIT_CARD_YEAR, year);
                    editor.putString(Preferences.CREDIT_CARD_CVV, cvv);
                    editor.putString(Preferences.CREDIT_CARD_FIRST_NAME, first_name);
                    editor.putString(Preferences.CREDIT_CARD_LAST_NAME, last_name);
                    if (editor.commit()) {
                        if (_prefs.getString(Preferences.ROLE, "").equals("pro")) {
                            Intent intent = new Intent(LastStep_Activity.this, HomeScreenNew.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        } else {
                            Intent intent = new Intent(LastStep_Activity.this, HomeScreenNew.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }

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
                handler.sendEmptyMessage(500);
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                ;

                txtCardNumber.setText(scanResult.getRedactedCardNumber().replace(" ", ""));
                card_number = scanResult.cardNumber;

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                    txtExpirationYear.setText(scanResult.expiryYear + "");
                    txtExpirationMonth.setText(scanResult.expiryMonth + "");
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                    txtSecurityCode.setText(scanResult.cvv);
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
//                    editZipCode.setText(scanResult.postalCode);
                }
            } else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }
}
