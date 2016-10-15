package fixdpro.com.fixdpro.adapters;

/**
 * Created by sahil on 28-04-2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.utilites.ChatService;
import fixdpro.com.fixdpro.utilites.Preferences;
import fixdpro.com.fixdpro.utilites.Utilities;

public class UserChatAdapters extends BaseAdapter {
    private List<QBDialog> dataSource;
    private LayoutInflater inflater;
    private Context context = null ;
    String QB_LOGIN = null ;
    SharedPreferences _prefs = null ;
    public UserChatAdapters(ArrayList<QBDialog> dataSource, Activity ctx) {
        this.dataSource = dataSource;
        this.inflater = LayoutInflater.from(ctx);
        context = ctx;
        _prefs = Utilities.getSharedPreferences(context);
        QB_LOGIN = _prefs.getString(Preferences.QB_ACCOUNT_ID,"");
    }

    public List<QBDialog> getDataSource() {
        return dataSource;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        QBUser user = null;
        // initIfNeed view
        //
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chat_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.roomName);
            holder.lastMessage = (TextView)convertView.findViewById(R.id.lastMessage);
            holder.groupType = (TextView)convertView.findViewById(R.id.textViewGroupType);
            holder.time = (TextView)convertView.findViewById(R.id.time);
            holder.imgUser = (de.hdodenhof.circleimageview.CircleImageView)convertView.findViewById(R.id.roomImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set data
        //
        QBDialog dialog = dataSource.get(position);
        if(dialog.getType().equals(QBDialogType.GROUP)){
            holder.name.setText(dialog.getName());
        }else{
            // get opponent name for private dialog
            //
            Integer opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(dialog);
             user = ChatService.getInstance().getDialogsUsers().get(opponentID);
            if(user != null){
                holder.name.setText(user.getLogin() == null ? user.getFullName() : user.getLogin());
            }
        }
        if (false) {
//        if (dialog.getLastMessage() != null && StickersManager.isSticker(dialog.getLastMessage())) {
            holder.lastMessage.setText("Sticker");
        } else {
            holder.lastMessage.setText(dialog.getLastMessage());
        }
//        holder.groupType.setText(user.getFullName());
        long date = dialog.getCreatedAt().getTime();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        Log.e("", "dialog" + date);
        try {
            JSONObject jsonObject = new JSONObject(dialog.getPhoto());
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    if (key.equals(QB_LOGIN) || key.equals("job_id")){
                        continue;

                    }
                    else {
                        Object value = jsonObject.get(key);
                        JSONObject object = new JSONObject(value.toString());
                        holder.groupType.setText(object.getString("name"));
                        if (!object.getString("image").equals("null"))
                            Picasso.with(context).load(object.getString("image"))
                                    .into(holder.imgUser);
                    }
                    break;
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long time1 = cal.getTimeInMillis();
        long time2 = 0;
//        try {
            time2 = dialog.getUpdatedAt().getTime();
//            time2 = Utilities.getTimeInMilliseconds(dialog.getCreatedAt().toString()) ;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        long difference = time1 - time2 ;
        holder.time.setText(Utilities.timeMedthod(difference / 1000l));
        return convertView;
    }

    private static class ViewHolder{
        TextView name;
        TextView lastMessage;
        TextView time;
        TextView groupType;
        de.hdodenhof.circleimageview.CircleImageView imgUser;
    }
}
