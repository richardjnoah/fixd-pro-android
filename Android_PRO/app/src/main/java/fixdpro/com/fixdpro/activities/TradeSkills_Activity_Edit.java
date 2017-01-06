package fixdpro.com.fixdpro.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.adapters.TradeSkillAdapter;
import fixdpro.com.fixdpro.beans.SkillTrade;
import fixdpro.com.fixdpro.net.GetApiResponseAsync;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.singleton.TradeSkillSingleTon;
import fixdpro.com.fixdpro.utilites.CheckAlertDialog;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.MultipartUtility;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

public class TradeSkills_Activity_Edit extends AppCompatActivity {
    Context context = this;
    ArrayList<SkillTrade> skillTrades;
    SkillTrade selectedTradeSkill = null;
    TradeSkillAdapter tradeSkillAdapter = null;
    SharedPreferences _prefs = null;

    ImageView imgClose, imgFinish;
    GridView gridView;
    TextView txtFinish;
    String error_message = "";
    CheckAlertDialog checkALert;
    HashMap<String,String> finalRequestParams = null ;
    boolean ispro = false ;
    boolean iscompleting = false ;
    MultipartUtility multipart = null;
    Dialog progressDialog;
    String  selectedImagePathDriver = null;
    String selectedImagePathUser = null ;
    String service_json_array = "[]";
    JSONArray service_array = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_skills__activity__edit);
        checkALert = new CheckAlertDialog();
        skillTrades = TradeSkillSingleTon.getInstance().getList();
        _prefs = Utilities.getSharedPreferences(context);
        setWidgets();
        setCLickListner();
        service_json_array = _prefs.getString(Preferences.SERVICES_JSON_ARRAY, "[]");
        try {
            service_array = new JSONArray(service_json_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (skillTrades.size() == 0) {
            GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync(Constants.BASE_URL,"POST", getTradeSkillListener,iHttpExceptionListener, TradeSkills_Activity_Edit.this, "Getting.");
            getApiResponseAsync.execute(getTradeSkillRequestParams());
        } else {
            setCheckedTradedSkill();
            setListAdapter();
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTradeSkill = skillTrades.get(position);
                if (selectedTradeSkill.isChecked()) {
                    skillTrades.get(position).setIsChecked(false);
                } else {
                    skillTrades.get(position).setIsChecked(true);
                }
                tradeSkillAdapter.notifyDataSetChanged();
            }
        });
    }
    private void setCheckedTradedSkill(){
        for (int i = 0 ; i < skillTrades.size() ; i++){
            if ((isSelectedTradeSKill(skillTrades.get(i).getId()+""))){
                skillTrades.get(i).setIsChecked(true);
            }
        }
    }
    private boolean isSelectedTradeSKill(String id){
        boolean isselected = false ;
        for (int i = 0 ; i < service_array.length() ; i++){
            try {
                if (service_array.get(i).equals(id)){
                    isselected = true ;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return isselected ;
    }
    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgFinish = (ImageView) findViewById(R.id.imgFinish);
        gridView = (GridView) findViewById(R.id.gridView);
        txtFinish = (TextView) findViewById(R.id.txtFinish);
    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(TradeSkills_Activity_Edit.this,TradeLiecenseNo_Activity_Edit.class);

                    intent.putExtra("list",skillTrades);
                    startActivity(intent);
                    finish();
            }
        });
    }
    IHttpExceptionListener iHttpExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception ;
            handler.sendEmptyMessage(1);
        }
    };
    IHttpResponseListener getTradeSkillListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response);
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    JSONArray jsonArray = Response.getJSONArray("RESPONSE");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        SkillTrade modal = new SkillTrade();
                        modal.setId(Integer.parseInt(jsonObj.getString("id")));
                        modal.setTitle(jsonObj.getString("name"));
                        modal.setIsChecked(false);

                        skillTrades.add(modal);
                    }
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            switch (msg.what) {


                case 0: {
                    setListAdapter();
                    break;
                }
                case 1:{
                    checkALert.showcheckAlert(TradeSkills_Activity_Edit.this, getResources().getString(R.string.app_name), error_message);
                    break;
                }
                case 2:{
                    Intent intent = new Intent(TradeSkills_Activity_Edit.this, HomeScreenNew.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;
                }
            }
        }
    };

    private HashMap<String, String> getTradeSkillRequestParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "services");
        hashMap.put("select", "^*");
        hashMap.put("per_page", "999");
        hashMap.put("page", "1");
        hashMap.put("where[status]", "ACTIVE");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setListAdapter() {
        tradeSkillAdapter = new TradeSkillAdapter(this, skillTrades, getResources());
        gridView.setAdapter(tradeSkillAdapter);
    }
    private HashMap<String,String> getProRegisterParameters(){

        for (int i = 0 ; i < TradeSkillSingleTon.getInstance().getListChecked().size() ; i++){
            finalRequestParams.put("data[services]["+i+"]",TradeSkillSingleTon.getInstance().getListChecked().get(i).getId()+"");
        }
        return finalRequestParams;
    }
}
