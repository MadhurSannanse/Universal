package rthree.infotech.r3infotech.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


public class CollectionReport extends AppCompatActivity {
    private static final int INTERNET = 1;
    Model model=Model.getInstance();
    ListView lstcollection;
    Spinner vouchertypes;
    String arrvoucherlist[];
    String salesman="",curdate="";
    int year_x,month_x,day_x,year_x1,month_x1,day_x1;
    static final int DILOG_ID=0;
    int DATE_PICKER_TO = 0;
    int DATE_PICKER_FROM = 1;
    Vector collection;
    Vector company;
    String usertype,username,billnumber,open="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_report);
        vouchertypes=(Spinner) findViewById(R.id.sp_vouchertype);
        lstcollection=(ListView) findViewById(R.id.lst_collection);
        loadSpinner();
        try {
            collection = new Vector();
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            salesman = b.getString("Salesman");
            Calendar c=Calendar.getInstance();
            year_x=c.get(Calendar.YEAR);
            month_x=c.get(Calendar.MONTH);
            day_x=c.get(Calendar.DAY_OF_MONTH);

            year_x1=c.get(Calendar.YEAR);
            month_x1=c.get(Calendar.MONTH);
            day_x1=c.get(Calendar.DAY_OF_MONTH);
            SimpleDateFormat df = new SimpleDateFormat("yyyy/mm/dd");
            curdate = df.format(c.getTime());
            if (isNetworkAvailable()) {
             //   new LoadTodaysCollection().execute("Load");
            } else {
                Toast.makeText(getApplicationContext(), "No Internte", Toast.LENGTH_SHORT).show();
            }

           vouchertypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   new LoadTodaysCollection().execute("Load");
               }

