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

import java.util.ArrayList;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.utilites.CurrentScheduledJobSingleTon;
import fixdpro.com.fixdpro.utilites.Utilities;


/**
 * Created by  on 18-04-2016.
 *
 */
public class CalanderEventsAdapter extends BaseAdapter {

    /*********** Declare Used Variables *********/
    private Activity activity ;
    private ArrayList data ;
    private static LayoutInflater inflater = null ;
    public Resources res ;
    AvailableJobModal tempValues = null ;
    Typeface fontfamily ;

    /*************  CustomAdapter Constructor *****************/
    public CalanderEventsAdapter(Activity a, ArrayList d,Resources resLocal) {

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
     }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return data.size();
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView timeFrom;
        public TextView timeTo;
        public TextView ServiceName;
        public TextView AddressName;
        public ImageView img_Reschedule_icon;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.calender_event_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.timeFrom = (TextView) vi.findViewById(R.id.timeFrom);
            holder.timeTo = (TextView) vi.findViewById(R.id.timeTo);
            holder.ServiceName = (TextView) vi.findViewById(R.id.ServiceName);
            holder.AddressName = (TextView) vi.findViewById(R.id.AddressName);
            holder.img_Reschedule_icon = (ImageView) vi.findViewById(R.id.img_Reschedule_icon);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
//            holder.BName.setText("No Data");
        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = (AvailableJobModal) data.get( position );
            holder.timeFrom.setText(Utilities.Am_PMFormat(tempValues.getTimeslot_start())) ;
            holder.timeTo.setText(Utilities.Am_PMFormat(tempValues.getTimeslot_end())) ;
            String appliance = "" ;
            for (int i=0 ; i < tempValues.getJob_appliances_arrlist().size(); i++){
                appliance = appliance + "" + tempValues.getJob_appliances_arrlist().get(i).getAppliance_type_name() + ",";
            }
            holder.ServiceName.setText(appliance);
            holder.AddressName.setText(tempValues.getContact_name() +" " +tempValues.getJob_customer_addresses_address());
            if (CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal() != null){
                if (tempValues.getId().equals(CurrentScheduledJobSingleTon.getInstance().getCurrentJonModal().getId())){
                    holder.img_Reschedule_icon.setVisibility(View.VISIBLE);
                }
            }

            /************  Set Model values in Holder elements ***********/
        }
        return vi;
    }

}




