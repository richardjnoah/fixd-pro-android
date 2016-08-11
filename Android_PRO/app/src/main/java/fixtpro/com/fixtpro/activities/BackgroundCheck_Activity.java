package fixtpro.com.fixtpro.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.adapters.StateListAdapter;
import fixtpro.com.fixtpro.utilites.Utilities;

public class BackgroundCheck_Activity extends AppCompatActivity {
    Context context = BackgroundCheck_Activity.this;
    public static final String TAG = "BackgroundCheck_Activity";
    ImageView imgClose, imgNext;
    EditText txtMiddleName, txtDriverLiecenseNo;
    TextView txtUserName, txtSocialSecurityNo, txtBirthDay, txtDriverLiecenseState;
    CheckBox checkMiddleName;
    Dialog dialog1;

    String middleName, bithDay, driverLiecenseNo, driverLiecenceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_check_);

        setWidgetsID();

        setCLickListner();
    }

    private void setWidgetsID() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtMiddleName = (EditText) findViewById(R.id.txtMiddleName);
        txtBirthDay = (TextView) findViewById(R.id.txtBirthDay);
        txtDriverLiecenseNo = (EditText) findViewById(R.id.txtDriverLiecenseNo);
        txtDriverLiecenseState = (TextView) findViewById(R.id.txtDriverLiecenseState);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtSocialSecurityNo = (TextView) findViewById(R.id.txtSocialSecurityNo);
        checkMiddleName = (CheckBox) findViewById(R.id.checkMiddleName);
    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                middleName = txtMiddleName.getText().toString().trim();
                bithDay = txtBirthDay.getText().toString().trim();
                driverLiecenseNo = txtDriverLiecenseNo.getText().toString().trim();
                driverLiecenceState = txtDriverLiecenseState.getText().toString().trim();
                if (middleName.equals("")){
                    showAlertDialog(context.getResources().getString(R.string.alert_title),"Please enter the middle name.");
                }else if (bithDay.equals("")){
                    showAlertDialog(context.getResources().getString(R.string.alert_title),"Please enter the birthday.");
                }else if (driverLiecenseNo.equals("")){
                    showAlertDialog(context.getResources().getString(R.string.alert_title),"Please enter the driver liecense number.");
                }else if (driverLiecenceState.equals("")){
                    showAlertDialog(context.getResources().getString(R.string.alert_title),"Please enter the driver liecense state");
                }else {
                    /****Run Api****/
                    Intent i = new Intent(WorkingRadiusNew.this, BackgroundCheck_Activity.class);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
