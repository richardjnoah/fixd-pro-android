package fixdpro.com.fixdpro;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fixdpro.com.fixdpro.adapters.HorizontalScrollApplianceAdapter;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.HorizontalListView;
import fixdpro.com.fixdpro.views.RatingBarView;


public class JobCompletedActivity extends AppCompatActivity {

    private Context context = JobCompletedActivity.this;
    private String TAG = "JobCompletedActivity";
    private Toolbar toolbar;
    private ImageView cancel;
    private Typeface fontfamily;
    private TextView txtToolbar,txtName,txtAddress,txtDateTime,txtArrivaltime,txtCompletedTime,txtTechName,txtTotalJobScheduled,txt8,txtTotal,txtServceTicket;
    private de.hdodenhof.circleimageview.CircleImageView circleImage;
    private RatingBarView custom_ratingbar_tech;
    private AvailableJobModal availableJobModal = null ;
    ArrayList<JobAppliancesModal> jobapplianceslist = null ;
    private HorizontalListView mHlvCustomList;
    HorizontalScrollApplianceAdapter horizontalScrollApplianceAdapter = null;
    LinearLayout appliance_layout ;
    ArrayList<Bitmap> arrayList = new ArrayList<>();
    int count = 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_completed);
        setToolBar();
        setWidgets();
        setTypeface();
        setClickListner();
        if (getIntent() != null){
            availableJobModal = (AvailableJobModal)getIntent().getSerializableExtra("CompletedJobObject");
            jobapplianceslist = availableJobModal.getJob_appliances_arrlist();
            Log.e(TAG,"jobapplianceslist+++++++++++++"+jobapplianceslist.size());
            Log.e(TAG,"jobapplianceslist+++++++++++++"+jobapplianceslist.toString());
//            horizontalScrollApplianceAdapter = new HorizontalScrollApplianceAdapter(this,jobapplianceslist);
//            mHlvCustomList.setAdapter(horizontalScrollApplianceAdapter);
            setValues();
            appliance_layout.removeAllViews();
//            getBitamap();
            showLayout();
        }
    }
    private void showLayout(){

        for (int i = 0 ; i < jobapplianceslist.size() ; i++){
            LayoutInflater layoutInflater =
                    (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.jobs_image_title_item, null);
            ImageView imageView = (ImageView)addView.findViewById(R.id.imgType);
            TextView txtTypeTitle = (TextView)addView.findViewById(R.id.txtTypeTitle);
            txtTypeTitle.setText(jobapplianceslist.get(i).getAppliance_type_name());
            if (Utilities.getApplianceImageByName(jobapplianceslist.get(i).getAppliance_type_name()) != -1){
                imageView.setImageResource(Utilities.getApplianceImageByName(jobapplianceslist.get(i).getAppliance_type_name()));
            }else {
                if (jobapplianceslist.get(i).getAppliance_type_image_original().length() > 0){
                    Picasso.with(context).load(jobapplianceslist.get(i).getAppliance_type_image_original()).into(imageView);
                }
            }


            appliance_layout.addView(addView);
        }
    }
    private void getBitamap(){
        try{
            if (jobapplianceslist.get(count).getAppliance_type_image_original().length() > 0){
                Picasso.with(this)
                        .load(jobapplianceslist.get(count).getAppliance_type_image_original()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        arrayList.add(bitmap);
                        ++count;
                        if (count < jobapplianceslist.size()){
                            getBitamap();
                        }else {
                            showLayout();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }else {
                ++count;
                if (count < jobapplianceslist.size()){
                    getBitamap();
                }else {
                    showLayout();
                }
            }

        }catch (Exception e){

        }

    }
    private void setValues(){
        appliance_layout = (LinearLayout)findViewById(R.id.layout_appliances);
        txtName.setText(availableJobModal.getContact_name());
        txtAddress.setText(availableJobModal.getJob_customer_addresses_address()+" - "+availableJobModal.getJob_customer_addresses_city()+","+availableJobModal.getJob_customer_addresses_state());
        txtDateTime.setText(Utilities.convertDate(availableJobModal.getRequest_date()));
        String dateTimeCreated[] = Utilities.getDate(availableJobModal.getCreated_at()).split(" ");
        String dateTimeFinished[] = Utilities.getDate(availableJobModal.getFinished_at()).split(" ");
//        txtArrivaltime.setText("Arrival Time: "+Utilities.getFormattedTimeSlots(availableJobModal.getTimeslot_start()));
        txtArrivaltime.setText("Arrival Time: "+dateTimeCreated[2] + dateTimeCreated[3]);
//        txtCompletedTime.setText("Completed Time: "+Utilities.getFormattedTimeSlots(availableJobModal.getTimeslot_end()));
        txtCompletedTime.setText("Completed Time: "+dateTimeFinished[2] + dateTimeFinished[3]);
        txtTechName.setText(availableJobModal.getTechnician_fname() +" "+availableJobModal.getTechnician_lname());
        txtTotalJobScheduled.setText(availableJobModal.getTechnician_fname() +" has" +" 0" +" jobs scheduled at this time." );
        txtTotal.setText("Total................$" + availableJobModal.getJob_line_items_pro_cut());
        custom_ratingbar_tech.setStar((int)Float.parseFloat(availableJobModal.getTechnician_avg_rating()),true);
        if (availableJobModal.getTechnician_profile_image().length() > 0)
        Picasso.with(this).load(availableJobModal.getTechnician_profile_image()).into(circleImage);
    }
    private void setClickListner() {
        txtServceTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,ServiceTicketActivity.class);
                i.putExtra("modal",availableJobModal);
                startActivity(i);
            }
        });
    }
    private void setTypeface() {
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");

        txtToolbar.setTypeface(fontfamily);
        txtName.setTypeface(fontfamily);
        txtAddress.setTypeface(fontfamily);
        txtDateTime.setTypeface(fontfamily);
        txtArrivaltime.setTypeface(fontfamily);
        txtCompletedTime.setTypeface(fontfamily);
        txtTechName.setTypeface(fontfamily);
        txtTotalJobScheduled.setTypeface(fontfamily);
        txt8.setTypeface(fontfamily);
        txtTotal.setTypeface(fontfamily);

        txtServceTicket.setTypeface(fontfamily);

    }

    private void setWidgets() {

        txtName = (TextView)findViewById(R.id.txtName);
        txtAddress = (TextView)findViewById(R.id.txtAddress);
        txtDateTime = (TextView)findViewById(R.id.txtDateTime);
        txtArrivaltime = (TextView)findViewById(R.id.txtArrivaltime);
        txtCompletedTime = (TextView)findViewById(R.id.txtCompletedTime);
        txtTechName = (TextView)findViewById(R.id.txtTechName);
        txtTotalJobScheduled = (TextView)findViewById(R.id.txtTotalJobScheduled);
        txt8 = (TextView)findViewById(R.id.txt8);
        txtTotal = (TextView)findViewById(R.id.txtTotal);

        txtServceTicket = (TextView)findViewById(R.id.txtServceTicket);

        custom_ratingbar_tech = (RatingBarView)findViewById(R.id.custom_ratingbar_tech);
        custom_ratingbar_tech.setClickable(false);

        mHlvCustomList = (HorizontalListView) findViewById(R.id.hlvImageList);
        circleImage = (CircleImageView)findViewById(R.id.circleImage);
    }

    private void setToolBar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cancel = (ImageView)toolbar.findViewById(R.id.cancel);
        txtToolbar = (TextView)toolbar.findViewById(R.id.txtToolbar);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
