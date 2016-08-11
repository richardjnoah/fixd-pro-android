package fixtpro.com.fixtpro.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import fixtpro.com.fixtpro.R;


public class LastStep_Activity extends AppCompatActivity {
    Context context = LastStep_Activity.this;
    public static final String TAG = "LastStep_Activity";
    ImageView imgClose,imgFinish;
    TextView txtCheckforPrice,txtDoller,txtDiscountCode,txtApply,txtTotal,txtTotalDoller;
    EditText txtCardNumber,txtExpiration,txtSecurityCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_step_);

        setWidgets();

        setCLickListner();

    }

    private void setWidgets() {
        imgClose = (ImageView)findViewById(R.id.imgClose);
        imgFinish = (ImageView)findViewById(R.id.imgFinish);

        txtCheckforPrice =(TextView)findViewById(R.id.txtCheckforPrice);
        txtDoller =(TextView)findViewById(R.id.txtDoller);
        txtDiscountCode =(TextView)findViewById(R.id.txtDiscountCode);
        txtApply =(TextView)findViewById(R.id.txtApply);
        txtTotal =(TextView)findViewById(R.id.txtTotal);
        txtTotalDoller =(TextView)findViewById(R.id.txtTotalDoller);

        txtCardNumber =(EditText)findViewById(R.id.txtCardNumber);
        txtExpiration =(EditText)findViewById(R.id.txtExpiration);
        txtSecurityCode =(EditText)findViewById(R.id.txtSecurityCode);

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

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
