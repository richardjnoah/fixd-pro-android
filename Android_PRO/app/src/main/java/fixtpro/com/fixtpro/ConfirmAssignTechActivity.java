package fixtpro.com.fixtpro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import fixtpro.com.fixtpro.beans.AssignTechModal;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsync;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

public class ConfirmAssignTechActivity extends AppCompatActivity {
    AssignTechModal modal = null ;
    TextView textChange,done;
    SharedPreferences _prefs = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_assign_tech);
        getSupportActionBar().hide();
        _prefs = Utilities.getSharedPreferences(this);
        setWidgets();
        setListeners();
        if (getIntent() != null){
            modal = (AssignTechModal)getIntent().getSerializableExtra("TechInfo");
        }
    }
    private void setWidgets(){
        textChange = (TextView)findViewById(R.id.textChange);
        done = (TextView)findViewById(R.id.done);
    }
    private void setListeners(){
        textChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST",assignTechListener,ConfirmAssignTechActivity.this,"Assigning");
                responseAsync.execute(getAssignTechParametrs());
            }
        });
    }
    private HashMap<String,String> getAssignTechParametrs(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "assign");
        hashMap.put("object", "jobs");
        hashMap.put("data[technician_id]", modal.getTechId());
        hashMap.put("data[id]", FixdProApplication.SelectedAvailableJobId);
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN,""));
        return hashMap;
    }
    ResponseListener assignTechListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("","Response"+Response);
        }
    };
}
