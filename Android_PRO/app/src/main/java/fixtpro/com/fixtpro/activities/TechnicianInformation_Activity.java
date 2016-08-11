package fixtpro.com.fixtpro.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.utilites.Utilities;

public class TechnicianInformation_Activity extends AppCompatActivity {
    Context context = this;
    ImageView imgClose, imgNext;
    EditText txtFirstName, txtLastName, txtEmailAdd, txtMobile;
    CheckBox checkJobPickUp;
    public String firstName, lastName, email, mobileNumber, middleName;
    boolean ispickUp_jobs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_information_);
        setWidgets();
        setCLickListner();
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtEmailAdd = (EditText) findViewById(R.id.txtEmailAdd);
        txtMobile = (EditText) findViewById(R.id.txtMobile);
        checkJobPickUp = (CheckBox) findViewById(R.id.checkJobPickUp);


    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        checkJobPickUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ispickUp_jobs = isChecked;
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = txtFirstName.getText().toString().trim();
                lastName = txtLastName.getText().toString().trim();
                email = txtEmailAdd.getText().toString().trim();
                mobileNumber = txtMobile.getText().toString().trim();
                if (firstName.equals("")) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Please enter the first name.");
                } else if (lastName.equals("")) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Please enter the last name.");
                } else if (email.equals("")) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Please enter the email address.");
                } else if (!Utilities.isValidEmail(email)) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Please enter the valid email address.");
                } else if (mobileNumber.equals("")) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Please enter the mobile number.");
                } else if (!(mobileNumber.length() < 10)) {
                    showAlertDialog(TechnicianInformation_Activity.this.getResources().getString(R.string.alert_title),
                            "Your phone number seems to invalid, Please try again.");
                } else if (ispickUp_jobs) {

                    Utilities.hideKeyBoad(context, TechnicianInformation_Activity.this.getCurrentFocus());
                    /*****Run Api****/
                    updateTech();

                }
            }
        });
    }

    private void updateTech(){

    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showAlertDialog(String Title, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                TechnicianInformation_Activity.this);

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
