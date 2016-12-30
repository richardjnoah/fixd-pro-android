package fixdpro.com.fixdpro;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import fixdpro.com.fixdpro.adapters.CalanderEventsAdapter;
import fixdpro.com.fixdpro.adapters.CalendarAdapter;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.CalendarCollection;
import fixdpro.com.fixdpro.beans.CalenderScheduledJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.WheelView;

public class CalendarActivity extends AppCompatActivity {
    public GregorianCalendar cal_month, cal_month_copy;
    private CalendarAdapter cal_adapter;
    private TextView tv_month, back, done;
    private Context context= this;
    Typeface fontfamily ;
    ArrayList<CalenderScheduledJobModal> calenderScheduledJobModalArrayList = new ArrayList<CalenderScheduledJobModal>();
    String error_message = "";
    ArrayList<AvailableJobModal> list = new ArrayList<AvailableJobModal>();
    ListView lstScheduledJobs;
    /*Reschedule Dialog*/
    TextView txtRescheduleDialog;
    String Rescheduling = "0";
    String JobId = "" ;
    String selectedGridDate;
    String[] TYPES ;
    String[] IDS ;
    Date dategot;
    String timerInterval;
    CalanderEventsAdapter adapter;
    int posForRescheduled = 0;
    View viewRescheduled = null ;
    boolean isJobRescheduled = false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        if (getIntent().getExtras() != null){
            Rescheduling = getIntent().getStringExtra("Rescheduling");
            getTimeSlotes();

            done = (TextView) findViewById(R.id.done);
            if (Rescheduling.equals("1"))
            done.setVisibility(View.VISIBLE);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isJobRescheduled){
                        showAlertDialog("Fixd-Pro","Please Select a date before proceeding",false);
                    }else {

                        Intent j = new Intent(CalendarActivity.this, ConfirmRescheduleActivity.class);
                        j.putExtra("JOB_DETAIL",CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal());
                        j.putExtra("show_cal",false);
                        startActivity(j);
                    }
                }
            });
        }
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
//        getSupportActionBar().hide();
        cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
        cal_month_copy = (GregorianCalendar) cal_month.clone();
        cal_adapter = new CalendarAdapter(this, cal_month, CalendarCollection.date_collection_arr);
        lstScheduledJobs = (ListView)findViewById(R.id.lstScheduledJobs);
        setRescheduleDialog();

        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_month.setText(android.text.format.DateFormat.format("MMM", cal_month) +"\n"+ android.text.format.DateFormat.format("yyyy", cal_month));

        back = (TextView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageButton previous = (ImageButton) findViewById(R.id.ib_prev);

        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
                getEventJobs();

            }
        });

        ImageButton next = (ImageButton) findViewById(R.id.Ib_next);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();
                getEventJobs();
            }
        });

        GridView gridview = (GridView) findViewById(R.id.gv_calendar);
        gridview.setAdapter(cal_adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

//                ((CalendarAdapter) parent.getAdapter()).setSelected(v, position);
                 selectedGridDate = CalendarAdapter.day_string
                        .get(position);
                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*", "");
                int gridvalue = Integer.parseInt(gridvalueString);

                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }
                list = ((CalendarAdapter) parent.getAdapter()).getPositionList(selectedGridDate, CalendarActivity.this);
                boolean hasEvent = false;
                if (list.size() > 0)
                    hasEvent = true;
                ((CalendarAdapter) parent.getAdapter()).setSelected(v, position, hasEvent);
                adapter = new CalanderEventsAdapter(CalendarActivity.this, list, getResources());
                lstScheduledJobs.setAdapter(adapter);

                 posForRescheduled = position;
                 viewRescheduled = v ;
                if (Rescheduling.equals("1")) {
//                    Show reschedule Dialog and call api
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date datenow = new Date();
                    sdf.format(datenow);
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    format.setTimeZone(TimeZone.getDefault());
                    try {
                        dategot = format.parse(selectedGridDate);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (datenow.after(dategot)){
                        if (!datenow.equals(dategot))
                        showAlertDialog("Oh-no","You can't select past date",false);
                        return;
                    }
                    JobId = CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId();
                    showRescheduleDialog();

                }
            }


        });

        getEventJobs();

    }

    private void setRescheduleDialog() {
        txtRescheduleDialog = (TextView)findViewById(R.id.txtRescheduleDialog);
        txtRescheduleDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });
    }
    private void showRescheduleDialog(){
        ShowDialog();
    }
    private void ShowDialog() {

        final Dialog dialog = new Dialog(this);
        final int[] SelectedIndex = {0};

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_reschedule_new);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);

        WheelView wheelView = (WheelView) dialog.findViewById(R.id.wheelView);
        ImageView img_Reschedule = (ImageView) dialog.findViewById(R.id.img_Reschedule);
        wheelView.setOffset(1);
        wheelView.setItems(Arrays.asList(TYPES));
        wheelView.setSeletion(0);

        wheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                SelectedIndex[0] = selectedIndex;
                String aa = TYPES[selectedIndex];
                timerInterval = item;
            }
        });

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        dateFormatter.setTimeZone(TimeZone.getDefault());
        String scheduledData = dateFormatter.format(dategot);


        TextView txtScheduledDate = (TextView) dialog.findViewById(R.id.selected_date);

        txtScheduledDate.setText(scheduledData);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        img_Reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (timerInterval == null){
                    timerInterval = TYPES[0];
                }
                CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().setTimeslot_name(timerInterval);
                CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().setRequest_date(Utilities.convertToServerDate(dategot));
                reScheduleJob(IDS[SelectedIndex[0]]);

            }
        });
        dialog.show();
    }

    private void getEventJobs(){
        GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", eventListener, CalendarActivity.this, "Loading");
        responseAsync.execute(getEventsRequestParams());
    }
    ResponseListener eventListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    JSONObject jsonObject1 = Response.getJSONObject("RESPONSE");
                    Iterator iterator = jsonObject1.keys();
                    while (iterator.hasNext()){
                        CalenderScheduledJobModal modal = new CalenderScheduledJobModal();
                        String key = (String)iterator.next();
                        JSONObject inner = jsonObject1.getJSONObject(key);
                        modal.setDate(key);
                        modal.setDay(inner.getString("day"));
                        JSONArray results = inner.getJSONArray("jobs");

                        ArrayList<AvailableJobModal> list = new ArrayList<AvailableJobModal>();

                        for ( int i = 0 ; i < results.length() ; i++){

                            JSONObject obj = results.getJSONObject(i);
                            AvailableJobModal model = new AvailableJobModal();
                            model.setContact_name(obj.getString("contact_name"));
                            model.setCreated_at(obj.getString("created_at"));
                            model.setCustomer_id(obj.getString("customer_id"));
                            model.setCustomer_notes(obj.getString("customer_notes"));
                            model.setFinished_at(obj.getString("finished_at"));
                            model.setId(obj.getString("id"));

//                            model.setLatitude(obj.getDouble("latitude"));
//                            model.setLongitude(obj.getDouble("longitude"));
                            model.setPhone(obj.getString("phone"));
                            model.setPro_id(obj.getString("pro_id"));
                            model.setRequest_date(obj.getString("request_date"));
//                            model.setService_id(obj.getString("service_id"));
//                            model.setService_type(obj.getString("service_type"));
                            model.setStarted_at(obj.getString("started_at"));
                            model.setStatus(obj.getString("status"));
                            model.setTechnician_id(obj.getString("technician_id"));
                            model.setTime_slot_id(obj.getString("time_slot_id"));
                            model.setTitle(obj.getString("title"));
//                            model.setTotal_cost(obj.getString("total_cost"));
                            model.setUpdated_at(obj.getString("updated_at"));
//                            model.setWarranty(obj.getString("warranty"));
//                        if(Utilities.getSharedPreferences(getContext()).getString(Preferences.ROLE, null).equals("pro")) {
                            JSONArray jobAppliances = obj.getJSONArray("job_appliances");
                            ArrayList<JobAppliancesModal>  jobapplianceslist = new ArrayList<JobAppliancesModal>();
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
                                    if (!jsonObject.isNull("service_type")){
                                        mod.setJob_appliances_service_type(jsonObject.getString("service_type"));
                                    }
                                    if (!jsonObject.isNull("customer_complaint")) {
                                        mod.setJob_appliances_customer_compalint(jsonObject.getString("customer_complaint"));
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
                                            Log.e("", "-----" + image_obj.toString());
                                            if (!image_obj.isNull("original")) {
                                                mod.setImg_original(image_obj.getString("original"));
                                                mod.setImg_160x170(image_obj.getString("160x170"));
                                                mod.setImg_150x150(image_obj.getString("150x150"));
                                                mod.setImg_75x75(image_obj.getString("75x75"));
                                                mod.setImg_30x30(image_obj.getString("30x30"));
                                            }
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
                                model.setJob_appliances_arrlist(jobapplianceslist);
                            }
                            if (!obj.isNull("technicians")){
                                JSONObject technician_object  =  obj.getJSONObject("technicians");
                                model.setTechnician_id(technician_object.getString("id"));
                                model.setTechnician_fname(technician_object.getString("first_name"));
                                model.setTechnician_lname(technician_object.getString("last_name"));
                                model.setTechnician_pickup_jobs(technician_object.getString("pickup_jobs"));
                                model.setTechnician_avg_rating(technician_object.getString("avg_rating"));
                                model.setTechnician_scheduled_job_count(technician_object.getString("scheduled_jobs_count"));
                                model.setTechnician_completed_job_count(technician_object.getString("completed_jobs_count"));
                                if (!technician_object.isNull("profile_image")){
                                    JSONObject object_profile_image  = technician_object.getJSONObject("profile_image");
                                    if (!object_profile_image.isNull("original"))
                                        model.setTechnician_profile_image(object_profile_image.getString("original"));
                                }

                            }
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            model.setTime_slot_id(time_slot_obj.getString("id"));
                            model.setTimeslot_start(time_slot_obj.getString("start"));
                            model.setTimeslot_end(time_slot_obj.getString("end"));
                            model.setTimeslot_name(time_slot_obj.getString("name"));
                            model.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));

                            if (!obj.isNull("job_customer_addresses")){
                                JSONObject job_customer_addresses_obj = obj.getJSONObject("job_customer_addresses");
                                model.setJob_customer_addresses_id(job_customer_addresses_obj.getString("id"));
                                model.setJob_customer_addresses_zip(job_customer_addresses_obj.getString("zip"));
                                model.setJob_customer_addresses_city(job_customer_addresses_obj.getString("city"));
                                model.setJob_customer_addresses_state(job_customer_addresses_obj.getString("state"));
                                model.setJob_customer_addresses_address(job_customer_addresses_obj.getString("address"));
                                model.setJob_customer_addresses_address_2(job_customer_addresses_obj.getString("address_2"));
                                model.setJob_customer_addresses_updated_at(job_customer_addresses_obj.getString("updated_at"));
                                model.setJob_customer_addresses_created_at(job_customer_addresses_obj.getString("created_at"));
                                model.setJob_customer_addresses_job_id(job_customer_addresses_obj.getString("job_id"));
                                model.setJob_customer_addresses_latitude(job_customer_addresses_obj.getDouble("latitude"));
                                model.setJob_customer_addresses_longitude(job_customer_addresses_obj.getDouble("longitude"));
                            }

                            list.add(model);
                        }
                        if (results.length() > 0){

                            CalendarCollection.date_collection_arr.add(new CalendarCollection(key,key,list));
                        }
                        modal.setList(list);
                        calenderScheduledJobModalArrayList.add(modal);
                    }
                    handler.sendEmptyMessage(3);
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    protected void setNextMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month
                .getActualMaximum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1),
                    cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) + 1);
        }

    }

    protected void setPreviousMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1),
                    cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) - 1);
        }

    }

    public void refreshCalendar() {
        cal_adapter.refreshDays();
        cal_adapter.notifyDataSetChanged();
//        tv_month.setText(android.text.format.DateFormat.format("MMMM yyyy", cal_month));
        tv_month.setText(android.text.format.DateFormat.format("MMMM", cal_month) +"\n"+ android.text.format.DateFormat.format("yyyy", cal_month));

    }
    private HashMap<String,String> getEventsRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","jobs");
        hashMap.put("object","calendar");
        if (Utilities.getSharedPreferences(this).getString(Preferences.ROLE,"").equals("pro"))
            hashMap.put("select", "^*,technicians.^*,job_appliances.^*,job_appliances.appliance_types.services.^*,job_appliances.appliance_types.^*,time_slots.^*,job_customer_addresses.^*");
        else
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.services.^*,job_appliances.appliance_types.^*,time_slots.^*,job_customer_addresses.^*");
        hashMap.put("month",cal_month.get(GregorianCalendar.MONTH) + 1 + "");
        hashMap.put("year", cal_month.get(GregorianCalendar.YEAR) + "");
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));
        return hashMap;
    }

    private void reScheduleJob(String index){
        GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerReschedule, CalendarActivity.this, "Loading");
        responseAsync.execute(getRequestParamsReschedule(index));

    }


    private void getTimeSlotes(){
        GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerTimeSlots, CalendarActivity.this, "Loading");
        responseAsync.execute(getRequestParamsTimeSlot());
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{

                    break;
                }
                case 1:{
                    showAlertDialog("FAILED", error_message,false);
                    break;
                }
                case 2:{
                    CalendarCollection.date_collection_arr.clear();
                    //CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(null);
                    SharedPreferences _prefs = Utilities.getSharedPreferences(CalendarActivity.this);
                    _prefs.edit().putString(Preferences.SCREEEN_NAME, Constants.NO_JOB).commit();
                    GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", eventListener1, CalendarActivity.this, "Loading");
                    responseAsync.execute(getEventsRequestParams());

                    break;
                }
                case 3:{
                    cal_adapter.notifyDataSetChanged();

                    break;
                }case 4:{
                    adapter = new CalanderEventsAdapter(CalendarActivity.this, list, getResources());
                    lstScheduledJobs.setAdapter(adapter);
                    break;
                }
                case 5:{
                    list = cal_adapter.getPositionList(selectedGridDate, CalendarActivity.this);
                    boolean hasEvent = false;
                    if (list.size() > 0)
                        hasEvent = true;

                    adapter = new CalanderEventsAdapter(CalendarActivity.this, list, getResources());
                    lstScheduledJobs.setAdapter(adapter);
                    cal_adapter.notifyDataSetChanged();
                    final boolean finalHasEvent = hasEvent;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cal_adapter.setSelected(viewRescheduled, posForRescheduled, finalHasEvent);
                        }
                    },1000);
                    break;
                }
            }
        }
    };
    ResponseListener responseListenerReschedule = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {

                    isJobRescheduled = true;
                    handler.sendEmptyMessage(2);
                }else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    ResponseListener responseListenerTimeSlots = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    JSONArray result = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    IDS = new String[result.length()];
                    TYPES = new String[result.length()];
                    for (int i = 0 ; i < result.length() ; i++){
                        JSONObject jsonObject = result.getJSONObject(i);
                        IDS[i] = jsonObject.getString("id");
                        TYPES[i] = jsonObject.getString("name");
//                        TYPES[i] = jsonObject.getString("start") + " - " + jsonObject.getString("end");
                    }
                    handler.sendEmptyMessage(4);
                }else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private HashMap<String,String> getRequestParamsReschedule(String index){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","reschedule");
        hashMap.put("object","jobs");
        hashMap.put("where[id]",JobId);
        hashMap.put("data[request_date]", selectedGridDate);
        hashMap.put("data[time_slot_id]", index+"");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));

        return hashMap;
    }
    private HashMap<String,String> getRequestParamsTimeSlot(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
        hashMap.put("object","time_slots");
        hashMap.put("per_page","20");
        hashMap.put("page", "1");
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));

        return hashMap;
    }
    private void showAlertDialog(String Title,String Message, final boolean success){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

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
                        if (success) {
                            Intent intent = new Intent(CalendarActivity.this, HomeScreenNew.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    ResponseListener eventListener1 = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    JSONObject jsonObject1 = Response.getJSONObject("RESPONSE");
                    Iterator iterator = jsonObject1.keys();
                    while (iterator.hasNext()){
                        CalenderScheduledJobModal modal = new CalenderScheduledJobModal();
                        String key = (String)iterator.next();
                        JSONObject inner = jsonObject1.getJSONObject(key);
                        modal.setDate(key);
                        modal.setDay(inner.getString("day"));
                        JSONArray results = inner.getJSONArray("jobs");

                        ArrayList<AvailableJobModal> list = new ArrayList<AvailableJobModal>();
                        for ( int i = 0 ; i < results.length() ; i++){

                            JSONObject obj = results.getJSONObject(i);
                            AvailableJobModal model = new AvailableJobModal();
                            model.setContact_name(obj.getString("contact_name"));
                            model.setCreated_at(obj.getString("created_at"));
                            model.setCustomer_id(obj.getString("customer_id"));
                            model.setCustomer_notes(obj.getString("customer_notes"));
                            model.setFinished_at(obj.getString("finished_at"));
                            model.setId(obj.getString("id"));

//                            model.setLatitude(obj.getDouble("latitude"));
//                            model.setLongitude(obj.getDouble("longitude"));
                            model.setPhone(obj.getString("phone"));
                            model.setPro_id(obj.getString("pro_id"));
                            model.setRequest_date(obj.getString("request_date"));
//                            model.setService_id(obj.getString("service_id"));
//                            model.setService_type(obj.getString("service_type"));
                            model.setStarted_at(obj.getString("started_at"));
                            model.setStatus(obj.getString("status"));
                            model.setTechnician_id(obj.getString("technician_id"));
//                            model.setTime_slot_id(obj.getString("time_slot_id"));
                            model.setTitle(obj.getString("title"));
//                            model.setTotal_cost(obj.getString("total_cost"));
                            model.setUpdated_at(obj.getString("updated_at"));
//                            model.setWarranty(obj.getString("warranty"));
//                        if(Utilities.getSharedPreferences(getContext()).getString(Preferences.ROLE, null).equals("pro")) {
                            JSONArray jobAppliances = obj.getJSONArray("job_appliances");
                            ArrayList<JobAppliancesModal>  jobapplianceslist = new ArrayList<JobAppliancesModal>();
                            if(jobAppliances != null){
                                for (int j = 0; j < jobAppliances.length(); j++) {
                                    JSONObject jsonObject = jobAppliances.getJSONObject(j);
                                    JobAppliancesModal mod = new JobAppliancesModal();
                                    mod.setJob_appliances_id(jsonObject.getString("id"));
                                    mod.setJob_appliances_job_id(jsonObject.getString("job_id"));
                                    mod.setJob_appliances_appliance_id(jsonObject.getString("appliance_id"));
                                    mod.setJob_appliances_brand_name(jsonObject.getString("brand_name"));

                                    JSONObject appliance_type_obj = jsonObject.getJSONObject("appliance_types");
                                    if (!jsonObject.isNull("description")){
                                        mod.setJob_appliances_appliance_description(jsonObject.getString("description"));
                                    }
                                    if (!jsonObject.isNull("service_type")){
                                        mod.setJob_appliances_service_type(jsonObject.getString("service_type"));
                                    }
                                    if (!jsonObject.isNull("customer_complaint")) {
                                        mod.setJob_appliances_customer_compalint(jsonObject.getString("customer_complaint"));
                                    }
                                    mod.setAppliance_type_id(appliance_type_obj.getString("id"));
                                    mod.setAppliance_type_has_power_source(appliance_type_obj.getString("has_power_source"));
                                    mod.setAppliance_type_service_id(appliance_type_obj.getString("service_id"));
                                    mod.setAppliance_type_name(appliance_type_obj.getString("name"));
                                    mod.setAppliance_type_soft_deleted(appliance_type_obj.getString("_soft_deleted"));
                                    if (!appliance_type_obj.isNull("image")){
                                        JSONObject image_obj = appliance_type_obj.getJSONObject("image");
                                        Log.e("", "-----" + image_obj.toString());
                                        if (!image_obj.isNull("original")) {
                                            mod.setImg_original(image_obj.getString("original"));
                                            mod.setImg_160x170(image_obj.getString("160x170"));
                                            mod.setImg_150x150(image_obj.getString("150x150"));
                                            mod.setImg_75x75(image_obj.getString("75x75"));
                                            mod.setImg_30x30(image_obj.getString("30x30"));
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
                                model.setJob_appliances_arrlist(jobapplianceslist);
                            }
                            if (!obj.isNull("technicians")){
                                JSONObject technician_object  =  obj.getJSONObject("technicians");
                                model.setTechnician_id(technician_object.getString("id"));
                                model.setTechnician_fname(technician_object.getString("first_name"));
                                model.setTechnician_lname(technician_object.getString("last_name"));
                                model.setTechnician_pickup_jobs(technician_object.getString("pickup_jobs"));
                                model.setTechnician_avg_rating(technician_object.getString("avg_rating"));
                                model.setTechnician_scheduled_job_count(technician_object.getString("scheduled_jobs_count"));
                                model.setTechnician_completed_job_count(technician_object.getString("completed_jobs_count"));
                                if (!technician_object.isNull("profile_image")){
                                    JSONObject object_profile_image  = technician_object.getJSONObject("profile_image");
                                    if (!object_profile_image.isNull("original"))
                                        model.setTechnician_profile_image(object_profile_image.getString("original"));
                                }

                            }
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            model.setTime_slot_id(time_slot_obj.getString("id"));
                            model.setTimeslot_start(time_slot_obj.getString("start"));
                            model.setTimeslot_end(time_slot_obj.getString("end"));
                            model.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));

                            if (!obj.isNull("job_customer_addresses")){
                                JSONObject job_customer_addresses_obj = obj.getJSONObject("job_customer_addresses");
                                model.setJob_customer_addresses_id(job_customer_addresses_obj.getString("id"));
                                model.setJob_customer_addresses_zip(job_customer_addresses_obj.getString("zip"));
                                model.setJob_customer_addresses_city(job_customer_addresses_obj.getString("city"));
                                model.setJob_customer_addresses_state(job_customer_addresses_obj.getString("state"));
                                model.setJob_customer_addresses_address(job_customer_addresses_obj.getString("address"));
                                model.setJob_customer_addresses_address_2(job_customer_addresses_obj.getString("address_2"));
                                model.setJob_customer_addresses_updated_at(job_customer_addresses_obj.getString("updated_at"));
                                model.setJob_customer_addresses_created_at(job_customer_addresses_obj.getString("created_at"));
                                model.setJob_customer_addresses_job_id(job_customer_addresses_obj.getString("job_id"));
                            }
                            list.add(model);
                        }
                        if (results.length() > 0){
//                            CalendarCollection.date_collection_arr.clear();
                            CalendarCollection.date_collection_arr.add(new CalendarCollection(key,key,list));
                        }
                        modal.setList(list);
                        calenderScheduledJobModalArrayList.add(modal);
                    }
                    handler.sendEmptyMessage(5);
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
