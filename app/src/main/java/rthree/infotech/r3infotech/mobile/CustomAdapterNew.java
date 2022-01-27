package rthree.infotech.r3infotech.mobile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by user on 9/3/2016.
 */
public class CustomAdapterNew extends ArrayAdapter<String> {
    String[] name = {};
    String[] qty = {};
    String[] rate={};
    String []total={};
    String []pid={};
    String []disc={};
    String []product={};
    ConfirmOrder confirm;

    Context context;
    LayoutInflater layoutInflater;

    public CustomAdapterNew(Context context, String[] name, String[] rate, String[] qty, String[] total, String pid[],String disc[],String product []) {
        super(context, R.layout.productlistviewitems, name);

        this.context = context;
        this.name = name;
        this.rate = rate;
        this.qty=qty;
        this.total=total;
        this.pid=pid;
        this.disc=disc;
        this.product=product;
        confirm=new ConfirmOrder();


    }

    public class ViewHolder {
        TextView txtname, txtrate,txtqty,txttotal,txtdisc,txtproduct;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.productlistviewitems, null);


        }
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.txtname = (TextView) convertView.findViewById(R.id.txt_proname);
        viewHolder.txtrate = (TextView) convertView.findViewById(R.id.txt_prorate);
        viewHolder.txtqty = (TextView) convertView.findViewById(R.id.txt_proqty);
        viewHolder.txttotal = (TextView) convertView.findViewById(R.id.txt_proamount);
        viewHolder.txtdisc=(TextView)convertView.findViewById(R.id.txt_prodiscount);
        viewHolder.txtproduct=(TextView)convertView.findViewById(R.id.txt_productname);


        viewHolder.txtname.setText(name[position]);
        viewHolder.txtrate.setText(rate[position]);
        viewHolder.txtqty.setText(qty[position]);
        viewHolder.txttotal.setText(total[position]);
        viewHolder.txtdisc.setText(disc[position]);
        viewHolder.txtproduct.setText(product[position]);
        try
        {
            convertView.setId(Integer.parseInt(""+pid[position].toString().trim()));
            if(Double.parseDouble(""+disc[position])<0)
            {
                if(position!=0) {
                    viewHolder.txtdisc.setTextColor(Color.RED);
                }
            }
        }
        catch (Exception ex){}
       /* if(position==0) {
            viewHolder.txtname.setTextColor(Color.BLUE);
            viewHolder.txtrate.setTextColor(Color.BLUE);
            viewHolder.txtqty.setTextColor(Color.BLUE);
            viewHolder.txttotal.setTextColor(Color.BLUE);
            viewHolder.txtdisc.setTextColor(Color.BLUE);
            viewHolder.txtproduct.setTextColor(Color.BLUE);
        }*/


        return convertView;


    }


}
