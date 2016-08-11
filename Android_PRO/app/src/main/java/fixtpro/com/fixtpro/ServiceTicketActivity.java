package fixtpro.com.fixtpro;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paging.listview.PagingListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixtpro.com.fixtpro.adapters.AvailableJobsPagingAdaper;
import fixtpro.com.fixtpro.beans.AvailableJobModal;
import fixtpro.com.fixtpro.beans.JobAppliancesModal;
import fixtpro.com.fixtpro.beans.JobPartsUsedModal;
import fixtpro.com.fixtpro.beans.install_repair_beans.InstallOrRepairModal;
import fixtpro.com.fixtpro.beans.install_repair_beans.Parts;
import fixtpro.com.fixtpro.utilites.CurrentScheduledJobSingleTon;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class ServiceTicketActivity extends AppCompatActivity {
    private Context context = ServiceTicketActivity.this;
    private String TAG = "ServiceTicketActivity";
    private Toolbar toolbar;
    private ImageView cancel;
    private Typeface fontfamily,italicfontfamily;
    ArrayList<AvailableJobModal> completedjoblist  = new ArrayList<AvailableJobModal>();
    String jobId = "";
    TextView txtToolbar,txttop,txtCustomer,txtCustomerSet,txtJobName,txtJobNameSet,txtType,txtTypeSet,
            txtUnit1,txtUnit1Set,txtcustomerCompaint,txtcustomerCompaintSet,txtGarInstall,txtGarDoller,txtIceInstall,txtTax,
            txtTexDoller,txtTotalDoller,txtContractor,txtContractoeDoller,txtIceDoller;
    TextView txtCustomerComplaint,txtDisplaingError,txtDescriptionOfWork,txtDescriptionTExt,txtRepairType,txtRepairTypeText,txtPartsType,txtHomeandConnectionText,txtPartsDollerTxxt;

    String nextComplete = "null";
    String error_message =  "";
    Context _context = this;
    LinearLayout layoutAppliances,layoutPartsUsed ;
    LayoutInflater inflater = null;
    float Jobtotal = 0;
    AvailableJobModal availableJobModal ;
    View layout_workorder;
    boolean isWorkorderShown = false;
    LinearLayout layout_appliances;
    ImageView imgArrow ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_ticket);
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setToolBar();

        setWidgets();

        setListeners();

        setTypeface();
        if (getIntent() != null){
            availableJobModal = (AvailableJobModal)getIntent().getSerializableExtra("modal");
            jobId = availableJobModal.getId();
            GetApiResponseAsync responseAsyncCompleted = new GetApiResponseAsync("POST", responseListenerCompleted, this, "Loading");
            responseAsyncCompleted.execute(getRequestParams(jobId));
        }

    }
    private void setListeners(){
        layout_appliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWorkorderShown) {
                    layout_workorder.setVisibility(View.GONE);
                    imgArrow.setImageResource(R.drawable.next_arrow_orange);
                    isWorkorderShown = false ;

                } else {
                    layout_workorder.setVisibility(View.VISIBLE);
                    imgArrow.setImageResource(R.drawable.down_arrow_orange);
                    isWorkorderShown = true ;
                }
            }
        });
    }

    ResponseListener responseListenerCompleted = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("","Response"+Response.toString());
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
                        model.setTotal_cost(obj.getString("total_cost"));
                        model.setUpdated_at(obj.getString("updated_at"));
                        model.setWarranty(obj.getString("warranty"));
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

                                        }

                                        if (!jsonObject.isNull("job_appliance_repair_types")){
                                            JSONObject jsonObjectRepairType  = jsonObject.getJSONObject("job_appliance_repair_types");
                                            JSONObject inner_object_repair_types = jsonObjectRepairType.getJSONObject("repair_types");
                                            installOrRepairModal.getRepairType().setId(inner_object_repair_types.getString("id"));
                                            installOrRepairModal.getRepairType().setPrice(inner_object_repair_types.getString("cost"));
                                            installOrRepairModal.getRepairType().setType(inner_object_repair_types.getString("name"));

                                        }

                                        if (!jsonObject.isNull("job_appliance_repair_info")){
                                            JSONObject jsonObjectRepairInfo  = jsonObject.getJSONObject("job_appliance_repair_info");

                                            installOrRepairModal.getRepairInfo().setModalNumber(jsonObjectRepairInfo.getString("model_number"));
                                            installOrRepairModal.getRepairInfo().setSerialNumber(jsonObjectRepairInfo.getString("serial_number"));
                                            installOrRepairModal.getRepairInfo().setUnitManufacturer(jsonObjectRepairInfo.getString("unit_manufacturer"));
                                            installOrRepairModal.getRepairInfo().setWorkDescription(jsonObjectRepairInfo.getString("work_description"));

                                        }

                                    }else if(jsonObject.getString("service_type").equals("Install")){

                                        if (!jsonObject.isNull("job_appliance_install_types")){
                                            JSONObject jsonObjectRepairType  = jsonObject.getJSONObject("job_appliance_install_types");
                                            JSONObject inner_object_repair_types = jsonObjectRepairType.getJSONObject("install_types");
                                            installOrRepairModal.getRepairType().setId(inner_object_repair_types.getString("id"));
                                            installOrRepairModal.getRepairType().setPrice(inner_object_repair_types.getString("cost"));
                                            installOrRepairModal.getRepairType().setType(inner_object_repair_types.getString("name"));


                                        }

                                        if (!jsonObject.isNull("job_appliance_install_info")){
                                            JSONObject jsonObjectRepairInfo  = jsonObject.getJSONObject("job_appliance_install_info");

                                            installOrRepairModal.getRepairInfo().setModalNumber(jsonObjectRepairInfo.getString("model_number"));
                                            installOrRepairModal.getRepairInfo().setSerialNumber(jsonObjectRepairInfo.getString("serial_number"));
                                            installOrRepairModal.getRepairInfo().setUnitManufacturer(jsonObjectRepairInfo.getString("unit_manufacturer"));
                                            installOrRepairModal.getRepairInfo().setWorkDescription(jsonObjectRepairInfo.getString("work_description"));


                                        }


                                    }else {
                                        // for maintain
                                        if (!jsonObject.isNull("job_appliance_maintain_types")){
                                            JSONObject jsonObjectRepairType  = jsonObject.getJSONObject("job_appliance_maintain_types");
                                            JSONObject inner_object_repair_types = jsonObjectRepairType.getJSONObject("install_types");
                                            installOrRepairModal.getRepairType().setId(inner_object_repair_types.getString("id"));
                                            installOrRepairModal.getRepairType().setPrice(inner_object_repair_types.getString("cost"));
                                            installOrRepairModal.getRepairType().setType(inner_object_repair_types.getString("name"));
                                            installOrRepairModal.getRepairType().setIsCompleted(true);

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
                                    if (!jsonObject.isNull("equipment_info")){
                                        JSONObject equipment_info = jsonObject.getJSONObject("equipment_info");
                                        installOrRepairModal.getEquipmentInfo().setIsCompleted(true);
                                    }
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
                        handler.sendEmptyMessage(4);
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

    private void setToolBar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cancel = (ImageView)toolbar.findViewById(R.id.cancel);
        txtToolbar = (TextView)toolbar.findViewById(R.id.txtToolbar);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    private void setWidgets() {
        txttop = (TextView)findViewById(R.id.txttop);
        txtCustomer = (TextView)findViewById(R.id.txtCustomer);
        txtCustomerSet = (TextView)findViewById(R.id.txtCustomerSet);
        txtJobName = (TextView)findViewById(R.id.txtJobName);
        txtJobNameSet = (TextView)findViewById(R.id.txtJobNameSet);
        txtType = (TextView)findViewById(R.id.txtType);
        txtTypeSet = (TextView)findViewById(R.id.txtTypeSet);
        txtUnit1 = (TextView)findViewById(R.id.txtUnit1);
        txtUnit1Set = (TextView)findViewById(R.id.txtUnit1Set);
        txtcustomerCompaint = (TextView)findViewById(R.id.txtcustomerCompaint);
        txtcustomerCompaintSet = (TextView)findViewById(R.id.txtcustomerCompaintSet);

        txtGarInstall = (TextView)findViewById(R.id.txtGarInstall);
        txtGarDoller = (TextView)findViewById(R.id.txtGarDoller);
        txtTax = (TextView)findViewById(R.id.txtTax);
        txtTexDoller = (TextView)findViewById(R.id.txtTexDoller);
        txtTotalDoller = (TextView)findViewById(R.id.txtTotalDoller);
        txtContractor = (TextView)findViewById(R.id.txtContractor);
        txtContractoeDoller = (TextView)findViewById(R.id.txtContractoeDoller);
        layoutAppliances = (LinearLayout)findViewById(R.id.layout4);
        layoutPartsUsed = (LinearLayout)findViewById(R.id.layout7);

        layout_workorder = (View)findViewById(R.id.layout_workorder);
        layout_workorder.setVisibility(View.GONE);

        layout_appliances = (LinearLayout)findViewById(R.id.layout_appliances);

        imgArrow = (ImageView)findViewById(R.id.imgArrow);

        txtCustomerComplaint = (TextView)findViewById(R.id.txtCustomerComplaint);
        txtDisplaingError = (TextView)findViewById(R.id.txtDisplaingError);
        txtDescriptionOfWork = (TextView)findViewById(R.id.txtDescriptionOfWork);
        txtDescriptionTExt = (TextView)findViewById(R.id.txtDescriptionTExt);
        txtRepairType = (TextView)findViewById(R.id.txtRepairType);
        txtRepairTypeText = (TextView)findViewById(R.id.txtRepairTypeText);
        txtPartsType = (TextView)findViewById(R.id.txtPartsType);
        txtHomeandConnectionText = (TextView)findViewById(R.id.txtHomeandConnectionText);
        txtPartsDollerTxxt = (TextView)findViewById(R.id.txtPartsDollerTxxt);
    }
    private void setTypeface() {
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
        italicfontfamily = Typeface.createFromAsset(getAssets(),"HelveticaNeueLTStd-It.otf");

        txtToolbar.setTypeface(fontfamily);
//        txttop.setTypeface(fontfamily);
//        txtCustomer.setTypeface(fontfamily);
//        txtCustomerSet.setTypeface(fontfamily);
//        txtJobName.setTypeface(fontfamily);
//        txtJobNameSet.setTypeface(fontfamily);
//        txtType.setTypeface(fontfamily);
//        txtTypeSet.setTypeface(fontfamily);
//
//
//        txtcustomerCompaint.setTypeface(fontfamily);
//        txtcustomerCompaintSet.setTypeface(fontfamily);
//
//        txtGarInstall.setTypeface(fontfamily);
//        txtGarDoller.setTypeface(fontfamily);
//
//        txtTax.setTypeface(fontfamily);
//        txtTexDoller.setTypeface(fontfamily);
//        txtTotalDoller.setTypeface(fontfamily);
//
//        txtContractor.setTypeface(italicfontfamily);
//        txtContractoeDoller.setTypeface(italicfontfamily);
    }

    private HashMap<String,String> getRequestParams(String Jobid){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "read");
        hashMap.put("object","jobs");
//        if (Utilities.getSharedPreferences(this).getString(Preferences.ROLE, null).equals("pro"))
        hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.equipment_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*");
//        else
//        hashMap.put("select", "^*,job_parts_used.^*,job_images.^*,job_repair.^*,job_repair.repair_types.^*,job_appliances.appliance_types.services.^*,job_appliances.appliance_types.^*,time_slots.^*,job_customer_addresses.^*");
        hashMap.put("where[id]", Jobid);
        hashMap.put("expand[0]", "work_order");
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("page", 1+"");
        hashMap.put("per_page", "15");
        return hashMap;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 1:{
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }
                case 4:{
                    setValues();
                    break;
                }

            }
        }
    };
    private  void setValues(){

        if (completedjoblist.size() > 0){
            for (int i = 0 ; i < completedjoblist.get(0).getJob_appliances_arrlist().size() ; i++ ){
                View view = inflater.inflate(R.layout.service_ticket_unit_item, null);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params1.setMargins(0, 16, 0, 0);
                params1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                ((LinearLayout)view).setLayoutParams(params1);

                TextView txtUnit1 = (TextView)view.findViewById(R.id.txtUnit1);
                TextView txtUnit1Set = (TextView)view.findViewById(R.id.txtUnit1Set);
                ViewGroup.LayoutParams params = txtUnit1Set.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;

                txtUnit1Set.setLayoutParams(params);
                txtUnit1.setTypeface(fontfamily);
                txtUnit1Set.setTypeface(fontfamily);
                txtUnit1.setText("Unit " +(i+1) +":");
                txtUnit1Set.setText(completedjoblist.get(0).getJob_appliances_arrlist().get(i).getAppliance_type_name());
                layoutAppliances.addView(view);
            }
            for ( int i = 0 ; i < completedjoblist.get(0).getJob_parts_used_list().size() ; i++){
                View view = inflater.inflate(R.layout.service_ticket_parts_used_item, null);
                TextView txtpart = (TextView)view.findViewById(R.id.txtpart);
                TextView txtpartNum = (TextView)view.findViewById(R.id.txtpartNum);
                TextView txtpartNumD = (TextView)view.findViewById(R.id.txtpartNumD);
                TextView txtpartNumDSet = (TextView)view.findViewById(R.id.txtpartNumDSet);
                txtpart.setTypeface(fontfamily);
                txtpartNum.setTypeface(fontfamily);
                txtpartNumD.setTypeface(fontfamily);
                txtpartNumDSet.setTypeface(fontfamily);
                txtpart.setText("Part "+ i + ":");
                txtpartNum.setText(completedjoblist.get(0).getJob_parts_used_list().get(i).getQty());
                txtpartNumD.setText(completedjoblist.get(0).getJob_parts_used_list().get(i).getPart_desc());
                txtpartNumDSet.setText(completedjoblist.get(0).getJob_parts_used_list().get(i).getPart_num());
                layoutPartsUsed.addView(view);
            }

//            JobAppliancesModal modal = new JobAppliancesModal();
//            txtDisplaingError.setText(modal.getJob_appliances_customer_compalint());
//            txtDescriptionTExt.setText(modal.getJob_appliances_appliance_description());
//            txtRepairTypeText.setText();
//            for (int i = 0; i < partsArrayList.)
//            txtHomeandConnectionText.setText();//setarray
//            txtPartsDollerTxxt.setText();




        }


    }
    private void showAlertDialog(String Title,String Message){
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
}
