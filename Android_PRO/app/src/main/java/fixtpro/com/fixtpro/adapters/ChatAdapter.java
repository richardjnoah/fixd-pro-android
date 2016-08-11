package fixtpro.com.fixtpro.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.QBContent;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smackx.carbons.packet.CarbonExtension;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.utilites.ChatService;
import fixtpro.com.fixtpro.utilites.Preferences;
import fixtpro.com.fixtpro.utilites.TimeUtils;
import fixtpro.com.fixtpro.utilites.Utilities;


public class ChatAdapter extends BaseAdapter {

    private final List<QBChatMessage> chatMessages;
    private Activity context;
    ImageView imageView;
    SharedPreferences _prefs = null ;
    private enum ChatItemType {
        Message,
        Sticker,
        SinceDate
    }

    public ChatAdapter(Activity context, List<QBChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        _prefs = Utilities.getSharedPreferences(context);
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public QBChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getViewTypeCount() {
        return ChatItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
//        return StickersManager.isSticker(getItem(position).getAttachments().size() > 0)
//                ? ChatItemType.Sticker.ordinal()
//                : ChatItemType.Message.ordinal();
        if (getItem(position).getAttachments().size() > 0){
            Log.e("","getItem(position)"+getItem(position).getAttachments());
//            List<QBAttachment> list = getItem(position).getAttachments();
            return  ChatItemType.Sticker.ordinal();
        }else if (getItem(position).getProperties().containsKey("time")){
            return  ChatItemType.SinceDate.ordinal();
        }else {
            return  ChatItemType.Message.ordinal();
        }
//        return ChatItemType.Message.ordinal();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        QBChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            if (getItemViewType(position) == ChatItemType.Sticker.ordinal()) {
                convertView = vi.inflate(R.layout.list_item_sticker, parent, false);
            } else if (getItemViewType(position) == ChatItemType.Message.ordinal()){
                convertView = vi.inflate(R.layout.list_item_message, parent, false);
            }else {
                convertView = vi.inflate(R.layout.chat_date_item, parent, false);
            }
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QBUser currentUser = ChatService.getInstance().getCurrentUser();
        boolean isOutgoing = chatMessage.getSenderId() == null || chatMessage.getSenderId().equals(currentUser.getId());
        setAlignment(holder, isOutgoing);

        if (chatMessage.getAttachments().size() > 0) {
            String id = "";
//            StickersManager.with(convertView.getContext())
//                    .loadSticker(chatMessage.getBody())
//                    .into(holder.stickerView);
            for(QBAttachment attachment :chatMessage.getAttachments()){
                attachment.getUrl();
                if (attachment.getUrl() != null){
                    _prefs.getString(Preferences.QB_TOKEN, "");
                    String ImageSplit[] = attachment.getUrl().split("=");
                    ImageSplit[1] = _prefs.getString(Preferences.QB_TOKEN, "");
                    String newUrl = ImageSplit[0] + "=" +ImageSplit[1] ;
                    Picasso.with(context).load(newUrl)
                    .into(holder.stickerView);
                }

//                Log.e("","attachment.getUrl()"+attachment.getUrl());
//                id = attachment.getId() ;

            }
//            imageView = holder.stickerView;
//            QBContent.downloadFile(id, new QBEntityCallback<InputStream>() {
//                @Override
//                public void onSuccess(InputStream inputStream, Bundle bundle) {
//                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
//                    imageView.setImageBitmap(bmp);
//                }
//
//                @Override
//                public void onError(QBResponseException e) {
//                    Log.e("","gg");
//                }
//            }, new QBProgressCallback() {
//                @Override
//                public void onProgressUpdate(int i) {
//
//                }
//            });
        } else if (holder.txtMessage != null) {
            holder.txtMessage.setText(chatMessage.getBody());
        }
        if (getItem(position).getProperties().containsKey("time")){
            holder.timeSpan.setText(getDate(getItem(position).getDateSent() * 1000));
        }
        if (holder.txtInfo != null){
            holder.txtInfo.setText("15:12");
        }

        if (holder.contentWithBG != null){
            holder.contentWithBG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.txtInfo.getVisibility() == View.VISIBLE){
                        holder.txtInfo.setVisibility(View.GONE);
                    }else {
                        holder.txtInfo.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
//        if (chatMessage.getSenderId() != null) {
//            holder.txtInfo.setText(chatMessage.getSenderId() + ": " + getTimeText(chatMessage));
//        } else {
//            holder.txtInfo.setText(getTimeText(chatMessage));
//        }
        return convertView;
    }

//    public void add(QBChatMessage message) {
//        chatMessages.add(message);
//    }
//    public void add(ArrayList<QBChatMessage> message) {
//        chatMessages.addAll(0,message);
//    }
//    public void add(List<QBChatMessage> messages) {
//        chatMessages.addAll(messages);
//    }

    private void setAlignment(ViewHolder holder, boolean isOutgoing) {
        if (holder.contentWithBG == null){
            return;
        }
        if (isOutgoing) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
            if (holder.txtMessage != null) {
                holder.contentWithBG.setBackgroundResource(R.drawable.outgoing_message_bg);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtMessage.setLayoutParams(layoutParams);
            } else {
                holder.contentWithBG.setBackgroundResource(android.R.color.transparent);
            }
        } else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);

            if (holder.txtMessage != null) {
                holder.contentWithBG.setBackgroundResource(R.drawable.incoming_message_bg);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtMessage.setLayoutParams(layoutParams);
            } else {
                holder.contentWithBG.setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    private ViewHolder createViewHolder(View v) {
        final ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        holder.timeSpan = (TextView) v.findViewById(R.id.timeSpan);
        holder.stickerView = (ImageView) v.findViewById(R.id.sticker_image);

        return holder;
    }

    private String getTimeText(QBChatMessage message) {
        return TimeUtils.millisToLongDHMS(message.getDateSent() * 1000);
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView timeSpan;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public ImageView stickerView;
    }
    private String getDate(long values){
        DateFormat formatter = new SimpleDateFormat("MMM dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(values);

        return calendar.getTime().toString();
    }
}
