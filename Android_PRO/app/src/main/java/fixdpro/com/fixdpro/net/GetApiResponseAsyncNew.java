package fixdpro.com.fixdpro.net;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.view.Window;

import org.json.JSONObject;

import java.util.HashMap;

import fixdpro.com.fixdpro.R;

/**
 * Created by sahil on 05-05-2016.
 */
public class GetApiResponseAsyncNew extends AsyncTask<HashMap<String, String>, Void, JSONObject> {

    Dialog progressDialog;
    IHttpResponseListener listener;
    String Method;
    String Text;
    String Url;
    IHttpExceptionListener exceptionListener;
    public GetApiResponseAsyncNew(String Url,String Method, IHttpResponseListener listener,IHttpExceptionListener exceptionListener,
                               Activity activity,String Text) {
        this.Method = Method;
        this.listener = listener;
        this.Text = Text;
        this.Url = Url ;
        this.exceptionListener = exceptionListener;
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
            JSONParserNew jsonParser = new JSONParserNew();
            arg0[0].put("_app_id", "FIXD_ANDROID_PRO");
            arg0[0].put("_company_id", "FIXD");
            result = jsonParser.makeHttpRequest(Url,Method,arg0[0],exceptionListener);
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


