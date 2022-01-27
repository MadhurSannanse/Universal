package rthree.infotech.r3infotech.mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by user on 9/3/2016.
 */
public class AdapterLedger extends ArrayAdapter<String> {
    String[] name = {};
    String[] total={};
    String []oid={};

    PartyOutstanding outstanding;

    Context context;
    LayoutInflater layoutInflater;

    public AdapterLedger(Context context, String[] name, String[] total, String[] oid) {
        super(context, R.layout.orderlistviewitems, name);

        this.context = context;
        this.name = name;
        this.total=total;
        this.oid=oid;
        outstanding=new PartyOutstanding();


    }

    public class ViewHolder {
        TextView txtparty,txttotal;


    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_ledger, null);

        }
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.txtparty = (TextView) convertView.findViewById(R.id.txt_partyname);
        viewHolder.txttotal = (TextView) convertView.findViewById(R.id.txt_amount);


        viewHolder.txtparty.setText(name[position]);
        viewHolder.txttotal.setText(total[position]);

        try
        {
            convertView.setId(Integer.parseInt(""+oid[position].toString().trim())); }
        catch (Exception ex){}
        return convertView;


    }


}
