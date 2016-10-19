package fixdpro.com.fixdpro.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.ResponseListener;
import fixdpro.com.fixdpro.adapters.InstallOrRepairJobAdapter;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.beans.install_repair_beans.InstallOrRepairModal;
import fixdpro.com.fixdpro.beans.install_repair_beans.Parts;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNoProgress;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.GPSTracker;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.JSONParser;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InstallorRepairFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InstallorRepairFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InstallorRepairFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ListView lstInstallRepair;
    ImageView img_finishjob;
    String role = "pro";
    Context _context;
    SharedPreferences _prefs = null;
    String error_message = "";
    Fragment fragment = null ;
    LinearLayout layout_finish_job ;
    Dialog dialog = null ;
    float Jobtotal = 0;
    TextView txtJobTotal ;
    String total_cost ="0";
    boolean isAutoNotiForWorkOrder = false ;
    InstallOrRepairJobAdapter adapter = null ;
    String job_id = "";
    String appliance_id = "";
    ArrayList<Location> locations_list = new ArrayList<Location>();
    Location prevoiusLOcation = null ;
    GPSTracker gpsTracker = null ;
    long start_time = 0;
    boolean start_going_to_get_parts_process = false;
    String total ="0",pro_cut="0",job_mileage="0",fixd_cut="0";

    public InstallorRepairFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InstallorRepairFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InstallorRepairFragment newInstance(String param1, String param2) {
        InstallorRepairFragment fragment = new InstallorRepairFragment();
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
            isAutoNotiForWorkOrder = true ;
            AvailableJobModal availableJobModal = new AvailableJobModal();

            appliance_id = getArguments().getString("appliance_id");
            job_id = getArguments().getString("job_id");
            availableJobModal.setId(job_id);
            CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(availableJobModal);
        }
        _context = getActivity();
        gpsTracker = new GPSTracker(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_installor_repair, container, false);
        _prefs = Utilities.getSharedPreferences(_context);
        role = _prefs.getString(Preferences.ROLE, "pro");
        setWidgets(view);
        setListeners();
//        if (CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal() != null){
//            handler.sendEmptyMessage(0);
//        }else{
            GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerScheduled, getActivity(), "Loading");
            responseAsync.execute(getRequestParams());

