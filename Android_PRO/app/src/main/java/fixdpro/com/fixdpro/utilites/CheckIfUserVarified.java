package fixdpro.com.fixdpro.utilites;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by sahil on 9/27/2016.
 */


public class CheckIfUserVarified {
    Context context = null ;
    SharedPreferences _prefs = null ;
    public CheckIfUserVarified(Context context){
        this.context = context;
        _prefs = Utilities.getSharedPreferences(context);
        getVarifiedStatus();
    }
    private HashMap<String, String> getreadUserFromServer() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "users");
        hashMap.put("select", "^*,technicians.^*");
        hashMap.put("where[id]", _prefs.getString(Preferences.ID,""));
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));

        return hashMap;
    }
    private void getVarifiedStatus(){
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(Constants.BASE_URL, "POST", getreadUserFromServer());
                if (jsonObject != null) {
//                    responseListenerCompleted.handleResponse(jsonObject);

                    try {
                        if (jsonObject.getString("STATUS").equals("SUCCESS")){
                            JSONArray RESPONSE = jsonObject.getJSONArray("RESPONSE");
                            if (RESPONSE.getJSONObject(0).getJSONObject("technicians").getString("verified").equals("1")){
                                _prefs.edit().putString(Preferences.IS_VARIFIED,"1").commit();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();
    }
}
