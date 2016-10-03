package fixtpro.com.fixtpro.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import fixtpro.com.fixtpro.R;

public class AddBankAccountNew extends AppCompatActivity {
    ImageView imgNext, imgClose;
    HashMap<String,String> finalRequestParams = null ;
    boolean ispro = false ;

    EditText txtBankName,txtRoutingNumber,txtAccountNumber;
    String bank_name,routing_number,account_type = "",account_number;
    String selectedImagePathUser = null ;
    String  selectedImagePathDriver = null;
    boolean iscompleting = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account_new);
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
        txtBankName = (EditText)findViewById(R.id.txtBankName);
        txtRoutingNumber = (EditText)findViewById(R.id.txtRoutingNumber);
        txtAccountNumber = (EditText)findViewById(R.id.txtAccountNumber);

    }
    private void showAlertDialog(String Title,String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AddBankAccountNew.this);
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
    }

    private void setCLickListner() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bank_name = txtBankName.getText().toString().trim();
                routing_number = txtRoutingNumber.getText().toString().trim();
                account_number = txtAccountNumber.getText().toString().trim();
                if (bank_name.length() == 0){
                    showAlertDialog("Fixd-Pro","Please enter the bank name.");
                    return;
                }else if (routing_number.length() == 0){
                    showAlertDialog("Fixd-Pro","Please enter the routing number.");
                    return;
                }else if (account_number.length() == 0){
                    showAlertDialog("Fixd-Pro","Please enter the account number.");
                    return;
                }
                finalRequestParams.put("data[pros][bank_name]",bank_name);
                finalRequestParams.put("data[pros][bank_routing_number]","011103093");
//                        finalRequestParams.put("data[pros][bank_routing_number]",routing_number);
                finalRequestParams.put("data[pros][bank_account_number]",account_number);
                Intent i = new Intent(AddBankAccountNew.this, BackgroundCheck_Activity.class);
                i.putExtra("ispro", ispro);
                i.putExtra("iscompleting", iscompleting);
                i.putExtra("finalRequestParams", finalRequestParams);
                i.putExtra("driver_image", selectedImagePathDriver);
                i.putExtra("user_image", selectedImagePathUser);
                startActivity(i);
                overridePendingTransition(R.anim.enter,R.anim.exit);
//                        finalRequestParams.put("data[pros][bank_account_type]",account_type);
//                intent.putExtra("finalRequestParams", finalRequestParams);
//                intent.putExtra("image_driver",image_driver);
//                intent.putExtra("image_profile",image_profile);
//                intent.putExtra("ispro",true);
            }
        });
    }
}
