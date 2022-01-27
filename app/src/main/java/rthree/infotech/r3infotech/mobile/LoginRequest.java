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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
/*import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;*/import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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

@SuppressWarnings("ALL")
public class LoginRequest extends AppCompatActivity {

    TextView userName,contactNo,usedemo;
    ImageButton btnRequest;
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    String uname, ucontactno, usertype, imeino, curdate;
    ProgressDialog progressBar;
    ServerDatabase mydb;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private long fileSize = 0;
    int year_x, month_x, day_x;
    Vector details,login;
    String fcmtoken="",imeiid="";
    String  username;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userName=(TextView)findViewById(R.id.txt_username);
        contactNo=(TextView)findViewById(R.id.txt_contact);
        btnRequest=(ImageButton)findViewById(R.id.btn_request);
        usedemo=(TextView)findViewById(R.id.txt_useas);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            mydb=new ServerDatabase(getApplicationContext());
            Vector userlogin=new Vector();
            try {
                userlogin = mydb.getUserLogin();
                Log.i("User",""+userlogin.toString());
            }catch (Exception ex){Log.i("Error",""+ex.getLocalizedMessage());}
            if(userlogin.size()==0)
            {

                Log.i("1","New User");
                //serverRequest(new View(this));
                if(isNetworkAvailable()) {
                    Log.i("2","Network Available");

                    int permissionCheck = ContextCompat.checkSelfPermission(LoginRequest.this, Manifest.permission.READ_PHONE_STATE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                        Log.i("3","Permited 1");

                        new LoadValidUser().execute("User");
                    }
                    {
                        ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                        Log.i("4","Permited 2");

                        new LoadValidUser().execute("User");
                    }

                }
                else
                {
                    Log.i("5","No Internet");

                    Toast.makeText(getApplicationContext(),"No Internet Available !",Toast.LENGTH_SHORT).show();
                }

            }
            else
            {
                String usertype=""+userlogin.elementAt(2).toString().trim();
                if(usertype.equals("Customer"))
                {
                    Toast.makeText(getApplicationContext(),"Logined As Customer Welcome "+userlogin.elementAt(0),Toast.LENGTH_SHORT).show();
                }

                else
                {
                    if(userlogin.elementAt(6).equals("NO"))
                    {
                        Toast.makeText(getApplicationContext(),"Login Confirmation Is Pending ",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Logined Validation Sucess Welcome "+userlogin.elementAt(0),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            usedemo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.i("Clicked","Yes");
                        process();
                    } catch (Exception ex) {Log.e("Error",""+ex.getLocalizedMessage());
                    }
                }
            });
            // mydb.checkDatabase();
            details=new Vector();
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!userName.getText().toString().trim().equals("")) {

                        if(!contactNo.getText().toString().trim().equals("")&&contactNo.getText().toString().length()==10)
                        {
                            //serverSend(view);
                            if(isNetworkAvailable()) {
                                int permissionCheck = ContextCompat.checkSelfPermission(LoginRequest.this, Manifest.permission.READ_PHONE_STATE);
                                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                                    new InsertUserRequest().execute("Request");
                                }
                                else
                                {
                                    ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                                    new InsertUserRequest().execute("Request");
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"No Internet Available !",Toast.LENGTH_SHORT).show();
                            }


                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Invalid Contact Number . .!!",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Username Required . .!!",Toast.LENGTH_SHORT).show();
                    }
                    //  if(mydb.addLoginManagement(details))
                    //   {
                    //       //Log.e("Data","Login Added Sucess");
                    //   }
                    // Intent i=new Intent(getApplicationContext(),Home_Screen.class);
                    // startActivity(i);
                }

            });

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Not Process Contact Administration team !", Toast.LENGTH_SHORT).show();
        }




    }
    public void getLoginData()
    {
        try
        {
            try
            {
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                mydb.deleteLogin();
                String tot = "";
                long kk = 0;
                Vector login;
                /*int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, PERMISSION_REQUEST_CODE, PE);
                }
                else
                {

                }*/


                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("imei",getImeiNumber()));

                //Log.e("Log ","Log 2");
                JSONArray jArray = new JSONArray(new JsonBilder().getLoginmanagement(""+getImeiNumber()));
                //Log.e("Data ",""+jArray.toString());
                JSONObject json_data = null;
                //Log.e("Log ","Log 3");
                for (int i = 0; i < jArray.length(); i++)
                {
                    json_data = jArray.getJSONObject(i);
                    login=new Vector();
                    //  login.addElement(json_data.getString("ID"));
                    login.addElement(json_data.getString("user_name"));
                    login.addElement(json_data.getString("contact_no"));
                    login.addElement(json_data.getString("user_type"));
                    login.addElement(json_data.getString("imei_no"));
                    login.addElement(json_data.getString("date"));
                    login.addElement(json_data.getString("status"));
                    login.addElement(json_data.getString("tally_name"));
                    if(login.elementAt(2).toString().equals("Customer")) {
                        if (mydb.addLoginDetails(login)) {
                            //Log.e("Status", "User Data Saved");
                            Intent intent=new Intent(getApplicationContext(),MainActivity. class);
                            startActivity(intent);

                        } else {
                            //Log.e("Status", "Not Saved");
                        }
                    }
                    else
                    {
                        if(!login.elementAt(5).toString().equals("NO")) {
                            if (mydb.addLoginDetails(login)) {
                                //Log.e("Status", "User Data Saved");
                                Intent intent=new Intent(getApplicationContext(),MainActivity. class);
                                finish();
                                startActivity(intent);
                            } else {
                                //Log.e("Status", "Not Saved");
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Your Login Process Is Pending Please Wait . .!",Toast.LENGTH_LONG).show();
                        }
                    }

                }
                //Log.e("Log ","Log 4");
                //Toast.makeText(getApplicationContext(),"Login Names Updated Sucessfully ", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Unregistred User Or Your Request Is Under Processed ..!" ,
                        Toast.LENGTH_SHORT).show();
            }

        }
        catch(Exception ex)
        {
            Toast.makeText(getApplicationContext(), "Error "+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    private class LoadValidUser extends AsyncTask<String, Void, Void> {

        private static final int REQUEST_READ_PHONE_STATE = 0;
        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(LoginRequest.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait ");
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
                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {
                    mydb.deleteLogin();
                    String tot = "";
                    long kk = 0;
                    Vector login;
                    String imeiid="";
                    Log.i("6","Getting User Data");
                    if (android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }

                    nameValuePairs.add(new BasicNameValuePair("imei",getImeiNumber()));

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://www.r3infoservices.com/Offline/universal/"+"selectloginman.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    //Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    //Log.e("Fail 1", e.toString());
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

                    // //Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    //Log.e("Fail 2", e.toString());

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
                    // ArrayList<String> arrlst = new ArrayList<String>();
                    // arrlst.add("Select Distributor");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);
                        login=new Vector();
                        //  login.addElement(json_data.getString("ID"));
                        login.addElement(jsonObject.getString("user_name"));
                        login.addElement(jsonObject.getString("contact_no"));
                        login.addElement(jsonObject.getString("user_type"));
                        login.addElement(jsonObject.getString("imei_no"));
                        login.addElement(jsonObject.getString("date"));
                        login.addElement(jsonObject.getString("status"));
                        login.addElement(jsonObject.getString("tally_name"));
                        login.addElement(jsonObject.getString("url_address"));
                        Log.e("Details ",""+login);
                        if(login.elementAt(2).toString().equals("Customer")) {
                            if (mydb.addLoginDetails(login)) {
                                //Log.e("Status", "User Data Saved");
                                finish();
                                Intent intent=new Intent(LoginRequest.this,MainActivity.class);
                                startActivity(intent);
                            } else {
                                //Log.e("Status", "Not Saved");
                            }
                        }
                        else
                        {

                            if(!login.elementAt(5).toString().equals("NO")) {
                                if (mydb.addLoginDetails(login)) {
                                    //Log.e("Status", "User Data Saved");
                                    finish();
                                    Intent intent=new Intent(LoginRequest.this,MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    //Log.e("Status", "Not Saved");
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Your Login Process Is Pending Please Wait . .!",Toast.LENGTH_LONG).show();
                            }
                        }

                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Unregistered User", Toast.LENGTH_LONG).show();
                    Log.e("Error In Login",""+e.getMessage());
                    //  Intent intent=new Intent(CreateLogin.this,MainActivity.class);
                    //  startActivity(intent);
                }

            }
        }

    }
    private class InsertUserRequest extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(LoginRequest.this);


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
                String request="";
                Vector req = new Vector();
                req=loadRequestData();

                for (int i = 0; i < req.size(); i++) {
                    if(""+req.elementAt(i)==""||""+req.elementAt(i)==null)
                    {
                        //m.add(i,"0");
                    }
                    request += req.elementAt(i).toString().trim() ;
                    if(i==req.size()-1)
                    {
                        // continue;
                    }
                    else
                    {
                        request=request+"~~~";
                    }

                }
                request=request+"^^^";
                Log.e("O Details",""+request);
                InputStream is = null;

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("details", request));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://www.r3infoservices.com/Offline/universal/"+"insert_loginmanagement.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // //Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    //Log.e("Fail 1", e.toString());
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

                    // //Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    //Log.e("Fail 2", e.toString());

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
                    userName.setText("");
                    contactNo.setText("");
                    Toast.makeText(getApplicationContext(),"Added Sucessfully Please Wait For Confirmation!",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(getApplicationContext(),MainActivity. class);
                    finish();
                    startActivity(i);


                } catch (Exception e) {
                    userName.setText("");
                    contactNo.setText("");
                    Toast.makeText(getApplicationContext(),"Added Sucessfully Please Wait For Confirmation!",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(getApplicationContext(),MainActivity. class);
                    finish();
                    startActivity(i);
                }

            }
        }

    }

    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(LoginRequest.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
    public Vector loadRequestData() {
        try {


            SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy hh:mm a");
            SimpleDateFormat Ins_Date = new SimpleDateFormat("d/MM/yyyy");
            String date = df.format(Calendar.getInstance().getTime());
            String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
            Vector req=new Vector();
            req.addElement(""+userName.getText());
            req.addElement(""+contactNo.getText());
            req.addElement("Admin");
            req.addElement(""+getImeiNumber());
            req.addElement(""+InsDate);
            req.addElement("NO");
            ////Log.e("Req",""+req);
            //details=req;
            return  req;


        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Error while getting data "+ex.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            return  null;
        }
    }
    public  void process()
    {
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Login Process . .");
            builder.setMessage("Are you sure to use application as Demo Version..?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    login = new Vector();
                    mydb = new ServerDatabase(getApplicationContext());
                    //  login.addElement(json_data.getString("ID"));
                    login.addElement("Demo User");
                    login.addElement("1111111111");
                    login.addElement("Admin");
                    login.addElement("1");
                    login.addElement("1/1/2020");
                    login.addElement("YES");
                    login.addElement("Demo User");
                    login.addElement("demoall_2");
                    if (mydb.addLoginDetails(login)) {
                        //Log.e("Status", "User Data Saved");
                        finish();
                        Intent intent = new Intent(LoginRequest.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        //Log.e("Status", "Not Saved");
                    }
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
                    /*builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });*/
            builder.show();

        }
        catch (Exception ex){Log.e("Process Error",""+ex.getLocalizedMessage());}
    }
    public String getImeiNumber() {

        String deviceid = "";
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(LoginRequest.this, Manifest.permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                Log.i("7", "Permitted 1");
            }
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return ""+Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
            if (android.os.Build.VERSION.RELEASE.startsWith("10")) {
                deviceid = Settings.Secure.getString(LoginRequest.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i("1 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            } else {
                deviceid = mngr.getDeviceId().toString().trim();
                Log.i("2 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            }
            if (android.os.Build.VERSION.SDK_INT > 28) {
                deviceid = Settings.Secure.getString(LoginRequest.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i("3 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            } else {
                deviceid = mngr.getDeviceId().toString().trim();
                Log.i("4 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            }

            return deviceid;


        } catch (Exception ex) {
            Log.e("Error IMEI", "" + ex.getLocalizedMessage());
            return deviceid;
        }
    }
}
