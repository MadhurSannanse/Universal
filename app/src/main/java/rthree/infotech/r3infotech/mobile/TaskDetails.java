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
import android.support.v7.app.AppCompatActivity;*/import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class TaskDetails extends AppCompatActivity {
    Button btn_process;
    private static final int INTERNET=1;
    String usertype,username,locationstr;
    EditText txt_task;
    Vector task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txt_task=(EditText)findViewById(R.id.txt_taskdetails);
        btn_process=(Button)findViewById(R.id.btn_taskprocess);
        try
        {
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            locationstr=b.getString("Location");
            task=new Vector();
            btn_process.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try
                    {
                        if(txt_task.getText().toString().trim().equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"Invalid Task",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String taskname = txt_task.getText().toString().trim();
                            SimpleDateFormat Ins_Date = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");
                            String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
                            task = new Vector();
                            task.addElement(""+username);
                            task.addElement("" + taskname);
                            task.addElement("" + InsDate);
                            task.addElement("" + locationstr);
                            if(isNetworkAvailable()) {
                                new SaveTask().execute("Save");
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"No Internet Connection.",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                    catch (Exception ex)
                    {

                    }
                }
            });
        }
        catch (Exception ex)
        {
            ////////Log.e("Error ","Main "+ex.getLocalizedMessage());
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
    private class SaveTask extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(TaskDetails.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait Adding Task !");
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
                String request="";
                for (int i = 0; i < task.size(); i++) {
                    if(""+task.elementAt(i)==""||""+task.elementAt(i)==null)
                    {
                        //m.add(i,"0");
                    }
                    request += task.elementAt(i).toString().trim() ;
                    if(i==task.size()-1)
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
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "inserttaskdetails.php");
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
                    txt_task.setText("");
                    Toast.makeText(getApplicationContext(),"Task Saved Sucessfully !",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    txt_task.setText("");
                    Toast.makeText(getApplicationContext(),"Task Saved Sucessfully !",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "No Any Found"+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(TaskDetails.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TaskDetails.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
