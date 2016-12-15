package fixdpro.com.fixdpro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.beans.Brands;

/**
 * Created by sahil on 21-06-2016.
 */
public class BrandDialogAdapter extends BaseAdapter {
    ArrayList<Brands> arrayList;
    Context context;

    public BrandDialogAdapter(ArrayList<Brands> arrayList, Context context) {
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
            convertView = inflater.inflate(R.layout.item_tellusmore,null);
            holder.lstTxt = (TextView)convertView.findViewById(R.id.lstTxt);
            holder.lstCheck = (ImageView)convertView.findViewById(R.id.lstCheck);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        Brands brands = (Brands)getItem(position);
        holder.lstTxt.setText(brands.getBrand_name());
//        holder.lstCheck.setImageResource();
        return convertView;
    }
}
