package fixdpro.com.fixdpro.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.AssignTechnicianActivity;
import fixdpro.com.fixdpro.ChatActivity;
import fixdpro.com.fixdpro.ChatActivityNew;
import fixdpro.com.fixdpro.DeclineJobActivity;
import fixdpro.com.fixdpro.FixdProApplication;
import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.beans.AssignTechModal;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.ChatService;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.RatingBarView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduledListDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduledListDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduledListDetailsFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    AvailableJobModal model;
    private static GoogleMap mMap;
    TextView contactName, address, date, timeinterval, txtUserName, txtJobDetails, txtCancelJob, txtEnrouteJob;
    ImageView  en_routeimg, cancel_jobimg, transparentImageView ,img_Edit, img_Pic, imgChat;
    HorizontalScrollView horizontalScrollView  = null ;
    LayoutInflater inflater ;
    ImageLoader imageLoader = null ;
    DisplayImageOptions defaultOptions;
    LinearLayout scrollViewLatout,layout_problem ,techView,layoutServiceDescription;
    ScrollView scrollViewParent  ;
    RatingBarView custom_ratingbar ;
    Fragment fragment = null ;
    SupportMapFragment mMapFragment = null ;
    View techDivider;
    SharedPreferences _prefs = null ;
    Context _context = null ;
    String error_message = "";
    String room_id  = "";
    Dialog progressDialog;
    private static final String EXTRA_DIALOG = "dialog";
    public ScheduledListDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduledListDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduledListDetailsFragment newInstance(String param1, String param2) {
        ScheduledListDetailsFragment fragment = new ScheduledListDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            model = (AvailableJobModal) getArguments().getSerializable("modal");
//            model = CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal();
        }
//        / UNIVERSAL IMAGE LOADER SETUP
        defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
        _context = getActivity();
        // Create configuration for ImageLoader (all options are optional)

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            Log.e("", "" + data);
            AssignTechModal assignTechModal = (AssignTechModal)data.getSerializableExtra("tech");
            model.setTechnician_profile_image(assignTechModal.getImage());
            model.setTechnician_fname(assignTechModal.getFirstName());
            model.setTechnician_lname(assignTechModal.getLasttName());
            model.setTechnician_avg_rating(assignTechModal.getRating());
            model.setTechnician_scheduled_job_count(assignTechModal.getJobSchedule());
            model.setTechnician_user_id(assignTechModal.getTech_User_id());
            setTechView();
        }
    }


    private void setTechView(){
        if (model.getTechnician_profile_image().length() > 0){
            imageLoader.loadImage(model.getTechnician_profile_image(), defaultOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // Do whatever you want with Bitmap
                    img_Pic.setImageBitmap(loadedImage);
                }
            });
        }
        txtUserName.setText(model.getTechnician_fname() + " " + model.getTechnician_lname());
        if (_prefs.getString(Preferences.ROLE,"").equals("pro")){
            custom_ratingbar.setClickable(false);
            if (model.getTechnician_avg_rating().length() > 0)
                custom_ratingbar.setStar((int) Float.parseFloat(model.getTechnician_avg_rating()), true);
            else
                custom_ratingbar.setStar(0, true);
        }
        txtJobDetails.setText(model.getTechnician_fname() + " has " + "1" + " jobs scheduled for this time");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.schedule_list_details, container, false);
        _prefs = Utilities.getSharedPreferences(_context);
        setWidgets(rootView);
        setListeners();
//        img_Pic

        contactName.setText(model.getContact_name());
        address.setText(model.getJob_customer_addresses_address() + " " + model.getJob_customer_addresses_address_2() + " - " + model.getJob_customer_addresses_city() + "," + model.getJob_customer_addresses_state());
        date.setText(Utilities.convertDate(model.getRequest_date()));
        timeinterval.setText(model.getTimeslot_name());
        setTechView();
