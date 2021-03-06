package fixtpro.com.fixtpro.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import fixtpro.com.fixtpro.CalendarActivity;
import fixtpro.com.fixtpro.CancelScheduledJob;
import fixtpro.com.fixtpro.HomeScreenNew;
import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.SignatureActivity;
import fixtpro.com.fixtpro.adapters.WhatsWrongAdapter;
import fixtpro.com.fixtpro.beans.Brands;
import fixtpro.com.fixtpro.beans.install_repair_beans.ReapirInstallProcessModal;
import fixtpro.com.fixtpro.net.IHttpExceptionListener;
import fixtpro.com.fixtpro.singleton.BrandNamesSingleton;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.CurrentScheduledJobSingleTon;
import fixtpro.com.fixtpro.utilites.JSONParser;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WhatsWrongFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WhatsWrongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WhatsWrongFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ProgressBar progressBar ;
    ListView listWhatsWrong ;
    Fragment fragment = null ;
    TextView txtProgress, txtCancelJOb,txtRescheduleJob;
    WhatsWrongAdapter adapter = null ;
    float progressText = 0 ;
    int progress = 0;
    ArrayList<Brands> arrayListBrands = BrandNamesSingleton.getInstance().getBrands();
    boolean isAutoNotiForWorkOrder = false ;
    public WhatsWrongFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WhatsWrongFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WhatsWrongFragment newInstance(String param1, String param2) {
        WhatsWrongFragment fragment = new WhatsWrongFragment();
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
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
            isAutoNotiForWorkOrder = true ;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew) getActivity()).setCurrentFragmentTag(Constants.WHATS_WRONG_FRAGMENT);
        setupToolBar();
        if (adapter != null){
            adapter.notifyDataSetChanged();
            if (CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getInstallOrRepairModal().getSignature().getSignature_path() != null){
                ((HomeScreenNew) getActivity()).setRightToolBarText("Done");
            }
        }
    }

    private void setupToolBar() {
        ((HomeScreenNew) getActivity()).hideRight();
        ((HomeScreenNew) getActivity()).setTitletext(CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getContact_name());
        ((HomeScreenNew) getActivity()).setLeftToolBarText("Back");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_whats_wrong, container, false);
        setWidgets(view);
        setListeners();
        setProgress();
        adapter = new WhatsWrongAdapter(getActivity(),CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList(),getResources());
        listWhatsWrong.setAdapter(adapter);
        if (arrayListBrands.size() <= 1)
            getBrands();
        if (isAutoNotiForWorkOrder){
            fragment = new WorkOrderFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isAutoNotiForWorkOrder",isAutoNotiForWorkOrder);
            ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.WORK_ORDER_FRAGMENT, true, bundle);
        }
        return view;
    }
    private void setProgress(){
            int size  = CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList().size();
                int completedCount = 0;
                for (int i = 0 ; i < size ; i++){
                    if (CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList().get(i).isCompleted()){
                        ++completedCount;
                    }
                }
                if (completedCount>0){
                    progress = (int)(100 / (size) * completedCount);
                    progressText =  (float)(100 / (size) * completedCount);
                    txtProgress.setText(progressText + "%");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                        }
                    }, 200);

                }
    }
    private void setWidgets(View view){
        progressBar = (ProgressBar)view.findViewById(R.id.progress);
        progressBar.setScaleY(5f);
        listWhatsWrong = (ListView)view.findViewById(R.id.listWhatsWrong);
        txtProgress = (TextView)view.findViewById(R.id.txtProgress);
        txtRescheduleJob = (TextView)view.findViewById(R.id.txtRescheduleJob);
        txtCancelJOb = (TextView)view.findViewById(R.id.txtCancelJOb);
    }
    private void setListeners(){
        listWhatsWrong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReapirInstallProcessModal modal = CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList().get(position);
                CurrentScheduledJobSingleTon.getInstance().setCurrentReapirInstallProcessModal(modal);

                if (modal.getName().equals(Constants.EQUIPMENT_INFO)){
//                    if (CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList().get(position).isCompleted()) {

                        fragment = new EquipmentInfoFragment();
                        ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.EQUIPMENT_FRAGMENT, true, null);
//                    }

                }else if (modal.getName().equals(Constants.REPAIR_TYPE) || modal.getName().equals(Constants.INSTALL_TYPE) || modal.getName().equals(Constants.MAINTAIN_TYPE)){
//                    if (CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList().get(position - 1).isCompleted()) {
                    if (checkIfAboveStepsCompleted(position)) {
                        fragment = new RepairFragment();
                        ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.REPAIR_TYPE_FRAGMENT, true, null);
                    }else{
                        showAlertDialog("Fixd-Pro","Please complete above steps first.",false);
                    }

                }else if (modal.getName().equals(Constants.PARTS)){
//                    if (CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList().get(position -1).isCompleted()){
                    if (checkIfAboveStepsCompleted(position)) {
                        fragment = new PartsFragment();
                        ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.PARTS_FRAGMENT, true, null);
                    }else{
                        showAlertDialog("Fixd-Pro", "Please complete above steps first.", false);
                    }

                }else if (modal.getName().equals(Constants.WORK_ORDER)){
//                    if (CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList().get(position -1).isCompleted()){
                    if (checkIfAboveStepsCompleted(position)) {
                        fragment = new WorkOrderFragment();
                        ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.WORK_ORDER_FRAGMENT, true, null);
                    }else{
                        showAlertDialog("Fixd-Pro", "Please complete above steps first.", false);
                    }

                }else if (modal.getName().equals(Constants.REPAIR_INFO) || modal.getName().equals(Constants.INSTALL_INFO) || modal.getName().equals(Constants.MAINTAIN_INFO)){
//                    if (CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList().get(position -1).isCompleted()){
                    if (checkIfAboveStepsCompleted(position)) {
                        fragment = new RepairInfoFragment();
                        ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.REPAIR_INFO_FRAGMENT, true, null);
                    }else{
                        showAlertDialog("Fixd-Pro", "Please complete above steps first.", false);
                    }

                }else if (modal.getName().equals(Constants.SIGNATURE)){
//                    if (CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList().get(position -1).isCompleted()){
                    if (checkIfAboveStepsCompleted(position)) {
                        Intent intent = new Intent(getActivity(), SignatureActivity.class);
                        startActivityForResult(intent, 200);
//                        fragment = new SignatureFragment();
//                        ((HomeScreenNew) getActivity()).switchFragment(fragment, Constants.SIGNATURE_FRAGMENT, true, null);
                    }else{
                        showAlertDialog("Fixd-Pro","Please complete above steps first.",false);
                    }
                }

            }
        });

        txtRescheduleJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarActivity.class);
                intent.putExtra("Rescheduling", "1");
                startActivity(intent);
            }
        });
        txtCancelJOb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CancelScheduledJob.class);
                startActivity(intent);
            }
        });
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private boolean checkIfAboveStepsCompleted(int pos){
        boolean isCompleted = true ;
        for (int i = 0 ; i < pos ; i++){
            if (!CurrentScheduledJobSingleTon.getInstance().getReapirInstallProcessModalList().get(i).isCompleted()){
                isCompleted = false ;
                break;
            }
        }
        return isCompleted;
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
    private void showAlertDialog(String Title,String Message, final boolean doSomeThing){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

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
                        if (doSomeThing){
                            (((HomeScreenNew) getActivity())).popInclusiveFragment(Constants.SCHEDULED_LIST_DETAILS_FRAGMENT);
                        }

                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.e("","requestCode"+requestCode);
        if (adapter != null){
            adapter.notifyDataSetChanged();
            onResume();
            progressBar.setProgress(100);
            txtProgress.setText("100%");
        }
    }
    public void submitPost(){
        ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.WHATS_WRONG_FRAGMENT);
        CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().setIsProcessCompleted(true);
    }
    private HashMap<String, String> getBrandsRequestParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "brands");
        hashMap.put("select", "^*");
        hashMap.put("per_page", "999");
        hashMap.put("page", "1");
        hashMap.put("appliance_type_id", CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getAppliance_type_id());
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        return hashMap;
    }
    private void getBrands(){
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(Constants.BASE_URL, "POST", getBrandsRequestParams());
                if (jsonObject != null) {
                    try {
                        String STATUS = jsonObject.getString("STATUS");
                        if (STATUS.equals("SUCCESS")) {
//                            JSONObject RESPONSE = jsonObject.getJSONObject("RESPONSE");
                            JSONArray results = jsonObject.getJSONArray("RESPONSE");
                            for (int i = 0; i < results.length(); i++) {
                                Brands brands = new Brands();
                                if (!results.getJSONObject(i).getString("brand_name").equals(""))
                                {
                                    brands.setBrand_name(results.getJSONObject(i).getString("brand_name"));
                                    brands.setId(results.getJSONObject(i).getString("id"));
                                    arrayListBrands.add(brands);
                                }
                            }
                            Brands brands1 = new Brands();
                            brands1.setBrand_name("Other Brand");
                            arrayListBrands.add(brands1);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                return null;
            }
        }.execute();
    }
}

