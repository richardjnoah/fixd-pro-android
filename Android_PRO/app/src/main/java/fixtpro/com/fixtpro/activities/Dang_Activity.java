package fixtpro.com.fixtpro.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.utilites.CheckAlertDialog;


public class Dang_Activity extends AppCompatActivity {
    Context context = Dang_Activity.this;
    CheckAlertDialog checkAlertDialog;
    ImageView imgClose,imgSubmit;
    EditText txtEmail;
    String Email = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_);
        checkAlertDialog = new CheckAlertDialog();
        setWidgets();
        setClickListner();
    }

    private void setWidgets() {
        imgClose = (ImageView)findViewById(R.id.imgClose);
        imgSubmit = (ImageView)findViewById(R.id.imgSubmit);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
    }

    private void setClickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email = txtEmail.getText().toString();
                if (Email.equals("")){
                    checkAlertDialog.showcheckAlert(Dang_Activity.this,context.getResources().getString(R.string.app_name),"Please enter the email.");
                }else{

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
