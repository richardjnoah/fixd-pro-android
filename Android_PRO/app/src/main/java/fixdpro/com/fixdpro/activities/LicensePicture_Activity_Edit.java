package fixdpro.com.fixdpro.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import fixdpro.com.fixdpro.FixdProApplication;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.ResponseListener;
import fixdpro.com.fixdpro.imageupload.ImageHelper2;
import fixdpro.com.fixdpro.net.GetApiResponseAsyncMutipartNoProgress;
import fixdpro.com.fixdpro.utilites.Constants;
import fixdpro.com.fixdpro.utilites.ExceptionListener;
import fixdpro.com.fixdpro.utilites.MultipartUtility;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.utilites.chat_utils.ImageUtils;
import fixdpro.com.fixdpro.utilites.chat_utils.SchemeType;
import fixdpro.com.fixdpro.utilites.chat_utils.StorageUtils;
import fixdpro.com.fixdpro.utilites.image_compressor.SiliCompressor;

public class LicensePicture_Activity_Edit extends AppCompatActivity {
    Context _context = LicensePicture_Activity_Edit.this;
    public static final String TAG = "LicensePicture_Activity_Edit";
    ImageView imgClose, imgProfilePic, imgAddDriverLiecense, imgNext;
    Dialog dialog;
    Typeface fontfamily;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;

    private static final int CAMERA_REQUEST_DRIVER = 5;
    private static final int GALLERY_REQUEST_DRIVER = 6;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    boolean ispro = false;
    HashMap<String, String> finalRequestParams = null;

    public String selectedImagePathDriver, selectedImagePathUser = null;

    public Uri selectedImageUri;
    boolean iscompleting = false;
    MultipartUtility multipart ;
    String Path = "";
    public String path;

    boolean isUser = false;
    boolean isDriver = false;
    Uri uriUser,uriDriver;
    private static final String CAMERA_FILE_NAME_PREFIX = "FIXD_";
    File photoFile ;
    File photoFileDriver ;
    Dialog progressDialog;
    SharedPreferences _prefs = null ;
    String error_message = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_picture__activity__edit);
        _prefs = Utilities.getSharedPreferences(this);

        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
        setWidgets();

        setCLickListner();
        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")) {
                finalRequestParams = (HashMap<String, String>) bundle.getSerializable("finalRequestParams");
                if (_prefs.getString(Preferences.PROFILE_IMAGE,"").length() > 0){
                    Picasso.with(this).load(_prefs.getString(Preferences.PROFILE_IMAGE,""))
                            .into(imgProfilePic);
                }if (_prefs.getString(Preferences.DRIVER_LICENSE_IMAGE,"").length() > 0){
                    Picasso.with(this).load(_prefs.getString(Preferences.DRIVER_LICENSE_IMAGE, ""))
                            .into(imgAddDriverLiecense);
                }
            }

        }
    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            }
        });
        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUser = true;
                isDriver = false;
                if (Build.VERSION.SDK_INT >= 23) {
                    insertDummyContactWrapper();
                } else {
                    showCamraGalleryPopUp();
                }

            }
        });
        imgAddDriverLiecense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDriver = true;
                isUser = false;
                if (Build.VERSION.SDK_INT >= 23) {
                    insertDummyContactWrapper();
                } else {
                    showCamraGalleryPopUp();
                }

            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                GetApiResponseAsyncNew apiResponseAsyncNew = new GetApiResponseAsyncNew(Constants.BASE_URL, "POST", updateResponseListener, updateExceptionListener, LicensePicture_Activity_Edit.this, "");
//                apiResponseAsyncNew.execute(finalRequestParams);

