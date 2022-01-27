package rthree.infotech.r3infotech.mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by user on 9/3/2016.
 */
public class AdapterOutstanding extends ArrayAdapter<String> {
    String[] name = {};
    String[] total={};
    String[] totalbills={};
    String []oid={};

    PartyOutstanding outstanding;

    Context context;
    LayoutInflater layoutInflater;

    public AdapterOutstanding(Context context, String[] name,  String[] totalbills,String[] total,String[] oid) {
        super(context, R.layout.orderlistviewitems, name);

        this.context = context;
        this.name = name;
        this.total=total;
        this.totalbills=totalbills;
        this.oid=oid;
        outstanding=new PartyOutstanding();


    }
    public AdapterOutstanding(Context context)
    {
        super(context,R.layout.orderlistviewitems);
    }

    public class ViewHolder {
        TextView txtparty,txttotalbills,txttotal;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_outstanding, null);

        }
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.txtparty = (TextView) convertView.findViewById(R.id.txt_partyname);
        viewHolder.txttotalbills = (TextView) convertView.findViewById(R.id.txt_totalbills);
        viewHolder.txttotal = (TextView) convertView.findViewById(R.id.txt_amount);


        viewHolder.txtparty.setText(name[position]);
        viewHolder.txttotalbills.setText(totalbills[position]);
        viewHolder.txttotal.setText(total[position]);

        try
        {
            convertView.setId(Integer.parseInt(""+oid[position].toString().trim()));
        }
        catch (Exception ex){}
        return convertView;


    }


}