//        setUpHorizontalScrollView();
        setUpApplianceDescription();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap ;
        setUpMap();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void setWidgets(View view){
        horizontalScrollView = (HorizontalScrollView)view.findViewById(R.id.horizontalScrollView);
//        mMap = ((SupportMapFragment)getChildFragmentManager()
//                .findFragmentById(R.id.location_map)).getMap();
        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.location_map, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
        en_routeimg = (ImageView) view.findViewById(R.id.enroute_job);
        cancel_jobimg = (ImageView) view.findViewById(R.id.canceljob);
        contactName = (TextView) view.findViewById(R.id.contactname);
        address = (TextView) view.findViewById(R.id.address);
        date = (TextView) view.findViewById(R.id.date);
        timeinterval = (TextView) view.findViewById(R.id.timeinterval);
        scrollViewLatout = (LinearLayout)view.findViewById(R.id.scrollViewLatout);
        layout_problem = (LinearLayout)view.findViewById(R.id.layout_problem);
        scrollViewParent = (ScrollView)view.findViewById(R.id.scrollViewParent);
        transparentImageView = (ImageView) view.findViewById(R.id.transparent_image);

        custom_ratingbar = (RatingBarView)view.findViewById(R.id.custom_ratingbar);
        txtJobDetails = (TextView) view.findViewById(R.id.txtJobDetails);
        txtUserName = (TextView) view.findViewById(R.id.txtUserName);

        txtCancelJob = (TextView) view.findViewById(R.id.txtcancelJob);
        txtEnrouteJob = (TextView) view.findViewById(R.id.txtEnrouteJob);
        techView = (LinearLayout)view.findViewById(R.id.techView);
        layoutServiceDescription = (LinearLayout)view.findViewById(R.id.layoutServiceDescription);
        techDivider = (View)view.findViewById(R.id.techDivider);
        if (!_prefs.getString(Preferences.ROLE,"").equals("pro")){
            techView.setVisibility(View.GONE);
            techDivider.setVisibility(View.GONE);
        }
        img_Edit = (ImageView)view.findViewById(R.id.img_Edit);
        img_Pic = (ImageView)view.findViewById(R.id.img_Pic);
        imgChat = (ImageView)view.findViewById(R.id.imgChat);
    }
    public void setListeners(){
        imgChat.setOnClickListener(this);
        en_routeimg.setOnClickListener(this);
        cancel_jobimg.setOnClickListener(this);
        txtCancelJob.setOnClickListener(this);
        txtEnrouteJob.setOnClickListener(this);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
        img_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AssignTechnicianActivity.class);
                intent.putExtra("api", "re_assign");
                intent.putExtra("isAvailable", false);
                FixdProApplication.SelectedAvailableJobId = model.getId();
                startActivityForResult(intent, 999);
            }
        });

    }
    private void setUpMap() {
        // For showing a move to my loction button
//        mMap.setMyLocationEnabled(true);
        // For dropping a marker at a point on the Map
        mMap.addMarker(new MarkerOptions().position(new LatLng(model.getJob_customer_addresses_latitude(), model.getJob_customer_addresses_longitude())).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(model.getJob_customer_addresses_latitude(),
                model.getJob_customer_addresses_longitude()), 12.0f));
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.SCHEDULED_LIST_DETAILS_FRAGMENT);
        setupToolBar();
    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).hideRight();
        ((HomeScreenNew)getActivity()).setTitletext("Scheduled Job");
        ((HomeScreenNew)getActivity()).setLeftToolBarImage(R.drawable.screen_cross);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
//                finish();
                ((HomeScreenNew)getActivity()).popStack();
                break;
            case R.id.imgChat:
