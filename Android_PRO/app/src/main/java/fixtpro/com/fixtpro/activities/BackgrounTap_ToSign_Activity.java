package fixtpro.com.fixtpro.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fixtpro.com.fixtpro.R;


public class BackgrounTap_ToSign_Activity extends AppCompatActivity {
    Context context = BackgrounTap_ToSign_Activity.this;
    public static final String TAG = "BackgrounTap_ToSign_Activity";
    ImageView imgClose, imgNext;
    TextView txtTapToSign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backgroun_tap__to_sign_);

        setWidgets();

        setClickListner();
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        txtTapToSign = (TextView) findViewById(R.id.txtTapToSign);

    }

    private void setClickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,AddTechnicion_Activity.class);
                startActivity(i);
            }
        });
        txtTapToSign.setOnClickListener(new View.OnClickListener() {
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
