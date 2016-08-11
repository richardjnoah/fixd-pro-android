package fixtpro.com.fixtpro.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.paging.listview.PagingListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixtpro.com.fixtpro.HomeScreenNew;
import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.adapters.NotificationListAdapter;
import fixtpro.com.fixtpro.beans.AvailableJobModal;
import fixtpro.com.fixtpro.beans.JobAppliancesModal;
import fixtpro.com.fixtpro.beans.NotificationListModal;
import fixtpro.com.fixtpro.beans.NotificationModal;
import fixtpro.com.fixtpro.beans.NotificationTypeData;
import fixtpro.com.fixtpro.net.GetApiResponseAsync;
import fixtpro.com.fixtpro.net.GetApiResponseAsyncNew;
import fixtpro.com.fixtpro.net.IHttpExceptionListener;
import fixtpro.com.fixtpro.net.IHttpResponseListener;
import fixtpro.com.fixtpro.singleton.NotificationSingleton;
import fixtpro.com.fixtpro.utilites.CheckAlertDialog;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.CurrentScheduledJobSingleTon;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Singleton;
import fixtpro.com.fixtpro.utilites.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    CheckAlertDialog checkALert;
    String error_message = "";
    SharedPreferences _prefs = null ;
    Context _context = null ;
    public static int page = 1 ;

    ArrayList<NotificationListModal> notificationlist  = null;

    String next = "null";

    NotificationListAdapter adapterNotification = null ;

    NotificationSingleton singleton;

    PagingListView listview_Notifications;

    Singleton singletonJobs = null ;

    ArrayList<AvailableJobModal>  schedulejoblist;

    AvailableJobModal model = null ;
    String role = "pro";
    public NotificationListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationListFragment newInstance(String param1, String param2) {
        NotificationListFragment fragment = new NotificationListFragment();
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
        singleton = NotificationSingleton.getInstance();
        checkALert = new CheckAlertDialog();
        this.next =  singleton.next;

        notificationlist = singleton.getlist();
        singletonJobs = Singleton.getInstance();
        schedulejoblist = singletonJobs.getSchedulejoblist();;

        _prefs = Utilities.getSharedPreferences(_context);
        role = _prefs.getString(Preferences.ROLE, "pro");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.NOTIFICATION_LIST_FRAGMENT);
        setupToolBar();

    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).setRightToolBarImage(R.drawable.refresh);
        ((HomeScreenNew)getActivity()).setTitletext("Notifications");
        ((HomeScreenNew)getActivity()).setLeftToolBarText("Back");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        /*****Setting Up Notifications******/
        listview_Notifications = (PagingListView)view.findViewById(R.id.listview_Notifications);
        if (notificationlist.size() > 0){
            handler.sendEmptyMessage(0);
        }else {
            GetApiResponseAsync apiResponseAsync = new GetApiResponseAsync(Constants.BASE_URL,"POST",notificationResponseListener,getNotificationsParams,getActivity(),"Loading");
            apiResponseAsync.execute(getNotificationParams());
        }
        listview_Notifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("", "");
                NotificationListModal listModal = notificationlist.get(position);
                NotificationTypeData data = listModal.getData().get("t");
                NotificationModal modal = new NotificationModal();
                String key = data.getKey();
                String value = data.getValue();
                if (value.equals("wod")) {//work order declined
                    modal.setJobId(listModal.getData().get("j").getValue());
                    modal.setJobAppliance(listModal.getData().get("ja").getValue());
                } else if (value.equals("woa")) {//work order approved
                    modal.setJobId(listModal.getData().get("j").getValue());
                    modal.setJobAppliance(listModal.getData().get("ja").getValue());
                } else if (value.equals("ja")) {//job avlbl
                    modal.setJobId(listModal.getData().get("j").getValue());
                    modal.setJobAppliance(listModal.getData().get("ja").getValue());
                } else if (value.equals("pjt")) {//pickup job tech
                    modal.setJobId(listModal.getData().get("j").getValue());
                    AvailableJobModal job_detail = getJobforId(modal.getJobId());
                    if (job_detail != null) {
                        ScheduledListDetailsFragment fragment = new ScheduledListDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("modal", job_detail);
                        ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
                    } else {
                        //get the job
                        GetApiResponseAsyncNew responseAsync = new  GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerScheduled,exceptionListener, getActivity(), "Loading");
                        responseAsync.execute(getRequestParams(modal.getJobId()));
                    }
                } else if (value.equals("pjp")) {//pickup job pro
                    modal.setJobId(listModal.getData().get("j").getValue());
                    AvailableJobModal job_detail = getJobforId(modal.getJobId());
                    if (job_detail != null) {
                        ScheduledListDetailsFragment fragment = new ScheduledListDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("modal", job_detail);
                        ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
                    } else {
                        //get the job
                        GetApiResponseAsyncNew responseAsync = new  GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerScheduled,exceptionListener, getActivity(), "Loading");
                        responseAsync.execute(getRequestParams(modal.getJobId()));
                    }
                }
            }
        });
        return view;
    }


    private HashMap<String, String> getRequestParams(String id) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "jobs");
        hashMap.put("expand[0]", "work_order");
        if (!role.equals("pro"))
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.equipment_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*");
        else
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.equipment_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*");
        hashMap.put("where[id]", id + "");
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("page", "1");
        hashMap.put("per_page", "20");
        return hashMap;
    }
    private HashMap<String,String> getNotificationParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
        hashMap.put("object","alerts");
        hashMap.put("select","^*");
