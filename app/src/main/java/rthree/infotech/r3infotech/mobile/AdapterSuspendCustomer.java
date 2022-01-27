package rthree.infotech.r3infotech.mobile;

import android.annotation.SuppressLint;
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
public class AdapterSuspendCustomer extends ArrayAdapter<String> {
    String[] name = {};
    String[] datefrom={};
    String []custid={};
    String[] suspend={};
    String[] color={};

    PartyOutstanding outstanding;

    Context context;
    LayoutInflater layoutInflater;

    public AdapterSuspendCustomer(Context context, String[] name, String[] datefrom, String[] suspend,String[] custid,String [] color) {
        super(context, R.layout.item_ledger, name);

        this.context = context;
        this.name = name;
        this.datefrom=datefrom;
        this.custid=custid;
        this.suspend=suspend;
        this.color=color;



    }

    public class ViewHolder {
        TextView txtparty,txttdatefrom,txtsuspend;


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
        viewHolder.txttdatefrom = (TextView) convertView.findViewById(R.id.txt_totalbills);
        viewHolder.txtsuspend = (TextView) convertView.findViewById(R.id.txt_amount);
        viewHolder.txtparty.setText(name[position]);
        viewHolder.txttdatefrom.setText(datefrom[position]);
        viewHolder.txtsuspend.setText(suspend[position]);

        try
        {
            convertView.setId(Integer.parseInt(""+custid[position].toString().trim()));
            if(color[position].toString().trim().equals("Red"))
            {
                viewHolder.txtsuspend.setTextColor(Color.RED);
            }
            else
            {
                viewHolder.txtsuspend.setTextColor(R.color.Text_Color_Green);
            }
        }
        catch (Exception ex){}
        return convertView;


    }


}
