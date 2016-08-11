package fixtpro.com.fixtpro.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.imageupload.ImageHelper2;

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
    boolean ispro = false ;
    HashMap<String,String> finalRequestParams = null ;

    public String selectedImagePathDriver,selectedImagePathUser=null;

    public Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_picture_);


        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
        setWidgets();

        setCLickListner();
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")){
                finalRequestParams = (HashMap<String,String>)bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")){
                ispro = bundle.getBoolean("ispro");
            }
        }
    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    insertDummyContactWrapper();
                }else{
                    showCamraGalleryPopUp();
                }

            }
        });
        imgAddDriverLiecense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent intent = new Intent(_context, TradeLiecenseNo_Activity.class);
                intent.putExtra("finalRequestParams", finalRequestParams);
                intent.putExtra("ispro", ispro);
                intent.putExtra("driver_image", selectedImagePathDriver);
                intent.putExtra("user_image", selectedImagePathUser);
                startActivity(intent);
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
        Intent camraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camraIntent, CAMERA_REQUEST_DRIVER);
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
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgProfilePic.setImageBitmap(photo);
                Uri uri = data.getData();
                selectedImagePathUser = ImageHelper2.getRealPathFromURI(uri.toString(), _context);
            } else if (requestCode == CAMERA_REQUEST_DRIVER && resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgAddDriverLiecense.setImageBitmap(photo);
                Uri uri = data.getData();
                selectedImagePathDriver = ImageHelper2.getRealPathFromURI(uri.toString(), _context);

            } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    selectedImagePathUser = getPathFromURI(selectedImageUri);
//                    Log.i(TAG, "Image Path : " + path);
                    // Set the image in ImageView
                    imgProfilePic.setImageURI(selectedImageUri);
                } else if (requestCode == GALLERY_REQUEST_DRIVER && resultCode == RESULT_OK) {
                    // Get the url from data
                    Uri selectedImageUriDriver = data.getData();
                    if (null != selectedImageUriDriver) {
                        // Get the path from the Uri
                        selectedImagePathDriver = getPathFromURI(selectedImageUriDriver);
//                        Log.i(TAG, "Image Path : " + path);
                        // Set the image in ImageView
                        imgAddDriverLiecense.setImageURI(selectedImageUriDriver);
                    }
                }
            }
        }
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
        int hasWriteCameraPermission = checkSelfPermission(android.Manifest.permission.CAMERA);
        int hasWriteReadExtewrnalPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasWriteExternalExtewrnalPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (hasWriteCameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }if (hasWriteReadExtewrnalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }if (hasWriteExternalExtewrnalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (listPermissionsNeeded.size() > 0){
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_CODE_ASK_PERMISSIONS);
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
}
