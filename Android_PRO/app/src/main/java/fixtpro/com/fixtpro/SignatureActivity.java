package fixtpro.com.fixtpro;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import fixtpro.com.fixtpro.beans.install_repair_beans.Signature;
import fixtpro.com.fixtpro.utilites.Constants;
import fixtpro.com.fixtpro.utilites.CurrentScheduledJobSingleTon;
import fixtpro.com.fixtpro.utilites.ExceptionListener;
import fixtpro.com.fixtpro.utilites.GetApiResponseAsyncMutipart;
import fixtpro.com.fixtpro.utilites.MultipartUtility;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Singleton;
import fixtpro.com.fixtpro.utilites.Utilities;

public class SignatureActivity extends AppCompatActivity {
    ImageView imgClose,imgAccept,imgClear;
    CurrentScheduledJobSingleTon singleTon;
    fixtpro.com.fixtpro.views.InkView inkView ;
    String error_message = "";
    MultipartUtility multipart = null;
    String mPath = "";
    boolean isCancel = false;
    private static final int REQUEST_WRITE_STORAGE = 112;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_signature);
        singleTon = CurrentScheduledJobSingleTon.getInstance();
        setWidgets();
        setListeners();
        if (getIntent().getExtras() != null){
            isCancel = true ;
        }


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
                finish();
            }
        });imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignatureActivity.this, SignatureActivity.class);
                startActivity(intent);
                finish();
            }
        });imgAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1) {
                    boolean hasPermission = (ContextCompat.checkSelfPermission(SignatureActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission){
                        ActivityCompat.requestPermissions(SignatureActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_STORAGE);
                    }else {
                        takeScreenshot();
                    }
                }else {
                    takeScreenshot();
                }


            }
        });
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
    public void executeRepairInfoSaveingRequest(){
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    multipart = new MultipartUtility(Constants.BASE_URL, Constants.CHARSET);
                    multipart.addFormField("api", "update_customer_signature");
//                    if (CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_service_type().equals("Install")){

                        multipart.addFormField("object", "job_appliances");

//                    }else{
//                        multipart.addFormField("object", "repair_flow");
//
//                    }

                    multipart.addFilePart("data[customer_signature]", new File(mPath));
                    multipart.addFormField("token", Utilities.getSharedPreferences(SignatureActivity.this).getString(Preferences.AUTH_TOKEN, ""));
                    multipart.addFormField("data[job_appliance_id]", CurrentScheduledJobSingleTon.getInstance().getJobApplianceModal().getJob_appliances_id());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                GetApiResponseAsyncMutipart getApiResponseAsync = new GetApiResponseAsyncMutipart(multipart, repairTypeResponseListener,repairTypeexceptionListener, SignatureActivity.this, "Saving");
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
                    singleTon.getCurrentReapirInstallProcessModal().setIsCompleted(true);
                    Intent intent = new Intent();
                    setResult(200, intent);
                    finish();
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
            if (!isCancel){
                singleTon.getInstallOrRepairModal().getSignature().setSignature_path(mPath);
                executeRepairInfoSaveingRequest();
            }else {
                Intent intent = new Intent();
                intent.putExtra("Path",mPath);
                setResult(200,intent);
                finish();
            }
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
                    boolean hasPermission = (ContextCompat.checkSelfPermission(SignatureActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission){
                        ActivityCompat.requestPermissions(SignatureActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_STORAGE);
                    }
                }
            }
        }

    }

}
