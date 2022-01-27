package rthree.infotech.r3infotech.mobile;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class NewOrder extends AppCompatActivity {
    Button btn_process;
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    AutoCompleteTextView party;
    TextView date,txtouts;
    ToggleButton mode;
    ProgressDialog progressBar;
    ServerDatabase mydb;
    String usertype,username,locationstr,paymode;
    int lastbillno;
    int billno=1;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    Vector customer,customerdetails;
    int year_x,month_x,day_x;
    static final int DILOG_ID=0;
    Vector order,plevel,outs,suspended;
    double totalouts=0;
    String partyname,pricelevel="";
    boolean issuspended=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        btn_process=(Button)findViewById(R.id.btn_process);
        party=(AutoCompleteTextView) findViewById(R.id.atv_party);
        date=(TextView)findViewById(R.id.txt_date);
        mode=(ToggleButton)findViewById(R.id.btn_cashcredit);
        btn_process=(Button)findViewById(R.id.btn_process);
        txtouts=(TextView)findViewById(R.id.txt_outstanding);
        loadTodaysDate();
        customer=new Vector();
        customerdetails=new Vector();
        plevel=new Vector();
        try
        {
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            locationstr=b.getString("Location");
            lastbillno=0;
            order=new Vector();
            //loadSpCustomer(btnprocess);
            //loadSpDistributor(btnprocess);
            if(usertype.equals("Customer"))
            {
                new GetLastBillNumber().execute("Loading");

            }
            else {
                new GetLastBillNumber().execute("Loading");
                new LoadSuspendPartyNames().execute("");
                new LoadPartyNames().execute("Party");

            }
            // billno=getLastBillNumber(party);
            final Calendar cal=Calendar.getInstance();
            year_x=cal.get(Calendar.YEAR);
            month_x=cal.get(Calendar.MONTH);
            day_x=cal.get(Calendar.DAY_OF_MONTH);
            Log.i("Cur Date ",""+day_x+"/"+month_x+"/"+year_x);
            showDateDialog();
            Log.e("UserType",""+usertype);

        }
        catch (Exception ex)
        {
            ////////Log.e("Error ","Main "+ex.getLocalizedMessage());
        }

        try
        {
            party.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String plev="";
                    boolean ch=false;
                    for(int i=0;i<plevel.size();i++)
                    {
                        if(customer.elementAt(i).toString().trim().equals(party.getText().toString().trim()))
                        {
                            plev=""+plevel.elementAt(i);
                            break;
                        }
                    }
                    if(plev.equals("")||plev.equals(null))
                    {
                        Toast.makeText(getApplicationContext(),"Customer not mapped to Pricce List,\n Please contact Admin !",Toast.LENGTH_SHORT).show();
                    }
                    for(int i=0;i<suspended.size();i++)
                    {
                        Vector sus=(Vector)suspended.elementAt(i);
                        if(party.getText().toString().trim().equals(""+sus.elementAt(2).toString().trim()))
                        {
                            issuspended=true;
                            Toast.makeText(getApplicationContext(),"Customer is temporarily Suspended, Please contact Admin !",Toast.LENGTH_SHORT).show();
                            break;
                        }

                    }
                    partyname=party.getText().toString().trim();
                    for(int i=0;i<customerdetails.size();i++)
                    {
                        Vector data=(Vector)customerdetails.elementAt(i);
                        if(data.elementAt(0).toString().trim().equals(partyname))
                        {
                            String outs=data.elementAt(1).toString().trim();
                            try
                            {
                                double o=Double.parseDouble(outs);
                                if(o<0)
                                {
                                    txtouts.setText("Current Outstanding : Dr "+getResources().getString(R.string.Rs)+" "+roundTwo(Math.abs(o)));
                                    txtouts.setTextColor(Color.RED);

                                }
                                else  if(o==0)
                                {
                                    txtouts.setText("Current Outstanding :  "+getResources().getString(R.string.Rs)+"  0");
                                }
                                else
                                {
                                    txtouts.setText("Current Outstanding : Cr "+getResources().getString(R.string.Rs)+" "+roundTwo(Math.abs(o)));
                                }
                            }
                            catch (Exception ex){}

                        }
                    }
                    // new LoadOutstandingAmount().execute("");
                }
            });
            btn_process.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        if(party.getText().toString().trim().equals("Select Party"))
                        {
                            Toast.makeText(getApplicationContext(),"Select Party Name For Sale",Toast.LENGTH_SHORT).show();
                        }
                        else  if(party.getText().toString().trim().equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Select Party Name For Sale",Toast.LENGTH_SHORT).show();
                        }
                        else  if(party.getText().toString().trim().equals("Select Party"))
                        {
                            Toast.makeText(getApplicationContext(),"Select Party Name For Sale",Toast.LENGTH_SHORT).show();
                        }
                        else if(date.getText().toString().trim().equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Select Order Date",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            boolean valid=false;
                            for(int i=0;i<customer.size();i++)
                            {
                                if(customer.elementAt(i).toString().trim().equals(party.getText().toString().trim()))
                                {
                                    valid=true;
                                    break;
                                }
                            }
                            if(valid) {
                                String partyname = party.getText().toString().trim();
                                partyname=partyname.replace("'","''");
                                String curdate = date.getText().toString().trim();
                                String paymode = mode.getText().toString().trim();
                                SimpleDateFormat Ins_Date = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");
                                String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
                                order = new Vector();
                                order.addElement("" + partyname);
                                order.addElement("" + lastbillno);
                                order.addElement("" + curdate);
                                order.addElement("" + InsDate);
                                order.addElement("0");
                                order.addElement("0");
                                order.addElement("0");
                                order.addElement("0");
                                order.addElement("0");
                                order.addElement("NO");
                                if (mode.isChecked()) {
                                    order.addElement("CASH");
                                } else {
                                    order.addElement("CREDIT");

                                }
                                order.addElement("" + username);
                                order.addElement("" + locationstr);
                                order.addElement("");
                                order.addElement("NO");
                                order.addElement("");
                                //Log.e("Order 1",""+order.toString());
                                String plev="";
                                partyname=partyname.replace("''","'");
                                for(int i=0;i<plevel.size();i++)
                                {
                                    if(customer.elementAt(i).toString().trim().equals(partyname))
                                    {
                                        pricelevel=""+plevel.elementAt(i);
                                        break;
                                    }
                                }
                                if(!issuspended) {
                                    new SaveOrder().execute("Save");
                                    Log.e("Order 1 ", "" + billno);
                                    // serverOrder(view, details);


                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Customer is temporarily Suspended, Please contact Admin !",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Invalid Party Name !",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    catch (Exception ex)
                    {

                    }
                }
            });
        }
        catch (Exception ex){}
        try
        {
            mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        paymode="CASH";
                        mode.setBackgroundDrawable(getResources().getDrawable(R.drawable.cash));

                    }
                    else
                    {
                        paymode="CREDIT";
                        mode.setBackgroundDrawable(getResources().getDrawable(R.drawable.credit));

                    }
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
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    /* @Override
     public void onBackPressed()
     {
        try
        {
            Intent intent = new Intent(getApplicationContext(), Home.class);
            intent.putExtra("UserType", "" + usertype);
            intent.putExtra("UserName", "" + username);
            //intent.putExtra("Location",""+locationstr);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            finish();
            startActivity(intent);
        }
        catch (Exception ex){}
     }*/
    private void serverOrder(View v, final Vector details) {
        try {
            progressBar = new ProgressDialog(v.getContext());
            progressBar.setCancelable(true);
            progressBar.setMessage("Please Wait ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
            //reset progress bar and filesize status
            progressBarStatus = 0;
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            new Thread(new Runnable() {
                public void run() {
                    while (progressBarStatus < 1000) {
                        // performing operation

                        progressBarStatus = 1000;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        // Updating the progress bar
                        final boolean post = progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressBarStatus);
                                try {
                                    String st="";
                                    try {
                                        st = new JsonBilder().insertOrder(details);
                                        //custname.setText("");
                                        //contactno.setText("");
                                        //address.setText("");
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed To Add Order " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    if (st.equals("sucess")) {
                                        //Toast.makeText(getBaseContext(), "Customer Added Successfully", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    //Toast.makeText(getApplicationContext(),"Customer Added Successfully ",Toast.LENGTH_LONG).show();
                                }


                            }
                        });
                    }
                    // performing operation if file is downloaded,
                    if (progressBarStatus >= 100) {
                        // sleeping for 1 second after operation completed
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        // close the progress bar dialog
                        progressBar.dismiss();
                    }

                }
            }).start();
        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), "Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    public int getLastBillNumber(View v)
    {
        try {
            progressBar = new ProgressDialog(v.getContext());
            progressBar.setCancelable(true);
            progressBar.setMessage("Please Wait ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
            //reset progress bar and filesize status
            progressBarStatus = 0;
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            new Thread(new Runnable() {
                public void run() {
                    while (progressBarStatus < 1000) {
                        // performing operation
                        progressBarStatus = 1000;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        // Updating the progress bar
                        progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressBarStatus);
                                try {
                                    try {
                                        try {
                                            if (Build.VERSION.SDK_INT > 9) {
                                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                        .permitAll().build();
                                                StrictMode.setThreadPolicy(policy);
                                            }
                                            JSONArray jArray = new JSONArray(new JsonBilder().getLastBillNumber());
                                            JSONObject json_data = null;
                                            int no = 1;
                                            for (int i = 0; i < jArray.length(); i++) {
                                                json_data = jArray.getJSONObject(i);
                                                //  login.addElement(json_data.getString("ID"));
                                                String bill = json_data.getString("max").toString().trim();
                                                if (!bill.equals("")) {
                                                    try {
                                                        no = Integer.parseInt(bill);
                                                        no = no + 1;
                                                        lastbillno= no;
                                                    }catch (Exception ex){Toast.makeText(getApplicationContext(),"Error in bill number",Toast.LENGTH_SHORT).show();}

                                                } else {
                                                    no = 1;
                                                    lastbillno = no;
                                                }

                                            }
                                            //progressBar.dismiss();
                                            //Toast.makeText(getApplicationContext(),"Number loaded Sucessfully ", Toast.LENGTH_SHORT).show();

                                        } catch (Exception e) {
                                            //e.printStackTrace();
                                            //   Toast.makeText(getApplicationContext(),"Request Is Under Processed ..!" ,Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception ex) {
                                        Toast.makeText(getApplicationContext(), "Error " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                    }


                                } catch (Exception ex) {
                                    Toast.makeText(getBaseContext(), "Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    // performing operation if file is downloaded,
                    if (progressBarStatus >= 100) {
                        // sleeping for 1 second after operation completed
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        // close the progress bar dialog
                        progressBar.dismiss();
                    }

                }
            }).start();

        }catch (Exception ex){}
        return lastbillno;
    }
    public void loadSpCustomer(View v)
    {
        try {
            progressBar = new ProgressDialog(v.getContext());
            progressBar.setCancelable(true);
            progressBar.setMessage("Please Wait ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
            //reset progress bar and filesize status
            progressBarStatus = 0;
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            new Thread(new Runnable() {
                public void run() {
                    while (progressBarStatus < 1000) {
                        // performing operation
                        progressBarStatus = 1000;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        // Updating the progress bar
                        progressBarHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(progressBarStatus);
                                try {
                                    try
                                    {
                                        try
                                        {
                                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                        .permitAll().build();
                                                StrictMode.setThreadPolicy(policy);
                                            }
                                            ////////Log.e("Log2 ",""+username);
                                            JSONArray jArray = new JSONArray(new JsonBilder().getCustomerNames(username));
                                            ////////Log.e("Data ",""+jArray.toString());
                                            JSONObject json_data = null;
                                            ////////Log.e("Log ","Log 3");
                                            ArrayList<String> arrlst = new ArrayList<String>();
                                            arrlst.add("Select Party");
                                            customer=new Vector();
                                            for (int i = 0; i < jArray.length(); i++)
                                            {
                                                json_data = jArray.getJSONObject(i);
                                                //  login.addElement(json_data.getString("ID"));
                                                customer.addElement(json_data.getString("name"));
                                                arrlst.add(json_data.getString("name"));
                                            }
                                            ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, arrlst);
                                            ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                                            party.setAdapter(ard);
                                            ////////Log.e("Log ","Log 4");
                                            //Toast.makeText(getApplicationContext(),"Login Names Updated Sucessfully ", Toast.LENGTH_SHORT).show();

                                        } catch (Exception e) {
                                            //e.printStackTrace();
                                            Toast.makeText(getApplicationContext(),
                                                    "Customer Not Found ..!" ,
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                    catch(Exception ex)
                                    {
                                        Toast.makeText(getApplicationContext(), "Error "+ex.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception ex) {
                                    Toast.makeText(getBaseContext(), "Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    // performing operation if file is downloaded,
                    if (progressBarStatus >= 100) {
                        // sleeping for 1 second after operation completed
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        // close the progress bar dialog
                        progressBar.dismiss();
                    }

                }
            }).start();
        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), "Error " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    public void showDateDialog()
    {
        try {
           /* date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DILOG_ID);
                }
            });*/
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Error in show Dialog"+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
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
                year_x = year;
                month_x = monthOfYear + 1;
                day_x = dayOfMonth;
                date.setText("" + day_x + "/" + month_x + "/" + year_x);
                Log.i("Date",""+day_x + "/" + month_x + "/" + year_x);
                btn_process.requestFocus();
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Error in show date"+ex.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    };
    private class LoadPartyNames extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(NewOrder.this);


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
                Log.i("Username",""+username);

                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectucustomer.php");
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
                    plevel=new Vector();
                    customerdetails=new Vector();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        Vector o=new Vector();
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));
                        customer.addElement(jsonObject.getString("name"));
                        plevel.addElement(jsonObject.getString("price_level"));
                        arrlst.add(jsonObject.getString("name"));
                        o.addElement(jsonObject.getString("name"));
                        o.addElement(jsonObject.getString("closing_balance"));
                        customerdetails.addElement(o);
                    }
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.activity_item__autocomplete,R.id.txt_name,arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    party.setAdapter(ard);



                } catch (Exception e) {
                    ArrayList<String> arrlst = new ArrayList<String>();
                    arrlst.add("Select Party");
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(),R.layout.activity_item__autocomplete,R.id.txt_name, arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    party.setAdapter(ard);
                    Toast.makeText(getApplicationContext(), "No Any Party Found ", Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    private class GetLastBillNumber extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(NewOrder.this);


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
                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectlastbillnumber.php");
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
                    int no = 1;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));
                        String bill = jsonObject.getString("max").toString().trim();
                        if (!bill.equals("")) {
                            try {
                                no = Integer.parseInt(bill);
                                no = no + 1;
                                lastbillno= no;
                                if(usertype.equals("Customer")) {
                                    isCustomer();
                                }
                            }catch (Exception ex){
                                //Toast.makeText(getApplicationContext(),"Error in bill number",Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            no = 1;
                            lastbillno = no;
                            if(usertype.equals("Customer")) {
                                isCustomer();
                            }
                        }

                    }



                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "No Any Distributor Found", Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private class SaveOrder extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(NewOrder.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait Order Is Processing");
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
                Log.i("Check Order"," Checking");
                String request="";
                for (int i = 0; i < order.size(); i++) {
                    if(""+order.elementAt(i)==""||""+order.elementAt(i)==null)
                    {
                        //m.add(i,"0");
                    }
                    request += order.elementAt(i).toString().trim() ;
                    if(i==order.size()-1)
                    {
                        // continue;
                    }
                    else
                    {
                        request=request+"~~~";
                    }

                }
                request=request+"^^^";
                Log.e("Order 2",""+request);
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("details", request));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "insertorder.php");
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
                    Intent i = new Intent(getApplicationContext(), ItemDisplay.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    i.putExtra("BillNumber", "" + lastbillno);
                    i.putExtra("Plevel", "" + pricelevel);
                    //  finish();
                    startActivity(i);
                } catch (Exception e) {
                    Intent i = new Intent(getApplicationContext(), ItemDisplay.class);
                    i.putExtra("UserType", "" + usertype);
                    i.putExtra("UserName", "" + username);
                    i.putExtra("BillNumber", "" + lastbillno);
                    i.putExtra("Plevel", "" + pricelevel);
                    //  finish();
                    startActivity(i);
                    //Toast.makeText(getApplicationContext(), "No Any Found"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private void isCustomer()
    {
        try
        {
            try
            {
                SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy hh:mm a");
                SimpleDateFormat Ins_Date = new SimpleDateFormat("d/MM/yyyy hh:mm a");
                String partyname = username.toString().trim();
                String curdate = Ins_Date.format(Calendar.getInstance().getTime());
                String paymode = "Cash";
                String distname = usertype.toString().trim();
                String date = df.format(Calendar.getInstance().getTime());
                String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
                order = new Vector();
                order.addElement("" + partyname);
                order.addElement("" + lastbillno);
                order.addElement("" + curdate);
                order.addElement("" + InsDate+" "+date);
                order.addElement("0");
                order.addElement("NO");
                order.addElement("" + paymode);
                order.addElement("" + username);
                //Log.e("Order 1",""+order.toString());
                new SaveOrder().execute("Save");
                //////Log.e("Bill 1 ",""+billno);
                // serverOrder(view, details);
                Intent i = new Intent(getApplicationContext(), ItemDisplay.class);
                i.putExtra("UserType", "" + usertype);
                i.putExtra("UserName", "" + username);
                i.putExtra("BillNumber", "" + lastbillno);
                // this.finish();
                // startActivity(i);



            }
            catch (Exception ex)
            {

            }
        }
        catch (Exception ex){}
    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(NewOrder.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewOrder.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
    private void loadTodaysDate()
    {
        try
        {
            Calendar c=Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(c.getTime());
            date.setText(""+formattedDate);

        }
        catch (Exception ex){}
    }
    private class LoadOutstandingAmount extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(NewOrder.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Outstanding is loading");
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
                nameValuePairs.add(new BasicNameValuePair("party", ""+partyname));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectpartyouts.php");
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
                    Log.i("Current Order",""+result);

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
                Log.i("Result 1 :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    outs=new Vector();
                    Vector o;
                    ArrayList<String> arrlst = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        Log.e("Comming ","Sucess "+i);
                        jsonObject = jsonArray.getJSONObject(i);

                        //  login.addElement(json_data.getString("ID"));
                        o=new Vector();
                        o.addElement(jsonObject.getString("Billno"));
                        o.addElement(jsonObject.getString("Billdate"));
                        o.addElement(jsonObject.getString("party"));
                        o.addElement(jsonObject.getString("Amount"));
                        o.addElement(jsonObject.getString("Salesman"));
                        o.addElement(jsonObject.getString("Opening"));
                        o.addElement(jsonObject.getString("Onaccount"));
                        outs.addElement(o);

                    }
                    loadOutsDetails();

                } catch (Exception e) {
                    Log.e("Error outs",""+ e.getLocalizedMessage());
                    Toast.makeText(getApplicationContext(), "No Any Outstanding Available", Toast.LENGTH_LONG).show();
                    txtouts.setText("");
                }

            }
        }

    }
    private void loadOutsDetails()
    {
        try
        {
            double tot=0,onacc=0,paidamount=0,bal=0;
            String overdue="";
            totalouts=0;
            for(int i=0;i<outs.size();i++)
            {
                Vector data=(Vector)outs.elementAt(i);
                try {
                    paidamount = Double.parseDouble(data.elementAt(5)
                            .toString().trim())
                            - Double.parseDouble(data.elementAt(3).toString()
                            .trim());
                }catch (Exception ex){paidamount=0;}
                try {
                    bal = roundTwo((Double.parseDouble("" + data.elementAt(3))));
                }catch (Exception ex){bal=0;}
                totalouts=totalouts+bal;
                try {
                    if(onacc==0) {
                        onacc = roundTwo((Double.parseDouble("" + data.elementAt(6))));
                    }
                }catch (Exception ex){onacc=0;}
            }
            if((totalouts+onacc)>0)
            {
                txtouts.setText("Current Outstanding : Dr "+getResources().getString(R.string.Rs)+" "+roundTwo(Math.abs(totalouts+onacc)));
                txtouts.setTextColor(Color.RED);

            }
            else
            {
                txtouts.setText("Current Outstanding : Cr "+getResources().getString(R.string.Rs)+" "+roundTwo(Math.abs(totalouts+onacc)));
            }

        }
        catch(Exception ex)
        {
            Log.e("Error outs",""+ ex.getLocalizedMessage());
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
    private class LoadSuspendPartyNames extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(NewOrder.this);
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

                } catch (Exception e) {

                    //Toast.makeText(getApplicationContext(), "No Any Party Found ", Toast.LENGTH_LONG).show();

                }

            }
        }

    }
}
