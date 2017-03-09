package fixdpro.com.fixdpro.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.ResponseListener;
import fixdpro.com.fixdpro.adapters.RepairTypeAdapter;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.beans.install_repair_beans.RepairType;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.ExceptionListener;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsyncMutipart;
import fixdpro.com.fixdpro.utilites.MultipartUtility;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RepairFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RepairFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepairFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    SharedPreferences _prefs = null ;
    Context _context = null ;
    ListView listViewInstallType = null ;
    String error_message = "";
    ArrayList<RepairType> arrayList = new ArrayList<RepairType>();
    MultipartUtility multipart = null;
    String install_or_repair_type_id = "";
    String install_or_repair_type_type = "";
    String install_or_repair_type_price = "";
    String install_or_repair_labour_hour = "";
    JobAppliancesModal modal = null ;
    TextView txtJobName ;
    EditText txtSearch ;
    RepairTypeAdapter adapter;
    CurrentScheduledJobSingleTon singleTon = null;
    RepairType repairType ;
    public RepairFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RepairFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RepairFragment newInstance(String param1, String param2) {
        RepairFragment fragment = new RepairFragment();
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
        }
        _context = getActivity();
        _prefs = Utilities.getSharedPreferences(_context);
        singleTon = CurrentScheduledJobSingleTon.getInstance();
        repairType =  singleTon.getJobApplianceModal().getInstallOrRepairModal().getRepairType();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_repair, container, false);
        modal = CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal();
        setWidgets(view);
        setListeners();
        txtJobName.setText(modal.getAppliance_type_name());
        GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync("POST",getRepairTypesListener,getActivity(),"Getting.");
        getApiResponseAsync.execute(getRepairOrInstallTypeRequestParams());

        return view;
    }
    private void setWidgets(View view){
        listViewInstallType = (ListView)view.findViewById(R.id.listViewInstallType);
        txtSearch = (EditText)view.findViewById(R.id.txtSearch);
        txtJobName = (TextView)view.findViewById(R.id.txtJobName);
    }

    IHttpResponseListener iHttpResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS")){
                    handler.sendEmptyMessage(2);
                }
                else{
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);
                        handler.sendEmptyMessage(1);
                    }
                }

            }catch (JSONException e){

            }
        }
    };
    IHttpExceptionListener exceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception;
            handler.sendEmptyMessage(1);
        }
    };

    private HashMap<String,String>  getRequestParamsForSeletedType(){
        HashMap<String,String> hashMap = new HashMap<String,String>();

        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, ""));
        if (CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_service_type().equals("Install")){
            hashMap.put("api", "update_install_types");
            hashMap.put("object", "install_flow");
            hashMap.put("data[items][0][install_type_id]", install_or_repair_type_id);
        }else if (CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_service_type().equals("Repair")){
            hashMap.put("api", "update_repair_types");
            hashMap.put("object", "repair_flow");
            hashMap.put("data[items][0][repair_type_id]", install_or_repair_type_id);
        }else {
            hashMap.put("api", "update_maintain_types");
            hashMap.put("object", "maintain_flow");
            hashMap.put("data[items][0][maintain_type_id]", install_or_repair_type_id);
        }
        hashMap.put("data[job_appliance_id]", CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_id());
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap ;
    }
    private HashMap<String,String>  getRequestParamsForSaveType(String hours,String Desc){
        HashMap<String,String> hashMap = new HashMap<String,String>();

        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("api", "add_custom_repair_type");
        hashMap.put("object", "job_appliances");
        hashMap.put("data[hours]", hours);
        hashMap.put("data[name]", Desc);
        hashMap.put("data[job_appliance_id]", CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_id());
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap ;
    }
    private void setListeners(){
        listViewInstallType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!arrayList.get(position).getType().equals("Others")) {
                    install_or_repair_type_id = arrayList.get(position).getId();
                    install_or_repair_type_type = arrayList.get(position).getType();
                    install_or_repair_type_price = arrayList.get(position).getPrice();
                    install_or_repair_labour_hour = arrayList.get(position).getLabor_hours();
                    GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL, "POST", iHttpResponseListener, exceptionListener, getActivity(), "");
                    getApiResponseAsyncNew.execute(getRequestParamsForSeletedType());
                } else {
                    showOtherTypeDialog();
                }

