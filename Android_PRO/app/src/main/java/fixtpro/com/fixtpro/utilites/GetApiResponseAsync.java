package fixtpro.com.fixtpro.utilites;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Window;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.ResponseListener;

/**
 * Created by sony on 08-02-2016.
 */
public class GetApiResponseAsync extends AsyncTask<HashMap<String, String>, Void, JSONObject> {

    Dialog progressDialog;
    ResponseListener listener;
    String Method;
    String Text;
    public GetApiResponseAsync(String Method, ResponseListener listener,
                              Activity activity,String Text) {
        this.Method = Method;
        this.listener = listener;
        this.Text = Text;
        progressDialog = new Dialog(activity);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.dialog_progress_simple);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(HashMap<String, String>... arg0) {
        // TODO Auto-generated method stub
        JSONObject result = null;

        try {
            JSONParser jsonParser = new JSONParser();
            result = jsonParser.makeHttpRequest(Constants.BASE_URL,Method,arg0[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        progressDialog.dismiss();
        if (listener != null && result != null) {
            listener.handleResponse(result);
        }
    }

}

