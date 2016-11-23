package fixtpro.com.fixtpro.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.paging.listview.PagingListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import fixtpro.com.fixtpro.HomeScreenNew;
import fixtpro.com.fixtpro.JobCompletedActivity;
import fixdpro.com.fixdpro.R;
import fixtpro.com.fixtpro.ResponseListener;
import fixtpro.com.fixtpro.adapters.JobsPagingAdapter;
import fixtpro.com.fixtpro.beans.AvailableJobModal;
import fixtpro.com.fixtpro.beans.JobAppliancesModal;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JobSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobSearchFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RelativeLayout schedulelayout;
    LinearLayout availschedule_layout;
    private OnFragmentInteractionListener mListener;
    TextView completed, scheduled;
    EditText editSearch;
    private boolean isCompletdShowing = true;
    String role = "pro";
    SharedPreferences _prefs = null;
    Context _context = null;
    String order_by = " finished_at";
    ListView completeJob_list_view, scheduleJob_list_view;
    ArrayList<AvailableJobModal> completedjoblist = new ArrayList<>();
    ArrayList<AvailableJobModal> schedulejoblist = new ArrayList<>();
    JobsPagingAdapter adapterCompleted, adapterSchedule;
    public static int pageSheduled = 1;
    public static int pagecomplted = 1;
    String nextScheduled = "null";
    String nextComplete = "null";
    String error_message = "";

    boolean isCompleted = true;
    boolean isScheduled = false;
    //ArrayList thats going to hold the search results
    ArrayList<AvailableJobModal> searchResults;

    public JobSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobSearchFragment newInstance(String param1, String param2) {
        JobSearchFragment fragment = new JobSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        _context = getActivity();
        _prefs = Utilities.getSharedPreferences(_context);
        role = _prefs.getString(Preferences.ROLE, "pro");
    }

    @Override
    public void onResume() {
        ((HomeScreenNew) getActivity()).setCurrentFragmentTag(Constants.JOB_SEARCH_FRAGMENT);
        setupToolBar();
        super.onResume();
    }

    private void setupToolBar() {
        ((HomeScreenNew) getActivity()).hideRight();
        ((HomeScreenNew) getActivity()).setTitletext("Search Job");
        ((HomeScreenNew) getActivity()).setLeftToolBarText("Cancel");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_search, container, false);

        setWidgets(view);
        setListeners();
        adapterCompleted = new JobsPagingAdapter(getActivity(), completedjoblist, getResources(),null,"Completed");
        completeJob_list_view.setAdapter(adapterCompleted);

        adapterSchedule = new JobsPagingAdapter(getActivity(), schedulejoblist, getResources(),null,"Scheduled");
        scheduleJob_list_view.setAdapter(adapterSchedule);

        completeJob_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(_context,JobCompletedActivity.class);
                i.putExtra("CompletedJobObject",completedjoblist.get(position));
                startActivity(i);
            }
        });
        scheduleJob_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AvailableJobModal job_detail = new AvailableJobModal();
                job_detail = schedulejoblist.get(position);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("SCHEDULED_JOB_DETAIL",job_detail);
                ScheduledListDetailsFragment fragment = new ScheduledListDetailsFragment();
//                fragment.setArguments(bundle);
                Bundle bundle = new Bundle();
                bundle.putSerializable("modal",job_detail);
//                CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(job_detail);
                ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
            }
        });
        /************Set Search Method*********/
