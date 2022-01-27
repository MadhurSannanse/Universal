package rthree.infotech.r3infotech.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.Vector;

public class SelectCompany extends AppCompatActivity {
    ProgressDialog progressBar;
    ServerDatabase mydb;
    String usertype,username;
    private int progressBarStatus = 0;
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    Spinner selCompany;
    Button setCompany,sendmessage;
    Vector salesmandetails;
    Model model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_company);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selCompany=(Spinner) findViewById(R.id.sp_companynames);
        setCompany=(Button)findViewById(R.id.btn_setCompany);
        sendmessage=(Button)findViewById(R.id.btn_sendmessage);
        try
        {
            salesmandetails=new Vector();
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            //username = b.getString("UserName");
            model=Model.getInstance();
            if(isNetworkAvailable()) {
                new LoadCompanies().execute("Products");
            }
            else {
                Toast.makeText(SelectCompany.this, "No Internte", Toast.LENGTH_SHORT).show();
            }
            setCompany.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i=0;i<salesmandetails.size();i++)
                    {
                        Vector data=(Vector)salesmandetails.elementAt(i);

                        if(selCompany.getSelectedItem().toString().trim().equals(""+data.elementAt(1).toString().trim()))
                        {
                            mydb=new ServerDatabase(getApplicationContext());
                            mydb.updateCompany(selCompany.getSelectedItem().toString().trim(),""+data.elementAt(2).toString().trim());
                            mydb.getCompanyNameDetails();
                            Toast.makeText(getApplicationContext(),"Company Selected Sucessfully !",Toast.LENGTH_SHORT).show();
                            Log.e(""+selCompany.getSelectedItem().toString().trim(),""+data.elementAt(2));
                            new LoadCompanyDetails().execute("");
                            break;
                        }
                        else
                        {
                            //  Toast.makeText(getApplicationContext(),"Invalid - Route !",Toast.LENGTH_SHORT).show();
                        }
                        try
                        {
                            checkCompanyDetails();

                        }
                        catch (Exception Ex){}

                    }
                }
            });
            sendmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"API not found !",Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch(Exception ex){
            Log.e("Error 1",""+ex.getLocalizedMessage());}
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if(id == android.R.id.home)
        {
            this.finish();
            return true;
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
    public void checkCompanyDetails()
    {
        try
        {
            ServerDatabase mydb = new ServerDatabase(getApplicationContext());
            Vector comp = mydb.getCompanyDetails();
            if(comp.size()==0)
            {
                new LoadCompanyDetails().execute("");
            }
            else
            {
                Model model=Model.getInstance();
                model.setCompanyName(""+comp.elementAt(0));
                model.setCompanyContact(""+comp.elementAt(1));
                model.setCompanyAddress(""+comp.elementAt(2));
                model.setCompanyGSTIN(""+comp.elementAt(3));
            }
        }
        catch (Exception ex){Log.i("Comp error",""+ex.getLocalizedMessage());}
    }
    private class LoadCompanyDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(SelectCompany.this);
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait.");
            Dialog.show();
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
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
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectCompany.php");
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
                    mydb=new ServerDatabase(getApplicationContext());
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);
                        Vector company=new Vector();
                        company.addElement(jsonObject.getString("ID"));
                        company.addElement(jsonObject.getString("company_name"));
                        company.addElement(jsonObject.getString("contact_no"));
                        company.addElement(jsonObject.getString("address"));
                        company.addElement(jsonObject.getString("gstin_no"));
                        mydb.deleteCompanyDetails();
                        if (mydb.addCompanyDetails(company)) {
                            checkCompanyDetails();

                        }
                    }

                } catch (Exception e) {
                    Log.e("Company", "No Company "+e.getLocalizedMessage());
                    //Toast.makeText(getApplicationContext(), "No Any Company Details Found ", Toast.LENGTH_LONG).show();

                }

            }
        }

    }
    private class LoadCompanies extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(SelectCompany.this);


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
                String imeiid="";
                // Log.i("6","Getting User Data");
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                model =Model.getInstance();
                nameValuePairs.add(new BasicNameValuePair("username",model.getUsername()));
                Log.e("Valid User",""+new JsonBilder().getHostName() + "selectcompanynames.php");
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectcompanynames.php");
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
                    ArrayList<String> arrlst = new ArrayList<String>();
                    arrlst.add("Select Company");
                    salesmandetails=new Vector();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        Vector data=new Vector();
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));
                        data.addElement(jsonObject.getString("ID"));
                        data.addElement(jsonObject.getString("Company_Name"));
                        data.addElement(jsonObject.getString("Server_Name"));
                        arrlst.add(jsonObject.getString("Company_Name"));
                        salesmandetails.addElement(data);
                    }
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    selCompany.setAdapter(ard);
                } catch (Exception e) {
                    ArrayList<String> arrlst = new ArrayList<String>();
                    arrlst.add("Select Company");
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    selCompany.setAdapter(ard);
                    Toast.makeText(getApplicationContext(), "No Any Company Found", Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(SelectCompany.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SelectCompany.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
