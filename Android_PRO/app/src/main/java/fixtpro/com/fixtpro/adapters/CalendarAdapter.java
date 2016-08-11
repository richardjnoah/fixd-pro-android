package fixtpro.com.fixtpro.adapters;

/**
 * Created by sony on 06-02-2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import fixtpro.com.fixtpro.R;
import fixtpro.com.fixtpro.beans.AvailableJobModal;
import fixtpro.com.fixtpro.beans.CalendarCollection;
import fixtpro.com.fixtpro.beans.CalenderScheduledJobList;

public class CalendarAdapter extends BaseAdapter {
    private Context context;

    private java.util.Calendar month;
    public GregorianCalendar pmonth;
    /**
     * calendar instance for previous month for getting complete view
     */
    public GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDay;
    int maxWeeknumber;
    int maxP;
    int calMaxP;
    int lastWeekDay;
    int leftDays;
    int mnthlength;
    String itemvalue, curentDateString;
    DateFormat df;

    private ArrayList<String> items;
    public static List<String> day_string;
    private View previousView;
    public ArrayList<CalendarCollection>  date_collection_arr;

    public CalendarAdapter(Context context, GregorianCalendar monthCalendar, ArrayList<CalendarCollection> date_collection_arr) {
        this.date_collection_arr=date_collection_arr;
        CalendarAdapter.day_string = new ArrayList<String>();
        Locale.setDefault(Locale.US);
        month = monthCalendar;
        selectedDate = (GregorianCalendar) monthCalendar.clone();
        this.context = context;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);

        this.items = new ArrayList<String>();
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();

    }

    public void setItems(ArrayList<String> items) {
        for (int i = 0; i != items.size(); i++) {
            if (items.get(i).length() == 1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }

    public int getCount() {
        return day_string.size();
    }

    public Object getItem(int position) {
        return day_string.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
//        View v = convertView;
        ViewHolder holder = null;
//        View convertView = view;
        TextView dayView;
        TextView event_indicator;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes

            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.cal_item, null);
            holder = new ViewHolder();
          
//            convertView.setTag(holder);

        } else {
//            holder = (ViewHolder) convertView.getTag();
        }

        dayView = (TextView) convertView.findViewById(R.id.date);
        event_indicator = (TextView) convertView.findViewById(R.id.event_indicator);

        String[] separatedTime = day_string.get(position).split("-");


        String gridvalue = separatedTime[2].replaceFirst("^0*", "");
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            dayView.setTextColor(Color.GRAY);
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            dayView.setTextColor(Color.GRAY);
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            // setting curent month's days in blue color.
            dayView.setTextColor(Color.WHITE);
        }




        if (day_string.get(position).equals(curentDateString)) {
            dayView.setTextColor(Color.parseColor("#fc7506"));
//            dayView.setBackgroundResource(R.drawable.rounded_calender_item);
        } else {
            convertView.setBackgroundColor(Color.parseColor("#00000000"));
        }
        dayView.setText(gridvalue);

        // create date string for comparison
        String date = day_string.get(position);

        if (date.length() == 1) {
            date = "0" + date;
        }
        String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        // show icon if date is not empty and it exists in the items array
        /*ImageView iw = (ImageView) v.findViewById(R.id.date_icon);
        if (date.length() > 0 && items != null && items.contains(date)) {
            iw.setVisibility(View.VISIBLE);
        } else {
            iw.setVisibility(View.GONE);
        }
        */

