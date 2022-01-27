package rthree.infotech.r3infotech.mobile;

import android.Manifest;
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

public class MultipleTargetView extends AppCompatActivity {
    private static final int STORAGE_CODE = 1000;
    ProgressDialog progressBar;
    ServerDatabase mydb;
    String usertype,username;
    private static final int INTERNET=1;
    ListView lstdetails;
    Vector target;
    String[] salesname,tarach,totaltarget,listid;
    double totalachieved=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_target_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lstdetails=(ListView)findViewById(R.id.lst_mtarget);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        usertype = b.getString("UserType");
        username = b.getString("UserName");
        if(isNetworkAvailable()) {
            new LoadTargetDetails().execute("");
        }
        else
        {

        }
        lstdetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vector data=(Vector) target.elementAt(position);
                Intent i = new Intent(getApplicationContext(), ViewSalesmanTarget.class);
                i.putExtra("UserType", "" + usertype);
                i.putExtra("UserName", "" + username);
                i.putExtra("Salesman", "" + data.elementAt(0));
                startActivity(i);
            }
        });
        lstdetails.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Vector data=(Vector) target.elementAt(position);
                Intent i = new Intent(getApplicationContext(), ViewSalesmanTarget.class);
                i.putExtra("UserType", "" + usertype);
                i.putExtra("UserName", "" + username);
                i.putExtra("Salesman", "" + data.elementAt(0));
                startActivity(i);
                return false;
            }
        });
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
    private class LoadTargetDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(MultipleTargetView.this);


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
                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {
                    HttpPost httppost;
                    HttpClient httpclient = new DefaultHttpClient();
                    if(usertype.equals("Admin")) {
                        // Log.i("Admin ","1");
                        httppost = new HttpPost(new JsonBilder().getHostName() + "selectadmintarget.php");
                    }
                    else
                    {
                        //Log.i("Teamleader","1");
                        httppost = new HttpPost(new JsonBilder().getHostName() + "selectteamleadertarget.php");
                    }
                    // httppost = new HttpPost(new JsonBilder().getHostName() + "selecttargetdetails.php");
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
                    Log.i("Current Order -",""+result);

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
                    totalachieved=0;
                    target=new Vector();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);
                        Vector data=new Vector();
                        data.addElement(jsonObject.getString("Salesman"));
                        data.addElement(jsonObject.getString("Target"));
                        data.addElement(jsonObject.getString("Achieved"));
                        data.addElement(jsonObject.getString("ID"));
                        target.addElement(data);
                    }

                    loadTargetDetails();


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Any Details Found"+e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private void loadTargetDetails()
    {
        try
        {
            salesname=new String[target.size()];
            tarach=new String[target.size()];
            totaltarget=new String[target.size()];
            listid=new String[target.size()];
            double ach=0;
            for(int i=0;i<target.size();i++)
            {
                // Log.i("Total ",""+target.size());
                ach=0;
                Vector data=new Vector();
                data=(Vector)target.elementAt(i);
                salesname[i]=" "+data.elementAt(0);
                try
                {
                    ach=Double.parseDouble(""+data.elementAt(2));
                    tarach[i]="   "+getResources().getString(R.string.Rs)+" "+roundTwo(ach) +"   Achieved";
                    ach=Double.parseDouble(""+data.elementAt(1));
                    totaltarget[i]="   "+getResources().getString(R.string.Rs)+" "+roundTwo(ach) ;
                }
                catch (Exception ex){tarach[i]="   "+getResources().getString(R.string.Rs)+" "+ach +"   Achieved";
                    totaltarget[i]="   "+getResources().getString(R.string.Rs)+" "+data.elementAt(1) ;}
                listid[i]=""+data.elementAt(3);
            }
            AdapterOutstanding adapter=new AdapterOutstanding(getApplicationContext(),salesname,tarach,totaltarget,listid);
            lstdetails.setAdapter(adapter);
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

            DecimalFormat twoDForm = new DecimalFormat("#");
            return Double.valueOf(twoDForm.format(value));
        }
        catch (Exception ex)
        {
            return 0;
        }
    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(MultipleTargetView.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MultipleTargetView.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
}