//                finish();
                GetApiResponseAsyncNew responseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST", responseListenerChatDetails,exceptionListener, getActivity(), "Loading");
                responseAsync.execute(getRequestParams());
                break;
            case R.id.enroute_job:
                if (!model.getTechnician_user_id().equals(_prefs.getString(Preferences.ID, ""))){
                    showAlertDialog("Fixd-pro","This job is assigned to your Tech, You are not authorized to start this job , you may Re-Assign tech to assign to your self");
                }else {
                    fragment = new StartJobFragment();
                    ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.START_JOB_FRAGMENT, true, null);
                }


                break;
            case R.id.canceljob:
                Intent i = new Intent(getActivity(), DeclineJobActivity.class);
                i.putExtra("JobType","Scheduled");
                i.putExtra("JobId",model.getId());
                startActivity(i);

                break;
            case R.id.txtEnrouteJob:
                if (!model.getTechnician_user_id().equals(_prefs.getString(Preferences.ID, ""))){
                    showAlertDialog("Fixd-pro","This job is assigned to your Tech, You are not authorized to start this job , you may Re-Assign tech to assign to your self");
                }else {
                    // check if already enrouted job.
                    if (CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal() != null){
                         // check if the same job is already enrouted
                        if (!CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId().equals(model.getId())){
                            showAlertDialog("Fixd-Pro","You were heading towards a different job already, Please press the top-bar to continue.");
                        }else{
                            CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(model);
                            enrouteJob();
                        }
                    }else {
                        CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(model);
                        enrouteJob();
                    }
                }


                break;
            case R.id.txtcancelJob:
                Intent intent = new Intent(getActivity(), DeclineJobActivity.class);
                intent.putExtra("JobType","Scheduled");
                intent.putExtra("JobId",model.getId());
                startActivity(intent);

                break;
        }
    }

    IHttpResponseListener responseListenerChatDetails = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS"))
                {
                    room_id = Response.getJSONObject("RESPONSE").getString("room_id");


                    handler.sendEmptyMessage(0);
                }else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                    }

                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };
    private void getChatUsers(String id){
        // Get dialogs
        //

        ChatService.getInstance().getDialogs(new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBDialog> dialogs, Bundle bundle1) {
               Log.e("","");
                handler.sendEmptyMessage(3);
                QBDialog selectedDialog = dialogs.get(0);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.EXTRA_DIALOG, selectedDialog);
                try {
                    JSONObject jsonObject = new JSONObject(selectedDialog.getPhoto());
                    Iterator<String> iter = jsonObject.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            Object value = jsonObject.get(key);
                            JSONObject object = new JSONObject(value.toString());
                            bundle.putSerializable("name", object.getString("name"));
                            break;
                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                 Open chat activity

                Intent i = new Intent(getActivity(), ChatActivityNew.class);
                i.putExtra(EXTRA_DIALOG,selectedDialog);
                startActivity(i);

            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e("","");
            }
        },id);
    }
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception ;
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    progressDialog = new Dialog(getActivity());
                    progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    progressDialog.setContentView(R.layout.dialog_progress_simple);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    getChatUsers(room_id);
                    break;
                }
                case 1:{
                    break;
                }
                case 2:{
                    break;
                } case 3:{
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    break;
                }

            }
        }
    };
    private HashMap<String,String> getRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api", "chatroom");
        hashMap.put("object", "jobs");
        hashMap.put("job_id", model.getId());
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));

        return hashMap;
    }

    private void enrouteJob(){
        if (model.getStarted_at().equals("0000-00-00 00:00:00")){
            fragment = new StartJobFragment();

            ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.START_JOB_FRAGMENT, true, null);
        }else {
            fragment = new InstallorRepairFragment();
            ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.INSTALL_OR_REPAIR_FRAGMENT, true, null);
        }
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
    private void setUpHorizontalScrollView(){
        if (model.getJob_appliances_arrlist().size() > 0){
            inflater =  (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0 ; i < model.getJob_appliances_arrlist().size() ; i++){
                View child = getLayoutInflater(null).inflate(R.layout.jobs_image_title_item, null);
                View child1 = getLayoutInflater(null).inflate(R.layout.problem_text_item, null);
                TextView txtTitle = (TextView)child.findViewById(R.id.txtTypeTitle);
                TextView txtProblem = (TextView)child1.findViewById(R.id.txtProblem);
                txtProblem.setText(model.getJob_appliances_arrlist().get(i).getAppliance_type_name() + " " +model.getJob_appliances_arrlist().get(i).getJob_appliances_service_type() + " - " +model.getJob_appliances_arrlist().get(i).getJob_appliances_appliance_description());
                final ImageView imgType = (ImageView)child.findViewById(R.id.imgType);
                txtTitle.setText(model.getJob_appliances_arrlist().get(i).getAppliance_type_name() +"\n" +model.getJob_appliances_arrlist().get(i).getJob_appliances_service_type());
                scrollViewLatout.addView(child);
//                layout_problem.addView(child1);
                imageLoader.loadImage(model.getJob_appliances_arrlist().get(i).getImg_original(), defaultOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Do whatever you want with Bitmap
                        imgType.setImageBitmap(loadedImage);
                    }
                });
            }
        }
    }
    private void setUpApplianceDescription(){
        final ArrayList<JobAppliancesModal> arrayList = model.getJob_appliances_arrlist();
        if (arrayList.size() > 0){
            inflater =  (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0 ; i <arrayList.size() ; i++){
                View child = getActivity().getLayoutInflater().inflate(R.layout.item_available_job_service_desc, null);
                TextView txtServiceType = (TextView)child.findViewById(R.id.txtServiceType);
                TextView txtPowerSourceName = (TextView)child.findViewById(R.id.txtPowerSourceName);
                TextView txtApplianceName = (TextView)child.findViewById(R.id.txtApplianceName);
                TextView txtProblem = (TextView)child.findViewById(R.id.txtProblem);
                TextView txtBrand = (TextView)child.findViewById(R.id.txtBrand);
                TextView txtDesc = (TextView)child.findViewById(R.id.txtDesc);
                final ImageView imgShowProblem = (ImageView)child.findViewById(R.id.imgShowProblem);
                View divider = (View)child.findViewById(R.id.divider);
                imgShowProblem.setTag(i + "");
                txtServiceType.setText(arrayList.get(i).getJob_appliances_service_type() + ":");
                if (arrayList.get(i).getJob_appliances_power_source().trim().length() == 0){
                    txtPowerSourceName.setText(" "+arrayList.get(i).getJob_appliances_power_source());
                }

                txtApplianceName.setText(" "+arrayList.get(i).getAppliance_type_name());
                if (arrayList.get(i).getJob_appliances_brand_name().trim().length() > 0){
                    txtBrand.setText(" "+arrayList.get(i).getJob_appliances_brand_name());
                }

                txtDesc.setText(arrayList.get(i).getJob_appliances_appliance_description());
                if (arrayList.get(i).getJob_appliances_customer_compalint().trim().length() > 0){
                    txtProblem.setText(" "+arrayList.get(i).getJob_appliances_customer_compalint());
                }

                if (arrayList.get(i).getImg_original().length() == 0)
                    imgShowProblem.setVisibility(View.GONE);
                imgShowProblem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ProblemImageActivity.class);
                        intent.putExtra("problemImageURL", arrayList.get(Integer.parseInt((String) imgShowProblem.getTag())).getImg_original());
                        startActivity(intent);

                    }
                });
                if (i == arrayList.size() - 1)
                    divider.setVisibility(View.GONE);
                layoutServiceDescription.addView(child);
            }
        }

    }
}
