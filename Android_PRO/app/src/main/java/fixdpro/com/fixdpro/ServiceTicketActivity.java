package fixdpro.com.fixdpro;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.beans.install_repair_beans.InstallOrRepairModal;
import fixdpro.com.fixdpro.beans.install_repair_beans.Parts;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

public class ServiceTicketActivity extends AppCompatActivity {
    private Context context = ServiceTicketActivity.this;
    private String TAG = "ServiceTicketActivity";
    private Toolbar toolbar;
    private ImageView cancel;
    private Typeface fontfamily,italicfontfamily;
    ArrayList<AvailableJobModal> completedjoblist  = new ArrayList<AvailableJobModal>();
    String jobId = "";
    TextView txtToolbar,txtTotalCost,txtName,txtAddresss,txtSubTotalDoller,txtJobId,txtTaxDoller,txtBack,txtWarrenty,txtWarrentyDoller,txtTotal,txtTotalDoller;
//    TextView txtCustomerComplaint,txtDisplaingError,txtDescriptionOfWork,txtDescriptionTExt,txtRepairType,txtRepairTypeText,txtPartsType,txtHomeandConnectionText,txtPartsDollerTxxt,txtApplianceName;

    String nextComplete = "null";
    String error_message =  "";
    Context _context = this;
    LinearLayout layoutAppliances,layoutPartsUsed ;
    LayoutInflater inflater = null;
    float Jobtotal = 0;
    AvailableJobModal availableJobModal ;

    boolean isWorkorderShown = false;
    LinearLayout layout_workorder;
    LinearLayout linearLayoutPrevious =  null ;
    ImageView imageViewPrevious = null ;

    ArrayList<JobAppliancesModal> jobapplianceslist = new ArrayList<JobAppliancesModal>();
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
            txtTotalCost.setText("$"+availableJobModal.getJob_line_items_pro_cut());
            txtName.setText(availableJobModal.getContact_name());
            txtJobId.setText("Job#"+jobId  );
            txtSubTotalDoller.setText("$"+availableJobModal.getJob_line_items_sub_total());
            txtWarrentyDoller.setText("$"+availableJobModal.getJob_line_items_diagnostic_fee());
            txtTaxDoller.setText("$"+availableJobModal.getJob_line_items_tax());
            txtTotalDoller.setText("$"+availableJobModal.getJob_line_items_pro_cut());
            txtAddresss.setText(availableJobModal.getJob_customer_addresses_address() + " " + availableJobModal.getJob_customer_addresses_address_2());
            GetApiResponseAsync responseAsyncCompleted = new GetApiResponseAsync("POST", responseListenerCompleted, this, "Loading");
            responseAsyncCompleted.execute(getRequestParams(jobId));
            if (availableJobModal.getIs_claim().equals("1") && availableJobModal.getJob_line_items_is_covered().equals("1")){
                txtWarrenty.setText("Warrenty");
                txtSubTotalDoller.setPaintFlags(txtSubTotalDoller.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            if (availableJobModal.getJob_line_items_diagnostic_fee().equals("0") || availableJobModal.getJob_line_items_diagnostic_fee().equals("0.0")){
                txtWarrentyDoller.setText("$50.00");
            }
        }

    }
    private void setListeners(){
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        layout_appliances.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isWorkorderShown) {
//                    layout_workorder.setVisibility(View.GONE);
//                    imgArrow.setImageResource(R.drawable.next_arrow_orange);
//                    isWorkorderShown = false ;
//
//                } else {
//                    layout_workorder.setVisibility(View.VISIBLE);
//                    imgArrow.setImageResource(R.drawable.down_arrow_orange);
//                    isWorkorderShown = true ;
//                }
//            }
//        });
    }

