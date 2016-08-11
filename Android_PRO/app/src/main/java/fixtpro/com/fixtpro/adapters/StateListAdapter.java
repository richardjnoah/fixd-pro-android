package fixtpro.com.fixtpro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fixtpro.com.fixtpro.R;


/**
 * Created by sahil on 23-06-2016.
 */
public class StateListAdapter extends BaseAdapter {
    ArrayList<String> arrayList;
    Context context;
    
    public StateListAdapter(ArrayList<String> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        TextView lstTxt;
        ImageView lstCheck;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_state_list,null);
            holder.lstTxt = (TextView)convertView.findViewById(R.id.lstTxt);
//            holder.lstCheck = (ImageView)convertView.findViewById(R.id.lstCheck);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        String state = (String)getItem(position);
        holder.lstTxt.setText(state);
//        holder.lstCheck.setImageResource();
        return convertView;
    }
}

