package fixtpro.com.fixtpro.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.Arrays;
import java.util.HashMap;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.views.WheelView;

public class CompanyInformation_Activity extends AppCompatActivity {

    EditText txtCompanyName, txtYearInBusiness, txtEniNumber, txtInsuranceCarr, txtPolicyNumber;
    TextView txtHourlyRate;
    ImageView imgNext, imgClose;
    String CompanyName = "", YearInBusiness = "", EniNumber = "", InsuranceCarrier = "", PolicyNumber = "", HourlyRate = "";
    String ENI_hint_text = "";
    HashMap<String,String> finalRequestParams = null ;
    boolean ispro = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_information_);

        setWidgets();

        setCLickListner();
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")){
                finalRequestParams = (HashMap<String,String>)bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")){
                ispro = bundle.getBoolean("ispro");
            }
        }
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
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the company name.");
                }else if (YearInBusiness.equals("")){
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the year in business.");
                }else if (EniNumber.equals("")){
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the eni number.");
                }else if (InsuranceCarrier.equals("")){
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the insurance carrier.");
                }else if(PolicyNumber.equals("")){
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the policy number.");
                }else if (HourlyRate.equals("")){
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the hourly rate.");
                }else{
                    Intent intent = new Intent(CompanyInformation_Activity.this,LicensePicture_Activity.class);
                    finalRequestParams.put("data[pros][company_name]", CompanyName);
                    finalRequestParams.put("data[technicians][years_in_business]", YearInBusiness);
                    finalRequestParams.put("data[pros][ein_number]", EniNumber);
                    finalRequestParams.put("data[pros][insurance]", InsuranceCarrier);
                    finalRequestParams.put("data[pros][insurance_policy]", PolicyNumber);
                    finalRequestParams.put("data[pros][hourly_rate]", HourlyRate);
                    intent.putExtra("ispro", ispro);
                    intent.putExtra("finalRequestParams", finalRequestParams);
                    startActivity(intent);

                }

            }
        });
    }

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
        final String[] TYPES = new String[]{"$75", "$95", "$100", "$125", "$150"};
        final String[] TYPES_NUMERIC = new String[]{"75", "95", "100", "125", "150"};
        final Dialog dialog = new Dialog(CompanyInformation_Activity.this);
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
                CompanyInformation_Activity.this);

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
}
