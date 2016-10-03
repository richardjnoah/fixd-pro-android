package fixtpro.com.fixtpro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import fixtpro.com.fixtpro.net.GetApiResponseAsync;
import fixtpro.com.fixtpro.net.IHttpExceptionListener;
import fixtpro.com.fixtpro.net.IHttpResponseListener;
import fixtpro.com.fixtpro.utilites.Constants;

import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.Utilities;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class AddCardScreen extends AppCompatActivity {
    EditText editCardNo, editYear, editMonth, editCw, editZipCode, editFirstName, editLastName;
    TextView txtAlmostDone, txtScanCard, txtDone, txtBack;
    LinearLayout btnScanCard;
    ImageView img_camra, img_keypad;
    private Typeface fontfamily;
    String card_number = "", month = "", year = "", cvv = "", zip_code = "", first_name = "", last_name = "";
    Context _context = null;
    String error_message = "";
    SharedPreferences _prefs = null;
    private static final int MY_SCAN_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_screen);
        _context = this;
        _prefs = Utilities.getSharedPreferences(_context);
        setWidgets();
        setyTypeFace();
        setListeners();
    }

    private void setListeners() {
        txtScanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanPress(v);
            }
        });
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        finish();
    }

    private void setyTypeFace() {
        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
//        txtAlmostDone.setTypeface(fontfamily);
        editCardNo.setTypeface(fontfamily);
        editYear.setTypeface(fontfamily);
        editMonth.setTypeface(fontfamily);
        editCw.setTypeface(fontfamily);
        editFirstName.setTypeface(fontfamily);
        editLastName.setTypeface(fontfamily);
        editZipCode.setTypeface(fontfamily);
        txtScanCard.setTypeface(fontfamily);
    }

    private void setWidgets() {
        txtDone = (TextView) findViewById(R.id.txtDone);
        txtBack = (TextView) findViewById(R.id.txtBack);
        editCardNo = (EditText) findViewById(R.id.editCardNo);
        img_keypad = (ImageView) findViewById(R.id.img_keypad);
        editYear = (EditText) findViewById(R.id.editYear);
        editMonth = (EditText) findViewById(R.id.editMonth);
        editCw = (EditText) findViewById(R.id.editCw);
        editZipCode = (EditText) findViewById(R.id.editZipCode);
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        img_camra = (ImageView) findViewById(R.id.img_camra);
        txtScanCard = (TextView) findViewById(R.id.txtScanCard);
        btnScanCard = (LinearLayout) findViewById(R.id.layout3);
    }
    public void submit(){
        card_number = editCardNo.getText().toString().trim();
        month = editMonth.getText().toString().trim();
        year = editYear.getText().toString().trim();
        cvv = editCw.getText().toString().trim();
        zip_code = editZipCode.getText().toString().trim();
        first_name = editFirstName.getText().toString().trim();
        last_name = editLastName.getText().toString().trim();
        if (card_number.length() == 0) {
            showAlertDialog("Fixd!", "please enter card number");
        } else if (card_number.length() != 16) {
            showAlertDialog("Fixd!", "please enter a valid card number");
        } else if (month.length() == 0) {
            showAlertDialog("Fixd!", "please enter month");
        } else if (year.length() == 16) {
            showAlertDialog("Fixd!", "please enter a year");
        } else if (year.length() < 4) {
            showAlertDialog("Fixd!", "please enter a valid a valid year");
        } else if (cvv.length() == 0) {
            showAlertDialog("Fixd!", "please enter CVV");
        } else if (cvv.length() < 3) {
            showAlertDialog("Fixd!", "please enter a valid a CVV number");
        } else if (first_name.length() == 0) {
            showAlertDialog("Fixd!", "please enter first name");
        } else if (last_name.length() == 0) {
            showAlertDialog("Fixd!", "please enter last name");
        } else {
                GetApiResponseAsync apiResponseAsync = new GetApiResponseAsync(Constants.BASE_URL, "POST", addCardResponseListener, addCardExceptionListener, this, "Saving");
                apiResponseAsync.execute(getAddCardPareams());

        }
    }
    private void showAlertDialog(String Title,String Message){
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
    private HashMap<String, String> getAddCardPareams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("api", "create");
        hashMap.put("data[primary]", "1");
        hashMap.put("object", "cards");
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("data[card_number]", card_number);
        hashMap.put("data[month]", month);
        hashMap.put("data[firstname]", first_name);
        hashMap.put("data[lastname]", last_name);
        hashMap.put("data[year]", year);
        hashMap.put("data[cvv]", cvv);

        hashMap.put("id", _prefs.getString(Preferences.ID, ""));

        return hashMap;

    }

    IHttpResponseListener addCardResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {
            Log.e("", "response" + response);
            try {
                if (response.getString("STATUS").equals("SUCCESS")) {
                    _prefs.edit().putString(Preferences.HAS_CARD, "1").commit();
                    JSONObject RESPONSE = response.getJSONObject("RESPONSE");
                    String id = RESPONSE.getString("id");
                    String card_number = RESPONSE.getString("card_number");
                    String firstname = RESPONSE.getString("firstname");
                    String cvv = RESPONSE.getString("cvv");
                    String lastname = RESPONSE.getString("lastname");
                    String month = RESPONSE.getString("month");
                    String year = RESPONSE.getString("year");
                    SharedPreferences.Editor editor = _prefs.edit() ;
                    editor.putString(Preferences.CREDIT_CARD_ID,id);
                    editor.putString(Preferences.CREDIT_CARD_NUMBER,card_number);
                    editor.putString(Preferences.CREDIT_CARD_FIRST_NAME,firstname);
                    editor.putString(Preferences.CREDIT_CARD_LAST_NAME,lastname);
                    editor.putString(Preferences.CREDIT_CARD_CVV,cvv);
                    editor.putString(Preferences.CREDIT_CARD_MONTH,month);
                    editor.putString(Preferences.CREDIT_CARD_YEAR,year);
                    editor.commit();

                    handler.sendEmptyMessage(0);

                } else {
                    JSONObject errors = response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    IHttpExceptionListener addCardExceptionListener = new IHttpExceptionListener() {
        @Override
        public void handleException(String exception) {
            error_message = exception;
            handler.sendEmptyMessage(1);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
//                    Now Card is Added its the call for Schedule
                    overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                    finish();
                    break;
                }
                case 1: {
                    showAlertDialog("Fixd", error_message);
                    break;
                }
            }
        }
    };

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);
        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, true); // default: false
        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                ;

                editCardNo.setText(scanResult.getRedactedCardNumber().replace(" ", ""));
                card_number = scanResult.cardNumber;

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                    editYear.setText(scanResult.expiryYear + "");
                    editMonth.setText(scanResult.expiryMonth + "");
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                    editCw.setText(scanResult.cvv);
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                    editZipCode.setText(scanResult.postalCode);
                }
            } else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }
}