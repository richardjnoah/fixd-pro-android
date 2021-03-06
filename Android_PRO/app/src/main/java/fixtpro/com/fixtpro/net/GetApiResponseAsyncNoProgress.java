package fixtpro.com.fixtpro.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.HashMap;

import fixtpro.com.fixtpro.utilites.JSONParser;

/**
 * Created by sahil on 09-05-2016.
 */
public class GetApiResponseAsyncNoProgress extends AsyncTask<HashMap<String, String>, Void, JSONObject> {

    ProgressDialog progressDialog;
    IHttpResponseListener listener;
    String Method;
    String Text;
    String Url;
    IHttpExceptionListener exceptionListener;
    public GetApiResponseAsyncNoProgress(String Url,String Method, IHttpResponseListener listener,IHttpExceptionListener exceptionListener,
                               Activity activity,String Text) {
        this.Method = Method;
        this.listener = listener;
        this.Text = Text;
        this.Url = Url ;
        this.exceptionListener = exceptionListener;
//        progressDialog = new ProgressDialog(activity);
//        progressDialog.setMessage(Text);
//        progressDialog.setCancelable(false);
//        progressDialog.show();
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
            result = jsonParser.makeHttpRequest(Url,Method,arg0[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
//        progressDialog.dismiss();
        if (listener != null && result != null) {
            listener.handleResponse(result);
        }
    }

}



