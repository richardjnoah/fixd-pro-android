package fixdpro.com.fixdpro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.Utilities;

public class ConfirmRescheduleActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context = ConfirmRescheduleActivity.this;
    private String TAG = "ConfirmRescheduleActivity";
    TextView done, date, time_interval, contact_name, address;
    ImageView action;
    SharedPreferences _prefs = null;
    AvailableJobModal model;
    boolean isJobReschduled = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reschedule);
        _prefs = Utilities.getSharedPreferences(context);
        model = (AvailableJobModal) getIntent().getSerializableExtra("JOB_DETAIL");
        setWidgets();
        setListeners();
//        if(Utilities.getSharedPreferences(this).getString(Preferences.ROLE, "").equals("pro")){
//            action.setImageResource(R.drawable.assign_technician);
//            done.setVisibility(View.VISIBLE);
//            done.setText("Back");
//        }else if(Utilities.getSharedPreferences(this).getString(Preferences.ROLE, "").equals("technician")){
//            action.setImageResource(R.drawable.view_in_calendar);
//            done.setVisibility(View.VISIBLE);
//        }

//        if (getIntent().getExtras() != null && !getIntent().getExtras().containsKey("confirm")){
//            isJobReschduled = true ;
//            action.setVisibility(View.GONE);
//            done.setVisibility(View.VISIBLE);
//        }
        date.setText(Utilities.convertDate(model.getRequest_date()));
        time_interval.setText(model.getTimeslot_name());
        contact_name.setText(model.getContact_name());
        address.setText(model.getJob_customer_addresses_zip() + " - " + model.getJob_customer_addresses_city() + "," + model.getJob_customer_addresses_state());
    }

    public void setWidgets(){
        done = (TextView) findViewById(R.id.done);
        date = (TextView) findViewById(R.id.date);
        time_interval = (TextView) findViewById(R.id.time_interval);
        contact_name = (TextView) findViewById(R.id.contact_name);
        address = (TextView) findViewById(R.id.address);
        action = (ImageView) findViewById(R.id.action);
    }

    public void setListeners(){
        done.setOnClickListener(this);
        action.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.done:

                    CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(null);
                    Intent intent = new Intent(ConfirmRescheduleActivity.this,HomeScreenNew.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                break;
        }
    }
}