//        setSearchForListView();
        setSearchOnKeyPadSerach();
        return view;
    }

    private void setSearchOnKeyPadSerach() {
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (isCompleted) {
                        GetApiResponseAsync responseAsyncCompleted = new GetApiResponseAsync("POST", responseListenerCompleted, getActivity(), "Loading");
                        responseAsyncCompleted.execute(getRequestParams("Complete"));
                    } else {
                        GetApiResponseAsync responseAsyncScheduled = new GetApiResponseAsync("POST", responseListenerScheduled, getActivity(), "Loading");
                        responseAsyncScheduled.execute(getRequestParams("Scheduled"));
                    }
                    return true;
                }
                return false;
            }
        });
    }


    private void setWidgets(View rootView) {
        editSearch = (EditText) rootView.findViewById(R.id.editSearch);
        schedulelayout = (RelativeLayout) rootView.findViewById(R.id.schedulelayout);
        availschedule_layout = (LinearLayout) rootView.findViewById(R.id.availschedule_layout);
        completed = (TextView) rootView.findViewById(R.id.completed);
        scheduled = (TextView) rootView.findViewById(R.id.scheduled);
        completeJob_list_view = (ListView) rootView.findViewById(R.id.completeJob_list_view);
        scheduleJob_list_view = (ListView) rootView.findViewById(R.id.scheduleJob_list_view);

    }

    void setListeners() {
        completed.setOnClickListener(this);
        scheduled.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.completed:
//                GetApiResponseAsync responseAsyncCompleted = new GetApiResponseAsync("POST", responseListenerCompleted, getActivity(), "Loading");
//                responseAsyncCompleted.execute(getRequestParams("Complete"));
                availschedule_layout.setBackgroundResource(R.drawable.search_completed);
                completeJob_list_view.setVisibility(View.VISIBLE);
                scheduleJob_list_view.setVisibility(View.GONE);
                isCompleted = true;
                isScheduled = false;
                break;
            case R.id.scheduled:
//                GetApiResponseAsync responseAsyncScheduled = new GetApiResponseAsync("POST", responseListenerScheduled, getActivity(), "Loading");
//                responseAsyncScheduled.execute(getRequestParams("Scheduled"));
                availschedule_layout.setBackgroundResource(R.drawable.search_scheduled);
                scheduleJob_list_view.setVisibility(View.VISIBLE);
                completeJob_list_view.setVisibility(View.GONE);
                isCompleted = false;
                isScheduled = true;
                break;
        }
    }

    ResponseListener responseListenerCompleted = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
                    nextComplete = pagination.getString("next");
                    completedjoblist.clear();
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject obj = results.getJSONObject(i);
                        AvailableJobModal model = new AvailableJobModal();
                        model.setContact_name(obj.getString("contact_name"));
                        model.setCreated_at(obj.getString("created_at"));
                        model.setCustomer_id(obj.getString("customer_id"));
                        model.setCustomer_notes(obj.getString("customer_notes"));
                        model.setFinished_at(obj.getString("finished_at"));
                        model.setId(obj.getString("id"));
                        model.setJob_id(obj.getString("job_id"));
//                        model.setLatitude(obj.getDouble("latitude"));
                        model.setLocked_by(obj.getString("locked_by"));
                        model.setLocked_on(obj.getString("locked_on"));
//                        model.setLongitude(obj.getDouble("longitude"));
                        model.setPhone(obj.getString("phone"));
                        model.setPro_id(obj.getString("pro_id"));
                        model.setRequest_date(obj.getString("request_date"));
//                        model.setService_id(obj.getString("service_id"));
//                        model.setService_type(obj.getString("service_type"));
                        model.setStarted_at(obj.getString("started_at"));
                        model.setStatus(obj.getString("status"));
                        model.setTechnician_id(obj.getString("technician_id"));
                        model.setTime_slot_id(obj.getString("time_slot_id"));
                        model.setTitle(obj.getString("title"));
//                        model.setTotal_cost(obj.getString("total_cost"));
                        model.setUpdated_at(obj.getString("updated_at"));