//
            }
        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (adapter != null)
                    adapter.getFilter().filter(txtSearch.getText().toString());
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.REPAIR_TYPE_FRAGMENT);
        setupToolBar();

    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).hideRight();
        ((HomeScreenNew)getActivity()).setTitletext("Repair Types");
        ((HomeScreenNew)getActivity()).setLeftToolBarImage(R.drawable.popup_cross);
    }
    private HashMap<String,String> getRepairOrInstallTypeRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
//
        if (modal.getJob_appliances_service_type().equals("Install")){
            hashMap.put("select","install_types.name,install_types.labor_hours,install_types.id");
            hashMap.put("object","install_types");
        }
        else if (modal.getJob_appliances_service_type().equals("Repair")){
            hashMap.put("select","repair_types.name,repair_types.labor_hours,repair_types.id");
            hashMap.put("object","repair_types");
        }
        else{
            hashMap.put("select","maintain_types.name,maintain_types.labor_hours,maintain_types.id");
            hashMap.put("object","maintain_types");
        }
        hashMap.put("token",_prefs.getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("data[job_appliance_id]", CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_id());
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }
    ResponseListener getRepairTypesListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    JSONObject jsonObject = Response.getJSONObject("RESPONSE");
                    String rate = "0";
                    if (jsonObject.has("hourly_rate")){
                        rate = jsonObject.getString("hourly_rate");
                    }

//                    JSONObject pagination = Response.getJSONObject("RESPONSE").getJSONObject("pagination");
//                    for (int i = 0 ; i < results.length() ; i++){
//                        JSONObject jsonObject = results.getJSONObject(i);

                        JSONArray jsonArrayRepairType = null;
                        if (!jsonObject.isNull("repair_types")){
                            jsonArrayRepairType = jsonObject.getJSONArray("repair_types");
                        }else if (!jsonObject.isNull("install_types")){
                            jsonArrayRepairType = jsonObject.getJSONArray("install_types");
                        }else if (!jsonObject.isNull("maintain_types")){
                            jsonArrayRepairType = jsonObject.getJSONArray("maintain_types");
                        }
                        for (int j = 0 ; j < jsonArrayRepairType.length() ; j++){
                            JSONObject jsonObjectRepairType =  jsonArrayRepairType.getJSONObject(j);
                            RepairType repairType = new RepairType();
                            repairType.setId(jsonObjectRepairType.getString("id"));
                            repairType.setType(jsonObjectRepairType.getString("name"));
                            float cost = 0 ;
                            DecimalFormat decimalFormat = new DecimalFormat("0.00");

                            if (jsonObjectRepairType.has("calculated_by") && jsonObjectRepairType.getString("calculated_by").equals("FIXED")){

                                repairType.setPrice(jsonObjectRepairType.getString("fixed_cost"));
                            }else {

                                cost = Float.parseFloat(rate) * Float.parseFloat(jsonObjectRepairType.getString("labor_hours"));
                                decimalFormat.format(cost);
                                repairType.setPrice(cost+"");

                            }

                            repairType.setLabor_hours(jsonObjectRepairType.getString("labor_hours"));
                            arrayList.add(repairType);
                        }

