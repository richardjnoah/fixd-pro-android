package fixtpro.com.fixtpro.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import fixtpro.com.fixtpro.R;

public class BackgroundCheck_Next_Activity extends AppCompatActivity {
    Context context = BackgroundCheck_Next_Activity.this;
    public static final String TAG = "BackgroundCheck_Next_Activity";
    ImageView imgClose, imgNext;
    CheckBox checkIacKnowlege;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_check__next_);

        setWidgets();

        setClickListner();
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);

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
               Intent i = new Intent(context,BackgrounTap_ToSign_Activity.class);
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
