package fixtpro.com.fixtpro.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fixtpro.com.fixtpro.R;


public class AddTechnicion_Activity extends AppCompatActivity {
    ImageView imgClose;
    TextView txtYes, txtNO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_technicion_);

        setWidgets();

        setClickListner();
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        txtYes = (TextView) findViewById(R.id.txtYes);
        txtNO = (TextView) findViewById(R.id.txtNO);
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
                startActivity(i);
            }
        });
        txtNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddTechnicion_Activity.this, LastStep_Activity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