//                if (selectedImagePathUser == null) {
//                    showAlertDialog(LicensePicture_Activity_Edit.this.getResources().getString(R.string.alert_title),
//                            "Please select user picture.");
//                    return;
//                }
//                if (selectedImagePathDriver == null) {
//                    showAlertDialog(LicensePicture_Activity_Edit.this.getResources().getString(R.string.alert_title),
//                            "Please select driver licence picture.");
//                    return;
//                }
//                if (ispro) {
//                    Intent intent = new Intent(_context, TradeLiecenseNo_Activity.class);
//                    intent.putExtra("finalRequestParams", finalRequestParams);
//                    intent.putExtra("ispro", ispro);
//                    intent.putExtra("driver_image", selectedImagePathDriver);
//                    intent.putExtra("user_image", selectedImagePathUser);
//                    intent.putExtra("iscompleting", iscompleting);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//                } else {
//                    Intent intent = new Intent(_context, TradeSkills_Activity.class);
//                    intent.putExtra("ispro", ispro);
//                    intent.putExtra("driver_image", selectedImagePathDriver);
//                    intent.putExtra("user_image", selectedImagePathUser);
//                    intent.putExtra("finalRequestParams", finalRequestParams);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.enter, R.anim.exit);
//                }
                sendRequest();
            }
        });

    }
    private void sendRequest() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new Dialog(LicensePicture_Activity_Edit.this);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.dialog_progress_simple);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                createMultiPartRequest();
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


                GetApiResponseAsyncMutipartNoProgress getApiResponseAsync = new GetApiResponseAsyncMutipartNoProgress(multipart, responseListener, exceptionListener, LicensePicture_Activity_Edit.this, "");
                getApiResponseAsync.execute();
            }
        }.execute();
    }
    ExceptionListener exceptionListener = new ExceptionListener() {
        @Override
        public void handleException(int exceptionStatus) {
            error_message = exceptionStatus+"";
            handler.sendEmptyMessage(0);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            switch (msg.what) {

                case 0: {
                    showAlertDialog("Fixd-pro", error_message);
                    break;
                }
                case 1:{
                    finish();
                    break;
                }

                case 500: {
                    showAlertDialog("Fixd-pro", "Server Error 500");
                    break;

                }
                default: {

                }
            }
        }
    };
    private MultipartUtility createMultiPartRequest(){

        try {
            multipart = new MultipartUtility(Constants.BASE_URL, Constants.CHARSET);
            for ( String key : finalRequestParams.keySet() ) {
                multipart.addFormField(key, finalRequestParams.get(key));
                Log.e("" + key, "=" + finalRequestParams.get(key));
            }
            if (Utilities.getSharedPreferences(this).getString(Preferences.ROLE,"pro").equals("pro")) {
                if (selectedImagePathDriver != null) {
                    multipart.addFilePart("data[technicians][driver_license_image]", new File(selectedImagePathDriver));
                }
                if (selectedImagePathUser != null) {
                    multipart.addFilePart("data[technicians][profile_image]", new File(selectedImagePathUser));
                }

            } else {
                if (selectedImagePathDriver != null) {
                    multipart.addFilePart("driver_license_image", new File(selectedImagePathDriver));
                }
                if (selectedImagePathUser != null) {
                    multipart.addFilePart("profile_image", new File(selectedImagePathUser));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return multipart;
    }
    private void showAlertDialog(String Title, String Message) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                LicensePicture_Activity_Edit.this);

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
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        imgAddDriverLiecense = (ImageView) findViewById(R.id.imgAddDriverLiecense);
        imgNext = (ImageView) findViewById(R.id.imgNext);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }

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

    private void showCamraGalleryPopUpDriver() {
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
                openCameraDriver();
            }
        });
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openGalleryDriver();
            }
        });
        dialog.show();
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
//        Intent camraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) == null) {
            return;
        }

        photoFile = getTemporaryCameraFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(intent, CAMERA_REQUEST);;
