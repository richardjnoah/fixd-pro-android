package fixtpro.com.fixtpro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fixtpro.com.fixtpro.beans.AvailableJobModal;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class ConfirmationActivity extends AppCompatActivity implements View.OnClickListener{
    private Context context = ConfirmationActivity.this;
    private String TAG = "ConfirmationActivity";
    TextView done, date, time_interval, contact_name, address;
    ImageView action;
    SharedPreferences _prefs = null;
    AvailableJobModal model;
    boolean isJobReschduled = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
//        getSupportActionBar().hide();
        _prefs = Utilities.getSharedPreferences(context);
        model = (AvailableJobModal) getIntent().getSerializableExtra("JOB_DETAIL");
        setWidgets();
        setListeners();
        if(Utilities.getSharedPreferences(this).getString(Preferences.ROLE, "").equals("pro")){
            action.setImageResource(R.drawable.assign_technician);
            done.setVisibility(View.GONE);
        }else if(Utilities.getSharedPreferences(this).getString(Preferences.ROLE, "").equals("technician")){
            action.setImageResource(R.drawable.view_in_calendar);
            done.setVisibility(View.VISIBLE);
        }

        if (getIntent().getExtras() != null && !getIntent().getExtras().containsKey("confirm")){
            isJobReschduled = true ;
            action.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);
        }
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
                Intent intent = new Intent(ConfirmationActivity.this,HomeScreenNew.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.action:
                if(Utilities.getSharedPreferences(this).getString(Preferences.ROLE, null).equals("pro")){
                    Intent i = new Intent(context,AssignTechnicianActivity.class);
                    i.putExtra("isAvailable",true);
                    i.putExtra("api","assign");
                    i.putExtra("JOB_DETAIL",model);
                    startActivity(i);
                }else if(Utilities.getSharedPreferences(this).getString(Preferences.ROLE, null).equals("technician")){
                    Intent i = new Intent(context,CalendarActivity.class);
                    startActivity(i);
                }
                break;
        }
    }
}
