package fixtpro.com.fixtpro.fragment;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import fixtpro.com.fixtpro.HomeScreenNew;
import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.ResponseListener;
import fixtpro.com.fixtpro.adapters.RepairTypeAdapter;
import fixtpro.com.fixtpro.beans.AvailableJobModal;
import fixtpro.com.fixtpro.beans.install_repair_beans.Parts;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.CurrentScheduledJobSingleTon;
import fixtpro.com.fixtpro.utilites.ExceptionListener;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsyncMutipart;
import fixtpro.com.fixtpro.utilites.MultipartUtility;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PartsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PartsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ImageView img_add;
    LinearLayout container_layout;
    ArrayList<Parts> partsArrayList = new ArrayList<Parts>();
    CurrentScheduledJobSingleTon singleTon = null;
    String error_message = "";
    MultipartUtility multipart = null;
    public PartsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PartsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PartsFragment newInstance(String param1, String param2) {
        PartsFragment fragment = new PartsFragment();
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
        singleTon = CurrentScheduledJobSingleTon.getInstance();
        partsArrayList = singleTon.getJobApplianceModal().getInstallOrRepairModal().getPartsContainer().getPartsArrayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parts, container, false);
        setWidgets(view);
        setListeners();
        setView();
        if (partsArrayList.size() == 0){
            partsArrayList.add(new Parts());
            setView();
        }
        return view;
    }
    private void setWidgets(View view){
        img_add = (ImageView)view.findViewById(R.id.img_add);
        container_layout = (LinearLayout)view.findViewById(R.id.container_layout);
    }
    private void setListeners(){
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container_layout.removeAllViews();
                partsArrayList.add(new Parts());
                setView();
            }
        });
    }
    private void setView(){
        for (int i = 0 ; i < partsArrayList.size() ; i++){
            LayoutInflater layoutInflater =
                    (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.parts_item, null);
            View line_view = (View)addView.findViewById(R.id.line_view);
            final EditText txtPartDescription = (EditText)addView.findViewById(R.id.txtPartDescription);
            final EditText txtPartNumber = (EditText)addView.findViewById(R.id.txtPartDescription);
            final EditText txtQty = (EditText)addView.findViewById(R.id.txtQty);
            final EditText txtCost = (EditText)addView.findViewById(R.id.txtCost);

            if (i == 0){
                line_view.setVisibility(View.GONE);
            }
            txtPartDescription.setText(partsArrayList.get(i).getDescription());
            txtPartDescription.setTag(i);
            txtPartNumber.setText(partsArrayList.get(i).getNumber());
            txtPartNumber.setTag(i);
            txtQty.setText(partsArrayList.get(i).getQuantity());
            txtQty.setTag(i);
            txtCost.setText(partsArrayList.get(i).getCost());
            txtCost.setTag(i);

            txtPartDescription.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    partsArrayList.get((int) txtPartDescription.getTag()).setDescription(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            txtPartNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    partsArrayList.get((int)txtPartNumber.getTag()).setNumber(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            txtQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    partsArrayList.get((int)txtQty.getTag()).setQuantity(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            txtCost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    partsArrayList.get((int)txtCost.getTag()).setCost(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            container_layout.addView(addView);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.PARTS_FRAGMENT);
        setupToolBar();

    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).setRightToolBarText("Done");
        ((HomeScreenNew)getActivity()).setTitletext("Parts Needed");
        ((HomeScreenNew)getActivity()).setLeftToolBarText("Back");
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
    public void submitPost(){
        executeAddPartsRequest();

    }
    public void clearList(){
        partsArrayList.clear();
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
    public void executeAddPartsRequest(){
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    multipart = new MultipartUtility(Constants.BASE_URL, Constants.CHARSET);
                    multipart.addFormField("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, ""));
//                    if (CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_service_type().equals("Install")){
//                        multipart.addFormField("object", "install_flow");
//                    }else{
                        multipart.addFormField("object", "job_parts_used");
//                    }
                    multipart.addFormField("data[job_appliance_id]", CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_id());
                    multipart.addFormField("api", "save");
                    for (int i = 0 ; i < partsArrayList.size() ; i++){
                        multipart.addFormField("data[items][" + i + "][part_num]", partsArrayList.get(i).getNumber());
                        multipart.addFormField("data[items][" + i + "][part_cost]", partsArrayList.get(i).getCost());
                        multipart.addFormField("data[items][" + i + "][qty]", partsArrayList.get(i).getQuantity());
                        multipart.addFormField("data[items][" + i + "][part_desc]", partsArrayList.get(i).getDescription());
                    }
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
    ExceptionListener repairTypeexceptionListener= new ExceptionListener() {
        @Override
        public void handleException(int exceptionStatus) {
            handler.sendEmptyMessage(exceptionStatus);
        }
    };
    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{
                    break;
                }case 1:{
                    showAlertDialog("Fixd-Pro",error_message);
                    break;
                }case 2:{
                     singleTon.getCurrentReapirInstallProcessModal().setIsCompleted(true);
                    ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.PARTS_FRAGMENT);
                    break;
                }
            }
        }
    };
    private void showAlertDialog(String Title,String Message){
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
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
