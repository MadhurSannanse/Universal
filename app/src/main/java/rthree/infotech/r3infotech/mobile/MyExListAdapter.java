package rthree.infotech.r3infotech.mobile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by USER on 05/04/2017.
 */

public class MyExListAdapter extends BaseExpandableListAdapter {
    Context context;
    List<String> company;
    Map<String,Vector> leddet;
    String[] name = {};
    String[] total={};
    String[] totalbills={};
    String []oid={};
    LayoutInflater layoutInflater;


    public MyExListAdapter(Context context, List<String> company, Map<String, Vector> leddet, String[] name, String[] totalbills, String[] total, String[] oid) {
        this.context = context;
        this.company = company;
        this.leddet = leddet;
        this.name = name;
        this.total=total;
        this.totalbills=totalbills;
        this.oid=oid;

    }

    @Override
    public int getGroupCount() {
        return company.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return leddet.get(company.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return company.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return leddet.get(company.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_outstanding, null);

        }
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.txtparty = convertView.findViewById(R.id.txt_partyname);
        viewHolder.txttotalbills = convertView.findViewById(R.id.txt_totalbills);
        viewHolder.txttotal = convertView.findViewById(R.id.txt_amount);


        viewHolder.txtparty.setText(name[groupPosition]);
        viewHolder.txttotalbills.setText(totalbills[groupPosition]);
        viewHolder.txttotal.setText(total[groupPosition]);

        try
        {
            convertView.setId(Integer.parseInt(""+ oid[groupPosition].trim()));
        }
        catch (Exception ex){}
        return convertView;

    }
    public class ViewHolder {
        TextView txtparty,txttotalbills,txttotal;


    }
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // String prod=(String)getChild(groupPosition,childPosition);
        Vector data=(Vector)getChild(groupPosition,childPosition);
        Log.i("Cur Vector ",""+data.toString());
        if (convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_rebate, null);

        }
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.txtparty = convertView.findViewById(R.id.txt_partyname);
        viewHolder.txttotalbills = convertView.findViewById(R.id.txt_totalbills);
        viewHolder.txttotal = convertView.findViewById(R.id.txt_amount);


        viewHolder.txtparty.setText("   Bill Date : "+data.elementAt(3));
        viewHolder.txttotalbills.setText("   Total Cs : " + data.elementAt(4)+"   Total Btl : " + data.elementAt(5));
        viewHolder.txttotal.setText("Rebate Amt\n"+data.elementAt(6));

        try
        {
            convertView.setId(Integer.parseInt(""+ oid[groupPosition].trim()));
        }
        catch (Exception ex){}
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
