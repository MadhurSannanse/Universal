package rthree.infotech.r3infotech.mobile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by user on 9/3/2016.
 */
public class AdapterOrderView extends ArrayAdapter<String> {
    String[] name = {};
    String[] total={};
    String[] odate={};
    String[] oid={};
    ViewAllOrders vieworder;

    Context context;
    LayoutInflater layoutInflater;

    public AdapterOrderView(Context context, String[] name, String[] odate, String[] total,String oid[]) {
        super(context, R.layout.orderlistviewitems, name);

        this.context = context;
        this.name = name;
        this.total=total;
        this.oid=oid;
        this.odate=odate;
        vieworder=new ViewAllOrders();


    }

    public class ViewHolder {
        TextView txtparty,txtdate,txttotal;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.orderlistviewitems, null);

        }
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.txtparty = (TextView) convertView.findViewById(R.id.txt_party);
        viewHolder.txtdate = (TextView) convertView.findViewById(R.id.txt_partydate);
        viewHolder.txttotal = (TextView) convertView.findViewById(R.id.txt_partytotal);


        viewHolder.txtparty.setText(name[position]);
        viewHolder.txtdate.setText(odate[position]);
        viewHolder.txttotal.setText(total[position]);

        try
        {
            convertView.setId(Integer.parseInt(""+oid[position].toString().trim()));
        }
        catch (Exception ex){}
        if(position==0) {
            viewHolder.txtparty.setTextColor(Color.BLUE);
            viewHolder.txtdate.setTextColor(Color.BLUE);
            viewHolder.txttotal.setTextColor(Color.BLUE);
            viewHolder.txtparty.setTextSize(17);
            viewHolder.txtdate.setTextSize(17);
            viewHolder.txttotal.setTextSize(17);

        }

        return convertView;


    }


}
