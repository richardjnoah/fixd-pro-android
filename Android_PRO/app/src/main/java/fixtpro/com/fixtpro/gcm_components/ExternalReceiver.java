package fixtpro.com.fixtpro.gcm_components;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

import fixtpro.com.fixtpro.HomeScreenNew;
import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.beans.NotificationModal;

public class ExternalReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION"))
                return;


            Bundle extras = intent.getExtras();
            NotificationModal modal = new NotificationModal();
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
            String message = extras.getString("message","message");
            if (extras != null){
                for (String key : extras.keySet()) {
                    Object value = extras.get(key);
                    Log.d("", String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                }
            }
            if(!HomeScreenNew.inBackground){
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
            }
        }
    }
}

