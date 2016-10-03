package fixtpro.com.fixtpro.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.utilites.Utilities;

public class BackgroundCheckDisclosure extends AppCompatActivity {
    Context context = BackgroundCheckDisclosure.this;
    public static final String TAG = "BackgroundCheckDisclosure";
    ImageView imgClose, imgNext,imgMessage;
    CheckBox checkIacKnowlege;
    HashMap<String,String> finalRequestParams = null ;
    boolean ispro = false ;
    WebView webview ;
    String selectedImagePathUser = null ;
    String  selectedImagePathDriver = null;
    boolean iscompleting = false ;
    int READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,CAMERA;
    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_ATTACHMENT = 721;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_check_disclosure);
        setWidgets();

        setClickListner();
        if (getIntent().getExtras() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("finalRequestParams")){
                finalRequestParams = (HashMap<String,String>)bundle.getSerializable("finalRequestParams");
            }
            if (bundle.containsKey("ispro")){
                ispro = bundle.getBoolean("ispro");
            }
            if (bundle.containsKey("iscompleting")){
                iscompleting = bundle.getBoolean("iscompleting");
            }
            if (bundle.containsKey("driver_image")){
                selectedImagePathDriver = bundle.getString("driver_image");
            }
            if (bundle.containsKey("user_image")){
                selectedImagePathUser = bundle.getString("user_image");
            }
        }
    }

    private void setWidgets() {
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgMessage = (ImageView) findViewById(R.id.imgMessage);
        webview = (WebView)findViewById(R.id.webview);
        checkIacKnowlege = (CheckBox)findViewById(R.id.checkIacKnowlege);
        webview.loadUrl("file:///android_asset/Disclosure.html");

    }
    private void showAlertDialog(String Title, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                BackgroundCheckDisclosure.this);

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
    private void setClickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (Build.VERSION.SDK_INT >= 23){
                   onAttachmentsClickThenShare(imgMessage);
               }else{
                   onMessageClickSharePDF();
               }
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIacKnowlege.isChecked()) {
                    Intent i = new Intent(context, BackgrounTap_ToSign_Activity.class);
                    i.putExtra("ispro", ispro);
                    i.putExtra("iscompleting", iscompleting);
                    i.putExtra("finalRequestParams", finalRequestParams);
                    i.putExtra("driver_image", selectedImagePathDriver);
                    i.putExtra("user_image", selectedImagePathUser);
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    showAlertDialog("Fixd-Pro", "Please accept the Terms to continue");
                }

            }
        });
    }
    public void onMessageClickSharePDF(){
        String  FilePath = Environment.getExternalStorageDirectory()
                + "/Disclosure";
        File file = new File(FilePath +"/Disclosure.pdf");
        File file1 = new File(FilePath, "Disclosure.pdf");
        if (file.exists()){
            // get file and attach to email
            sharePDFFileWithGmail(file1);

        }else {
            File Folder = new File(FilePath);
            Folder.mkdir();
            Utilities.copySqltiteFromAssets(BackgroundCheckDisclosure.this, "Disclosure.pdf", FilePath);
            // share pdf
            sharePDFFileWithGmail(file1);
        }
    }
    public void sharePDFFileWithGmail(File shareFile){
        Intent intent = new Intent(Intent.ACTION_SEND , Uri.parse("mailto:")); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Fixd - Terms & Conditions");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);
    }
    public void onAttachmentsClickThenShare(View view) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> permissionsNeeded = new ArrayList<String>();
            if (READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsNeeded.size() > 0) {
                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            } else {
                // share the pdf
               onMessageClickSharePDF();
            }
        }else{
            // share the pdf
            onMessageClickSharePDF();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }
}