//        startActivityForResult(camraIntent, CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void openCameraDriver() {
        Intent driverIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(driverIntent, CAMERA_REQUEST_DRIVER);
    }

    private void openGalleryDriver() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST_DRIVER);
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
                if (isUser) {
                    selectedImagePathUser = imageFilePath;
                    photoFile = new File(selectedImagePathUser);
                    Uri uri1 = Uri.fromFile(photoFile);
                    selectedImagePathUser = SiliCompressor.with(this).compress(uri1.toString(), true);
                    photoFile = new File(selectedImagePathUser);

                    Picasso.with(this).load(Uri.fromFile(photoFile))
                            .into(imgProfilePic);
                } else {
                    selectedImagePathDriver = imageFilePath;
                    photoFile = new File(selectedImagePathDriver);
                    Uri uri1 = Uri.fromFile(photoFile);
                    selectedImagePathDriver = SiliCompressor.with(this).compress(uri1.toString(), true);
                    photoFile = new File(selectedImagePathDriver);

                    Picasso.with(this).load(Uri.fromFile(photoFile))
                            .into(imgAddDriverLiecense);
                }


                if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && isUser) {
//                isUser = false;
//
//                // Getting Data as a bitmap
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                // SettingUp bitmap to imageview
//                imgProfilePic.setImageBitmap(photo);
//                // Getting Uri of intent
//                uriUser = getImagePathURI(_context, photo);
//                Log.e(TAG,"Uri+++++++++++++++"+uriUser);
//                // Getting Image Actual Path
//                if (Build.VERSION.SDK_INT >= 23){
////                    selectedImagePathUser = gettingMarshmallowSelectedImagePath(_context, uriUser);
//                    selectedImagePathUser = ImageHelper2.compressImage(uriUser,_context);
//                    Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathUser);
//                }else{
//                    selectedImagePathUser = getPath(_context, uriUser);
//                    Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathUser);
//                }
                } else if (requestCode == CAMERA_REQUEST_DRIVER && resultCode == RESULT_OK && isDriver) {
//                isDriver = false;
//                // Getting Data as a bitmap
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                // SettingUp bitmap to imageview
//                imgAddDriverLiecense.setImageBitmap(photo);
//                // Getting uri of image
////                Uri uri = data.getData();
//                // Getting Image Actual Path
//                uriDriver = getImagePathURI(_context,photo);
//                if (Build.VERSION.SDK_INT >= 23){
////                    selectedImagePathDriver = gettingMarshmallowSelectedImagePath(_context, uriDriver);
//                    selectedImagePathDriver = ImageHelper2.compressImage(uriDriver,_context);
//                    Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathDriver);
//                }else{
//                    selectedImagePathDriver = getPath(_context,uriDriver);
//                    Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathDriver);
//                }

                } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && isUser) {
//                isUser = false;
//                // Get the uri from data
//                Uri selectedImageUri = data.getData();
//                if (null != selectedImageUri) {
//                    // Set the image in ImageView
//                    imgProfilePic.setImageURI(selectedImageUri);
//                    if (Build.VERSION.SDK_INT >= 23){
//                        selectedImagePathUser = gettingMarshmallowSelectedImagePath(_context,selectedImageUri);
//                        Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathUser);
//                    }else{
//                        // Get the actual path of image from the Uri
//                        selectedImagePathUser = getPathFromURI(selectedImageUri);
//                    }
//                }

                } else if (requestCode == GALLERY_REQUEST_DRIVER && resultCode == RESULT_OK && isDriver) {
//                isDriver = false;
//                // Get the url from data
//                Uri selectedImageUriDriver = data.getData();
//                if (null != selectedImageUriDriver) {
//                    // Set the image in ImageView
//                    imgAddDriverLiecense.setImageURI(selectedImageUriDriver);
//                    if (Build.VERSION.SDK_INT >= 23){
//                        selectedImagePathDriver = gettingMarshmallowSelectedImagePath(_context,selectedImageUriDriver);
//                        Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathDriver);
//                    }else{
//                        // Get the path from the Uri
//                        selectedImagePathDriver = getPathFromURI(selectedImageUriDriver);
//                    }
//
//                }
                }
            }
        }else if (requestCode == CAMERA_REQUEST && isDriver && requestCode == RESULT_OK) {
            selectedImagePathDriver = photoFile.getPath();
            photoFile = new File(selectedImagePathDriver);
            Uri uri1 = Uri.fromFile(photoFile);
            selectedImagePathDriver = SiliCompressor.with(this).compress(uri1.toString(), true);
            photoFile = new File(selectedImagePathDriver);


//            txtTakepic.setVisibility(View.INVISIBLE);
//            img_Camra.setVisibility(View.INVISIBLE);
            Picasso.with(this).load(Uri.fromFile(photoFile))
                    .into(imgAddDriverLiecense);
        } else if (requestCode == CAMERA_REQUEST && isUser && requestCode == RESULT_OK) {
            selectedImagePathUser = photoFile.getPath();
            photoFile = new File(selectedImagePathUser);
            Uri uri2 = Uri.fromFile(photoFile);
            selectedImagePathUser = SiliCompressor.with(this).compress(uri2.toString(), true);
            photoFile = new File(selectedImagePathUser);


//            txtTakepic.setVisibility(View.INVISIBLE);
//            img_Camra.setVisibility(View.INVISIBLE);
            Picasso.with(this).load(Uri.fromFile(photoFile))
                    .into(imgProfilePic);
        }
    }

    private String gettingMarshmallowSelectedImagePath(Context context,Uri uri){
        String imgPath = "";
        imgPath = ImageHelper2.compressImage(uri, context);
        return  imgPath;
    }

    // get the path/....
    public Uri getImagePathURI(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {
        int hasWriteCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
        int hasWriteReadExtewrnalPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasWriteExternalExtewrnalPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (hasWriteCameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (hasWriteReadExtewrnalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (hasWriteExternalExtewrnalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        showCamraGalleryPopUp();
//        if (isUser) {
//            showCamraGalleryPopUp();
//        } else if (isDriver) {
//            showCamraGalleryPopUpDriver();
//        }

        if (listPermissionsNeeded.size() > 0) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LicensePicture_Activity_Edit.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("CANCEL", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    showCamraGalleryPopUp();
                } else {
                    // Permission Denied
                    insertDummyContactWrapper();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M;

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
            // MediaStore (and general)
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
            // File
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
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


    ResponseListener responseListener = new ResponseListener() {
        @Override
    public void handleResponse(JSONObject Response) {
        Log.e("", "Response" + Response.toString());
        try {
            String STATUS = Response.getString("STATUS");
            if (STATUS.equals("SUCCESS")) {
                JSONObject RESPONSE = Response.getJSONObject("RESPONSE");
                String Token = Response.getJSONObject("RESPONSE").getString("token");
                String id = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("id");
                String role = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("role");
                String email = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("email");
                String phone = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("phone");
                String has_card = "0";
                if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("has_card")) {
                    has_card = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("has_card");
                }

                String account_status = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("account_status");
                Log.e("AUTH TOKEN", Token);
                Log.e("ROLE", role);
                JSONObject pro_settings;
                SharedPreferences.Editor editor = _prefs.edit();
                editor.putString(Preferences.ID, id);
                editor.putString(Preferences.ROLE, role);
                editor.putString(Preferences.AUTH_TOKEN, Token);
                editor.putString(Preferences.EMAIL, email);
                editor.putString(Preferences.PHONE, phone);
                editor.putString(Preferences.HAS_CARD, has_card);
                editor.putString(Preferences.ACCOUNT_STATUS, account_status);
                if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("pros")) {
                    JSONObject pros = null;
                    pros = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("pros");
                    String city = pros.getString("city");
                    String state = pros.getString("state");
                    String zip = pros.getString("zip");
                    String address = pros.getString("address");
                    String address2 = pros.getString("address_2");
                    String hourly_rate = pros.getString("hourly_rate");
                    String company_name = pros.getString("company_name");
                    String ein_number = "";
                    if (!pros.isNull("ein_number"))
                        ein_number = pros.getString("ein_number");
                    String bank_name = pros.getString("bank_name");
                    String bank_routing_number = pros.getString("bank_routing_number");
                    String bank_account_number = pros.getString("bank_account_number");
                    String bank_account_type = pros.getString("bank_account_type");
                    String insurance = pros.getString("insurance");
                    String insurance_policy = pros.getString("insurance_policy");
                    String isvarified = pros.getString("verified");
                    String working_radius_miles = pros.getString("working_radius_miles");
                    String is_pre_registered = pros.getString("is_pre_registered");
                    String latitude = pros.getString("latitude");
                    String longitude = pros.getString("longitude");
                    String avg_rating = pros.getString("avg_rating");
                    editor.putString(Preferences.CITY, city);
                    editor.putString(Preferences.STATE, state);
                    editor.putString(Preferences.ZIP, zip);
                    editor.putString(Preferences.ADDRESS, address);
                    editor.putString(Preferences.ADDRESS2, address2);
                    editor.putString(Preferences.HOURLY_RATE, hourly_rate);
                    editor.putString(Preferences.COMPANY_NAME, company_name);
                    editor.putString(Preferences.EIN_NUMEBR, ein_number);
                    editor.putString(Preferences.BANK_NAME, bank_name);
                    editor.putString(Preferences.BANK_ROUTING_NUMBER, bank_routing_number);
                    editor.putString(Preferences.BANK_ACCOUNT_NUMBER, bank_account_number);
                    editor.putString(Preferences.BANK_ACCOUNT_TYPE, bank_account_type);
                    editor.putString(Preferences.INSURANCE, insurance);
                    editor.putString(Preferences.INSURANCE_POLICY, insurance_policy);
                    editor.putString(Preferences.IS_VARIFIED, isvarified);
                    editor.putString(Preferences.WORKING_RADIUS_MILES, working_radius_miles);
                    editor.putString(Preferences.IS_PRE_REGISTERED, is_pre_registered);
                    editor.putString(Preferences.LATITUDE, latitude);
                    editor.putString(Preferences.LONGITUDE, longitude);
                    editor.putString(Preferences.AVERAGE_RATING, avg_rating);
                }

                if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("technicians")) {
                    JSONObject technicians = null;
                    technicians = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians");
                    String first_name = technicians.getString("first_name");
                    String last_name = technicians.getString("last_name");
                    String social_security_number = technicians.getString("social_security_number");
                    String years_in_business = technicians.getString("years_in_business");
//                        String trade_license_number = technicians.getString("trade_license_number");
                    editor.putString(Preferences.FIRST_NAME, first_name);
                    editor.putString(Preferences.LAST_NAME, last_name);
                    editor.putString(Preferences.SOCIAL_SECURITY_NUMBER, social_security_number);
                    editor.putString(Preferences.YEARS_IN_BUSINESS, years_in_business);
//                        editor.putString(Preferences.TRADE_LICENSE_NUMBER, trade_license_number);
                    editor.putBoolean(Preferences.ISLOGIN, true);
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").isNull("profile_image")) {
                        JSONObject profile_image = null;
                        profile_image = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").getJSONObject("profile_image");
                        if (profile_image.has("original")) {
                            String image_original = profile_image.getString("original");
                            editor.putString(Preferences.PROFILE_IMAGE, image_original);
                        }
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").isNull("driver_license_image")) {
                        JSONObject profile_image = null;
                        profile_image = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").getJSONObject("driver_license_image");
                        if (profile_image.has("original")) {
                            String image_original = profile_image.getString("original");
                            editor.putString(Preferences.DRIVER_LICENSE_IMAGE, image_original);
                        }
                    }
                }

                if (editor.commit()) {
                   handler.sendEmptyMessage(1);
                }
            } else {
                JSONObject errors = Response.getJSONObject("ERRORS");
                Iterator<String> keys = errors.keys();
                if (keys.hasNext()) {
                    String key = (String) keys.next();
                    error_message = errors.getString(key);
                }
                handler.sendEmptyMessage(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(500);
        }

    }
};

}
