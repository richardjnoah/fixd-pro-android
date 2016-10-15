package fixdpro.com.fixdpro.utilites;

import com.quickblox.chat.model.QBDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by sahil on 02-05-2016.
 */
public class ChatSingleton {
    public static ChatSingleton singleton = new ChatSingleton( );

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    public ArrayList<QBDialog> dataSourceUsers = new ArrayList<QBDialog>();
    private ChatSingleton(){ }

    /* Static 'instance' method */
    public static ChatSingleton getInstance( ) {
        return singleton;
    }
    QBDialog qbDialog = null ;

    public QBDialog getCurrentQbDialog() {
        return qbDialog;
    }

    public void setCurrentQbDialog(QBDialog qbDialog) {
        this.qbDialog = qbDialog;
    }

    public String getINameFromDialogId(String dialogId,String senderId){
        String name = "";
        for (int i = 0 ; i < dataSourceUsers.size(); i++){

            if (dataSourceUsers.get(i).getDialogId().equals(dialogId))
            {
                try {
                    JSONObject jsonObject = new JSONObject(qbDialog.getPhoto());
                    Iterator<String> iter = jsonObject.keys();
                    String QB_LOGIN = senderId;
                    while (iter.hasNext()) {
                        String key = iter.next();

                        if (key.equals(QB_LOGIN)){
                            try {
                                Object value = jsonObject.get(key);
                                JSONObject object = new JSONObject(value.toString());
                                name = object.getString("name");

//                        JSONArray jsonArray = object.getJSONArray("endpoint_arn");
//                        for (int i = 0 ; i < jsonArray.length(); i++){
//                            endpoint_arn.add(jsonArray.getString(i));
//                        }
                                break;
                            } catch (JSONException e) {
                                // Something went wrong!
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return name;
    }
}

