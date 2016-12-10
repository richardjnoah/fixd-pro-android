package fixdpro.com.fixdpro.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.beans.ProfileComplitionValuesBean;
import fixdpro.com.fixdpro.singleton.TemporaryProfileComplitionSignleton;
import fixdpro.com.fixdpro.views.WheelView;

public class CompanyInformation_Activity extends AppCompatActivity {

    EditText txtCompanyName, txtYearInBusiness, txtEniNumber, txtInsuranceCarr, txtPolicyNumber;
    TextView txtHourlyRate;
    ImageView imgNext, imgClose;
    String CompanyName = "", YearInBusiness = "", EniNumber = "", InsuranceCarrier = "", PolicyNumber = "", HourlyRate = "";
    String ENI_hint_text = "";
    HashMap<String,String> finalRequestParams = null ;
    boolean ispro = false ;
    boolean iscompleting = false;
    ProfileComplitionValuesBean temporaryProfileComplitionSignleton = TemporaryProfileComplitionSignleton.getInstance().getInstance().getProfileComplitionValuesBean();
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
            if (bundle.containsKey("iscompleting")){
                iscompleting = bundle.getBoolean("iscompleting");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtCompanyName.setText(temporaryProfileComplitionSignleton.getCompanyName());
        txtYearInBusiness.setText(temporaryProfileComplitionSignleton.getYearInBusiness());
        txtInsuranceCarr.setText(temporaryProfileComplitionSignleton.getInsuranceCarrier());
        txtEniNumber.setText(temporaryProfileComplitionSignleton.getEniNumber());
        txtPolicyNumber.setText(temporaryProfileComplitionSignleton.getPolicyNumber());
        txtHourlyRate.setText("$"+temporaryProfileComplitionSignleton.getHourlyRate());
        HourlyRate = temporaryProfileComplitionSignleton.getHourlyRate();
    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
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
//                HourlyRate = txtHourlyRate.getText().toString().trim();
                if (CompanyName.equals("")){
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the company name.");
                }else if (YearInBusiness.equals("")){
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the year in business.");
                }else if (Integer.parseInt(YearInBusiness) > 80){
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Pleae enter a valid value for Number of Years in Business < 80.");
                }else if (EniNumber.equals("")){
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Pleae enter the ein number.");
                }else if (EniNumber.length() < 9){
                    showAlertDialog(CompanyInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "The entered ein number looks invalid.");
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

                    finalRequestParams.put("data[pros][company_name]", CompanyName);
                    finalRequestParams.put("data[technicians][years_in_business]", YearInBusiness);
                    finalRequestParams.put("data[pros][ein_number]", EniNumber);
                    finalRequestParams.put("data[pros][insurance]", InsuranceCarrier);
                    finalRequestParams.put("data[pros][insurance_policy]", PolicyNumber);
                    finalRequestParams.put("data[pros][hourly_rate]", HourlyRate);
                    temporaryProfileComplitionSignleton.setCompanyName(CompanyName);
                    temporaryProfileComplitionSignleton.setYearInBusiness(YearInBusiness);
                    temporaryProfileComplitionSignleton.setEniNumber(EniNumber);
                    temporaryProfileComplitionSignleton.setInsuranceCarrier(InsuranceCarrier);
                    temporaryProfileComplitionSignleton.setPolicyNumber(PolicyNumber);
                    temporaryProfileComplitionSignleton.setHourlyRate(HourlyRate);
                    if (HourlyRate.equals("125")){
                        showAlertDialogRate("Fixd-Pro","Keep in mind Pros with lower labor ates will see available job first.Higher labor rates may limit your job availability");
                    }else{
                        Intent intent = new Intent(CompanyInformation_Activity.this,LicensePicture_Activity.class);
//                        Intent intent = new Intent(CompanyInformation_Activity.this,New_LicensePicture_Activity.class);
                        intent.putExtra("ispro", ispro);
                        intent.putExtra("iscompleting", iscompleting);
                        intent.putExtra("finalRequestParams", finalRequestParams);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }

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
        final String[] TYPES = new String[]{"$45","$55","$65","$75","$85","$95","$100", "$105", "$115", "$125"};
        final String[] TYPES_NUMERIC = new String[]{"45","55","65","75","85", "95","100", "105","115", "125"};
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
    }private void showAlertDialogRate(String Title, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                CompanyInformation_Activity.this);

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
                Intent intent = new Intent(CompanyInformation_Activity.this,LicensePicture_Activity.class);
                intent.putExtra("ispro", ispro);
                intent.putExtra("iscompleting", iscompleting);
                intent.putExtra("finalRequestParams", finalRequestParams);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