    ResponseListener responseListenerCompleted = new ResponseListener() {
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

                        JSONArray jobAppliances = obj.getJSONArray("job_appliances");
                        jobapplianceslist.clear();
                        if (jobAppliances != null) {
                            for (int j = 0; j < jobAppliances.length(); j++) {
                                JSONObject jsonObject = jobAppliances.getJSONObject(j);
                                JobAppliancesModal mod = new JobAppliancesModal();
                                mod.setJob_appliances_id(jsonObject.getString("id"));
                                mod.setJob_appliances_job_id(jsonObject.getString("job_id"));
                                mod.setJob_appliances_appliance_id(jsonObject.getString("appliance_id"));
                                mod.setJob_appliances_brand_name(jsonObject.getString("brand_name"));

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
                                            }


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
                                            }



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
                                            if (!jsonObjectRepairType.isNull("maintain_types")){
                                                JSONObject inner_object_repair_types = jsonObjectRepairType.getJSONObject("maintain_types");
                                                installOrRepairModal.getRepairType().setId(inner_object_repair_types.getString("id"));
                                                installOrRepairModal.getRepairType().setPrice(inner_object_repair_types.getString("part_cost"));
                                                installOrRepairModal.getRepairType().setType(inner_object_repair_types.getString("name"));
                                                installOrRepairModal.getRepairType().setLabor_hours(inner_object_repair_types.getString("labor_hours"));
//                                            installOrRepairModal.getRepairType().setIsCompleted(true);
                                                if (!inner_object_repair_types.isNull("calculate_by")){
                                                    if(inner_object_repair_types.getString("calculate_by").equals("FIXED")){
                                                        installOrRepairModal.getRepairType().setCalculatedBy("FIXED");
                                                        if (!inner_object_repair_types.isNull("fixed_cost")){
                                                            installOrRepairModal.getRepairType().setFixed_cost(inner_object_repair_types.getString("fixed_cost"));
                                                        }
                                                    }
                                                }
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
                                        installOrRepairModal.getWorkOrder().setHourly_rate(workorderObject.getString("hourly_rate"));
                                        if (workorderObject.has("subtotal"))
                                            installOrRepairModal.getWorkOrder().setSub_total(workorderObject.getString("subtotal"));
                                        Jobtotal = Jobtotal + Float.parseFloat(workorderObject.getString("total"));
//                                        installOrRepairModal.getWorkOrder().setIsCompleted(true);
                                    }
                                    if (!jsonObject.isNull("customer_signature")) {
                                        installOrRepairModal.getSignature().setSignature_path(jsonObject.getString("customer_signature"));
                                        if (jsonObject.getString("customer_signature").length() > 0) {
                                            installOrRepairModal.getSignature().setSignature_path(jsonObject.getString("customer_signature"));

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
//                                if(!jsonObject.isNull(""))
//                                JSONObject services_obj = jsonObject.getJSONObject("services");
//                                mod.setService_id(services_obj.getString("id"));
//                                mod.setService_name(services_obj.getString("name"));
//                                mod.setService_created_at(services_obj.getString("created_at"));
//                                mod.setService_updated_at(services_obj.getString("updated_at"));
                                jobapplianceslist.add(mod);
                            }
//                            }
                            availableJobModal.setJob_appliances_arrlist(jobapplianceslist);
                        }

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

//
        txtTotalCost = (TextView)findViewById(R.id.txtTotalCost);
        txtSubTotalDoller = (TextView)findViewById(R.id.txtSubTotalDoller);
        txtTaxDoller = (TextView)findViewById(R.id.txtTaxDoller);

        txtWarrenty = (TextView)findViewById(R.id.txtWarrenty);
        txtWarrentyDoller = (TextView)findViewById(R.id.txtWarrentyDoller);
        txtTotal = (TextView)findViewById(R.id.txtTotal);
        txtTotalDoller = (TextView)findViewById(R.id.txtTotalDoller);
        txtBack = (TextView)findViewById(R.id.txtBack);
        txtJobId = (TextView)findViewById(R.id.txtJobId);
        txtName = (TextView)findViewById(R.id.txtName);
        txtAddresss = (TextView)findViewById(R.id.txtAddresss);
        layout_workorder = (LinearLayout)findViewById(R.id.layout_workorder);
    }
    private void setTypeface() {
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
        italicfontfamily = Typeface.createFromAsset(getAssets(),"HelveticaNeueLTStd-It.otf");

        txtToolbar.setTypeface(fontfamily);
    }