//        }
        return view;
    }

    private void setWidgets(View view) {
        lstInstallRepair = (ListView) view.findViewById(R.id.lstInstallRepair);
        img_finishjob = (ImageView) view.findViewById(R.id.img_finishjob);
        layout_finish_job = (LinearLayout)view.findViewById(R.id.layout_finish_job);
    }

    private void setListeners() {
        lstInstallRepair.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragment = new WhatsWrongFragment();
                CurrentScheduledJobSingleTon.getInstance().setSelectedJobApplianceModal(CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getJob_appliances_arrlist().get(position - 1));

                ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.WHATS_WRONG_FRAGMENT, true, null);
            }
        });
        img_finishjob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();

            }
        });
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
    public void onResume() {
        super.onResume();
        ((HomeScreenNew) getActivity()).setCurrentFragmentTag(Constants.INSTALL_OR_REPAIR_FRAGMENT);
        setupToolBar();
//        start_going_to_get_parts_process = CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().isStart_going_to_get_parts_process();
        if (start_going_to_get_parts_process){
            start_time = System.currentTimeMillis();
            collectDataForGoingTogetParts();

        }
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
    private void collectDataForGoingTogetParts(){
        if (start_going_to_get_parts_process){
            // getting gps points every second automatically
            //make a local array to cache these gps points
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Location location = gpsTracker.getLocation() ;
//                add the point to local array, only if the last saved point in array is >= 1 metre far from this point.
                    if (prevoiusLOcation == null) {
                        locations_list.add(location);
                        prevoiusLOcation = location;
                    } else {
                        if (prevoiusLOcation.distanceTo(location) > 1) {
                            locations_list.add(location);
                            prevoiusLOcation = location;
                        }
                    }
//                last sent GPS point to server's time and difference from the current points time, if it >= 1 min,
//                 send the point array to server, and empty my local array on success.
                    if (System.currentTimeMillis() - start_time > 60000) {
                        // call api to send location array to server
                        GetApiResponseAsyncNoProgress responseAsync = new GetApiResponseAsyncNoProgress(Constants.BASE_URL,"POST", goingtogetPartsResponseListener,goingtogetPartsExceptionListener, getActivity(), "Loading");
                        responseAsync.execute(getRequestParamsForGoingtogetParts());
                    } else {
                        collectDataForGoingTogetParts();
                    }
                }
            }, 10000);
        }
    }
    IHttpResponseListener goingtogetPartsResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {
            try {
                if (response.getString("STATUS").equals("SUCCESS")) {
                    start_time = System.currentTimeMillis();
                    locations_list.clear();
                    prevoiusLOcation = null ;
                    collectDataForGoingTogetParts();
                } else {
                    collectDataForGoingTogetParts();
                }
            }catch (JSONException e){
                collectDataForGoingTogetParts();
            }
        }
    };
    IHttpExceptionListener goingtogetPartsExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            collectDataForGoingTogetParts();
        }
    };
    private HashMap<String,String> getRequestParamsForGoingtogetParts(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","update");
        hashMap.put("object","tech_routes");
        hashMap.put("data[job_id]",CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId());
        hashMap.put("data[travel_type]","PARTS_SHOPPING");
        for (int i = 0 ; i < locations_list.size() ; i++){
            hashMap.put("data[stream]["+i+"][lat]",locations_list.get(i).getLatitude()+"");
            hashMap.put("data[stream]["+i+"][lng]",locations_list.get(i).getLongitude()+"");
            hashMap.put("data[stream]["+i+"][utime]",System.currentTimeMillis() / 1000 + "");
        }
        hashMap.put("token",Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN,""));
        return hashMap;
    }
    private void setupToolBar() {
        ((HomeScreenNew) getActivity()).hideRight();
        ((HomeScreenNew) getActivity()).setTitletext(CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getContact_name() +"-" +CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getJob_customer_addresses_address());
        ((HomeScreenNew) getActivity()).setLeftToolBarImage(R.drawable.menu_icon);
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

    private HashMap<String, String> getRequestParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "jobs");
        hashMap.put("expand[0]", "work_order");
        if (!role.equals("pro"))
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*");
        else
            hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*");

        hashMap.put("where[id]", CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId() + "");
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("page", "1");
        hashMap.put("per_page", "20");
        return hashMap;
    }

    ResponseListener responseListenerScheduled = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    JSONArray results = Response.getJSONObject("RESPONSE").getJSONArray("results");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
//                    nextScheduled = pagination.getString("next");
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
                        model.setIs_claim(obj.getString("is_claim"));
