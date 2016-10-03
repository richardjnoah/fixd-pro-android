package fixtpro.com.fixtpro.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import fixtpro.com.fixtpro.R;

public class TermsAndConditions_Activity extends AppCompatActivity {
    Context context = TermsAndConditions_Activity.this;
    public static final String TAG = "TermsAndConditions_Activity";
    ImageView imgClose;
    TextView txtToolbar;
    WebView webviewTermCon;
    String strTermsCondtionsDealerService = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions_);

        /******Getting Intent****/
        gettingIntentString();

        /******Set Widget IDs******/
        setWidgetIDs();

        /*******ClickLIstner*******/
        setCLickListner();

        if (strTermsCondtionsDealerService.equals("Terms and Conditions")){
            txtToolbar.setText(R.string.termscondtiontextstr);
            webviewTermCon.clearView();
            webviewTermCon.loadUrl("file:///android_asset/fixd_technology_services_agreement.html");
        }else if (strTermsCondtionsDealerService.equals("Dealer Service Agreement")){
            txtToolbar.setText(R.string.dealerserviceagre);
            webviewTermCon.clearView();
            webviewTermCon.loadUrl("file:///android_asset/fixd_dealer_service_agreement.html");
        }else{
        }





    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter,R.anim.pop_exit);
        finish();
    }

    private void gettingIntentString() {
        Intent intent = getIntent();
        strTermsCondtionsDealerService = intent.getStringExtra("TermCondition_DealerServices");
    }

    private void setWidgetIDs() {
        imgClose = (ImageView)findViewById(R.id.imgClose);
        webviewTermCon = (WebView)findViewById(R.id.webviewTermCon);
        txtToolbar = (TextView)findViewById(R.id.txtToolbar);
    }
}
