package fixtpro.com.fixtpro;

import android.*;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import fixtpro.com.fixtpro.utilites.CheckIfUserVarified;
import fixtpro.com.fixtpro.utilites.Preferences;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView cancel, email, call, feedback;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    List<String> listPermissionsNeeded = new ArrayList<>();
    SharedPreferences _prefs = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        _prefs = getSharedPreferences(Preferences.FIXIT_PRO_PREFERNCES,MODE_PRIVATE);
//        getSupportActionBar().hide();
        setWidgets();
        setListeners();
    }

    public void setWidgets(){
        cancel = (ImageView)findViewById(R.id.cancel);
        email = (ImageView)findViewById(R.id.email);
        call = (ImageView)findViewById(R.id.call);
        feedback = (ImageView)findViewById(R.id.feedback);
    }

    public void setListeners(){
        cancel.setOnClickListener(this);
        email.setOnClickListener(this);
        call.setOnClickListener(this);
        feedback.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_us, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                finish();
                break;
            case R.id.email:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "support@fixdrepair.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fixd-Pro App Support(Ver: 1.0)");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
            case R.id.call:
                if (Build.VERSION.SDK_INT >= 23) {
                    insertDummyContactWrapper();
                    if (listPermissionsNeeded.size() > 0){
                        requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_CODE_ASK_PERMISSIONS);
                    }else {
                        String number = "tel:" + "80-01111111";
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                        startActivity(callIntent);
                    }
                } else {
                    String number = "tel:" + "80-01111111";
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    startActivity(callIntent);
                }

                break;
            case R.id.feedback:
                Intent feedbackemailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "feedback@fixdrepair.com", null));
                feedbackemailIntent.putExtra(Intent.EXTRA_SUBJECT, "Fixd-Pro App Feedback(Ver: 1.0)");
                startActivity(Intent.createChooser(feedbackemailIntent, "Send email..."));
                break;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {

        int hasWriteReadExtewrnalPermission = checkSelfPermission(android.Manifest.permission.CALL_PHONE);
        listPermissionsNeeded.clear();

        if (hasWriteReadExtewrnalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CALL_PHONE);
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    String number = "tel:" + "80-01111111";
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                    startActivity(callIntent);
                } else {
                    // Permission Denied
                    insertDummyContactWrapper();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!_prefs.getString(Preferences.ACCOUNT_STATUS, "").equals("DEMO_PRO") && _prefs.getString(Preferences.IS_VARIFIED, "").equals("0")){
            new CheckIfUserVarified(this);
        }
    }
}
