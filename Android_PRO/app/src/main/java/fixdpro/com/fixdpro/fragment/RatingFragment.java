package fixdpro.com.fixdpro.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.RatingDetailsActivity;
import fixdpro.com.fixdpro.ResponseListener;
import fixdpro.com.fixdpro.adapters.RatingListPageAdpater;
import fixdpro.com.fixdpro.beans.RatingListModal;
import fixdpro.com.fixdpro.utilites.CheckIfUserVarified;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsyncBatch;
import fixdpro.com.fixdpro.utilites.HandlePagingResponse;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.RatingBarView;


public class RatingFragment extends Fragment {
//    private TextView txtAverageRating,txtnumtotaljob,txtjobstxt,txtnumreviews,txtreviewsltxt;
    private ListView ratingListView;
//    private RatingBarView custom_ratingbar;
    private ImageView imgCenter;
    private Typeface fontfamily;
    private ArrayList<RatingListModal> modalList;
    RatingListPageAdpater adpater =  null ;
    static int page =  1 ;
    String TechId = "";
    SharedPreferences _prefs = null;
    Context _context = null ;
    ArrayList<RatingListModal> ratingListModalArrayList = new ArrayList<RatingListModal>();
    String error_message = "";
    String next = "null";
    static  int pageRating = 1 ;
    private boolean hasgotOverView = false ;
    String total_jobs = "",total_reviews = "0";
    RatingListPageAdpater adapter = null ;
    RatingBarView custom_ratingbar;
    TextView txtnumtotaljob,txtnumreviews;
    public RatingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = getActivity() ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rating, container, false);

        _prefs = Utilities.getSharedPreferences(_context);

        TechId = _prefs.getString(Preferences.ID, "");

        setWidgets(rootView);

        setTypeface();

        setClickListner();
        adapter = new RatingListPageAdpater(getActivity(), ratingListModalArrayList, getResources(), new HandlePagingResponse() {
            @Override
            public void handleChangePage() {
                if (!next.equals("null")) {
                    pageRating = Integer.parseInt(next);
                    GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", responseListenerRatings, getActivity(), "Loading");
                    responseAsync.execute(getRequestParamsRatings());
                }
            }
        });
        ratingListView.addHeaderView(addHeader());
        ratingListView.setAdapter(adapter);
//        if (!hasgotOverView){
            GetApiResponseAsyncBatch responseAsyncOverview= new GetApiResponseAsyncBatch("POST", responseListenerOverview, getActivity(), "Loading");
            responseAsyncOverview.execute(getRequestParamsRatingsOverview());