//        hashMap.put("token",_prefs.getString(Preferences.AUTH_TOKEN,""));
        hashMap.put("token","$2y$10$mh4vuAEGx/UZcm1FafydGeXR/sf.JPAbAEX00/BiooPY2gGpd7NqG");
        hashMap.put("order_by","created_at");
        hashMap.put("order","DESC");
        hashMap.put("pageNo",page+"");
        hashMap.put("per_page",30+"");
        return hashMap;
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
                         model = new AvailableJobModal();
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
                        model.setWarranty(obj.getString("warranty"));
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
                                        if( !image_obj.isNull("original")){
                                            mod.setAppliance_type_image_original(image_obj.getString("original"));

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
                        if (!obj.isNull("time_slots")){
                            JSONObject time_slot_obj = obj.getJSONObject("time_slots");
                            model.setTime_slot_id(time_slot_obj.getString("id"));
                            model.setTimeslot_start(time_slot_obj.getString("start"));
                            model.setTimeslot_end(time_slot_obj.getString("end"));
                            model.setTimeslot_soft_deleted(time_slot_obj.getString("_soft_deleted"));
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

                    }

                    handler.sendEmptyMessage(2);
                }else {
                    handler.sendEmptyMessage(3);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception;
            handler.sendEmptyMessage(1);
        }
    };
    IHttpResponseListener notificationResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "response" + Response);
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
                    next = pagination.getString("next");
                    for(int i = 0; i < results.length(); i++){
                        JSONObject jsonObject = results.getJSONObject(i);
                        NotificationListModal modal = new NotificationListModal();
                        modal.setID(jsonObject.getString("id"));
                        modal.setUserID(jsonObject.getString("user_id"));
                        modal.setIsRead(jsonObject.getString("is_read"));
                        modal.setText(jsonObject.getString("text"));
                        modal.setJobID(jsonObject.getString("job_id"));
                        modal.setCreateAt(jsonObject.getString("created_at"));
                        modal.setUpdatedAt(jsonObject.getString("updated_at"));
                        if (!jsonObject.isNull("icon_and_title")) {
                            JSONObject icontitleObject = jsonObject.getJSONObject("icon_and_title");
                            modal.setServiceType(icontitleObject.getString("service_type"));
                            modal.setServiceName(icontitleObject.getString("name"));
                            if (!icontitleObject.isNull("icon")) {
                                JSONObject iconObject = icontitleObject.getJSONObject("icon");
                                modal.setIconImage(iconObject.getString("original"));
                            }
                        }
                        HashMap<String,NotificationTypeData> dataHashMap = new HashMap<String,NotificationTypeData>();
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int j = 0 ; j < data.length() ; j++){
                            NotificationTypeData notificationTypeData = new NotificationTypeData();
                            JSONObject object = data.getJSONObject(j);
                            String key = object.getString("key");
                            String value = object.getString("value");
                            notificationTypeData.setKey(key);
                            notificationTypeData.setValue(value);
                            dataHashMap.put(key,notificationTypeData);
                        }
                        modal.setData(dataHashMap);
                        notificationlist.add(modal);

                    }

                    handler.sendEmptyMessage(0);
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

    IHttpExceptionListener getNotificationsParams = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            Log.e("","response"+exception);
            error_message = exception;
            handler.sendEmptyMessage(1);
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    Log.e("notificationlist","notificationlist---------"+notificationlist.size());
                    adapterNotification = new NotificationListAdapter(getActivity(),notificationlist,getResources());
                    listview_Notifications.setAdapter(adapterNotification);
                    listview_Notifications.setHasMoreItems(false);
                    break;
                }
                case 1:{
                    checkALert.showcheckAlert(getActivity(), getActivity().getResources().getString(R.string.alert_title), error_message);
                    break;
                }
                case 2:{
                        if (model != null){
                            ScheduledListDetailsFragment fragment = new ScheduledListDetailsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("modal", model);
                            ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
                        }
                    break;
                }
            }
        }
    };
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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
    public AvailableJobModal getJobforId(String id){
        AvailableJobModal jobModal = null ;
        for (int i = 0 ; i < schedulejoblist.size() ; i++ ){
            if (schedulejoblist.get(i).getId().equals(id)){
                jobModal = schedulejoblist.get(i);
                break;
            }
        }
        return jobModal;
    }
}
