package fixtpro.com.fixtpro.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import fixtpro.com.fixtpro.R;
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
    HashMap<String,String> finalRequestParams = null ;

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
        }
        _prefs = Utilities.getSharedPreferences(_context);
        service_json_array = _prefs.getString(Preferences.SERVICES_JSON_ARRAY, "[]");
        try {
            service_array = new JSONArray(service_json_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setWidgets();
        layout1.removeAllViews();
        setViews();
        setCLickListner();
    }
    private void setViews(){
        for (int i = 0 ; i <service_array.length() ; i++){
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.trade_license_text_item, null);
            final EditText txtItem = (EditText)addView.findViewById(R.id.txtItem);
            try {
                txtItem.setTag(service_array.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            layout1.addView(addView);
        }
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
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(context,WorkingRadiusNew.class);
                    i.putExtra("ispro",ispro);
                    i.putExtra("finalRequestParams",finalRequestParams);
                    startActivity(i);
//                   Intent i = new Intent(context,BackgroundCheck_Next_Activity.class);
//                    startActivity(i);


            }
        });

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
        finish();
    }
}