//                    }
                    RepairType repairType = new RepairType();
                    repairType.setType("Others");
                    arrayList.add(repairType);
                    handler.sendEmptyMessage(0);
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);

                    }
                    handler.sendEmptyMessage(1);
                }
            }catch (JSONException e){

            }
        }
    };

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    adapter = new RepairTypeAdapter(getActivity(),arrayList,getResources());
                    listViewInstallType.setAdapter(adapter);
                    if (arrayList.size() == 0)
                        handler.sendEmptyMessage(2);
                    break;
                }case 1:{
//                    showAlertDialog("Fixd-Pro",error_message);
                    handler.sendEmptyMessage(2);
                    break;
                }case 2:{
                    CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getInstallOrRepairModal().getRepairType().setLabor_hours(install_or_repair_labour_hour);
                    CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getInstallOrRepairModal().getRepairType().setId(install_or_repair_type_id);
                    CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getInstallOrRepairModal().getRepairType().setType(install_or_repair_type_type);
                    CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getInstallOrRepairModal().getRepairType().setPrice(install_or_repair_type_price);
                    CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getInstallOrRepairModal().getRepairType().setIsCompleted(true);
                    CurrentScheduledJobSingleTon.getInstance().getCurrentReapirInstallProcessModal().setIsCompleted(true);
                    ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.REPAIR_TYPE_FRAGMENT);
                    break;
                }
            }
        }
    };


    private void showOtherTypeDialog(){
        final Dialog  subDialog = new Dialog(getActivity());
        subDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        subDialog.setContentView(R.layout.diaolod_add_repair_type);
        subDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText editHour = (EditText) subDialog.findViewById(R.id.editHour);
        final EditText editDesc = (EditText) subDialog.findViewById(R.id.editDesc);
        TextView txtOK = (TextView) subDialog.findViewById(R.id.txtOK);

        txtOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editHour.getText().toString().length() == 0){
                    showAlertDialog("Fixd-Pro","Please enter hours.");
                }else if (editDesc.getText().toString().length() == 0){
                    showAlertDialog("Fixd-Pro","Please enter description.");
                }else {
                    subDialog.dismiss();
                    install_or_repair_labour_hour  = editHour.getText().toString();
                    install_or_repair_type_id  = editDesc.getText().toString();
                    install_or_repair_type_type = editDesc.getText().toString();
                    GetApiResponseAsyncNew getApiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL, "POST", iHttpResponseListener, exceptionListener, getActivity(), "");
                    getApiResponseAsyncNew.execute(getRequestParamsForSaveType(editHour.getText().toString(),editDesc.getText().toString()));
                }
            }
        });
        subDialog.show();
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
    public void executeRepairTypeSaveingRequest(){
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    multipart = new MultipartUtility(Constants.BASE_URL, Constants.CHARSET);
                    multipart.addFormField("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN,""));
                    if (CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_service_type().equals("Install")){
                        multipart.addFormField("api", "update_install_types");
                        multipart.addFormField("object", "install_flow");
                        multipart.addFormField("data[install_type_id]", install_or_repair_type_id);
                    }else{
                        multipart.addFormField("api", "update_repair_types");
                        multipart.addFormField("object", "repair_flow");
                        multipart.addFormField("data[repair_type_id]", install_or_repair_type_id);
                    }
                    multipart.addFormField("data[job_appliance_id]", CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_id());
                    multipart.addFormField("_app_id", "FIXD_ANDROID_PRO");
                    multipart.addFormField("_company_id", "FIXD");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                GetApiResponseAsyncMutipart getApiResponseAsync = new GetApiResponseAsyncMutipart(multipart, repairTypeResponseListener,repairTypeexceptionListener, getActivity(), "Saving");
                getApiResponseAsync.execute();
            }
        }.execute();
    }
    ResponseListener repairTypeResponseListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("","Response"+Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS")){
                    handler.sendEmptyMessage(2);
                }
                else{
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()){
                        String key = (String)keys.next();
                        error_message = errors.getString(key);
                        handler.sendEmptyMessage(1);
                    }
                }

            }catch (JSONException e){

            }
        }
    };
    ExceptionListener repairTypeexceptionListener= new ExceptionListener() {
        @Override
        public void handleException(int exceptionStatus) {
            handler.sendEmptyMessage(exceptionStatus);
        }
    };

}
