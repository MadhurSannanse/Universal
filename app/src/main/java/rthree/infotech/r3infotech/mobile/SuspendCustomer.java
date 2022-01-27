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
import android.os.StrictMode;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class SuspendCustomer extends AppCompatActivity {
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    AutoCompleteTextView party;
    ListView lstcustomer;
    ServerDatabase mydb;
    String usertype,username,locationstr;
    Vector customer;
    String partyname,customerID;
    String custname[],datefrom[],suspend[],custid[],color[];
    Vector suspendcustomer,suspended;
    AdapterSuspendCustomer adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspend_customer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        party=(AutoCompleteTextView)findViewById(R.id.atv_allcustomer);
        lstcustomer=(ListView)findViewById(R.id.lst_allcustomer);
        try
        { Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            if(isNetworkAvailable()) {
                loadPartyNames();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No Internte", Toast.LENGTH_SHORT).show();
            }
            party.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            lstcustomer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SuspendCustomer.this);
                        builder.setTitle("Processing . .");
                        builder.setMessage("Are you sure to Suspend / Active ?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing but close the dialog
                                int cid = view.getId();
                                boolean issuspended=false;
                                for(int i=0;i<suspended.size();i++)
                                {
                                    Vector sus=(Vector)suspended.elementAt(i);
                                    int susid=Integer.parseInt(""+sus.elementAt(1));
                                    if(cid==susid)
                                    {
                                        setCustomerID(""+cid);
                                        issuspended=true;
                                        new ActiveParty().execute("");
                                        break;
                                    }

                                }
                                if(!issuspended) {
                                    for (int i = 0; i < customer.size(); i++) {
                                        Vector data = (Vector) customer.elementAt(i);
                                        if (data.elementAt(0).toString().trim().equals("" + cid)) {
                                            suspendcustomer = new Vector();
                                            suspendcustomer.addElement(data.elementAt(0));
                                            suspendcustomer.addElement(data.elementAt(1));
                                            suspendcustomer.addElement(data.elementAt(2));
                                            suspendcustomer.addElement("Suspend");
                                            suspendcustomer.addElement(loadTodaysDate());
                                            new SaveCustomer().execute("");

                                        }
                                    }
                                }
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                        return false;
                    } catch (Exception ex) {
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
    private class LoadPartyNames extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(SuspendCustomer.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait.");
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


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectallcustomer.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // ////Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 1", e.toString());
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

                    // ////Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 2", e.toString());

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
                //Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    ArrayList<String> arrlst = new ArrayList<String>();
                    arrlst.add("Select Party");
                    customer=new Vector();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));
                        Vector cust=new Vector();
                        cust.addElement(jsonObject.getString("ID"));
                        cust.addElement(jsonObject.getString("name"));
                        cust.addElement(jsonObject.getString("salesman"));
                        cust.addElement(jsonObject.getString("ID"));
                        cust.addElement(jsonObject.getString("price_level"));
                        customer.addElement(cust);
                        arrlst.add(jsonObject.getString("name"));

                    }
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.activity_item__autocomplete,R.id.txt_name,arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    party.setAdapter(ard);
                    loadCustomerDetails();


                } catch (Exception e) {
                    ArrayList<String> arrlst = new ArrayList<String>();
                    arrlst.add("Select Party");
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(),R.layout.activity_item__autocomplete,R.id.txt_name, arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    party.setAdapter(ard);
                    Toast.makeText(getApplicationContext(), "No Any Party Found "+e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(SuspendCustomer.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
    private String loadTodaysDate()
    {
        try
        {
            Calendar c=Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(c.getTime());
            return formattedDate;

        }
        catch (Exception ex){return  "";}
    }
    private void loadCustomerDetails()
    {
        try
        {
            custname=new String[customer.size()];
            suspend=new String[customer.size()];
            datefrom=new String[customer.size()];
            custid=new String[customer.size()];
            color=new String[customer.size()];
            for(int i=0;i<customer.size();i++) {
                Vector data = (Vector) customer.elementAt(i);
                boolean issuspended=false;
                for(int j=0;j<suspended.size();j++)
                {
                    Vector sus=(Vector)suspended.elementAt(j);
                    if(data.elementAt(1).toString().trim().equals(""+sus.elementAt(2).toString().trim()))
                    {
                        custname[i] = ""+data.elementAt(1);
                        datefrom[i]="Suspended from "+sus.elementAt(5);
                        custid[i]=""+data.elementAt(0);
                        suspend[i]="Suspend";
                        issuspended=true;
                        color[i]="Red";
                        break;
                    }

                }
                if(!issuspended)
                {
                    custname[i] = ""+data.elementAt(1);
                    datefrom[i]="";
                    custid[i]=""+data.elementAt(0);
                    suspend[i]="Active";
                    color[i]="Green";

                }

            }
            adapter=new AdapterSuspendCustomer(getApplicationContext(),custname,datefrom,suspend,custid,color);
            lstcustomer.setAdapter(adapter);

        }
        catch(Exception ex)
        {
            Log.e("Error Load Customer",""+ ex.getLocalizedMessage());
            Toast.makeText(getApplicationContext(),"Fail to view Customers "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    private class SaveCustomer extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(SuspendCustomer.this);


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
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method
                String request="";
                for (int i = 0; i < suspendcustomer.size(); i++) {
                    if(""+suspendcustomer.elementAt(i)==""||""+suspendcustomer.elementAt(i)==null)
                    {
                        //m.add(i,"0");
                    }
                    request += suspendcustomer.elementAt(i).toString().trim() ;
                    if(i==suspendcustomer.size()-1)
                    {
                        // continue;
                    }
                    else
                    {
                        request=request+"~~~";
                    }

                }
                request=request+"^^^";
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("details", request));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "insertSuspendCustomer.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // ////Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 1", e.toString());
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

                    // ////Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 2", e.toString());

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
                //Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    Toast.makeText(getApplicationContext(),"Sucess ..!",Toast.LENGTH_SHORT).show();
                    loadPartyNames();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Not Done ",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "No Any Found"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private class ActiveParty extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(SuspendCustomer.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait.");
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
                nameValuePairs.add(new BasicNameValuePair("details", getCustomerID()));
                Log.e("Customr id ",""+getCustomerID());

                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "deleteSuspendCustomer.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // ////Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 1", e.toString());
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

                    // ////Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 2", e.toString());

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
                //Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;

                    Toast.makeText(getApplicationContext(), "Sucess ..!", Toast.LENGTH_LONG).show();
                    loadPartyNames();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error "+e.getMessage(), Toast.LENGTH_LONG).show();
                    loadPartyNames();
                }

            }
        }

    }
    private class LoadSuspendPartyNames extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(SuspendCustomer.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait.");
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


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectallcustomersuspend.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // ////Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 1", e.toString());
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

                    // ////Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    ////Log.e("Fail 2", e.toString());

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
                //Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    suspended=new Vector();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));
                        Vector cust=new Vector();
                        cust.addElement(jsonObject.getString("ID"));
                        cust.addElement(jsonObject.getString("Customer_id"));
                        cust.addElement(jsonObject.getString("Name"));
                        cust.addElement(jsonObject.getString("Salesman"));
                        cust.addElement(jsonObject.getString("Status"));
                        cust.addElement(jsonObject.getString("Ins_date"));
                        suspended.addElement(cust);

                    }
                    new LoadPartyNames().execute("");
                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), "No Any Party Found "+e.getMessage(), Toast.LENGTH_LONG).show();
                    new LoadPartyNames().execute("");
                }

            }
        }

    }
    private  void setCustomerID(String id)
    {
        this.customerID=id;
    }
    private String getCustomerID()
    {
        return customerID;
    }
    public void loadPartyNames()
    {
        try
        {
            new LoadSuspendPartyNames().execute("");


        }
        catch (Exception ex){}
    }
}
