package fixdpro.com.fixdpro.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.AvailableJobListClickActivity;
import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.adapters.NotificationAdapter;
import fixdpro.com.fixdpro.adapters.swipe_undo.SwipeDismissList;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.beans.NotificationListModal;
import fixdpro.com.fixdpro.beans.NotificationModal;
import fixdpro.com.fixdpro.beans.NotificationTypeData;
import fixdpro.com.fixdpro.net.GetApiResponseAsync;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNoProgress;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.singleton.NotificationSingleton;
import fixdpro.com.fixdpro.utilites.CheckAlertDialog;
import fixdpro.com.fixdpro.utilites.CheckIfUserVarified;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.HandlePagingResponse;
import fixdpro.com.fixdpro.utilites.JSONParser;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Singleton;
import fixdpro.com.fixdpro.utilites.Utilities;

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
    public int page = 1 ;

    ArrayList<NotificationListModal> notificationlist  = null;

    String next = "null";

    NotificationAdapter adapterNotification = null ;

    NotificationSingleton singleton;

    ListView listview_Notifications;

    Singleton singletonJobs = null ;

    ArrayList<AvailableJobModal>  schedulejoblist;
    ArrayList<AvailableJobModal>  availablejoblist;

    AvailableJobModal jobModal = null ;
    NotificationModal modal;
    String role = "pro";
    private SwipeDismissList mSwipeList;
    /**
     * The key of the {@link Bundle} extra we store the mode of the list the
     * user selected.
     */
    private final static String EXTRA_MODE = "MODE";
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
        availablejoblist = singletonJobs.getAvailablejoblist();;

        _prefs = Utilities.getSharedPreferences(_context);
        role = _prefs.getString(Preferences.ROLE, "pro");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.NOTIFICATION_LIST_FRAGMENT);
        setupToolBar();
        adapterNotification.notifyDataSetChanged();
        if (!_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.IS_VARIFIED, "").equals("0")){
            new CheckIfUserVarified(getActivity());
        }

    }
    HandlePagingResponse pagingResponseNotification = new HandlePagingResponse() {
        @Override
        public void handleChangePage() {
            if (!next.equals("null")){
                page = Integer.parseInt(next);
                GetApiResponseAsync apiResponseAsync = new GetApiResponseAsync(Constants.BASE_URL,"POST",notificationResponseListener,getNotificationsParams,getActivity(),"Loading");
                apiResponseAsync.execute(getNotificationParams());
            }
        }
    };
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).hideRight();
        ((HomeScreenNew)getActivity()).setTitletext("Notifications");
        ((HomeScreenNew)getActivity()).setLeftToolBarImage(R.drawable.menu_icon);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        SwipeDismissList.UndoMode mode = SwipeDismissList.UndoMode.SINGLE_UNDO;
        /*****Setting Up Notifications******/
        listview_Notifications = (ListView)view.findViewById(R.id.listview_Notifications);

        View emptyView = view.findViewById(R.id.emptyView);
        listview_Notifications.setEmptyView(emptyView);

        adapterNotification = new NotificationAdapter(getActivity(), notificationlist,getActivity().getResources(), pagingResponseNotification);
//        ContextualUndoAdapter contextualUndoAdapter = new ContextualUndoAdapter(adapterNotification, R.layout.undo_row, R.id.undo_row_undobutton);
//        contextualUndoAdapter.setAbsListView(listview_Notifications);
        listview_Notifications.setAdapter(adapterNotification);
