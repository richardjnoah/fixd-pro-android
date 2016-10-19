package fixdpro.com.fixdpro.fragment;

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
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.ResponseListener;
import fixdpro.com.fixdpro.beans.install_repair_beans.Parts;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncNew;
import fixdpro.com.fixdpro.net.IHttpExceptionListener;
import fixdpro.com.fixdpro.net.IHttpResponseListener;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.ExceptionListener;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsyncMutipart;
import fixdpro.com.fixdpro.utilites.MultipartUtility;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

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
    ScrollView scrollView ;
    TextView txtTapAddParts,txtEnterPartsCost, txtDisposalFees ;
    LinearLayout layout_add_part ,layout_no_part, layout_main, layout_bottom_view,layout_plus;
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
        if (partsArrayList.size() > 0){
//            partsArrayList.add(new Parts());
            setView();
            scrollView.setVisibility(View.VISIBLE);
            layout_bottom_view.setVisibility(View.VISIBLE);
            layout_add_part.setVisibility(View.VISIBLE);
            layout_main.setVisibility(View.GONE);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }else {
            layout_main.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            layout_bottom_view.setVisibility(View.GONE);
        }
        txtTapAddParts.setText(Html.fromHtml(getResources().getString(R.string.tap_add_parts)));
        txtDisposalFees.setText(Html.fromHtml(getResources().getString(R.string.disposal_fess)));
        txtEnterPartsCost.setText(Html.fromHtml(getResources().getString(R.string.enter_parts_cost)));
        return view;
    }

    private void setWidgets(View view){
        img_add = (ImageView)view.findViewById(R.id.img_add);
        img_add = (ImageView)view.findViewById(R.id.img_add);
        container_layout = (LinearLayout)view.findViewById(R.id.container_layout);
        scrollView = (ScrollView)view.findViewById(R.id.scrollView);
        txtTapAddParts = (TextView)view.findViewById(R.id.txtTapAddParts);
        txtDisposalFees = (TextView)view.findViewById(R.id.txtDisposalFees);
        txtEnterPartsCost = (TextView)view.findViewById(R.id.txtEnterPartsCost);

        layout_add_part = (LinearLayout)view.findViewById(R.id.layout_add_part);
        layout_plus = (LinearLayout)view.findViewById(R.id.layout_plus);
        layout_no_part = (LinearLayout)view.findViewById(R.id.layout_no_part);
        layout_main = (LinearLayout)view.findViewById(R.id.layout_main);
        layout_bottom_view = (LinearLayout)view.findViewById(R.id.layout_bottom_view);

    }
    private void setListeners(){
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scrollView.setVisibility(View.VISIBLE);
                layout_main.setVisibility(View.GONE);
                container_layout.removeAllViews();
                partsArrayList.add(new Parts());
                setView();

            }
        });
        layout_no_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noPartsNeeded();
            }
        });
        layout_add_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeScreenNew)getActivity()).setRightToolBarText("Done");
                scrollView.setVisibility(View.VISIBLE);
                layout_plus.setVisibility(View.VISIBLE);
                layout_main.setVisibility(View.GONE);
                layout_bottom_view.setVisibility(View.VISIBLE);
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
            final EditText txtPartNumber = (EditText)addView.findViewById(R.id.txtPartNumber);
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
        ((HomeScreenNew)getActivity()).hideRight();
        ((HomeScreenNew)getActivity()).setTitletext("Parts Used");
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
//        executeAddPartsRequest();
        GetApiResponseAsyncNew noPartsNeededAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",addPartsResponseListener,addPartsExceptionListener,getActivity(),"");
        noPartsNeededAsync.execute(getAddPartsNeededRequestParams());

    }

    IHttpResponseListener addPartsResponseListener = new IHttpResponseListener() {
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
                e.printStackTrace();
            }
        }
    };
    IHttpExceptionListener addPartsExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception ;
            handler.sendEmptyMessage(1);
        }
    };
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
    private HashMap<String,String> getAddPartsNeededRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","save");
        hashMap.put("object","job_parts_used");
        hashMap.put("data[job_appliance_id]",CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_id());
        for (int i = 0 ; i < partsArrayList.size() ; i++){
            if (partsArrayList.get(i).getNumber().toString().trim().length() != 0 && partsArrayList.get(i).getCost().toString().trim().length() != 0 && partsArrayList.get(i).getQuantity().toString().trim().length() != 0 && partsArrayList.get(i).getDescription().toString().trim().length() != 0){
                hashMap.put("data[items][" + i + "][part_num]", partsArrayList.get(i).getNumber());
                hashMap.put("data[items][" + i + "][part_cost]", partsArrayList.get(i).getCost());
                hashMap.put("data[items][" + i + "][qty]", partsArrayList.get(i).getQuantity());
                hashMap.put("data[items][" + i + "][part_desc]", partsArrayList.get(i).getDescription());
            }

        }
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, ""));
        return hashMap;
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
                    container_layout.removeAllViews();
//                    partsArrayList.add(new Parts());
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



    private void noPartsNeeded(){
        submitPost();
    }
}

