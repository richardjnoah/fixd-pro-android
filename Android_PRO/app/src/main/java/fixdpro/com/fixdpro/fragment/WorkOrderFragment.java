package fixdpro.com.fixdpro.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.CalendarActivity;
import fixdpro.com.fixdpro.CancelScheduledJob;
import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.ResponseListener;
import fixdpro.com.fixdpro.SignatureActivity;
import fixdpro.com.fixdpro.beans.NotificationModal;
import fixdpro.com.fixdpro.beans.install_repair_beans.RepairType;
import fixdpro.com.fixdpro.beans.install_repair_beans.WorkOrder;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WorkOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WorkOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    SharedPreferences _prefs = null ;
    Context _context = null ;
    CurrentScheduledJobSingleTon singleTon = null ;
    String error_message = "";
    WorkOrder workOrder ;
    RepairType repairType ;
    TextView txtDiagnosticDoller,txtSubTotalDoller,txtTaxDoller,txtTotalDoller,txtJob,txtJobType,txtUserNameAddress,txtRepairType,txtReapirTypeCost,partstxtType,partstxtDollerT, typeHeading,txtDiagnostic;
    EditText txtcomplaint,txtDescValue;
    Dialog dialog = null ;
    boolean isAutoNotiForWorkOrder = false ;
    long start_time = 0;
    ArrayList<Location> locations_list = new ArrayList<Location>();
    Location prevoiusLOcation = null ;
    GPSTracker gpsTracker = null ;
    Handler handler1 = null ;
    boolean isMileageCalulateCaptured  = false ;
    DecimalFormat df = new DecimalFormat("#.00");
    public WorkOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkOrderFragment newInstance(String param1, String param2) {
        WorkOrderFragment fragment = new WorkOrderFragment();
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
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
            isAutoNotiForWorkOrder = true ;
        }
        _context = getActivity();
        _prefs = Utilities.getSharedPreferences(_context);
        singleTon = CurrentScheduledJobSingleTon.getInstance();
        workOrder = singleTon.getJobApplianceModal().getInstallOrRepairModal().getWorkOrder();
        repairType = singleTon.getJobApplianceModal().getInstallOrRepairModal().getRepairType();
        gpsTracker = new GPSTracker(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_work_order, container, false);
        setWidgets(view);
        setListeners();
        GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync("POST",getWorkOrderListener,getActivity(),"Getting.");
        getApiResponseAsync.execute(getWorkOrderRequestParams());
        return view;
    }
    private void setWidgets(View view){
        txtcomplaint = (EditText)view.findViewById(R.id.txtcomplaint);
        txtDescValue = (EditText)view.findViewById(R.id.txtDescValue);
        txtRepairType =  (TextView)view.findViewById(R.id.txtRepairType);
        partstxtType =  (TextView)view.findViewById(R.id.partstxtType);
        partstxtDollerT =  (TextView)view.findViewById(R.id.partstxtDollerT);
        typeHeading =  (TextView)view.findViewById(R.id.typeHeading);
        txtReapirTypeCost =  (TextView)view.findViewById(R.id.txtReapirTypeCost);
        txtDiagnosticDoller = (TextView)view.findViewById(R.id.txtDiagnosticDoller);
        txtSubTotalDoller = (TextView)view.findViewById(R.id.txtSubTotalDoller);
        txtTaxDoller = (TextView)view.findViewById(R.id.txtTaxDoller);
        txtTotalDoller = (TextView)view.findViewById(R.id.txtTotalDoller);
        txtUserNameAddress = (TextView)view.findViewById(R.id.txtUserNameAddress);
        txtJobType = (TextView)view.findViewById(R.id.txtJobType);
        txtDiagnostic = (TextView)view.findViewById(R.id.txtDiagnostic);
        txtJob = (TextView)view.findViewById(R.id.txtJob);
        txtJob.setText("Job #"+singleTon.getCurrentJonModal().getId());
        txtJobType.setText(singleTon.getJobApplianceModal().getAppliance_type_name() + " - " + singleTon.getJobApplianceModal().getJob_appliances_service_type());
        txtUserNameAddress.setText((singleTon.getCurrentJonModal().getContact_name() +" - " +singleTon.getCurrentJonModal().getJob_customer_addresses_address()));
        txtcomplaint.setText(singleTon.getJobApplianceModal().getJob_appliances_customer_compalint());
//        txtDescValue.setText(singleTon.getJobApplianceModal().getJob_appliances_appliance_description());
        if (singleTon.getInstallOrRepairModal().getRepairType().getType().length() > 0)
        txtRepairType.setText( singleTon.getInstallOrRepairModal().getRepairType().getType());
        txtDescValue.setText(singleTon.getInstallOrRepairModal().getEquipmentInfo().getDescription());
        String parts = "" ;
        float cost = 0;
        for (int i = 0 ; i < singleTon.getInstallOrRepairModal().getPartsContainer().getPartsArrayList().size() ; i++){
            if (!(singleTon.getInstallOrRepairModal().getPartsContainer().getPartsArrayList().get(i).getDescription().length() == 0)){
                partstxtType.setText(singleTon.getInstallOrRepairModal().getPartsContainer().getPartsArrayList().get(i).getDescription()+"\n");
                parts = singleTon.getInstallOrRepairModal().getPartsContainer().getPartsArrayList().get(i).getDescription() +"," + parts;
            }


            if (!(singleTon.getInstallOrRepairModal().getPartsContainer().getPartsArrayList().get(i).getCost().length() == 0)){
                cost = cost + Float.parseFloat(singleTon.getInstallOrRepairModal().getPartsContainer().getPartsArrayList().get(i).getCost());
                float parts_Cost = Float.parseFloat(singleTon.getInstallOrRepairModal().getPartsContainer().getPartsArrayList().get(i).getCost()) * Float.parseFloat(singleTon.getInstallOrRepairModal().getPartsContainer().getPartsArrayList().get(i).getQuantity());
                partstxtDollerT.setText("$"+df.format(parts_Cost)+"" +"\n");
            }

        }
        typeHeading.setText(singleTon.getJobApplianceModal().getJob_appliances_service_type() +" Type");
    }
    private void setListeners(){

    }
    private HashMap<String,String> getWorkOrderRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","work_order");
        hashMap.put("object","job_appliances");
