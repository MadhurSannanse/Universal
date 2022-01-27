package rthree.infotech.r3infotech.mobile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
/*/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

public class ViewAllOrders extends AppCompatActivity {
    String [] pname,pdate,ptotal,pdist,oid;
    String usertype,username,billnumber,ordernumber,orderid="";
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    ListView orderlist;

    Vector order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_orders);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        orderlist=(ListView)findViewById(R.id.lst_orderview);
        try
        {
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            // billnumber=b.getString("BillNumber");
            new LoadOrder().execute("Order");
        }
        catch (Exception ex)
        {

        }
        try
        {
            orderlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(isNetworkAvailable())
                    {
                        processRequest(position);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "No Internte Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            orderlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //setOrderNumber(""+view.getId());
                    editOrder(""+view.getId());
                    return false;
                }
            });

        }
        catch (Exception ex){
            Log.e("Error long",""+ex.getLocalizedMessage());}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
       /* if(id == android.R.id.home)
        {
            this.finish();
            return true;
        }*/
        switch (id)
        {
            case R.id.action_date:
                //showDialog(DILOG_ID);
                break;
            case android.R.id.home:
                this.finish();
                return true;
            //break;
            case R.id.action_home:
                Intent intent =new Intent(getApplicationContext(),Home.class);
                intent.putExtra("UserType", "" + usertype);
                intent.putExtra("UserName", "" + username);
                //finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(getApplicationContext(), Home.class);
            intent.putExtra("UserType", "" + usertype);
            intent.putExtra("UserName", "" + username);
            //intent.putExtra("Location",""+locationstr);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            finish();
            startActivity(intent);
        } catch (Exception ex) {
        }
    }
    private void loadOrderDetailListItems()
    {
        try
        {
            pname=new String[order.size()+1];
            pdate=new String[order.size()+1];
            ptotal=new String[order.size()+1];
            oid=new String[order.size()+1];
            pname[0]="Party Name";
            pdate[0]="Date";
            ptotal[0]="Amount";
            oid[0]="";
            for(int i=0;i<order.size();i++)
            {
                Vector data=(Vector)order.elementAt(i);
                pname[i+1]=""+data.elementAt(0);
                pdate[i+1]=""+data.elementAt(2);
                ptotal[i+1]=""+data.elementAt(4);
                oid[i+1]=""+data.elementAt(1).toString();
                // Log.e("Det"+i,""+data.toString());

            }
            //Log.e("Total Length ",""+orderdet.size());
            AdapterOrderView adapter=new AdapterOrderView(getApplicationContext(),pname,pdate,ptotal,oid);
            orderlist.setAdapter(adapter);
        }
        catch(Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Fail to view Order "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    private class LoadOrder extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewAllOrders.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait ");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method


                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("username", username));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectorder.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    // Log.i("Current Order", "" + result);

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                //  Log.i("Result 1 :", result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray = new JSONArray(result.trim());
                    JSONObject jsonObject = null;
                    order = new Vector();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Vector data = new Vector();
                        jsonObject = jsonArray.getJSONObject(i);
                        data.addElement(jsonObject.getString("Customer_Name"));
                        data.addElement(jsonObject.getString("Bill_No"));
                        data.addElement(jsonObject.getString("CDate"));
                        data.addElement(jsonObject.getString("Ins_Date"));
                        data.addElement(jsonObject.getString("Total_amount"));
                        data.addElement(jsonObject.getString("Status"));
                        data.addElement(jsonObject.getString("paymode"));
                        data.addElement(jsonObject.getString("salesman"));
                        data.addElement(jsonObject.getString("approved"));
                        data.addElement(jsonObject.getString("price_level"));
                        order.addElement(data);
                        //Log.e("Final "," "+order.toString());
                    }
                    loadOrderDetails();
                    //loadOrderDetailListItems();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Any Order Found", Toast.LENGTH_LONG).show();
                }

            }
        }
    }
    public void setOrderNumber(String ordernumber)
    {
        this.ordernumber=ordernumber;
    }
    public String getOrderNumber()
    {
        return ordernumber;
    }
    public void editOrder(String oid)
    {
        try
        {
            for(int i=0;i<order.size();i++)
            {
                Vector data=(Vector)order.elementAt(i);
                if(data.elementAt(1).equals(""+oid))
                {
                    //Log.e("Sucess ","YES "+data.elementAt(1));
                    //break;
                    Intent intent = new Intent(getApplicationContext(), ItemDisplay.class);
                    intent.putExtra("UserType", "" + usertype);
                    intent.putExtra("UserName", "" + username);
                    intent.putExtra("BillNumber", "" + oid);
                    startActivity(intent);
                }
                else
                {
                    //Log.e("Sucess ","NO "+data.elementAt(1));
                }
            }
        }
        catch (Exception ex){Log.e("Error In Edit",""+ex.getLocalizedMessage());}
    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(ViewAllOrders.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewAllOrders.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();


            }
            ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        } else {
            //TODO
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();

            }
            ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            //Log.e("Internet 3","Available");
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        }

    }
    private void loadOrderDetails()
    {
        try
        {
            pname=new String[order.size()];
            pdate=new String[order.size()];
            ptotal=new String[order.size()];
            oid=new String[order.size()];
            double bal=0;
            for(int i=0;i<order.size();i++)
            {
                Vector data=new Vector();
                data=(Vector)order.elementAt(i);
                pname[i]=" "+data.elementAt(0);
                if(data.elementAt(8).toString().trim().equals("YES"))
                {
                    pdate[i]="   "+data.elementAt(2)+" | "+data.elementAt(6)+" | Approved";
                }
                else
                {
                    pdate[i]="   "+data.elementAt(2)+" | "+data.elementAt(6);
                }
                bal=Double.parseDouble(""+data.elementAt(4));
                ptotal[i]=""+getResources().getString(R.string.Rs)+" "+roundTwo(bal);
                oid[i]=""+i;
            }
            AdapterOutstanding adapter=new AdapterOutstanding(getApplicationContext(),pname,pdate,ptotal,oid);
            orderlist.setAdapter(adapter);
        }
        catch(Exception ex)
        {
            Log.e("Error ",""+ex.getLocalizedMessage());
            Toast.makeText(getApplicationContext(),"Fail to view Orders ",Toast.LENGTH_SHORT).show();
        }
    }
    public double roundTwo(double value)
    {
        try
        {
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            return Double.valueOf(twoDForm.format(value));
        }
        catch (Exception ex)
        {
            return 0;
        }
    }
    public void processRequest(final int position)
    {
        try
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(ViewAllOrders.this);
            builder.setTitle("Process . .");
            builder.setMessage("Chouse Your Action !");

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    Vector data=(Vector)order.elementAt(position);
                    setOrderID("" + data.elementAt(1));
                    new DeleteOrderDetails().execute("Delete");
                    new LoadOrder().execute("Order");
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(order.size()!=0)
                    {
                        Vector data=(Vector)order.elementAt(position);
                        String approve=""+data.elementAt(8);
                        if(approve.equals("YES"))
                        {
                            Toast.makeText(getApplicationContext(),"Order is approved \nYou can not edit this order !",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent i = new Intent(getApplicationContext(), ItemDisplay.class);
                            i.putExtra("UserType", "" + usertype);
                            i.putExtra("UserName", "" + username);
                            i.putExtra("BillNumber", "" + data.elementAt(1));
                            i.putExtra("Plevel", "" + data.elementAt(9));
                            //  finish();
                            startActivity(i);
                        }

                    }
                    dialog.dismiss();
                }
            });
            builder.setNeutralButton("Approve", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Vector data=(Vector)order.elementAt(position);
                    setOrderID("" + data.elementAt(1));
                    new ApproveOrder().execute("Approve");
                    new LoadOrder().execute("Order");
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            //Log.e("Check 1","Ckecked ");
        }
        catch (Exception ex){
            //Log.e("Exception ",""+ex.getLocalizedMessage());Toast.makeText(ConfirmOrder.this," पचका "+ex.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    public void setOrderID(String orderid)
    {
        this.orderid=orderid;
        //Log.e("pid "," "+pid);

    }
    public String getOrderID()
    {
        return orderid;
    }
    public class ApproveOrder extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewAllOrders.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait For Approve ");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method


                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("ordernumber", getOrderID()));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "approveOrder.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);
                Toast.makeText(getApplicationContext(), "Approved Sucess !", Toast.LENGTH_LONG).show();

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    Toast.makeText(getApplicationContext(), "Approved Sucess !", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Approved Sucess !", Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    public class DeleteOrderDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewAllOrders.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait For Delete ");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method


                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("billnumber", getOrderID()));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "deleteorder.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                }

            }
        }

    }

}