//        contextualUndoAdapter.setDeleteItemCallback(new MyDeleteItemCallback());
        adapterNotification.notifyDataSetChanged();
        // Create a new SwipeDismissList from the activities listview.
        mSwipeList = new SwipeDismissList(
                // 1st parameter is the ListView you want to use
                listview_Notifications,
                // 2nd parameter is an OnDismissCallback, that handles the deletion
                // and undo of list items. It only needs to implement onDismiss.
                // This method can return an Undoable (then this deletion can be undone)
                // or null (if the user shouldn't get the possibility to undo the
                // deletion).
                new SwipeDismissList.OnDismissCallback() {
                    /**
                     * Will be called, whenever the user swiped out an list item.
                     *
                     * @param listview_Notifications The {@link ListView} that the item was deleted
                     * from.
                     * @param position The position of the item, that was deleted.
                     * @return An {@link Undoable} or {@code null} if this deletion
                     * shouldn't be undoable.
                     */
                    public SwipeDismissList.Undoable onDismiss(AbsListView listview_Notifications, final int position) {

                        // Get item that should be deleted from the adapter.
                        final NotificationListModal item = adapterNotification.getItem(position);
                        // Delete that item from the adapter.
                        GetApiResponseAsyncNoProgress apiResponseAsync = new GetApiResponseAsyncNoProgress(Constants.BASE_URL, "POST", iHttpResponseListener, iHttpExceptionListener, getActivity(), "Loading");
                        apiResponseAsync.execute(getDeleteUndoParams("delete", notificationlist.get(position).getID()));
                        adapterNotification.remove(position);

                        // Return an Undoable, for that deletion. If you write return null
                        // instead, this deletion won't be undoable.
                        return new SwipeDismissList.Undoable() {
                            /**
                             * Optional method. If you implement this method, the
                             * returned String will be presented in the undo view to the
                             * user.
                             */
                            @Override
                            public String getTitle() {
                                return "  Notification" + " Deleted.    ";
                            }

                            /**
                             * Will be called when the user hits undo. You want to
                             * reinsert the item to the adapter again. The library will
                             * always call undo in the reverse order the item has been
                             * deleted. So you can insert the item at the position it
                             * was deleted from, unless you have modified the list
                             * (added or removed items) somewhere else in your activity.
                             * If you do so, you might want to call
                             * {@link SwipeDismissList#discardUndo()}, so the user
                             * cannot undo the action anymore. If you still want the
                             * user to be able to undo the deletion (after you modified
                             * the list somewhere else) you will need to calculate the
                             * new position of this item yourself.
                             */
                            @Override
                            public void undo() {
                                // Reinsert the item at its previous position.

                                adapterNotification.insert(item, position);
                                GetApiResponseAsyncNoProgress apiResponseAsync = new GetApiResponseAsyncNoProgress(Constants.BASE_URL, "POST", iHttpResponseListener, iHttpExceptionListener, getActivity(), "Loading");
                                apiResponseAsync.execute(getDeleteUndoParams("undo_delete", notificationlist.get(position).getID()));
                            }

                            /**
                             * Will be called, when the user doesn't have the
                             * possibility to undo the action anymore. This can either
                             * happen, because the undo timed out or
                             * {@link SwipeDismissList#discardUndo()} was called. If you
                             * have stored your objects somewhere persistent (e.g. a
                             * database) you might want to use this method to delete the
                             * object from this persistent storage.
                             */
                            @Override
                            public void discard() {
                                // Just write a log message (use logcat to see the effect)
                                Log.w("DISCARD", "item " + item + " now finally discarded");
                            }
                        };

                    }
                },
                // 3rd parameter needs to be the mode the list is generated.
                mode);

        // If we have a MULTI_UNDO list (several items can be undone one by one),
        // set the UndoMultipleString to null. If you set this to null the undo popup
        // will show the title of the item that will be undone next. If you don't
        // set this to null (leave it default, or set some other string), the string
        // will be shown (and first placeholder %d replaced with number of pending undos).
        if (mode == SwipeDismissList.UndoMode.MULTI_UNDO) {
            mSwipeList.setUndoMultipleString(null);
        }
        
        
        

