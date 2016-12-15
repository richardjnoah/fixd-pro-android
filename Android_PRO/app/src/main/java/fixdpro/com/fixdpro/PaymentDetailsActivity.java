package fixdpro.com.fixdpro;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.adapters.ApplianceReceipt;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

public class PaymentDetailsActivity extends AppCompatActivity {
    private Context context = PaymentDetailsActivity.this;
    private String TAG  = "PaymentDetailsActivity";
    private Typeface fontfamily;
    ImageView img_Cancel;
    TextView titletext,txtPayment,txtJobID,txtName,txtAddress,txtDateTime,txtArrivaltxt,txtArrivalTime,
            txtCompletedtxt,txtCompletedTime,txtSummary,txtTripCharges,txtTripChargesDoller, txtSubTotal,txtSubTotaldDoller,txtFixdFee,txtFixdFeeDoller,txtTotalEared,txtTotalEaredDoller;
    LinearLayout applianceReceiptContainer;
    AvailableJobModal availableJobModal = null ;
    ArrayList<ApplianceReceipt> applianceReceipts = new ArrayList<>();

    String miles_paid_amount = "0",error_message="",total ="0",subtotal = "0",fixd_cut ="0",fixd_cut_pers ="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
        setToolbar();
        setWidgets();
        setTypeface();
        if (getIntent() != null){
            availableJobModal = (AvailableJobModal)getIntent().getSerializableExtra("PaymentObject");
            setValues();
        }
        GetApiResponseAsyncNew responseAsyncCompleted = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", iHttpResponseListener,iHttpExceptionListener, PaymentDetailsActivity.this, "Loading");
        responseAsyncCompleted.execute(getRequestParams());
        
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    if (!miles_paid_amount.equals("0"))
                    {
                        txtTripChargesDoller.setText("$"+miles_paid_amount);
                    }else {
                        txtTripCharges.setVisibility(View.GONE);
                        txtTripChargesDoller.setVisibility(View.GONE);
                    }

