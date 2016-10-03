package fixtpro.com.fixtpro.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.imageupload.ImageHelper2;
import fixtpro.com.fixtpro.round_img_ropper.ImageCropActivity;

public class LicensePicture_Activity extends AppCompatActivity {
    Context _context = LicensePicture_Activity.this;
    public static final String TAG = "LicensePicture_Activity";
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

    String Path = "";
    public String path;

    boolean isUser = false;
    boolean isDriver = false;
    Uri uriUser,uriDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_picture_);


        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
        setWidgets();

        setCLickListner();
        if (getIntent().getExtras() != null) {

            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")) {
                finalRequestParams = (HashMap<String, String>) bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")) {
                ispro = bundle.getBoolean("ispro");
            }
            if (bundle.containsKey("iscompleting")) {
                iscompleting = bundle.getBoolean("iscompleting");
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
                if (Build.VERSION.SDK_INT >= 23) {
                    insertDummyContactWrapper();
                } else {
                    showCamraGalleryPopUpDriver();
                }

            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (selectedImagePathUser == null) {
//                    showAlertDialog(LicensePicture_Activity.this.getResources().getString(R.string.alert_title),
//                            "Please select user picture.");
//                    return;
//                }
//                if (selectedImagePathDriver == null) {
//                    showAlertDialog(LicensePicture_Activity.this.getResources().getString(R.string.alert_title),
//                            "Please select driver licence picture.");
//                    return;
//                }
                if (ispro) {
                    Intent intent = new Intent(_context, TradeLiecenseNo_Activity.class);
                    intent.putExtra("finalRequestParams", finalRequestParams);
                    intent.putExtra("ispro", ispro);
                    intent.putExtra("driver_image", selectedImagePathDriver);
                    intent.putExtra("user_image", selectedImagePathUser);
                    intent.putExtra("iscompleting", iscompleting);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    Intent intent = new Intent(_context, TradeSkills_Activity.class);
                    intent.putExtra("ispro", ispro);
                    intent.putExtra("driver_image", selectedImagePathDriver);
                    intent.putExtra("user_image", selectedImagePathUser);
                    intent.putExtra("finalRequestParams", finalRequestParams);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

            }
        });

    }

    private void showAlertDialog(String Title, String Message) {
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                LicensePicture_Activity.this);

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

    private void openCamera() {
        Intent camraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camraIntent, CAMERA_REQUEST);
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
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && isUser) {
                isUser = false;

                // Getting Data as a bitmap
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // SettingUp bitmap to imageview
                imgProfilePic.setImageBitmap(photo);
                // Getting Uri of intent
                uriUser = getImagePathURI(_context, photo);
                Log.e(TAG,"Uri+++++++++++++++"+uriUser);
                // Getting Image Actual Path
                if (Build.VERSION.SDK_INT >= 23){
//                    selectedImagePathUser = gettingMarshmallowSelectedImagePath(_context, uriUser);
                    selectedImagePathUser = ImageHelper2.compressImage(uriUser,_context);
                    Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathUser);
                }else{
                    selectedImagePathUser = getPath(_context, uriUser);
                    Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathUser);
                }
            } else if (requestCode == CAMERA_REQUEST_DRIVER && resultCode == RESULT_OK && isDriver) {
                isDriver = false;
                // Getting Data as a bitmap
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // SettingUp bitmap to imageview
                imgAddDriverLiecense.setImageBitmap(photo);
                // Getting uri of image
//                Uri uri = data.getData();
                // Getting Image Actual Path
                uriDriver = getImagePathURI(_context,photo);
                if (Build.VERSION.SDK_INT >= 23){
//                    selectedImagePathDriver = gettingMarshmallowSelectedImagePath(_context, uriDriver);
                    selectedImagePathDriver = ImageHelper2.compressImage(uriDriver,_context);
                    Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathDriver);
                }else{
                    selectedImagePathDriver = getPath(_context,uriDriver);
                    Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathDriver);
                }

            } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && isUser) {
                isUser = false;
                // Get the uri from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Set the image in ImageView
                    imgProfilePic.setImageURI(selectedImageUri);
                    if (Build.VERSION.SDK_INT >= 23){
                        selectedImagePathUser = gettingMarshmallowSelectedImagePath(_context,selectedImageUri);
                        Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathUser);
                    }else{
                        // Get the actual path of image from the Uri
                        selectedImagePathUser = getPathFromURI(selectedImageUri);
                    }
                }

            } else if (requestCode == GALLERY_REQUEST_DRIVER && resultCode == RESULT_OK && isDriver) {
                isDriver = false;
                // Get the url from data
                Uri selectedImageUriDriver = data.getData();
                if (null != selectedImageUriDriver) {
                    // Set the image in ImageView
                    imgAddDriverLiecense.setImageURI(selectedImageUriDriver);
                    if (Build.VERSION.SDK_INT >= 23){
                        selectedImagePathDriver = gettingMarshmallowSelectedImagePath(_context,selectedImageUriDriver);
                        Log.e(TAG,"ActualPath+++++++++++++++"+selectedImagePathDriver);
                    }else{
                        // Get the path from the Uri
                        selectedImagePathDriver = getPathFromURI(selectedImageUriDriver);
                    }

                }
            }
        }
    }


    private String gettingMarshmallowSelectedImagePath(Context context,Uri uri){
        String imgPath = "";
        imgPath = ImageHelper2.compressImage(uri,context);
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
        if (isUser) {
            showCamraGalleryPopUp();
        } else if (isDriver) {
            showCamraGalleryPopUpDriver();
        }

        if (listPermissionsNeeded.size() > 0) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LicensePicture_Activity.this)
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





}