//                        model.setWarranty(obj.getString("warranty"));
//                        if(Utilities.getSharedPreferences(getContext()).getString(Preferences.ROLE, null).equals("pro")) {
                        JSONArray jobAppliances = obj.getJSONArray("job_appliances");
                        ArrayList<JobAppliancesModal> jobapplianceslist = new ArrayList<JobAppliancesModal>();
                        if (jobAppliances != null) {
                            for (int j = 0; j < jobAppliances.length(); j++) {
                                JSONObject jsonObject = jobAppliances.getJSONObject(j);
                                JobAppliancesModal mod = new JobAppliancesModal();
                                mod.setJob_appliances_job_id(jsonObject.getString("job_id"));
                                mod.setJob_appliances_appliance_id(jsonObject.getString("appliance_id"));
                                if (!jsonObject.isNull("appliance_types")){
                                    JSONObject appliance_type_obj = jsonObject.getJSONObject("appliance_types");
                                    mod.setAppliance_type_id(appliance_type_obj.getString("id"));
                                    mod.setAppliance_type_has_power_source(appliance_type_obj.getString("has_power_source"));
                                    mod.setAppliance_type_service_id(appliance_type_obj.getString("service_id"));
                                    mod.setAppliance_type_name(appliance_type_obj.getString("name"));
                                    mod.setAppliance_type_soft_deleted(appliance_type_obj.getString("_soft_deleted"));
                                    if (!jsonObject.isNull("image")) {
                                        JSONObject image_obj = appliance_type_obj.getJSONObject("image");
                                        if (!image_obj.isNull("original")) {
                                            mod.setAppliance_type_image_original(image_obj.getString("original"));
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
                            model.setTechnician_technicians_id(technician_object.getString("id"));
                            model.setTechnician_user_id(technician_object.getString("user_id"));
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

                        if(!obj.isNull("time_slots")){
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            model.setTime_slot_id(time_slot_obj.getString("id"));
                            model.setTimeslot_start(time_slot_obj.getString("start"));
                            model.setTimeslot_end(time_slot_obj.getString("end"));
                            model.setTimeslot_name(time_slot_obj.getString("name"));
                            model.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));
                        }

                        if (!obj.isNull("job_repair")) {
                            JSONObject job_repair_obj = obj.getJSONObject("job_repair");
                            if (!job_repair_obj.isNull("repair_types")) {
                                JSONObject job_repair_repair_type_obj = job_repair_obj.getJSONObject("repair_types");
                                model.setJob_repair_job_types_cost(job_repair_repair_type_obj.getString("cost"));
                                model.setJob_repair_job_types_name(job_repair_repair_type_obj.getString("name"));
                            }

                        }
                        if (!obj.isNull("cost_details")) {
                            JSONObject cost_details_obj = obj.getJSONObject("cost_details");
                            if (!cost_details_obj.isNull("repair")) {
                                JSONObject cost_details_repair = cost_details_obj.getJSONObject("repair");
                                Iterator<?> keys = cost_details_repair.keys();
                                if (keys.hasNext()) {
                                    String key = (String) keys.next();
                                    String value = cost_details_repair.getString(key);
                                    model.setCost_details_repair_type(key);
                                    model.setCost_details_repair_value(value);
                                }
                            }

                            model.setCost_details_tripcharges(cost_details_obj.getString("trip_charges"));
                            model.setCost_details_tax(cost_details_obj.getString("tax"));

                            model.setCost_details_fixd_fee_percentage(cost_details_obj.getString("fixd_fee_percentage"));
                            model.setCost_details_fixd_fee(cost_details_obj.getString("fixd_fee"));
                            model.setCost_details_pro_earned(cost_details_obj.getString("pro_earned"));
                            model.setCost_details_customer_payment(cost_details_obj.getString("customer_payment"));
                        }
                        if (!obj.isNull("job_customer_addresses")) {
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
                        if(!obj.isNull("job_line_items")){
                            JSONObject job_line_items_obj = obj.getJSONObject("job_line_items");
                            model.setJob_line_items_tax(job_line_items_obj.getString("tax"));
                            model.setJob_line_items_fixd_cut(job_line_items_obj.getString("fixd_cut"));
                            model.setJob_line_items_govt_cut(job_line_items_obj.getString("govt_cut"));
                            model.setJob_line_items_pro_cut(job_line_items_obj.getString("pro_cut"));
                            model.setJob_line_items_sub_total(job_line_items_obj.getString("sub_total"));
                            model.setJob_line_items_total(job_line_items_obj.getString("total"));
                        }
                        completedjoblist.add(model);
                        Collections.reverse(completedjoblist);
                    }

                    handler.sendEmptyMessage(4);
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

    private void showAlertDialog(String Title, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                _context);

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

    ResponseListener responseListenerScheduled = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
                    nextScheduled = pagination.getString("next");
                    schedulejoblist.clear();
                    for(int i = 0; i < results.length(); i++) {
                        JSONObject obj = results.getJSONObject(i);
                        AvailableJobModal model = new AvailableJobModal();
                        model.setContact_name(obj.getString("contact_name"));
                        model.setCreated_at(obj.getString("created_at"));
                        model.setCustomer_id(obj.getString("customer_id"));
                        model.setCustomer_notes(obj.getString("customer_notes"));
                        model.setFinished_at(obj.getString("finished_at"));
                        model.setId(obj.getString("id"));
                        model.setJob_id(obj.getString("job_id"));
//                        model.setLatitude(obj.getDouble("latitude"));
                        model.setLocked_by(obj.getString("locked_by"));
                        model.setLocked_on(obj.getString("locked_on"));
//                        model.setLongitude(obj.getDouble("longitude"));
                        model.setPhone(obj.getString("phone"));
                        model.setPro_id(obj.getString("pro_id"));
                        model.setRequest_date(obj.getString("request_date"));
//                        model.setService_id(obj.getString("service_id"));
//                        model.setService_type(obj.getString("service_type"));
                        model.setStarted_at(obj.getString("started_at"));
                        model.setStatus(obj.getString("status"));
                        model.setTechnician_id(obj.getString("technician_id"));
//                        model.setTime_slot_id(obj.getString("time_slot_id"));
                        model.setTitle(obj.getString("title"));
//                        model.setTotal_cost(obj.getString("total_cost"));
                        model.setUpdated_at(obj.getString("updated_at"));
//                        model.setWarranty(obj.getString("warranty"));
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
                                if (!jsonObject.isNull("description")){
                                    mod.setJob_appliances_appliance_description(jsonObject.getString("description"));
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
                                        if(!image_obj.isNull("original")){
                                            mod.setAppliance_type_image_original(image_obj.getString("original"));

                                        }
                                    }
                                }
//

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
                            model.setTechnician_technicians_id(technician_object.getString("id"));
                            model.setTechnician_user_id(technician_object.getString("user_id"));
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
                        if (!obj.isNull("time_slots")){
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            model.setTime_slot_id(time_slot_obj.getString("id"));
                            model.setTimeslot_start(time_slot_obj.getString("start"));
                            model.setTimeslot_name(time_slot_obj.getString("name"));
                            model.setTimeslot_end(time_slot_obj.getString("end"));
                            model.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));
                        }
                        if (!obj.isNull("cost_details")){
                            JSONObject cost_details_obj = obj.getJSONObject("cost_details");
                            if (!cost_details_obj.isNull("repair")){
                                JSONObject cost_details_repair  = cost_details_obj.getJSONObject("repair");
                                Iterator<?> keys = cost_details_repair.keys();
                                if (keys.hasNext()){
                                    String key = (String)keys.next();
                                    String value = cost_details_repair.getString(key);
                                    model.setCost_details_repair_type(key);
                                    model.setCost_details_repair_value(value);
                                }
                            }

                            model.setCost_details_tripcharges(cost_details_obj.getString("trip_charges"));
                            model.setCost_details_tax(cost_details_obj.getString("tax"));

                            model.setCost_details_fixd_fee_percentage(cost_details_obj.getString("fixd_fee_percentage"));
                            model.setCost_details_fixd_fee(cost_details_obj.getString("fixd_fee"));
                            model.setCost_details_pro_earned(cost_details_obj.getString("pro_earned"));
                            model.setCost_details_customer_payment(cost_details_obj.getString("customer_payment"));
                        }

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
                        schedulejoblist.add(model);
                    }

                    Log.e("schedulejoblist", "schedulejoblist.---------------" + schedulejoblist.size());

                    handler.sendEmptyMessage(2);
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
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 4: {
                    adapterCompleted.notifyDataSetChanged();
//                    adapterCompleted = new JobsPagingAdapter(getActivity(), completedjoblist, getResources());
//                    completeJob_list_view.setAdapter(adapterCompleted);

//                    completeJob_list_view.onFinishLoading(true, completedjoblist);
//                    if (!nextComplete.equals("null")) {
//                        completeJob_list_view.setHasMoreItems(true);
//                    } else {
//                        completeJob_list_view.setHasMoreItems(false);
//                    }
//
//                    completeJob_list_view.setPagingableListener(new PagingListView.Pagingable() {
//                        @Override
//                        public void onLoadMoreItems() {
//                            if (!nextComplete.equals("null")) {
//                                pagecomplted = Integer.parseInt(nextComplete);
//                                GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerCompleted, getActivity(), "Loading");
//                                responseAsync.execute(getRequestParams("Complete"));
//                            } else {
//                                completeJob_list_view.onFinishLoading(false, null);
//                            }
//                        }
//                    });
                    break;
                }

                case 1: {
                    showAlertDialog("Fixd-Pro", error_message);
                    break;
                }
                case 2: {
                    adapterSchedule.notifyDataSetChanged();
//                    adapterSchedule = new JobsPagingAdapter(getActivity(), schedulejoblist, getResources());
//                    scheduleJob_list_view.setAdapter(adapterSchedule);
//                    scheduleJob_list_view.onFinishLoading(true, schedulejoblist);
//                    if (!nextScheduled.equals("null")) {
//                        scheduleJob_list_view.setHasMoreItems(true);
//                    } else {
//                        scheduleJob_list_view.setHasMoreItems(false);
//                    }
//                    scheduleJob_list_view.setPagingableListener(new PagingListView.Pagingable() {
//                        @Override
//                        public void onLoadMoreItems() {
//                            if (!nextScheduled.equals("null")) {
//                                pageSheduled = Integer.parseInt(nextScheduled);
//                                GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerScheduled, getActivity(), "Loading");
//                                responseAsync.execute(getRequestParams("Scheduled"));
//                            } else {
//                                scheduleJob_list_view.onFinishLoading(false, null);
//                            }
//                        }
//                    });
                    break;
                }
                case 3: {

                    break;
                }
            }
        }
    };


    private HashMap<String, String> getRequestParams(String Type) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        if (!Type.equals("Scheduled"))
            hashMap.put("order_by", "finished_at");
        else
            hashMap.put("order_by", "request_date");
        hashMap.put("object", "jobs");
        if (!role.equals("pro"))
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.services.^*,job_appliances.appliance_types.^*,time_slots.^*,job_customer_addresses.^*,job_line_items.^*");
        else
            hashMap.put("select", "^*,job_appliances.^*,technicians.^*,job_appliances.appliance_types.services.^*,job_appliances.appliance_types.^*,time_slots.^*,job_customer_addresses.^*,job_line_items.^*");
        hashMap.put("order", "DESC");
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("page", "1");
        hashMap.put("per_page", "20");
        hashMap.put("where[status]", Type);
        hashMap.put("search", editSearch.getText().toString());
        return hashMap;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
