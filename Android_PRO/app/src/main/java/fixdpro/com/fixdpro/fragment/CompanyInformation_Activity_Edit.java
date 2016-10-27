package fixdpro.com.fixdpro.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.WheelView;

public class CompanyInformation_Activity_Edit extends AppCompatActivity {
    EditText txtCompanyName, txtYearInBusiness, txtEniNumber, txtInsuranceCarr, txtPolicyNumber;
    TextView txtHourlyRate;
    ImageView imgNext, imgClose;
    String CompanyName = "", YearInBusiness = "", EniNumber = "", InsuranceCarrier = "", PolicyNumber = "", HourlyRate = "";
    HashMap<String,String> finalRequestParams = new HashMap<String,String>(); ;
    boolean ispro = false ;
    SharedPreferences _prefs = null ;
    String error_message  = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_information__activity__edit);
        _prefs = Utilities.getSharedPreferences(this);
        if (_prefs.getString(Preferences.ROLE,"pro").equals("pro")){
            ispro = true ;
        }
        setWidgets();
        setCLickListner();
        initLayout();
    }

    private void initLayout(){
        txtCompanyName.setText(_prefs.getString(Preferences.COMPANY_NAME,""));
        txtYearInBusiness.setText(_prefs.getString(Preferences.YEARS_IN_BUSINESS, ""));
        txtEniNumber.setText(_prefs.getString(Preferences.EIN_NUMEBR, ""));
        txtInsuranceCarr.setText(_prefs.getString(Preferences.INSURANCE, ""));
        txtPolicyNumber.setText(_prefs.getString(Preferences.INSURANCE_POLICY, ""));
        String hourlyRate = _prefs.getString(Preferences.HOURLY_RATE, "");
        if (!hourlyRate.contains("$")) txtHourlyRate.setText("$"+Math.round(Float.parseFloat(hourlyRate)));
        else txtHourlyRate.setText(hourlyRate);
    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtHourlyRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHourlyRateDialog();
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompanyName = txtCompanyName.getText().toString().trim();
                YearInBusiness = txtYearInBusiness.getText().toString().trim();
                EniNumber = txtEniNumber.getText().toString().trim();
                InsuranceCarrier = txtInsuranceCarr.getText().toString().trim();
                PolicyNumber = txtPolicyNumber.getText().toString().trim();
                HourlyRate = txtHourlyRate.getText().toString().trim();
                if (CompanyName.equals("")){
                    showAlertDialog(CompanyInformation_Activity_Edit.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the company name.");
                }else if (YearInBusiness.equals("")){
                    showAlertDialog(CompanyInformation_Activity_Edit.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the year in business.");
                }else if (Integer.parseInt(YearInBusiness) > 80){
                    showAlertDialog(CompanyInformation_Activity_Edit.this.getResources().getString(R.string.alert_title),
                            "Pleae enter a valid value for Number of Years in Business < 80.");
                }else if (EniNumber.equals("")){
                    showAlertDialog(CompanyInformation_Activity_Edit.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the ein number.");
                }else if (InsuranceCarrier.equals("")){
                    showAlertDialog(CompanyInformation_Activity_Edit.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the insurance carrier.");
                }else if(PolicyNumber.equals("")){
                    showAlertDialog(CompanyInformation_Activity_Edit.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the policy number.");
                }else if (HourlyRate.equals("")){
                    showAlertDialog(CompanyInformation_Activity_Edit.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the hourly rate.");
                }else{
                    finalRequestParams.put("api", "update");
                    if (ispro)
                        finalRequestParams.put("object", "pros");
                    else
                        finalRequestParams.put("object", "technician");
                    finalRequestParams.put("data[pros][company_name]", CompanyName);
                    finalRequestParams.put("data[technicians][years_in_business]", YearInBusiness);
                    finalRequestParams.put("data[pros][ein_number]", EniNumber);
                    finalRequestParams.put("data[pros][insurance]", InsuranceCarrier);
                    finalRequestParams.put("data[pros][insurance_policy]", PolicyNumber);
                    finalRequestParams.put("data[pros][hourly_rate]", HourlyRate);
                    finalRequestParams.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
                    if (HourlyRate.equals("$125")){
                        showAlertDialogRate("Fixd-Pro","Keep in mind Pros with lower labor ates will see available job first.Higher labor rates may limit your job availability");
                    }else{
                        GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",updateResponseListener,exceptionListener,CompanyInformation_Activity_Edit.this,"");
                        getApiResponseAsyncNew.execute(finalRequestParams);
                    }

                }

            }
        });
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            switch (msg.what) {
                case 500: {
                    showAlertDialog("Fixd-pro", "Server Error 500");
                    break;
                }
                default: {
                    showAlertDialog("Fixd-Pro", error_message);
                }
            }


        }
    };
    IHttpResponseListener updateResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response.toString()" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    SharedPreferences.Editor editor = _prefs.edit();
                    editor.putString(Preferences.YEARS_IN_BUSINESS, YearInBusiness);
                    editor.putString(Preferences.COMPANY_NAME, CompanyName);
                    editor.putString(Preferences.EIN_NUMEBR, EniNumber);
                    editor.putString(Preferences.INSURANCE, InsuranceCarrier);
                    editor.putString(Preferences.INSURANCE_POLICY, PolicyNumber);
                    editor.putString(Preferences.HOURLY_RATE, HourlyRate);
                    editor.commit();
                    finish();
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                        handler.sendEmptyMessage(2);
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
    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtCompanyName = (EditText) findViewById(R.id.txtCompanyName);
        txtYearInBusiness = (EditText) findViewById(R.id.txtYearInBusiness);
        txtEniNumber = (EditText) findViewById(R.id.txtEniNumber);
        txtInsuranceCarr = (EditText) findViewById(R.id.txtInsuranceCarr);
        txtPolicyNumber = (EditText) findViewById(R.id.txtPolicyNumber);
        txtHourlyRate = (TextView) findViewById(R.id.txtHourlyRate);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showHourlyRateDialog() {
        final String[] TYPES = new String[]{"$45","$55","$65","$75","$85","$95","$100", "$105", "$115", "$125"};
        final String[] TYPES_NUMERIC = new String[]{"45","55","65","75","85", "95","100", "105","115", "125"};
        final Dialog dialog = new Dialog(CompanyInformation_Activity_Edit.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wheelviewdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        ImageView imgok = (ImageView) dialog.findViewById(R.id.img_ok);
        WheelView wheelView = (WheelView) dialog.findViewById(R.id.wheelView);
        wheelView.setOffset(1);
        wheelView.setSeletion(2);
        wheelView.setItems(Arrays.asList(TYPES));
        if (HourlyRate.length() == 0) {
            HourlyRate = TYPES_NUMERIC[1].toString();
            txtHourlyRate.setText(TYPES[1].toString());
        }

        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d("", "selectedIndex: " + selectedIndex + ", item: " + item);
                HourlyRate = TYPES_NUMERIC[selectedIndex - 1].toString();
                txtHourlyRate.setText(TYPES[selectedIndex - 1].toString());
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        imgok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showAlertDialog(String Title, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                CompanyInformation_Activity_Edit.this);

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

    private void showAlertDialogRate(String Title, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                CompanyInformation_Activity_Edit.this);

        // set title
        alertDialogBuilder.setTitle(Title);

        // set dialog message
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setNegativeButton("Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                        showHourlyRateDialog();
                    }
                }).setPositiveButton("Ok,Countinue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close
                // current activity
                dialog.cancel();
                GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",updateResponseListener,exceptionListener,CompanyInformation_Activity_Edit.this,"");
                getApiResponseAsyncNew.execute(finalRequestParams);
            }
        });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
