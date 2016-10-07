package fixdpro.com.fixdpro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import fixdpro.com.fixdpro.beans.AssignTechModal;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Singleton;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.RatingBarView;

public class ConfirmAssignTechActivity extends AppCompatActivity {
    AssignTechModal modal = null ;
    TextView textChange,done,textTitleName,date,time_interval,contact_name,address,textJobSchedule;
    SharedPreferences _prefs = null ;
    fixdpro.com.fixdpro.views.CircularImageView circleImage ;
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
            time_interval.setText(modelAvail.getTimeslot_name());
            contact_name.setText(modal.getFirstName() +" "+modal.getLasttName());
            address.setText(modelAvail.getJob_customer_addresses_zip() + " - " + modelAvail.getJob_customer_addresses_city() + "," + modelAvail.getJob_customer_addresses_state());
            cusRatingbar.setStar((int) Float.parseFloat(modal.getRating()), true);
            textJobSchedule.setText(modal.getFirstName() + " has " + (Integer.parseInt(modal.getJobSchedule()) + 1) + " job schduled for this time");
            modal.setJobSchedule(Integer.parseInt(modal.getJobSchedule() +1)+"");
            if (modal.getImage().length() > 0)
                Picasso.with(this).load(modal.getImage())
                        .into(circleImage);
        }
    }
    private void setWidgets(){
        textChange = (TextView)findViewById(R.id.textChange);
        done = (TextView)findViewById(R.id.done);
        date = (TextView)findViewById(R.id.date);
        time_interval = (TextView)findViewById(R.id.time_interval);
        textJobSchedule = (TextView)findViewById(R.id.textJobSchedule);
        textTitleName = (TextView)findViewById(R.id.textTitleName);
        contact_name = (TextView)findViewById(R.id.contact_name);
        address = (TextView)findViewById(R.id.address);
        cusRatingbar = (RatingBarView)findViewById(R.id.cusRatingbar);
        circleImage = (fixdpro.com.fixdpro.views.CircularImageView)findViewById(R.id.circleImage);
    }
    private void changeStatusofJob(){
        for (int i = 0; i < Singleton.getInstance().getAvailablejoblist().size() ; i++){
            AvailableJobModal modallocal = Singleton.getInstance().getAvailablejoblist().get(i);
            if (modallocal.getId().equals(modelAvail.getId())){
                modallocal.setStatus("Scheduled");
                modallocal.setTechnician_technicians_id(modal.getTechId());
                modallocal.setTechnician_user_id(modal.getTech_User_id());
                modallocal.setTechnician_fname(modal.getFirstName());
                modallocal.setTechnician_lname(modal.getLasttName());
                modallocal.setTechnician_pickup_jobs(modal.getJobSchedule() + 1);
                modallocal.setTechnician_avg_rating(modal.getRating());

                modallocal.setTechnician_profile_image(modal.getImage());

                Singleton.getInstance().getSchedulejoblist().add(0, modallocal);
                Singleton.getInstance().getAvailablejoblist().remove(i);
                break;
            }
        }
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

                changeStatusofJob();
                Intent intent = new Intent(ConfirmAssignTechActivity.this,HomeScreenNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("switch_tab","Scheduled");
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
