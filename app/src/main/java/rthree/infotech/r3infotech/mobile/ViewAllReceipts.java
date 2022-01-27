package rthree.infotech.r3infotech.mobile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
/*import android.support.v4.app.ActivityCompat;
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
import android.widget.DatePicker;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

@SuppressWarnings("ALL")
public class ViewAllReceipts extends AppCompatActivity {
    String [] pname,pdate,ptotal,pdist,oid;
    String usertype,username,billnumber,ordernumber,salesman,receiptid="";
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    ListView receiptlist;
    Vector receipt;
    int year_x,month_x,day_x;
    static final int DILOG_ID=0;
    Model model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_receipts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        receiptlist=(ListView)findViewById(R.id.lst_receiptview);
        try
        {
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            salesman=b.getString("Salesman");
            final Calendar cal=Calendar.getInstance();
            year_x=cal.get(Calendar.YEAR);
            month_x=cal.get(Calendar.MONTH);
            day_x=cal.get(Calendar.DAY_OF_MONTH);
            model=Model.getInstance();
            if(model.getTodaysDateNew().equals(""))
            {
                loadTodaysDate();
            }
            new LoadReceipts().execute("Receipts");
            receiptlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isNetworkAvailable()) {
                        setReceiptID(""+view.getId());
                        processRequest(position);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internte Connection", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        }
        catch (Exception ex)
        {

        }
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
                showDialog(DILOG_ID);
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
    /* @Override
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
     }*/
    private void loadTodaysDate()
    {
        try
        {
            Calendar c=Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
            String formattedDate = df.format(c.getTime());
            Model model=Model.getInstance();
            model.setTodaysDateNew(formattedDate);

        }
        catch (Exception ex){}
    }
    private class LoadReceipts extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewAllReceipts.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait");
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
                nameValuePairs.add(new BasicNameValuePair("username", salesman));
                nameValuePairs.add(new BasicNameValuePair("curdate", model.getTodaysDateNew()));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectallreceipt.php");
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
                    receipt = new Vector();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Vector data = new Vector();
                        jsonObject = jsonArray.getJSONObject(i);
                        data.addElement(jsonObject.getString("Party_Name"));
                        data.addElement(jsonObject.getString("Ins_Date"));
                        data.addElement(jsonObject.getString("Amount"));
                        data.addElement(jsonObject.getString("Cheque_Number"));
                        data.addElement(jsonObject.getString("Location"));
                        data.addElement(jsonObject.getString("Salesman"));
                        data.addElement(jsonObject.getString("ID"));
                        data.addElement(jsonObject.getString("Rec_Number"));
                        receipt.addElement(data);
                        //Log.e("Final "," "+order.toString());
                    }
                    loadOrderDetails();
                    //loadOrderDetailListItems();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Any Receipt Found", Toast.LENGTH_LONG).show();
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
            for(int i=0;i<receipt.size();i++)
            {
                Vector data=(Vector)receipt.elementAt(i);
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
        int permissionCheck = ContextCompat.checkSelfPermission(ViewAllReceipts.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewAllReceipts.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
            pname=new String[receipt.size()];
            pdate=new String[receipt.size()];
            ptotal=new String[receipt.size()];
            oid=new String[receipt.size()];
            double bal=0;
            for(int i=0;i<receipt.size();i++)
            {
                Vector data=new Vector();
                data=(Vector)receipt.elementAt(i);
                pname[i]=" "+data.elementAt(0);
                pdate[i]="   "+data.elementAt(1)+" | "+data.elementAt(7)+" | "+data.elementAt(3);
                bal=Double.parseDouble(""+data.elementAt(2));
                ptotal[i]=""+getResources().getString(R.string.Rs)+" "+roundTwo(bal);
                oid[i]=""+data.elementAt(6);
            }
            AdapterOutstanding adapter=new AdapterOutstanding(getApplicationContext(),pname,pdate,ptotal,oid);
            receiptlist.setAdapter(adapter);
        }
        catch(Exception ex)
        {
            Log.e("Error ",""+ex.getLocalizedMessage());
            Toast.makeText(getApplicationContext(),"Fail to view Outstanding ",Toast.LENGTH_SHORT).show();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewAllReceipts.this);
            builder.setTitle("Deleteing . .");
            builder.setMessage("Chouse Your Action !");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    Vector data=(Vector)receipt.elementAt(position);
                    //setReceiptID("" + data.elementAt(1));
                    new DeleteReceiptDetails().execute("Delete");
                    new LoadReceipts().execute("Order");
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {


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
    public class DeleteReceiptDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewAllReceipts.this);


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
                nameValuePairs.add(new BasicNameValuePair("receiptno", getReceiptID()));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "deletereceipt.php");
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
                    new LoadReceipts().execute("Order");
                }

            }
        }

    }
    public void setReceiptID(String receiptid)
    {
        this.receiptid=receiptid;
        //Log.e("pid "," "+pid);

    }
    public String getReceiptID()
    {
        return receiptid;
    }
    @Override
    protected Dialog onCreateDialog(int id)
    {
        try {
            if (id == DILOG_ID) {
                return new DatePickerDialog(this, dpickerlistener, year_x, month_x, day_x);
            }
            return null;
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Error in show Dialog 1"+ex.getMessage(),Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    protected DatePickerDialog.OnDateSetListener dpickerlistener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try
            {
                String strmon="",strday="";
                Model model=Model.getInstance();
                year_x = year;
                month_x = monthOfYear + 1;
                day_x = dayOfMonth;
                String monstr="";
                if(month_x==1)
                {
                    monstr="Jan";
                }
                if(month_x==2)
                {
                    monstr="Feb";
                }
                if(month_x==3)
                {
                    monstr="Mar";
                }
                if(month_x==4)
                {
                    monstr="Apr";
                }
                if(month_x==5)
                {
                    monstr="May";
                }
                if(month_x==6)
                {
                    monstr="Jun";
                }
                if(month_x==7)
                {
                    monstr="Jul";
                }
                if(month_x==8)
                {
                    monstr="Aug";
                }
                if(month_x==9)
                {
                    monstr="Sep";
                }
                if(month_x==10)
                {
                    monstr="Oct";
                }
                if(month_x==11)
                {
                    monstr="Nov";
                }
                if(month_x==12)
                {
                    monstr="Dec";
                }
                if(month_x < 10){

                    strmon = "0" + month_x;
                }
                else
                {
                    strmon = "" + month_x;
                }
                if(day_x < 10){

                    strday  = "0" + day_x ;
                }
                else
                {
                    strday = "" + day_x;
                }
                model.setTodaysDateNew(""+strday+"/"+monstr+"/"+year_x);
                new LoadReceipts().execute("Order");
                //Log.i("Date",""+day_x + "/" + month_x + "/" + year_x);

            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Error in show date"+ex.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    };
}
