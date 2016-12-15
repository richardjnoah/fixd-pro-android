package fixdpro.com.fixdpro;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fixdpro.com.fixdpro.beans.RatingListModal;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.CircularImageView;
import fixdpro.com.fixdpro.views.RatingBarView;

public class RatingDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView cancel;
    RatingListModal ratingListModal = null ;
    RatingBarView custom_ratingbar,customrating_tech ;
    public TextView txtKnowlageable,txtCourteous,txtAppearance;;
    TextView txtDispute,txtToolbar,txtJobId,txtUserName,txtAddress,txtDateTime,txtArrivalTime,txtCompleteTime,txtTechName,txtDetailss;
    Typeface fontfamily ;
    String versionName = "";
    int versionCode = 0;
    fixdpro.com.fixdpro.views.CircularImageView circleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_details);
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
        setToolbar();
        setWidgets();
        setTypeFace();
        if (getIntent() != null){
            ratingListModal = (RatingListModal)getIntent().getSerializableExtra("RatingObject");
            setValues();
        }

        try {
             versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
             versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        txtDispute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedbackemailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "dispute@fixdrepair.com", null));
                feedbackemailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fixd-Pro App Dispute (Ver : "+versionName + "), JobID : "+ ratingListModal.getJob_id());
                startActivity(Intent.createChooser(feedbackemailIntent, "Send email."));
            }
        });
    }

    private void setToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cancel = (ImageView)toolbar.findViewById(R.id.cancel);
        txtToolbar = (TextView)toolbar.findViewById(R.id.txtToolbar);
        txtToolbar.setTypeface(fontfamily);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void setWidgets(){
        custom_ratingbar = (RatingBarView)findViewById(R.id.custom_ratingbar);
        custom_ratingbar.setClickable(false);
        customrating_tech = (RatingBarView)findViewById(R.id.customrating_tech);
        customrating_tech.setClickable(false);

        txtDispute = (TextView)findViewById(R.id.txtDispute);
        txtJobId = (TextView)findViewById(R.id.txtJobId);
        txtUserName = (TextView)findViewById(R.id.txtUserName);
        txtAddress = (TextView)findViewById(R.id.txtAddress);
        txtDateTime = (TextView)findViewById(R.id.txtDateTime);
        txtArrivalTime = (TextView)findViewById(R.id.txtArrivalTime);
        txtCompleteTime = (TextView)findViewById(R.id.txtCompleteTime);
        txtTechName = (TextView)findViewById(R.id.txtTechName);
//        txtDetailss = (TextView)findViewById(R.id.txtDetailss);

        txtAppearance = (TextView) findViewById(R.id.txtAppearance);
        txtCourteous = (TextView) findViewById(R.id.txtCourteous);
        txtKnowlageable = (TextView) findViewById(R.id.txtKnowlageable);

        circleImage =(CircularImageView)findViewById(R.id.circleImage);
    }
    private void setTypeFace(){
//        txtJobId.setTypeface(fontfamily);
//        txtUserName.setTypeface(fontfamily);
//        txtAddress.setTypeface(fontfamily);
//        txtDateTime.setTypeface(fontfamily);
//        txtArrivalTime.setTypeface(fontfamily);
//        txtCompleteTime.setTypeface(fontfamily);
//        txtTechName.setTypeface(fontfamily);
//        txtDetailss.setTypeface(fontfamily);
    }
    private void setValues(){
        txtJobId.setText("Job # "+ ratingListModal.getJob_id());
        txtUserName.setText(ratingListModal.getCustomers_first_name() +" " +ratingListModal.getCustomers_last_name());
        txtAddress.setText(ratingListModal.getJobs_job_customers_addresses_address() +" - "+ratingListModal.getJobs_job_customers_addresses_city() +"," +ratingListModal.getJobs_job_customers_addresses_state());
        txtDateTime.setText(Utilities.convertDate(ratingListModal.getJobs_request_date()));
        if (!ratingListModal.getJobs_started_at().equals("0000-00-00 00:00:00")){
            String date[] = Utilities.getDate(ratingListModal.getJobs_started_at()).split(" ");
            txtArrivalTime.setText(date[2] + date[3]);
        }else {
            txtArrivalTime.setText("(null)");
        }
        if (!ratingListModal.getJobs_finished_at().equals("0000-00-00 00:00:00")){
            String date[] = Utilities.getDate(ratingListModal.getJobs_finished_at()).split(" ");
            txtCompleteTime.setText(date[2] + date[3]);
        }else {
            txtCompleteTime.setText("(null)");
        }


        txtTechName.setText(ratingListModal.getJobs_technilcians_first_name() + " " + ratingListModal.getJobs_technilcians_last_name());
//        txtDetailss.setText(ratingListModal.getComments());
//
        custom_ratingbar.setStar((int) (Float.parseFloat(ratingListModal.getRatings())), true);
//        customrating_tech.setStar((int) (Float.parseFloat(ratingListModal.getJobs_technilcians_avg_rating())), true);
        txtKnowlageable.setText(ratingListModal.getKnowledgeable());
        txtAppearance.setText(ratingListModal.getAppearance());
        txtCourteous.setText(ratingListModal.getCourteous());
        Float avd_rating  = Float.parseFloat(ratingListModal.getKnowledgeable()) + Float.parseFloat(ratingListModal.getCourteous()) + Float.parseFloat(ratingListModal.getAppearance()) ;
        avd_rating = avd_rating / 3 ;
        customrating_tech.setStar((int) Math.round(avd_rating), true);
        if (ratingListModal.getJobs_technilcians_img_original().length() > 0)
        Picasso.with(this).load(ratingListModal.getJobs_technilcians_img_original()).into(circleImage);
    }
}