//        SimpleSwipeUndoAdapter swipeUndoAdapter = new SimpleSwipeUndoAdapter(adapterNotification, getActivity(),
//                new OnDismissCallback() {
//                    @Override
//                    public void onDismiss(@NonNull final ViewGroup listview_Notifications, @NonNull final int[] reverseSortedPositions) {
//                        for (int position : reverseSortedPositions) {
//                            adapterNotification.remove(position);
//                        }
//                    }
//                }
//        );
//        swipeUndoAdapter.setAbsListView(listview_Notifications);
//        listview_Notifications.setAdapter(swipeUndoAdapter);
//        listview_Notifications.enableSwipeToDismiss(new OnDismissCallback() {
//            @Override
//            public void onDismiss(@NonNull ViewGroup viewGroup, @NonNull int[] ints) {
//                for (int position : ints) {
//                    adapterNotification.remove(position);
//                }
//            }
//        });

//        listview_Notifications.setHasMoreItems(false);
        notificationlist.clear();
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
                if (listModal.getIs_active().equals("0")){
                    return;
                }
                if (listModal.getIsRead().equals("0")){
                    setIsReadValue(position);
                }
                NotificationTypeData data = listModal.getData().get("t");
                modal = new NotificationModal();
                String key = data.getKey();
                String value = data.getValue();
                modal.setType(value);
                if (value.equals("wodfj")) {//work order declined
                    modal.setJobId(listModal.getData().get("j").getValue());
                    modal.setJobAppliance(listModal.getData().get("ja").getValue());
                    Bundle bundle = new Bundle();
                    bundle.putString("job_id", modal.getJobId());
                    bundle.putString("appliance_id", modal.getJobAppliance());
                    ((HomeScreenNew) getActivity()).switchFragment(new InstallorRepairFragment(), Constants.INSTALL_OR_REPAIR_FRAGMENT, true, bundle);
                } else if (value.equals("woafj")) {//work order approved
                    modal.setJobId(listModal.getData().get("j").getValue());
                    modal.setJobAppliance(listModal.getData().get("ja").getValue());
                    Bundle bundle = new Bundle();
                    bundle.putString("job_id", modal.getJobId());
                    bundle.putString("appliance_id", modal.getJobAppliance());
                    ((HomeScreenNew) getActivity()).switchFragment(new InstallorRepairFragment(), Constants.INSTALL_OR_REPAIR_FRAGMENT, true, bundle);
                } else if (value.equals("ja")) {//job avlbl
                    modal.setJobId(listModal.getData().get("j").getValue());
                    AvailableJobModal job_detail = getJobforIdAvalable(modal.getJobId());
                    if (job_detail != null) {
                        Intent i = new Intent(getActivity(), AvailableJobListClickActivity.class);
                        i.putExtra("JOB_DETAIL", job_detail);
                        startActivity(i);
                    } else {
                        //get the job
                        GetApiResponseAsyncNew responseAsync = new  GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerScheduled,exceptionListener, getActivity(), "Loading");
                        responseAsync.execute(getRequestParams(modal.getJobId(),"read_open"));
                    }
                } else if (value.equals("pjt") ||value.equals("cja") ) {//pickup job tech
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
                        responseAsync.execute(getRequestParams(modal.getJobId(),"read"));
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
                        responseAsync.execute(getRequestParams(modal.getJobId(),"read"));
                    }
                }
            }
        });
        return view;
    }


    private HashMap<String, String> getRequestParams(String id,String api) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", api);
        hashMap.put("object", "jobs");
        hashMap.put("expand[0]", "work_order");
        if (!role.equals("pro"))
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*,job_line_items.^*,customers.users.company_id");
        else
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*,job_line_items.^*,customers.users.company_id");
        hashMap.put("where[id]", id + "");
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("page", "1");
        hashMap.put("per_page", "20");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }
    private HashMap<String,String> getNotificationParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
        hashMap.put("object","alerts");
        hashMap.put("select","^*");
        hashMap.put("token",_prefs.getString(Preferences.AUTH_TOKEN,""));
