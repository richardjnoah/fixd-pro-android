package fixdpro.com.fixdpro;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.beans.NotificationModal;
import fixdpro.com.fixdpro.beans.install_repair_beans.Parts;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNoProgress;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.GPSTracker;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.CircularProgressView;

public class NewWorkOrderActivity extends AppCompatActivity {
    private Context context = NewWorkOrderActivity.this;
    private String TAG = "NewWorkOrderActivity";
    private Toolbar toolbar;
    private ImageView cancel;
    Dialog dialog;
    private Typeface fontfamily,italicfontfamily;
    ArrayList<AvailableJobModal> completedjoblist  = new ArrayList<AvailableJobModal>();
    String jobId = "";
    TextView txtToolbar,txtDone,txtName,txtAddresss,txtSubTotalDoller,txtJobId,txtTaxDoller,txtBack,txtWarrenty,txtWarrentyDoller,txtTotal,txtTotalDoller;
//    TextView txtCustomerComplaint,txtDisplaingError,txtDescriptionOfWork,txtDescriptionTExt,txtRepairType,txtRepairTypeText,txtPartsType,txtHomeandConnectionText,txtPartsDollerTxxt,txtApplianceName;

    String nextComplete = "null";
    String error_message =  "";
    Context _context = this;
    LinearLayout layoutAppliances,layoutPartsUsed ;
    LayoutInflater inflater = null;
    float Jobtotal = 0;
    AvailableJobModal availableJobModal = CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal();

    boolean isWorkorderShown = false;
    LinearLayout layout_workorder;
    LinearLayout linearLayoutPrevious =  null ;
    ImageView imageViewPrevious = null ;
    String workOrderStatus = "";
    long start_time = 0;
    ArrayList<Location> locations_list = new ArrayList<Location>();
    Location prevoiusLOcation = null ;
    GPSTracker gpsTracker = null ;
    Handler handler1 = null ;
    boolean isMileageCalulateCaptured  = false ;
    DecimalFormat df = new DecimalFormat("#.00");

