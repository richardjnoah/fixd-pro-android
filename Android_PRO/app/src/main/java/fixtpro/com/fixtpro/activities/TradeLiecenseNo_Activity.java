package fixtpro.com.fixtpro.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import fixdpro.com.fixdpro.R;
import fixtpro.com.fixtpro.beans.SkillTrade;
import fixtpro.com.fixtpro.singleton.TradeSkillSingleTon;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.JSONParser;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class TradeLiecenseNo_Activity extends AppCompatActivity {
    Context context = TradeLiecenseNo_Activity.this;
    public static final String TAG = "TradeLiecenseNo_Activity";
    ImageView imgClose, imgNext;
    LinearLayout layout1 ;

    SharedPreferences _prefs = null ;
    Context _context = this ;
    String service_json_array = "[]";
    JSONArray service_array = null ;
    boolean ispro = false ;
    boolean iscompleting = false ;
    HashMap<String,String> finalRequestParams = null ;
    ArrayList<SkillTrade>  arrayList = TradeSkillSingleTon.getInstance().getList() ;
    String  selectedImagePathDriver = null;
    String selectedImagePathUser = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_liecense_no_);
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")){
                finalRequestParams = (HashMap<String,String>)bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")){
                ispro = bundle.getBoolean("ispro");
            }
            if (bundle.containsKey("iscompleting")){
                iscompleting = bundle.getBoolean("iscompleting");
            }
            if (bundle.containsKey("driver_image")){
                selectedImagePathDriver = bundle.getString("driver_image");
            } if (bundle.containsKey("user_image")){
                selectedImagePathUser = bundle.getString("user_image");
            }
        }
        _prefs = Utilities.getSharedPreferences(_context);
        service_json_array = _prefs.getString(Preferences.SERVICES_JSON_ARRAY, "[]");
        try {
            service_array = new JSONArray(service_json_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setWidgets();
        setCLickListner();
        setCheckedTradedSkill();
        setTradeLicenseNumbers();

    }
    private void setCheckedTradedSkill(){
        for (int i = 0 ; i < arrayList.size() ; i++){
            if ((isSelectedTradeSKill(arrayList.get(i).getId()+""))){
                arrayList.get(i).setIsChecked(true);
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
    private void setTradeLicenseNumbers(){
        if (arrayList.size() > 0){
            layout1.removeAllViews();
            setViews();
        }else {
            getTradeSkills();
        }
    }
    private void getTradeSkills(){
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                layout1.removeAllViews();
                setCheckedTradedSkill();
                if (arrayList.size() > 0) {
                    setViews();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(Constants.BASE_URL, "POST", getTradeSkillRequestParams());
                if (jsonObject != null) {
                    try {
                        String STATUS = jsonObject.getString("STATUS");
                        if (STATUS.equals("SUCCESS")) {
//                            JSONObject RESPONSE = jsonObject.getJSONObject("RESPONSE");
                            JSONArray results = jsonObject.getJSONArray("RESPONSE");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject object = results.getJSONObject(i);
                                SkillTrade skillTrade = new SkillTrade();
                                skillTrade.setId(object.getInt("id"));
                                skillTrade.setTitle(object.getString("name"));
                                arrayList.add(skillTrade);
                            }
                            TradeSkillSingleTon.getInstance().setList(arrayList);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return null;
            }
        }.execute();
    }

    private HashMap<String, String> getTradeSkillRequestParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "services");
        hashMap.put("select", "^*");
        hashMap.put("per_page", "999");
        hashMap.put("page", "1");
        hashMap.put("where[status]", "ACTIVE");
        return hashMap;
    }
    private void setViews(){
        for (int i = 0 ; i <arrayList.size() ; i++){

            if (arrayList.get(i).isChecked()){
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.trade_license_text_item, null);
                final EditText txtItem = (EditText)addView.findViewById(R.id.txtItem);
                final TextView txtName = (TextView)addView.findViewById(R.id.txtName);
                txtItem.setTag(i+"");
                txtItem.setHint(arrayList.get(i).getTitle());
                txtName.setText(arrayList.get(i).getTitle()+":");
                txtItem.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        arrayList.get(Integer.parseInt(txtItem.getTag().toString())).setNumber(txtItem.getText().toString().trim());
                    }
                });
                layout1.addView(addView);
            }

        }
    }

    private String getServiceName(String id){
        String title = "";
        for (int i = 0 ; i < arrayList.size() ; i++){
            if ((arrayList.get(i).getId()+"").equals(id)){
                title  = arrayList.get(i).getTitle();
                break;
            }
        }
        return title;
    }
    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        layout1 = (LinearLayout) findViewById(R.id.layout1);


    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkForEmptyValues()){
                    getFinalRequestParams();
                    if (ispro){
                        Intent i = new Intent(context,WorkingRadiusNew.class);
                        i.putExtra("ispro",ispro);
                        i.putExtra("iscompleting",iscompleting);
                        i.putExtra("finalRequestParams",finalRequestParams);
                        i.putExtra("driver_image", selectedImagePathDriver);
                        i.putExtra("user_image", selectedImagePathUser);
                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }else {
                        Intent i = new Intent(context,BackgroundCheck_Activity.class);
                        i.putExtra("ispro",ispro);
                        i.putExtra("finalRequestParams",finalRequestParams);
                        startActivity(i);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }else {
                    showAlertDialog("Fixd","Please enter Trade's License Number before proceeding");
                }

//                   Intent i = new Intent(context,BackgroundCheck_Next_Activity.class);
//                    startActivity(i);


            }
        });

    }
    private boolean checkForEmptyValues(){
        boolean isEmpty = false ;
        for (int i = 0 ; i < arrayList.size() ; i++){
            if (arrayList.get(i).isChecked()){
                if (arrayList.get(i).getNumber().length() == 0){
                    isEmpty = true;
                    break;
                }
            }
        }
        return isEmpty;
    }
    private void getFinalRequestParams(){
        for (int i = 0 ; i <arrayList.size() ; i++){
                if (arrayList.get(i).isChecked()){
                    finalRequestParams.put("data[trade_licenses]["+ i + "][service_id]",arrayList.get(i).getId()+"");
                    finalRequestParams.put("data[trade_licenses]["+ i + "][license_number]",arrayList.get(i).getNumber()+"");
                }


        }
    }
    private void showAlertDialog(String Title, String Message) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                TradeLiecenseNo_Activity.this);

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
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }
}
