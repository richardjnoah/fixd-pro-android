package fixtpro.com.fixtpro.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;

import fixtpro.com.fixtpro.R;

import fixtpro.com.fixtpro.utilites.CurrentScheduledJobSingleTon;
import fixtpro.com.fixtpro.utilites.MultipartUtility;

public class TapToSignActivity extends AppCompatActivity {
    ImageView imgClose,imgAccept,imgClear;
    CurrentScheduledJobSingleTon singleTon;
    fixtpro.com.fixtpro.views.InkView inkView ;
    String error_message = "";
    MultipartUtility multipart = null;
    String mPath = "";
    boolean isCancel = false;
    private static final int REQUEST_WRITE_STORAGE = 112;
    boolean ispro = false ;
    HashMap<String,String> finalRequestParams = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_to_sign);
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")){
                finalRequestParams = (HashMap<String,String>)bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")){
                ispro = bundle.getBoolean("ispro");
            }
        }
        setWidgets();
        setListeners();
    }
    private void setWidgets(){
        imgClose = (ImageView)findViewById(R.id.imgClose);
        imgClear = (ImageView)findViewById(R.id.imgClear);
        imgAccept = (ImageView)findViewById(R.id.imgAccept);
        inkView = (fixtpro.com.fixtpro.views.InkView)findViewById(R.id.signatureView);
    }
    private void setListeners(){
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                finish();
            }
        });imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TapToSignActivity.this, TapToSignActivity.class);
                startActivity(intent);
                finish();
            }
        });imgAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    boolean hasPermission = (ContextCompat.checkSelfPermission(TapToSignActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission) {
                        ActivityCompat.requestPermissions(TapToSignActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_STORAGE);
                    } else {
                        takeScreenshot();
                    }
                } else {
                    takeScreenshot();
                }


            }
        });
    }
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            mPath = Environment.getExternalStorageDirectory().toString() + "/" + System.currentTimeMillis() + ".jpg";

            // create bitmap screen capture
//            View v1 = getWindow().getDecorView().getRootView();
            inkView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(inkView.getDrawingCache());
            inkView.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Intent intent = new Intent();
            intent.putExtra("Path",mPath);
            setResult(200, intent);
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            finish();

        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    takeScreenshot();
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    boolean hasPermission = (ContextCompat.checkSelfPermission(TapToSignActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission){
                        ActivityCompat.requestPermissions(TapToSignActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_STORAGE);
                    }
                }
            }
        }

    }
    private void showAlertDialog(String Title,String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

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