    private HashMap<String,String> getRequestParams(String Jobid){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "read");
        hashMap.put("object","jobs");
//        if (Utilities.getSharedPreferences(this).getString(Preferences.ROLE, null).equals("pro"))
        hashMap.put("select", "^*,job_appliances.^*,job_appliances.appliance_types.^*,job_appliances.job_parts_used.^*,job_appliances.job_appliance_install_info.^*,job_appliances.job_appliance_install_types.install_types.^*,job_customer_addresses.^*,technicians.^*,job_appliances.job_appliance_repair_whats_wrong.^*,job_appliances.job_appliance_repair_types.repair_types.^*,job_appliances.job_appliance_maintain_info.^*,job_appliances.job_appliance_maintain_types.maintain_types.^*");
//        else
//        hashMap.put("select", "^*,job_parts_used.^*,job_images.^*,job_repair.^*,job_repair.repair_types.^*,job_appliances.appliance_types.services.^*,job_appliances.appliance_types.^*,time_slots.^*,job_customer_addresses.^*");
        hashMap.put("where[id]", Jobid);
        hashMap.put("expand[0]", "work_order");
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("page", 1+"");
        hashMap.put("per_page", "15");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    initLayout();
                    break;
                }
                case 1:{
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }
                case 4:{
//                    setValues();
                    break;
                }

            }
        }
    };

    private void initLayout(){
        layout_workorder.removeAllViews();
        float tax =0;
        float subtotal =0;

        for (int i = 0 ; i < jobapplianceslist.size() ; i++){
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(R.layout.layout_worker_anim, null);
            LinearLayout layout_appliances = (LinearLayout)view.findViewById(R.id.layout_appliances);
            final LinearLayout  layout_desc_container = (LinearLayout)view.findViewById(R.id.layout_desc_container);
            final TextView txtApplianceName = (TextView)view.findViewById(R.id.txtApplianceName);
            final ImageView imgArrow = (ImageView)view.findViewById(R.id.imgArrow);

            final TextView txtCustomerComplaint = (TextView)view.findViewById(R.id.txtCustomerComplaint);
            final TextView txtDisplaingError = (TextView)view.findViewById(R.id.txtDisplaingError);
            final TextView txtRepairTypeDollarText = (TextView)view.findViewById(R.id.txtRepairTypeDollarText);
            final TextView txtDescriptionOfWork = (TextView)view.findViewById(R.id.txtDescriptionOfWork);
            final TextView txtDescriptionTExt = (TextView)view.findViewById(R.id.txtDescriptionTExt);
            final TextView txtRepairType = (TextView)view.findViewById(R.id.txtRepairType);
            final TextView txtRepairTypeText = (TextView)view.findViewById(R.id.txtRepairTypeText);
            final TextView txtPartsType = (TextView)view.findViewById(R.id.txtPartsType);
            final TextView txtHomeandConnectionText = (TextView)view.findViewById(R.id.txtHomeandConnectionText);
            final TextView txtPartsDollerTxxt = (TextView)view.findViewById(R.id.txtPartsDollerTxxt);
            txtApplianceName.setText(jobapplianceslist.get(i).getAppliance_type_name());
//            layout_desc_container.setTag(i + "ll");
//            layout_appliances.setTag(i + "");
//            imgArrow.setTag(i + "img");
            layout_appliances.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (layout_desc_container.getVisibility() == View.VISIBLE) {
                        layout_desc_container.setVisibility(View.GONE);
                        imgArrow.setImageResource(R.drawable.next_arrow_orange);
                    } else {
                        layout_desc_container.setVisibility(View.VISIBLE);
                        imgArrow.setImageResource(R.drawable.down_arrow_orange);
                    }

                    if (linearLayoutPrevious != null && linearLayoutPrevious.getVisibility() == View.VISIBLE && layout_desc_container != linearLayoutPrevious) {
                        linearLayoutPrevious.setVisibility(View.GONE);
                        imageViewPrevious.setImageResource(R.drawable.next_arrow_orange);
                    }
                    linearLayoutPrevious = layout_desc_container;
                    imageViewPrevious = imgArrow;

                }
            });

            if (jobapplianceslist.get(i).getJob_appliances_customer_compalint().length() > 0)
                txtDisplaingError.setText(jobapplianceslist.get(i).getJob_appliances_customer_compalint());

            if (jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getType().length() > 0){
                txtRepairTypeText.setText(jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getType());
                if (jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getCalculatedBy().equals("FIXED")){
                    txtRepairTypeDollarText.setText("$"+jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getFixed_cost());
                }else {
                    float value = Float.parseFloat(jobapplianceslist.get(i).getInstallOrRepairModal().getWorkOrder().getHourly_rate()) * Float.parseFloat(jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getLabor_hours());
                    txtRepairTypeDollarText.setText("$"+value);
//                    txtRepairTypeDollarText.setText("$"+jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getPrice());
                }

            }



            if (jobapplianceslist.get(i).getJob_appliances_appliance_description().length() > 0)
                txtDescriptionTExt.setText(jobapplianceslist.get(i).getJob_appliances_appliance_description());

            ArrayList<Parts> partsArrayList = jobapplianceslist.get(i).getInstallOrRepairModal().getPartsContainer().getPartsArrayList();
            String parts_total =  "";
            String parts_desc =  "";
            for (int j = 0 ; j < partsArrayList.size() ; j++){
                float cost = Float.parseFloat(partsArrayList.get(j).getCost());
                float quantity = Float.parseFloat(partsArrayList.get(j).getQuantity());
                parts_total = parts_total + "$"+ (cost*quantity) +"\n";
                parts_desc = parts_desc + partsArrayList.get(j).getDescription() +"\n";
            }
            if (parts_desc.length() != 0)
            txtHomeandConnectionText.setText(parts_desc);
            txtPartsDollerTxxt.setText(parts_total);
            subtotal = subtotal +Float.parseFloat(jobapplianceslist.get(i).getInstallOrRepairModal().getWorkOrder().getSub_total());
            tax = tax + Float.parseFloat(jobapplianceslist.get(i).getInstallOrRepairModal().getWorkOrder().getTax());

            layout_workorder.addView(view);
        }
//        txtSubTotalDoller.setText("$"+subtotal);
//        txtTaxDoller.setText("$"+tax);
    }
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
