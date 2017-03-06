package fixdpro.com.fixdpro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import fixdpro.com.fixdpro.beans.AssignTechModal;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.Constants;
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
    String role = "pro";
    AvailableJobModal jobModal = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_assign_tech);
//        getSupportActionBar().hide();
        _prefs = Utilities.getSharedPreferences(this);
        role = _prefs.getString(Preferences.ROLE, "");
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

                Singleton.getInstance().getAvailablejoblist().remove(i);
                break;
            }

        }
        Intent intent = new Intent(ConfirmAssignTechActivity.this,HomeScreenNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("switch_tab","Scheduled");
        startActivity(intent);
//        getDetailedJob(modelAvail.getId());
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
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }
    ResponseListener assignTechListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("","Response"+Response);
        }
    };

    private void getDetailedJob(String id){
        //get the job
        GetApiResponseAsyncNew responseAsync = new  GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerScheduled,exceptionListener, ConfirmAssignTechActivity.this, "Loading");
        responseAsync.execute(getRequestParams(id, "read_open"));

    }

    IHttpResponseListener responseListenerScheduled = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
//                    nextScheduled = pagination.getString("next");
                    for(int i = 0; i < results.length(); i++){
                        JSONObject obj = results.getJSONObject(i);
                        jobModal = new AvailableJobModal();
                        jobModal.setContact_name(obj.getString("contact_name"));
                        jobModal.setCreated_at(obj.getString("created_at"));
                        jobModal.setCustomer_id(obj.getString("customer_id"));
                        jobModal.setCustomer_notes(obj.getString("customer_notes"));
                        jobModal.setFinished_at(obj.getString("finished_at"));
                        jobModal.setId(obj.getString("id"));
                        jobModal.setJob_id(obj.getString("job_id"));
//                        jobModal.setLatitude(obj.getDouble("latitude"));
                        jobModal.setLocked_by(obj.getString("locked_by"));
                        jobModal.setLocked_on(obj.getString("locked_on"));
//                        jobModal.setLongitude(obj.getDouble("longitude"));
                        jobModal.setPhone(obj.getString("phone"));
                        jobModal.setPro_id(obj.getString("pro_id"));
                        jobModal.setRequest_date(obj.getString("request_date"));
//                        jobModal.setService_id(obj.getString("service_id"));
//                        jobModal.setService_type(obj.getString("service_type"));
                        jobModal.setStarted_at(obj.getString("started_at"));
                        jobModal.setStatus(obj.getString("status"));
                        jobModal.setTechnician_id(obj.getString("technician_id"));
                        jobModal.setTime_slot_id(obj.getString("time_slot_id"));
                        jobModal.setTitle(obj.getString("title"));
//                        jobModal.setTotal_cost(obj.getString("total_cost"));
                        jobModal.setUpdated_at(obj.getString("updated_at"));
                        if (!obj.isNull("customers")){
                            if (!obj.getJSONObject("customers").isNull("users")){
                                if (obj.getJSONObject("customers").getJSONObject("users").getString("company_id").equals("FE")){
                                    jobModal.setIs_fe_job("1");
                                } else {
                                    jobModal.setIs_fe_job("0");
                                }
                            }
                        }

//                        jobModal.setWarranty(obj.getString("warranty"));
//                        if(Utilities.getSharedPreferences(getContext()).getString(Preferences.ROLE, null).equals("pro")) {
                        JSONArray jobAppliances = obj.getJSONArray("job_appliances");
                        ArrayList<JobAppliancesModal> jobapplianceslist = new ArrayList<JobAppliancesModal>();
                        if(jobAppliances != null){
                            for (int j = 0; j < jobAppliances.length(); j++) {
                                JSONObject jsonObject = jobAppliances.getJSONObject(j);
                                JobAppliancesModal mod = new JobAppliancesModal();
                                mod.setJob_appliances_id(jsonObject.getString("id"));
                                mod.setJob_appliances_job_id(jsonObject.getString("job_id"));
                                mod.setJob_appliances_appliance_id(jsonObject.getString("appliance_id"));
                                mod.setJob_appliances_brand_name(jsonObject.getString("brand_name"));


                                if (!jsonObject.isNull("description")){
                                    mod.setJob_appliances_appliance_description(jsonObject.getString("description"));
                                }
                                if (!jsonObject.isNull("power_source")){
                                    mod.setJob_appliances_power_source(jsonObject.getString("power_source"));
                                }
                                if (!jsonObject.isNull("brand_name")){
                                    mod.setJob_appliances_brand_name(jsonObject.getString("brand_name"));
                                }
                                if (!jsonObject.isNull("service_type")){
                                    mod.setJob_appliances_service_type(jsonObject.getString("service_type"));
                                }
                                if (!jsonObject.isNull("customer_complaint")) {
                                    mod.setJob_appliances_customer_compalint(jsonObject.getString("customer_complaint"));
                                }
                                if (!jsonObject.isNull("image")){
                                    JSONObject image_obj = jsonObject.getJSONObject("image");
                                    if(!image_obj.isNull("original")){
                                        mod.setImg_original(image_obj.getString("original"));
                                        mod.setImg_160x170(image_obj.getString("160x170"));
                                        mod.setImg_150x150(image_obj.getString("150x150"));
                                        mod.setImg_75x75(image_obj.getString("75x75"));
                                        mod.setImg_30x30(image_obj.getString("30x30"));
                                    }
                                }
                                if (!jsonObject.isNull("appliance_types")){
                                    JSONObject appliance_type_obj = jsonObject.getJSONObject("appliance_types");
                                    mod.setAppliance_type_id(appliance_type_obj.getString("id"));
                                    mod.setAppliance_type_has_power_source(appliance_type_obj.getString("has_power_source"));
                                    mod.setAppliance_type_service_id(appliance_type_obj.getString("service_id"));
                                    mod.setAppliance_type_name(appliance_type_obj.getString("name"));
                                    mod.setAppliance_type_soft_deleted(appliance_type_obj.getString("_soft_deleted"));
                                    if (!appliance_type_obj.isNull("image")){
                                        JSONObject image_obj = appliance_type_obj.getJSONObject("image");
                                        if( !image_obj.isNull("original")){
                                            mod.setAppliance_type_image_original(image_obj.getString("original"));

                                        }
                                    }
                                    if (!appliance_type_obj.isNull("services")){
                                        JSONObject services_obj = appliance_type_obj.getJSONObject("services");
                                        mod.setService_id(services_obj.getString("id"));
                                        mod.setService_name(services_obj.getString("name"));
                                        mod.setService_created_at(services_obj.getString("created_at"));
                                        mod.setService_updated_at(services_obj.getString("updated_at"));
                                    }
                                }


//                                JSONObject services_obj = jsonObject.getJSONObject("services");
//                                mod.setService_id(services_obj.getString("id"));
//                                mod.setService_name(services_obj.getString("name"));
//                                mod.setService_created_at(services_obj.getString("created_at"));
//                                mod.setService_updated_at(services_obj.getString("updated_at"));
                                jobapplianceslist.add(mod);
                            }
//                            }
                            jobModal.setJob_appliances_arrlist(jobapplianceslist);
                        }
                        if (!obj.isNull("technicians")){
                            JSONObject technician_object  =  obj.getJSONObject("technicians");
                            jobModal.setTechnician_technicians_id(technician_object.getString("id"));
                            jobModal.setTechnician_user_id(technician_object.getString("user_id"));
                            jobModal.setTechnician_fname(technician_object.getString("first_name"));
                            jobModal.setTechnician_lname(technician_object.getString("last_name"));
                            jobModal.setTechnician_pickup_jobs(technician_object.getString("pickup_jobs"));
                            jobModal.setTechnician_avg_rating(technician_object.getString("avg_rating"));
                            jobModal.setTechnician_scheduled_job_count(technician_object.getString("scheduled_jobs_count"));
                            jobModal.setTechnician_completed_job_count(technician_object.getString("completed_jobs_count"));
                            if (!technician_object.isNull("profile_image")){
                                JSONObject object_profile_image  = technician_object.getJSONObject("profile_image");
                                if (!object_profile_image.isNull("original"))
                                    jobModal.setTechnician_profile_image(object_profile_image.getString("original"));
                            }

                        }
                        if (!obj.isNull("time_slots")){
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            jobModal.setTime_slot_id(time_slot_obj.getString("id"));
                            jobModal.setTimeslot_start(time_slot_obj.getString("start"));
                            jobModal.setTimeslot_end(time_slot_obj.getString("end"));
                            jobModal.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));
                        }


                        if (!obj.isNull("job_customer_addresses")){
                            JSONObject job_customer_addresses_obj = obj.getJSONObject("job_customer_addresses");
                            jobModal.setJob_customer_addresses_id(job_customer_addresses_obj.getString("id"));
                            jobModal.setJob_customer_addresses_zip(job_customer_addresses_obj.getString("zip"));
                            jobModal.setJob_customer_addresses_city(job_customer_addresses_obj.getString("city"));
                            jobModal.setJob_customer_addresses_state(job_customer_addresses_obj.getString("state"));
                            jobModal.setJob_customer_addresses_address(job_customer_addresses_obj.getString("address"));
                            jobModal.setJob_customer_addresses_address_2(job_customer_addresses_obj.getString("address_2"));
                            jobModal.setJob_customer_addresses_updated_at(job_customer_addresses_obj.getString("updated_at"));
                            jobModal.setJob_customer_addresses_created_at(job_customer_addresses_obj.getString("created_at"));
                            jobModal.setJob_customer_addresses_job_id(job_customer_addresses_obj.getString("job_id"));
                            jobModal.setJob_customer_addresses_latitude(job_customer_addresses_obj.getDouble("latitude"));
                            jobModal.setJob_customer_addresses_longitude(job_customer_addresses_obj.getDouble("longitude"));
                        }

                    }


                }else {
                    Intent intent = new Intent(ConfirmAssignTechActivity.this,HomeScreenNew.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("switch_tab","Scheduled");
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Intent intent = new Intent(ConfirmAssignTechActivity.this,HomeScreenNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("switch_tab","Scheduled");
                startActivity(intent);
                finish();
            }
        }
    };
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
//            error_message = exception;
//            handler.sendEmptyMessage(1);
            Intent intent = new Intent(ConfirmAssignTechActivity.this,HomeScreenNew.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("switch_tab","Scheduled");
            startActivity(intent);
            finish();
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (jobModal != null){
                Singleton.getInstance().getSchedulejoblist().add(0,jobModal);
                Intent intent = new Intent(ConfirmAssignTechActivity.this,HomeScreenNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("switch_tab","Scheduled");
                startActivity(intent);
                finish();
            }
        }
    };
    private HashMap<String, String> getRequestParams(String id,String api) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", api);
        hashMap.put("object", "jobs");
        hashMap.put("expand[0]", "work_order");
        hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*,job_line_items.^*,customers.users.company_id");
        hashMap.put("where[id]", id + "");
        hashMap.put("token", Utilities.getSharedPreferences(ConfirmAssignTechActivity.this).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("page", "1");
        hashMap.put("per_page", "20");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }
}