               @Override
               public void onNothingSelected(AdapterView<?> parent) {

               }
           });
        }
        catch (Exception ex){}
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
                showDialog(DATE_PICKER_FROM);
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
    private void loadTodaysDate()
    {
        try
        {
            Calendar c=Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
            String formattedDate = df.format(c.getTime());
            Model model=Model.getInstance();
            model.setTodaysDate(formattedDate);
            model.setFromDate(formattedDate);

        }
        catch (Exception ex){}
    }
    @Override
    protected Dialog onCreateDialog(int id)
    {
        try {
            if (id == DATE_PICKER_FROM) {

                return new DatePickerDialog(this, from_dateListener, year_x, month_x, day_x);

            }
          if (id == DATE_PICKER_TO) {

                return new DatePickerDialog(this, to_dateListener, year_x1, month_x1, day_x1);
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
                Log.i("Date","Only");
                String strmon="",strday="";
                Model model=Model.getInstance();
                year_x = year;
                month_x = monthOfYear + 1;
                day_x = dayOfMonth;
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
                model.setTodaysDate(""+year_x+"/"+strmon+"/"+day_x);
                curdate=model.getTodaysDate();
                new LoadTodaysCollection().execute("");
                //   model.setTodaysDate(year_x+"/"+strmon+"/"+strday);

                // new LoadOrders().execute("Order");
                //Log.i("Date",""+day_x + "/" + month_x + "/" + year_x);

            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Error in show date"+ex.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    };
    protected DatePickerDialog.OnDateSetListener from_dateListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try
            {
                Log.i("Date","From");
                String strmon="",strday="";
                Model model=Model.getInstance();
                year_x = year;
                month_x = monthOfYear + 1;
                day_x = dayOfMonth;
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
                // model.setFromDate(""+strday+"/"+strmon+"/"+year_x);
                model.setTodaysDate(""+year_x+"/"+strmon+"/"+day_x);
                curdate=model.getTodaysDate();
               // new LoadTodaysCollection().execute("");
               showDialog(DATE_PICKER_TO);
                // new LoadOrders().execute("Order");
                //Log.i("Date",""+day_x + "/" + month_x + "/" + year_x);

            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Error in show date"+ex.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
        public void setPermanentTitle(CharSequence title) {

            setTitle("From Order Date");
        }
    };
    protected DatePickerDialog.OnDateSetListener to_dateListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try
            {
                Log.i("Date","To");
                String strmon="",strday="";
                Model model=Model.getInstance();
                year_x1 = year;
                month_x1 = monthOfYear + 1;
                day_x1 = dayOfMonth;
                if(month_x1 < 10){

                    strmon = "0" + month_x1;
                }
                else
                {
                    strmon = "" + month_x1;
                }
                if(day_x1 < 10){

                    strday  = "0" + day_x1 ;
                }
                else
                {
                    strday = "" + day_x1;
                }
                // model.setTodaysDate(""+strday+"/"+strmon+"/"+year_x1);
                model.setTodaysDateNew(year_x1+"/"+strmon+"/"+strday);
               //curdate=model.getTodaysDate();
                new LoadTodaysCollection().execute("Order");
                //Log.i("Date",""+day_x + "/" + month_x + "/" + year_x);

            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Error in show date"+ex.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
        public void setPermanentTitle(CharSequence title) {

            setTitle("To Date");
        }
    };
public void loadSpinner()
{
    try {
        ArrayList <String> arrvoucherlist = new ArrayList<String>();
        arrvoucherlist.add("Anuron Sales");
        arrvoucherlist.add("SALE BTI");
        arrvoucherlist.add("SALE I MARK TV");
        arrvoucherlist.add("SALE REALMI");
        arrvoucherlist.add("Sales");
        arrvoucherlist.add("Sales - Crompton");
        arrvoucherlist.add("Sales - Lava");
        arrvoucherlist.add("Sales - Lava SP");
        arrvoucherlist.add("Sales -Mobile Plug");
        arrvoucherlist.add("Sales - Ottomate");
        arrvoucherlist.add("Sales Zebronics");
        model = Model.getInstance();
        ArrayAdapter ard = new ArrayAdapter(this, R.layout.spinneritems, arrvoucherlist);
        ard.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vouchertypes.setAdapter(ard);

    }
    catch (Exception ex){

    }
}
    private class LoadTodaysCollection extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(CollectionReport.this);


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
                String refstring="";
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                if(vouchertypes.getSelectedItem().toString().equals("ALl"))
                {
                    refstring="All";
                }
                else if(vouchertypes.getSelectedItem().toString().equals("Anuron Sales"))
                {
                    refstring="NCA2";
                }
                else if(vouchertypes.getSelectedItem().toString().equals("SALE BTI"))
                {
                    refstring="NCB2";
                }
                else if(vouchertypes.getSelectedItem().toString().equals("SALE I MARK TV"))
                {
                    refstring="NCI2";
                }
                else if(vouchertypes.getSelectedItem().toString().equals("SALE REALMI"))
                {
                    refstring="NCR2";
                }
                /*else if(vouchertypes.getSelectedItem().toString().equals("Sales"))
                {
                    refstring="";
                }*/
                else if(vouchertypes.getSelectedItem().toString().equals("Sales - Crompton"))
                {
                    refstring="NCC2";
                }
                else if(vouchertypes.getSelectedItem().toString().equals("Sales - Lava"))
                {
                    refstring="NCL2";
                }
                else if(vouchertypes.getSelectedItem().toString().equals("Sales - Lava SP"))
                {
                    refstring="NCLP2";
                }
                else if(vouchertypes.getSelectedItem().toString().equals("Sales -Mobile Plug"))
                {
                    refstring="NCM2";
                }
                else if(vouchertypes.getSelectedItem().toString().equals("Sales - Ottomate"))
                {
                    refstring="NC02";
                }
                else if(vouchertypes.getSelectedItem().toString().equals("Sales Zebronics"))
                {
                    refstring="NCZ2";
                }
                else
                {
                    refstring="All";
                }

                Log.i("vouchertype",""+refstring);
                Log.i("salesman",""+salesman);
                Log.i("date",""+model.getTodaysDate());
                nameValuePairs.add(new BasicNameValuePair("vouchertype",refstring));
                nameValuePairs.add(new BasicNameValuePair("salesman", salesman));
                nameValuePairs.add(new BasicNameValuePair("date", model.getTodaysDate()));
                nameValuePairs.add(new BasicNameValuePair("todate", model.getTodaysDateNew()));

                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectcollectionsalesman.php");
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
                Log.i("Result 2 :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    collection=new Vector();
                    Vector o;
                    ArrayList<String> arrlst = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        Log.e("Comming ","Sucess "+i);
                        jsonObject = jsonArray.getJSONObject(i);

                        //  login.addElement(json_data.getString("ID"));
                        o=new Vector();
                        o.addElement(jsonObject.getString("Doc_Number"));
                        o.addElement(jsonObject.getString("Doc_Date"));
                        String inputPattern = "yyyy-mm-dd";
                        String outputPattern = "dd-mm-yyyy";
                        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                        Date date = null;
                        String str = "";
                        try {
                            date = inputFormat.parse(jsonObject.getString("Doc_Date"));
                            str = outputFormat.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        o.addElement(str);
                        o.addElement(jsonObject.getString("Doc_Type"));
                        o.addElement(jsonObject.getString("Party_Name"));
                        o.addElement(jsonObject.getString("Amount"));
                        o.addElement(jsonObject.getString("Salesman"));
                        o.addElement(jsonObject.getString("Ref_Number"));
                        collection.addElement(o);
                    }
                    loadledgerDetails();
                } catch (Exception e) {
                    Log.e("Error ledger",""+ e.getLocalizedMessage());
                    Toast.makeText(getApplicationContext(), "No Any Ledger Details Available"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private void loadledgerDetails()
    {
        try
        {
            String Billno[]=new String[collection.size()+1];
            String details[]=new String[collection.size()+1];
            String total[]=new String[collection.size()+1];
            String oid[]=new String[collection.size()+1];
            double tot=0,onacc=0,paidamount=0,bal=0;
            String overdue="";
            double totalledger=0;
            int i=0;
            for(i=0;i<collection.size();i++)
            {

                Vector data=(Vector)collection.elementAt(i);
                try {
                    bal = Double.parseDouble(""+data.elementAt(5));

                }catch (Exception ex){bal=0;}
                Billno[i]="  "+data.elementAt(4);
                details[i]=" Agst Bill No:"+data.elementAt(7).toString()+" | \nDate : "+data.elementAt(2);
                total[i]="Cr  "+getResources().getString(R.string.Rs)+" "+bal+"  ";
                oid[i]=""+i;
                totalledger=totalledger+bal;

            }
            Billno[i]="Total Collection :";
            details[i]="";
            total[i]="  "+getResources().getString(R.string.Rs)+" "+totalledger+" ";
            oid[i]=""+i;
            AdapterOutstanding adapter=new AdapterOutstanding(getApplicationContext(),Billno,details,total,oid);
            lstcollection.setAdapter(adapter);
        }
        catch(Exception ex)
        {
            Log.e("Error ",""+ex.getLocalizedMessage());
            Toast.makeText(getApplicationContext(),"Fail to view Ledger ",Toast.LENGTH_SHORT).show();
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
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(CollectionReport.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CollectionReport.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();


            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        } else {
            //TODO
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();

            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            //Log.e("Internet 3","Available");
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        }
    }

}