    ArrayList<JobAppliancesModal> jobapplianceslist = new ArrayList<JobAppliancesModal>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_work_order);
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setToolBar();

        setWidgets();

        setListeners();

        setTypeface();

        initValue();

        initLayout();

        getWorkOrder();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mGcmPushReceiver, new IntentFilter("gcm_push_notification_work_order_approved"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mGcmPushReceiver);
    }

    private BroadcastReceiver mGcmPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            if (intent != null){
                NotificationModal notificationModal = (NotificationModal)intent.getSerializableExtra("data");
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                if (notificationModal.getType().equals("woafj")){
                    availableJobModal.setIsWorkOrderApproved("1");
                    showDialogWorkOrderApproved();
                }else {
                    showDialogWorkOrderResend();
                }
            }
        }
    };

    private void initValue(){
        jobId = availableJobModal.getId();
        txtName.setText(availableJobModal.getContact_name());
        txtJobId.setText("Job#"+jobId  );
        txtSubTotalDoller.setText("$"+availableJobModal.getJob_line_items_sub_total());
        txtWarrentyDoller.setText("$"+availableJobModal.getJob_line_items_diagnostic_fee());
        txtTaxDoller.setText("$"+availableJobModal.getJob_line_items_tax());
        txtTotalDoller.setText("$"+availableJobModal.getJob_line_items_pro_cut());
        txtAddresss.setText(availableJobModal.getJob_customer_addresses_address() + " " + availableJobModal.getJob_customer_addresses_address_2());

        if (availableJobModal.getIs_fe_job().equals("0") && availableJobModal.getIs_claim().equals("1") && availableJobModal.getJob_line_items_is_covered().equals("1")){
            txtWarrenty.setText("Warrenty");
            txtSubTotalDoller.setPaintFlags(txtSubTotalDoller.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


    }

    private void getWorkOrder(){
        GetApiResponseAsync responseAsyncCompleted = new GetApiResponseAsync("POST", responseListenerWorkOrder, this, "Loading");
        responseAsyncCompleted.execute(getRequestParams(jobId));
    }

    private void setListeners(){
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtnClicked();
            }
        });

    }


    ResponseListener responseListenerWorkOrder = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    JSONObject response = Response.getJSONObject("RESPONSE");
                    JSONArray applianceArray = response.getJSONArray("appliances");

                    workOrderStatus = response.getString("work_order_status");
                    availableJobModal.setJob_line_items_diagnostic_fee(response.getString("diagnostic_fee"));
                    availableJobModal.setJob_line_items_sub_total(response.getString("sub_total"));
                    availableJobModal.setJob_line_items_tax(response.getString("tax"));
                    availableJobModal.setJob_line_items_pro_cut(response.getString("total"));

        //         mNotCoveredView.setVisibility(View.VISIBLE);

                    String isWorkOrderApproved = "1";
                    if (!workOrderStatus.equals("APPROVED")){
                        isWorkOrderApproved = "0";
                    }
                    for (int i=0; i<availableJobModal.getJob_appliances_arrlist().size();i++){
                        JobAppliancesModal appliancesModal = availableJobModal.getJob_appliances_arrlist().get(i);
                        for (int j=0; j<applianceArray.length();j++){
                            if (applianceArray.getJSONObject(j).getString("id").equals(appliancesModal.getJob_appliances_id())){
                                JSONObject costDict = applianceArray.getJSONObject(j).getJSONObject("cost");
                                if (costDict.getString("is_claim").equals("1") && costDict.getString("is_covered").equals("0")){
//                                    mNotCoveredView.setVisibility(View.VISIBLE);

                                }
                                break;
                            }
                        }
                    }

                    availableJobModal.setIsWorkOrderApproved(isWorkOrderApproved);
                    if (isWorkOrderApproved.equals(1)){
                        showDialogWorkOrderApproved();
                    }

                    handler.sendEmptyMessage(0);
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

        txtDone = (TextView)findViewById(R.id.txtDone);
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
        hashMap.put("api", "work_order");
        hashMap.put("object","jobs");
        hashMap.put("data[job_id]", Jobid);
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));
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
                    //initLayout();
                    initValue();
                    break;
                }
                case 1:{
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }
                case 2:{
                    if (dialog != null ){
                        dialog.dismiss();
                    }
                    showhangTightDialog();
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
        jobapplianceslist = availableJobModal.getJob_appliances_arrlist();
        layout_workorder.removeAllViews();
        float tax =0;
        float subtotal =0;

        for (int i = 0 ; i < jobapplianceslist.size() ; i++){
            if(jobapplianceslist.get(i).isCanceled()){
                continue;
            }
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

            txtRepairTypeText.setText(jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getType());
            if (jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getCalculatedBy().equals("FIXED")){
                txtRepairTypeDollarText.setText("$"+jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getFixed_cost());
            }else {
                float value = Float.parseFloat(jobapplianceslist.get(i).getInstallOrRepairModal().getWorkOrder().getHourly_rate()) * Float.parseFloat(jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getLabor_hours());
                txtRepairTypeDollarText.setText("$"+value);
//                    txtRepairTypeDollarText.setText("$"+jobapplianceslist.get(i).getInstallOrRepairModal().getRepairType().getPrice());
            }

            txtDescriptionTExt.setText(jobapplianceslist.get(i).getInstallOrRepairModal().getEquipmentInfo().getDescription());

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

    public void nextBtnClicked(){
        if (availableJobModal.getIsWorkOrderApproved().equals("1")){
            showDialogWorkOrderApproved();
        } else if(workOrderStatus.equals("DECLINED")){
            showDialogWorkOrderResend();
        } else {
            showCustomDialog();
        }
    }

    private void showCustomDialog() {
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_work_order_new);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        LinearLayout layout_complete_job = (LinearLayout)dialog.findViewById(R.id.layout_complete_job);
        LinearLayout send_to_owner = (LinearLayout)dialog.findViewById(R.id.send_to_owner);
        send_to_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Send to Owner Api..

                dialog.dismiss();
                GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync("POST", getSendOwnerRequestResponse, NewWorkOrderActivity.this, "Getting.");
                getApiResponseAsync.execute(getSendOwnerRequest());
            }
        });
        layout_complete_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

//                        CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().setWorkOrder(workOrder);
                        Intent intent = new Intent(NewWorkOrderActivity.this, SignatureActivity.class);
                        startActivityForResult(intent, 200);
                    }
                }, 300);
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void showhangTightDialog() {
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_owner_approval_wait);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CircularProgressView progressView = (CircularProgressView) dialog.findViewById(R.id.progress_view);
        progressView.startAnimation();
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showTrackingMileageDialog() {
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tracking_mileage);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CircularProgressView progressView = (CircularProgressView) dialog.findViewById(R.id.progress_view);
        progressView.startAnimation();
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        Button btnReturn = (Button)dialog.findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isMileageCalulateCaptured = true ;
//                ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.WHATS_WRONG_FRAGMENT);
                finish();
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showDialogWorkOrderApproved() {
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_workorder);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout  layout_complete_job = (LinearLayout)dialog.findViewById(R.id.layout_complete_job);
        LinearLayout  layout_gettingParts = (LinearLayout)dialog.findViewById(R.id.layout_gettingParts);
        LinearLayout  layout_reschedule = (LinearLayout)dialog.findViewById(R.id.layout_reschedule);
        LinearLayout  layout_cancel_job = (LinearLayout)dialog.findViewById(R.id.layout_cancel_job);
        layout_complete_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showNextStepPopup();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.dismiss();