//        if (CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_service_type().equals("Install"))
//            hashMap.put("object","install_flow");
//        else
//            hashMap.put("object","repair_flow");
        hashMap.put("data[job_appliance_id]",singleTon.getJobApplianceModal().getJob_appliances_id());
        hashMap.put("token",_prefs.getString(Preferences.AUTH_TOKEN,""));
        return hashMap;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.WORK_ORDER_FRAGMENT);
        setupToolBar();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mGcmPushReceiver, new IntentFilter("gcm_push_notification_work_order_approved"));
    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).setRightToolBarText("Done");
        ((HomeScreenNew)getActivity()).setTitletext("Work Order");
        ((HomeScreenNew)getActivity()).setLeftToolBarText("Back");
    }
    private BroadcastReceiver mGcmPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            if (intent != null){
                NotificationModal notificationModal = (NotificationModal)intent.getSerializableExtra("data");
                if (notificationModal.getType().equals("woa")){
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
//                        Show work order approved dialog
                    showDialogWorkOrderApproved();
                }else {
                    showDialogWorkOrderResend();
                }
            }
        }
    };
    ResponseListener getWorkOrderListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject jsonObject) {
            Log.e("", "Response" + jsonObject);
            try {
                String STATUS = jsonObject.getString("STATUS");
                if (STATUS.equals("SUCCESS")){
                    JSONObject RESPONSE = jsonObject.getJSONObject("RESPONSE");
                    if (!RESPONSE.isNull("warranty_fee"))
                    workOrder.setWarranty_fee(RESPONSE.getString("warranty_fee"));
                    if (!RESPONSE.isNull("is_covered"))
                    workOrder.setIs_covered(RESPONSE.getBoolean("is_covered"));
                    if (!RESPONSE.isNull("diagnostic_fee"))
                    workOrder.setDisgnostic(RESPONSE.getString("diagnostic_fee"));
                    if (!RESPONSE.isNull("is_claim"))
                    workOrder.setIs_claim(RESPONSE.getString("is_claim"));
                    if (!RESPONSE.isNull("subtotal"))
                        workOrder.setSub_total(RESPONSE.getString("subtotal"));
                    if (!RESPONSE.isNull("tax"))
                        workOrder.setTax(RESPONSE.getString("tax"));
                    if (!RESPONSE.isNull("status"))
                        workOrder.setStatus(RESPONSE.getString("status"));
                    if (!RESPONSE.isNull("hourly_rate"))
                        workOrder.setHourly_rate(RESPONSE.getString("hourly_rate"));
                    if (!RESPONSE.isNull("total"))
                        workOrder.setTotal(RESPONSE.getString("total"));
                    handler.sendEmptyMessage(0);
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
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{

                    txtSubTotalDoller.setText("$"+workOrder.getSub_total());
                    txtTaxDoller.setText("$"+workOrder.getTax());
                    txtTotalDoller.setText("$"+workOrder.getTotal());
                    if (repairType.getCalculatedBy().equals("FIXED")){
                        txtReapirTypeCost.setText(repairType.getFixed_cost());
                    }else {
                        float value = Float.parseFloat(workOrder.getHourly_rate()) * Float.parseFloat(repairType.getLabor_hours());
                        txtReapirTypeCost.setText("$"+value);
                    }

                    if (isAutoNotiForWorkOrder){
                        submitPost();
                    }
                    if (workOrder.is_covered() && workOrder.getIs_claim().equals("1")){
                        txtSubTotalDoller.setPaintFlags(txtSubTotalDoller.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        txtTaxDoller.setPaintFlags(txtTaxDoller.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        txtDiagnosticDoller.setText("$"+workOrder.getWarranty_fee());
                        txtDiagnostic.setText("Warranty Fee:");

                    }else {
                        txtDiagnosticDoller.setText("$"+workOrder.getDisgnostic());
                    }
                    if (!workOrder.is_covered() && workOrder.getIs_claim().equals("1")){
                        showNotCoveredDialog();
                    }

                    break;
                }
                case 1:{
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }
                case 2:{
//                    show processing Dialog..
                    if (dialog != null ){
                        dialog.dismiss();
                    }
                    showhangTightDialog();

                    break;
                }
                default:{
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

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                mGcmPushReceiver);
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
    public void submitPost(){
        if (workOrder.getStatus().equals("APPROVED")){
            showDialogWorkOrderApproved();
        }else if (workOrder.getStatus().equals("DECLINED")){
                showDialogWorkOrderResend();
        }else {
            showCustomDialog();
        }
//        ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.WORK_ORDER_FRAGMENT);
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
                GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync("POST", getSendOwnerRequestResponse, getActivity(), "Getting.");
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

                        CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().setWorkOrder(workOrder);
                        Intent intent = new Intent(getActivity(), SignatureActivity.class);
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
                ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.WHATS_WRONG_FRAGMENT);
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        singleTon.getCurrentReapirInstallProcessModal().setIsCompleted(true);
//                        CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().setWorkOrder(workOrder);
//                        Intent intent = new Intent(getActivity(), SignatureActivity.class);
//                        startActivity(intent);
                        ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.WHATS_WRONG_FRAGMENT);
                    }
                }, 300);
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            singleTon.getCurrentReapirInstallProcessModal().setIsCompleted(true);
                            CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().setWorkOrder(workOrder);
                            Intent intent = new Intent(getActivity(), SignatureActivity.class);
                            CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().getSignature().setIsCompleted(true);
//                            startActivity(intent);
                            ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.WHATS_WRONG_FRAGMENT);
                        }
                    }, 300);
                }


            }
        });layout_reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                intent.putExtra("Rescheduling","1");
                startActivity(intent);
            }
        });layout_cancel_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), CancelScheduledJob.class);
                intent.putExtra("name",txtDiagnostic.getText().toString());
                intent.putExtra("fees",txtDiagnosticDoller.getText().toString());
                intent.putExtra("total",txtTotalDoller.getText().toString());
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
    }private void showDialogWorkOrderResend() {
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
                Intent intent = new Intent(getActivity(), CancelScheduledJob.class);
                startActivity(intent);
            }
        });
        layout_edit_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.WHATS_WRONG_FRAGMENT);
            }
        });
        resend_to_owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync("POST", getSendOwnerRequestResponse, getActivity(), "Getting.");
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
        hashMap.put("api","send_work_order_for_signature");
        hashMap.put("object","job_appliances");
//        if (CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_service_type().equals("Install"))
//            hashMap.put("object","install_flow");
//        else
//            hashMap.put("object","repair_flow");
        hashMap.put("data[job_appliance_id]",singleTon.getJobApplianceModal().getJob_appliances_id());
        hashMap.put("token",_prefs.getString(Preferences.AUTH_TOKEN,""));
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
                            GetApiResponseAsyncNoProgress responseAsync = new GetApiResponseAsyncNoProgress(Constants.BASE_URL, "POST", goingtogetPartsResponseListener, goingtogetPartsExceptionListener, getActivity(), "Loading");
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
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, ""));
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
        if (requestCode == 200){
            singleTon.getCurrentReapirInstallProcessModal().setIsCompleted(true);
            showDialogWorkOrderApproved();
        }
    }
}



