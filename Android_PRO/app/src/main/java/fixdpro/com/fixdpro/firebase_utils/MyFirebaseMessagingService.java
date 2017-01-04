package fixdpro.com.fixdpro.firebase_utils;

/**
 * Created by Harwinder Paras on 10/3/2016.
 */


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import fixdpro.com.fixdpro.ChatActivityNew;
import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.beans.NotificationModal;
import fixdpro.com.fixdpro.utilites.ChatSingleton;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import me.leolin.shortcutbadger.ShortcutBadger;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    SharedPreferences _prefs = null ;
    NotificationModal modal = new NotificationModal();
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        _prefs = Utilities.getSharedPreferences(this);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

                if (remoteMessage.getData().containsKey("message")){
                    String message = remoteMessage.getData().get("message");
                    modal.setMessage(message);
                }

                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                if (remoteMessage.getData().containsKey("dialogId")){
                    String dialogId = remoteMessage.getData().get("dialogId");
                    modal.setDialogId(dialogId);
                    modal.setType("cn");
                }else {
                    try {
                        JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("data"));
                        if (jsonObject.has("dialogId")) {
                            String dialogId = jsonObject.getString("dialogId");
                            modal.setDialogId(dialogId);
                            modal.setType("cn");// Chat Notification
                            Log.e("", "" + jsonObject.getString("dialogId"));
                        }
                        if (jsonObject.has("message"))
                            modal.setMessage(jsonObject.getString("message"));
                        if (jsonObject.has("payload")){

                            JSONObject jsonPayload = jsonObject.getJSONObject("payload");
                            if (jsonPayload.has("t"))
                                modal.setType(jsonPayload.getString("t"));

                            if (modal.getType().equals("pa")){

                                if (jsonPayload.has("tID"))
                                    modal.setTechId(jsonPayload.getString("tID"));
                                if (jsonPayload.has("j"))
                                    modal.setJobId(jsonPayload.getString("j"));
                            }
                            if (modal.getType().equals("swo")){
                                if (jsonPayload.has("ja"))
                                    modal.setJobAppliance(jsonPayload.getString("ja"));
                            }
                            if (modal.getType().equals("woa")){
                                if (jsonPayload.has("ja"))
                                    modal.setJobAppliance(jsonPayload.getString("ja"));
                                if (jsonPayload.has("j"))
                                    modal.setJobId(jsonPayload.getString("j"));
                            }
                            if (modal.getType().equals("wod")){
                                if (jsonPayload.has("ja"))
                                    modal.setJobAppliance(jsonPayload.getString("ja"));
                                if (jsonPayload.has("j"))
                                    modal.setJobId(jsonPayload.getString("j"));
                            }
                            if (modal.getType().equals("ja")){
                                if (jsonPayload.has("j"))
                                    modal.setJobId(jsonPayload.getString("j"));
                            }
                                if (modal.getType().equals("cja")){
                                    if (jsonPayload.has("j"))
                                        modal.setJobId(jsonPayload.getString("j"));
                                }

                            if (modal.getType().equals("pjp") ||modal.getType().equals("pjt") ||modal.getType().equals("cja")) {
                                if (jsonPayload.has("j"))
                                    modal.setJobId(jsonPayload.getString("j"));
                            }

                        }


                        Log.e("","jsonObject"+jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            if (modal.getDialogId().length() > 0 && !ChatActivityNew.inBackground && ChatSingleton.getInstance().getCurrentQbDialog() != null && ChatSingleton.getInstance().getCurrentQbDialog().getDialogId().equals(modal.getDialogId())){
                return;
            }

            if (modal.getDialogId().length() > 0){
                for (int i = 0 ; i < ChatSingleton.getInstance().dataSourceUsers.size() ; i++){
                    if (ChatSingleton.getInstance().dataSourceUsers.get(i).getDialogId().equals(modal.getDialogId())){
                        int unreadMessageCount = ChatSingleton.getInstance().dataSourceUsers.get(i).getUnreadMessageCount();
                        ChatSingleton.getInstance().dataSourceUsers.get(i).setUnreadMessageCount(unreadMessageCount+1);
                        break;
                    }
                }
            }


            if(!HomeScreenNew.inBackground){
                Intent intent1 = new Intent("gcm_push_notification_receiver");
                // You can also include some extra data.
                intent1.putExtra("data", modal);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
            }
            else{
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                NotificationManager notificationManager = (NotificationManager)getSystemService(this.NOTIFICATION_SERVICE);
                Date now = new Date();
                long uniqueId = now.getTime();//use date to generate an unique id to differentiate the notifications.
                Intent notificationIntent = new Intent(this, HomeScreenNew.class);
                notificationIntent.putExtra("data", modal);
                notificationIntent.putExtra("isnoty","yes");
                notificationIntent.setAction("fixdpro.com.fixdpro" + uniqueId);

                PendingIntent contentIntent = PendingIntent.getActivity(this,
                        0, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                Notification.Builder builder = new Notification.Builder(this);
                Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setContentIntent(contentIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(largeIcon)
                        .setAutoCancel(true)
                        .setSound(uri)
                        .setContentTitle("Fixd Pro")
                        .setContentText(modal.getMessage());


                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.setSmallIcon(R.drawable.pro_noti_icon);
                } else {
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                }

                Notification n = builder.build();
                notificationManager.notify((111111 + (int)(Math.random() * 999999)), n);
                int notiCountTotal = 0 ;
                int notiCountChat = 0 ;
                int notiCountNormal = 0 ;
                SharedPreferences.Editor editor = _prefs.edit() ;
                notiCountChat = _prefs.getInt(Preferences.CHAT_NOTI_COUNT,0);
                notiCountNormal = _prefs.getInt(Preferences.CHAT_NOTI_COUNT,0);
                if (modal.getDialogId().length() > 0){
                    // chat_noti
//            notiCountChat = _prefs.getInt(Preferences.CHAT_NOTI_COUNT,0);
//            ++notiCountChat;
//            editor.putInt(Preferences.CHAT_NOTI_COUNT,notiCountChat);
                }else {
                    // normal_noti
                    notiCountNormal = _prefs.getInt(Preferences.NORMAL_NOTI_COUNT,0);
                    ++notiCountNormal;
                    editor.putInt(Preferences.NORMAL_NOTI_COUNT,notiCountNormal);
                }
                editor.commit();
//        notiCountTotal = notiCountNormal + notiCountChat;
                ShortcutBadger.applyCount(this, notiCountNormal);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String body = remoteMessage.getNotification().getBody() ;
        }

    }

}
