package rthree.infotech.r3infotech.mobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
//import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddNewLedger extends AppCompatActivity {
    EditText partyname,address_1,address_2,contactno,emailid,gstno,contactper,pincode;
    AutoCompleteTextView state,plevel,gsttype;
    String usertype,username,locationstr;
    private static final int INTERNET=1;
    Vector pricelevel,ledger,statename,allgstintype;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_ledger);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        partyname=(EditText)findViewById(R.id.et_partyname);
        address_1=(EditText)findViewById(R.id.et_address1);
        address_2=(EditText)findViewById(R.id.et_address2);
        contactno=(EditText)findViewById(R.id.et_contactno);
        emailid=(EditText)findViewById(R.id.et_emailid);
        gstno=(EditText)findViewById(R.id.et_gstno);
        contactper=(EditText)findViewById(R.id.et_contactperson);
        pincode=(EditText)findViewById(R.id.et_pincode);
        state=(AutoCompleteTextView)findViewById(R.id.atv_state);
        plevel=(AutoCompleteTextView)findViewById(R.id.atv_pricelevel);
        gsttype=(AutoCompleteTextView)findViewById(R.id.atv_gsttype);
        save=(Button)findViewById(R.id.btn_save);
        try {
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            locationstr = b.getString("Location");
            loadAllState();
            loadAllGSTType();
            if(isNetworkAvailable())
            {
                new LoadPriceLevel().execute("");
            }
            else {
            }
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        if(address_1.getText().toString().trim().equals(""))
                        {
                            address_1.setError("Enter Party Address");
                        }
                        else  if(address_2.getText().toString().trim().equals(""))
                        {
                            address_2.setError("Enter Party Address");
                        }
                        else  if(state.getText().toString().trim().equals("Select State"))
                        {
                            state.setError("Enter Party Address");
                        }
                        else  if(contactno.getText().toString().trim().equals("")||contactno.getText().toString().trim().length()!=10)
                        {
                            contactno.setError("Invalid Contact Number");
                        }
                        else  if(pincode.getText().toString().trim().equals("")||pincode.getText().toString().trim().length()!=6)
                        {
                            pincode.setError("Invalid Pincode");
                        }
                    /*else  if(!isValidEmail(emailid.getText().toString().trim())||emailid.getText().toString().trim().equals(""))
                      {
                          emailid.setError("Invalid Email");
                      }*/
                        else  if(contactper.getText().toString().trim().equals(""))
                        {
                            contactper.setError("Enter Contact Person Name");
                        }
                    /*  else if(gsttype.getText().toString().trim().equals("Composition")||gsttype.getText().toString().trim().equals("Regular")||gsttype.getText().toString().trim().equals("Consumer"))
                      {
                          if(gstno.getText().toString().trim().equals("")||gstno.getText().toString().trim().length()!=15)
                             {
                                gstno.setError("Invalid GSTIN"); }
                      }*/

                        else {
                            boolean valid1=false,valid2=false;
                            for(int i=0;i<statename.size();i++)
                            {
                                Log.i(""+statename.elementAt(i),""+state.getText());
                                if(statename.elementAt(i).toString().trim().equals(state.getText().toString().trim()))
                                {
                                    valid1=true;
                                    Log.i("Valid "," Valid 1");
                                    break;
                                }
                            }
                            for(int i=0;i<pricelevel.size();i++)
                            {
                                if(pricelevel.elementAt(i).toString().trim().equals(plevel.getText().toString().trim()))
                                {
                                    valid2=true;
                                    Log.i("Valid "," Valid 2");
                                    break;
                                }
                            }
                            if(valid1&&valid2) {
                                String pname = partyname.getText().toString().trim();
                                String add_1 = address_1.getText().toString().trim();
                                String add_2 = address_2.getText().toString().trim();
                                String stname = state.getText().toString().trim();
                                String contact = contactno.getText().toString().trim();
                                String email = emailid.getText().toString().trim();
                                String pricel = plevel.getText().toString().trim();;
                                String gstinno = gstno.getText().toString().trim();
                                String gstintype = gsttype.getText().toString().trim();
                                String contactpername = contactper.getText().toString().trim();
                                String pincodeno=pincode.getText().toString().trim();

                                ledger = new Vector();
                                ledger.addElement(pname);
                                ledger.addElement(username);
                                ledger.addElement(contact);
                                ledger.addElement(pricel);
                                ledger.addElement(add_1);
                                ledger.addElement(add_2);
                                ledger.addElement(stname);
                                ledger.addElement(email);
                                ledger.addElement(pincodeno);
                                ledger.addElement(contactpername);
                                ledger.addElement(gstintype);
                                ledger.addElement(gstinno);


                                new SaveLedger().execute("Save");
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Invalid Details !",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    catch (Exception ex)
                    {
                        Log.e("Error ","Yetoy "+ex.getMessage());
                    }
                }

            });

        }
        catch (Exception ex){}
    }
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
    public  void loadAllState()
    {
        try
        {
            ArrayList<String> arrlst = new ArrayList<String>();
            statename=new Vector();
            arrlst.add("Select State");
            arrlst.add("Andaman and Nicobar Islands");
            arrlst.add("Andhra Pradesh");
            arrlst.add("Arunachal Pradesh");
            arrlst.add("Assam");
            arrlst.add("Bihar");
            arrlst.add("Chandigarh");
            arrlst.add("Chhattisgarh");
            arrlst.add("Dadar and Nagar Haveli");
            arrlst.add("Delhi");
            arrlst.add("Daman and Diu");
            arrlst.add("Goa");
            arrlst.add("Gujarat");
            arrlst.add("Haryana");
            arrlst.add("Himachal Pradesh");
            arrlst.add("Jammu and Kashmir");
            arrlst.add("Jharkhand");
            arrlst.add("Karnataka");
            arrlst.add("Kerala");
            arrlst.add("Lakshadweep");
            arrlst.add("Madhya Pradesh");
            arrlst.add("Maharashtra");
            arrlst.add("Manipur");
            arrlst.add("Meghalaya");
            arrlst.add("Mizoram");
            arrlst.add("Nagaland");
            arrlst.add("Odisha");
            arrlst.add("Puducherry(Pondicherry)");
            arrlst.add("Punjab");
            arrlst.add("Rajasthan");
            arrlst.add("Sikkim");
            arrlst.add("Tamil Nadu");
            arrlst.add("Telangana");
            arrlst.add("Tripura");
            arrlst.add("Uttar Pradesh");
            arrlst.add("Uttarakhand");
            arrlst.add("West Bengal");

            statename.addElement("Andaman and Nicobar Islands");
            statename.addElement("Andhra Pradesh");
            statename.addElement("Arunachal Pradesh");
            statename.addElement("Assam");
            statename.addElement("Bihar");
            statename.addElement("Chandigarh");
            statename.addElement("Chhattisgarh");
            statename.addElement("Dadar and Nagar Haveli");
            statename.addElement("Delhi");
            statename.addElement("Daman and Diu");
            statename.addElement("Goa");
            statename.addElement("Gujarat");
            statename.addElement("Haryana");
            statename.addElement("Himachal Pradesh");
            statename.addElement("Jammu and Kashmir");
            statename.addElement("Jharkhand");
            statename.addElement("Karnataka");
            statename.addElement("Kerala");
            statename.addElement("Lakshadweep");
            statename.addElement("Madhya Pradesh");
            statename.addElement("Maharashtra");
            statename.addElement("Manipur");
            statename.addElement("Meghalaya");
            statename.addElement("Mizoram");
            statename.addElement("Nagaland");
            statename.addElement("Odisha");
            statename.addElement("Puducherry(Pondicherry)");
            statename.addElement("Punjab");
            statename.addElement("Rajasthan");
            statename.addElement("Sikkim");
            statename.addElement("Tamil Nadu");
            statename.addElement("Telangana");
            statename.addElement("Tripura");
            statename.addElement("Uttar Pradesh");
            statename.addElement("Uttarakhand");
            statename.addElement("West Bengal");

            ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.activity_item__autocomplete,R.id.txt_name,arrlst);
            ard.setDropDownViewResource(android.R.layout.select_dialog_item);
            state.setAdapter(ard);
        }
        catch (Exception ex){}
    }
    public  void loadAllGSTType()
    {
        try
        {
            ArrayList<String> arrlst = new ArrayList<String>();
            allgstintype=new Vector();
            arrlst.add("Unknown");
            arrlst.add("Composition");
            arrlst.add("Consumer");
            arrlst.add("Regular");
            arrlst.add("Unregistered");

            allgstintype.addElement("Unknown");
            allgstintype.addElement("Composition");
            allgstintype.addElement("Consumer");
            allgstintype.addElement("Regular");
            allgstintype.addElement("Unregistered");

            ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.activity_item__autocomplete,R.id.txt_name,arrlst);
            ard.setDropDownViewResource(android.R.layout.select_dialog_item);
            gsttype.setAdapter(ard);
        }
        catch (Exception ex){}
    }
    private class LoadPriceLevel extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(AddNewLedger.this);


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
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectpricelevel.php");
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
                    pricelevel=new Vector();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));
                        pricelevel.addElement(jsonObject.getString("price_level"));
                        arrlst.add(jsonObject.getString("price_level"));
                    }
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(), R.layout.activity_item__autocomplete,R.id.txt_name,arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    plevel.setAdapter(ard);



                } catch (Exception e) {
                    ArrayList<String> arrlst = new ArrayList<String>();
                    arrlst.add("Select Price Level");
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(),R.layout.activity_item__autocomplete,R.id.txt_name, arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    plevel.setAdapter(ard);
                    Toast.makeText(getApplicationContext(), "No Any Price Level Found", Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(AddNewLedger.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNewLedger.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
    private class SaveLedger extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(AddNewLedger.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait While Saveing");
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
                for (int i = 0; i < ledger.size(); i++) {
                    if(""+ledger.elementAt(i)==""||""+ledger.elementAt(i)==null)
                    {
                        //m.add(i,"0");
                    }
                    request += ledger.elementAt(i).toString().trim() ;
                    if(i==ledger.size()-1)
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
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "insertledger.php");
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
                partyname.setText("");
                address_1.setText("");
                address_2.setText("");
                state.setText("");
                contactno.setText("");
                emailid.setText("");
                plevel.setText("");


                Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;

                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "No Any Found"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
}
