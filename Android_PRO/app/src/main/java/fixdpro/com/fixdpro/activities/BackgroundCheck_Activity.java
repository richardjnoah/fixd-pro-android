package fixdpro.com.fixdpro.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.adapters.StateListAdapter;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

public class BackgroundCheck_Activity extends AppCompatActivity {
    Context context = BackgroundCheck_Activity.this;
    public static final String TAG = "BackgroundCheck_Activity";
    ImageView imgClose, imgNext,imgMessage;
    EditText txtMiddleName, txtDriverLiecenseNo,txtSocialSecurityNo;
    TextView txtUserName, txtBirthDay, txtDriverLiecenseState ;
    CheckBox checkMiddleName;
    Dialog dialog1;
    boolean ispro = false ;
    HashMap<String,String> finalRequestParams = null ;
    String middleName = "", bithDay = "", driverLiecenseNo = "", driverLiecenceState = "";
    String security_number = "";
    String selectedImagePathUser = null ;
    String  selectedImagePathDriver = null;
    boolean iscompleting = false ;
    SharedPreferences _prefs = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_check_);
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
        setWidgetsID();

        setCLickListner();
    }

    private void setWidgetsID() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgMessage = (ImageView) findViewById(R.id.imgMessage);
        txtMiddleName = (EditText) findViewById(R.id.txtMiddleName);
        txtBirthDay = (TextView) findViewById(R.id.txtBirthDay);
        txtDriverLiecenseNo = (EditText) findViewById(R.id.txtDriverLiecenseNo);
        txtDriverLiecenseState = (TextView) findViewById(R.id.txtDriverLiecenseState);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtSocialSecurityNo = (EditText) findViewById(R.id.txtSocialSecurityNo);
        checkMiddleName = (CheckBox) findViewById(R.id.checkMiddleName);
        txtUserName.setText(_prefs.getString(Preferences.FIRST_NAME, "") + " " + _prefs.getString(Preferences.LAST_NAME, ""));
    }

    private void setCLickListner() {
        ;
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        txtBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateOfBirthDialog();
            }
        });
        txtDriverLiecenseState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStateList();
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                security_number = txtSocialSecurityNo.getText().toString();
                middleName = txtMiddleName.getText().toString().trim();
                bithDay = txtBirthDay.getText().toString().trim();
                driverLiecenseNo = txtDriverLiecenseNo.getText().toString().trim();
                driverLiecenceState = txtDriverLiecenseState.getText().toString().trim();

                if (middleName.equals("") && !checkMiddleName.isChecked()) {
                    showAlertDialog(context.getResources().getString(R.string.alert_title), "Please enter the middle name.");
                } else if (bithDay.equals("")) {
                    showAlertDialog(context.getResources().getString(R.string.alert_title), "Please enter the birthday.");
                } else if (security_number.length() != 9) {
                    showAlertDialog("Fixd-Pro", "Security number seems to be invalid.");
                    return;
                } else if (driverLiecenseNo.equals("")) {
                    showAlertDialog(context.getResources().getString(R.string.alert_title), "Please enter the driver liecense number.");
                } else if (driverLiecenceState.equals("")) {
                    showAlertDialog(context.getResources().getString(R.string.alert_title), "Please enter the driver liecense state");
                } else {
                    /****Run Api****/
                    String temp_securiyt_number = security_number.substring(0, 3) + "-" + security_number.substring(3, 5) + "-" + security_number.substring(5, security_number.length());
                    if (ispro) {
                        finalRequestParams.put("data[technicians][middle_name]", middleName);
                        finalRequestParams.put("data[technicians][dob]", bithDay);
                        finalRequestParams.put("data[technicians][driver_license_number]", driverLiecenseNo);
                        finalRequestParams.put("data[technicians][driver_license_state]", driverLiecenceState);
                        finalRequestParams.put("data[technicians][social_security_number]", temp_securiyt_number);
                    } else {
                        finalRequestParams.put("data[technicians][middle_name]", middleName);
                        finalRequestParams.put("dob", bithDay);
                        finalRequestParams.put("driver_license_number", driverLiecenseNo);
                        finalRequestParams.put("driver_license_state", driverLiecenceState);
                        finalRequestParams.put("social_security_number", temp_securiyt_number);
                    }

                    if ( finalRequestParams.containsKey("data[technicians][field_work]") && finalRequestParams.get("data[technicians][field_work]").equals("1") || !finalRequestParams.containsKey("data[technicians][field_work]")) {
                        Intent i = new Intent(BackgroundCheck_Activity.this, BackgroundCheck_Next_Activity.class);
                        i.putExtra("ispro", ispro);
                        i.putExtra("iscompleting", iscompleting);
                        i.putExtra("finalRequestParams", finalRequestParams);
                        i.putExtra("driver_image", selectedImagePathDriver);
                        i.putExtra("user_image", selectedImagePathUser);
                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        Intent i = new Intent(BackgroundCheck_Activity.this, AddTechnicion_Activity.class);
                        i.putExtra("ispro", ispro);
                        i.putExtra("iscompleting", iscompleting);
                        i.putExtra("finalRequestParams", finalRequestParams);
                        i.putExtra("driver_image", selectedImagePathDriver);
                        i.putExtra("user_image", selectedImagePathUser);
                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }

    private void showDateOfBirthDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_dateofbirth);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
        TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        final String format = "" + day + "/" + "" + month + "/" + "" + year;
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String year = datePicker.getYear() + "";
                String month = datePicker.getMonth() + "";
                String day = datePicker.getDayOfMonth() + "";
                if (month.length() == 1)
                    month = "0" + month;
                if (day.length() == 1)
                    day = "0" + day;
                txtBirthDay.setText(year + "-" + month + "-" + day);
            }
        });

        dialog.show();
    }

    private void showStateList() {

        dialog1 = new Dialog(context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.layout_tellus_more);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog1 components -listview
        ListView lst_TellUsMore = (ListView) dialog1.findViewById(R.id.lst_TellUsMore);
                /*To set the Adapter*/
        StateListAdapter mAdp = new StateListAdapter(Utilities.getStateList(), BackgroundCheck_Activity.this);
        lst_TellUsMore.setAdapter(mAdp);
        lst_TellUsMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog1.dismiss();
                txtDriverLiecenseState.setText(Utilities.getStateList().get(position));
                driverLiecenceState = Utilities.getStateList().get(position);
            }
        });
        dialog1.show();
    }

    private void showAlertDialog(String Title, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                BackgroundCheck_Activity.this);

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
