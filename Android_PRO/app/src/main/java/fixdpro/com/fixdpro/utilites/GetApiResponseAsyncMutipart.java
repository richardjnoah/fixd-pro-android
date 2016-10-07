package fixdpro.com.fixdpro.utilites;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.ResponseListener;

/**
 * Created by sahil on 08-03-2016.
 */
public class GetApiResponseAsyncMutipart extends AsyncTask<HashMap<String, String>, Void, JSONObject> {

    Dialog progressDialog;
    ResponseListener listener;

    String Text;
    MultipartUtility multipartUtility = null ;
    ExceptionListener exceptionListener;
    public GetApiResponseAsyncMutipart(MultipartUtility multipartUtility, ResponseListener listener,ExceptionListener exceptionListener,
                               Activity activity,String Text) {
        this.multipartUtility = multipartUtility;
        this.listener = listener;
        this.Text = Text;
        this.exceptionListener = exceptionListener;
        progressDialog = new Dialog(activity);
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

            result = new JSONObject(multipartUtility.finish(this.exceptionListener));
            Log.e("","result "+result.toString());
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


