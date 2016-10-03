package fixtpro.com.fixtpro.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import fixtpro.com.fixtpro.Add_TechScreen;
import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.adapters.TechnicianAdapter;
import fixtpro.com.fixtpro.beans.TechnicianModal;
import fixtpro.com.fixtpro.singleton.TechniciansListSinglton;


public class AddTechnicion_Activity extends AppCompatActivity {
    ImageView imgClose;
    TextView txtYes, txtNO;
    boolean ispro = false ;
    HashMap<String,String> finalRequestParams = null ;
    String selectedImagePathUser = null ;
    String  selectedImagePathDriver = null;
    String  selectedImagePathSignature = null;
    boolean iscompleting = false ;
    TechnicianAdapter adapter ;
    ListView lstTechnicians ;
    ArrayList<TechnicianModal> technicianModalsList = TechniciansListSinglton.getInstance().technicianModalsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_technicion_);
        setWidgets();
        setClickListner();
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
            }
            if (bundle.containsKey("user_image")){
                selectedImagePathUser = bundle.getString("user_image");
            }
            if (bundle.containsKey("user_image_sign")){
                selectedImagePathSignature = bundle.getString("user_image_sign");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new TechnicianAdapter(AddTechnicion_Activity.this,technicianModalsList,getResources());
        lstTechnicians.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        txtYes = (TextView) findViewById(R.id.txtYes);
        txtNO = (TextView) findViewById(R.id.txtNO);
        lstTechnicians = (ListView) findViewById(R.id.lstTechnicians);
    }

    private void setClickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddTechnicion_Activity.this, TechnicianInformation_Activity.class);
                i.putExtra("ispro",ispro);
                i.putExtra("finalRequestParams",finalRequestParams);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        txtNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddTechnicion_Activity.this, LastStep_Activity.class);
                i.putExtra("ispro",ispro);
                i.putExtra("finalRequestParams",finalRequestParams);
                i.putExtra("iscompleting", iscompleting);
                i.putExtra("driver_image", selectedImagePathDriver);
                i.putExtra("user_image", selectedImagePathUser);
                i.putExtra("user_image_sign", selectedImagePathSignature);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }
}