//        hashMap.put("token","$2y$10$mh4vuAEGx/UZcm1FafydGeXR/sf.JPAbAEX00/BiooPY2gGpd7NqG");
        hashMap.put("order_by","created_at");
        hashMap.put("order","DESC");
        hashMap.put("page",page+"");
        hashMap.put("per_page",10+"");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
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
                                if (!jsonObject.isNull("power_source")){
                                    mod.setJob_appliances_power_source(jsonObject.getString("power_source"));
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
                            jobModal.setTimeslot_name(time_slot_obj.getString("name"));
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
                    if (results.length() > 0){
                        handler.sendEmptyMessage(2);
                    }else {
                        error_message = "Looks like the job isn't Available anymore";
                        handler.sendEmptyMessage(1);
                    }

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
                        modal.setIs_active(jsonObject.getString("is_active"));
                        modal.setCreateAt(jsonObject.getString("created_at"));
                        modal.setUpdatedAt(jsonObject.getString("updated_at"));
                        if (!jsonObject.isNull("icon_and_title")) {
                            JSONObject icontitleObject = jsonObject.getJSONObject("icon_and_title");
                            modal.setServiceType(icontitleObject.getString("service_type"));
                            modal.setServiceName(icontitleObject.getString("name"));
                            if (!icontitleObject.isNull("icon")) {
                                JSONObject iconObject = icontitleObject.getJSONObject("icon");
                                if (!iconObject.isNull("original"))
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
                case 0: {
                    Log.e("notificationlist", "notificationlist---------"+notificationlist.size());
                    adapterNotification.notifyDataSetChanged();
                    break;
                }
                case 1:{
                    checkALert.showcheckAlert(getActivity(), getActivity().getResources().getString(R.string.alert_title), error_message);
                    break;
                }
                case 2:{

                        if (jobModal != null){
                            if (modal.getType().equals("ja")){
                                Intent i = new Intent(getActivity(), AvailableJobListClickActivity.class);
                                i.putExtra("JOB_DETAIL", jobModal);
                                startActivity(i);
                            }else {
                                ScheduledListDetailsFragment fragment = new ScheduledListDetailsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("modal", jobModal);
                                ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.SCHEDULED_LIST_DETAILS_FRAGMENT, true, bundle);
                            }

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
    public AvailableJobModal getJobforIdAvalable(String id){
        AvailableJobModal jobModal = null ;
        for (int i = 0 ; i < availablejoblist.size() ; i++ ){
            if (availablejoblist.get(i).getId().equals(id)){
                jobModal = availablejoblist.get(i);
                break;
            }
        }
        return jobModal;
    }

    private void setIsReadValue(final int position){
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapterNotification.notifyDataSetChanged();
            }

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(Constants.BASE_URL, "POST", getReadRequestParams(position));
                if (jsonObject != null) {
                    try {
                        String STATUS = jsonObject.getString("STATUS");
                        if (STATUS.equals("SUCCESS")) {
//                            JSONObject RESPONSE = jsonObject.getJSONObject("RESPONSE");
                            notificationlist.get(position).setIsRead("1");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return null;
            }
        }.execute();
    }

    private HashMap<String,String> getReadRequestParams(int position){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","update");
        hashMap.put("object","alerts");
        hashMap.put("token","alerts");
        hashMap.put("data[is_read]","1");
        hashMap.put("where[id]",notificationlist.get(position).getID());
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }

    private HashMap<String, String> getDeleteUndoParams(String  api,String id) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", api);
        hashMap.put("object", "alerts");
        hashMap.put("data[id]", id);
        hashMap.put("token",_prefs.getString(Preferences.AUTH_TOKEN,""));
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");

        return hashMap;
    }
    IHttpResponseListener iHttpResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {
            Log.e("","");
        }
    };
    IHttpExceptionListener iHttpExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {

        }
    };
}
