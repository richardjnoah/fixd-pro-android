package fixdpro.com.fixdpro.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.ResponseListener;
import fixdpro.com.fixdpro.adapters.HasPowerSourceAdapter;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.beans.install_repair_beans.InstallOrRepairModal;
import fixdpro.com.fixdpro.singleton.CurrentServiceAddingSingleTon;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HasPowerSourceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HasPowerSourceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HasPowerSourceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ListView lstChoosePowerSource ;
    Fragment fragment = null ;
    SharedPreferences _prefs = null;
    Context _context = null ;
    CurrentServiceAddingSingleTon singleTon = null ;
    String message = "";
    public HasPowerSourceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HasPowerSourceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HasPowerSourceFragment newInstance(String param1, String param2) {
        HasPowerSourceFragment fragment = new HasPowerSourceFragment();
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
        singleTon = CurrentServiceAddingSingleTon.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_has_power_source, container, false);
        setWidgets(view);
        setListeners();
        HasPowerSourceAdapter adapter = new HasPowerSourceAdapter(getActivity(), CurrentServiceAddingSingleTon.getInstance().getPowerSources(),getResources());
        lstChoosePowerSource.setAdapter(adapter);
        return view;
    }
    private void setWidgets(View view){
        lstChoosePowerSource = (ListView)view.findViewById(R.id.lstChoosePowerSource);
    }
    private void setListeners(){
        lstChoosePowerSource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CurrentServiceAddingSingleTon.getInstance().setSelectedPowerSource(CurrentServiceAddingSingleTon.getInstance().getPowerSources().get(position));
                addServiceRequest();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew) getActivity()).setCurrentFragmentTag(Constants.HAS_POWERSOURCE_FRAGMENT);
         setupToolBar();
    }

    private void setupToolBar() {
        ((HomeScreenNew) getActivity()).hideRight();
        ((HomeScreenNew) getActivity()).setTitletext(CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getContact_name());
        ((HomeScreenNew) getActivity()).setLeftToolBarText("Back");
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
    private void addServiceRequest(){
        GetApiResponseAsync getApiResponseAsync = new GetApiResponseAsync("POST",addServiceResponseListener,getActivity(),"Saving.");
        getApiResponseAsync.execute(getAddServiceParams());
    }
    private HashMap<String,String> getAddServiceParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","add_appliance");
        hashMap.put("object","jobs");
        hashMap.put("data[appliance_id]",CurrentServiceAddingSingleTon.getInstance().getSelectedApplianceId());
        hashMap.put("token",_prefs.getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("data[service_type]", CurrentServiceAddingSingleTon.getInstance().getSelectedServicetype() + "");
        hashMap.put("job_id", CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId() + "");
        hashMap.put("data[power_source]", CurrentServiceAddingSingleTon.getInstance().getSelectedPowerSource() + "");
        hashMap.put("_app_id", "FIXD_ANDROID_PRO");
        hashMap.put("_company_id", "FIXD");
        return hashMap;
    }
    ResponseListener addServiceResponseListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("","Response"+Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    JSONObject jsonObject = Response.getJSONObject("RESPONSE");
                    ArrayList<JobAppliancesModal> appliancesModals = CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getJob_appliances_arrlist();

                    JobAppliancesModal mod = new JobAppliancesModal();
                    mod.setJob_appliances_id(jsonObject.getString("id"));
                    mod.setJob_appliances_job_id(jsonObject.getString("job_id"));
                    mod.setJob_appliances_appliance_id(jsonObject.getString("appliance_id"));
                    if (!jsonObject.isNull("service_type")) {
                        mod.setJob_appliances_service_type(jsonObject.getString("service_type"));
                        InstallOrRepairModal installOrRepairModal = mod.getInstallOrRepairModal();
                        installOrRepairModal.getWorkOrder().setHourly_rate(appliancesModals.get(0).getInstallOrRepairModal().getWorkOrder().getHourly_rate());
                    }
                    if (!jsonObject.isNull("appliance_types")){
                        JSONObject appliance_type_obj = jsonObject.getJSONObject("appliance_types");
                        mod.setAppliance_type_id(appliance_type_obj.getString("id"));
                        mod.setAppliance_type_has_power_source(appliance_type_obj.getString("has_power_source"));
                        mod.setAppliance_type_service_id(appliance_type_obj.getString("service_id"));
                        mod.setAppliance_type_name(appliance_type_obj.getString("name"));
                        if (mod.getAppliance_type_name().equals("Re Key")){
                            mod.getInstallOrRepairModal().getEquipmentInfo().setIsCompleted(true);
                        }
                        mod.setAppliance_type_soft_deleted(appliance_type_obj.getString("_soft_deleted"));
                        if (!appliance_type_obj.isNull("image")) {
                            JSONObject image_obj = appliance_type_obj.getJSONObject("image");
                            Log.e("", "-----" + image_obj.toString());
                            if (!image_obj.isNull("original")) {
                                mod.setImg_original(image_obj.getString("original"));
                                mod.setImg_160x170(image_obj.getString("160x170"));
                                mod.setImg_150x150(image_obj.getString("150x150"));
                                mod.setImg_75x75(image_obj.getString("75x75"));
                                mod.setImg_30x30(image_obj.getString("30x30"));
                            }
                        }
                    }
                    appliancesModals.add(mod);

                    handler.sendEmptyMessage(0);
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:{
//                     pop fragment till Install or Repair Fragment
                    ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.ADD_SERVICE_FRAGMENT);
                    break;
                }
                case 1: {
                    showAlertDialog("Fixd-Pro", message);
                    break;
                }
            }
        }
    };
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
}
