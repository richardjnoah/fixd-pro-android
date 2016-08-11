package fixtpro.com.fixtpro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import fixtpro.com.fixtpro.beans.AssignTechModal;
import fixtpro.com.fixtpro.beans.AvailableJobModal;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;
import fixtpro.com.fixtpro.views.RatingBarView;

public class ConfirmAssignTechActivity extends AppCompatActivity {
    AssignTechModal modal = null ;
    TextView textChange,done,textTitleName,date,time_interval,contact_name,address;
    SharedPreferences _prefs = null ;
    RatingBarView cusRatingbar;
    AvailableJobModal modelAvail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_assign_tech);
//        getSupportActionBar().hide();
        _prefs = Utilities.getSharedPreferences(this);
        setWidgets();
        setListeners();
        if (getIntent() != null){
            modal = (AssignTechModal)getIntent().getSerializableExtra("TechInfo");
            modelAvail = (AvailableJobModal) getIntent().getSerializableExtra("JOB_DETAIL");
            textTitleName.setText(modal.getFirstName());
            date.setText(Utilities.convertDate(modelAvail.getRequest_date()));
            time_interval.setText(Utilities.getFormattedTimeSlots(modelAvail.getTimeslot_start()) + " - " + Utilities.getFormattedTimeSlots(modelAvail.getTimeslot_end()));
            contact_name.setText(modelAvail.getContact_name());
            address.setText(modelAvail.getJob_customer_addresses_zip() + " - " + modelAvail.getJob_customer_addresses_city() + "," + modelAvail.getJob_customer_addresses_state());
            cusRatingbar.setStar((int) Float.parseFloat(modal.getRating()), true);
        }
    }
    private void setWidgets(){
        textChange = (TextView)findViewById(R.id.textChange);
        done = (TextView)findViewById(R.id.done);
        date = (TextView)findViewById(R.id.date);
        time_interval = (TextView)findViewById(R.id.time_interval);
        textTitleName = (TextView)findViewById(R.id.textTitleName);
        contact_name = (TextView)findViewById(R.id.contact_name);
        address = (TextView)findViewById(R.id.address);
        cusRatingbar = (RatingBarView)findViewById(R.id.cusRatingbar);
    }
    private void setListeners(){
        textChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmAssignTechActivity.this,HomeScreenNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
//                GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST",assignTechListener,ConfirmAssignTechActivity.this,"Assigning");
//                responseAsync.execute(getAssignTechParametrs());
            }
        });
    }
    private HashMap<String,String> getAssignTechParametrs(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "assign");
        hashMap.put("object", "jobs");
        hashMap.put("data[technician_id]", modal.getTech_User_id());
        hashMap.put("data[id]", FixdProApplication.SelectedAvailableJobId);
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN,""));
        return hashMap;
    }
    ResponseListener assignTechListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("","Response"+Response);
        }
    };
}
