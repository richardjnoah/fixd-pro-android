package fixdpro.com.fixdpro.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

import fixdpro.com.fixdpro.R;

public class AddProTechnician_Activity extends AppCompatActivity {
    String selectedImagePathUser = null ;
    String  selectedImagePathDriver = null;
    boolean iscompleting = false ;
    HashMap<String,String> finalRequestParams = null ;
    boolean ispro = false ;

    ListView lstTechnicians ;
    ImageView imgClose;
    TextView txtYes, txtNO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pro_technician_);
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
        }
        setWidgets();
        setClickListner();
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
                finalRequestParams.put("[data][technicians][field_work]","1");
                Intent i = new Intent(AddProTechnician_Activity.this, BackgroundCheck_Activity.class);
                i.putExtra("ispro",ispro);
                i.putExtra("finalRequestParams",finalRequestParams);
                i.putExtra("iscompleting", iscompleting);
                i.putExtra("driver_image", selectedImagePathDriver);
                i.putExtra("user_image", selectedImagePathUser);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        txtNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalRequestParams.put("[data][technicians][field_work]","0");
                Intent i = new Intent(AddProTechnician_Activity.this, BackgroundCheck_Activity.class);
                i.putExtra("ispro",ispro);
                i.putExtra("finalRequestParams",finalRequestParams);
                i.putExtra("iscompleting", iscompleting);
                i.putExtra("driver_image", selectedImagePathDriver);
                i.putExtra("user_image", selectedImagePathUser);

                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }
}