//                        model.setWarranty(obj.getString("warranty"));
//                        if(Utilities.getSharedPreferences(getContext()).getString(Preferences.ROLE, null).equals("pro")) {
                        JSONArray jobAppliances = obj.getJSONArray("job_appliances");
                        ArrayList<JobAppliancesModal> jobapplianceslist = new ArrayList<JobAppliancesModal>();
                        if (jobAppliances != null) {
                            for (int j = 0; j < jobAppliances.length(); j++) {
                                JSONObject jsonObject = jobAppliances.getJSONObject(j);
                                JobAppliancesModal mod = new JobAppliancesModal();
                                mod.setJob_appliances_id(jsonObject.getString("id"));
                                mod.setJob_appliances_job_id(jsonObject.getString("job_id"));
                                mod.setJob_appliances_appliance_id(jsonObject.getString("appliance_id"));
                                if (!jsonObject.isNull("description")) {
                                    mod.setJob_appliances_appliance_description(jsonObject.getString("description"));
                                }
                                if (!jsonObject.isNull("service_type")) {
                                    mod.setJob_appliances_service_type(jsonObject.getString("service_type"));

                                    InstallOrRepairModal installOrRepairModal = mod.getInstallOrRepairModal();

                                    if (jsonObject.getString("service_type").equals("Repair")){

                                        if (!jsonObject.isNull("job_appliance_repair_whats_wrong")){
                                            JSONObject whatsWrongObject = jsonObject.getJSONObject("job_appliance_repair_whats_wrong");
                                            if (!whatsWrongObject.isNull("image")){
                                                JSONObject image = whatsWrongObject.getJSONObject("image");
                                                if (!image.isNull("original"))
                                                    installOrRepairModal.getWhatsWrong().setImage(image.getString("original"));
                                            }

                                            installOrRepairModal.getWhatsWrong().setDiagnosis_and_resolution(whatsWrongObject.getString("diagnosis_and_resolution"));
                                            installOrRepairModal.getWhatsWrong().setIsCompleted(true);
                                        }

                                        if (!jsonObject.isNull("job_appliance_repair_types")){
                                            JSONObject jsonObjectRepairType  = jsonObject.getJSONObject("job_appliance_repair_types");
                                            if (!jsonObjectRepairType.isNull("repair_types")){
                                                JSONObject inner_object_repair_types = jsonObjectRepairType.getJSONObject("repair_types");
                                                installOrRepairModal.getRepairType().setId(inner_object_repair_types.getString("id"));
                                                installOrRepairModal.getRepairType().setPrice(inner_object_repair_types.getString("part_cost"));
                                                installOrRepairModal.getRepairType().setType(inner_object_repair_types.getString("name"));
                                                installOrRepairModal.getRepairType().setLabor_hours(inner_object_repair_types.getString("labor_hours"));
                                                if (!inner_object_repair_types.isNull("calculate_by")){
                                                    if(inner_object_repair_types.getString("calculate_by").equals("FIXED")){
                                                        installOrRepairModal.getRepairType().setCalculatedBy("FIXED");
                                                        if (!inner_object_repair_types.isNull("fixed_cost")){
                                                            installOrRepairModal.getRepairType().setFixed_cost(inner_object_repair_types.getString("fixed_cost"));
                                                        }
                                                    }
                                                }
                                                installOrRepairModal.getRepairType().setIsCompleted(true);
                                            }

                                        }

                                        if (!jsonObject.isNull("job_appliance_repair_info")){
                                            JSONObject jsonObjectRepairInfo  = jsonObject.getJSONObject("job_appliance_repair_info");

                                            installOrRepairModal.getRepairInfo().setModalNumber(jsonObjectRepairInfo.getString("model_number"));
                                            installOrRepairModal.getRepairInfo().setSerialNumber(jsonObjectRepairInfo.getString("serial_number"));
                                            installOrRepairModal.getRepairInfo().setUnitManufacturer(jsonObjectRepairInfo.getString("unit_manufacturer"));
                                            installOrRepairModal.getRepairInfo().setWorkDescription(jsonObjectRepairInfo.getString("work_description"));
                                            installOrRepairModal.getRepairInfo().setIsCompleted(true);
                                            installOrRepairModal.getWorkOrder().setIsCompleted(true);
                                        }

                                    }else if(jsonObject.getString("service_type").equals("Install") || jsonObject.getString("service_type").equals("Re Key")){

                                        if (!jsonObject.isNull("job_appliance_install_types")){
                                            JSONObject jsonObjectRepairType  = jsonObject.getJSONObject("job_appliance_install_types");
                                            if (!jsonObjectRepairType.isNull("install_types")){
                                                JSONObject inner_object_repair_types = jsonObjectRepairType.getJSONObject("install_types");
                                                installOrRepairModal.getRepairType().setId(inner_object_repair_types.getString("id"));
                                                installOrRepairModal.getRepairType().setPrice(inner_object_repair_types.getString("part_cost"));
                                                installOrRepairModal.getRepairType().setType(inner_object_repair_types.getString("name"));
                                                installOrRepairModal.getRepairType().setLabor_hours(inner_object_repair_types.getString("labor_hours"));
                                                if (!inner_object_repair_types.isNull("calculate_by")){
                                                    if(inner_object_repair_types.getString("calculate_by").equals("FIXED")){
                                                        installOrRepairModal.getRepairType().setCalculatedBy("FIXED");
                                                        if (!inner_object_repair_types.isNull("fixed_cost")){
                                                            installOrRepairModal.getRepairType().setFixed_cost(inner_object_repair_types.getString("fixed_cost"));
                                                        }
                                                    }
                                                }
                                                installOrRepairModal.getRepairType().setIsCompleted(true);
                                            }



                                        }

                                        if (!jsonObject.isNull("job_appliance_install_info")){
                                            JSONObject jsonObjectRepairInfo  = jsonObject.getJSONObject("job_appliance_install_info");
                                            installOrRepairModal.getRepairInfo().setModalNumber(jsonObjectRepairInfo.getString("model_number"));
                                            installOrRepairModal.getRepairInfo().setSerialNumber(jsonObjectRepairInfo.getString("serial_number"));
                                            installOrRepairModal.getRepairInfo().setUnitManufacturer(jsonObjectRepairInfo.getString("unit_manufacturer"));
                                            installOrRepairModal.getRepairInfo().setWorkDescription(jsonObjectRepairInfo.getString("work_description"));
                                            installOrRepairModal.getRepairInfo().setIsCompleted(true);
                                            installOrRepairModal.getWorkOrder().setIsCompleted(true);

                                        }
                                    }else {
                                    // for maintain
                                        if (!jsonObject.isNull("job_appliance_maintain_types")){

                                            JSONObject jsonObjectRepairType  = jsonObject.getJSONObject("job_appliance_maintain_types");
                                            if (!jsonObjectRepairType.isNull("maintain_types")){
                                                JSONObject inner_object_repair_types = jsonObjectRepairType.getJSONObject("maintain_types");
                                                installOrRepairModal.getRepairType().setId(inner_object_repair_types.getString("id"));
                                                installOrRepairModal.getRepairType().setPrice(inner_object_repair_types.getString("part_cost"));
                                                installOrRepairModal.getRepairType().setType(inner_object_repair_types.getString("name"));
                                                installOrRepairModal.getRepairType().setLabor_hours(inner_object_repair_types.getString("labor_hours"));
                                                if (!inner_object_repair_types.isNull("calculate_by")){
                                                    if(inner_object_repair_types.getString("calculate_by").equals("FIXED")){
                                                        installOrRepairModal.getRepairType().setCalculatedBy("FIXED");
                                                        if (!inner_object_repair_types.isNull("fixed_cost")){
                                                            installOrRepairModal.getRepairType().setFixed_cost(inner_object_repair_types.getString("fixed_cost"));
                                                        }
                                                    }
                                                }
                                                installOrRepairModal.getRepairType().setIsCompleted(true);
                                            }



                                        }

                                        if (!jsonObject.isNull("job_appliance_maintain_info")){
                                            JSONObject jsonObjectRepairInfo  = jsonObject.getJSONObject("job_appliance_maintain_info");

                                            installOrRepairModal.getRepairInfo().setModalNumber(jsonObjectRepairInfo.getString("model_number"));
                                            installOrRepairModal.getRepairInfo().setSerialNumber(jsonObjectRepairInfo.getString("serial_number"));
                                            installOrRepairModal.getRepairInfo().setUnitManufacturer(jsonObjectRepairInfo.getString("unit_manufacturer"));
                                            installOrRepairModal.getRepairInfo().setWorkDescription(jsonObjectRepairInfo.getString("work_description"));
                                            installOrRepairModal.getRepairInfo().setIsCompleted(true);
                                            installOrRepairModal.getWorkOrder().setIsCompleted(true);
                                        }

                                    }
//                                    if (!jsonObject.isNull("equipment_info")){
//                                        JSONObject equipment_info = jsonObject.getJSONObject("equipment_info");
                                        installOrRepairModal.getEquipmentInfo().setModel_number(jsonObject.getString("model_number"));
                                        if (!jsonObject.isNull("serial_number"))
                                        installOrRepairModal.getEquipmentInfo().setSerial_number(jsonObject.getString("serial_number"));
                                        installOrRepairModal.getEquipmentInfo().setDescription(jsonObject.getString("description"));
                                        installOrRepairModal.getEquipmentInfo().setBrand_name(jsonObject.getString("brand_name"));
                                        if (!jsonObject.isNull("image")){
                                            JSONObject image =  jsonObject.getJSONObject("image");
                                            if (!image.isNull("original")){
                                                String  original = image.getString("original");
                                                installOrRepairModal.getEquipmentInfo().setImage(original);
                                            }


                                        }
                                        if(jsonObject.getString("model_number").length() > 0)
                                        installOrRepairModal.getEquipmentInfo().setIsCompleted(true);
//                                    }
                                    JSONArray jsonArray = jsonObject.getJSONArray("job_parts_used");
                                    ArrayList<Parts> partsArrayList = new ArrayList<Parts>();
                                    for (int k = 0 ; k < jsonArray.length() ; k++){
                                        JSONObject partsObject = jsonArray.getJSONObject(k);
                                        Parts parts = new Parts();
                                        parts.setCost(partsObject.getString("part_cost"));
                                        parts.setDescription(partsObject.getString("part_desc"));
                                        parts.setQuantity(partsObject.getString("qty"));
                                        parts.setNumber(partsObject.getString("part_num"));
                                        partsArrayList.add(parts);

                                    }
                                    installOrRepairModal.getPartsContainer().setPartsArrayList(partsArrayList);

                                    if(!jsonObject.isNull("work_order")){
                                        JSONObject workorderObject = jsonObject.getJSONObject("work_order");
                                        installOrRepairModal.getWorkOrder().setTotal(workorderObject.getString("total"));
                                        installOrRepairModal.getWorkOrder().setTax(workorderObject.getString("tax"));
                                        if (workorderObject.has("sub_total"))
                                        installOrRepairModal.getWorkOrder().setSub_total(workorderObject.getString("sub_total"));
                                        Jobtotal = Jobtotal + Float.parseFloat(workorderObject.getString("total"));
                                        if (!workorderObject.getString("status").equals("NOT_SENT"))
                                        installOrRepairModal.getWorkOrder().setIsCompleted(true);
                                    }
                                    if (partsArrayList.size()>0){
                                        installOrRepairModal.getPartsContainer().setIsCompleted(true);
                                    }
                                    if (!jsonObject.isNull("customer_signature")) {
                                        installOrRepairModal.getSignature().setSignature_path(jsonObject.getString("customer_signature"));
                                        if (jsonObject.getString("customer_signature").length() > 0) {
                                            installOrRepairModal.getSignature().setSignature_path(jsonObject.getString("customer_signature"));
                                            installOrRepairModal.getSignature().setIsCompleted(true);
                                            mod.setIsProcessCompleted(true);
                                        }
                                    }
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
                                    if (mod.getAppliance_type_name().equals("Re Key")){
                                        mod.getInstallOrRepairModal().getEquipmentInfo().setIsCompleted(true);
                                    }
                                    mod.setAppliance_type_soft_deleted(appliance_type_obj.getString("_soft_deleted"));
                                    if (!appliance_type_obj.isNull("image")) {
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
                        if (!obj.isNull("technicians")) {
                            JSONObject technician_object = obj.getJSONObject("technicians");
                            model.setTechnician_id(technician_object.getString("id"));
                            model.setTechnician_fname(technician_object.getString("first_name"));
                            model.setTechnician_lname(technician_object.getString("last_name"));
                            model.setTechnician_pickup_jobs(technician_object.getString("pickup_jobs"));
                            model.setTechnician_avg_rating(technician_object.getString("avg_rating"));
                            model.setTechnician_scheduled_job_count(technician_object.getString("scheduled_jobs_count"));
                            model.setTechnician_completed_job_count(technician_object.getString("completed_jobs_count"));
                            if (!technician_object.isNull("profile_image")) {
                                JSONObject object_profile_image = technician_object.getJSONObject("profile_image");
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
                        if (!obj.isNull("cost_details")) {
                            JSONObject cost_details_obj = obj.getJSONObject("cost_details");

                            model.setCost_details_tripcharges(cost_details_obj.getString("trip_charges"));
                            model.setCost_details_tax(cost_details_obj.getString("tax"));
                            model.setCost_details_tripcharges(cost_details_obj.getString("trip_charges"));
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
                        CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(model);

//                        CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().getWhatsWrong().setDiagnosis_and_resolution(editDescription.getText().toString());
//                        CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().getWhatsWrong().setImage(Path);
//                        CurrentScheduledJobSingleTon.getInstance().getCurrentReapirInstallProcessModal().setIsCompleted(true);
                        handler.sendEmptyMessage(0);
                    }
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
                case 0:{
                     adapter = new InstallOrRepairJobAdapter(getActivity(),CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getJob_appliances_arrlist(),getResources());
                    lstInstallRepair.addFooterView(getFooterView());
                    lstInstallRepair.addHeaderView(getHeaderView());
                    lstInstallRepair.setAdapter(adapter);
                    isJobCompleted();
                    getTotalCost();
                    if (isAutoNotiForWorkOrder){
                        fragment = new WhatsWrongFragment();
                        for (int i = 0 ; i < CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getJob_appliances_arrlist().size() ; i++){
                            if(CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getJob_appliances_arrlist().get(i).getJob_appliances_id().equals(appliance_id)){
                                CurrentScheduledJobSingleTon.getInstance().setSelectedJobApplianceModal(CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getJob_appliances_arrlist().get(i));

                                break;
                            }
                        }
                        isAutoNotiForWorkOrder = false ;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isAutoNotiForWorkOrder",isAutoNotiForWorkOrder);
                        ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.WHATS_WRONG_FRAGMENT, true, bundle);
                    }
                    break;
                }
                case 1: {
                    showAlertDialog("Fixd-Pro", error_message,false);
                    break;
                }
                case 2:{
                    CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(null);
                    // show pro cut popup
                    shoProcutPopupdialog();
                    break;
                }
                case 3:{
                    showAlertDialog("Fixd-Pro", error_message,true);
                    break;
                }
                case 4:{
                    txtJobTotal.setText("$"+total_cost);
                    break;
                }
            }
        }
    };
    private void isJobCompleted(){
        boolean isAllCompleted = true ;
        for (int i = 0 ; i < CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getJob_appliances_arrlist().size() ; i++){
                if (!CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getJob_appliances_arrlist().get(i).isProcessCompleted()){
                    isAllCompleted = false ;
                    break;
                }
        }
        if (isAllCompleted){
            layout_finish_job.setVisibility(View.VISIBLE);
        }
    }
    private void showAlertDialog(String Title,String Message, final boolean doSomeThing){
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
                        if (doSomeThing){
                            (((HomeScreenNew) getActivity())).popInclusiveFragment(Constants.SCHEDULED_LIST_DETAILS_FRAGMENT);
                        }

                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    private View getFooterView(){
            LayoutInflater inflater = getActivity().getLayoutInflater();
            ViewGroup footerView = (ViewGroup) inflater.inflate(R.layout.install_or_repair_footer_item, lstInstallRepair,
                    false);
            ImageView img_AddServices = (ImageView)footerView.findViewById(R.id.img_AddServices);
            img_AddServices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment = new AddServiceFragment();
                    ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.ADD_SERVICE_FRAGMENT, true, null);
                }
            });
            return footerView;

    }
    private View getHeaderView(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup headerView = (ViewGroup) inflater.inflate(R.layout.install_repair_job_total_item, lstInstallRepair,
                false);
        txtJobTotal = (TextView)headerView.findViewById(R.id.txtJobTotal);
        txtJobTotal.setText("$" + Jobtotal + "");
        return headerView;

    }
    private void shoProcutPopupdialog(){
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pro_cut);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        Button img_Finish = (Button)dialog.findViewById(R.id.img_Finish);
        TextView txtJobTotal1 = (TextView)dialog.findViewById(R.id.txtJobTotal1);
        TextView txtOurFee = (TextView)dialog.findViewById(R.id.txtOurFee);
        TextView txtYourCut = (TextView)dialog.findViewById(R.id.txtYourCut);
        TextView txtMileage = (TextView)dialog.findViewById(R.id.txtMileage);
        LinearLayout LinearLayout = (LinearLayout)dialog.findViewById(R.id.layout_mileage);

        txtJobTotal1.setText("$"+total);
        txtOurFee.setText("$"+fixd_cut);
        txtYourCut.setText("$"+pro_cut);
        txtMileage.setText("$"+job_mileage);
        if (job_mileage.length() > 0 && !job_mileage.equals("0")){

        }else{
            LinearLayout.setVisibility(View.GONE);
        }
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        img_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),HomeScreenNew.class);
                    startActivity(intent);
                    getActivity().finish();
            }
        });
        dialog.show();
    }
    private void showCustomDialog() {
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_finish);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        TextView img_Finish = (TextView)dialog.findViewById(R.id.img_Finish);
        AvailableJobModal availableJobModal = CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal();
        TextView txtUserName = (TextView)dialog.findViewById(R.id.txtUserName);
        txtUserName.setText(availableJobModal.getContact_name());
        TextView txtAddress = (TextView)dialog.findViewById(R.id.txtAddress);
        txtAddress.setText(availableJobModal.getJob_customer_addresses_address()+" - "+availableJobModal.getJob_customer_addresses_city()+","+availableJobModal.getJob_customer_addresses_state());
        TextView txtDateTime = (TextView)dialog.findViewById(R.id.txtDateTime);
        txtDateTime.setText(Utilities.convertDate(availableJobModal.getRequest_date()));

        String dateTime[] = Utilities.getDate(availableJobModal.getCreated_at()).split(" ");
        String dateTime1[] = Utilities.getDate(availableJobModal.getFinished_at()).split(" ");
        String actualDateTime = dateTime[0] +" "+ dateTime[1] + " at " + dateTime[2] +"" +dateTime[3];
        String actualDateTime1 = dateTime1[0] +" "+ dateTime1[1] + " at " + dateTime1[2] +"" +dateTime1[3];
        TextView txtArrivalTime = (TextView)dialog.findViewById(R.id.txtArrivalTime);
        txtArrivalTime.setText(dateTime[2] +"" +dateTime[3]);

        TextView txtCompleteTime = (TextView)dialog.findViewById(R.id.txtCompleteTime);
        txtCompleteTime.setText(dateTime1[2] +"" +dateTime1[3]);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        img_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                start_going_to_get_parts_process = false ;
                GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerFinishJob, getActivity(), "Loading");
                responseAsync.execute(getRequestParamsFinishJob());
            }
        });
        dialog.show();
    }
    ResponseListener responseListenerFinishJob = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
                Log.e("","Response"+Response);
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    total = Response.getJSONObject("RESPONSE").getString("total");
                    JSONObject cuts = Response.getJSONObject("RESPONSE").getJSONObject("cuts");
                    pro_cut = cuts.getString("pro_cut");
                    fixd_cut = cuts.getString("fixd_cut");
                    if (!cuts.isNull("job_mileage"))
                    job_mileage = cuts.getString("job_mileage");
                    handler.sendEmptyMessage(2);
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);

                    }
                    handler.sendEmptyMessage(3);
                }
            }catch (JSONException e){

            }
        }
    };
    private HashMap<String, String> getRequestParamsFinishJob() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "finish");
        hashMap.put("object", "jobs");
        hashMap.put("data[job_id]", CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId() + "");
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        return hashMap;
    }
    private HashMap<String, String> getRequestParamsForTotalCost() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "estimated_pro_cut");
        hashMap.put("object", "jobs");
        hashMap.put("data[job_id]", CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId() + "");
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        return hashMap;
    }
    private void getTotalCost(){
//         Getting Trade Skills on App Start
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(Constants.BASE_URL, "POST", getRequestParamsForTotalCost());
                if (jsonObject != null) {
                    try {
                        String STATUS = jsonObject.getString("STATUS");
                        if (STATUS.equals("SUCCESS")) {
                            total_cost = jsonObject.getString("RESPONSE");

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                txtJobTotal.setText("$"+total_cost);
            }
        }.execute();
    }
}