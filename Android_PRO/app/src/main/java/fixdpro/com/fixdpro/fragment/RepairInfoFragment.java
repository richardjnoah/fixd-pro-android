package fixdpro.com.fixdpro.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.ResponseListener;
import fixdpro.com.fixdpro.beans.install_repair_beans.RepairInfo;
import fixdpro.com.fixdpro.imageupload.ImageHelper2;
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
 * {@link RepairInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RepairInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepairInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    CurrentScheduledJobSingleTon singleTon = null ;
    EditText edit_UnitManufac,editMOdalNum,editSerialNum,editDescription;
    ImageView imgMain,img_Camra;
    TextView txtTakepic ;
    Typeface fontfamily;
    Dialog dialog = null ;
    Context _context = null ;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    public String Path = "",path;
    public Uri selectedImageUri;
    int finalHeight, finalWidth;
    String unit_manufacturer = "",modal_number = "",serial_number = "",description = "";
    RepairInfo repairInfo = null ;
    MultipartUtility multipart = null;
    String error_message = "";
    public RepairInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RepairInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RepairInfoFragment newInstance(String param1, String param2) {
        RepairInfoFragment fragment = new RepairInfoFragment();
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
        repairInfo = singleTon.getJobApplianceModal().getInstallOrRepairModal().getRepairInfo();
        _context = getActivity() ;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew)getActivity()).setCurrentFragmentTag(Constants.REPAIR_INFO_FRAGMENT);
         setupToolBar();

    }
    private void setupToolBar(){
        ((HomeScreenNew)getActivity()).setRightToolBarText("Next");
        ((HomeScreenNew)getActivity()).setTitletext("Repair Information");
        ((HomeScreenNew)getActivity()).setLeftToolBarText("Back");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_repair_info, container, false);
        setWidgets(view);
        setListener();
        return view;
    }
        private void setListener(){
            txtTakepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCamraGalleryPopUp();
                }
            });
            imgMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCamraGalleryPopUp();
                }
            });
            img_Camra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCamraGalleryPopUp();
                }
            });

        }
        private void setWidgets(View view){
            edit_UnitManufac = (EditText)view.findViewById(R.id.edit_UnitManufac);
            editMOdalNum = (EditText)view.findViewById(R.id.editMOdalNum);
            editSerialNum = (EditText)view.findViewById(R.id.editSerialNum);
            editDescription = (EditText)view.findViewById(R.id.editDescription);
            imgMain = (ImageView)view.findViewById(R.id.imgMain);
            img_Camra = (ImageView)view.findViewById(R.id.img_Camra);
            txtTakepic = (TextView)view.findViewById(R.id.txtTakepic);
            fontfamily = Typeface.createFromAsset(getActivity().getAssets(), "HelveticaNeue-Thin.otf");
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
        unit_manufacturer =edit_UnitManufac.getText().toString().trim();
        modal_number =editMOdalNum.getText().toString().trim();
        serial_number =editSerialNum.getText().toString().trim();
        description =editDescription.getText().toString().trim();
        if (unit_manufacturer.length() == 0){
            showAlertDialog("Fixd-Pro","please enter unit manufacturer");
        }else if (modal_number.length() == 0){
            showAlertDialog("Fixd-Pro","please enter modal number");
        }else if (serial_number.length() == 0){
            showAlertDialog("Fixd-Pro","please enter serial number");
        }else if (description.length() == 0){
            showAlertDialog("Fixd-Pro","please enter unit work description");
        }else if (Path.length() == 0){
            showAlertDialog("Fixd-Pro","please add image");
        }else{
            executeRepairInfoSaveingRequest();
        }

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
    /*Create Camra Gallery PopUP*/
    private void showCamraGalleryPopUp() {
        dialog = new Dialog(_context);
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_camra_gallery);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        TextView txtTakePicture = (TextView) dialog.findViewById(R.id.txtTakePicture);
        TextView txtCamera = (TextView) dialog.findViewById(R.id.txtCamera);
        TextView txtGallery = (TextView) dialog.findViewById(R.id.txtGallery);
        // set the typeface...
        txtCamera.setTypeface(fontfamily);
        txtGallery.setTypeface(fontfamily);
        txtTakePicture.setTypeface(fontfamily);
        // set the click listner...
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openCamera();
            }
        });
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openGallery();
            }
        });
        dialog.show();
    }

    private  void openCamera(){
        Intent camraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camraIntent, CAMERA_REQUEST);
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == CAMERA_REQUEST) {
                try {
                    selectedImageUri = data.getData();
                    Path = ImageHelper2.compressImage(selectedImageUri, getActivity());
                    txtTakepic.setVisibility(View.INVISIBLE);
                    img_Camra.setVisibility(View.INVISIBLE);
                    Picasso.with(getActivity()).load(selectedImageUri)
                            .into(imgMain);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == GALLERY_REQUEST) {
                Uri selectedImageUri = data.getData();
                Path = getPath(selectedImageUri);
                txtTakepic.setVisibility(View.INVISIBLE);
                img_Camra.setVisibility(View.INVISIBLE);
//                imgMain.getLayoutParams().height = finalHeight;
//                imgMain.getLayoutParams().width = finalWidth;
//                imgDriver.setImageBitmap(ImageHelper2.decodeSampledBitmapFromFile(Path, finalWidth, finalHeight));
                Picasso.with(getActivity()).load(selectedImageUri)
                        .into(imgMain);
            }
        }
    }
    // get path from the gallery...
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
    public void executeRepairInfoSaveingRequest(){
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    multipart = new MultipartUtility(Constants.BASE_URL, Constants.CHARSET);
                    if (CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_service_type().equals("Install")){
                        multipart.addFormField("api", "install_info");
                        multipart.addFormField("object", "install_flow");

                    }else{
                        multipart.addFormField("api", "repair_info");
                        multipart.addFormField("object", "repair_flow");

                    }
                    multipart.addFilePart("data[image]", new File(Path));
                    multipart.addFormField("data[model_number]", modal_number);
                    multipart.addFormField("data[unit_manufacturer]", unit_manufacturer);
                    multipart.addFormField("data[serial_number]", serial_number);
                    multipart.addFormField("data[work_description]", description);
                    multipart.addFormField("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, ""));
                    multipart.addFormField("data[job_appliance_id]", CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_id());
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
    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:{

                    break;
                }case 1:{
                    showAlertDialog("Fixd-Pro", error_message);
                    break;
                }case 2:{
                    repairInfo.setImage(path);
                    repairInfo.setUnitManufacturer(unit_manufacturer);
                    repairInfo.setModalNumber(modal_number);
                    repairInfo.setSerialNumber(serial_number);
                    repairInfo.setWorkDescription(description);
                    singleTon.getCurrentReapirInstallProcessModal().setIsCompleted(true);
                    CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().setRepairInfo(repairInfo);
                    ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.REPAIR_INFO_FRAGMENT);
                    break;
                }
            }
        }
    };
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
}
