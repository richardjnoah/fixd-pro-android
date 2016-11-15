//package fixdpro.com.fixdpro.adapters;
//
//import android.app.Activity;
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
//import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;
//import com.nhaarman.listviewanimations.util.Swappable;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.Collections;
//
//import fixdpro.com.fixdpro.R;
//import fixdpro.com.fixdpro.beans.NotificationListModal;
//import fixdpro.com.fixdpro.utilites.HandlePagingResponse;
//import fixdpro.com.fixdpro.utilites.Utilities;
//
///**
// * Created by Harwinder Paras on 11/9/2016.
// */
//public class NotificationSwipeToDismissAdapter extends BaseAdapter implements Swappable, UndoAdapter, OnDismissCallback {
//
//    private Context mContext;
//    private LayoutInflater mInflater;
//    private ArrayList<NotificationListModal> mNotificationListModalList;
//    private boolean mShouldShowDragAndDropIcon;
//    Activity activity ;
//    NotificationListModal tempValues=null;
//    HandlePagingResponse handlePagingResponse ;
//    private static LayoutInflater inflater=null;
//
//    public NotificationSwipeToDismissAdapter(Activity a,Context context, ArrayList<NotificationListModal> NotificationListModalList, boolean shouldShowDragAndDropIcon,HandlePagingResponse handlePagingResponse) {
//        mContext = context;
//        activity = a;
//        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mNotificationListModalList = NotificationListModalList;
//        mShouldShowDragAndDropIcon = shouldShowDragAndDropIcon;
//        /***********  Layout inflator to call external xml layout () ***********/
//        inflater = ( LayoutInflater )activity.
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//
//    @Override
//    public int getCount() {
//        return mNotificationListModalList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mNotificationListModalList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return mNotificationListModalList.get(position).getId();
//    }
//
//    @Override
//    public void swapItems(int positionOne, int positionTwo) {
//        Collections.swap(mNotificationListModalList, positionOne, positionTwo);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View vi = convertView;
//        final ViewHolder holder;
//
//        if(convertView==null){
//
//            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
//            vi = inflater.inflate(R.layout.item_notificationlist, null);
//
//            /****** View Holder Object to contain tabitem.xml file elements ******/
//
//            holder = new ViewHolder();
//            holder.txtTitle = (TextView) vi.findViewById(R.id.txtTitle);
//            holder.txtDateTime = (TextView) vi.findViewById(R.id.txtDateTime);
//            holder.imgPicture = (ImageView) vi.findViewById(R.id.imgPicture);
//            holder.container = (LinearLayout) vi.findViewById(R.id.container);
//
//            /************  Set holder with LayoutInflater ************/
//            vi.setTag( holder );
//        }
//        else
//            holder=(ViewHolder)vi.getTag();
//
//        if(mNotificationListModalList.size()<=0)
//        {
//        }
//        else
//        {
////            MMMM d, yyyy
//            /***** Get each Model object from Arraylist ********/
//            try {
//                tempValues = null;
//                tempValues = (NotificationListModal) mNotificationListModalList.get(position);
//                if (Utilities.getApplianceImageByName(tempValues.getServiceName()) != -1){
//                    holder.imgPicture.setImageResource(Utilities.getApplianceImageByName(tempValues.getServiceName()));
//                }else{
//                    if (tempValues.getIconImage().length() > 0){
//                        Picasso.with(activity).load(tempValues.getIconImage()).into(holder.imgPicture);
//                    }
//
//                }
//
//                holder.txtTitle.setText(tempValues.getText());
//                /*****Change the DateTime Format*****/
//                String datetimeArr[] = tempValues.getCreateAt().split(" ");
//                String date = datetimeArr[0];
//                String time = datetimeArr[1];
//
//                String dateTime[] = Utilities.getDate(tempValues.getCreateAt()).split(" ");
//                String actualDateTime = dateTime[0] +" "+ dateTime[1] + " at " + dateTime[2] +"" +dateTime[3];
//                holder.txtDateTime.setText(actualDateTime);
//                if (((NotificationListModal)mNotificationListModalList.get(position)).getIs_active().equals("0")){
////                    holder.container.setBackgroundResource(R.color.color_grey);
//                    holder.txtTitle.setTextColor(activity.getResources().getColor(R.color.calendar_day_color));
//                }else {
////                    holder.container.setBackgroundResource(android.R.color.transparent);
//                    holder.txtTitle.setTextColor(activity.getResources().getColor(R.color.white));
//                }
//                if (((NotificationListModal)mNotificationListModalList.get(position)).getIs_active().equals("1") && ((NotificationListModal)mNotificationListModalList.get(position)).getIsRead().equals("0")){
//                    holder.container.setBackgroundResource(R.color.calendar_day_color);
////                    holder.txtTitle.setTextColor(activity.getResources().getColor(R.color.calendar_day_color));
//                }else {
//                    holder.container.setBackgroundResource(android.R.color.transparent);
////                    holder.txtTitle.setTextColor(activity.getResources().getColor(R.color.white));
//                }
//                if (position == mNotificationListModalList.size() - 1 && handlePagingResponse != null){
//                    handlePagingResponse.handleChangePage();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        return vi;
//    }
//
//
//    private static class ViewHolder {
//        public TextView txtTitle;
//        public TextView txtDateTime;
//        public ImageView imgPicture;
//        LinearLayout container;
//    }
//
//    @Override
//    @NonNull
//    public View getUndoClickView(@NonNull View view) {
//        return view.findViewById(R.id.undo_button);
//    }
//
//    @Override
//    @NonNull
//    public View getUndoView(final int position, final View convertView,
//                            @NonNull final ViewGroup parent) {
//        View view = convertView;
//        if (view == null) {
//            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_undo_view,
//                    parent, false);
//        }
//        return view;
//    }
//
//    @Override
//    public void onDismiss(@NonNull final ViewGroup listView,
//                          @NonNull final int[] reverseSortedPositions) {
//        for (int position : reverseSortedPositions) {
//            remove(position);
//        }
//    }
//    public void remove(int position) {
//        mNotificationListModalList.remove(position);
//    }
//}
//
