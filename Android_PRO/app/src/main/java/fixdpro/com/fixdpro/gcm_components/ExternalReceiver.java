package fixdpro.com.fixdpro.gcm_components;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Date;

import fixdpro.com.fixdpro.ChatActivityNew;
import fixdpro.com.fixdpro.HomeScreenNew;
import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.beans.NotificationModal;
import fixdpro.com.fixdpro.utilites.ChatSingleton;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;
import me.leolin.shortcutbadger.ShortcutBadger;

public class ExternalReceiver extends BroadcastReceiver {
    SharedPreferences _prefs = null ;
    public void onReceive(Context context, Intent intent) {
        _prefs = Utilities.getSharedPreferences(context);
        if(intent!=null){
            if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION"))
                return;


            Bundle extras = intent.getExtras();
            NotificationModal modal = new NotificationModal();
            if (extras.containsKey("dialogId")){
                String dialogId = extras.getString("dialogId", "");
                modal.setDialogId(dialogId);
                modal.setType("cn");// Chat Notification
                Log.e("",""+extras.getString("dialogId", ""));
            }
            if (extras.containsKey("message"))
                modal.setMessage(extras.getString("message", ""));
            if (extras.containsKey("t"))
                modal.setType(extras.getString("t", ""));
            // Case for Job Assinged
            if (modal.getType().equals("pa")){

                if (extras.containsKey("tID"))
                    modal.setTechId(extras.getString("tID", ""));
                if (extras.containsKey("j"))
                    modal.setJobId(extras.getString("j", ""));
            }
            if (modal.getType().equals("swo")){
                if (extras.containsKey("ja"))
                    modal.setJobAppliance(extras.getString("ja", ""));
            }
            if (modal.getType().equals("woa")){
                if (extras.containsKey("ja"))
                    modal.setJobAppliance(extras.getString("ja", ""));
                if (extras.containsKey("j"))
                    modal.setJobId(extras.getString("j", ""));
            }
            if (modal.getType().equals("wod")){
                if (extras.containsKey("ja"))
                    modal.setJobAppliance(extras.getString("ja", ""));
                if (extras.containsKey("j"))
                    modal.setJobId(extras.getString("j", ""));
            }
            if (modal.getType().equals("ja")){
                if (extras.containsKey("j"))
                    modal.setJobId(extras.getString("j", ""));
            }

            String message = extras.getString("message","New Notification");
            if (extras != null){
                for (String key : extras.keySet()) {
                    Object value = extras.get(key);
                    Log.d("", String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                }
            }
            if (modal.getDialogId().length() > 0 && !ChatActivityNew.inBackground && ChatSingleton.getInstance().getCurrentQbDialog() != null && ChatSingleton.getInstance().getCurrentQbDialog().getDialogId().equals(modal.getDialogId())){
                return;
            }
            if(HomeScreenNew.inBackground>0){
                Intent intent1 = new Intent("gcm_push_notification_receiver");
                // You can also include some extra data.
                intent1.putExtra("data", modal);
//                MessageReceivingService.sendToApp(extras, context);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
            }
            else{
//                MessageReceivingService.saveToLog(extras, context);
//                final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//                final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, HomeScreenNew.class), Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL);
//                final Notification notification = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("Fixd")
//                        .setContentText(message)
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true)
//                        .getNotification();
//
//                mNotificationManager.notify(R.string.notification_number, notification);

                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                Date now = new Date();
                long uniqueId = now.getTime();//use date to generate an unique id to differentiate the notifications.
                Intent notificationIntent = new Intent(context, HomeScreenNew.class);
                notificationIntent.putExtra("data", modal);
                notificationIntent.putExtra("isnoty","yes");
                notificationIntent.setAction("com.fixtconsumer" + uniqueId);

                PendingIntent contentIntent = PendingIntent.getActivity(context,
                        0, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                Notification.Builder builder = new Notification.Builder(context);

                builder.setContentIntent(contentIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentTitle("Fixd")
                        .setContentText(modal.getMessage());
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
                ShortcutBadger.applyCount(context, notiCountNormal);
            }
            }
        }


    }