//        if (setEventView(convertView, position,event_indicator)){
////            event_indicator.setVisibility(View.VISIBLE);
//        }else {
////            event_indicator.setVisibility(View.GONE);
//        }
        setEventView(convertView, position,event_indicator);

        return convertView;
    }
    private class ViewHolder {
      
    }
    public View setSelected(View view,int pos,boolean hasEvent) {
        if (previousView != null) {
            previousView.setBackgroundColor(Color.parseColor("#00000000"));
            if(previousView.getTag().equals("1")){
                TextView date = (TextView)previousView.findViewById(R.id.date);
                date.setTextColor(Color.parseColor("#fc7506"));
                TextView indicator = (TextView)previousView.findViewById(R.id.event_indicator);
                indicator.setTextColor(Color.parseColor("#fc7506"));
            }else if(previousView.getTag().equals("2")){
                TextView date = (TextView)previousView.findViewById(R.id.date);
                date.setTextColor(Color.parseColor("#fc7506"));
            }else if(previousView.getTag().equals("3")){
                TextView date = (TextView)previousView.findViewById(R.id.date);
                date.setTextColor(Color.parseColor("#ffffff"));
                TextView indicator = (TextView)previousView.findViewById(R.id.event_indicator);
                indicator.setTextColor(Color.parseColor("#fc7506"));
            }
            else if(previousView.getTag().equals("4")){
                TextView date = (TextView)previousView.findViewById(R.id.date);
                date.setTextColor(Color.parseColor("#ffffff"));
            }
// else{
//                TextView date = (TextView)previousView.findViewById(R.id.date);
//                date.setTextColor(Color.parseColor("#ffffff"));
//                TextView indicator = (TextView)previousView.findViewById(R.id.event_indicator);
//                indicator.setTextColor(Color.parseColor("#ffffff"));
//            }
        }
        view.setBackgroundResource(R.drawable.rounded_calender_item);

        int len=day_string.size();
        if (len>pos) {
            previousView = view;
            TextView txtIndicator = (TextView)view.findViewById(R.id.event_indicator);
            TextView txtDate = (TextView)view.findViewById(R.id.date);
            txtIndicator.setTextColor(Color.parseColor("#ffffff"));
            txtDate.setTextColor(Color.parseColor("#ffffff"));
            if (day_string.get(pos).equals(curentDateString) && hasEvent) {
                previousView.setTag("1");

            }else if (day_string.get(pos).equals(curentDateString) && !hasEvent){

                previousView.setTag("2");
            }else if (!day_string.get(pos).equals(curentDateString) && hasEvent){

                previousView.setTag("3");
            }else if (!day_string.get(pos).equals(curentDateString) && !hasEvent){

                previousView.setTag("4");
            }

        }

//
        return view;


    }

    public void refreshDays() {
        // clear items
        items.clear();
        day_string.clear();
        Locale.setDefault(Locale.US);
        pmonth = (GregorianCalendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        /**
         * Calendar instance for getting a complete gridview including the three
         * month's (previous,current,next) dates.
         */
        pmonthmaxset = (GregorianCalendar) pmonth.clone();
        /**
         * setting the start date as previous month's required date.
         */
        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

        /**
         * filling calendar gridview.
         */
        for (int n = 0; n < mnthlength; n++) {

            itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            day_string.add(itemvalue);

        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }

    public boolean hasEvent(String date){

        return false;
    }


    public void setEventView(View v,int pos,TextView txt){

        int len=CalendarCollection.date_collection_arr.size();
        for (int i = 0; i < len; i++) {
            CalendarCollection cal_obj=CalendarCollection.date_collection_arr.get(i);
            String date=cal_obj.date;
            int len1=day_string.size();
            if (len1>pos) {

                if (day_string.get(pos).equals(date)) {
                    txt.setVisibility(View.VISIBLE);
                    break;
//                    return  true;
//                    txt.setBackgroundColor(Color.parseColor("#343434"));
//                    txt.setBackgroundResource(R.drawable.rounded_calender_item);
//
//                    txt.setTextColor(Color.WHITE);
                }else {
//                    return  false;
                    txt.setVisibility(View.GONE);

//                    txt.setTag(pos);
                }
            }
        }


//    return false;
    }


    public ArrayList<AvailableJobModal> getPositionList(String date,final Activity act){
        ArrayList<AvailableJobModal> list = new ArrayList<AvailableJobModal>();
        int len=CalendarCollection.date_collection_arr.size();
        for (int i = 0; i < len; i++) {
            CalendarCollection cal_collection=CalendarCollection.date_collection_arr.get(i);
            String event_date=cal_collection.date;

            String event_message = cal_collection.event_message;

            if (date.equals(event_date)) {
                list = CalendarCollection.date_collection_arr.get(i).list;
                Toast.makeText(context, "You have event on this date: " + event_date, Toast.LENGTH_LONG).show();

                break;
            }else{


            }}

        return  list;

    }

}