                    LayoutInflater layoutInflater =
                            (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    for (int i = 0 ; i < applianceReceipts.size() ; i++){
                        final View itemView = layoutInflater.inflate(R.layout.item_receipt, null);
                        TextView txtRepairType= (TextView)itemView.findViewById(R.id.txtRepairType);
                        TextView txtRepairCost= (TextView)itemView.findViewById(R.id.txtRepairCost);
                        txtRepairType.setText(applianceReceipts.get(i).getAppliance_name());
                        txtRepairCost.setText("$" +applianceReceipts.get(i).getPro_sub_total());
                        applianceReceiptContainer.addView(itemView);
                    }

                    txtSubTotaldDoller.setText("$" + subtotal);

                    txtFixdFeeDoller.setText("$" + fixd_cut);
                    txtFixdFee.setText("Fixd Fee("+ fixd_cut_pers+"%)");
                    txtTotalEaredDoller.setText("$"+availableJobModal.getJob_line_items_pro_cut());
                    break;
                }case 1:{
                    break;
                }
            }
        }
    };
    private HashMap<String,String> getRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","details");
        hashMap.put("object","jobs");
        hashMap.put("data[job_id]",availableJobModal.getId());
        hashMap.put("token",Utilities.getSharedPreferences(this).getString(Preferences.AUTH_TOKEN,""));
        return hashMap;
    }
    IHttpResponseListener iHttpResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("",""+Response);
            try {
                if(Response.getString("STATUS").equals("SUCCESS")){
                    JSONObject jsonObject_job = Response.getJSONObject("RESPONSE").getJSONObject("job");
                    miles_paid_amount = Response.getJSONObject("RESPONSE").getString("miles_paid_amount");
                    total = Response.getJSONObject("RESPONSE").getString("pro_total");
                    subtotal = Response.getJSONObject("RESPONSE").getString("pro_subtotal");

                    JSONObject jsonObject_cuts = Response.getJSONObject("RESPONSE").getJSONObject("cuts");
                    fixd_cut = jsonObject_cuts.getString("fixd_cut");

                    JSONObject jsonObject_cuts_percentage = Response.getJSONObject("RESPONSE").getJSONObject("cuts_percentage");
                    fixd_cut_pers = jsonObject_cuts_percentage.getString("fixd_cut_percentage");

                    JSONObject jsonObject_appliances_reciepts =  Response.getJSONObject("RESPONSE").getJSONObject("appliances_reciepts");
                    Iterator<String> keys = jsonObject_appliances_reciepts.keys();

                    while(keys.hasNext()){
                        String key = keys.next();
                        ApplianceReceipt modal = new ApplianceReceipt();
                        modal.setAppliance_name(key);
                        modal.setPro_sub_total(jsonObject_appliances_reciepts.getJSONObject(key).getString("pro_subtotal"));
                        applianceReceipts.add(modal);
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

    IHttpExceptionListener iHttpExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {

        }
    };
    private  void setValues(){
        txtPayment.setText("$"+availableJobModal.getJob_line_items_pro_cut());
        txtJobID.setText("Job # "+availableJobModal.getId());
        txtName.setText(availableJobModal.getContact_name());
        txtAddress.setText(availableJobModal.getJob_customer_addresses_address()+" - "+availableJobModal.getJob_customer_addresses_city()+","+availableJobModal.getJob_customer_addresses_state());
        txtDateTime.setText(Utilities.convertDate(availableJobModal.getRequest_date()));
        String[] created = Utilities.getDate(availableJobModal.getCreated_at()).split(" ");
        String[] finished = Utilities.getDate(availableJobModal.getFinished_at()).split(" ");

        txtArrivalTime.setText(created[2]+ created[3]);
        txtCompletedTime.setText(finished[2]+ finished[3]);

    }
    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        img_Cancel = (ImageView)toolbar.findViewById(R.id.img_Cancel);
        img_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titletext = (TextView)toolbar.findViewById(R.id.titletext);
        titletext.setTypeface(fontfamily);

    }
    private void setWidgets(){
        txtPayment = (TextView)findViewById(R.id.txtPayment);
        txtJobID = (TextView)findViewById(R.id.txtJobID);
        txtName= (TextView)findViewById(R.id.txtName);
        txtAddress = (TextView)findViewById(R.id.txtAddress);
        txtDateTime= (TextView)findViewById(R.id.txtDateTime);
        txtArrivaltxt= (TextView)findViewById(R.id.txtArrivaltxt);
        txtArrivalTime= (TextView)findViewById(R.id.txtArrivalTime);
        txtCompletedtxt= (TextView)findViewById(R.id.txtCompletedtxt);
        txtCompletedTime = (TextView)findViewById(R.id.txtCompletedTime);
        txtSummary = (TextView)findViewById(R.id.txtSummary);
        txtTripCharges= (TextView)findViewById(R.id.txtTripCharges);
        txtTripChargesDoller = (TextView)findViewById(R.id.txtTripChargesDoller);
        applianceReceiptContainer = (LinearLayout)findViewById(R.id.layout_appliance_container);
        txtSubTotal= (TextView)findViewById(R.id.txtSubTotal);
        txtSubTotaldDoller= (TextView)findViewById(R.id.txtSubTotaldDoller);
        txtFixdFee= (TextView)findViewById(R.id.txtFixdFee);
        txtFixdFeeDoller = (TextView)findViewById(R.id.txtFixdFeeDoller);
        txtTotalEared= (TextView)findViewById(R.id.txtTotalEared);
        txtTotalEaredDoller= (TextView)findViewById(R.id.txtTotalEaredDoller);
    }
    private void setTypeface(){
//        txtPayment.setTypeface(fontfamily);
//        txtJobID.setTypeface(fontfamily);
//        txtName.setTypeface(fontfamily);
//        txtAddress.setTypeface(fontfamily);
//        txtDateTime.setTypeface(fontfamily);
//        txtArrivaltxt.setTypeface(fontfamily);
//        txtArrivalTime.setTypeface(fontfamily);
//        txtCompletedtxt.setTypeface(fontfamily);
//        txtCompletedTime.setTypeface(fontfamily);
//        txtSummary.setTypeface(fontfamily);
//        txtTripCharges.setTypeface(fontfamily);
//        txtTripChargesDoller.setTypeface(fontfamily);
//        txtRepairType.setTypeface(fontfamily);
//        txtRepairCost.setTypeface(fontfamily);
//        txtSubTotal.setTypeface(fontfamily);
//        txtSubTotaldDoller.setTypeface(fontfamily);
//        txtFixdFee.setTypeface(fontfamily);
//        txtFixdFeeDoller .setTypeface(fontfamily);
//        txtTotalEared.setTypeface(fontfamily);
//        txtTotalEaredDoller.setTypeface(fontfamily);
    }

}
