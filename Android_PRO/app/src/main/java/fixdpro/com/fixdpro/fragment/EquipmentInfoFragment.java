package fixdpro.com.fixdpro.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fixdpro.com.fixdpro.FixdProApplication;
import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.ResponseListener;
import fixdpro.com.fixdpro.adapters.BrandDialogAdapter;
import fixdpro.com.fixdpro.beans.Brands;
import fixdpro.com.fixdpro.beans.install_repair_beans.EquipmentInfo;
import fixdpro.com.fixdpro.singleton.BrandNamesSingleton;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.ExceptionListener;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsync;
import fixdpro.com.fixdpro.utilites.GetApiResponseAsyncMutipart;
import fixdpro.com.fixdpro.utilites.MultipartUtility;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.utilites.chat_utils.ImageUtils;
import fixdpro.com.fixdpro.utilites.chat_utils.SchemeType;
import fixdpro.com.fixdpro.utilites.chat_utils.StorageUtils;
import fixdpro.com.fixdpro.utilites.image_compressor.SiliCompressor;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EquipmentInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EquipmentInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EquipmentInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    CurrentScheduledJobSingleTon singleTon = null;
    EditText editMOdalNum, editSerialNum, editDescription;
    TextView edit_Brand;
    ImageView imgMain, img_Camra;
    LinearLayout multiPicView, singlePicView;
    RelativeLayout picHolder1, picHolder2, picHolder3, picHolder4, picHolder5, picHolder6, picHolder7, picHolder8;
    ImageView pic1, pic2, pic3, pic4, pic5, pic6, pic7, pic8;
    TextView txtTakepic;
    Typeface fontfamily;
    Dialog dialog = null;
    Context _context = null;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    public String Path = "", path;
    public Uri selectedImageUri;
    int finalHeight, finalWidth;
    String brand = "brand", modal_number = "", serial_number = "", description = "";
    EquipmentInfo equipmentInfo = null;
    MultipartUtility multipart = null;
    String error_message = "";
    Dialog dialog1 = null;
    Dialog subDialog = null;
    BrandDialogAdapter mAdp;
    ArrayList<Brands> arrayListBrands = BrandNamesSingleton.getInstance().getBrands();
    private static final int REQUEST_READ_STORAGE = 111;
    private static final String CAMERA_FILE_NAME_PREFIX = "FIXD_";
    File photoFile ;
    boolean isPhoto1Changed, isPhoto2Changed, isPhoto3Changed, isPhoto4Changed, isPhoto5Changed, isPhoto6Changed, isPhoto7Changed, isPhoto8Changed;
    int currentImageIndex = 0 ;
    int uploadImageIndex = 0;
    ArrayList<String> localImageUrls = CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getInstallOrRepairModal().getEquipmentInfo().getImgLocalUrls();
    ArrayList<String> serverImageUrls = CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getInstallOrRepairModal().getEquipmentInfo().getImgServerUrls();

    public EquipmentInfoFragment() {
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
        equipmentInfo = singleTon.getJobApplianceModal().getInstallOrRepairModal().getEquipmentInfo();
        _context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeScreenNew) getActivity()).setCurrentFragmentTag(Constants.EQUIPMENT_FRAGMENT);
        setupToolBar();

    }

    private void setupToolBar() {
        ((HomeScreenNew) getActivity()).setRightToolBarText("Done");
        ((HomeScreenNew) getActivity()).setTitletext("Equipment Info");
        ((HomeScreenNew) getActivity()).setLeftToolBarText("Back");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_equipment_info, container, false);
        resetLocalImageUrl();
        setWidgets(view);
        setListener();
        initLayout();
        return view;
    }
    private void initLayout(){
        edit_Brand.setText(equipmentInfo.getBrand_name());
        editMOdalNum.setText(equipmentInfo.getModel_number());
        editSerialNum.setText(equipmentInfo.getSerial_number());
        editDescription.setText(equipmentInfo.getDescription());
        if (equipmentInfo.getImage().length() > 0){
            if (equipmentInfo.isLocal()){
//                Loadimage  from loccally
                Uri uri = Uri.fromFile(new File(equipmentInfo.getImage()));
                Picasso.with(getActivity()).load(uri)
                        .into(imgMain);
//                txtTakepic.setVisibility(View.INVISIBLE);
                img_Camra.setVisibility(View.INVISIBLE);
            }else{
//                Load image form server
                Picasso.with(getActivity()).load(equipmentInfo.getImage())
                        .into(imgMain);
//                txtTakepic.setVisibility(View.INVISIBLE);
                img_Camra.setVisibility(View.INVISIBLE);
            }
        }

        // Reset Pics
        for (int imageIndex = 0; imageIndex < serverImageUrls.size() ; imageIndex ++){
            switch (imageIndex + 1){
                case 1:
                    Picasso.with(getActivity()).load(serverImageUrls.get(imageIndex))
                            .into(pic1);
                    break;
                case 2:
                    Picasso.with(getActivity()).load(serverImageUrls.get(imageIndex))
                            .into(pic2);
                    break;
                case 3:
                    Picasso.with(getActivity()).load(serverImageUrls.get(imageIndex))
                            .into(pic3);
                    break;
                case 4:
                    Picasso.with(getActivity()).load(serverImageUrls.get(imageIndex))
                            .into(pic4);
                    break;
                case 5:
                    Picasso.with(getActivity()).load(serverImageUrls.get(imageIndex))
                            .into(pic5);
                    break;
                case 6:
                    Picasso.with(getActivity()).load(serverImageUrls.get(imageIndex))
                            .into(pic6);
                    break;
                case 7:
                    Picasso.with(getActivity()).load(serverImageUrls.get(imageIndex))
                            .into(pic7);
                    break;
                case 8:
                    Picasso.with(getActivity()).load(serverImageUrls.get(imageIndex))
                            .into(pic8);
                    break;
            }
        }

        hideUnhidePhotoButtons();
    }

    private void resetLocalImageUrl(){
        int serverImageUrlCount = serverImageUrls.size();
        if (localImageUrls.size() > 0 && localImageUrls.size() > serverImageUrlCount){
            for (int i = localImageUrls.size() ; i < serverImageUrlCount ; i++){
                localImageUrls.set(i, null);
            }
        }
    }

    private void setListener() {
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
        edit_Brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDropDownWithDialog();
            }
        });
        picHolder1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoClicked(v);
            }
        });
        picHolder2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoClicked(v);
            }
        });
        picHolder3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoClicked(v);
            }
        });
        picHolder4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoClicked(v);
            }
        });
        picHolder5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoClicked(v);
            }
        });
        picHolder6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoClicked(v);
            }
        });
        picHolder7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoClicked(v);
            }
        });
        picHolder8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhotoClicked(v);
            }
        });

    }

    private void addPhotoClicked(View v){
        currentImageIndex = Integer.valueOf((String)v.getTag());
        showCamraGalleryPopUp();
    }


    private void setDropDownWithDialog() {
        final String[] selectedBrand = {""};
//        Brands brands1 = new Brands();
//        brands1.setBrand_name("Other Brand");
//        arrayListBrands.add(brands1);
        dialog1 = new Dialog(_context);
        dialog1 = new Dialog(_context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.layout_tellus_more);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog1 components -listview
        ListView lst_TellUsMore = (ListView) dialog1.findViewById(R.id.lst_TellUsMore);
                /*To set the Adapter*/
        mAdp = new BrandDialogAdapter(arrayListBrands, getActivity());
        lst_TellUsMore.setAdapter(mAdp);
        lst_TellUsMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBrand[0] = arrayListBrands.get(position).getBrand_name();
                Log.e("selectedBrand", "selectedBrand" + selectedBrand);
                edit_Brand.setText(selectedBrand[0]);
                if (arrayListBrands.get(position).getBrand_name().equals("Other Brand")) {
                    dialog1.dismiss();
                    subDialog = new Dialog(getActivity());
                    subDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    subDialog.setContentView(R.layout.layout_subdialog);
                    subDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    final EditText editBrandName = (EditText) subDialog.findViewById(R.id.editBrandName);
                    TextView txtOK = (TextView) subDialog.findViewById(R.id.txtOK);
                    ImageView img_Cancel = (ImageView) subDialog.findViewById(R.id.img_Cancel);
                    img_Cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            subDialog.dismiss();
                        }
                    });
                    txtOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editBrandName.getText().toString().equals("")) {
                                editBrandName.setError("Please Enter Brand Name.");
                            } else {
                                subDialog.dismiss();
                                edit_Brand.setText(editBrandName.getText().toString());
                            }
                        }
                    });
                    subDialog.show();
                }
                dialog1.dismiss();
            }
        });
        dialog1.show();


    }

    private void setWidgets(View view) {
        edit_Brand = (TextView) view.findViewById(R.id.edit_Brand);
        editMOdalNum = (EditText) view.findViewById(R.id.editMOdalNum);
        editSerialNum = (EditText) view.findViewById(R.id.editSerialNum);
        editDescription = (EditText) view.findViewById(R.id.editDescription);
        imgMain = (ImageView) view.findViewById(R.id.imgMain);
        img_Camra = (ImageView) view.findViewById(R.id.img_Camra);
        txtTakepic = (TextView) view.findViewById(R.id.txtTakepic);
        fontfamily = Typeface.createFromAsset(getActivity().getAssets(), "HelveticaNeue-Thin.otf");
        singlePicView = (LinearLayout) view.findViewById(R.id.singlePicView);
        multiPicView = (LinearLayout) view.findViewById(R.id.multiPicView);
        pic1 = (ImageView) view.findViewById(R.id.pic1);
        pic2 = (ImageView) view.findViewById(R.id.pic2);
        pic3 = (ImageView) view.findViewById(R.id.pic3);
        pic4 = (ImageView) view.findViewById(R.id.pic4);
        pic5 = (ImageView) view.findViewById(R.id.pic5);
        pic6 = (ImageView) view.findViewById(R.id.pic6);
        pic7 = (ImageView) view.findViewById(R.id.pic7);
        pic8 = (ImageView) view.findViewById(R.id.pic8);
        picHolder1 = (RelativeLayout) view.findViewById(R.id.picHolder1);
        picHolder2 = (RelativeLayout) view.findViewById(R.id.picHolder2);
        picHolder3 = (RelativeLayout) view.findViewById(R.id.picHolder3);
        picHolder4 = (RelativeLayout) view.findViewById(R.id.picHolder4);
        picHolder5 = (RelativeLayout) view.findViewById(R.id.picHolder5);
        picHolder6 = (RelativeLayout) view.findViewById(R.id.picHolder6);
        picHolder7 = (RelativeLayout) view.findViewById(R.id.picHolder7);
        picHolder8 = (RelativeLayout) view.findViewById(R.id.picHolder8);
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

    public void submitPost() {
        brand =edit_Brand.getText().toString().trim();
        modal_number = editMOdalNum.getText().toString().trim();
        serial_number = editSerialNum.getText().toString().trim();
        description = editDescription.getText().toString().trim();
        if (brand.length() == 0) {
            showAlertDialog("Fixd-Pro", "please select brand.");
        } else if (modal_number.length() == 0) {
            showAlertDialog("Fixd-Pro", "please enter modal number");
        } else if (serial_number.length() == 0) {
            showAlertDialog("Fixd-Pro", "please enter serial number");
        } else if (description.length() == 0) {
            showAlertDialog("Fixd-Pro", "please enter unit work description");
        } else if (equipmentInfo.getImage().length() == 0) {
            if (localImageUrls.size() > 0){
                uploadEquipmentInfoToServer();
            }else {
                showAlertDialog("Fixd-Pro", "please add image");
            }

        } else {
            uploadEquipmentInfoToServer();
        }
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
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    boolean hasPermissionCamra = (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);

                    if (!hasPermission || !hasPermissionCamra) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                REQUEST_READ_STORAGE);
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        });
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_READ_STORAGE);
                    } else {
                        openGallery();
                    }
                } else {
                    openGallery();
                }
            }
        });
        dialog.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    openGallery();
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission){
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_READ_STORAGE);
                    }
                }
            }
        }

    }
    private static String getTemporaryCameraFileName() {
        return CAMERA_FILE_NAME_PREFIX + System.currentTimeMillis() + ".jpg";
    }
    public static File getTemporaryCameraFile() {
        File storageDir = StorageUtils.getAppExternalDataDirectoryFile();
        File file = new File(storageDir, getTemporaryCameraFileName());
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    private void openCamera() {
//        Intent camraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(camraIntent, CAMERA_REQUEST);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
            return;
        }

        photoFile = getTemporaryCameraFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(intent, CAMERA_REQUEST);;
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
            String imageFilePath = null;
            Uri uri = data.getData();
            String uriScheme = uri.getScheme();
            boolean isFromGoogleApp = uri.toString().startsWith(SchemeType.SCHEME_CONTENT_GOOGLE);
            boolean isKitKatAndUpper = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            if (SchemeType.SCHEME_CONTENT.equalsIgnoreCase(uriScheme) && !isFromGoogleApp && !isKitKatAndUpper) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = FixdProApplication.getInstance().getContentResolver().query(uri, filePathColumn, null, null, null);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imageFilePath = cursor.getString(columnIndex);
                    }
                    cursor.close();
                }
            } else if (SchemeType.SCHEME_FILE.equalsIgnoreCase(uriScheme)) {
                imageFilePath = uri.getPath();
            } else {
                try {
                    imageFilePath = ImageUtils.saveUriToFile(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!TextUtils.isEmpty(imageFilePath)) {

                Path = imageFilePath ;
                photoFile = new File(Path);
                Uri uri1 = Uri.fromFile(photoFile);
                Path = SiliCompressor.with(getActivity()).compress(uri1.toString(), true);
                photoFile = new File(Path);

                img_Camra.setVisibility(View.INVISIBLE);
                Picasso.with(getActivity()).load(Uri.fromFile(photoFile))
                        .into(imgMain);
            }
        }
        else if (requestCode == CAMERA_REQUEST && Activity.RESULT_OK == resultCode){
            Path = photoFile.getPath() ;
            photoFile = new File(Path);
            Uri uri = Uri.fromFile(photoFile);
            Path = SiliCompressor.with(getActivity()).compress(uri.toString(), true);
            photoFile = new File(Path);

            img_Camra.setVisibility(View.INVISIBLE);
            Picasso.with(getActivity()).load(Uri.fromFile(photoFile))
                    .into(imgMain);
        }

        // Save Image
        if (localImageUrls.size() > currentImageIndex){
            localImageUrls.set(currentImageIndex, Path);
        } else {
            localImageUrls.add(Path);
        }
        hideUnhidePhotoButtons();
        setImageOnPhotoButton();
        resetBoolForImageChanged();
    }

    private void hideUnhidePhotoButtons(){

        singlePicView.setVisibility(View.GONE);
        multiPicView.setVisibility(View.VISIBLE);
        int count = localImageUrls.size();

        picHolder1.setVisibility(View.INVISIBLE);
        picHolder2.setVisibility(View.INVISIBLE);
        picHolder3.setVisibility(View.INVISIBLE);
        picHolder4.setVisibility(View.INVISIBLE);
        picHolder5.setVisibility(View.INVISIBLE);
        picHolder6.setVisibility(View.INVISIBLE);
        picHolder7.setVisibility(View.INVISIBLE);
        picHolder8.setVisibility(View.INVISIBLE);

        switch (count){
            case 0:
                multiPicView.setVisibility(View.GONE);
                singlePicView.setVisibility(View.VISIBLE);
                break;
            case 1:
                picHolder1.setVisibility(View.VISIBLE);
                picHolder2.setVisibility(View.VISIBLE);
                break;
            case 2:
                picHolder1.setVisibility(View.VISIBLE);
                picHolder2.setVisibility(View.VISIBLE);
                picHolder3.setVisibility(View.VISIBLE);
                break;
            case 3:
                picHolder1.setVisibility(View.VISIBLE);
                picHolder2.setVisibility(View.VISIBLE);
                picHolder3.setVisibility(View.VISIBLE);
                picHolder4.setVisibility(View.VISIBLE);
                break;
            case 4:
                picHolder1.setVisibility(View.VISIBLE);
                picHolder2.setVisibility(View.VISIBLE);
                picHolder3.setVisibility(View.VISIBLE);
                picHolder4.setVisibility(View.VISIBLE);
                picHolder5.setVisibility(View.VISIBLE);
                break;
            case 5:
                picHolder1.setVisibility(View.VISIBLE);
                picHolder2.setVisibility(View.VISIBLE);
                picHolder3.setVisibility(View.VISIBLE);
                picHolder4.setVisibility(View.VISIBLE);
                picHolder5.setVisibility(View.VISIBLE);
                picHolder6.setVisibility(View.VISIBLE);
                break;
            case 6:
                picHolder1.setVisibility(View.VISIBLE);
                picHolder2.setVisibility(View.VISIBLE);
                picHolder3.setVisibility(View.VISIBLE);
                picHolder4.setVisibility(View.VISIBLE);
                picHolder5.setVisibility(View.VISIBLE);
                picHolder6.setVisibility(View.VISIBLE);
                picHolder7.setVisibility(View.VISIBLE);
                break;
            case 7:
            case 8:
                picHolder1.setVisibility(View.VISIBLE);
                picHolder2.setVisibility(View.VISIBLE);
                picHolder3.setVisibility(View.VISIBLE);
                picHolder4.setVisibility(View.VISIBLE);
                picHolder5.setVisibility(View.VISIBLE);
                picHolder6.setVisibility(View.VISIBLE);
                picHolder7.setVisibility(View.VISIBLE);
                picHolder8.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void setImageOnPhotoButton(){

        switch (currentImageIndex + 1){
            case 1:
                Picasso.with(getActivity()).load(Uri.fromFile(new File(localImageUrls.get(0))))
                        .into(pic1);
                break;
            case 2:
                Picasso.with(getActivity()).load(Uri.fromFile(new File(localImageUrls.get(1))))
                        .into(pic2);
                break;
            case 3:
                Picasso.with(getActivity()).load(Uri.fromFile(new File(localImageUrls.get(2))))
                        .into(pic3);
                break;
            case 4:
                Picasso.with(getActivity()).load(Uri.fromFile(new File(localImageUrls.get(3))))
                        .into(pic4);
                break;
            case 5:
                Picasso.with(getActivity()).load(Uri.fromFile(new File(localImageUrls.get(4))))
                        .into(pic5);
                break;
            case 6:
                Picasso.with(getActivity()).load(Uri.fromFile(new File(localImageUrls.get(5))))
                        .into(pic6);
                break;
            case 7:
                Picasso.with(getActivity()).load(Uri.fromFile(new File(localImageUrls.get(6))))
                        .into(pic7);
                break;
            case 8:
                Picasso.with(getActivity()).load(Uri.fromFile(new File(localImageUrls.get(7))))
                        .into(pic8);
                break;
        }
    }

    private void resetBoolForImageChanged(){
        switch (currentImageIndex){
            case 0:
                isPhoto1Changed = true;
                break;
            case 1:
                isPhoto2Changed = true;
                break;
            case 2:
                isPhoto3Changed = true;
                break;
            case 3:
                isPhoto4Changed = true;
                break;
            case 4:
                isPhoto5Changed = true;
                break;
            case 5:
                isPhoto6Changed = true;
                break;
            case 6:
                isPhoto7Changed = true;
                break;
            case 7:
                isPhoto8Changed = true;
                break;
        }
    }


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
    // get path from the gallery...
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void showAlertDialog(String Title, String Message) {
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



    public void uploadEquipmentInfoToServer(){
        if (isPhoto1Changed || isPhoto2Changed || isPhoto3Changed || isPhoto4Changed || isPhoto5Changed || isPhoto6Changed || isPhoto7Changed || isPhoto8Changed){
            checkAndUploadImagesSequentially();
        } else {
            saveEquipmentInfoServer();
        }
    }

    private void uploadImageAtIndex(int imageIndex){

        uploadImageIndex = imageIndex;

        new AsyncTask<Integer, Void, String>() {

            @Override
            protected String doInBackground(Integer... params) {
                try {
                    multipart = new MultipartUtility(Constants.BASE_URL, Constants.CHARSET);
                    multipart.addFormField("api", "save_slot");
                    multipart.addFormField("object", "equipment_images");
                    multipart.addFormField("data[slot]", String.valueOf(params[0]+1));
                    multipart.addFormField("_app_id", "FIXD_ANDROID_PRO");
                    multipart.addFormField("_company_id", "FIXD");
                    multipart.addFormField("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, ""));
                    multipart.addFormField("data[job_appliance_id]", CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_id());
                    if (Path != null && !Path.equals(""))
                        multipart.addFilePart("data[image]", new File(Path));


                } catch (IOException e) {
                    Log.e("eeeee", e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                GetApiResponseAsyncMutipart getApiResponseAsync = new GetApiResponseAsyncMutipart(multipart, repairTypeResponseListener, repairTypeexceptionListener, getActivity(), "Uploading");
                getApiResponseAsync.execute();
            }
        }.execute(imageIndex);
    }

    private void saveEquipmentInfoServer() {

        GetApiResponseAsync responseAsyncCompleted = new GetApiResponseAsync("POST", responseListenerSaveEquipmentInfo, getActivity(), "Saving");
        responseAsyncCompleted.execute(getRequestParams());
    }

    public void checkAndUploadImagesSequentially() {

        if (isPhoto1Changed){
            uploadImageAtIndex(0);
        } else if (isPhoto2Changed){
            uploadImageAtIndex(1);
        } else if (isPhoto3Changed){
            uploadImageAtIndex(2);
        } else if (isPhoto4Changed){
            uploadImageAtIndex(3);
        } else if (isPhoto5Changed){
            uploadImageAtIndex(4);
        } else if (isPhoto6Changed){
            uploadImageAtIndex(5);
        } else if (isPhoto7Changed){
            uploadImageAtIndex(6);
        } else if (isPhoto8Changed){
            uploadImageAtIndex(7);
        } else {
            saveEquipmentInfoServer();
        }

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {

                    break;
                }
                case 1: {
                    showAlertDialog("Fixd-Pro", error_message);
                    break;
                }
                case 2: {

                    if (Path != null && Path.length() > 0){
                        equipmentInfo.setImage(Path);
                        equipmentInfo.setIsLocal(true);
                    }

                    equipmentInfo.setBrand_name(brand);
                    equipmentInfo.setModel_number(modal_number);
                    equipmentInfo.setSerial_number(serial_number);
                    equipmentInfo.setDescription(description);
                    singleTon.getCurrentReapirInstallProcessModal().setIsCompleted(true);
                    CurrentScheduledJobSingleTon.getInstance().getInstallOrRepairModal().setEquipmentInfo(equipmentInfo);

                    singleTon.getJobApplianceModal().getInstallOrRepairModal().setEquipmentInfo(equipmentInfo);
                    singleTon.getJobApplianceModal().getInstallOrRepairModal().getEquipmentInfo().setIsCompleted(true);
                    ((HomeScreenNew) getActivity()).popInclusiveFragment(Constants.EQUIPMENT_FRAGMENT);
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
                if (Response.getString("STATUS").equals("SUCCESS")) {

                    if (isPhoto1Changed){
                        isPhoto1Changed = false;
                    } else if (isPhoto2Changed){
                        isPhoto2Changed = false;
                    } else if (isPhoto3Changed){
                        isPhoto3Changed = false;
                    } else if (isPhoto4Changed){
                        isPhoto4Changed = false;
                    } else if (isPhoto5Changed){
                        isPhoto5Changed = false;
                    } else if (isPhoto6Changed){
                        isPhoto6Changed = false;
                    } else if (isPhoto7Changed){
                        isPhoto7Changed = false;
                    } else if (isPhoto8Changed){
                        isPhoto8Changed = false;
                    }

                    String uploadedImageUrl = Response.getJSONObject("RESPONSE").getJSONObject("image").getString("original");

                    if (serverImageUrls.size() > uploadImageIndex){
                        serverImageUrls.set(uploadImageIndex, uploadedImageUrl);
                    } else {
                        serverImageUrls.add(uploadedImageUrl);
                    }

                    checkAndUploadImagesSequentially();
//                    handler.sendEmptyMessage(2);

                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                        handler.sendEmptyMessage(1);
                    }
                }

            } catch (JSONException e) {

            }
        }
    };
    ExceptionListener repairTypeexceptionListener = new ExceptionListener() {
        @Override
        public void handleException(int exceptionStatus) {
            handler.sendEmptyMessage(exceptionStatus);
        }
    };


    private HashMap<String,String> getRequestParams(){
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","save");
        hashMap.put("object","equipment_info");
        hashMap.put("data[job_appliance_id]", CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_id());
        hashMap.put("token", Utilities.getSharedPreferences(getActivity()).getString(Preferences.AUTH_TOKEN, null));
        hashMap.put("data[brand_name]", brand);
        hashMap.put("data[model_number]", modal_number);
        hashMap.put("data[serial_number]", serial_number);
        hashMap.put("data[description]", description);

        return hashMap;
    }

    ResponseListener responseListenerSaveEquipmentInfo = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response.toString()" + Response.toString());
            try {
                if(Response.getString("STATUS").equals("SUCCESS")){

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
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
