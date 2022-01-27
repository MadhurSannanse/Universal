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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

public class TeamLeaderOutstanding extends AppCompatActivity {
    String usertype,username,billnumber;
    TextView txtamount;
    AutoCompleteTextView atvparty;
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    ListView lstouts;
    double totalouts=0;
    ListView lstorderitems;
    Vector outs;
    String party[],totalbills[],total[],oid[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_leader_outstanding);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        atvparty=(AutoCompleteTextView) findViewById(R.id.atv_teamlouts);
        txtamount=(TextView)findViewById(R.id.txt_total);
        lstouts=(ListView)findViewById(R.id.lst_teamlouts);
        try
        {
            lstouts.requestFocus();
            outs=new Vector();
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            usertype = b.getString("UserType");
            username = b.getString("UserName");
            billnumber=b.getString("BillNumber");
            if(isNetworkAvailable()) {
                new LoadOutstandingCount().execute("Load");
            }
            else
            {
                Toast.makeText(TeamLeaderOutstanding.this, "No Internte", Toast.LENGTH_SHORT).show();
            }
            atvparty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (isNetworkAvailable()) {
                        String partyname="";
                        partyname=""+atvparty.getText();
                        atvparty.setText("");
                        Intent i = new Intent(getApplicationContext(), PartyOutstanding.class);
                        i.putExtra("UserType", "" + usertype);
                        i.putExtra("UserName", "" + username);
                        i.putExtra("Salesman", "" + partyname);
                        startActivity(i);


                    } else {
                        Toast.makeText(TeamLeaderOutstanding.this, "No Internte", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            atvparty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isNetworkAvailable()) {
                        String partyname="";
                        partyname=""+atvparty.getText();
                        atvparty.setText("");
                        Intent i = new Intent(getApplicationContext(), PartyOutstanding.class);
                        i.putExtra("UserType", "" + usertype);
                        i.putExtra("UserName", "" + username);
                        i.putExtra("Salesman", "" + partyname);
                        startActivity(i);


                    } else {
                        Toast.makeText(TeamLeaderOutstanding.this, "No Internte", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            lstouts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isNetworkAvailable()) {
                        String partyname="";
                        Vector o=new Vector();
                        o=(Vector)outs.elementAt(position);
                        partyname=""+o.elementAt(0);
                        Intent i = new Intent(getApplicationContext(), PartyOutstanding.class);
                        i.putExtra("UserType", "" + usertype);
                        i.putExtra("UserName", "" + username);
                        i.putExtra("Salesman", "" + partyname);
                        startActivity(i);

                    } else {
                        Toast.makeText(TeamLeaderOutstanding.this, "No Internte", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Error "+ex.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
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

    private void loadOutsDetails()
    {
        try
        {
            Log.e("Outs ",""+outs.size());
            party=new String[outs.size()];
            totalbills=new String[outs.size()];
            total=new String[outs.size()];
            oid=new String[outs.size()];
            double tot=0,onacc=0,totbil=0;
            totalouts=0;
            for(int i=0;i<outs.size();i++)
            {
                tot=0;
                onacc=0;
                totbil=0;
                Vector data=(Vector)outs.elementAt(i);
                party[i]=""+data.elementAt(0);
                totalbills[i]="Total Vouchers : "+data.elementAt(1);
                try {
                    if(onacc==0) {
                        onacc = Double.parseDouble("" + data.elementAt(5)) ;// / Integer.parseInt("" + data.elementAt(1));
                    }
                }
                catch (Exception ex){onacc=0;}
                try {
                    tot = Double.parseDouble("" + data.elementAt(2));
                }catch (Exception ex){tot=0;}
                totbil=Double.parseDouble(""+data.elementAt(1));
                tot=tot+onacc;
                //total[i]=""+getResources().getString(R.string.Rs)+" "+roundTwo(tot);
                total[i]="";
                oid[i]=""+(i+1);
                totalouts=totalouts+tot;
            }
            txtamount.setText("");
            //txtamount.setText(""+getResources().getString(R.string.Rs)+" "+roundTwo(totalouts));
            AdapterOutstanding adapter=new AdapterOutstanding(getApplicationContext(),party,totalbills,total,oid);
            lstouts.setAdapter(adapter);
            lstouts.requestFocus();
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
    private class LoadOutstandingCount extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(TeamLeaderOutstanding.this);


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
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(new JsonBilder().getHostName() + "selectteamleaderouts.php");
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
                        o.addElement(jsonObject.getString("Salesman"));
                        o.addElement(jsonObject.getString("c"));
                        o.addElement(jsonObject.getString("a"));
                        o.addElement(jsonObject.getString("p"));
                        o.addElement(jsonObject.getString("o"));
                        o.addElement(jsonObject.getString("q"));
                        arrlst.add(""+jsonObject.getString("Salesman"));
                        outs.addElement(o);
                        Log.e("",""+o.toString());

                    }
                    ArrayAdapter ard = new ArrayAdapter(getApplicationContext(),R.layout.activity_item__autocomplete,R.id.txt_name, arrlst);
                    ard.setDropDownViewResource(android.R.layout.select_dialog_item);
                    atvparty.setAdapter(ard);
                    atvparty.clearFocus();
                    loadOutsDetails();

                } catch (Exception e) {
                    Log.e("Error outs",""+ e.getLocalizedMessage());
                    Toast.makeText(getApplicationContext(), "No Any Outstanding Available", Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(TeamLeaderOutstanding.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TeamLeaderOutstanding.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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