//        }

        return rootView;
    }



    ResponseListener responseListenerOverview = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if ((Response.getJSONObject("job_count").getString("STATUS")).equals("SUCCESS")){
                    hasgotOverView = true;
                    total_jobs = (Response.getJSONObject("job_count").getString("RESPONSE"));
                    if ((Response.getJSONObject("avg_rating").getString("STATUS")).equals("SUCCESS")){
                        total_reviews = (Response.getJSONObject("avg_rating").getJSONObject("RESPONSE").getJSONArray("results")).getJSONObject(0).getString("avg_rating");
                        total_reviews = (int)Float.parseFloat(total_reviews) +"";
                        handler.sendEmptyMessage(1);
                    }
                }else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(3);
                }
            }catch (JSONException e){

            }
        }
    };
    ResponseListener responseListenerRatings = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS")){
                    JSONObject RESPONSE = Response.getJSONObject("RESPONSE");
                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
                    next = pagination.getString("next");
                    JSONArray results = RESPONSE.getJSONArray("results");
                    for (int i = 0 ; i < results.length() ; i++){
                        RatingListModal ratingListModal = new RatingListModal();
                        ratingListModal.setJob_id(results.getJSONObject(i).getString("job_id"));
                        ratingListModal.setCustomer_id(results.getJSONObject(i).getString("customer_id"));
//                        ratingListModal.setRatings(results.getJSONObject(i).getString("rating"));
                        ratingListModal.setCreated_at(results.getJSONObject(i).getString("created_at"));
                        ratingListModal.setFinished_at(results.getJSONObject(i).getString("updated_at"));
                        ratingListModal.setAppearance(results.getJSONObject(i).getString("appearance"));
                        ratingListModal.setCourteous(results.getJSONObject(i).getString("courteous"));
                        ratingListModal.setKnowledgeable(results.getJSONObject(i).getString("knowledgeable"));
//                        ratingListModal.setComments(results.getJSONObject(i).getString("comments"));
                        ratingListModal.setCustomers_first_name(results.getJSONObject(i).getJSONObject("customers").getString("first_name"));
                        ratingListModal.setCustomers_last_name(results.getJSONObject(i).getJSONObject("customers").getString("last_name"));
                        if (!results.getJSONObject(i).isNull("jobs")){
                            ratingListModal.setJobs_contact_name(results.getJSONObject(i).getJSONObject("jobs").getString("contact_name"));
                                ratingListModal.setJobs_request_date(results.getJSONObject(i).getJSONObject("jobs").getString("request_date"));
                            ratingListModal.setJobs_started_at(results.getJSONObject(i).getJSONObject("jobs").getString("started_at"));
                            ratingListModal.setJobs_finished_at(results.getJSONObject(i).getJSONObject("jobs").getString("finished_at"));
                            if (!results.getJSONObject(i).getJSONObject("jobs").isNull("technicians")){
                                ratingListModal.setJobs_technilcians_first_name(results.getJSONObject(i).getJSONObject("jobs").getJSONObject("technicians").getString("first_name"));
                                ratingListModal.setJobs_technilcians_last_name(results.getJSONObject(i).getJSONObject("jobs").getJSONObject("technicians").getString("last_name"));
                                ratingListModal.setJobs_technilcians_avg_rating(results.getJSONObject(i).getJSONObject("jobs").getJSONObject("technicians").getString("avg_rating"));
                                if( !results.getJSONObject(i).getJSONObject("jobs").getJSONObject("technicians").isNull("profile_image")){
                                    JSONObject profile_image = results.getJSONObject(i).getJSONObject("jobs").getJSONObject("technicians").getJSONObject("profile_image");
                                    if (!profile_image.isNull("original")) {
                                        ratingListModal.setJobs_technilcians_img_original(profile_image.getString("original"));
                                    }
                                }

                            }
                            if (!results.getJSONObject(i).getJSONObject("jobs").isNull("job_customer_addresses")){
                                ratingListModal.setJobs_job_customers_addresses_address(results.getJSONObject(i).getJSONObject("jobs").getJSONObject("job_customer_addresses").getString("address"));
                                ratingListModal.setJobs_job_customers_addresses_city(results.getJSONObject(i).getJSONObject("jobs").getJSONObject("job_customer_addresses").getString("city"));
                                ratingListModal.setJobs_job_customers_addresses_state(results.getJSONObject(i).getJSONObject("jobs").getJSONObject("job_customer_addresses").getString("state"));
                            }
                        }





                        ratingListModalArrayList.add(ratingListModal);
                    }
                    handler.sendEmptyMessage(2);
                }else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(3);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:{
                    GetApiResponseAsync responseAsyncCompleted = new GetApiResponseAsync("POST", responseListenerRatings, getActivity(), "Loading");
                    responseAsyncCompleted.execute(getRequestParamsRatings());
                    break;
                }
                case 2:{
                    txtnumtotaljob.setText(total_jobs);
                    txtnumreviews.setText(ratingListModalArrayList.size()+"");
                    custom_ratingbar.setStar(Integer.parseInt(total_reviews),true);
                    adapter.notifyDataSetChanged();
                    break;
                }
                case 3:{
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }
                default:{

                }
            }
        }
    };
    private void setWidgets(View v) {

        ratingListView = (ListView)v.findViewById(R.id.ratingListView);

    }
    private void setTypeface(){
        fontfamily = Typeface.createFromAsset(getActivity().getAssets(), "HelveticaNeue-Thin.otf");



    }

    private void setClickListner(){
        ratingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0){
                    Intent i = new Intent(getActivity(), RatingDetailsActivity.class);
                    i.putExtra("RatingObject", ratingListModalArrayList.get(position - 1));
                    startActivity(i);
                }

            }
        });


    }
    private HashMap<String,String> getRequestParamsRatings(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
        hashMap.put("object","technicians_ratings");
        hashMap.put("select", "^*,customers.^*,jobs.contact_name,jobs.contact_name,jobs.started_at,jobs.finished_at,jobs.request_date,jobs.technicians.^*,jobs.job_customer_addresses.^*");
        hashMap.put("where[technician_id]", TechId);
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("per_page", "15");
        hashMap.put("page", pageRating + "");
        for ( String key : hashMap.keySet() ) {
            Log.e(""+key,"="+hashMap.get(key));
        }
        return hashMap;
    }
    private HashMap<String,String> getRequestParamsRatingsOverview(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("avg_rating[api]","read");
        hashMap.put("avg_rating[object]","technicians");
        hashMap.put("job_count[api]", "count");
        hashMap.put("job_count[object]", "jobs");
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("avg_rating[select]", "avg_rating");
        hashMap.put("avg_rating[where][user_id]", TechId + "");
        for ( String key : hashMap.keySet() ) {
            Log.e(""+key,"="+hashMap.get(key));
        }
        Log.e("","-------------------------------------");
        return hashMap;
    }
    private void showAlertDialog(String Title,String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                _context);

        // set title
        alertDialogBuilder.setTitle(Title);

        // set dialog message
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    private ViewGroup addHeader(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.rating_list_header, ratingListView,
                false);
        TextView txtAverageRating,txtjobstxt,txtreviewsltxt;


        txtAverageRating = (TextView)v.findViewById(R.id.txtAverageRating);
        txtnumtotaljob = (TextView)v.findViewById(R.id.txtnumtotaljob);
        txtjobstxt = (TextView)v.findViewById(R.id.txtjobstxt);
        txtnumreviews = (TextView)v.findViewById(R.id.txtnumreviews);
        txtreviewsltxt = (TextView)v.findViewById(R.id.txtreviewsltxt);

        custom_ratingbar = (RatingBarView)v.findViewById(R.id.custom_ratingbar);
        custom_ratingbar.setClickable(false);

//        txtAverageRating.setTypeface(fontfamily);
//        txtnumtotaljob.setTypeface(fontfamily);
//        txtjobstxt.setTypeface(fontfamily);
//        txtnumreviews.setTypeface(fontfamily);
//        txtreviewsltxt.setTypeface(fontfamily);


        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.RATING_FRAGMENT);
        setupToolBar();
        if (!_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.IS_VARIFIED, "").equals("0")){
            new CheckIfUserVarified(getActivity());
        }
    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).hideRight();
        ((HomeScreenNew)getActivity()).setTitletext("Ratings");
        ((HomeScreenNew)getActivity()).setLeftToolBarImage(R.drawable.menu_icon);
    }
}