////                        singleTon.getCurrentReapirInstallProcessModal().setIsCompleted(true);
//                        finish();
//                    }
//                }, 300);
            }
        });
        layout_gettingParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getIs_claim().equals("1")){
                    start_time = System.currentTimeMillis();
                    collectDataForGoingTogetParts();
                    showTrackingMileageDialog();
                }else {
                    showNextStepPopup();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            dialog.dismiss();
////                            singleTon.getCurrentReapirInstallProcessModal().setIsCompleted(true);
////                            CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().setWorkOrder(workOrder);
////                            CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().getSignature().setIsCompleted(true);
////                            ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.WHATS_WRONG_FRAGMENT);
//                            finish();
//                        }
//                    }, 300);
                }


            }
        });
        layout_reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(NewWorkOrderActivity.this, CalendarActivity.class);
                intent.putExtra("Rescheduling","1");
                startActivity(intent);
            }
        });
        layout_cancel_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(NewWorkOrderActivity.this, CancelScheduledJob.class);
                intent.putExtra("name",txtWarrenty.getText().toString());
                intent.putExtra("fees",txtWarrentyDoller.getText().toString());
                intent.putExtra("total", txtTotalDoller.getText().toString());
                intent.putExtra("is_whole_cancel", true);
                startActivity(intent);
            }
        });
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void showDialogWorkOrderResend() {
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_work_order_resend_owner);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout  layout_edit_job = (LinearLayout)dialog.findViewById(R.id.layout_edit_job);
        LinearLayout  resend_to_owner = (LinearLayout)dialog.findViewById(R.id.resend_to_owner);
        LinearLayout  layout_cancel_job = (LinearLayout)dialog.findViewById(R.id.layout_cancel_job);
        layout_cancel_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(NewWorkOrderActivity.this, CancelScheduledJob.class);
                intent.putExtra("is_whole_cancel", true);
                startActivity(intent);
            }
        });
        layout_edit_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.WHATS_WRONG_FRAGMENT);
            }
        });
        resend_to_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync("POST", getSendOwnerRequestResponse, NewWorkOrderActivity.this, "Getting.");
                getApiResponseAsync.execute(getSendOwnerRequest());
            }
        });
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private HashMap<String,String> getSendOwnerRequest(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "send_work_order_for_signature");
        hashMap.put("object", "jobs");
        hashMap.put("data[job_id]", jobId);
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }
    ResponseListener getSendOwnerRequestResponse = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject jsonObject) {
            Log.e("", "Response" + jsonObject);
            try {
                String STATUS = jsonObject.getString("STATUS");
                if (STATUS.equals("SUCCESS")){

                    handler.sendEmptyMessage(2);
                }else {
                    JSONObject errors = jsonObject.getJSONObject("ERRORS");
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

    private void showNotCoveredDialog(){
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_not_covered);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView img_close = (ImageView)dialog.findViewById(R.id.img_close);
        Button btnFinish = (Button)dialog.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void collectDataForGoingTogetParts(){

        // getting gps points every second automatically
        //make a local array to cache these gps points
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Location location = gpsTracker.getLocation();
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
                    if (!isMileageCalulateCaptured){
                        GetApiResponseAsyncNoProgress responseAsync = new GetApiResponseAsyncNoProgress(Constants.BASE_URL, "POST", goingtogetPartsResponseListener, goingtogetPartsExceptionListener, NewWorkOrderActivity.this, "Loading");
                        responseAsync.execute(getRequestParamsForGoingtogetParts());
                    }

                } else {
                    collectDataForGoingTogetParts();
                }
            }
        }, 60000);

    }

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
        hashMap.put("token", Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200){
//            singleTon.getCurrentReapirInstallProcessModal().setIsCompleted(true);
            availableJobModal.setIsWorkOrderApproved("1");
            showDialogWorkOrderApproved();
        }
    }

    public void showNextStepPopup(){
            dialog = new Dialog(_context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_next_step);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView txtStart = (TextView)dialog.findViewById(R.id.txtStart);
            txtStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
    }


}
