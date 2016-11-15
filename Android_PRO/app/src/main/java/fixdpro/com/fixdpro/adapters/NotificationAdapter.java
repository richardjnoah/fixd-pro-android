package fixdpro.com.fixdpro.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.beans.NotificationListModal;
import fixdpro.com.fixdpro.utilites.HandlePagingResponse;
import fixdpro.com.fixdpro.utilites.Utilities;

/**
 * Created by Harwinder Paras on 11/14/2016.
 */
public class NotificationAdapter extends BaseAdapter {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<NotificationListModal> data;
    private static LayoutInflater inflater=null;
    public Resources res;
    NotificationListModal tempValues=null;
    Typeface fontfamily;
    HandlePagingResponse handlePagingResponse ;

    /*************  CustomAdapter Constructor *****************/
    public NotificationAdapter(Activity a, ArrayList d, Resources resLocal,HandlePagingResponse handlePagingResponse) {


        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;
        fontfamily = Typeface.createFromAsset(activity.getAssets(), "HelveticaNeue-Thin.otf");
//
//            Log.i("", "lstheight"+lstheight);
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        activity = a;
        this.handlePagingResponse = handlePagingResponse ;
    }
    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return data.size();
        return data.size();
    }

    public NotificationListModal getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
        public TextView txtTitle;
        public TextView txtDateTime;
        public ImageView imgPicture;
        ViewGroup container;
    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = (View)inflater.inflate(R.layout.item_notificationlist, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtTitle = (TextView) vi.findViewById(R.id.txtTitle);
            holder.txtDateTime = (TextView) vi.findViewById(R.id.txtDateTime);
            holder.imgPicture = (ImageView) vi.findViewById(R.id.imgPicture);
            holder.container = (ViewGroup) vi.findViewById(R.id.container);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
        }
        else
        {
//            MMMM d, yyyy
            /***** Get each Model object from Arraylist ********/
            try {
                tempValues = null;
                tempValues = (NotificationListModal) data.get(position);
                if (Utilities.getApplianceImageByName(tempValues.getServiceName()) != -1){
                    holder.imgPicture.setImageResource(Utilities.getApplianceImageByName(tempValues.getServiceName()));
                }else{
                    if (tempValues.getIconImage().length() > 0){
                        Picasso.with(activity).load(tempValues.getIconImage()).into(holder.imgPicture);
                    }

                }

                holder.txtTitle.setText(tempValues.getText());
                /*****Change the DateTime Format*****/
                String datetimeArr[] = tempValues.getCreateAt().split(" ");
                String date = datetimeArr[0];
                String time = datetimeArr[1];

                String dateTime[] = Utilities.getDate(tempValues.getCreateAt()).split(" ");
                String actualDateTime = dateTime[0] +" "+ dateTime[1] + " at " + dateTime[2] +"" +dateTime[3];
                holder.txtDateTime.setText(actualDateTime);
                if (((NotificationListModal)data.get(position)).getIs_active().equals("0")){
//                    holder.container.setBackgroundResource(R.color.color_grey);
                    holder.txtTitle.setTextColor(activity.getResources().getColor(R.color.calendar_day_color));
                }else {
//                    holder.container.setBackgroundResource(android.R.color.transparent);
                    holder.txtTitle.setTextColor(activity.getResources().getColor(R.color.white));
                }
                if (((NotificationListModal)data.get(position)).getIs_active().equals("1") && ((NotificationListModal)data.get(position)).getIsRead().equals("0")){
                    holder.container.setBackgroundResource(R.color.calendar_day_color);
//                    holder.txtTitle.setTextColor(activity.getResources().getColor(R.color.calendar_day_color));
                }else {
                    holder.container.setBackgroundResource(android.R.color.transparent);
//                    holder.txtTitle.setTextColor(activity.getResources().getColor(R.color.white));
                }
                if (position == data.size() - 1){
                    handlePagingResponse.handleChangePage();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return vi;
    }

    public void insert(NotificationListModal  modal ,int pos){
        data.add(pos,modal);
        notifyDataSetChanged();
    } public void remove(int pos){
        data.remove(pos);
        notifyDataSetChanged();
    }
}







