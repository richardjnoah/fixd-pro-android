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
import fixtpro.com.fixtpro.utilites.chat_utils.ImagePickHelper;

public class BackgroundCheck_Next_Activity extends AppCompatActivity {
    Context context = BackgroundCheck_Next_Activity.this;
    public static final String TAG = "BackgroundCheck_Next_Activity";
    ImageView imgClose, imgNext,imgMessage;
    CheckBox checkIacKnowlege;
    HashMap<String,String> finalRequestParams = null ;
    boolean ispro = false ;
    WebView webview = null ;
    String selectedImagePathUser = null ;
    String  selectedImagePathDriver = null;
    boolean iscompleting = false ;
    int READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,CAMERA;
    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_ATTACHMENT = 721;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_check__next_);

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
        webview.loadUrl("file:///android_asset/FCRA.html");
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
                // share the pdf
                onMessageClickSharePDF();
            }
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIacKnowlege.isChecked()) {
                    Intent i = new Intent(context, BackgroundCheckDisclosure.class);
                    i.putExtra("ispro", ispro);
                    i.putExtra("finalRequestParams", finalRequestParams);
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
                + "/Rights_Under_FCRA";
        File file = new File(FilePath +"/FCRAAgreement.pdf");
        File file1 = new File(FilePath, "FCRAAgreement.pdf");
        if (file.exists()){
            // get file and attach to email
            sharePDFFileWithGmail(file1);

        }else {
            File Folder = new File(FilePath);
            Folder.mkdir();
            Utilities.copySqltiteFromAssets(BackgroundCheck_Next_Activity.this, "FCRAAgreement.pdf", FilePath);
            // share pdf
            sharePDFFileWithGmail(file1);
        }
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


    public void sharePDFFileWithGmail(File shareFile){
        Intent intent = new Intent(Intent.ACTION_SEND , Uri.parse("mailto:")); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Fixd - Terms & Conditions");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(shareFile));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);
    }

    /* String  FilePath = Environment.getExternalStorageDirectory()
                + "/FIXD";
        File file = new File(FilePath +"/FCRAAgreement.pdf");
        if (file.exists()){
            // get file and attach to email

            File file1 = new File(FilePath, "FCRAAgreement.pdf");
            Intent intent = new Intent(Intent.ACTION_SEND ,Uri.parse("mailto:")); // it's not ACTION_SEND
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Card Set ");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file1));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            startActivity(intent);
        }else {
            File Folder = new File(FilePath);
            Folder.mkdir();
            Utilities.copySqltiteFromAssets(HomeScreenNew.this, "FCRAAgreement.pdf", FilePath);
            File file1 = new File(FilePath, "FCRAAgreement.pdf");
            Intent intent = new Intent(Intent.ACTION_SEND ,Uri.parse("mailto:")); // it's not ACTION_SEND
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Card Set ");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(file1));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
            startActivity(intent);
        }*/
    private void showAlertDialog(String Title, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                BackgroundCheck_Next_Activity.this);

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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }
}
