package rthree.infotech.r3infotech.mobile;

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
public class AdminReceiptView extends AppCompatActivity {
    String [] sname,total,totalorder,oid;
    String usertype,username;
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    ListView adminreceiptlist;
    Vector receipt;
    int year_x,month_x,day_x;
    static final int DILOG_ID=0;
    Model model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_receipt_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adminreceiptlist=(ListView)findViewById(R.id.lst_adminreceiptview);
        try {
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            final Calendar cal=Calendar.getInstance();
            year_x=cal.get(Calendar.YEAR);
            month_x=cal.get(Calendar.MONTH);
            day_x=cal.get(Calendar.DAY_OF_MONTH);
            model=Model.getInstance();
            loadTodaysDate();
            // billnumber=b.getString("BillNumber");
            if (isNetworkAvailable()) {
                new LoadReceipts().execute("Receipt");
            } else {
                Toast.makeText(AdminReceiptView.this, "No Internte Connection", Toast.LENGTH_SHORT).show();
            }
            adminreceiptlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isNetworkAvailable()) {
                        Intent intent = new Intent(getApplicationContext(), ViewAllReceipts.class);
                        String salesman="";
                        Vector o=new Vector();
                        o=(Vector)receipt.elementAt(position);
                        salesman=""+o.elementAt(0);
                        intent.putExtra("Salesman", "" + salesman);
                        startActivity(intent);

                    } else {
                        Toast.makeText(AdminReceiptView.this, "No Internte", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception ex){}

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
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(AdminReceiptView.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AdminReceiptView.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
            sname=new String[receipt.size()];
            total=new String[receipt.size()];
            totalorder=new String[receipt.size()];
            oid=new String[receipt.size()];
            double bal=0;
            for(int i=0;i<receipt.size();i++)
            {
                Vector data=new Vector();
                data=(Vector)receipt.elementAt(i);
                sname[i]=" "+data.elementAt(0);
                totalorder[i]="  Total No. Of Receipts "+data.elementAt(1);
                bal=Double.parseDouble("0"+data.elementAt(2));
                total[i]=""+getResources().getString(R.string.Rs)+" "+roundTwo(bal);
                oid[i]=""+i;
            }
            AdapterOutstanding adapter=new AdapterOutstanding(getApplicationContext(),sname,totalorder,total,oid);
            adminreceiptlist.setAdapter(adapter);
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
    private class LoadReceipts extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(AdminReceiptView.this);


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
                nameValuePairs.add(new BasicNameValuePair("curdate", model.getTodaysDateNew()));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectcountallreceipt.php");
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
                        data.addElement(jsonObject.getString("Salesman"));
                        data.addElement(jsonObject.getString("c"));
                        data.addElement(jsonObject.getString("t"));
                        receipt.addElement(data);
                        //Log.e("Final "," "+receipt.toString());
                    }
                    loadOrderDetails();
                    //loadOrderDetailListItems();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Any Receipt Found", Toast.LENGTH_LONG).show();
                }

            }
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
                String strmon="",strday="";
                Model model=Model.getInstance();
                year_x = year;
                month_x = monthOfYear + 1;
                day_x = dayOfMonth;
                String monstr="";
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
