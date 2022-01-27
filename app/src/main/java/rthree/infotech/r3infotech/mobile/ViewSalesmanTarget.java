package rthree.infotech.r3infotech.mobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

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

public class ViewSalesmanTarget extends AppCompatActivity {
    private  static  String Tag="ViewSalesmanTarget";
    float[] ydata={1,2,3};
    String[] xdata={"Target","Achieved","Remaining"};
    PieChart pitarget;
    private static final int STORAGE_CODE = 1000;
    ProgressDialog progressBar;
    ServerDatabase mydb;
    String usertype,username,salesman;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private static final int REQUEST_READ_PHONE_STATE =1 ;
    private static final int INTERNET=1;
    Button showdetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_salesman_target);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pitarget=(PieChart)findViewById(R.id.ch_target);
        showdetails=(Button)findViewById(R.id.btn_details);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        usertype = b.getString("UserType");
        username = b.getString("UserName");
        salesman=b.getString("Salesman");
        // username="Sumit Pople";
        pitarget.setHoleRadius(25f);
        pitarget.setRotationEnabled(true);
        pitarget.setCenterText("Target\n&\nAchievement");
        pitarget.setCenterTextColor(Color.BLACK);
        pitarget.setCenterTextSize(10);
        pitarget.setDrawEntryLabels(true);
        pitarget.setTransparentCircleAlpha(0);
        new LoadTarget().execute("");
        showdetails.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), TargetDetaiils.class);
            i.putExtra("UserType", "" + usertype);
            i.putExtra("UserName", "" + username);
            i.putExtra("Salesman", "" + salesman);
            startActivity(i);
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
    private void fillPieChart()
    {
        try
        {
            ArrayList<PieEntry> yentry=new ArrayList<>();
            ArrayList<String> xentry=new ArrayList<>();
            for(int i=0;i<ydata.length;i++)
            {
                try
                {
                    if(ydata[i]<0)
                    {
                        ydata[i]=Math.abs(ydata[i]);
                    }
                    yentry.add(new PieEntry(ydata[i],xdata[i]));
                }
                catch (Exception ex ){

                    yentry.add(new PieEntry(1,xdata[i]));}

            }
            for (int i=0;i<xdata.length;i++)
            {
                xentry.add(xdata[i]);
            }
            PieDataSet piedataset=new PieDataSet(yentry,"  "+salesman);
            piedataset.setSliceSpace(2);
            piedataset.setValueTextSize(15);
            ArrayList<Integer> colors=new ArrayList<>();
            colors.add(Color.BLUE);
            colors.add(Color.GREEN);
            colors.add(Color.RED);
            piedataset.setColors(colors);

            Legend legend=pitarget.getLegend();
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
            PieData pidata=new PieData(piedataset);
            pitarget.setData(pidata);
            pitarget.invalidate();
        }
        catch (Exception ex){}
    }
    private class LoadTarget extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(ViewSalesmanTarget.this);


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
                //                Log.e("Bill Number 2 "," "+billnumber);
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("username", salesman));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost;
                    httppost = new HttpPost(new JsonBilder().getHostName() + "selectusertarget.php");
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
                Log.i("Result 1 :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    JSONObject jsonObject=null;
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);
                        //  login.addElement(json_data.getString("ID"));
                        try {
                            ydata[0]=Float.parseFloat(jsonObject.getString("Target"));
                            ydata[1]=Float.parseFloat(jsonObject.getString("Achieved"));
                            ydata[2]=ydata[0]-ydata[1];
                        }
                        catch (Exception ex)
                        {
                            ydata[0]=0;
                            ydata[1]=0;
                            ydata[2]=0;
                        }

                    }

                    fillPieChart();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "No Any Order Found"+e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(ViewSalesmanTarget.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewSalesmanTarget.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
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
